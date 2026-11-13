package com.abhishek.tenanthub.monitor.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.abhishek.tenanthub.common.po.SearchPO;
import com.abhishek.tenanthub.common.util.TenantUtils;
import com.abhishek.tenanthub.internal.api.ApiPmsUserService;
import com.abhishek.tenanthub.internal.dto.PmsUserDTO;
import com.abhishek.tenanthub.monitor.entity.LmsOperLog;
import com.abhishek.tenanthub.monitor.mapper.LmsOperLogMapper;
import com.abhishek.tenanthub.monitor.service.LmsOperLogService;
import com.abhishek.tenanthub.tenant.service.MultiTenantManager;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static com.abhishek.tenanthub.tenant.util.DelegateUtils.delegate;


/**
 * <p>
 * Operation log
 * </p>
 *
 * @author WangChenChen
 * @version 1.1
 * @date 2023/2/6 15:28
 */


@Slf4j
@Service
@AllArgsConstructor
public class LmsOperLogServiceImpl extends ServiceImpl<LmsOperLogMapper, LmsOperLog> implements LmsOperLogService {

    private final ApiPmsUserService userService;

    private final MultiTenantManager tenantManager;


    @Override
    public IPage<LmsOperLog> pageKeywords(SearchPO params) {
        Page<LmsOperLog> pageRequest = Page.of(params.getPageIndex(), params.getPageSize());
        var wrapper = new LambdaQueryWrapper<LmsOperLog>();
        // Whether the start time is empty
        if (ObjectUtil.isNotEmpty(params.getStartDate())) {
            // Whether the end time is empty
            if (ObjectUtil.isNotEmpty(params.getEndDate())) {
                wrapper.between(LmsOperLog::getCreateTime, params.getStartDate(), params.getEndDate());
            } else {
                wrapper.between(LmsOperLog::getCreateTime, params.getStartDate(), LocalDate.now());
            }
        }
        if (StringUtils.isNotBlank(params.getKeywords())) {
            wrapper.like(LmsOperLog::getCreateTime, params.getKeywords());
        }
        wrapper.orderByDesc(LmsOperLog::getCreateTime);
        var result = super.page(pageRequest, wrapper);
        include(result.getRecords());
        return result;
    }


    private void include(List<LmsOperLog> list) {
        if (list.isEmpty()) {
            return;
        }
        var userIds = list.stream()
                .map(LmsOperLog::getUserId)
                .collect(Collectors.toSet());

        // Get users of the default tenant ID
        var delegate1 = delegate(tenantManager.getDefaultTenantId(), () -> {
            return userService.listByUserIds(userIds)
                    .stream().collect(Collectors.toMap(PmsUserDTO::getUserId, r -> r));
        });

        var mapUsers = new HashMap<Long, PmsUserDTO>(delegate1);

        // Get users of non-default tenant IDs
        if (!tenantManager.isDefaultTenantId(TenantUtils.getTenantId())) {
            var delegate2 = delegate(TenantUtils.getTenantId(), () -> {
                return userService.listByUserIds(userIds)
                        .stream().collect(Collectors.toMap(PmsUserDTO::getUserId, r -> r));
            });
            mapUsers.putAll(delegate2);
        }

        list.stream()
                .filter(f -> f.getUserId() != null)
                .forEach(r -> {
                    var user = mapUsers.get(r.getUserId());
                    r.setNickname(user.getNickname());
                    r.setUsername(user.getUsername());
                    r.setAvatar(user.getAvatar());
                });
    }


}