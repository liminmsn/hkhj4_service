package com.hkhj4.controller;

import com.hkhj4.mapper.PremiumMapper;
import com.hkhj4.pay.Spay;
import com.hkhj4.utily.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Tag(name = "订阅管理")
public class PremiumController {
    @Resource
    Spay spay;
    @Resource
    PremiumMapper premiumMapper;

    //获取订阅价格列表
    @GetMapping("/api/premium/premium_list")
    Result getPremiumList(){
        return Result.success(premiumMapper.list());
    }
    //获取支付二维码
    @PostMapping("/api/premium/spay")
    Result getSpay(String name,String money,String type) throws Exception {
        Map<String, String> sign = spay.initPay(name,money,type);
        return spay.sendPostFormData(sign);
    }
}
