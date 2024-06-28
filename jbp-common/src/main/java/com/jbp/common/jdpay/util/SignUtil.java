package com.jbp.common.jdpay.util;

import com.jbp.common.jdpay.sdk.JdPayConstant;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.BaseNCodec;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class SignUtil {
    private static final String[] hexStrings;

    static {
        hexStrings = new String[256];
        for (int i = 0; i < 256; i++) {
            StringBuilder d = new StringBuilder(2);
            char ch = Character.forDigit(((byte) i >> 4) & 0x0F, 16);
            d.append(Character.toUpperCase(ch));
            ch = Character.forDigit((byte) i & 0x0F, 16);
            d.append(Character.toUpperCase(ch));
            hexStrings[i] = d.toString();
        }
    }

    /**
     * 计算签名
     *
     * @param map       有key和value的map，使用=和&拼接所有参数，
     *                  "sign_type", "sign_data", "encrypt_type", "encrypt_data"不参加计算
     * @param algorithm 签名算法 MD5, SHA-1, SHA-256
     * @param salt      签名密钥
     * @param charset   字符串编码
     * @return 签名
     */
    public static String sign(Map<String, String> map, String algorithm, String salt, String charset) throws UnsupportedEncodingException {
        String linkString = map2LinkString(map);
        String data = linkString + salt;
        return digestHex(algorithm, data, charset);
    }

    /**
     * 验证签名正确性。
     *
     * @param sign      签名数据
     * @param map       数据
     * @param algorithm 签名算法 MD5, SHA-1, SHA-256
     * @param salt      签名密钥
     * @param charset   字符串
     * @return 验证结果
     */
    public static boolean verify(String sign,
                                 Map<String, String> map,
                                 String algorithm,
                                 String salt,
                                 String charset) throws UnsupportedEncodingException {
        if (sign == null || "".equals(sign.trim()) || map.size() == 0) {
            return false;
        }
        String newSign = sign(map, algorithm, salt, charset);
        return newSign.equals(sign);
    }

    /**
     * 验证页面回调
     *
     * @param respMap 页面回调参数
     * @param signKey signKey
     * @return 验证结果
     * @throws NoSuchAlgorithmException
     */
    public static boolean verifyPageCallBackSign(Map<String, String> respMap, String signKey) throws UnsupportedEncodingException {
        String sign = respMap.remove("sign");
        String newSign = sign(respMap, JdPayConstant.SHA256, signKey, JdPayConstant.UTF8);
        return newSign.equals(sign);
    }

    /**
     * 将MAP数据用=和&拼接成String
     *
     * @param map 数据
     * @return 字符串
     */
    public static String map2LinkString(Map<String, String> map) {
        ArrayList<String> mapKeys = new ArrayList<String>(map.keySet());
        Collections.sort(mapKeys);
        StringBuilder link = new StringBuilder(2048);
        for (String key : mapKeys) {
            String value = map.get(key);
            // 属性为空不参与签名
            if (value == null || "".equals(value.trim())) {
                continue;
            }
            link.append(key).append("=").append(value).append("&");
        }
        // 删除末尾的&
        link.deleteCharAt(link.length() - 1);
        return link.toString();
    }

    /**
     * 对数据进行指定算法的数据摘要
     *
     * @param algorithm 算法名，如MD2, MD5, SHA-1, SHA-256, SHA-512
     * @param data      待计算的数据
     * @param charset   字符串的编码
     * @return 摘要结果
     */
    public static String digestHex(String algorithm, String data, String charset) throws UnsupportedEncodingException {
        byte[] digest = DigestUtils.getDigest(algorithm).digest(data.getBytes(charset));
        return hexString(digest);
    }

    /**
     * 将字节数组转换成HEX String
     *
     * @param b
     * @return HEX String
     */
    public static String hexString(byte[] b) {
        StringBuilder d = new StringBuilder(b.length * 2);
        for (byte aB : b) {
            d.append(hexStrings[(int) aB & 0xFF]);
        }
        return d.toString();
    }

    /**
     * 对数据进行BASE64解码
     *
     * @param base64Data Base64数据
     * @param charset    解码的编码格式
     * @param urlSafe    是否是URL安全的，如果为true，则将会被URL编码的'+', '/'转成'-', '_'
     * @param oneLine    是否是一行
     * @return 解码后数据
     */
    public static String decodeBase64(String base64Data, String charset, boolean urlSafe, boolean oneLine) throws UnsupportedEncodingException {
        Base64 base64 = oneLine ? new Base64(BaseNCodec.MIME_CHUNK_SIZE, null, urlSafe) : new Base64(urlSafe);
        byte[] binaryData = base64.decode(base64Data);
        return new String(binaryData, charset);
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("tradeAmount", "10");
        map.put("tradeStatus", "FINI");
        map.put("outTradeNo", "20210112133105PAY_SIGN");
        map.put("currency", "CNY");
        map.put("finishDate", "20210112133105");
        map.put("sign", "F9327EF0C7D20D721FA3694189CFD36C923361A55AF6649B1A81397B8402A933");
        boolean verify = verifyPageCallBackSign(map, "12d2633744ddeca9a127a862e5d67e46ed14e52dd272d74a1c3d25eab0970f04");
        System.out.println("verify = " + verify);


    }
}
