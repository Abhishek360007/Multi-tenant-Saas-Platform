package com.abhishek.tenanthub.auth.po;

import com.abhishek.tenanthub.auth.otp.OtpSender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

/**
 * Request a sign-in OTP.
 *
 * @author Abhishek Raj
 */
@Getter
@Setter
public class OtpSendPO {

    @Schema(description = "EMAIL or SMS", requiredMode = REQUIRED)
    @NotNull(message = "Choose email or SMS")
    private OtpSender.Channel channel;

    @Schema(description = "Email address, or 10-digit Indian mobile number", requiredMode = REQUIRED)
    @NotBlank(message = "Enter your email or mobile number")
    private String target;

    @Schema(description = "Captcha", requiredMode = REQUIRED)
    @NotBlank(message = "Captcha is required")
    @Length(min = 4, max = 4, message = "Captcha must be 4 characters")
    private String codeText;

    @Schema(description = "Captcha KEY", requiredMode = REQUIRED)
    @NotBlank(message = "Captcha key is required")
    @Length(min = 16, message = "Captcha key must be at least 16 characters")
    private String codeKey;
}
