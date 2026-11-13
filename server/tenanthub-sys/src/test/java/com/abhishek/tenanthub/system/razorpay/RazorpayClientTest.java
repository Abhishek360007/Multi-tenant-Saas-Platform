package com.abhishek.tenanthub.system.razorpay;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Expected HMACs were computed independently with:
 * printf '%s' 'order_9A33XWu170gUtm|pay_29QQoUBi66xm2f' | openssl dgst -sha256 -hmac test_secret_123
 */
class RazorpayClientTest {

    private final RazorpayClient client = new RazorpayClient("rzp_test_key", "test_secret_123", "whsec_test", "http://localhost");

    @Test
    void acceptsValidPaymentSignature() {
        assertTrue(client.isValidPaymentSignature("order_9A33XWu170gUtm", "pay_29QQoUBi66xm2f",
                "eab16fd05960997216354fdf0a8961abd9f098420059cfbe1b37104e60ddcbce"));
    }

    @Test
    void rejectsTamperedPayment() {
        // Same signature, different payment ID
        assertFalse(client.isValidPaymentSignature("order_9A33XWu170gUtm", "pay_SOMEONEELSE",
                "eab16fd05960997216354fdf0a8961abd9f098420059cfbe1b37104e60ddcbce"));
        assertFalse(client.isValidPaymentSignature("order_9A33XWu170gUtm", "pay_29QQoUBi66xm2f", null));
    }

    @Test
    void verifiesWebhookBody() {
        var body = "{\"event\":\"payment.captured\"}";
        assertTrue(client.isValidWebhookSignature(body, "4f463a57dd128675850163391f0311888616d57bccca75c774c9cdb28134f851"));
        assertFalse(client.isValidWebhookSignature(body + " ", "4f463a57dd128675850163391f0311888616d57bccca75c774c9cdb28134f851"));
    }

    @Test
    void notConfiguredRejectsEverything() {
        var empty = new RazorpayClient("", "", "", "http://localhost");
        assertFalse(empty.isConfigured());
        assertFalse(empty.isValidWebhookSignature("{}", "anything"));
    }
}
