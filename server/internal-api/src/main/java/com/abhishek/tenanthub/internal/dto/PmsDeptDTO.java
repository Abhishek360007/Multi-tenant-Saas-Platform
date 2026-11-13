package com.abhishek.tenanthub.internal.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.abhishek.tenanthub.common.consts.RegexConst;
import com.abhishek.tenanthub.common.valid.ValidCreate;
import com.abhishek.tenanthub.common.valid.ValidUpdate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author WangChenChen
 * @version 1.2
 * @date 2022/6/24 16:33
 */


@Getter
@Setter
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class PmsDeptDTO implements java.io.Serializable {

    /**
     * Department ID
     */
    @Schema(description = "Department ID")
    @NotNull(message = "Department ID is required", groups = {ValidUpdate.class})
    private Long deptId;

    /**
     * Parent department
     */
    @Schema(description = "Parent department ID")
    @NotNull(message = "Parent department ID is required", groups = {ValidCreate.class})
    private Long parentId;

    /**
     * Department name
     */
    @Schema(description = "Department name")
    @NotBlank(message = "Department name is required", groups = {ValidCreate.class})
    private String deptName;

    /**
     * Department leader name
     */
    @Schema(description = "Leader name")
    @NotBlank(message = "Leader name is required", groups = {ValidCreate.class})
    private String leader;

    /**
     * Department leader mobile
     */
    @Schema(description = "Leader mobile number")
    @NotBlank(message = "Leader mobile number is required", groups = {ValidCreate.class})
    @Pattern(regexp = RegexConst.MOBILE, message = "Leader mobile number is invalid", groups = {ValidCreate.class, ValidUpdate.class})
    private String leaderMobile;

    /**
     * Sort order
     */
    @Schema(description = "Sort order")
    @NotNull(message = "Department sort order is required", groups = {ValidCreate.class})
    private Long sort;

    /**
     * Department description
     */
    @Schema(description = "Department description")
    @NotBlank(message = "Department description is required", groups = {ValidCreate.class})
    private String description;

    /**
     * Ancestor list
     */
    @Schema(description = "Ancestor list")
    @JsonIgnore
    private String ancestors;

}
