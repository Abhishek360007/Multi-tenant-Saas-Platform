package com.abhishek.tenanthub.system.controller;

import com.abhishek.tenanthub.common.util.JsonResult;
import com.abhishek.tenanthub.system.entity.SysPayment;
import com.abhishek.tenanthub.system.service.impl.BillingServiceImpl;
import com.abhishek.tenanthub.system.service.impl.PaymentServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * Subscription and GST quotes for the tenant making the request.
 *
 * @author Abhishek Raj
 */
@Tag(name = "[ Billing ] Subscription")
@RestController
@AllArgsConstructor
@RequestMapping("/billing")
public class BillingController {

    private final BillingServiceImpl billingService;

    private final PaymentServiceImpl paymentService;

    @Operation(summary = "Subscription summary", description = "Current plan, usage and subscription end date")
    @GetMapping("/summary")
    public JsonResult<BillingServiceImpl.Summary> summary() {
        return JsonResult.success(billingService.summary());
    }

    @Operation(summary = "Price quote", description = "Plan price with GST (CGST+SGST within the seller's state, otherwise IGST); amounts in paise")
    @GetMapping("/quote")
    public JsonResult<BillingServiceImpl.Quote> quote(@RequestParam String plan,
                                                      @RequestParam(defaultValue = "MONTHLY") BillingServiceImpl.Cycle cycle) {
        return JsonResult.success(billingService.quote(plan, cycle));
    }

    @Operation(summary = "Start checkout", description = "Creates a Razorpay order for the GST-inclusive price; returns Razorpay Checkout options")
    @PostMapping("/checkout")
    public JsonResult<PaymentServiceImpl.Checkout> checkout(@RequestBody CheckoutPO po) {
        return JsonResult.success(paymentService.checkout(po.plan(), po.cycle() == null ? BillingServiceImpl.Cycle.MONTHLY : po.cycle()));
    }

    @Operation(summary = "Verify payment", description = "Razorpay Checkout success callback; activates the plan")
    @PostMapping("/verify")
    public JsonResult<SysPayment> verify(@RequestBody VerifyPO po) {
        return JsonResult.success(paymentService.verify(po.razorpay_order_id(), po.razorpay_payment_id(), po.razorpay_signature()));
    }

    @Operation(summary = "Payment history", description = "Payments of the current tenant, newest first")
    @GetMapping("/payments")
    public JsonResult<List<SysPayment>> payments() {
        return JsonResult.success(paymentService.history());
    }

    public record CheckoutPO(String plan, BillingServiceImpl.Cycle cycle) {
    }

    /**
     * Field names as Razorpay Checkout returns them.
     */
    public record VerifyPO(String razorpay_order_id, String razorpay_payment_id, String razorpay_signature) {
    }
}
