package com.abhishek.tenanthub.perms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.abhishek.tenanthub.common.domain.LinkedTarget;
import com.abhishek.tenanthub.perms.entity.PmsRole;
import com.abhishek.tenanthub.perms.entity.PmsRoleProxy;
import com.abhishek.tenanthub.tenant.ds.DataScope;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Set;


public interface PmsRoleMapper extends BaseMapper<PmsRole> {

    // Add menu permissions to the role
    int insertByMenus(@Param("roleId") Long roleId,
                      @Param("menus") Set<Long> menus);


    // Delete all permissions of the role
    @Delete("delete from pms_role_menu where role_id = #{roleId}")
    int deleteHaveMenus(Long roleId);


    // Whether any user still uses this role
    @Select("select count(*) from pms_user_role where role_id = #{roleId}")
    int userReference(Long roleId);


    /**
     * Query the user roles
     *
     * @author WangChenChen
     * @date 2022/3/22 18:14
     */
    Set<PmsRoleProxy> selectListByUserIds(@Param("userIds") Set<Long> userIds);

    /**
     * Query the user roles
     *
     * @author WangChenChen
     * @date 2022/3/22 18:14
     */
    Set<PmsRoleProxy> selectListByUserId(@Param("userId") Long userId);


    /**
     * Query the menus of a role
     */
    @Select("select menu_id from pms_role_menu where role_id = #{roleId}")
    Set<Long> selectMenuIdByRoleId(Long roleId);


    /**
     * Query the menus of a role
     */
    Set<Long> selectMenuIdByRoleIds(Set<Long> roleIds);


    /**
     * Query the departments linked to a role id
     */
    List<LinkedTarget> selectDeptIdByRoleIds(Set<Long> roleIds);

    /**
     * Delete the departments linked to a role
     */
    @Delete("DELETE FROM pms_role_dept WHERE role_id = #{roleId}")
    int deleteHaveDepts(Long roleId);


    /**
     * Link the role to departments
     */
    int insertByDepts(@Param("roleId") Long roleId,
                      @Param("deptIds") Set<Long> deptIds);



    /**
     * List query by conditions
     */
    @DataScope()
    List<PmsRole> selectRoleList(@Param("p") PmsRole role);


    /**
     * Paged query by conditions
     */
    @DataScope()
    IPage<PmsRole> selectRolePage(IPage<?> page, @Param("p") PmsRole role);



}