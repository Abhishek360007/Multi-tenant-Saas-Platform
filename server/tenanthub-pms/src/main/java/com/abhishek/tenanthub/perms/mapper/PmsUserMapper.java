package com.abhishek.tenanthub.perms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.abhishek.tenanthub.perms.entity.PmsUser;
import com.abhishek.tenanthub.tenant.ds.DataScope;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;


public interface PmsUserMapper extends BaseMapper<PmsUser> {

    /**
     * Delete the roles of a user
     */
    @Delete("delete from pms_user_role where user_id = #{userId}")
    int deleteHaveRoles(Long userId);


    /**
     * Link a user to several roles
     */
    int insertByRoles(@Param("userId") Long userId,
                      @Param("roles") Set<Long> roles);


    /**
     * Query by user ID
     */
    List<PmsUser> selectSimpleListByUserIds(
            @Param("dbNameList") Set<String> dbNameList,
            @Param("userIdList") Set<Long> userIdList);



    /**
     * List query by conditions
     */
    @DataScope()
    List<PmsUser> selectUserList(@Param("p") PmsUser user);


    /**
     * Paged query by conditions
     */
    @DataScope()
    IPage<PmsUser> selectUserPage(IPage<?> page, @Param("p") PmsUser user);


}