package com.abhishek.tenanthub.common.util;

import cn.hutool.core.net.NetUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import com.maxmind.db.CHMCache;
import com.maxmind.geoip2.DatabaseReader;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    /**
     * MaxMind GeoLite2 City database. Download GeoLite2-City.mmdb with a free MaxMind account
     * (https://dev.maxmind.com/geoip/geolite2-free-geolocation-data) and point GEOIP_CITY_DB at it.
     * Without it, locations are reported as "Unknown".
     */
    private static final DatabaseReader GEOIP = openGeoIp();

    private static DatabaseReader openGeoIp() {
        var path = StringUtils.defaultIfBlank(System.getenv("GEOIP_CITY_DB"), "./data/GeoLite2-City.mmdb");
        var file = new File(path);
        if (!file.isFile()) {
            log.info("GeoLite2 database not found at {}; IP locations will show as Unknown", file.getAbsolutePath());
            return null;
        }
        try {
            return new DatabaseReader.Builder(file).withCache(new CHMCache()).build();
        } catch (IOException e) {
            log.warn("Could not open GeoLite2 database {}: {}", file.getAbsolutePath(), e.getMessage());
            return null;
        }
    }

    /**
     * Location of an IP, e.g. "Bengaluru, Karnataka, India".
     */
    public static String getRealAddressByIP(String ip) {
        if (NetUtil.isInnerIP(ip) || LOCAL_IP.equals(ip) || LOCALHOST_IP.equals(ip)) {
            return "Private network";
        }
        if (GEOIP == null) {
            return "Unknown";
        }
        try {
            return GEOIP.tryCity(InetAddress.getByName(ip))
                    .map(r -> Stream.of(r.getCity().getName(), r.getMostSpecificSubdivision().getName(), r.getCountry().getName())
                            .filter(StringUtils::isNotBlank)
                            .collect(Collectors.joining(", ")))
                    .filter(StringUtils::isNotBlank)
                    .orElse("Unknown");
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
