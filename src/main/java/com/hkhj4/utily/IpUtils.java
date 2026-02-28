package com.hkhj4.utily;

import jakarta.servlet.http.HttpServletRequest;

public class IpUtils {

    public static String getIp(HttpServletRequest request) {

        String ip = request.getHeader("X-Forwarded-For");

        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }

        return ip;
    }
}