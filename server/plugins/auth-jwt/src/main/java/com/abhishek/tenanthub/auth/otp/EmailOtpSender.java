package com.abhishek.tenanthub.auth.otp;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * Sends OTPs by email over SMTP (spring.mail.* / MAIL_* environment variables).
 *
 * @author Abhishek Raj
 */
@Component
public class EmailOtpSender implements OtpSender {

    private final ObjectProvider<JavaMailSender> mailSender;
    private final String host;
    private final String from;
    private final String appName;

    public EmailOtpSender(ObjectProvider<JavaMailSender> mailSender,
                          @Value("${spring.mail.host:}") String host,
                          @Value("${tenanthub.otp.mail-from:}") String from,
                          @Value("${tenanthub.app-name:TenantHub}") String appName) {
        this.mailSender = mailSender;
        this.host = host;
        this.from = from;
        this.appName = appName;
    }

    @Override
    public Channel channel() {
        return Channel.EMAIL;
    }

    @Override
    public boolean isConfigured() {
        return StringUtils.isNotBlank(host) && mailSender.getIfAvailable() != null;
    }

    @Override
    public void send(String target, String code, long ttlMinutes) {
        var message = new SimpleMailMessage();
        if (StringUtils.isNotBlank(from)) {
            message.setFrom(from);
        }
        message.setTo(target);
        message.setSubject(String.format("%s sign-in code: %s", appName, code));
        message.setText(String.format("""
                Your %s sign-in code is %s

                It expires in %d minutes. Do not share this code with anyone.
                If you did not try to sign in, you can ignore this email.
                """, appName, code, ttlMinutes));
        mailSender.getObject().send(message);
    }
}
