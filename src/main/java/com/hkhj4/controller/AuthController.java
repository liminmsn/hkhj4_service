package com.hkhj4.controller;

import com.hkhj4.utily.CodeUtils;
import com.hkhj4.utily.EmailService;
import com.hkhj4.utily.IpUtils;
import com.hkhj4.utily.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.redis.core.StringRedisTemplate;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
@Tag(name = "验证鉴权")
public class AuthController {
    @Resource
    private EmailService emailService;
    @Resource(name = "stringRedisTemplate")
    private StringRedisTemplate redisTemplate;

    /**
     * 发送邮箱验证码接口
     *
     * @param email 收件人邮箱
     * @return 响应结果
     */
    @GetMapping("/captcha")
    public Result getCaptcha(HttpServletRequest request, @RequestParam String email) {
        String ip = IpUtils.getIp(request);
        String limitKey = "captcha_limit" + ip;
        Boolean exists = redisTemplate.hasKey(limitKey);
        if (Boolean.TRUE.equals(exists)) {
            return Result.error(404, "请求过于频繁，请稍后再试");
        } else {
            try {
                redisTemplate.opsForValue().set(limitKey, "1", 60, TimeUnit.SECONDS);
                String code = CodeUtils.generateNumericCode(6);
                String subject = "验证码:" + code;
                String content = "你好！你的验证码是：" + code + "，有效期5分钟，请妥善保管，切勿泄露给他人。";
                emailService.sendVerificationCode(email, subject, content);

                redisTemplate.opsForValue().set(email, code, 5, TimeUnit.MINUTES);
                return Result.success(200, "验证码已发送至你的邮箱，请查收,5分钟内有效！");
            } catch (Exception e) {
                return Result.error("发送失败：" + e.getMessage());
            }
        }
    }
}
