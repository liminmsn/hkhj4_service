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
        //用户邮箱
        String token = request.getHeader("token");
        String redisKey = "login:token:" + token;
        String email = stringRedisTemplate.opsForValue().get(redisKey);
        tb.setUserEmail(email);
        premiumMapper.createTradeNo(tb);

        return Result.success(data, "获取支付数据成功！");
    }

    //查询订阅时长信息
    @GetMapping("/api/premium/get_member")
    Result getMember(HttpServletRequest request) {
        //用户邮箱
        String token = request.getHeader("token");
        String redisKey = "login:token:" + token;
        String email = stringRedisTemplate.opsForValue().get(redisKey);
        TbMember tbMember = premiumMapper.getMember(email);
        if (tbMember != null) {
            if (tbMember.getExpireTime().isAfter(LocalDateTime.now())) {
                return Result.success(tbMember);
            }
            return Result.error("订阅已经过期");
        }
        return Result.error("没有查到相关数据");
    }

    @GetMapping("/api/premium/spay_notify_url")
    public String SpayNotify(@Parameter SpayNotifyUrl spayNotifyUrl) {
        log.info("notify_url:{}", spayNotifyUrl);
        String outTradeNo = spayNotifyUrl.getOut_trade_no();
        // 1 查订单
        TbTradeNo tradeNo = premiumMapper.getTradeNo(outTradeNo);
        if (tradeNo == null) {
            log.error("订单不存在: {}", outTradeNo);
            return "fail";
        }
        //3 验签
        Map<String, String> params = spay.getParams(spayNotifyUrl);
        if (!spay.verifySign(params)) {
            log.error("验签失败");
            return "fail";
        }
        log.info("验签成功");

        // 4 防重复回调
        if (tradeNo.getPayState() == 1) {
            return "success";
        }
        // 3 判断支付状态
        if ("TRADE_SUCCESS".equals(spayNotifyUrl.getTrade_status())) {
            //价格列表item
            TbPremium tbPremium = premiumMapper.getPremium(tradeNo.getPremiumId());
            //会员订阅剩余时间
            TbMember tbMember_old = premiumMapper.getMember(tradeNo.getUserEmail());

            if (tbMember_old == null) {
                TbMember tbMember = new TbMember();
                tbMember.setEmail(tradeNo.getUserEmail());
                tbMember.setPremiumType(tradeNo.getPremiumId());
                tbMember.setExpireTime(LocalDateTime.now().plusDays(tbPremium.getDay()));
                //创建tbMember
                premiumMapper.createMember(tbMember);
            } else {
                LocalDateTime expire_time = tbMember_old.getExpireTime();
                if (expire_time.isAfter(LocalDateTime.now())) {
                    tbMember_old.setExpireTime(expire_time.plusDays(tbPremium.getDay()));
                } else {
                    tbMember_old.setExpireTime(LocalDateTime.now().plusDays(tbPremium.getDay()));
                }
                //叠加tbMember
                premiumMapper.updateMember(tbMember_old.getEmail(), tbMember_old.getExpireTime());
            }
            int i = premiumMapper.updateTradeNo(1, tradeNo.getTradeNo());
            log.info("订单完成: {}", i);
        }
        return "success";
    }
}
