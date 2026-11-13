package com.abhishek.tenanthub.internal.dto;

import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p>
 * Platform user
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
public class PmsUserDTO implements java.io.Serializable {

    /**
     * Unique user id
     */
    private Long userId;

    /**
     * Avatar
     */
    private String avatar;

    /**
     * Username
     */
    private String username;

    /**
     * Mobile number
     */
    private String mobile;

    /**
     * Email
     */
    private String email;

    /**
     * Nickname
     */
    private String nickname;

    /**
     * Password
     */
    private String password;

    /**
     * Gender [0 = female, 1 = male, 2 = unknown]
     */
    private Byte gender;

    /**
     * Is admin: 0 = regular user, 1 = admin
     */
    private Byte adminFlag;

    /**
     * Status [0 = disabled, 1 = normal]
     */
    private Byte status;

    /**
     * Department
     */
    private Long deptId;

    /**
     * 1 = the user must change their password at next login
     */
    private Byte passwordChangeRequired;

    /**
     * Expiry time (empty = never expires)
     */
    private LocalDateTime expired;

}
