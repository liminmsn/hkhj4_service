package com.hkhj4.controller;

import com.hkhj4.entity.TbUser;
import com.hkhj4.mapper.UserMapper;
import com.hkhj4.utily.JwtsUtil;
import com.hkhj4.utily.Result;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * 用户管理控制器
 * 负责用户注册、查询等接口的实现
 */
@Slf4j
@RestController
public class UserController {
    @Resource
    RedisTemplate<String, Object> redisTemplate;
    @Resource
    UserMapper userMapper;
    @Resource
    JwtsUtil jwtsUtil;

    @PostMapping("/login")
    Result Login(String email, String password, String captchaKey, String captchaCode) {

        Object redisCode = redisTemplate.opsForValue().get(captchaKey);
        if (redisCode == null) {
            return Result.error(500, "验证码已过期");
        }
        //对比输入
        if (!redisCode.toString().equalsIgnoreCase(captchaCode)) {
            return Result.error(500, "验证码错误");
        }

        int count_email = userMapper.countEmail(email);
        if (count_email == 0) {
            return Result.error(400, "邮箱未注册!");
        } else {
            TbUser user = userMapper.userLogin(email, password);
            if (user == null) {
                return Result.error(400, "邮箱或者密码错误!");
            } else {
                //删除redis存的验证码
                redisTemplate.delete(captchaKey);

                //登录成功
                HashMap<String, Object> map = new HashMap<>();
                map.put("email", user.getEmail());
                map.put("id", user.getPassword());
                String token = jwtsUtil.generateJwt(map);
                // Redis key
                String redisKey = "login:token:" + token;
                // token有效期 = JWT一致
                redisTemplate.opsForValue().set(redisKey, user.getEmail(), 30, TimeUnit.MINUTES);
                return Result.success(token);
            }
        }
    }

    @PostMapping("api/user/createUser")
    Result createUser(@Parameter(description = "用户信息，必填字段：username/password/email") @RequestBody TbUser tbUser) {
        int count = userMapper.countEmail(tbUser.getEmail());
        if (count == 0) {
            userMapper.createUser(tbUser);
            return Result.success("注册成功！");
        }
        return Result.error(400, "邮箱已经注册！");
    }

    @GetMapping("api/user/info")
    Result userInfo(@RequestHeader("token") String token) {
        try {
            Claims claims = jwtsUtil.parseJwt(token);
            String email = claims.get("email").toString();
            TbUser user = userMapper.getUserInfo(email);
            return Result.success(user);
        } catch (Exception e) {
            log.info(e.getMessage());
            return Result.error(401, "token无效或已过期");
        }
    }
}
