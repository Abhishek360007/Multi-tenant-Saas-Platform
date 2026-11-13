package com.abhishek.tenanthub.internal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * Operator
 * </p>
 *
 * @author WangChenChen
 * @version 1.1
 * @date 2023/10/31 13:39
 */


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class OperateUserDTO implements java.io.Serializable {

    /**
     * Unique user id
     */
    @Schema(description = "User Id")
    private Long userId;

    /**
     * Avatar
     */
    @Schema(description = "Avatar")
    private String avatar;

    /**
     * Nickname
     */
    @Schema(description = "Nickname")
    private String nickname;

    @Override
    public String toString() {
        return String.format("User Id : %s\n Avatar : %s\n Nickname : %s", this.userId, this.avatar, this.nickname);
    }

}
