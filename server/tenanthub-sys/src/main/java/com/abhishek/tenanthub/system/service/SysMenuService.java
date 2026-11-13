package com.abhishek.tenanthub.system.service;

import cn.hutool.core.lang.tree.Tree;
import com.abhishek.tenanthub.system.entity.SysMenu;
import com.abhishek.tenanthub.tenant.base.service.BaseService;

import java.util.List;

/**
 * <p>
 * </p>
 *
 * @author WangChenChen
 * @version 1.1
 * @date 2022/12/9 15:33
 */

public interface SysMenuService extends BaseService<SysMenu> {

    /**
     * Get all menus as a tree
     */
    List<Tree<Long>> tree();


    /**
     * Get all menus available to the current user
     */
    List<Tree<Long>> treeCurrentUserAvailable();


    /**
     * Get all menus available to the tenant
     */
    List<Tree<Long>> treeTenantAvailable();


}
