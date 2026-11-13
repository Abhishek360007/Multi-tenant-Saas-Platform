package com.abhishek.tenanthub.system.controller;

import com.abhishek.tenanthub.common.util.JsonResult;
import com.abhishek.tenanthub.system.service.impl.BillingServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
