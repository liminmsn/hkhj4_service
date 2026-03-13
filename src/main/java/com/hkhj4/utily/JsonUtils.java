package com.hkhj4.utily;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;

/**
 * JSON 与 Java 实例互转工具类
 * 适配 HTTP 响应 JSON 解析场景，包含空值、日期、未知字段兼容处理
 */
public class JsonUtils {
    // 日志（适配你的项目日志框架，如slf4j）
    private static final Logger log = LoggerFactory.getLogger(JsonUtils.class);

    // 全局 ObjectMapper 实例（线程安全，复用提升性能）
    private static final ObjectMapper OBJECT_MAPPER;

    // 静态初始化：配置 ObjectMapper 全局规则
    static {
        OBJECT_MAPPER = new ObjectMapper();
        // 1. 忽略 JSON 中存在但 Java 实体类不存在的字段（避免解析时报错）
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 2. 允许空值转换（避免空字符串/Null 导致解析失败）
        OBJECT_MAPPER.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        // 3. 统一日期格式（根据你的业务调整，比如 yyyy-MM-dd HH:mm:ss）
        OBJECT_MAPPER.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        // 4. 其他常用配置（可选）
        // OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false); // 序列化空对象不报错
    }

    /**
     * 私有构造方法：禁止实例化工具类
     */
    private JsonUtils() {
        throw new UnsupportedOperationException("工具类禁止实例化");
    }

    /**
     * JSON 字符串转 Java 实例（通用方法）
     * @param jsonStr JSON 字符串（如你的 HTTP 响应 response.toString()）
     * @param clazz   目标实体类的 Class 对象
     * @param <T>     泛型，适配任意实体类
     * @return 解析后的 Java 实例，解析失败返回 null
     */
    public static <T> T jsonToObject(String jsonStr, Class<T> clazz) {
        // 空值校验
        if (jsonStr == null || jsonStr.isEmpty() || clazz == null) {
            log.warn("JSON 解析参数为空：jsonStr={}, clazz={}", jsonStr, clazz);
            return null;
        }

        try {
            // 核心解析逻辑
            return OBJECT_MAPPER.readValue(jsonStr, clazz);
        } catch (JsonProcessingException e) {
            log.error("JSON 转 Java 实例失败！JSON字符串：{}，目标类：{}，异常信息：{}",
                    jsonStr, clazz.getName(), e.getMessage(), e);
            return null;
        }
    }

    /**
     * 重载方法：JSON 字符串转复杂类型（如 List、Map）
     * 示例：jsonToComplexObject(jsonStr, new TypeReference<List<User>>() {});
     * @param jsonStr    JSON 字符串
     * @param typeReference 复杂类型引用（com.fasterxml.jackson.core.type.TypeReference）
     * @param <T>        泛型
     * @return 解析后的复杂类型实例
     */
    public static <T> T jsonToComplexObject(String jsonStr, com.fasterxml.jackson.core.type.TypeReference<T> typeReference) {
        if (jsonStr == null || jsonStr.isEmpty() || typeReference == null) {
            log.warn("JSON 解析复杂类型参数为空：jsonStr={}, typeReference={}", jsonStr, typeReference);
            return null;
        }

        try {
            return OBJECT_MAPPER.readValue(jsonStr, typeReference);
        } catch (JsonProcessingException e) {
            log.error("JSON 转复杂类型实例失败！JSON字符串：{}，类型引用：{}，异常信息：{}",
                    jsonStr, typeReference.getType(), e.getMessage(), e);
            return null;
        }
    }

    /**
     * 反向方法：Java 实例转 JSON 字符串（可选，方便调试/回写）
     * @param obj Java 实例
     * @return JSON 字符串，转换失败返回空字符串
     */
    public static String objectToJson(Object obj) {
        if (obj == null) {
            log.warn("Java 转 JSON 参数为空：obj=null");
            return "";
        }

        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("Java 实例转 JSON 失败！对象：{}，异常信息：{}", obj, e.getMessage(), e);
            return "";
        }
    }
}