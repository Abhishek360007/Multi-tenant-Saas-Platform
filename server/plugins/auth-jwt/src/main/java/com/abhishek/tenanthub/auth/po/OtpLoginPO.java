package com.abhishek.tenanthub.auth.po;

import com.abhishek.tenanthub.auth.otp.OtpSender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

/**
 * Sign in with an OTP.
 *
 * @author Abhishek Raj
 */
@Getter
@Setter
public class OtpLoginPO {

    @Schema(description = "EMAIL or SMS", requiredMode = REQUIRED)
    @NotNull(message = "Choose email or SMS")
    private OtpSender.Channel channel;

    @Schema(description = "Email address, or 10-digit Indian mobile number", requiredMode = REQUIRED)
    @NotBlank(message = "Enter your email or mobile number")
    private String target;

    @Schema(description = "The code that was sent", requiredMode = REQUIRED)
    @NotBlank(message = "Enter the code")
    @Pattern(regexp = "^[0-9]{4,8}$", message = "The code is digits only")
    private String otp;
}
