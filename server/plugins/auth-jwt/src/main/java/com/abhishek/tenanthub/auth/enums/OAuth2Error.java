package com.abhishek.tenanthub.auth.enums;

import lombok.Getter;

/**
 * <p>
 * Authentication status
 * </p>
 *
 * @author Wang Chen Chen
 * @version 1.0.1
 * @date 2021/7/5 9:31
 */

@Getter
public enum OAuth2Error {

    /**
     * Authentication error, unknown cause
     */
    OAUTH2_EXCEPTION(400004, "Authentication error!"),

    /**
     * access_token does not exist
     */
    ACCESS_TOKEN_INVALID(400010, "access_token does not exist"),


    /**
     * access_token Expired
     */
    ACCESS_TOKEN_EXPIRED(400011, "access_token Expired"),


    /**
     * token Invalid format
     */
    TOKEN_FORMAT_ERROR(400012, "access_token Invalid format"),


    /**
     * The user must change their password before calling anything else
     */
    PASSWORD_CHANGE_REQUIRED(400020, "Password change required. Please set a new password to continue."),


    /**
     * refresh_token does not exist
     */
    REFRESH_TOKEN_INVALID(400014, "refresh_token does not exist"),


    /**
     * refresh_token Expired
     */
    REFRESH_TOKEN_EXPIRED(400015, "refresh_token has expired"),


    /**
     * This service requires a non-client-credentials grant
     */
    TOKEN_USER_INVALID(400016, "This service requires a non-client-credentials grant"),

    /**
     * User does not exist
     */
    USER_INVALID(400020, "User does not exist"),

    /**
     * Incorrect password
     */
    USER_PASSWORD_ERROR(400021, "Incorrect password"),

    /**
     * This user is disabled
     */
    USER_DISABLE(400022, "This user is disabled"),

    /**
     * This user is locked
     */
    USER_LOCKING(400023, "This user is locked"),

    /**
     * Mobile number not found
     */
    USER_MOBILE_INVALID(400024, "Mobile number not found"),

    /**
     * Invalid user type
     */
    USER_TYPES_ERROR(400025, "Invalid user type"),

    /**
     * Client does not exist
     */
    CLIENT_INVALID(400030, "Client does not exist"),

    /**
     * Client is disabled
     */
    CLIENT_DISABLE(400031, "Client is disabled"),

    /**
     * Incorrect client secret
     */
    CLIENT_SECRET_ERROR(400032, "Incorrect client secret"),

    /**
     * Invalid grant type
     */
    AUTHORIZATION_GRANT_TYPE(400033, "Invalid grant type"),

    /**
     * In the authorization code flow, the code exchanged for an access_token does not exist.
     */
    CODE_INVALID(400034, "code does not exist"),

    /**
     * In the authorization code flow, the callback domain does not match the stored domain
     */
    DOMAIN_NAME_ILLEGAL(400035, "Callback domain does not match the configured domain"),

    /**
     * Client mismatch
     */
    CLIENT_IS_DIFFERENT(400036, "Client mismatch"),

    /**
     * Could not parse request parameters
     */
    REQUEST_PARAM_VALIDATE(400044, "Could not parse request parameters"),

    /**
     * Incorrect captcha
     */
    VERIFICATION_CODE_ERROR(400045, "Incorrect captcha"),

    /**
     * Access denied
     */
    ACCESS_DENIED(400061, "Request denied: insufficient permissions"),


    /**
     * Tenant ID does not exist
     */
    TENANT_ID_DOES_NOT_EXIST(400444, "Tenant ID does not exist"),


    /**
     * A tenant is required ID
     */
    REQUEST_MUST_ADD_TENANT_ID(400445, "Tenant ID does not exist"),


    /**
     * This platform user has no access to this tenant ID
     */
    NO_HAVE_TENANT_PERMISSIONS(400446, "This user has no access to this tenant ID"),


    /**
     * This platform user has no access to this project ID
     */
    NO_HAVE_PROJECT_PERMISSIONS(400447, "This user has no permission for this project");


    OAuth2Error(Integer status, String error) {
        this.status = status;
        this.error = error;
    }

    /**
     * Status message
     */
    private final Integer status;

    /**
     * Error message
     */
    private final String error;

}
