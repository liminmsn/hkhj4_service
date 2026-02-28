package com.hkhj4.controller;

import com.hkhj4.utily.ImageCodeUtils;
import com.hkhj4.utily.IpUtils;
import com.hkhj4.utily.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.redis.core.StringRedisTemplate;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
@Tag(name = "验证鉴权")
public class AuthController {
    @Resource(name = "stringRedisTemplate")
    private StringRedisTemplate redisTemplate;

    @GetMapping("/captcha")
    public Result getCaptcha(HttpServletRequest request) {
        String ip = IpUtils.getIp(request);
        String limitKey = "captcha_limit" + ip;
        // ✅ 是否频繁获取
        Boolean exists = redisTemplate.hasKey(limitKey);
        if (Boolean.TRUE.equals(exists)) {
            return Result.error(404, "请求过于频繁，请稍后再试");
        }
        // ===== 生成验证码 =====
        String code = ImageCodeUtils.generateCode();
//        session.setAttribute("captchaCode", code);
        String key = "captcha:" + UUID.randomUUID();
        // 保存验证码
        redisTemplate.opsForValue().set(key, code, 5, TimeUnit.MINUTES);
        // 限流 key（60秒）
        redisTemplate.opsForValue().set(limitKey, "1", 60, TimeUnit.SECONDS);
        return Result.result(200, "5分钟内有效！", Map.of("captchaKey", key, "captchaCode", code));
    }
}
