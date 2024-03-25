package com.jbp.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

@Slf4j
public class SignUtil {
    private SignUtil() {}

    public static String getSign(String app_secret, Map<String, Object> params) {
        try {
            String sortedStr = getSortedParamStr(params);
            String paraStr = app_secret + sortedStr;

            return createSign(paraStr);
        } catch (Exception e) {
            log.warn("getSign UnsupportedEncodingException ", e);
        }

        return StringUtils.EMPTY;
    }

    /**
     * 构造自然排序请求参数
     *
     * @param params 请求
     * @return 字符串
     */
    private static String getSortedParamStr(Map<String, Object> params) throws UnsupportedEncodingException {
        Set<String> sortedParams = new TreeSet<>(params.keySet());

        StringBuilder strB = new StringBuilder();
        // 排除sign和空值参数
        for (String key : sortedParams) {
            if ("sign".equalsIgnoreCase(key)) {
                continue;
            }

            String value = MapUtils.getString(params, key) ;

            if (StringUtils.isNotEmpty(value)) {
                strB.append(key).append(value);
            }
        }
        return strB.toString();
    }

    /**
     * 生成新sign
     *
     * @param str 字符串
     * @return String
     */
    private static String createSign(String str) {
        if (str == null || str.length() == 0) {
            return null;
        }
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(str.getBytes("UTF-8"));

            byte[] md = mdTemp.digest();
            int j = md.length;
            char[] buf = new char[j * 2];
            int k = 0;
            int i = 0;
            while (i < j) {
                byte byte0 = md[i];
                buf[k++] = hexDigits[byte0 >>> 4 & 0xf];
                buf[k++] = hexDigits[byte0 & 0xf];
                i++;
            }
            return new String(buf);
        } catch (Exception e) {
            log.warn("create sign was failed", e);
            return null;
        }
    }


    public static String getSignToUpperCase(String app_secret, Map<String, Object> params) {
        try {
            String sortedStr = getSortedParamStr(params);
            String paraStr = app_secret + sortedStr;

            return md5ToUpperCase(paraStr);
        } catch (Exception e) {
            log.warn("getSign UnsupportedEncodingException ", e);
        }

        return StringUtils.EMPTY;
    }

    public static String md5ToUpperCase(String data) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] md5 = md.digest(data.getBytes(StandardCharsets.UTF_8));

            // 将处理后的字节转成 16 进制，得到最终 32 个字符
            StringBuilder sb = new StringBuilder();
            for (byte b : md5) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString().toUpperCase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}