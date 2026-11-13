package com.abhishek.tenanthub.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.abhishek.tenanthub.system.entity.SysTemplate;
import com.abhishek.tenanthub.system.entity.SysTemplateProxy;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Set;


public interface SysTemplateMapper extends BaseMapper<SysTemplate> {

    // Add menu permissions to the template
    int insertByMenus(@Param("id") Long id, @Param("menus") Set<Long> menus);


    // Whether any tenant still uses this template
    @Select("select count(*) from sys_tenant_template where template_id = #{templateId}")
    int userReference(Long templateId);


    // Delete all menus of the template
    @Delete("delete from sys_template_menu where template_id = #{templateId}")
    int deleteHaveMenus(Long templateId);


    @Select("select menu_id from sys_template_menu where template_id = #{templateId}")
    Set<Long> selectByTemplateId(Long templateId);


    // Get the templates linked to a tenant ID
    List<SysTemplateProxy> selectListByTenantIds(@Param("tenantIds") Set<String> tenantIds);

}