package com.abhishek.tenanthub.common.util;

import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.abhishek.tenanthub.common.domain.CustomRequestInfo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;


/**
 * <p>
 *
 * </p>
 *
 * @author WangChenChen
 * @version 1.2
 * @date 2022/5/26 10:24
 */


@Slf4j
public class ServletUtils {

    public static ServletRequestAttributes getRequestAttributes() {
        RequestAttributes attributes = RequestContextHolder.currentRequestAttributes();
        return (ServletRequestAttributes) attributes;
    }


    /**
     * Get request
     */
    public static HttpServletRequest getRequest() {
        return getRequestAttributes().getRequest();
    }


    /**
     * Get response
     */
    public static HttpServletResponse getResponse() {
        return getRequestAttributes().getResponse();
    }


    /**
     * Get a String parameter
     */
    public static String getParameter(String name) {
        return getRequest().getParameter(name);
    }


    /**
     * Get client information
     *
     * @return
     */
    public static UserAgent getUserAgent() {
        String ua = getRequest().getHeader(HttpHeaders.USER_AGENT);
        return UserAgentUtil.parse(ua);
    }


    /**
     * Get client information
     *
     * @return
     */
    public static UserAgent getUserAgent(HttpServletRequest request) {
        String ua = request.getHeader(HttpHeaders.USER_AGENT);
        return UserAgentUtil.parse(ua);
    }


    /**
     * Get client information
     */
    public static UserAgent getUserAgent(String ua) {
        return UserAgentUtil.parse(ua);
    }


    /**
     * Get the full request path
     */
    public static String getFullPath(HttpServletRequest request) {
        // Behind a gateway the prefix must be included
        String prefix = request.getHeader("x-forwarded-prefix");
        StringBuilder url = new StringBuilder(prefix == null ? "" : prefix);
        url.append(request.getRequestURI());
        if (!StringUtils.isEmpty(request.getQueryString())) {
            url.append("?").append(request.getQueryString());
        }
        return url.toString();
    }


    /**
     * Get parameters of the HTTP request client
     */
    public static CustomRequestInfo getRequestInfo() {
        var request = getRequest();
        return getRequestInfo(request);
    }


    /**
     * Get parameters of the HTTP request client
     */
    public static CustomRequestInfo getRequestInfo(HttpServletRequest request) {
        var fullPath = getFullPath(request);
        var userAgent = getUserAgent(request);
        var browser = String.format("%s %s", userAgent.getBrowser(), userAgent.getVersion());
        var os = String.format("%s %s", userAgent.getPlatform(), Objects.requireNonNullElse(userAgent.getOsVersion(), ""));
        var ipAddr = IpUtils.getIpAddr(request);
        var realAddress = IpUtils.getRealAddressByIP(ipAddr);
        return new CustomRequestInfo()
                .setMethod(request.getMethod())
                .setRequestUrl(fullPath)
                .setOsName(os)
                .setBrowser(browser)
                .setIp(ipAddr)
                .setAddress(realAddress);
    }


    /**
     * Render a string to the client
     *
     * @param response Render object
     * @param string   String to render
     * @return null
     */
    public static void renderString(HttpServletResponse response, String string) {
        try {
            response.setStatus(HttpStatus.OK.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.getWriter().println(string);
            response.getWriter().flush();
        } catch (IOException e) {
            log.error("renderString: {}", e.getMessage());
        }
    }


    /**
     * Render a string to the client
     *
     * @param response Render object
     * @param ex       String to render
     * @return null
     */
    public static void renderError(HttpServletResponse response, JsonResult result) {
        renderString(response, JsonUtils.toJson(result));
    }


    /**
     * Render a string to the client
     *
     * @param response Render object
     * @param ex       String to render
     * @return null
     */
    public static void renderError(HttpServletResponse response, int status, String message) {
        String json = JsonUtils.toJson(JsonResult.error(status, message));
        renderString(response, json);
    }


}
