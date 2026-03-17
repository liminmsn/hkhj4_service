package com.hkhj4.utily;

import com.hkhj4.entity.SpayNotifyUrl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@Component
public class Spay {
    public final static String targetUrl = "https://zpayz.cn/mapi.php";
    String pid = "2025040423043232";//商户id
    String outTradeNo = "";//不可重复，最多32位
    //    String type = "alipay";//alipay 微信支付：wxpay
    //    String name = "iPhone17苹果手机";
    //    String money = "1.00";
//    String signType = "MD5";
    String key = "1xa6FEwJ7mc1iKxAdtvjGGTcYmo3j9NP";//商户密钥@Value("pay_notify_url")
    String notify_url = "http://8.148.250.179:8080/api/premium/spay_notify_url";

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

    public boolean verifySign(Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return false;
        }
        // 获取回调 sign
        String paySign = params.get("sign");
        // 复制参数，避免修改原map
        Map<String, String> signParams = new TreeMap<>(params);
        // 移除不参与签名的字段
        signParams.remove("sign");
        signParams.remove("sign_type");
        // 拼接字符串
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : signParams.entrySet()) {
            if (entry.getValue() == null || entry.getValue().isEmpty()) {
                continue;
            }
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        // 删除最后一个 &
        sb.deleteCharAt(sb.length() - 1);
        // 拼接 KEY
        sb.append(key);
        // MD5
        String localSign = DigestUtils.md5DigestAsHex(sb.toString().getBytes(StandardCharsets.UTF_8));
        log.info("本地sign: {}", localSign);
        log.info("回调sign: {}", paySign);
        return localSign.equalsIgnoreCase(paySign);
    }

    public Map<String, String> initPay(String name, String money, String type) {
        outTradeNo = generateOutTradeNo();
        Map<String, String> sign = new TreeMap<>();
        sign.put("pid", pid);
        sign.put("type", type);
        sign.put("out_trade_no", outTradeNo);
        sign.put("name", name);
        sign.put("money", money);
        sign.put("notify_url", notify_url);
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> m : sign.entrySet()) {
            sb.append(m.getKey()).append("=").append(m.getValue()).append("&");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(key);
        String signValue = DigestUtils.md5DigestAsHex(sb.toString().getBytes(StandardCharsets.UTF_8));

        sign.put("sign", signValue);
        sign.put("sign_type", "MD5");
        return sign;
    }

    public Map<String, String> getParams(SpayNotifyUrl spayNotifyUrl) {
        Map<String, String> params = new HashMap<>();
        params.put("pid", spayNotifyUrl.getPid());
        params.put("name", spayNotifyUrl.getName());
        params.put("money", spayNotifyUrl.getMoney());
        params.put("out_trade_no", spayNotifyUrl.getOut_trade_no());
        params.put("trade_no", spayNotifyUrl.getTrade_no());
        params.put("param", spayNotifyUrl.getParam());
        params.put("trade_status", spayNotifyUrl.getTrade_status());
        params.put("type", spayNotifyUrl.getType());
        params.put("sign", spayNotifyUrl.getSign());
        params.put("sign_type", spayNotifyUrl.getSign_type());
        return params;
    }
}
