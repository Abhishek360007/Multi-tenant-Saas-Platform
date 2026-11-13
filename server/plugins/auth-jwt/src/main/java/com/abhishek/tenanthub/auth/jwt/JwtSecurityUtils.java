package com.abhishek.tenanthub.auth.jwt;

import com.abhishek.tenanthub.auth.exception.JwtNoLoginException;
import com.abhishek.tenanthub.common.enums.AdminFlag;
import com.abhishek.tenanthub.common.enums.UserType;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * <p>
 * Security utilities
 * </p>
 *
 * @author Wang Chen Chen
 * @version 1.0
 * @date 2021/7/5 10:50
 */

public class JwtSecurityUtils {

    /**
     * Get Authentication
     */
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }


    /**
     * Authenticated
     */
    public static boolean isAuthenticated() {
        var auth = getAuthentication();
        return auth != null && !(auth instanceof AnonymousAuthenticationToken) && auth.isAuthenticated();
    }


    /**
     * Whether the signed-in user belongs to the master platform
     */
    public static boolean isMasterUser() {
        return JwtSecurityUtils.isAuthenticated() &&
                JwtSecurityUtils.getLoginUser().getUserType() == UserType.SYSTEM;
    }


    /**
     * Whether the signed-in user is an admin
     */
    public static boolean isAdminUser() {
        return JwtSecurityUtils.isAuthenticated() &&
                JwtSecurityUtils.getLoginUser().getAdminFlag() == AdminFlag.YES;
    }


    /**
     * Get the tenant of the signed-in user ID
     **/
    public static String getTenantId() {
        return getLoginUser().getTenantId();
    }


    /**
     * Get the unique login of the user ID
     **/
    public static String getLoginId() {
        return getLoginUser().getLoginId();
    }


    /**
     * Get the user account
     **/
    public static Long getUserId() {
        return getLoginUser().getUserId();
    }


    /**
     * Get the user account
     **/
    public static String getUsername() {
        return getLoginUser().getUsername();
    }


    /**
     * Get the user
     **/
    public static JwtLoginUser getLoginUser() {
        if (isAuthenticated()) {
            return (JwtLoginUser) getAuthentication().getPrincipal();
        } else {
            throw new JwtNoLoginException("No user is signed in", new RuntimeException());
        }
    }


    /**
     * Set the user
     **/
    public static void setLoginUser(JwtLoginUser loginUser) {
        if (loginUser == null) {
            SecurityContextHolder.clearContext();
        } else {
            var authentication = new UsernamePasswordAuthenticationToken(
                    loginUser, null, loginUser.getAuthorities());
            // Put the user into the SecurityContext so the current user can be read anywhere with the statement below
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }


    private final static PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    public static PasswordEncoder getPasswordEncoder() {
        return PASSWORD_ENCODER;
    }

    /**
     * Hash a password with BCryptPasswordEncoder
     *
     * @param password Password
     * @return String to hash
     */
    public static String encryptPassword(String password) {
        return PASSWORD_ENCODER.encode(password);
    }

    /**
     * Check whether the passwords match
     *
     * @param rawPassword     Plain password
     * @param encodedPassword Hashed value
     * @return Result
     */
    public static boolean matchesPassword(String rawPassword, String encodedPassword) {
        return PASSWORD_ENCODER.matches(rawPassword, encodedPassword);
    }

}
