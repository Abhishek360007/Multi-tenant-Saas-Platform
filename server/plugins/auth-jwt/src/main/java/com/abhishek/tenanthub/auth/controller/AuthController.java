package com.abhishek.tenanthub.auth.controller;

import cn.hutool.crypto.CryptoException;
import com.abhishek.tenanthub.auth.jwt.JwtLoginUser;
import com.abhishek.tenanthub.auth.jwt.JwtSecurityUtils;
import com.abhishek.tenanthub.auth.jwt.JwtTokenValue;
import com.abhishek.tenanthub.auth.po.LoginFormPO;
import com.abhishek.tenanthub.auth.service.JwtTokenService;
import com.abhishek.tenanthub.auth.service.LineCaptchaService;
import com.abhishek.tenanthub.auth.service.RsaAsymmetricCryptoService;
import com.abhishek.tenanthub.auth.service.UserLoginService;
import com.abhishek.tenanthub.common.util.JsonResult;
import com.abhishek.tenanthub.common.util.ServletUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.Objects;

import static com.abhishek.tenanthub.common.consts.JwtConst.*;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


/**
 * <p>
 * User authentication controller
 * </p>
 *
 * @author Wang Chen Chen <932560435@qq.com>
 * @version 3.0
 */


@Slf4j
@Tag(name = "[ Auth ] User authentication")
@RestController
@AllArgsConstructor
public class AuthController {

    private final JwtTokenService jwtTokenService;

    private final UserLoginService loginService;

    private final LineCaptchaService captchaService;

    private final RsaAsymmetricCryptoService cryptoService;

    /**
     * TODO [json] User login
     *
     * @author WangChenChen
     * @date 2022/12/11 8:58
     */
    @Operation(summary = "[json] User login", description = "Sign in with a JSON request body")
    @PostMapping(value = LOGIN_URL, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public JsonResult<JwtTokenValue> login(@RequestBody @Validated LoginFormPO user,
                                           HttpServletRequest request, BindingResult br) {
        if (br.hasErrors()) {
            String err = Objects.requireNonNull(br.getFieldError()).getDefaultMessage();
            return JsonResult.fail(err, JwtTokenValue.class);
        }
        return login(user, request);
    }


    /**
     * TODO [Form Data] User login
     *
     * @author WangChenChen
     * @date 2022/12/11 8:58
     */
    @Operation(summary = "[FormData] User login", description = "Sign in with form data")
    @PostMapping(value = LOGIN_URL, consumes = APPLICATION_FORM_URLENCODED_VALUE, produces = APPLICATION_FORM_URLENCODED_VALUE)
    public JsonResult<JwtTokenValue> login2(@Validated LoginFormPO user,
                                            HttpServletRequest request, BindingResult br) {
        if (br.hasErrors()) {
            String err = Objects.requireNonNull(br.getFieldError()).getDefaultMessage();
            return JsonResult.fail(err, JwtTokenValue.class);
        }
        return login(user, request);
    }


    public JsonResult<JwtTokenValue> login(LoginFormPO user, HttpServletRequest request) {
        try {
            var tokenValue = loginService.login(user, request);
            return JsonResult.success("Signed in", tokenValue);
        } catch (RuntimeException failed) {
            String msg = switch (failed) {
                case UsernameNotFoundException ignored1 -> String.format("Username %s does not exist", user.getUsername());
                case BadCredentialsException ignored2 -> String.format("Incorrect password for %s", user.getUsername());
                case DisabledException ignored3 -> String.format("Username %s is disabled", user.getUsername());
                case LockedException ignored4 -> String.format("Username %s is locked", user.getUsername());
                case AccountExpiredException ignored5 -> String.format("Username %s has expired", user.getUsername());
                case CryptoException ignored6 -> "Username and password must be encrypted with the public key";
                default -> String.format(failed.getMessage());
            };
            return JsonResult.fail(msg, JwtTokenValue.class);
        }
    }


    /**
     * TODO Sign out
     *
     * @author WangChenChen
     * @date 2022/12/11 8:58
     */
    @Operation(summary = "Sign out", description = "Sign out")
    @PostMapping(LOGOUT_URL)
    public JsonResult<String> logout() {
        jwtTokenService.logout();
        return JsonResult.success();
    }


    /**
     * TODO Get the signed-in user
     *
     * @author WangChenChen
     * @date 2022/12/11 8:58
     */
    @Operation(summary = "Signed-in user", description = "Get the signed-in user")
    @GetMapping(LOGIN_USER_URL)
    public JsonResult<JwtLoginUser> loginUser() {
        var loginUser = JwtSecurityUtils.getLoginUser();
        loginUser.setAuthorities(null);
        return JsonResult.success(loginUser);
    }


    /**
     * TODO Refresh token
     *
     * @author WangChenChen
     * @date 2022/12/11 8:58
     */
    @Operation(summary = "Refresh token", description = "Refresh token")
    @GetMapping(REFRESH_URL)
    public JsonResult<JwtTokenValue> refresh() {
        var refresh = jwtTokenService.refresh();
        return JsonResult.success(refresh);
    }


    /**
     * TODO Get captcha
     *
     * @author WangChenChen
     * @date 2022/12/11 8:58
     */
    @Operation(summary = "Get captcha", description = "Get a captcha image")
    @GetMapping(CAPTCHA_CODES_URL)
    public void imageVerifyCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
        var codeKey = request.getParameter("codeKey");
        if (StringUtils.isBlank(codeKey) || StringUtils.length(codeKey) < 16) {
            ServletUtils.renderError(response, JsonResult.fail("The codeKey parameter is required and must be at least 16 characters"));
            return;
        }
        // Set the response type to an image
        response.setContentType("image/png");
        // Disable image caching.
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        var image = captchaService.random(codeKey);
        // Get an image captcha
        ImageIO.write(image, "PNG", response.getOutputStream());
    }


    /**
     * TODO Get public key
     *
     * @author WangChenChen
     * @date 2024/06/18 15:39
     */
    @Operation(summary = "Get public key", description = "Get the public key")
    @GetMapping(PUBLIC_KEY_URL)
    public JsonResult<String> publicKey() {
        return JsonResult.success("ok", cryptoService.getRSA().getPublicKeyBase64());
    }


}
