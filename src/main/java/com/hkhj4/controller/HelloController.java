package com.hkhj4.controller;

import com.hkhj4.entity.TbPremium;
import com.hkhj4.mapper.UserMapper;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class HelloController {
    @Resource(name = "userMapper")
    private UserMapper userMapper;
    @GetMapping("/hello")
    public List<TbPremium> hello() {
        return userMapper.list();
    }
}