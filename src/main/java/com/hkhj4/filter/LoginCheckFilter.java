package com.hkhj4.filter;

import com.hkhj4.utily.JwtsUtil;
import com.hkhj4.utily.Result;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;


@Slf4j
@Component
public class LoginCheckFilter extends OncePerRequestFilter {
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final JwtsUtil jwtsUtil;

    public LoginCheckFilter(ObjectMapper objectMapper, JwtsUtil jwtsUtil, StringRedisTemplate stringRedisTemplate) {
        this.objectMapper = objectMapper;
        this.jwtsUtil = jwtsUtil;
        this.redisTemplate = stringRedisTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {

        String url = request.getRequestURI();
        log.info("url:{}", url);

        // 放行接口
        if (url.contains("/login")
                || url.contains("/createUser")
                || url.contains("/changePassword")
                || url.contains("/captcha")
                || url.contains("/doc")
                || url.contains("/v3/api-docs")
                || url.contains("/swagger")
                || url.contains("/webjars")
                || url.contains("/favicon")
        ) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = request.getHeader("token");
        if (!StringUtils.hasLength(token)) {
            writeNotLogin(response);
            return;
        }
        try {
            // ✅ JWT校验
            jwtsUtil.parseJwt(token);
            // ✅ Redis校验登录状态
            String redisKey = "login:token:" + token;
            Object user = redisTemplate.opsForValue().get(redisKey);
            if (user == null) {
                writeNotLogin(response);
                return;
            }
            // ⭐⭐⭐ 自动续签核心
            redisTemplate.expire(redisKey, 7, TimeUnit.DAYS);
        } catch (Exception e) {
            log.error("token解析失败: {}", e.getMessage());
            writeNotLogin(response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void writeNotLogin(HttpServletResponse response) throws IOException {

        response.setContentType("application/json;charset=UTF-8");

        Result error = Result.error(-1, "未登录！");
        response.getWriter().write(objectMapper.writeValueAsString(error));
    }
}