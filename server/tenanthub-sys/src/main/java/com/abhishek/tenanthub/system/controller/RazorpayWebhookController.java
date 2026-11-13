package com.abhishek.tenanthub.system.controller;

import com.abhishek.tenanthub.system.razorpay.RazorpayClient;
import com.abhishek.tenanthub.system.service.impl.PaymentServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Razorpay webhook. Configure in the Razorpay dashboard: URL https://your-domain/api/razorpay/webhook,
 * events payment.captured and order.paid, and the same secret as RAZORPAY_WEBHOOK_SECRET.
 * Needs no login; every request must carry a valid X-Razorpay-Signature.
 *
 * @author Abhishek Raj
 */
@Slf4j
@Tag(name = "[ Billing ] Razorpay webhook")
@RestController
@AllArgsConstructor
@RequestMapping("/razorpay")
public class RazorpayWebhookController {

    private final RazorpayClient razorpay;
    private final PaymentServiceImpl paymentService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Operation(summary = "Razorpay webhook", description = "payment.captured / order.paid activate the plan")
    @PostMapping("/webhook")
    public ResponseEntity<String> webhook(@RequestBody String rawBody,
                                          @RequestHeader(value = "X-Razorpay-Signature", required = false) String signature) {
        if (!razorpay.isValidWebhookSignature(rawBody, signature)) {
            log.warn("Rejected Razorpay webhook with an invalid signature");
            return ResponseEntity.status(400).body("invalid signature");
        }
        try {
            JsonNode root = objectMapper.readTree(rawBody);
            var event = root.path("event").asText();
            var payment = root.path("payload").path("payment").path("entity");
            if ("payment.captured".equals(event) || "order.paid".equals(event)) {
                var orderId = payment.path("order_id").asText(root.path("payload").path("order").path("entity").path("id").asText());
                var paymentId = payment.path("id").asText();
                if (!orderId.isEmpty() && !paymentId.isEmpty()) {
                    paymentService.onWebhookPaid(orderId, paymentId);
                }
            }
            return ResponseEntity.ok("ok");
        } catch (Exception e) {
            log.error("Could not process Razorpay webhook: {}", e.getMessage());
            // 500 makes Razorpay retry later
            return ResponseEntity.status(500).body("error");
        }
    }
}
