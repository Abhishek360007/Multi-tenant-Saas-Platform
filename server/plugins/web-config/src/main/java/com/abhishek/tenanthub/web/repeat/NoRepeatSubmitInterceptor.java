package com.abhishek.tenanthub.web.repeat;

import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import com.abhishek.tenanthub.auth.jwt.JwtSecurityUtils;
import com.abhishek.tenanthub.common.util.JsonResult;
import com.abhishek.tenanthub.common.util.ServletUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.abhishek.tenanthub.common.consts.LoginConst.NO_REPEAT_SUBMIT;

/**
 * TODO Duplicate request interceptor
 *
 * @author WangChenChen
 * @date 2023/10/31 19:30
 */


@Slf4j
@Component
@AllArgsConstructor
public class NoRepeatSubmitInterceptor implements HandlerInterceptor {

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) throws Exception {
        if (JwtSecurityUtils.isAuthenticated() && handler instanceof HandlerMethod method) {
            // on the method @NoRepeatSubmit
            var repeatSubmitByMethod = AnnotationUtils.findAnnotation(method.getMethod(), NoRepeatSubmit.class);
            // on the controller class @NoRepeatSubmit
            var repeatSubmitByCls = AnnotationUtils.findAnnotation(method.getMethod().getDeclaringClass(), NoRepeatSubmit.class);
            // No duplicate submission limit, skip
            if (Objects.isNull(repeatSubmitByMethod) && Objects.isNull(repeatSubmitByCls)) {
                return HandlerInterceptor.super.preHandle(request, response, handler);
            }
            var redisKey = String.format("%s:%s:%s:%s",
                    NO_REPEAT_SUBMIT,
                    JwtSecurityUtils.getLoginId(),
                    request.getMethod(),
                    HexUtil.encodeHexStr(request.getRequestURI())
            );
            var ms = Objects.nonNull(repeatSubmitByMethod) ? repeatSubmitByMethod.value() : repeatSubmitByCls.value();
            //Returns false if it exists, otherwise true
            var ifAbsent = stringRedisTemplate.opsForValue().setIfAbsent(redisKey, StrUtil.EMPTY, ms, TimeUnit.MILLISECONDS);
            //If it exists the request was already made; throw and let the global handler return the message
            if (ifAbsent != null && !ifAbsent) {
                ServletUtils.renderError(response, JsonResult.fail("Duplicate submission. Please try again shortly."));
                return false;
            }
        }
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

}
