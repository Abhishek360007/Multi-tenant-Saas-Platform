package com.abhishek.tenanthub.auth.otp;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

/**
 * Sends OTPs by SMS through MSG91's Flow API (https://control.msg91.com/api/v5/flow).
 * <p>
 * Setup: create a DLT-approved SMS template in MSG91 containing a variable for the code
 * (default name "otp", e.g. "##otp## is your TenantHub sign-in code"), then set
 * MSG91_AUTH_KEY and MSG91_TEMPLATE_ID. Indian SMS requires DLT registration of the sender and template.
 *
 * @author Abhishek Raj
 */
@Component
public class Msg91SmsOtpSender implements OtpSender {

    private final String authKey;
    private final String templateId;
    private final String otpVariable;
    private final RestClient client;

    public Msg91SmsOtpSender(@Value("${tenanthub.otp.msg91.auth-key:}") String authKey,
                             @Value("${tenanthub.otp.msg91.template-id:}") String templateId,
                             @Value("${tenanthub.otp.msg91.otp-variable:otp}") String otpVariable,
                             @Value("${tenanthub.otp.msg91.base-url:https://control.msg91.com}") String baseUrl) {
        this.authKey = authKey;
        this.templateId = templateId;
        this.otpVariable = otpVariable;
        this.client = RestClient.builder().baseUrl(baseUrl).build();
    }

    @Override
    public Channel channel() {
        return Channel.SMS;
    }

    @Override
    public boolean isConfigured() {
        return StringUtils.isNoneBlank(authKey, templateId);
    }

    @Override
    public void send(String target, String code, long ttlMinutes) {
        var body = Map.of(
                "template_id", templateId,
                "short_url", "0",
                // MSG91 expects the country code without "+"
                "recipients", List.of(Map.of("mobiles", "91" + target, otpVariable, code))
        );
        client.post()
                .uri("/api/v5/flow")
                .header("authkey", authKey)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .toBodilessEntity();
    }
}
