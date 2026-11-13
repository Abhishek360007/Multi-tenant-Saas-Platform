package com.abhishek.tenanthub.system.razorpay;

import com.abhishek.tenanthub.common.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Map;

/**
 * Minimal Razorpay REST client: create orders and verify signatures.
 * Keys come from RAZORPAY_KEY_ID / RAZORPAY_KEY_SECRET (test keys start with rzp_test_);
 * webhooks are verified with RAZORPAY_WEBHOOK_SECRET.
 *
 * @author Abhishek Raj
 */
@Slf4j
@Component
public class RazorpayClient {

    private final String keyId;
    private final String keySecret;
    private final String webhookSecret;
    private final RestClient client;

    public RazorpayClient(@Value("${tenanthub.razorpay.key-id:}") String keyId,
                          @Value("${tenanthub.razorpay.key-secret:}") String keySecret,
                          @Value("${tenanthub.razorpay.webhook-secret:}") String webhookSecret,
                          @Value("${tenanthub.razorpay.base-url:https://api.razorpay.com}") String baseUrl) {
        this.keyId = keyId;
        this.keySecret = keySecret;
        this.webhookSecret = webhookSecret;
        this.client = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeaders(h -> {
                    if (StringUtils.isNoneBlank(keyId, keySecret)) {
                        h.setBasicAuth(keyId, keySecret);
                    }
                })
                .build();
    }

    public boolean isConfigured() {
        return StringUtils.isNoneBlank(keyId, keySecret);
    }

    public boolean isWebhookConfigured() {
        return StringUtils.isNotBlank(webhookSecret);
    }

    /**
     * Public key ID, sent to the browser for Razorpay Checkout.
     */
    public String keyId() {
        return keyId;
    }

    /**
     * Creates an order and returns its ID (order_...).
     */
    @SuppressWarnings("unchecked")
    public String createOrder(long amountPaise, String receipt, Map<String, String> notes) {
        if (!isConfigured()) {
            throw new BizException("Online payment is not set up on this server");
        }
        try {
            Map<String, Object> order = client.post()
                    .uri("/v1/orders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("amount", amountPaise, "currency", "INR", "receipt", receipt, "notes", notes))
                    .retrieve()
                    .body(Map.class);
            if (order == null || order.get("id") == null) {
                throw new BizException("Razorpay did not return an order");
            }
            return order.get("id").toString();
        } catch (RestClientResponseException e) {
            log.error("Razorpay order failed: {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new BizException("Could not start the payment. Please try again");
        }
    }

    /**
     * Checkout success callback: signature = HMAC_SHA256(order_id + "|" + payment_id, key_secret).
     */
    public boolean isValidPaymentSignature(String orderId, String paymentId, String signature) {
        return isConfigured() && matches(hmacHex(orderId + "|" + paymentId, keySecret), signature);
    }

    /**
     * Webhook: X-Razorpay-Signature = HMAC_SHA256(raw request body, webhook_secret).
     */
    public boolean isValidWebhookSignature(String rawBody, String signature) {
        return isWebhookConfigured() && matches(hmacHex(rawBody, webhookSecret), signature);
    }

    static String hmacHex(String data, String secret) {
        try {
            var mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return HexFormat.of().formatHex(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static boolean matches(String expected, String given) {
        return given != null && MessageDigest.isEqual(
                expected.getBytes(StandardCharsets.UTF_8), given.trim().toLowerCase().getBytes(StandardCharsets.UTF_8));
    }
}
