package com.hkhj4.utily;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
public class Fetch {
    private HttpURLConnection conn;
    private String boundary; // 仅POST表单时初始化
    private static final int CONNECT_TIMEOUT = 5000; // 连接超时5秒
    private static final int READ_TIMEOUT = 10000; // 读取超时10秒

    /**
     * 构造方法：初始化连接，仅设置基础参数
     */
    public Fetch(String url) throws IOException {
        URL urlObj = new URL(url);
        conn = (HttpURLConnection) urlObj.openConnection();
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        // 设置超时时间，避免卡死
        conn.setConnectTimeout(CONNECT_TIMEOUT);
        conn.setReadTimeout(READ_TIMEOUT);
    }

    /**
     * GET 请求（无需表单参数，清除多余请求头）
     */
    public Fetch get() throws ProtocolException {
        conn.setRequestMethod("GET");
        // GET 请求不需要 multipart/form-data 头，移除冗余配置
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        return this;
    }

    /**
     * POST 表单请求（multipart/form-data）
     */
    public Fetch post(Map<String, String> params) throws IOException {
        conn.setRequestMethod("POST");
        // 初始化 boundary（仅POST表单时使用）
        boundary = "----WebkitFormBoundary" + System.currentTimeMillis();
        // 修复拼写错误：mutipart → multipart
        conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        // 写入表单参数（移除finally中的disconnect）
        writeFormParams(params);
        return this;
    }

    /**
     * 私有方法：写入表单参数（替代原静态InitParams，避免参数冗余）
     */
    private void writeFormParams(Map<String, String> params) throws IOException {
        if (params == null || params.isEmpty()) {
            log.warn("POST表单参数为空，无需写入");
            return;
        }
        try (OutputStream out = conn.getOutputStream();
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8), true)) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                writer.println("--" + boundary);
                writer.println("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"");
                writer.println(); // 必须空行分隔头和内容
                writer.println(entry.getValue());
            }
            // 结束边界（关键：末尾必须加 --）
            writer.println("--" + boundary + "--");
            writer.flush();
        }
        // 移除finally中的conn.disconnect()！！！
    }

    /**
     * 核心方法：读取响应并转换为指定类型
     */
    public <T> T then(Class<T> clazz) throws IOException {
        if (clazz == null) {
            throw new IllegalArgumentException("目标类型Class不能为null");
        }
        // 读取响应内容
        String responseStr = readResponse();
        log.info("接口响应内容：{}", responseStr);

        // JSON转对象，兜底处理null
        T result = JsonUtils.jsonToObject(responseStr, clazz);
        if (result == null) {
            throw new IOException("JSON解析失败，无法转换为类型：" + clazz.getName() + "，响应内容：" + responseStr);
        }
        return result;
    }

    /**
     * 私有方法：读取响应（正常流+错误流）
     */
    private String readResponse() throws IOException {
        StringBuilder response = new StringBuilder();
        // 标记连接是否已关闭，避免重复关闭
        boolean isDisconnected = false;
        try {
            // 读取正常响应流（200状态码）
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }
        } catch (IOException e) {
            // 读取错误流（非200状态码）
            try (BufferedReader errorReader = new BufferedReader(
                    new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = errorReader.readLine()) != null) {
                    response.append(line);
                }
            } catch (IOException ex) {
                log.error("读取错误流失败", ex);
                throw ex;
            }
            log.error("请求失败，错误响应：{}", response);
            isDisconnected = true;
        } finally {
            // 最终统一关闭连接（仅未关闭时执行）
            if (!isDisconnected) {
                conn.disconnect();
            }
        }
        return response.toString();
    }
}