package com.abhishek.tenanthub.common.consts;

import org.apache.commons.collections4.KeyValue;
import org.apache.commons.collections4.keyvalue.DefaultKeyValue;

/**
 * <p>
 * Global configuration file key
 * DefConfigValueConst
 * </p>
 *
 * @author Wang Chen Chen
 * @version 1.0.1
 * @date 2021/7/16 15:15
 */

public class ConfigDataConst {

    /**
     * System settings
     */
    public static final String REDIS_CACHE_KEY = "sys_config_cache";


    /**
     * Default user password
     */
    public static final KeyValue<String, String> DEFAULT_USER_PASSWORD = new DefaultKeyValue<>("default_user_password", "1234546");


    /**
     * Default project password
     */
    public static final KeyValue<String, String> DEFAULT_PROJECT_PASSWORD = new DefaultKeyValue<>("default_project_password", "1234546");


    /**
     * Default project of each tenant ID
     */
    public static final KeyValue<String, Long> DEFAULT_PROJECT_ID = new DefaultKeyValue<>("default_project_id", 10001L);


    /**
     * Tenant default logo
     */
    public static final KeyValue<String, String> DEFAULT_TENANT_LOGO = new DefaultKeyValue<>("default_tenant_logo", "https://raw.githubusercontent.com/Abhishek360007/Multi-tenant-Saas-Platform/main/docs/brand/tenanthub-logo.png");


    /**
     * Tenant default role name
     */
    public static final KeyValue<String, String> DEFAULT_ROLE_NAME = new DefaultKeyValue<>("default_role_name", "Operator");


    /**
     * Default role of each tenant ID
     */
    public static final KeyValue<String, Long> DEFAULT_ROLE_ID = new DefaultKeyValue<>("default_role_id", 10001L);


    /**
     * Default department of each tenant ID
     */
    public static final KeyValue<String, Long> DEFAULT_DEPT_ID = new DefaultKeyValue<>("default_dept_id", 10001L);


}
