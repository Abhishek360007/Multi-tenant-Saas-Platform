package com.abhishek.tenanthub.perms.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

/**
 * <p>
 * Reset password
 * </p>
 *
 * @author Wang Chen Chen
 * @version 1.0
 * @date 2021/7/5 12:14
 */


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Schema(description = "Reset password")
public class ResetPasswordVO {

    /**
     * User ID
     */
    @Schema(description = "User ID is required", requiredMode = REQUIRED)
    @NotNull(message = "User ID is required!")
    private Long userId;

    /**
     * New password
     */
    @Schema(description = "New password must be longer than 5 characters", requiredMode = REQUIRED)
    @NotBlank(message = "New password is required!")
    @Length(min = 5, max = 32, message = "New password, 5 to 32 characters")
    private String newPwd;

}
