package com.abhishek.tenanthub.auth;

import com.abhishek.tenanthub.auth.enums.OAuth2Error;
import com.abhishek.tenanthub.auth.jwt.JwtAuthTokenFilter;
import com.abhishek.tenanthub.auth.jwt.JwtSecurityUtils;
import com.abhishek.tenanthub.auth.jwt.JwtTokenProperties;
import com.abhishek.tenanthub.auth.service.JwtTokenService;
import com.abhishek.tenanthub.auth.service.impl.JwtTokenServiceImpl;
import com.abhishek.tenanthub.common.util.ServletUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import static com.abhishek.tenanthub.common.consts.JwtConst.WHITE_LIST;


/**
 * <p>
 * spring Security Core configuration
 * </p>
 *
 * @author Wang Chen Chen <932560435@qq.com>
 * @version 2.0
 * @date 2019/4/18 11:45
 */


@Slf4j
@Configuration
@AllArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class WebSecurityConfig {

    private final JwtTokenProperties tokenProperties;

    private final UserDetailsService userDetailsService;


    /**
     * Authentication manager; sign-in parameters are passed to authenticationManager
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration auth) throws Exception {
        return auth.getAuthenticationManager();
    }


    @Autowired
    public void configureAuthentication(AuthenticationManagerBuilder builder) throws Exception {
        builder.userDetailsService(this.userDetailsService)
                .passwordEncoder(JwtSecurityUtils.getPasswordEncoder());
    }


    /**
     * TODO JWT token Save on the server
     *
     * @author WangChenChen
     * @date 2022/12/10 16:30
     */
    @Bean
    public JwtTokenService jwtTokenService(RedisTemplate<String, Object> redisTemplate,
                                           StringRedisTemplate strRedisTemplate) {
        return new JwtTokenServiceImpl(tokenProperties, redisTemplate, strRedisTemplate);
    }


    /**
     * TODO JWT token Authentication interceptor
     *
     * @author WangChenChen
     * @date 2022/12/10 16:30
     */
    @Bean
    public JwtAuthTokenFilter jwtAuthTokenFilter(JwtTokenService jwtTokenService,
                                                 AuthenticationManager authenticationManager) {
        return new JwtAuthTokenFilter(tokenProperties, jwtTokenService, authenticationManager);
    }


    /**
     * spring Security Settings
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, JwtAuthTokenFilter jwtAuthTokenFilter) throws Exception {
        return httpSecurity
                // Close csrf
                .csrf(CsrfConfigurer::disable)
                // Allow cross-origin requests (optional, depends on your needs)
                .cors(AbstractHttpConfigurer::disable)
                .headers(h -> h.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                // Not needed Session
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Custom authentication error handling
                .exceptionHandling(ex -> ex
                        // Authentication error handling
                        .authenticationEntryPoint((request, response, ex1) -> {
                            log.warn(ex1.getMessage());
                            var msg = String.format("Request to %s: authentication failed, access denied", request.getRequestURI());
                            ServletUtils.renderError(response, OAuth2Error.OAUTH2_EXCEPTION.getStatus(), msg);
                        })
                        // Access denied handling
                        .accessDeniedHandler((request, response, ex2) -> {
                            log.warn(ex2.getMessage());
                            var msg = String.format("Request to %s: insufficient permissions, contact your administrator", request.getRequestURI());
                            ServletUtils.renderError(response, OAuth2Error.ACCESS_DENIED.getStatus(), msg);
                        })
                )
                .authorizeHttpRequests(a -> a
                        // Cross-origin calls
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        //  Get the allowlist (no permission checks)
                        .requestMatchers(tokenProperties.getExcludePath()).permitAll()
                        //  Fixed allowlist that skips authentication
                        .requestMatchers(WHITE_LIST).permitAll()
                        //  All other requests require authentication
                        .anyRequest()
                        .authenticated()
                )
                .addFilterBefore(jwtAuthTokenFilter, BasicAuthenticationFilter.class)
                .build();
    }


}
