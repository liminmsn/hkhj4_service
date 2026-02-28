package com.hkhj4.controller;

import com.hkhj4.mapper.PremiumMapper;
import com.hkhj4.utily.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PremiumController {
    @Resource
    PremiumMapper premiumMapper;

    //获取订阅价格列表
    @GetMapping("/api/premium/premium_list")
    Result getPremiumList(){
        return Result.success(premiumMapper.list());
    }
}
