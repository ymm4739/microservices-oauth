package com.ymm.microservices.oauth.handler;

import com.ymm.microservices.oauth.config.ApiResult;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(OAuth2Exception.class)
    public ApiResult handleOAuth2Exception(OAuth2Exception e) {
        if (e instanceof InvalidGrantException) {
            return new ApiResult(400, "用户名或密码错误", null);
        }
        return new ApiResult(400, "登陆错误", null);
    }
}
