package com.hkhj4.controller;

import com.hkhj4.entity.TbUser;
import com.hkhj4.mapper.UserMapper;
import com.hkhj4.utily.Result;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户管理控制器
 * 负责用户注册、查询等接口的实现
 */
@RestController
public class UserController {
    @Resource(name = "userMapper")
    UserMapper userMapper;

    @PostMapping("/login")
    Result Login(String email, String password) {
        int count_email = userMapper.countEmail(email);
        if (count_email == 0) {
            return Result.error(400, "邮箱未注册!");
        } else {
            int count = userMapper.login(email, password);
            if (count == 0) {
                return Result.error(400, "邮箱或者密码错误!");
            } else {
                return Result.success(200, "登录成功");
            }
        }
    }

    @PostMapping("/createUser")
    Result createUser(
            @Parameter(description = "用户信息，必填字段：username/password/email")
            @RequestBody TbUser tbUser) {
        int count = userMapper.countEmail(tbUser.getEmail());
        if (count == 0) {
            userMapper.createUser(tbUser);
            return Result.success("注册成功！");
        }
        return Result.error(400, "邮箱已经注册！");
    }
}
