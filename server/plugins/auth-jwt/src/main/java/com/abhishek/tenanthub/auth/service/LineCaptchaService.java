package com.abhishek.tenanthub.auth.service;

import java.awt.image.BufferedImage;

/**
 * <p>
 * Captcha
 * </p>
 *
 * @author Wang Chen Chen<932560435@qq.com>
 * @version 1.0
 * @createTime 2020/3/5 0005 11:28
 */

public interface LineCaptchaService {

    /**
     * Get a random image captcha
     *
     * @param codeKey
     * @return ImageVerifyCode
     * @author Wang Chen Chen<932560435@qq.com>
     * @createTime 2020/3/5 0005 11:31
     */
    BufferedImage random(String codeKey);

    /**
     * Delete the cached captcha value from Redis
     *
     * @param codeKey
     * @return ImageVerifyCode
     * @author Wang Chen Chen<932560435@qq.com>
     * @createTime 2020/3/5 0005 11:31
     */
    void delete(String codeKey);

    /**
     * Delete the cached captcha value from Redis
     *
     * @param codeKey
     * @param userCodeText
     * @return ImageVerifyCode
     * @author Wang Chen Chen<932560435@qq.com>
     * @createTime 2020/3/5 0005 11:31
     */
    boolean check(String codeKey, String userCodeText);

}
