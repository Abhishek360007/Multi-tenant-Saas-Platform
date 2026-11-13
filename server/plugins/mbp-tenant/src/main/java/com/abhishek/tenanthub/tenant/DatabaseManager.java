package com.abhishek.tenanthub.tenant;

import com.abhishek.tenanthub.tenant.props.MultiTenantProperties;

import javax.sql.DataSource;
import java.util.Set;
import java.util.function.Consumer;

public interface DatabaseManager {

    /**
     * TODO Get the default data source
     *
     * @author WangChenChen
     * @date 2022/12/11 11:04
     */

    DataSource getDefaultDataSource();


    /**
     * TODO Get the data source for a tenant ID
     *
     * @author WangChenChen
     * @date 2022/12/11 11:04
     */

    DataSource getDataSource(String tenantId);


    /**
     * TODO Create tenant tables
     *
     * @author WangChenChen
     * @date 2022/12/11 11:04
     */
    void asyncUpdateTable(String tenantId, Consumer<Exception> consumer);


    /**
     * TODO Create tenant tables
     *
     * @author WangChenChen
     * @date 2022/12/11 11:04
     */
    void updateTable(String tenantId) throws Exception;


    /**
     * TODO Create tenant tables
     *
     * @author WangChenChen
     * @date 2022/12/11 11:04
     */
    void updateTable(Set<String> tenantIds) throws Exception;


    /**
     * TODO Drop tenant tables
     *
     * @author WangChenChen
     * @date 2022/12/11 11:04
     */
    void deleteTable(String tenantId);


    /**
     * TODO Get multi-tenant details
     *
     * @author WangChenChen
     * @date 2022/12/11 11:04
     */
    MultiTenantProperties getMultiTenantProperties();


}
