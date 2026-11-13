package com.abhishek.tenanthub.auth.po;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

/**
 * <p>
 * User login parameters
 * </p>
 *
 * @author Wang Chen Chen <932560435@qq.com>
 * @version 2.0
 * @date 2019/4/18 11:45
 */


@Schema(description = "Username/password login parameters")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class LoginFormPO {

    @Schema(description = "Username", requiredMode = REQUIRED)
    @NotNull(message = "Username is required")
    @Length(min = 5, message = "Username must be at least 5 characters")
    private String username;

    @Schema(description = "Password", requiredMode = REQUIRED)
    @NotNull(message = "Password is required")
    @Length(min = 5, message = "Password must be at least 5 characters")
    private String password;

    @Schema(description = "Captcha", requiredMode = REQUIRED)
    @NotNull(message = "Captcha is required")
    @Length(min = 4, max = 4, message = "Captcha must be 4 characters")
    private String codeText;

    @Schema(description = "Captcha KEY", requiredMode = REQUIRED)
    @NotNull(message = "Captcha key is required")
    @Length(min = 5, message = "Captcha key must be at least 16 characters")
    private String codeKey;

}
