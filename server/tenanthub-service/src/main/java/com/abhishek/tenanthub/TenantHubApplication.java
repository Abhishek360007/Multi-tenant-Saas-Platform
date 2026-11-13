package com.abhishek.tenanthub;

import org.dromara.x.file.storage.spring.EnableFileStorage;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;


/**
 * <p>
 * spring boot mybatis-plus Multi-tenant application entry point
 * </p>
 *
 * @author WangChenChen
 * @version 1.0.1
 * @date 2021/10/14 14:24
 */


@EnableAsync
@EnableCaching
@EnableScheduling
@EnableFileStorage
@SpringBootApplication
@MapperScan("com.abhishek.tenanthub.*.mapper")
public class TenantHubApplication {

    public static void main(String[] args) {
        // The API stores and returns times in UTC (the web UIs convert to the viewer's time zone),
        // so don't depend on the host's time zone or a -Duser.timezone flag
        java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("UTC"));
        SpringApplication.run(TenantHubApplication.class, args);
    }

}
