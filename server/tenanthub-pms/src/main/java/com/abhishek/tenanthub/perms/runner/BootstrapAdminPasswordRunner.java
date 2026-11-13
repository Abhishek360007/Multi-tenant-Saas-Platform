package com.abhishek.tenanthub.perms.runner;

import com.abhishek.tenanthub.perms.entity.PmsUser;
import com.abhishek.tenanthub.perms.mapper.PmsUserMapper;
import com.abhishek.tenanthub.tenant.props.MultiTenantProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import static com.abhishek.tenanthub.auth.jwt.JwtSecurityUtils.encryptPassword;
import static com.abhishek.tenanthub.tenant.util.DelegateUtils.delegate;

/**
 * Lets a deployment choose the platform admin's password without ever using the seeded default.
 * <p>
 * If {@code tenanthub.bootstrap.admin-password} (env: TENANTHUB_BOOTSTRAP_ADMIN_PASSWORD) is set and
 * the seeded admin has never changed its password, that password is applied at startup.
 * Once the admin has changed their password, the setting is ignored.
 *
 * @author Abhishek Raj
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BootstrapAdminPasswordRunner implements ApplicationRunner {

    /**
     * user_id of the platform admin created by pms_init.sql
     */
    static final long SEEDED_ADMIN_ID = 19980817L;

    private final PmsUserMapper userMapper;

    private final MultiTenantProperties multiTenantProperties;

    @Value("${tenanthub.bootstrap.admin-password:}")
    private String bootstrapPassword;

    @Override
    public void run(ApplicationArguments args) {
        if (StringUtils.isBlank(bootstrapPassword)) {
            return;
        }
        delegate(multiTenantProperties.getDefaultTenantId(), () -> {
            var admin = userMapper.selectById(SEEDED_ADMIN_ID);
            if (admin == null || admin.getPasswordChangeRequired() == null || admin.getPasswordChangeRequired() != 1) {
                return false;
            }
            var update = new PmsUser()
                    .setUserId(SEEDED_ADMIN_ID)
                    .setPassword(encryptPassword(bootstrapPassword))
                    .setPasswordChangeRequired((byte) 0);
            userMapper.updateById(update);
            log.info("Applied TENANTHUB_BOOTSTRAP_ADMIN_PASSWORD to the platform admin account.");
            return true;
        });
    }
}
