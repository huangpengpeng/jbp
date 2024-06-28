package com.jbp.common.jdpay.util;


import com.google.common.primitives.Primitives;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

/*************************************************
 *
 * Gson工具类，对Google的gson工具进行封装
 * (1)日期格式yyyyMMddHHmmss
 * (2)toJson时支持掩码
 * (3)toJson时支持跳过指定属性
 *************************************************/
public class GsonUtil {
    private static final String EMPTY_JSON = "{}";
    private static final String EMPTY_JSON_ARRAY = "[]";
    private static final String DEFAULT_DATE_PATTERN = "yyyyMMddHHmmss";
    private static Gson defaultGson;

    static {
        GsonBuilder builder = new GsonBuilder();
        builder.setDateFormat(DEFAULT_DATE_PATTERN);
        defaultGson = builder.create();
        defaultGson.toJson(null);
    }

    public static String toJson(Object target) {
        return toJson(target, null, null, null);
    }

    public static String toJson(Object target, Type targetType) {
        return toJson(target, targetType, null, null);
    }

    public static String toMaskJson(Object target) {
        return toJson(target, null, null, null);
    }

    public static String toMaskJson(Object target, List<String> excludeFields) {
        return toJson(target, null, null, excludeFields);
    }

    /**
     * 打印目标对象的json串
     *
     * @param target        目标对象
     * @param targetType    对象类型，可为null
     * @param datePattern   日期格式，可为null，若为null则按 DEFAULT_DATE_PATTERN格式输出
     * @param excludeFields 不打印的字段，可为null
     * @return 目标对象的标准json串
     */
    public static String toJson(Object target, Type targetType, String datePattern, final List<String> excludeFields) {
        if (target == null) {
            return EMPTY_JSON;
        }

        Gson gson = defaultGson;
        if (null != datePattern && !"".equals(datePattern)) {
            gson = getGson(datePattern);
        }
        if (isNotEmpty(excludeFields)) {
            gson = getStrategyGson(excludeFields);
        }

        String result = emptyResult(target);
        try {
            if (targetType == null) {
                targetType = target.getClass();
            }
            result = gson.toJson(target, targetType);
        } catch (Exception ignore) {
        }
        return result;
    }

    public static <T> T fromJson(String json, TypeToken<T> token) {
        return fromJson(json, token, null);
    }

    public static <T> T fromJson(String json, TypeToken<T> token, String datePattern) {
        return fromJson(json, token.getType(), datePattern);
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        Object object = fromJson(json, (Type) classOfT, null);
        return Primitives.wrap(classOfT).cast(object);
    }

    public static <T> T fromJson(String json, Class<T> classOfT, String datePattern) {
        Object object = fromJson(json, (Type) classOfT, datePattern);
        return Primitives.wrap(classOfT).cast(object);
    }

    /**
     * 将json串转化为目标对象
     *
     * @param json        json串
     * @param type        目标对象类型
     * @param datePattern json串中日期格式，若为null，则按两个标准日期尝试解析：DEFAULT_DATE_PATTERN和DEFAULT_DATE_PATTERN_1
     * @param <T>
     * @return 目标对象
     */
    public static <T> T fromJson(String json, Type type, String datePattern) {
        if (null == json || "".equals(json)) {
            return null;
        }

        if (null == datePattern || "".equals(datePattern)) {
            try {
                return defaultGson.fromJson(json, type);
            } catch (Exception ignore) {
            }
            return null;
        }

        try {
            Gson gson = getGson(datePattern);
            return gson.fromJson(json, type);
        } catch (Exception ignore) {
        }
        return null;
    }

    private static Gson getGson(String datePattern) {
        GsonBuilder builder = new GsonBuilder();
        builder.setDateFormat(datePattern);
        return builder.create();
    }

    private static Gson getStrategyGson(final List<String> excludeFields) {
        ExclusionStrategy myExclusionStrategy = new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes fa) {
                return excludeFields != null && excludeFields.contains(fa.getName());
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }

        };
        return new GsonBuilder().setExclusionStrategies(myExclusionStrategy).create();
    }

    private static boolean isNotEmpty(final List<String> fieldNames) {
        return fieldNames != null && !fieldNames.isEmpty();
    }

    private static String emptyResult(Object target) {
        if (target == null) {
            return EMPTY_JSON;
        }
        if (target instanceof Collection
                || target instanceof Iterator
                || target instanceof Enumeration
                || target.getClass().isArray()) {
            return EMPTY_JSON_ARRAY;
        }
        return EMPTY_JSON;
    }
}
