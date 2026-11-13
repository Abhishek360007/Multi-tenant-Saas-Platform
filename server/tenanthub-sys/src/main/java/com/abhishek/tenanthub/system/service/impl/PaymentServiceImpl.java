package com.abhishek.tenanthub.system.service.impl;

import com.abhishek.tenanthub.common.exception.BizException;
import com.abhishek.tenanthub.system.entity.SysPayment;
import com.abhishek.tenanthub.system.entity.SysTenant;
import com.abhishek.tenanthub.system.mapper.SysPaymentMapper;
import com.abhishek.tenanthub.system.mapper.SysTenantMapper;
import com.abhishek.tenanthub.system.razorpay.RazorpayClient;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static com.abhishek.tenanthub.common.util.TenantUtils.getTenantId;

/**
 * Razorpay checkout for subscription plans.
 * <p>
 * Flow: {@link #checkout} creates a Razorpay order for the GST-inclusive total; the browser pays with
 * Razorpay Checkout; {@link #verify} (browser callback) and {@link #onWebhookPaid} (server-to-server)
 * both activate the plan, and {@link #markPaid} makes sure that happens exactly once.
 *
 * @author Abhishek Raj
 */
@Slf4j
@Service
public class PaymentServiceImpl {

    private final BillingServiceImpl billingService;
    private final RazorpayClient razorpay;
    private final SysPaymentMapper paymentMapper;
    private final SysTenantMapper tenantMapper;
    private final String invoicePrefix;
    private final String businessName;

    public PaymentServiceImpl(BillingServiceImpl billingService, RazorpayClient razorpay,
                              SysPaymentMapper paymentMapper, SysTenantMapper tenantMapper,
                              @Value("${tenanthub.billing.invoice-prefix:TH}") String invoicePrefix,
                              @Value("${tenanthub.app-name:TenantHub}") String businessName) {
        this.billingService = billingService;
        this.razorpay = razorpay;
        this.paymentMapper = paymentMapper;
        this.tenantMapper = tenantMapper;
        this.invoicePrefix = invoicePrefix;
        this.businessName = businessName;
    }

    /**
     * Creates a Razorpay order for the current tenant and returns what Razorpay Checkout needs.
     */
    public Checkout checkout(String planCode, BillingServiceImpl.Cycle cycle) {
        if (!razorpay.isConfigured()) {
            throw new BizException("Online payment is not set up on this server");
        }
        var tenant = currentTenant();
        var quote = billingService.quote(planCode, cycle);
        var amounts = quote.amounts();
        if (amounts.totalPaise() <= 0) {
            throw new BizException("This plan is free; nothing to pay");
        }
        var payment = new SysPayment()
                .setTenantId(tenant.getTenantId())
                .setPlanCode(quote.planCode())
                .setBillingCycle(cycle.name())
                .setBasePaise(amounts.basePaise())
                .setCgstPaise(amounts.cgstPaise())
                .setSgstPaise(amounts.sgstPaise())
                .setIgstPaise(amounts.igstPaise())
                .setTotalPaise(amounts.totalPaise())
                .setBuyerGstin(StringUtils.defaultString(tenant.getGstin()))
                .setPlaceOfSupply(billingService.buyerStateCode(tenant))
                .setStatus(SysPayment.CREATED)
                .setRazorpayPaymentId("")
                .setInvoiceNo("")
                .setCreatedAt(LocalDateTime.now());
        var orderId = razorpay.createOrder(amounts.totalPaise(),
                "th-" + tenant.getTenantId() + "-" + System.currentTimeMillis(),
                Map.of("tenant_id", tenant.getTenantId(), "plan", quote.planCode(), "cycle", cycle.name()));
        payment.setRazorpayOrderId(orderId);
        paymentMapper.insert(payment);

        var description = String.format("%s plan (%s)", quote.planName(), cycle == BillingServiceImpl.Cycle.YEARLY ? "yearly" : "monthly");
        return new Checkout(razorpay.keyId(), orderId, amounts.totalPaise(), "INR", businessName, description,
                tenant.getName(), tenant.getEmail(), tenant.getContactNumber());
    }

    /**
     * Browser callback after a successful Razorpay Checkout.
     */
    public SysPayment verify(String orderId, String paymentId, String signature) {
        if (!razorpay.isValidPaymentSignature(orderId, paymentId, signature)) {
            throw new BizException("Payment could not be verified");
        }
        var payment = findByOrder(orderId);
        if (payment == null || !StringUtils.equals(payment.getTenantId(), getTenantId())) {
            throw new BizException("Payment not found");
        }
        return markPaid(payment.getRazorpayOrderId(), paymentId);
    }

