package com.hkhj4.controller;

import com.hkhj4.entity.TbPremium;
import com.hkhj4.mapper.PremiumMapper;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PremiumController {
    @Resource(name = "premiumMapper")
    PremiumMapper premiumMapper;

    //获取订阅价格列表
    @GetMapping("/premium_list")
    List<TbPremium> getPremiumList(){
        return premiumMapper.list();
    }
}
