package com.jbp.common.lianlian.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

/**
 * 连连账户+签名
 */
public class LLianPayAccpSignature {

    private static Logger log = LoggerFactory.getLogger(LLianPayAccpSignature.class);
    private static LLianPayAccpSignature instance;

    private LLianPayAccpSignature() {

    }

    public static LLianPayAccpSignature getInstance() {
        if (null == instance)
            return new LLianPayAccpSignature();
        return instance;
    }

    /**
     * 签名处理
     *
     * @param prikeyvalue ：私钥
     * @param sign_str    ：签名源内容
     * @return
     */
    public String sign(String prikeyvalue, String sign_str) {
        try {
            String hash = Md5Algorithm.getInstance().md5Digest(sign_str.getBytes(StandardCharsets.UTF_8));
            log.info(String.format("签名处理中，签名源内容：%s，对应MD5值：%s", sign_str, hash));
            return RSASign.getInstance().sign(prikeyvalue, hash);
        } catch (Exception e) {
            log.error("签名失败,{}", e.getMessage());
        }
        return null;
    }

    /**
     * 签名验证
     *
     * @param pubkeyvalue ：公钥
     * @param sign_str    ：源串
     * @param signed_str  ：签名结果串
     * @return
     */
    public boolean checkSign(String pubkeyvalue, String sign_str, String signed_str) {
        try {
            String hash = Md5Algorithm.getInstance().md5Digest(sign_str.getBytes(StandardCharsets.UTF_8));
            log.info(String.format("签名验证中，源串：%s，对应MD5值：%s", sign_str, hash));
            return RSASign.getInstance().checksign(pubkeyvalue, hash, signed_str);
        } catch (Exception e) {
            log.error("签名验证异常,{}", e.getMessage());
        }
        return false;
    }



}