package com.hkhj4.utily;

import java.util.Random;

public class CodeUtils {
    /**
     * 生成指定长度的数字验证码
     * @param length 验证码长度
     * @return 随机验证码
     */
    public static String generateNumericCode(int length) {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }
}