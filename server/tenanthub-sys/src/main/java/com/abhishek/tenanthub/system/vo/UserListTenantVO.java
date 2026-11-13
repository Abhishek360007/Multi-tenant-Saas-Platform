package com.abhishek.tenanthub.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.Set;

/**
 * <p>
 * </p>
 *
 * @author Wang Chen Chen<932560435@qq.com>
 * @version 1.0.1
 * @date 2021/7/24 14:39
 */

@Getter
@Setter
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Grant a platform user access to several tenants")
public class UserListTenantVO implements java.io.Serializable {

    /**
     * Assigned tenants ID
     */
    @Schema(description = "Assigned tenant IDs")
    private Set<String> have;

    /**
     * All tenants
     */
    @Schema(description = "All tenants")
    private Set<Entry> all;


    @Getter
    @Setter
    @ToString
    @Builder
    @Accessors(chain = true)
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Entry implements java.io.Serializable {

        private String value;

        private String label;

    }


}


