package com.abhishek.tenanthub.common.consts;

public class LoginConst {

    /**
     * Captcha redis key
     */
    public static final String CAPTCHA_CODE_KEY = "captcha_codes:";

    /**
     * Signed-in user redis key
     */
    public static final String LOGIN_TOKEN_KEY = "login_tokens:";

    /**
     * Online user token prefix
     */
    public static final String ONLINE_USER_KEY = "online_user:";

    /**
     * Forced sign-out token prefix
     */
    public static final String FORCED_OFFLINE_KEY = "forced_offline_user:";

    /**
     * Duplicate submission token prefix
     */
    public static final String NO_REPEAT_SUBMIT = "no_repeat_submit";

}
