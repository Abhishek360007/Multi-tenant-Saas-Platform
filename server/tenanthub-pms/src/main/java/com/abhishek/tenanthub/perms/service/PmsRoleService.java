package com.abhishek.tenanthub.perms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.abhishek.tenanthub.common.po.SearchPO;
import com.abhishek.tenanthub.perms.entity.PmsRole;
import com.abhishek.tenanthub.perms.vo.UpdateMenusVO;
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

public interface PmsRoleService extends BaseService<PmsRole> {


    IPage<PmsRole> pageKeywords(SearchPO params);


    /**
     * Assigned departments
     *
     * @throws
     * @author Wang Chen Chen<932560435@qq.com>
     * @create 2024/8/13 14:49
     */
    UpdateMenusVO listHaveDepts(Long roleId);


    /**
     * Assigned menus
     *
     * @throws
     * @author Wang Chen Chen<932560435@qq.com>
     * @create 2021/7/24 14:49
     */
    UpdateMenusVO listHaveMenus(Long roleId);

    /**
     * Update role menus
     *
     * @author Wang Chen Chen
     * @date 2021/7/23 15:19
     */
    boolean updateMenus(Long roleId, Set<Long> menus);


    /**
     * Query the user roles
     *
     * @author WangChenChen
     * @date 2022/3/22 18:14
     */
    Map<Long, Set<PmsRole>> listByUserIds(Set<Long> userIds);


}
