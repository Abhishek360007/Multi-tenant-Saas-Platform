package com.abhishek.tenanthub.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.abhishek.tenanthub.system.entity.SysMenu;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Set;


public interface SysMenuMapper extends BaseMapper<SysMenu> {


    // Query all tenant permissions
    List<SysMenu> selectByTenantId(String tenantId);


    // Query all tenant permissions
    Set<String> selectPermsByTenantId(String tenantId);


    // Check whether a permission still has child permissions
    @Select("SELECT COUNT(*) FROM sys_menu WHERE parent_id = #{id}")
    int haveChildren(Long id);


    // Check whether a permission is still used by a tenant permission template
    @Select("SELECT COUNT(*) FROM sys_template_menu WHERE menu_id = #{id}")
    int templateReference(Long id);


}