package com.abhishek.tenanthub.tenant.props;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

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
@Component
@ConfigurationProperties(prefix = "multi.tenant")
public class MultiTenantProperties {

    /**
     * Enable multi-tenant mode
     */
    private Boolean enable = true;

    /**
     * Enable project mode
     */
    private Boolean enableProject = true;

    /**
     * Database name prefix
     */
    private String prefix = "tenanthub_";

    /**
     * Default tenant ID
     */
    private String defaultTenantId = "master";

    /**
     * Default project ID
     */
    private Long defaultProjectId = 10001L;

    /**
     * Default database name
     */
    private String dbName = prefix + defaultTenantId;

    /**
     * Create tables
     */
    private Boolean createTable = true;

    /**
     * Liquibase contexts
     */
    private String contexts = "tenant";

    /**
     * Liquibase changelog used to create tables
     */
    private String changeLog = "classpath:db/changelog-master.xml";

}
