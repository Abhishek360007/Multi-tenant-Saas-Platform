package com.abhishek.tenanthub.internal.api;

import com.abhishek.tenanthub.internal.dto.PmsDeptDTO;

import java.util.Map;
import java.util.Set;


/**
 * <p>
 * Department
 * </p>
 *
 * @author WangChenChen
 * @version 1.1
 * @date 2023/2/14 10:17
 */

public interface ApiPmsDeptService {

    /**
     * Get department details by department ID
     */
    Map<Long, PmsDeptDTO> mapByDeptIds(Set<Long> deptIds);


    /**
     * Get the full department name by department ID
     * e.g. Google India/R&D/Web frontend team
     */
    Map<Long, PmsDeptDTO> mapFullNameByDeptIds(Set<Long> deptIds);


    /**
     * Get child departments
     */
    Set<Long> listChildIdByDeptId(Long deptId);


    /**
     * Get child departments
     */
    Set<PmsDeptDTO> listChildByDeptId(Long deptId);


    /**
     * Get all departments ID
     */
    Set<Long> listDeptIdByAll();


    /**
     * Get the departments linked to a role id
     */
    Set<Long> listDeptIdByRuleId(Set<Long> ruleIds);

}
