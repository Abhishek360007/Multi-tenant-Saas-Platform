package com.abhishek.tenanthub.monitor.api.impl;

import com.abhishek.tenanthub.common.fqueue.FQueue;
import com.abhishek.tenanthub.common.fqueue.FQueueRegistry;
import com.abhishek.tenanthub.common.util.JsonUtils;
import com.abhishek.tenanthub.internal.api.ApiLogStorageService;
import com.abhishek.tenanthub.internal.dto.LoginLogDTO;
import com.abhishek.tenanthub.internal.dto.OperLogDTO;
import com.abhishek.tenanthub.monitor.entity.LmsLoginLog;
import com.abhishek.tenanthub.monitor.entity.LmsOperLog;
import com.abhishek.tenanthub.monitor.service.LmsLoginLogService;
import com.abhishek.tenanthub.monitor.service.LmsOperLogService;
import com.abhishek.tenanthub.tenant.util.DelegateUtils;
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
        // Batch queue: flushes every 100 records or every 10 seconds
        loginLogQueueHandler = FQueueRegistry.buildFQueue(LoginLogDTO.class)
                .fanOut(2)
                .batch()
                .withChunkSize(100)
                .withFlushTimeout(10)
                .withFlushTimeUnit(TimeUnit.SECONDS)
                .done()
                .consume(list -> {
                    var start = System.currentTimeMillis();
                    // Group by index name
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
                    log.info("Batch-saved login logs, count: {}, took: {} ms", list.size(), end);
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
                    // Group by index name
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
                    log.info("Batch-saved operation logs, count: {}, took: {}ms", list.size(), end);
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
