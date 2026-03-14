package com.hkhj4.controller;

import com.hkhj4.entity.*;
import com.hkhj4.mapper.PremiumMapper;
import com.hkhj4.utily.Spay;
import com.hkhj4.utily.Fetch;
import com.hkhj4.utily.Result;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

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
    Result getSpay(HttpServletRequest request, @RequestParam Integer premium_id, @RequestParam String name, @RequestParam String money, @RequestParam String type) throws Exception {
        Map<String, String> sign = spay.initPay(name, money, type);
        var data = new Fetch(Spay.targetUrl).post(sign).then(SpayRes.class);
        log.info("sign:{}", sign.get("sign"));

        //存订单信息
        var tb = new TbTradeNo();
        tb.setPremiumId(premium_id);
        tb.setTradeNo(data.getTrade_no());
        tb.setPayType(type);
        tb.setSign(sign.get("sign"));
        //用户邮箱
        String token = request.getHeader("token");
        String redisKey = "login:token:" + token;
        String email = stringRedisTemplate.opsForValue().get(redisKey);
        tb.setUserEmail(email);
        premiumMapper.createTradeNo(tb);

        return Result.success(data, "获取支付数据成功！");
    }

    @GetMapping("/api/premium/spay_notify_url")
    String SpayNotify(@Parameter SpayNotifyUrl spayNotifyUrl) {
        log.info("notify_url:{}", spayNotifyUrl);
        String trade_no = spayNotifyUrl.getTrade_no();
        String sign = spayNotifyUrl.getSign();

        //匹配现有订单列表
        TbTradeNo tradeNo = premiumMapper.getTradeNo(trade_no, sign);
        if (tradeNo != null) {
            TbPremium tbPremium = premiumMapper.getPremium(tradeNo.getPremiumId());
            //支付成功
            if (Objects.equals(spayNotifyUrl.getTrade_status(), "TRADE_SUCCESS")) {
                TbMember tbMember_old = premiumMapper.getMembe(tradeNo.getUserEmail());
                if (tbMember_old != null) {
                    LocalDateTime expire_time = tbMember_old.getExpireTime();
                    //没过期叠加
                    if (expire_time.isAfter(LocalDateTime.now())) {
                        tbMember_old.setExpireTime(expire_time.plusDays(tbPremium.getDay()));
                    } else {
                        tbMember_old.setExpireTime(LocalDateTime.now().plusDays(tbPremium.getDay()));
                    }
                    premiumMapper.updateMember(tbMember_old.getEmail(), tbMember_old.getExpireTime());
                } else {
                    //没有记录添加
                    TbMember tbMember = new TbMember();
                    tbMember.setEmail(tradeNo.getUserEmail());
                    tbMember.setPremiumType(tradeNo.getPremiumId());
                    tbMember.setExpireTime(LocalDateTime.now().plusDays(tbPremium.getDay()));
                    premiumMapper.createMember(tbMember);
                }


                log.info("tradeNo:{}", tradeNo);
            }
        } else {
            log.error("没有找到");
        }
        return "success";
    }
}
