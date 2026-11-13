package com.abhishek.tenanthub.perms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.abhishek.tenanthub.perms.entity.PmsDept;
import com.abhishek.tenanthub.tenant.ds.DataScope;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * </p>
 *
 * @author WangChenChen
 * @version 1.1
 * @date 2022/12/14 10:03
 */

public interface PmsDeptMapper extends BaseMapper<PmsDept> {

    // Query descendant departments by department ID, excluding itself
    @Select("select * from pms_dept where find_in_set(#{deptId}, ancestors)")
    Set<PmsDept> selectChildDept(Long deptId);


    // Query descendant department IDs by department ID, excluding itself
    @Select("select dept_id from pms_dept where find_in_set(#{deptId}, ancestors)")
    Set<Long> selectChildDeptId(Long deptId);


    /**
     * Update child relationships
     *
     * @param childs Children
     */
    int updateChilds(@Param("childs") Set<PmsDept> childs);


    /**
     * Delete all permissions of the department
     *
     * @param id Department ID
     */
    @Delete("DELETE FROM pms_dept_menu WHERE dept_id = #{id}")
    int deleteHaveMenus(Long id);


    /**
     * Add menu permissions to the department
     *
     * @param id    Department ID
     * @param menus Menu ID
     */
    int insertByMenus(@Param("id") Long id,
                      @Param("menus") Set<Long> menus);


    /**
     * Query departments by user ID
     */
    List<PmsDept> selectChildDeptByUserId(Long userId);


    /**
     * Query department IDs by user ID
     */
    Set<Long> selectChildDeptIdByUserId(Long userId);


    /**
     * Query departments by user ID
     */
    PmsDept selectDeptByUserId(Long userId);


    /**
     * Query department permissions by role ID
     */
    List<PmsDept> selectDeptByRuleIds(Set<Long> roleIds);


    /**
     * Query role department permissions
     */
    Set<Long> selectDeptIdByRuleIds(Set<Long> roleIds);


    /**
     * Paged query by conditions
     */
    @DataScope()
    IPage<PmsDept> selectDeptPage(IPage<?> page, @Param("p") PmsDept role);


    /**
     * List query by conditions
     */
    @DataScope()
    List<PmsDept> selectDeptList(@Param("p") PmsDept dept);


    @DataScope()
    Set<Long> selectDeptIdList(@Param("p") PmsDept dept);



}
