package com.abhishek.tenanthub.auth.jwt;

import com.abhishek.tenanthub.auth.enums.OAuth2Error;
import com.abhishek.tenanthub.auth.service.JwtTokenService;
import com.abhishek.tenanthub.common.util.JsonResult;
import com.abhishek.tenanthub.common.util.ServletUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;

import java.io.IOException;

import static com.abhishek.tenanthub.auth.enums.OAuth2Error.ACCESS_TOKEN_INVALID;
import static com.abhishek.tenanthub.auth.enums.OAuth2Error.PASSWORD_CHANGE_REQUIRED;
import static com.abhishek.tenanthub.common.consts.JwtConst.WHITE_LIST;


/**
 * <p>
 * jwt Core interceptor
 * </p>
 *
 * @author Wang Chen Chen <932560435@qq.com>
 * @version 2.0
 * @date 2019/4/18 11:45
 */


@Slf4j
public class JwtAuthTokenFilter extends BasicAuthenticationFilter {

    private final JwtTokenProperties props;

    private final JwtTokenService tokenService;

    public JwtAuthTokenFilter(JwtTokenProperties props,
                              JwtTokenService tokenService,
                              AuthenticationManager authenticationManager) {
        super(authenticationManager);
        this.props = props;
        this.tokenService = tokenService;
    }

    /**
     * Get the signed-in user
     * <p>
     * JwtUserDetails userDetails = JwtSecurityUtils.getLoginUser()
     *
     * @throws IOException
     * @author Wang Chen Chen<932560435@qq.com>
     * @date 2019/12/19 0:11
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        // Browser CORS preflights never carry a token; skip them, as do paths that need no login
        if (HttpMethod.OPTIONS.matches(request.getMethod()) || urlIgnore(request.getRequestURI())) {
            chain.doFilter(request, response);
            return;
        }
        // Read the token from the Authorization request header
        String bearerToken = request.getHeader(props.getTokenHeader());
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(props.getTokenType())) {
            try {
                // Look up the user in Redis by token; skip this if the token is fully trusted
                var userDetails = tokenService.validate(bearerToken);
                // Accounts with an administrator-set password may only change it (or log out)
                if (isPasswordChangePending(userDetails, request.getRequestURI())) {
                    ServletUtils.renderError(response, JsonResult.error(PASSWORD_CHANGE_REQUIRED.getStatus(),
                            PASSWORD_CHANGE_REQUIRED.getError()));
                    return;
                }
                var authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // Put the user into the SecurityContext so the current user can be read anywhere with the statement below
                SecurityContextHolder.getContext().setAuthentication(authentication);
                chain.doFilter(request, response);
            } catch (Exception ex) {
                SecurityContextHolder.clearContext();
                ServletUtils.renderError(response, OAuth2Error.OAUTH2_EXCEPTION.getStatus(), ex.getMessage());
            }
        } else {
            var error = JsonResult.error(ACCESS_TOKEN_INVALID.getStatus(), ACCESS_TOKEN_INVALID.getError());
            ServletUtils.renderError(response, error);
        }
    }


    // spring Path matcher
    private static final PathMatcher PATH_MATCHER = new AntPathMatcher();

    /**
     * Endpoints still reachable while a password change is pending.
     */
    private static final String[] PASSWORD_CHANGE_ALLOWED = {"/auth/**", "/pms/user/update/password"};

    private static boolean isPasswordChangePending(JwtLoginUser user, String url) {
        if (user.getPasswordChangeRequired() == null || user.getPasswordChangeRequired() != 1) {
            return false;
        }
        for (String pattern : PASSWORD_CHANGE_ALLOWED) {
            if (PATH_MATCHER.match(pattern, url)) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param url URL to check
     * @author Wang Chen Chen
     * @date 2021/7/9 10:14
     */
    public boolean urlIgnore(String url) {
        for (String pattern : WHITE_LIST) {
            if (PATH_MATCHER.match(pattern, url)) {
                return true;
            }
        }
        for (String pattern : props.getExcludePath()) {
            if (PATH_MATCHER.match(pattern, url)) {
                return true;
            }
        }
        return false;
    }


}
