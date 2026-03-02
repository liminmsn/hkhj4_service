package com.hkhj4.controller;

import com.hkhj4.entity.TbUser;
import com.hkhj4.entity.TbUserRegister;
import com.hkhj4.mapper.UserMapper;
import com.hkhj4.utily.JwtsUtil;
import com.hkhj4.utily.Result;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * 用户管理控制器
 * 负责用户注册、查询等接口的实现
 */
@Slf4j
@RestController
@Tag(name = "用户管理接口")
public class UserController {
    @Resource(name = "stringRedisTemplate")
    StringRedisTemplate redisTemplate;
    @Resource
    UserMapper userMapper;
    @Resource
    JwtsUtil jwtsUtil;


    @PostMapping("/login")
    Result Login(String email, String password) {
        int count_email = userMapper.countEmail(email);
        if (count_email == 0) {
            return Result.error(200, "邮箱未注册!");
        } else {
            TbUser user = userMapper.userLogin(email, password);
            if (user == null) {
                return Result.error(403, "邮箱或者密码错误!");
            } else {
                //登录成功
                HashMap<String, Object> map = new HashMap<>();
                map.put("email", user.getEmail());
                map.put("id", user.getPassword());
                String token = jwtsUtil.generateJwt(map);
                // Redis key
                // token有效期 = JWT一致
                String redisKey = "login:token:" + token;
                redisTemplate.opsForValue().set(redisKey, user.getEmail(), 7, TimeUnit.DAYS);
                return Result.success(token);
            }
        }
    }

    @PostMapping("/createUser")
    Result createUser(TbUserRegister tbUser) {
        String email = tbUser.getEmail();
        String code = tbUser.getCode();

        String redisCode = redisTemplate.opsForValue().get(email);
        if (redisCode == null) {
            return Result.error(500, "验证码已过期");
        }
        if (!redisCode.equalsIgnoreCase(code)) {
            return Result.error(500, "验证码错误");
        }

        int count = userMapper.countEmail(email);
        if (count == 0) {
            userMapper.createUser(tbUser);
            //删除redis存的验证码
            redisTemplate.delete(email);
            return Result.success(200,"注册成功！");
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
