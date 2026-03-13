package com.hkhj4.pay;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

@Slf4j
@Component
public class Spay {
    public final static String targetUrl = "https://zpayz.cn/mapi.php";
    String pid = "2025040423043232";//商户id
    String outTradeNo = "";//不可重复，最多32位
    //    String type = "alipay";//alipay 微信支付：wxpay
    //    String name = "iPhone17苹果手机";
    //    String money = "1.00";
    String signType = "MD5";
    @Value("pay_key")
    String key = "f8848mCKqEGc51N5Fp69FZNyNQbtFPqp";//商户密钥

    /**
     * 生成唯一的商户单号
     * 规则：时间戳(13位) + 随机数(6位) = 19位（≤32位），保证唯一性
     *
     * @return 符合要求的商户单号
     */
    private String generateOutTradeNo() {
        // 1. 获取当前时间戳（13位数字）
        String timestamp = String.valueOf(System.currentTimeMillis());
        // 2. 生成6位随机数（000000-999999）
        Random random = new Random();
        String randomNum = String.format("%06d", random.nextInt(1000000));
        // 3. 拼接成唯一单号（总长度19位，满足≤32位要求）
        String tradeNo = timestamp + randomNum;
        // 安全校验：如果长度超过32位，截取前32位（防止极端情况）
        if (tradeNo.length() > 32) {
            tradeNo = tradeNo.substring(0, 32);
        }
        return tradeNo;
    }

    /**
     * 排序map
     */
    public static <K extends Comparable<? super K>, V> Map<K, V> sortByKey(Map<K, V> map) {
        Map<K, V> result = new LinkedHashMap<>();

        map.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEachOrdered(e -> result.put(e.getKey(), e.getValue()));
        return result;
    }


    public Map<String, String> initPay(String name, String money, String type) {
        outTradeNo = generateOutTradeNo();
        //参数存入 map
        Map<String, String> sign = new HashMap<>();
        sign.put("pid", pid);
        sign.put("type", type);
        sign.put("out_trade_no", outTradeNo);
        sign.put("name", name);
        sign.put("money", money);
        sign.put("notify_url", "null");
        sign = sortByKey(sign);
        StringBuilder signStr = new StringBuilder();
        for (Map.Entry<String, String> m : sign.entrySet()) {
            signStr.append(m.getKey()).append("=").append(m.getValue()).append("&");
        }
        signStr = new StringBuilder(signStr.substring(0, signStr.length() - 1));
        signStr.append(key);
        signStr = new StringBuilder(DigestUtils.md5DigestAsHex(signStr.toString().getBytes()));
        sign.put("sign", signStr.toString());
        sign.put("sign_type", signType);
        return sign;
    }
}
