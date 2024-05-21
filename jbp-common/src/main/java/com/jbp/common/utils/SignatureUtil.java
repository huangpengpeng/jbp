package com.jbp.common.utils;

import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;

public class SignatureUtil {

    public final static String FIELD_SIGN ="sign";
    public static final Random RANDOM = new Random();


    /**
     * 生成 MD5
     *
     * @param data 待处理数据
     *
     * @return MD5结果
     */
    private static String MD5(String data) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] array = md.digest(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte item : array) {
            sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
        }
        return sb.toString().toUpperCase();
    }

    public static String HMACSHA256(String secret, String message) throws Exception {
        String hash = "";
        Mac hmacSha256 = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
        hmacSha256.init(secret_key);
        byte[] bytes = hmacSha256.doFinal(message.getBytes());
        hash = byteArrayToHexString(bytes);
        return hash;
    }

    /**
     * 将加密后的字节数组转换成字符串
     *
     * @param b 字节数组
     * @return 字符串
     */
    public  static String byteArrayToHexString(byte[] b) {
        StringBuilder hs = new StringBuilder();
        String stmp;
        for (int n = 0; b!=null && n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0XFF);
            if (stmp.length() == 1)
                hs.append('0');
            hs.append(stmp);
        }
        return hs.toString().toLowerCase();
    }


    /**
     * 生成签名. 注意，若含有sign_type字段，必须和signType参数保持一致。
     *
     * @param data     待签名数据
     * @param key      API密钥
     * @param signType 签名方式
     * @return 签名
     */
    public static String generateSignature(final Map<String, String> data, String key, SignType signType) throws Exception {
        Set<String> keySet = data.keySet();
        String[] keyArray = keySet.toArray(new String[keySet.size()]);
        Arrays.sort(keyArray); // 排序
        StringBuilder sb = new StringBuilder();
        for (String k : keyArray) {
            if (k.equals(FIELD_SIGN)) {
                continue;
            }
            if (data.get(k).trim().length() > 0) // 参数值为空，则不参与签名
                sb.append(k).append("=").append(data.get(k).trim()).append("&");
        }
        sb.append("key=").append(key);
        if (SignType.MD5.isEquals(signType)) {
            return MD5(sb.toString()).toUpperCase();
        } else if (SignType.HMACSHA256.isEquals(signType)) {
            return HMACSHA256(sb.toString(), key);
        } else {
            throw new Exception(String.format("Invalid sign_type: %s", signType));
        }
    }

    /**
     * 判断签名是否正确，必须包含sign字段，否则返回false。
     *
     * @param data     Map类型数据
     * @param key      API密钥
     * @param signType 签名方式
     * @return 签名是否正确
     * @throws Exception
     */
    public static boolean isSignatureValid(Map<String, String> data, String key, SignType signType) throws Exception {
        if (!data.containsKey(FIELD_SIGN)) {
            return false;
        }
        String sign = data.get(FIELD_SIGN);
        return generateSignature(data, key, signType).equals(sign);
    }



    /**
     * 获取随机字符串 Nonce Str
     *
     * @return String 随机字符串
     */
    private static final String SYMBOLS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static String generateNonceStr() {
        char[] nonceChars = new char[8];
        for (int index = 0; index < nonceChars.length; ++index) {
            nonceChars[index] = SYMBOLS.charAt(RANDOM.nextInt(SYMBOLS.length()));
        }
        return new String(nonceChars);
    }




    /**
     * 入参对象转Map
     * @param obj
     * @return
     */
    public static Map<String, String> convertObjectToMap(Object obj) {
        return Arrays.stream(BeanUtils.getPropertyDescriptors(obj.getClass()))
            .filter(pd -> !"class".equals(pd.getName()))
            .collect(HashMap::new,
                (map, pd) -> {
                    if (ReflectionUtils.invokeMethod(pd.getReadMethod(), obj) != null) {
                        map.put(pd.getName(), String.valueOf(ReflectionUtils.invokeMethod(pd.getReadMethod(), obj)));
                    }
                },
                HashMap::putAll);
    }

    
}
