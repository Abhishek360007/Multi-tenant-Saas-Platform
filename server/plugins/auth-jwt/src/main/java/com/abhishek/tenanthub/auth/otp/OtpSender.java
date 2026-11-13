package com.abhishek.tenanthub.auth.otp;

/**
 * Delivers a one-time password over one channel.
 *
 * @author Abhishek Raj
 */
public interface OtpSender {

    enum Channel {
        EMAIL, SMS
    }

    Channel channel();

    /**
     * Whether the provider credentials are present.
     */
    boolean isConfigured();

    /**
     * @param target email address, or a 10-digit Indian mobile number
     * @param code   the OTP
     * @param ttlMinutes how long the code is valid, for the message text
     */
    void send(String target, String code, long ttlMinutes);
}
