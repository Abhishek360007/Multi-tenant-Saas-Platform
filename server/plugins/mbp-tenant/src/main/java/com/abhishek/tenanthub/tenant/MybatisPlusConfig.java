package com.abhishek.tenanthub.tenant;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.abhishek.tenanthub.auth.jwt.JwtSecurityUtils;
import com.abhishek.tenanthub.common.util.IdUtils;
import com.abhishek.tenanthub.common.util.JsonUtils;
import com.abhishek.tenanthub.common.util.TenantUtils;
import com.abhishek.tenanthub.tenant.props.MultiTenantProperties;
import com.abhishek.tenanthub.tenant.schema.SchemaInterceptor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.abhishek.tenanthub.common.consts.MbpConst.*;


/**
 * <p>
 * </p>
 *
 * @author Wang Chen Chen
 * @version 1.0
 * @date 2021/7/8 9:21
 */


@Slf4j
@Configuration
@AllArgsConstructor
@EnableTransactionManagement
public class MybatisPlusConfig {

    private final MultiTenantProperties multiTenantProperties;

    /**
     * Per-page row limit (unlimited by default, see the plugin handlerLimit method)
     */
    private static final Long MAX_LIMIT = 100L;

    /**
     * New pagination plugin; first- and second-level caches follow MyBatis rules,
     * Must set MybatisConfiguration#useDeprecatedExecutor = false
     * Avoids cache problems (removed together with the old plugin)
     */
    @Bean
    public MybatisPlusInterceptor paginationInterceptor() {
        // Set ObjectMapper
        JacksonTypeHandler.setObjectMapper(JsonUtils.getMapper());

        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // Enable tenants
        if (multiTenantProperties.getEnable()) {
            var schemaInterceptor = new SchemaInterceptor(multiTenantProperties);
            interceptor.addInnerInterceptor(schemaInterceptor);
        }

        // Enable projects
        if (multiTenantProperties.getEnableProject()) {
            interceptor.addInnerInterceptor(
                    new TenantLineInnerInterceptor(
                            new ProjectLineHandler(multiTenantProperties)
                    )
            );
        }

        //Pagination plugin: PaginationInnerInterceptor
        var paginationInnerInterceptor = new PaginationInnerInterceptor();
        paginationInnerInterceptor.setMaxLimit(MAX_LIMIT);
        interceptor.addInnerInterceptor(paginationInnerInterceptor);

        //Block full-table update and delete: BlockAttackInnerInterceptor
        var blockAttackInnerInterceptor = new BlockAttackInnerInterceptor();
        interceptor.addInnerInterceptor(blockAttackInnerInterceptor);

        return interceptor;
    }


    /**
     * Custom ID generator
     *
     * @author WangChenChen
     * @version 1.0.1
     * @date 2024/1/14 14:32
     */
    @Bean
    public IdentifierGenerator idGenerator() {
        return entity -> IdUtils.getStandaloneId();
    }


    /**
     * Automatic field fill
     *
     * @author WangChenChen
     * @version 1.0.1
     * @date 2024/1/14 14:32
     */
    @Slf4j
    @Component
    @AllArgsConstructor
    public static class MyMetaObjectHandler implements MetaObjectHandler {

        private final MultiTenantProperties multiTenantProperties;

        @Override
        public void insertFill(MetaObject metaObject) {
            if (JwtSecurityUtils.isAuthenticated()) {
                var userId = JwtSecurityUtils.getUserId();
                this.strictInsertFill(metaObject, ATTR_CREATE_USER, () -> userId, Long.class);
                this.strictInsertFill(metaObject, ATTR_LAST_UPDATE_USER, () -> userId, Long.class);
            }
            this.strictInsertFill(metaObject, ATTR_CREATE_TIME, LocalDateTime::now, LocalDateTime.class);
            this.strictInsertFill(metaObject, ATTR_LAST_UPDATE_TIME, LocalDateTime::now, LocalDateTime.class);
            if (metaObject.hasSetter(ATTR_PROJECT_ID)) {
                var projectId = Optional.ofNullable(TenantUtils.getProjectId())
                        .orElse(multiTenantProperties.getDefaultProjectId());
                this.strictInsertFill(metaObject, ATTR_PROJECT_ID, () -> projectId, Long.class);
            }
        }

        @Override
        public void updateFill(MetaObject metaObject) {
            if (JwtSecurityUtils.isAuthenticated()) {
                var userId = JwtSecurityUtils.getUserId();
                this.strictUpdateFill(metaObject, ATTR_LAST_UPDATE_USER, () -> userId, Long.class);
            }
            this.strictUpdateFill(metaObject, ATTR_LAST_UPDATE_TIME, LocalDateTime::now, LocalDateTime.class);
        }

    }

}
