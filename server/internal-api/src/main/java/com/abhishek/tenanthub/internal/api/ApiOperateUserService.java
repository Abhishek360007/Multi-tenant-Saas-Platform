package com.abhishek.tenanthub.internal.api;

import com.abhishek.tenanthub.internal.dto.OperateUserDTO;

import java.util.Map;
import java.util.Set;

/**
 * <p>
 * Fill operator details by reflection
 * </p>
 *
 * @author WangChenChen
 * @version 1.1
 * @date 2023/10/31 10:53
 */

public interface ApiOperateUserService {

    /**
     * Get the operator by user ID
     */
    Map<Long, OperateUserDTO> mapOperateUser(Set<Long> userIds);

}
