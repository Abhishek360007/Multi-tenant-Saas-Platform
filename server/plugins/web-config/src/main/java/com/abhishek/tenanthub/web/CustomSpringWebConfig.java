package com.abhishek.tenanthub.web;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.net.Ipv4Util;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.abhishek.tenanthub.common.consts.JwtConst;
import com.abhishek.tenanthub.common.util.JsonUtils;
import com.abhishek.tenanthub.internal.api.ApiSysTenantService;
import com.abhishek.tenanthub.web.interceptor.ProjectIdInterceptor;
import com.abhishek.tenanthub.web.interceptor.TenantIdInterceptor;
import com.abhishek.tenanthub.web.repeat.NoRepeatSubmitInterceptor;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.format.Formatter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.abhishek.tenanthub.common.util.IpUtils.getLocalIPS;


@Slf4j
@Configuration
@AllArgsConstructor
public class CustomSpringWebConfig implements WebMvcConfigurer {

    private final ServerProperties sp;

    /**
     * TODO Print the docs URL
     *
     * @author WangChenChen
     * @date 2023/3/26 1:30
     */
    @PostConstruct
    public void outputDocAddress() {
        Set<String> localIPS = getLocalIPS();
        localIPS.add("localhost");
        localIPS.add(Ipv4Util.LOCAL_IP);
        localIPS.forEach(ip -> log.info("http://{}:{}/doc.html", ip, sp.getPort()));
    }


    // Duplicate submission interceptor
    private final NoRepeatSubmitInterceptor noRepeatSubmitInterceptor;

    // Tenant ID interceptor
    private final TenantIdInterceptor tenantIdInterceptor;

    // Project ID interceptor
    private final ProjectIdInterceptor projectIdInterceptor;

    private final ApiSysTenantService tenantService;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // The login endpoint also needs the tenant ID
        var whiteList = Stream.of(JwtConst.WHITE_LIST, JwtConst.TENANT_WHITE_LIST)
                .flatMap(Stream::of)
                .filter(s -> !JwtConst.LOGIN_URL.equals(s))
                .distinct()
                .collect(Collectors.toList());

        var mtp = tenantService.getByMultiTenantProperties();
        // Enable the tenant ID interceptor
        if (mtp.getEnable()) {
            registry.addInterceptor(tenantIdInterceptor)
                    .addPathPatterns("/**")
                    .excludePathPatterns(whiteList);
            log.info("Enable TenantIdInterceptor Success ...");
        }

        // Enable the project ID interceptor
        if (mtp.getEnableProject()) {
            registry.addInterceptor(projectIdInterceptor)
                    .addPathPatterns("/**")
                    .excludePathPatterns(whiteList);
            log.info("Enable ProjectIdInterceptor Success ...");
        }

        // Add the duplicate submission interceptor
        registry.addInterceptor(noRepeatSubmitInterceptor)
                .addPathPatterns("/**");
        WebMvcConfigurer.super.addInterceptors(registry);
    }


    @Bean
    @Primary
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }


    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        return JsonUtils.getMapper();
    }


    /**
     * CORS filter
     *
     * @author WangChenChen
     * @date 2022/12/30 13:42
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern("*");
        config.setAllowCredentials(true);
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.addExposedHeader("*");
        UrlBasedCorsConfigurationSource configSource = new UrlBasedCorsConfigurationSource();
        configSource.registerCorsConfiguration("/**", config);
        return new CorsFilter(configSource);
    }


    /***
     * Date converter
     */
    @Bean
    public Formatter<Date> dateFormatter() {
        return new Formatter<>() {
            @Override
            public Date parse(String text, Locale locale) {
                Date date = null;
                try {
                    date = DateUtils.parseDate(text, locale, DatePattern.NORM_DATETIME_PATTERN);
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
                return date;
            }

            @Override
            public String print(Date date, Locale locale) {
                return DateFormatUtils.format(date, DatePattern.NORM_DATETIME_PATTERN, TimeZone.getDefault(), locale);
            }
        };
    }

    @Bean
    public Formatter<LocalDate> localDateFormatter() {
        return new Formatter<>() {
            @Override
            public LocalDate parse(String text, Locale locale) {
                return LocalDate.parse(text, DatePattern.NORM_DATE_FORMATTER.withLocale(locale));
            }

            @Override
            public String print(LocalDate object, Locale locale) {
                return DatePattern.NORM_DATE_FORMATTER.withLocale(locale).format(object);
            }
        };
    }

    @Bean
    public Formatter<LocalTime> localTimeFormatter() {
        return new Formatter<>() {
            @Override
            public LocalTime parse(String text, Locale locale) {
                return LocalTime.parse(text, DatePattern.NORM_TIME_FORMATTER.withLocale(locale));
            }

            @Override
            public String print(LocalTime object, Locale locale) {
                return DatePattern.NORM_TIME_FORMATTER.withLocale(locale).format(object);
            }
        };
    }

    @Bean
    public Formatter<LocalDateTime> localDateTimeFormatter() {
        return new Formatter<>() {
            @Override
            public LocalDateTime parse(String text, Locale locale) {
                return LocalDateTime.parse(text, DatePattern.NORM_DATETIME_FORMATTER.withLocale(locale));
            }

            @Override
            public String print(LocalDateTime localDateTime, Locale locale) {
                return DatePattern.NORM_DATETIME_FORMATTER.withLocale(locale).format(localDateTime);
            }
        };
    }

}
