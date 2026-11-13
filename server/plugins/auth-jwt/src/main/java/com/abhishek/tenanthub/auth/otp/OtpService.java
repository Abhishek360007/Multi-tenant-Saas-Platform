package com.abhishek.tenanthub.auth.otp;

import com.abhishek.tenanthub.auth.exception.JwtAuthException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * One-time passwords for sign-in. Codes are stored hashed in Redis (keys are tenant-prefixed),
 * expire after {@code ttl}, allow {@code max-attempts} guesses, and are rate limited per target.
 *
 * @author Abhishek Raj
 */
@Slf4j
@Service
public class OtpService {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String PREFIX = "otp:";

    private final StringRedisTemplate redis;
    private final Map<OtpSender.Channel, OtpSender> senders;
    private final int length;
    private final Duration ttl;
    private final Duration resendInterval;
    private final int maxAttempts;
    private final int maxSendsPerHour;
    private final boolean logCodes;

    public OtpService(StringRedisTemplate redis,
                      List<OtpSender> senders,
                      @Value("${tenanthub.otp.length:6}") int length,
                      @Value("${tenanthub.otp.ttl:5m}") Duration ttl,
                      @Value("${tenanthub.otp.resend-interval:60s}") Duration resendInterval,
                      @Value("${tenanthub.otp.max-attempts:5}") int maxAttempts,
                      @Value("${tenanthub.otp.max-sends-per-hour:5}") int maxSendsPerHour,
                      @Value("${tenanthub.otp.log-codes:false}") boolean logCodes) {
        this.redis = redis;
        this.senders = senders.stream().collect(Collectors.toMap(OtpSender::channel, Function.identity()));
        this.length = length;
        this.ttl = ttl;
        this.resendInterval = resendInterval;
        this.maxAttempts = maxAttempts;
        this.maxSendsPerHour = maxSendsPerHour;
        this.logCodes = logCodes;
    }

    /**
     * Lowercased email, or a 10-digit Indian mobile number (accepts +91, 91 or 0 prefixes and spaces/dashes).
     */
    public static String normalizeTarget(OtpSender.Channel channel, String target) throws JwtAuthException {
        if (StringUtils.isBlank(target)) {
            throw new JwtAuthException(channel == OtpSender.Channel.EMAIL ? "Enter your email" : "Enter your mobile number");
        }
        if (channel == OtpSender.Channel.EMAIL) {
            var email = target.trim().toLowerCase(Locale.ROOT);
            if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
                throw new JwtAuthException("Enter a valid email address");
            }
            return email;
        }
        var digits = target.replaceAll("[^0-9]", "");
        if (digits.length() == 12 && digits.startsWith("91")) {
            digits = digits.substring(2);
        } else if (digits.length() == 11 && digits.startsWith("0")) {
            digits = digits.substring(1);
        }
        if (!digits.matches("^[6-9][0-9]{9}$")) {
            throw new JwtAuthException("Enter a valid 10-digit Indian mobile number");
        }
        return digits;
    }

    /**
     * Whether codes can be delivered on this channel (or are logged for development).
     */
    public boolean isAvailable(OtpSender.Channel channel) {
        var sender = senders.get(channel);
        return logCodes || (sender != null && sender.isConfigured());
    }

    /**
     * Generates, stores and delivers a code. The target must already be normalized.
     */
    public void send(OtpSender.Channel channel, String target) throws JwtAuthException {
        var sender = senders.get(channel);
        boolean configured = sender != null && sender.isConfigured();
        if (!configured && !logCodes) {
            throw new JwtAuthException(channel == OtpSender.Channel.EMAIL
                    ? "Email sign-in codes are not set up on this server"
                    : "SMS sign-in codes are not set up on this server");
        }
        var base = PREFIX + channel.name().toLowerCase(Locale.ROOT) + ":" + target;
        if (Boolean.FALSE.equals(redis.opsForValue().setIfAbsent(base + ":resend", "1", resendInterval))) {
            throw new JwtAuthException(String.format("Please wait %d seconds before requesting another code", resendInterval.toSeconds()));
        }
        Long sentThisHour = redis.opsForValue().increment(base + ":hourly");
        if (sentThisHour != null && sentThisHour == 1) {
            redis.expire(base + ":hourly", Duration.ofHours(1));
        }
        if (sentThisHour != null && sentThisHour > maxSendsPerHour) {
            throw new JwtAuthException("Too many codes requested. Try again in an hour");
        }

        var code = randomCode();
        redis.opsForValue().set(base + ":code", sha256(code), ttl);
        redis.delete(base + ":attempts");

        if (configured) {
            try {
                sender.send(target, code, ttl.toMinutes());
            } catch (RuntimeException e) {
                log.error("Failed to send {} OTP: {}", channel, e.getMessage());
                redis.delete(List.of(base + ":code", base + ":resend"));
                throw new JwtAuthException("Could not send the code. Please try again");
            }
        } else {
            log.warn("[DEV] {} sign-in code for {} is {} (tenanthub.otp.log-codes=true; never enable in production)",
                    channel, target, code);
        }
    }

    /**
     * Checks a code. Throws if it is wrong, expired or out of attempts; consumes it on success.
     */
    public void verify(OtpSender.Channel channel, String target, String code) throws JwtAuthException {
        var base = PREFIX + channel.name().toLowerCase(Locale.ROOT) + ":" + target;
        var stored = redis.opsForValue().get(base + ":code");
        if (stored == null) {
            throw new JwtAuthException("The code has expired. Request a new one");
        }
        Long attempts = redis.opsForValue().increment(base + ":attempts");
        if (attempts != null && attempts == 1) {
            redis.expire(base + ":attempts", ttl);
        }
        if (attempts != null && attempts > maxAttempts) {
            redis.delete(List.of(base + ":code", base + ":attempts"));
            throw new JwtAuthException("Too many wrong attempts. Request a new code");
        }
        var given = StringUtils.trimToEmpty(code);
        if (!MessageDigest.isEqual(stored.getBytes(StandardCharsets.UTF_8), sha256(given).getBytes(StandardCharsets.UTF_8))) {
            throw new JwtAuthException("Incorrect code");
        }
        redis.delete(List.of(base + ":code", base + ":attempts"));
    }

    private String randomCode() {
        var sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(RANDOM.nextInt(10));
        }
        return sb.toString();
    }

    private static String sha256(String value) {
        try {
            var digest = MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
