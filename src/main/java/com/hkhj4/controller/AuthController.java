package com.hkhj4.controller;

import com.hkhj4.utily.ImageCodeUtils;
import com.hkhj4.utily.Result;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
    @GetMapping("/captcha")
    public Result getCaptcha(HttpSession session) {
        String code = ImageCodeUtils.generateCode();
        session.setAttribute("captchaCode", code);
        return Result.success(code);
    }
}
