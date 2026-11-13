package com.abhishek.tenanthub.system.service.impl;

import com.abhishek.tenanthub.common.exception.BizException;
import com.abhishek.tenanthub.common.util.GstCalculator;
import com.abhishek.tenanthub.common.util.IndianTaxIds;
import com.abhishek.tenanthub.internal.api.ApiBillingService;
import com.abhishek.tenanthub.internal.api.ApiCmsProjectService;
import com.abhishek.tenanthub.internal.api.ApiPmsUserService;
import com.abhishek.tenanthub.system.entity.SysPlan;
import com.abhishek.tenanthub.system.entity.SysTenant;
import com.abhishek.tenanthub.system.mapper.SysPlanMapper;
import com.abhishek.tenanthub.system.service.SysTenantService;
import com.abhishek.tenanthub.tenant.service.MultiTenantManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.abhishek.tenanthub.common.util.TenantUtils.getTenantId;

/**
 * Subscription plans, plan limits and GST quotes for the current tenant.
 *
 * @author Abhishek Raj
 */
@Service
public class BillingServiceImpl implements ApiBillingService {

    public enum Cycle {MONTHLY, YEARLY}

    private final SysPlanMapper planMapper;
    private final ObjectProvider<SysTenantService> tenantService;
    private final ObjectProvider<ApiPmsUserService> userService;
    private final ObjectProvider<ApiCmsProjectService> projectService;
    private final MultiTenantManager tenantManager;
    private final int gstRate;
    private final int sellerStateCode;

    public BillingServiceImpl(SysPlanMapper planMapper,
                              ObjectProvider<SysTenantService> tenantService,
                              ObjectProvider<ApiPmsUserService> userService,
                              ObjectProvider<ApiCmsProjectService> projectService,
                              MultiTenantManager tenantManager,
                              @Value("${tenanthub.billing.gst-rate:18}") int gstRate,
                              @Value("${tenanthub.billing.seller-state-code:29}") int sellerStateCode) {
        this.planMapper = planMapper;
        this.tenantService = tenantService;
        this.userService = userService;
        this.projectService = projectService;
        this.tenantManager = tenantManager;
        this.gstRate = gstRate;
        this.sellerStateCode = sellerStateCode;
    }

    @Override
    public void checkCanAddUser() {
        var plan = currentPlanOrNull();
        if (plan == null || plan.getMaxUsers() < 0) {
            return;
        }
        if (userService.getObject().countUsers() >= plan.getMaxUsers()) {
            throw new BizException(String.format("Your %s plan allows %s. Upgrade your plan to add more.",
                    plan.getName(), count(plan.getMaxUsers(), "user")));
        }
    }

    @Override
    public void checkCanAddProject() {
        var plan = currentPlanOrNull();
        if (plan == null || plan.getMaxProjects() < 0) {
            return;
        }
        if (projectService.getObject().countProjects() >= plan.getMaxProjects()) {
            throw new BizException(String.format("Your %s plan allows %s. Upgrade your plan to add more.",
                    plan.getName(), count(plan.getMaxProjects(), "project")));
        }
    }

    /**
     * Current plan, usage and subscription end date of the tenant making the request.
     */
    public Summary summary() {
        var tenant = currentTenant();
        var plan = planOf(tenant);
        return new Summary(tenant.getTenantId(), tenant.getName(), plan,
                userService.getObject().countUsers(), projectService.getObject().countProjects(),
                tenant.getExpired(), tenant.getPlanExpires(), buyerStateCode(tenant), sellerStateCode);
    }

    /**
     * Price of a plan for the current tenant, with the GST split for its state.
     */
    public Quote quote(String planCode, Cycle cycle) {
        var plan = planMapper.selectById(planCode);
        if (plan == null || plan.getStatus() == null || plan.getStatus() != 1) {
            throw new BizException("Plan " + planCode + " is not available");
        }
        var tenant = currentTenant();
        long base = cycle == Cycle.YEARLY ? plan.getYearlyPricePaise() : plan.getMonthlyPricePaise();
        var gst = GstCalculator.quote(base, gstRate, sellerStateCode, buyerStateCode(tenant));
        return new Quote(plan.getPlanCode(), plan.getName(), cycle, gst);
    }

    /**
     * Place of supply: the state in the tenant's GSTIN, otherwise the state of its address.
     */
    int buyerStateCode(SysTenant tenant) {
        if (StringUtils.isNotBlank(tenant.getGstin()) && IndianTaxIds.isValidGstin(tenant.getGstin())) {
            return Integer.parseInt(tenant.getGstin().substring(0, 2));
        }
        return tenant.getAreaCode() == null ? sellerStateCode : IndianTaxIds.gstStateCodeOfArea(tenant.getAreaCode());
    }

    private SysTenant currentTenant() {
        var tenant = tenantService.getObject().getById(getTenantId());
        if (tenant == null) {
            throw new BizException("Tenant not found");
        }
        return tenant;
    }

    /**
     * The plan in effect: a paid plan whose period has ended counts as Free.
     */
    SysPlan planOf(SysTenant tenant) {
        var code = StringUtils.defaultIfBlank(tenant.getPlanCode(), "free");
        if (tenant.getPlanExpires() != null && tenant.getPlanExpires().isBefore(LocalDateTime.now())) {
            code = "free";
        }
        var plan = planMapper.selectById(code);
        if (plan == null) {
            throw new BizException("Subscription plan not found");
        }
        return plan;
    }

    /**
     * The platform's own tenant has no limits.
     */
    private SysPlan currentPlanOrNull() {
        var tenantId = getTenantId();
        if (StringUtils.isBlank(tenantId) || StringUtils.equals(tenantId, tenantManager.getDefaultTenantId())) {
            return null;
        }
        return planOf(currentTenant());
    }

    private static String count(int n, String noun) {
        return n + " " + noun + (n == 1 ? "" : "s");
    }

    public record Summary(String tenantId, String tenantName, SysPlan plan, long users, long projects,
                          LocalDateTime expires, LocalDateTime planExpires, int buyerStateCode, int sellerStateCode) {
    }

    public record Quote(String planCode, String planName, Cycle cycle, GstCalculator.Quote amounts) {
    }
}
