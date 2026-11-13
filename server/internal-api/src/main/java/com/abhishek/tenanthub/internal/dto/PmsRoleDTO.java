package com.abhishek.tenanthub.internal.dto;

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
public class PmsRoleDTO implements java.io.Serializable {

    /**
     * Role id
     */
    private Long roleId;

    /**
     * Role name
     */
    private String roleName;

    /**
     * Role description
     */
    private String description;

    /**
     * Data scope
     */
    private int dataScope;

}
