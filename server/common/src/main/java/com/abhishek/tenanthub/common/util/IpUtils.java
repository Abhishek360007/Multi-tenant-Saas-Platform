package com.abhishek.tenanthub.common.util;

import cn.hutool.core.net.NetUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.lionsoul.ip2region.xdb.Searcher;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;

import static cn.hutool.core.net.NetUtil.LOCAL_IP;

/**
 * <p>
 * </p>
 *
 * @author Wang Chen Chen
 * @version 1.0.1
 * @date 2021/8/3 9:13
 */

@Slf4j
public class IpUtils {

    private static final String IP_UTILS_FLAG = ",";

    private static final String UNKNOWN = "unknown";

    private static final String LOCALHOST_IP = "0:0:0:0:0:0:0:1";

    private static Searcher SEARCHER = null;

    static {
        final var cpr = new ClassPathResource("ip2region.xdb");
        try {
            byte[] dbBinStr = FileCopyUtils.copyToByteArray(cpr.getInputStream());
            SEARCHER = Searcher.newWithBuffer(dbBinStr);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Get the real location of an IP
     */
    public static String getRealAddressByIP(String ip) {
        if (NetUtil.isInnerIP(ip)) {
            return "Private network IP";
        }
        try {
            return SEARCHER.search(ip);
        } catch (Exception e) {
            return "Unknown";
        }
    }

    /**
     * Get the IP address
     * <p>
     * Behind Nginx or another reverse proxy, request.getRemoteAddr() does not return the client IP
     * With several proxies, X-Forwarded-For holds a list of IPs; the first valid non-unknown one is the client IP
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ip = null;
        //In Kubernetes the client IP is in x-Original-Forwarded-For and the WAF origin address is in x-Forwarded-For.
        ip = request.getHeader("X-Original-Forwarded-For");
        if (StringUtils.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }
        //Get from nginx or other proxies ip
        if (StringUtils.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("x-forwarded-for");
        }
        if (StringUtils.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isEmpty(ip) || ip.isEmpty() || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (StringUtils.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        //Kubernetes-compatible lookup ip
        if (StringUtils.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
            if (LOCAL_IP.equalsIgnoreCase(ip) || LOCALHOST_IP.equalsIgnoreCase(ip)) {
                //Get the configured address from the network interface IP
                ip = NetUtil.getLocalhostStr();
            }
        }
        //Behind a proxy, use the first IP address
        if (!StringUtils.isEmpty(ip) && ip.indexOf(IP_UTILS_FLAG) > 0) {
            ip = ip.substring(0, ip.indexOf(IP_UTILS_FLAG));
        }
        return ip;
    }


    /**
     * Get the local IP address
     *
     * @return Local IP address
     */
    public static String getHostIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException ignored) {
        }
        return "127.0.0.1";
    }


    /**
     * Get the host name
     *
     * @return Local host name
     */
    public static String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ignored) {
        }
        return "Unknown";
    }


    /**
     * Gets the server IP address (multiple NICs)
     */
    public static Set<String> getLocalIPS() {
        var ipList = new HashSet<String>();
        try {
            var netInterfaces = NetworkInterface.getNetworkInterfaces();
            while (netInterfaces.hasMoreElements()) {
                var ips = netInterfaces.nextElement().getInetAddresses();
                while (ips.hasMoreElements()) {
                    var ip = ips.nextElement();
                    if (!ip.isLoopbackAddress() && ip.getHostAddress().matches("(\\d{1,3}\\.){3}\\d{1,3}")) {
                        ipList.add(ip.getHostAddress());
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return ipList;
    }


}
