package com.abhishek.tenanthub.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.abhishek.tenanthub.system.entity.SysTenant;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Set;


public interface SysTenantMapper extends BaseMapper<SysTenant> {

    // Query all table names in the database
    @Select("SELECT DISTINCT TABLE_NAME FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE ())")
    Set<String> selectListTableNames();


    // Clear the data of the given tables
    int truncateTableData(@Param("dbName") String dbName, @Param("tableNames") Set<String> tableNames);


    // Delete all permission templates of the tenant
    @Delete("delete from sys_tenant_template WHERE tenant_id = #{tenantId}")
    int deleteHaveTemplates(String tenantId);


    // Add template permissions to the tenant
    int insertByTemplates(@Param("tenantId") String tenantId,
                          @Param("templates") Set<Long> templates);


}