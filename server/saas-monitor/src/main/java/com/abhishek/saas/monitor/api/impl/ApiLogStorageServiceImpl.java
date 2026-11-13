package com.abhishek.saas.monitor.api.impl;

import com.abhishek.saas.common.fqueue.FQueue;
import com.abhishek.saas.common.fqueue.FQueueRegistry;
import com.abhishek.saas.common.util.JsonUtils;
import com.abhishek.saas.internal.api.ApiLogStorageService;
import com.abhishek.saas.internal.dto.LoginLogDTO;
import com.abhishek.saas.internal.dto.OperLogDTO;
import com.abhishek.saas.monitor.entity.LmsLoginLog;
import com.abhishek.saas.monitor.entity.LmsOperLog;
import com.abhishek.saas.monitor.service.LmsLoginLogService;
import com.abhishek.saas.monitor.service.LmsOperLogService;
import com.abhishek.saas.tenant.util.DelegateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * <p>
 * </p>
 *
 * @author WangChenChen
 * @version 1.1
 * @date 2023/2/14 11:48
 */


@Slf4j
@Service
public class ApiLogStorageServiceImpl implements ApiLogStorageService {

    private final FQueue<LoginLogDTO> loginLogQueueHandler;

    private final FQueue<OperLogDTO> operLogQueueHandler;


    public ApiLogStorageServiceImpl(LmsLoginLogService loginLogService, LmsOperLogService operLogService) {
        // 批量操作队列。100条数据，或者10秒钟提交一次
        loginLogQueueHandler = FQueueRegistry.buildFQueue(LoginLogDTO.class)
                .fanOut(2)
                .batch()
                .withChunkSize(100)
                .withFlushTimeout(10)
                .withFlushTimeUnit(TimeUnit.SECONDS)
                .done()
                .consume(list -> {
                    var start = System.currentTimeMillis();
                    // 根据 索引名称 分组
                    list.stream()
                            .collect(Collectors.groupingBy(LoginLogDTO::getTenantId))
                            .forEach((tenantId, values) -> {
                                var collect = values.stream()
                                        .map(source -> {
                                            var target = new LmsLoginLog();
                                            BeanUtils.copyProperties(source, target);
                                            return target;
                                        }).collect(Collectors.toSet());
                                if (!collect.isEmpty()) {
                                    DelegateUtils.delegate(tenantId, () -> loginLogService.saveBatch(collect));
                                }
                            });
                    var end = System.currentTimeMillis() - start;
                    log.info("批量保存登录日志 数量: {} , 耗时: {} ms", list.size(), end);
                });

        operLogQueueHandler = FQueueRegistry.buildFQueue(OperLogDTO.class)
                .fanOut(2)
                .batch()
                .withChunkSize(100)
                .withFlushTimeout(10)
                .withFlushTimeUnit(TimeUnit.SECONDS)
                .done()
                .consume(list -> {
                    var start = System.currentTimeMillis();
                    // 根据 索引名称 分组
                    list.stream()
                            .collect(Collectors.groupingBy(OperLogDTO::getTenantId))
                            .forEach((tenantId, values) -> {
                                var collect = values.stream()
                                        .map(source -> {
                                            var target = new LmsOperLog();
                                            BeanUtils.copyProperties(source, target);
                                            target.setMethodArgs(JsonUtils.toJson(source.getMethodArgs()));
                                            target.setResponseResult(JsonUtils.toJson(source.getResponseResult()));
                                            return target;
                                        }).collect(Collectors.toSet());
                                if (!collect.isEmpty()) {
                                    DelegateUtils.delegate(tenantId, () -> operLogService.saveBatch(collect));
                                }
                            });
                    var end = System.currentTimeMillis() - start;
                    log.info("批量保存操作日志 数量: {} , 耗时: {}ms", list.size(), end);
                });
    }


    @Override
    public void asyncLoginSave(LoginLogDTO source) {
        loginLogQueueHandler.add(source);
    }


    @Override
    public void asyncOperateSave(OperLogDTO source) {
        operLogQueueHandler.add(source);
    }


}
