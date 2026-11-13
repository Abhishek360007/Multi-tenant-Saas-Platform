package com.abhishek.tenanthub.corems.vo;

import io.swagger.v3.oas.annotations.media.Schema;
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
     * Project ID
     */
    @Schema(description = "Project ID is required", requiredMode = REQUIRED)
    @NotNull(message = "Project ID is required!")
    private Long projectId;

    /**
     * New password
     */
    @Schema(description = "New password must be longer than 5 characters", requiredMode = REQUIRED)
    @NotNull(message = "New password is required!")
    @Length(min = 5, message = "New password must be longer than 5 characters!")
    private String newPwd;

}
