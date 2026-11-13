package com.abhishek.tenanthub.web.log;

import cn.hutool.core.bean.BeanUtil;
import com.abhishek.tenanthub.auth.jwt.JwtSecurityUtils;
import com.abhishek.tenanthub.common.util.IdUtils;
import com.abhishek.tenanthub.common.util.JsonResult;
import com.abhishek.tenanthub.common.util.ServletUtils;
import com.abhishek.tenanthub.common.util.TenantUtils;
import com.abhishek.tenanthub.internal.api.ApiLogStorageService;
import com.abhishek.tenanthub.internal.dto.OperLogDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * <p>
 * Operation log handling
 * </p>
 *
 * @author Wang Chen Chen
 * @version 1.0.1
 * @date 2021/8/10 15:01
 */


@Slf4j
@Aspect
@Component
public class OperateLogAspect {

    private final ApiLogStorageService logStorage;

    public OperateLogAspect(ApiLogStorageService logStorage) {
        this.logStorage = logStorage;
    }

    @Value("${spring.application.name}")
    private String appName;

    /**
     * Unified aspect for validation, exception handling and logging
     */
    @Pointcut("@annotation(io.swagger.v3.oas.annotations.Operation)")
    public void logPointCut() {
    }


    /**
     * Around advice, equivalent to MethodInterceptor
     */
    @Around("logPointCut()")
    public Object doAfterReturning(ProceedingJoinPoint joinPoint) {
        long startTime = System.currentTimeMillis();
        // Method result
        Object result = null;
        // Duration in milliseconds
        long timeCost = 0L;
        // Method threw an exception
        Throwable ex = null;
        try {
            result = joinPoint.proceed();
            timeCost = System.currentTimeMillis() - startTime;
        } catch (Throwable ew) {
            ex = ew;
            ew.printStackTrace();
            log.error(ew.getMessage());
            result = JsonResult.fail(ew.getMessage());
        } finally {
            //Log after the method completes
            addOperationLog(joinPoint, result, ex, timeCost);
        }
        return result;
    }


    private void addOperationLog(JoinPoint joinPoint, Object resp, Throwable ex, long timeCost) {
        var request = ServletUtils.getRequestInfo();
        // Skip GET requests
        if ("GET".equals(request.getMethod())) {
            return;
        }
        var signature = (MethodSignature) joinPoint.getSignature();
        var operLog = new OperLogDTO();
        var tag = AnnotationUtils.findAnnotation(joinPoint.getTarget().getClass(), Tag.class);
        var annotation = signature.getMethod().getAnnotation(Operation.class);
        if (tag != null) {
            operLog.setTitle(String.format("%s %s", tag.name(), annotation.summary()));
        } else {
            operLog.setTitle(annotation.summary());
        }
        operLog.setDescription(annotation.description());
        operLog.setStatus(HttpStatus.OK.value());
        if (ex != null) {
            operLog.setErrorLog(ex.getMessage());
            operLog.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        operLog.setServiceName(appName);
        // Fully qualified class name and method name
        var methodName = String.format("%s.%s()", signature.getDeclaringTypeName(), signature.getName());
        operLog.setMethod(methodName);
        if (joinPoint.getArgs() != null) {
            var params = new LinkedList<>();
            for (var arg : joinPoint.getArgs()) {
                if (arg instanceof BindingResult
                        || arg instanceof HttpServletRequest
                        || arg instanceof HttpServletResponse
                        || arg instanceof HttpHeaders
                ) {
                    log.debug("spring mvc Object...");
                } else {
                    params.add(arg);
                }
            }
            var methodArgs = params.stream()
                    .map(r -> BeanUtil.beanToMap(r, false, true))
                    .collect(Collectors.toList());
            operLog.setMethodArgs(methodArgs);
        }
        operLog.setRequestUrl(request.getRequestUrl());
        operLog.setRequestMethod(request.getMethod());
        operLog.setRequestIp(request.getIp());
        operLog.setAddress(request.getAddress());
        Map<String, Object> responseResultMap = BeanUtil.beanToMap(resp, false, true);
        operLog.setResponseResult(responseResultMap);
        operLog.setTimeCost(timeCost);
        operLog.setTenantId(TenantUtils.getTenantId());
        if (JwtSecurityUtils.isAuthenticated()) {
            var userInfo = JwtSecurityUtils.getLoginUser();
            operLog.setUserId(userInfo.getUserId());
            operLog.setNickname(userInfo.getNickname());
            operLog.setTenantId(userInfo.getTenantId());
        }
        operLog.setCreateTime(LocalDateTime.now());
        operLog.setId(IdUtils.getStandaloneStrId());
        logStorage.asyncOperateSave(operLog);
    }


}
