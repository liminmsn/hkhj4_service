package com.hkhj4.filter;

import com.alibaba.fastjson.JSONObject;
import com.hkhj4.utily.JwtsUtil;
import com.hkhj4.utily.Result;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;

@Slf4j
@Component
@WebFilter(urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    @Resource(name = "jwtsUtil")
    JwtsUtil jwtsUtil;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String url = req.getRequestURL().toString();
        log.info("url:{}", url);

        if (url.contains("login") || url.contains("captcha")) {
            log.info("登录|获取图片验证码,放行");
            chain.doFilter(request, response);
            return;
        }

        String jwt = req.getHeader("token");
        if (!StringUtils.hasLength(jwt)) {
            log.info("请求头token为空,不放行");
            Result error = Result.error(-1, "not_login");
            String notLogin = JSONObject.toJSONString(error);
            res.getWriter().write(notLogin);
            return;
        }

        try {
            Claims claims = jwtsUtil.parseJwt(jwt);
            log.info("解析jwt成功!", claims.toString());
        } catch (Exception e) {
            e.printStackTrace();
            log.info("token解析失败，返回未登录信息");
            Result error = Result.error(-1, "not_login");
            String notLogin = JSONObject.toJSONString(error);
            res.getWriter().write(notLogin);
            return;
        }

        //登录成功
        chain.doFilter(request, response);
    }
}
