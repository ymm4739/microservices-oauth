package com.ymm.microservices.oauth.feign;

import com.baomidou.mybatisplus.extension.api.R;
import com.ymm.microservices.oauth.config.ApiResult;
import com.ymm.microservices.oauth.feign.fallback.UserCenterAuthFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-center", fallback = UserCenterAuthFallback.class)
public interface UserCenterAuth {

    @GetMapping("/user-center/auth")
    R auth(@RequestParam("username") String username);
}
