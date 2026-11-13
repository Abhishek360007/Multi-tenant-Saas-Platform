package com.abhishek.tenanthub.common.util;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * jackson Json Utility class
 * </p>
 *
 * @author Wang Chen Chen
 * @version 1.0.1
 * @date 2021/7/5 9:31
 */


@Slf4j
public class JsonUtils {

    private static final ObjectMapper MAPPER;

    private static final JavaTimeModule JAVA_8_TIME_MODULE;

    static {
        MAPPER = new ObjectMapper();
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DatePattern.NORM_DATETIME_FORMATTER));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DatePattern.NORM_DATETIME_FORMATTER));
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DatePattern.NORM_DATE_FORMATTER));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DatePattern.NORM_DATE_FORMATTER));
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DatePattern.NORM_TIME_FORMATTER));
        javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DatePattern.NORM_TIME_FORMATTER));
        MAPPER.registerModule(javaTimeModule);
        JAVA_8_TIME_MODULE = javaTimeModule;
/*
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        simpleModule.addSerializer(long.class, ToStringSerializer.instance);
        MAPPER.registerModule(simpleModule);
*/

        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static JavaTimeModule getJavaTimeModule() {
        return JAVA_8_TIME_MODULE;
    }

    public static ObjectMapper getMapper() {
        return MAPPER;
    }

    /**
     * Convert an object to a JSON string.
     *
     * @param data
     * @return java.util.List<T>
     * @author Wang Chen Chen <932560435@qq.com>
     * @date 2019/4/18 15:26
     */
    public static String toJson(Object data) {
        try {
            return MAPPER.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
        return StringUtils.EMPTY;
    }


    /**
     * Convert an object to a formatted JSON string.
     *
     * @param data
     * @return java.util.List<T>
     * @author Wang Chen Chen <932560435@qq.com>
     * @date 2019/4/18 15:26
     */
    public static String toFormatJson(Object data) {
        try {
            var prettyPrinter = new DefaultPrettyPrinter();
            prettyPrinter.indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE);
            return MAPPER.writer().with(prettyPrinter).writeValueAsString(data);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
        return StringUtils.EMPTY;
    }


    /**
     * Check whether the string is json
     *
     * @param jsonStr
     * @return boolean
     * @author Wang Chen Chen <932560435@qq.com>
     * @date 2023/11/03 15:26
     */
    public static boolean isJsonValid(String jsonStr) {
        var stringObjectMap = toMap(jsonStr, String.class, Object.class);
        return stringObjectMap != null && !stringObjectMap.isEmpty();
    }


    /**
     * Check whether the string is json
     *
     * @param jsonBytes
     * @return boolean
     * @author Wang Chen Chen <932560435@qq.com>
     * @date 2023/11/03 15:26
     */
    public static boolean isJsonValid(byte[] jsonBytes) {
        var stringObjectMap = toMap(jsonBytes, String.class, Object.class);
        return stringObjectMap != null && !stringObjectMap.isEmpty();
    }


    /**
     * Convert an object to bytes
     *
     * @param data
     * @return java.util.List<T>
     * @author Wang Chen Chen <932560435@qq.com>
     * @date 2019/4/18 15:26
     */
    public static byte[] toBytes(Object data) {
        try {
            return MAPPER.writeValueAsBytes(data);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
        return new byte[0];
    }


    /**
     * Convert a JSON result into an object
     *
     * @param jsonData
     * @param beanType
     * @return java.util.List<T>
     * @author Wang Chen Chen <932560435@qq.com>
     * @date 2019/4/18 15:26
     */
    public static <T> T toPojo(String jsonStr, Class<T> beanType) {
        try {
            return MAPPER.readValue(jsonStr, beanType);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }


    /**
     * Convert a JSON result into an object
     *
     * @param src
     * @param beanType
     * @return java.util.List<T>
     * @author Wang Chen Chen <932560435@qq.com>
     * @date 2019/4/18 15:26
     */
    public static <T> T toPojo(byte[] jsonBytes, Class<T> beanType) {
        try {
            return MAPPER.readValue(jsonBytes, beanType);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }


    /**
     * Convert JSON into a POJO list
     *
     * @param jsonStr
     * @param beanType
     * @return java.util.List<T>
     * @author Wang Chen Chen <932560435@qq.com>
     * @date 2019/4/18 15:26
     */
    public static <T> List<T> toListPojo(String jsonStr, Class<T> beanType) {
        try {
            return MAPPER.readValue(jsonStr, MAPPER.getTypeFactory().constructCollectionType(ArrayList.class, beanType));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return new ArrayList<>();
    }


    /**
     * Convert JSON into Map
     *
     * @param jsonBytes
     * @param keyType
     * @param valueType
     * @return java.util.List<T>
     * @author Wang Chen Chen <932560435@qq.com>
     * @date 2019/4/18 15:26
     */
    public static <K, V> Map<K, V> toMap(byte[] jsonBytes, Class<K> keyType, Class<V> valueType) {
        try {
            return MAPPER.readValue(jsonBytes, MAPPER.getTypeFactory().constructMapType(HashMap.class, keyType, valueType));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return new HashMap<>();
    }


    /**
     * Convert JSON into Map
     *
     * @param jsonStr
     * @param keyType
     * @param valueType
     * @return java.util.List<T>
     * @author Wang Chen Chen <932560435@qq.com>
     * @date 2019/4/18 15:26
     */
    public static <K, V> Map<K, V> toMap(String jsonStr, Class<K> keyType, Class<V> valueType) {
        try {
            return MAPPER.readValue(jsonStr, MAPPER.getTypeFactory().constructMapType(HashMap.class, keyType, valueType));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return new HashMap<>();
    }


    /**
     * Convert JSON into a POJO list
     *
     * @param jsonStr
     * @param keyType
     * @param valueType
     * @return java.util.List<T>
     * @author Wang Chen Chen <932560435@qq.com>
     * @date 2019/4/18 15:26
     */
    public static <K, V> List<Map<K, V>> toListMap(String jsonStr, Class<K> keyType, Class<V> valueType) {
        try {
            return MAPPER.readValue(
                    jsonStr,
                    MAPPER.getTypeFactory().constructCollectionType(List.class,
                            MAPPER.getTypeFactory().constructMapType(HashMap.class, keyType, valueType)
                    )
            );
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return new ArrayList<>();
    }


}
