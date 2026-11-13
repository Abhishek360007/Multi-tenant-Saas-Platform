package com.abhishek.tenanthub.internal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;


/**
 * <p>
 * Project table
 * </p>
 *
 * @author Wang Chen Chen
 * @version 1.0
 * @date 2021/7/5 9:31
 */


@Schema(description = "Project")
@Getter
@Setter
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class CmsProjectDTO implements java.io.Serializable {

    /**
     * Project ID
     */
    @Schema(description = "Project ID")
    private Long projectId;

    /**
     * Project name
     */
    @Schema(description = "Project name")
    private String projectName;

    /**
     * Contact name
     */
    @Schema(description = "Contact name")
    private String linkman;

    /**
     * Contact number
     */
    @Schema(description = "Contact number")
    private String contactNumber;

    /**
     * Address area
     */
    @Schema(description = "Address area")
    private Long areaCode;

    /**
     * Address, e.g. 12 MG Road, Block A
     */
    @Schema(description = "Address")
    private String address;

    @Schema(description = "6-digit PIN code")
    private String pinCode;

    /**
     * Sort order
     */
    @Schema(description = "Sort order")
    private Long sort;

    /**
     * Project password, required for dangerous operations
     */
    @Schema(description = "Project password, required for dangerous operations")
    private String password;

    /**
     * Status [0 = disabled, 1 = normal, 2 = locked]
     */
    @Schema(description = "Status [0 = disabled, 1 = normal, 2 = locked]")
    private Byte status;

    /**
     * Department
     */
    @Schema(description = "Department Id")
    private Long deptId;

}
