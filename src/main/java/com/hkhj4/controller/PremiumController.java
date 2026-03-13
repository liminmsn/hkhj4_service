package com.hkhj4.controller;

import com.hkhj4.entity.SpayRes;
import com.hkhj4.entity.TbTradeNo;
import com.hkhj4.mapper.PremiumMapper;
import com.hkhj4.pay.Spay;
import com.hkhj4.utily.Fetch;
import com.hkhj4.utily.JwtsUtil;
import com.hkhj4.utily.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@Tag(name = "订阅管理")
public class PremiumController {
    @Resource
    StringRedisTemplate stringRedisTemplate;
    @Resource
    Spay spay;
    @Resource
    PremiumMapper premiumMapper;

    //获取订阅价格列表
    @GetMapping("/api/premium/premium_list")
    Result getPremiumList() {
        return Result.success(premiumMapper.list(), "订阅列表查询成功!");
    }

    //获取支付二维码
    @PostMapping("/api/premium/spay")
    Result getSpay(HttpServletRequest request, @RequestParam Integer premiumId, @RequestParam String name, @RequestParam String money, @RequestParam String type) throws Exception {
        Map<String, String> sign = spay.initPay(name, money, type);
        var data = new Fetch(Spay.targetUrl).post(sign).then(SpayRes.class);

        //存订单信息
        var tb = new TbTradeNo();
        tb.setPremiumId(premiumId);
        tb.setTradeNo(data.getTrade_no());
        tb.setPayType(type);
        //用户邮箱
        String token = request.getHeader("token");
        String redisKey = "login:token:" + token;
        String email = stringRedisTemplate.opsForValue().get(redisKey);
        tb.setUserEmail(email);
        log.info("{} yyyy", email);
        premiumMapper.createTradeNo(tb);


        return Result.success(data, "获取支付数据成功！");
    }
}
