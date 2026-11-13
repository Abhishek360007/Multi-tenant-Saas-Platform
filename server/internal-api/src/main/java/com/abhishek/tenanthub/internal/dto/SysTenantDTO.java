package com.abhishek.tenanthub.internal.dto;

import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;


/**
 * <p>
 * Tenant
 * </p>
 *
 * @author Wang Chen Chen
 * @version 1.0
 * @date 2021/7/5 9:31
 */


@Getter
@Setter
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class SysTenantDTO implements java.io.Serializable {

    /**
     * Tenant ID
     */
    private String tenantId;

    /**
     * Tenant logo
     */
    private String logo;

    /**
     * Tenant name
     */
    private String name;

    /**
     * Tenant email
     */
    private String email;

    /**
     * Contact name
     */
    private String linkman;

    /**
     * Contact number
     */
    private String contactNumber;

    /**
     * Address area
     */
    private Long areaCode;

    /**
     * Address
     */
    private String address;

    /**
     * 6-digit PIN code
     */
    private String pinCode;

    /**
     * GSTIN (optional)
     */
    private String gstin;

    /**
     * PAN (optional)
     */
    private String pan;

    /**
     * Subscription plan code
     */
    private String planCode;

    /**
     * Status [0 = disabled, 1 = normal]
     */
    private Byte status;

    /**
     * Expiry time (default 100 years)
     */
    private LocalDateTime expired;


}
