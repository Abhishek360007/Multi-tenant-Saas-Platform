package com.abhishek.tenanthub.tenant.base.service;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.service.IService;
import com.abhishek.tenanthub.auth.jwt.JwtSecurityUtils;
import com.abhishek.tenanthub.common.po.SearchPO;
import com.abhishek.tenanthub.tenant.base.BaseEntity;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;


/**
 * <p>
 * Generic service implementation
 * </p>
 *
 * @author Wang Chen Chen <932560435@qq.com>
 * @version 2.0
 * @date 2019/4/18 11:45
 */


public interface BaseService<T extends BaseEntity> extends IService<T> {

    /**
     * Get the operator by user ID
     */
    void reflectionFill(Object objList);

    /**
     * Filled on insert
     */
    default <S extends BaseEntity> void saveFill(S entity) {
        var userId = JwtSecurityUtils.getLoginUser().getUserId();
        if (null == entity.getCreateUser() && userId != null) {
            entity.setCreateUser(userId);
        }
        if (null == entity.getLastUpdateUser() && userId != null) {
            entity.setLastUpdateUser(userId);
        }
        entity.setCreateTime(LocalDateTime.now());
        entity.setLastUpdateTime(LocalDateTime.now());
    }


    /**
     * Filled on update
     */
    default <S extends BaseEntity> void updateFill(S entity) {
        var userId = JwtSecurityUtils.getLoginUser().getUserId();
        entity.setCreateTime(null);
        entity.setCreateUser(null);
        if (userId != null) {
            entity.setLastUpdateUser(userId);
        }
        entity.setLastUpdateTime(LocalDateTime.now());
    }


    /**
     * Query by keyword
     *
     * @param params  Parameters
     * @param columns Entity field
     * @author Wang Chen Chen
     * @date 2021/8/25 9:41
     */
    IPage<T> pageKeywords(SearchPO params, Collection<SFunction<T, ?>> columns);


    /**
     * Query all by conditions
     *
     * @author Wang Chen Chen
     * @date 2021/8/25 9:41
     */
    List<T> listByEq(SFunction<T, ?> column, Object value);


    /**
     * Query all by multiple conditions
     *
     * @param columns
     * @author Wang Chen Chen
     * @date 2021/8/25 9:41
     */
    List<T> listByEq(Map<SFunction<T, ?>, Object> columns);


    /**
     * Query all by multiple conditions
     *
     * @param values
     * @author Wang Chen Chen
     * @date 2021/8/25 9:41
     */
    List<T> listByIn(SFunction<T, ?> column, Collection<?> values);


    /**
     * Check whether a record exists by field
     *
     * @author Wang Chen Chen
     * @date 2021/8/25 9:41
     */
    boolean exist(SFunction<T, ?> column, Object value);


    /**
     * Check whether a record exists by field
     *
     * @author Wang Chen Chen
     * @date 2021/8/25 9:41
     */
    boolean exist(Wrapper<T> wrapper);


    /**
     * Count by field
     *
     * @return
     * @author Wang Chen Chen
     * @date 2021/8/25 9:41
     */
    long count(SFunction<T, ?> column, Object value);


    /**
     * Check whether a record exists by field
     *
     * @author Wang Chen Chen
     * @date 2021/8/25 9:41
     */
    T getOne(SFunction<T, ?> column, Object value);


}
