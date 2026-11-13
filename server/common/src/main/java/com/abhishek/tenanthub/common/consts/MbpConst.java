package com.abhishek.tenanthub.common.consts;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>
 * mybatis-plus Constants
 * </p>
 *
 * @author Wang Chen Chen
 * @version 1.0.1
 * @date 2021/7/16 15:15
 */

public class MbpConst {


    /**
     * Data field.
     */
    public static final String PROJECT_ID = "project_id";

    public final static String CREATE_TIME = "create_time";

    public final static String CREATE_USER = "create_user";

    public final static String LAST_UPDATE_TIME = "last_update_time";

    public final static String LAST_UPDATE_USER = "last_update_user";


    /**
     * Property field.
     */
    public final static String ATTR_PROJECT_ID = "projectId";

    public final static String ATTR_CREATE_TIME = "createTime";

    public final static String ATTR_CREATE_USER = "createUser";

    public final static String ATTR_LAST_UPDATE_TIME = "lastUpdateTime";

    public final static String ATTR_LAST_UPDATE_USER = "lastUpdateUser";

    /**
     * Base property fields
     */
    public final static Set<String> ATTR_BASE_FIELDS = Set.of(
            ATTR_CREATE_TIME,
            ATTR_CREATE_USER,
            ATTR_LAST_UPDATE_TIME,
            ATTR_LAST_UPDATE_USER
    );

    /**
     * Tables excluded from tenant interception
     * Usage:
     *
     * @see com.abhishek.tenanthub.tenant.schema.SchemaInterceptor.ignoreTable()
     */
    public final static Set<String> TENANT_IGNORE_TABLES = new HashSet<>(
            Set.of(
                    "sys_tenant",                   // Tenant
                    "sys_tenant_template",          // Tenant templates
                    "sys_template",                 // Template
                    "sys_template_menu",            // Template menus
                    "sys_menu",                     // Menu
                    "sys_config",                   // Global settings
                    "sys_dict_data",                // Dictionary data
                    "sys_dict_type",                // Dictionary types
                    "sys_file_detail",              // Common configuration file
                    "india_area",                   // India administrative areas
                    "sys_holiday"                   // Indian public holidays
            )
    );


    /**
     * Tables excluded from project_id interception
     *
     * <p>
     * Tables without a project_id column are read from the database automatically at startup
     * <p>
     * COLUMNS must be added, otherwise table names are read automatically at startup
     * <p>
     *
     * @see com.abhishek.tenanthub.corems.runner.ProjectTableRunner
     * <p>
     */
    public final static Set<String> PROJECT_IGNORE_TABLES = new HashSet<>(
            Stream.of(TENANT_IGNORE_TABLES, Set.of("COLUMNS"))
                    .flatMap(Collection::stream)
                    .collect(Collectors.toSet())
    );


}
