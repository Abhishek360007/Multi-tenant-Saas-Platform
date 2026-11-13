package com.abhishek.saas.perms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.abhishek.saas.common.po.SearchPO;
import com.abhishek.saas.perms.entity.PmsRole;
import com.abhishek.saas.perms.vo.UpdateMenusVO;
import com.abhishek.saas.tenant.base.service.BaseService;

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
     * 已经拥有的部门
     *
     * @throws
     * @author Wang Chen Chen<932560435@qq.com>
     * @create 2024/8/13 14:49
     */
    UpdateMenusVO listHaveDepts(Long roleId);


    /**
     * 已经拥有的菜单
     *
     * @throws
     * @author Wang Chen Chen<932560435@qq.com>
     * @create 2021/7/24 14:49
     */
    UpdateMenusVO listHaveMenus(Long roleId);

    /**
     * 修改 角色 菜单
     *
     * @author Wang Chen Chen
     * @date 2021/7/23 15:19
     */
    boolean updateMenus(Long roleId, Set<Long> menus);


    /**
     * 查询用户的角色
     *
     * @author WangChenChen
     * @date 2022/3/22 18:14
     */
    Map<Long, Set<PmsRole>> listByUserIds(Set<Long> userIds);


}