    /**
     * Razorpay webhook (payment.captured / order.paid), already signature-checked by the caller.
     */
    public void onWebhookPaid(String orderId, String paymentId) {
        if (findByOrder(orderId) == null) {
            log.warn("Razorpay webhook for unknown order {}", orderId);
            return;
        }
        markPaid(orderId, paymentId);
    }

    /**
     * Completed payments of the current tenant, newest first. Abandoned checkouts stay in the
     * table (a late webhook can still complete them) but are not shown.
     */
    public List<SysPayment> history() {
        return paymentMapper.selectList(new LambdaQueryWrapper<SysPayment>()
                .eq(SysPayment::getTenantId, getTenantId())
                .ne(SysPayment::getStatus, SysPayment.CREATED)
                .orderByDesc(SysPayment::getCreatedAt));
    }

    /**
     * Marks the order paid and activates the plan exactly once (the browser callback and the webhook
     * can both arrive; only the first one that flips the status does the work).
     */
    @Transactional(rollbackFor = Exception.class)
    public synchronized SysPayment markPaid(String orderId, String paymentId) {
        var payment = findByOrder(orderId);
        if (SysPayment.PAID.equals(payment.getStatus())) {
            return payment;
        }
        var tenant = tenantMapper.selectById(payment.getTenantId());
        var now = LocalDateTime.now();
        // Renewing the same plan before it ends extends it; otherwise the new period starts now
        var start = StringUtils.equals(tenant.getPlanCode(), payment.getPlanCode())
                && tenant.getPlanExpires() != null && tenant.getPlanExpires().isAfter(now)
                ? tenant.getPlanExpires() : now;
        var end = "YEARLY".equals(payment.getBillingCycle()) ? start.plusYears(1) : start.plusMonths(1);

        int updated = paymentMapper.update(null, new LambdaUpdateWrapper<SysPayment>()
                .eq(SysPayment::getId, payment.getId())
                .eq(SysPayment::getStatus, SysPayment.CREATED)
                .set(SysPayment::getStatus, SysPayment.PAID)
                .set(SysPayment::getRazorpayPaymentId, paymentId)
                .set(SysPayment::getPaidAt, now)
                .set(SysPayment::getPeriodStart, start)
                .set(SysPayment::getPeriodEnd, end)
                .set(SysPayment::getInvoiceNo, nextInvoiceNo(now)));
        if (updated == 1) {
            tenantMapper.update(null, new LambdaUpdateWrapper<SysTenant>()
                    .eq(SysTenant::getTenantId, tenant.getTenantId())
                    .set(SysTenant::getPlanCode, payment.getPlanCode())
                    .set(SysTenant::getPlanExpires, end));
            log.info("Tenant {} paid for {} ({}), plan active until {}",
                    tenant.getTenantId(), payment.getPlanCode(), payment.getBillingCycle(), end);
        }
        return findByOrder(orderId);
    }

    /**
     * Sequential per Indian financial year (April-March): PREFIX/2026-27/000001.
     */
    private String nextInvoiceNo(LocalDateTime now) {
        int fyStart = now.getMonthValue() >= 4 ? now.getYear() : now.getYear() - 1;
        var fy = String.format("%d-%02d", fyStart, (fyStart + 1) % 100);
        var prefix = invoicePrefix + "/" + fy + "/";
        long issued = paymentMapper.selectCount(new LambdaQueryWrapper<SysPayment>()
                .likeRight(SysPayment::getInvoiceNo, prefix));
        return prefix + String.format("%06d", issued + 1);
    }

    private SysPayment findByOrder(String orderId) {
        return paymentMapper.selectOne(new LambdaQueryWrapper<SysPayment>().eq(SysPayment::getRazorpayOrderId, orderId));
    }

    private SysTenant currentTenant() {
        var tenant = tenantMapper.selectById(getTenantId());
        if (tenant == null) {
            throw new BizException("Tenant not found");
        }
        return tenant;
    }

    /**
     * Options for Razorpay Checkout (amount in paise).
     */
    public record Checkout(String keyId, String orderId, long amount, String currency, String name,
                           String description, String prefillName, String prefillEmail, String prefillContact) {
    }
}
