package com.hkhj4.utily;

/**
 * HTTP状态码枚举类
 * 包含常用的HTTP状态码、描述信息，以及实用的辅助方法
 */
public enum HttpStatusCode {
    // 2xx 成功
    OK(200, "请求成功"),
    CREATED(201, "资源创建成功"),
    NO_CONTENT(204, "请求成功但无返回内容"),

    // 3xx 重定向
    MOVED_PERMANENTLY(301, "永久重定向"),
    FOUND(302, "临时重定向"),
    NOT_MODIFIED(304, "资源未修改，使用缓存"),

    // 4xx 客户端错误
    BAD_REQUEST(400, "请求参数格式错误"),
    UNAUTHORIZED(401, "未认证，需要登录"),
    FORBIDDEN(403, "权限不足，拒绝访问"),
    NOT_FOUND(404, "请求的资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不被允许"),
    TOO_MANY_REQUESTS(429, "请求频率超限"),

    // 5xx 服务端错误
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),
    BAD_GATEWAY(502, "网关错误"),
    SERVICE_UNAVAILABLE(503, "服务暂不可用");

    // 状态码数值
    private final int code;
    // 状态码描述
    private final String message;

    /**
     * 枚举构造方法
     * @param code 状态码
     * @param message 描述信息
     */
    HttpStatusCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    // 获取状态码
    public int getCode() {
        return code;
    }

    // 获取描述信息
    public String getMessage() {
        return message;
    }

    /**
     * 辅助方法：根据状态码数值获取枚举实例
     * @param code 状态码数值
     * @return 对应的枚举实例，无匹配时返回null
     */
    public static HttpStatusCode fromCode(int code) {
        for (HttpStatusCode status : values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        return null;
    }

    /**
     * 重写toString，方便日志输出
     */
    @Override
    public String toString() {
        return String.format("%d %s", code, message);
    }
}