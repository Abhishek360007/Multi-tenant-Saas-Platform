package com.abhishek.tenanthub.auth.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.ToString;

/**
 * <p>
 * code : Authorization code flow: step 1
 * authorization_code : Authorization code flow
 * password : Password grant
 * client_credentials : Client credentials grant
 * refresh_token : Refresh token
 * we_chat : WeChat sign-in
 * tencent_qq : Tencent QQ sign-in
 * sms : SMS grant
 * </p>
 *
 * @author Wang Chen Chen
 * @version 1.0.1
 * @date 2021/7/5 9:31
 */

@Getter
@ToString
public enum GrantType {

    CODE("code", "Authorization code flow, 1. get code"),

    AUTHORIZATION_CODE("authorization_code", "Authorization code flow, 2. exchange the code for access_token"),

    PASSWORD("password", "Password grant"),

    CLIENT_CREDENTIALS("client_credentials", "Client credentials grant"),

    SEND_SMS("send_sms", "Send SMS"),

    SMS("sms", "SMS grant");

    GrantType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    @JsonValue
    private final String code;

    private final String description;

    @JsonCreator
    public static GrantType get(String value) {
        for (var v : values()) {
            if (v.code.equalsIgnoreCase(value)) {
                return v;
            }
        }
        return CODE;
    }

}
