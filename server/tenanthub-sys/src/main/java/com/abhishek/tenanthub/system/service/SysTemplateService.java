package com.abhishek.tenanthub.system.service;

import com.abhishek.tenanthub.system.entity.SysTemplate;
import com.abhishek.tenanthub.system.vo.UpdateMenusVO;
import com.abhishek.tenanthub.tenant.base.service.BaseService;

import java.util.Map;
import java.util.Set;


/**
 * <p>
 * </p>
 *
 * @author WangChenChen
 * @version 1.1
 * @date 2022/12/9 15:33
 */


public interface SysTemplateService extends BaseService<SysTemplate> {

    /**
     * Get all menu permissions of a permission template
     */
    UpdateMenusVO listHaveMenus(Long templateId);


    /**
     * Update the menus of a permission template
     */
    boolean updateMenus(Long templateId, Set<Long> menus);


    /**
     * Get the templates linked to a tenant ID
     */
    Map<String, Set<SysTemplate>> listByTenantIds(Set<String> tenantIds);


}
