package com.abhishek.tenanthub.perms.service;

import cn.hutool.core.lang.tree.Tree;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.abhishek.tenanthub.common.po.SearchParentPO;
import com.abhishek.tenanthub.perms.entity.PmsDept;
import com.abhishek.tenanthub.tenant.base.service.BaseService;

import java.util.List;
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

public interface PmsDeptService extends BaseService<PmsDept> {

    /**
     * Paged query
     */
    IPage<PmsDept> pageKeywords(SearchParentPO params);


    /**
     * Get all departments as a tree
     */
    List<Tree<Long>> treeNode();


    /**
     * Get the data permissions of the user department
     */
    Set<Long> listHaveDeptIds(Long userId);


    /**
     * Get child departments ID
     */
    Set<Long> listChildIdByDeptId(Long deptId);


    /**
     * Get child department details
     */
    Set<PmsDept> listChildByDeptId(Long deptId);


    /**
     * Get the full department name by department ID
     * e.g. Google India/R&D/Web frontend team
     */
    Map<Long, String> mapDeptFullName(Set<Long> deptIds);


    /**
     * Get the full department name by department ID
     * e.g. Google India/R&D/Web frontend team
     */
    List<PmsDept> listDeptFullName(Set<Long> deptIds);


    /**
     * Query departments by user ID
     */
    List<PmsDept> listChildDeptByUserId(Long userId);


    /**
     * Query department IDs by user ID
     */
    Set<Long> listChildDeptIdByUserId(Long userId);

}
