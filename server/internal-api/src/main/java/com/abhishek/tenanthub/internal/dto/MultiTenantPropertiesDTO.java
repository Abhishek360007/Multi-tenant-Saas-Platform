package com.abhishek.tenanthub.internal.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * Multi-tenant global settings
 * </p>
 *
 * @author WangChenChen
 * @version 1.1
 * @date 2022/12/9 11:53
 */

@Getter
@Setter
public class MultiTenantPropertiesDTO {

    /**
     * Enable multi-tenant mode
     */
    private Boolean enable;

    /**
     * Enable multi-tenant mode
     */
    private Boolean enableProject;

    /**
     * Database name prefix
     */
    private String prefix;

    /**
     * Default tenant ID
     */
    private String defaultTenantId;

    /**
     * Default project ID
     */
    private Long defaultProjectId;

    /**
     * Default database name
     */
    private String dbName;

}
