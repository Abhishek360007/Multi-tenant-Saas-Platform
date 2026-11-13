package com.abhishek.tenanthub.perms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.abhishek.tenanthub.perms.entity.PmsUser;
import com.abhishek.tenanthub.perms.po.UserQueryPO;
import com.abhishek.tenanthub.perms.vo.ResetPasswordVO;
import com.abhishek.tenanthub.perms.vo.UpdatePasswordVO;
import com.abhishek.tenanthub.perms.vo.UserRightsVO;
import com.abhishek.tenanthub.tenant.base.service.BaseService;

import java.util.Set;


/**
 * <p>
 * </p>
 *
 * @author WangChenChen
 * @version 1.1
 * @date 2022/12/9 15:33
 */

public interface PmsUserService extends BaseService<PmsUser> {


    IPage<PmsUser> pageKeywords(UserQueryPO params);


    /**
     * 修改密码
     *
     * @param pwd
     * @return int
     * @date 2022/12/9 15:33
     */
    boolean updatePassword(UpdatePasswordVO pwd);


    /**
     * 重置密码
     *
     * @param pwd
     * @return int
     * @date 2022/12/9 15:33
     */
    boolean resetPassword(ResetPasswordVO pwd);


    /**
     * 修改用户的角色
     *
     * @param userId
     * @param roleIds
     * @return int
     * @date 2022/12/9 15:33
     */
    Boolean updateUserRoles(Long userId, Set<Long> roleIds);


    /**
     * 获取用户权限
     */
    UserRightsVO getUserRights();


}
