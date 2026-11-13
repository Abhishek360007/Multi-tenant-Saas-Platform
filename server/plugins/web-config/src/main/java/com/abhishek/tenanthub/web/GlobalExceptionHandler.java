package com.abhishek.tenanthub.web;

import com.abhishek.tenanthub.auth.enums.OAuth2Error;
import com.abhishek.tenanthub.auth.exception.JwtAuthException;
import com.abhishek.tenanthub.common.exception.BizException;
import com.abhishek.tenanthub.common.util.JsonResult;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * <p>
 * Global exception handler
 * </p>
 *
 * @author Wang Chen Chen
 * @version 1.0.1
 * @date 2021/7/13 13:47
 */

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Data conversion error
     *
     * @param e
     */
    @ExceptionHandler(value = HttpMessageConversionException.class)
    public JsonResult<String> exceptionHandler(HttpMessageConversionException e) {
        return JsonResult.fail(e.getMessage());
    }


    /**
     * Validation error
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public JsonResult<String> exceptionHandler(MethodArgumentNotValidException e) {
        var fieldError = e.getBindingResult().getFieldError();
        var errorMessage = fieldError != null ? fieldError.getDefaultMessage() : e.getMessage();
        return JsonResult.error(e.getStatusCode().value(), errorMessage);
    }


    /**
     * Missing parameter
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public JsonResult<String> exceptionHandler(MethodArgumentTypeMismatchException e) {
        return JsonResult.fail(e.getMessage());
    }


    /**
     * jwt Authentication error
     *
     * @param e
     */
    @ExceptionHandler(value = JwtAuthException.class)
    public JsonResult<String> exceptionHandler(JwtAuthException e) {
        return JsonResult.error(e.getStatus(), e.getMessage());
    }


    /**
     * Null pointer
     *
     * @param e
     */
    @ExceptionHandler(value = NullPointerException.class)
    public JsonResult<String> exceptionHandler(NullPointerException e) {
        int status = HttpStatus.INTERNAL_SERVER_ERROR.value();
        return JsonResult.error(status, e.getMessage());
    }


    /**
     * Authentication failure
     */
    @ExceptionHandler(value = AuthenticationException.class)
    public JsonResult<String> exceptionHandler(HttpServletRequest request, AuthenticationException e) {
        var msg = String.format("Request to %s: authentication failed, access denied", request.getRequestURI());
        return JsonResult.error(OAuth2Error.OAUTH2_EXCEPTION.getStatus(), msg);
    }


    /**
     * Access denied
     */
    @ExceptionHandler(value = AccessDeniedException.class)
    public JsonResult<String> exceptionHandler(HttpServletRequest request, AccessDeniedException e) {
        var msg = String.format("Request to %s: insufficient permissions, contact your administrator", request.getRequestURI());
        return JsonResult.error(OAuth2Error.ACCESS_DENIED.getStatus(), msg);
    }


    /**
     * Business exception
     *
     * @param e
     */
    @ExceptionHandler(value = BizException.class)
    public JsonResult<String> exceptionHandler(BizException e) {
        return JsonResult.fail(e.getMessage());
    }


    /**
     * Runtime error
     *
     * @param e
     */
    @ExceptionHandler(value = RuntimeException.class)
    public JsonResult<String> exceptionHandler(RuntimeException e) {
        return JsonResult.fail(e.getMessage());
    }


    /**
     * Handle other errors
     *
     * @param e
     */
    @ExceptionHandler(value = Exception.class)
    public JsonResult<String> exceptionHandler(Exception e) {
        e.printStackTrace();
        log.error("Unknown error. Cause:: {} ", e.getMessage());
        if (e instanceof ErrorResponse er) {
            return JsonResult.error(er.getStatusCode().value(), e.getMessage());
        }
        int status = HttpStatus.INTERNAL_SERVER_ERROR.value();
        return JsonResult.error(status, e.getMessage());
    }


}
