package com.jbp.common.encryptapi;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.alibaba.fastjson.JSON;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

public class AESUtils {
    private static final String KEY_ALGORITHM = "AES";
    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String ENCODING = "UTF-8";

    public static byte[] encrypt(byte[] plainBytes, byte[] keyBytes, String IV) {
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            SecretKey secretKey = new SecretKeySpec(keyBytes, KEY_ALGORITHM);
            if (StringUtils.isNotBlank(IV)) {
                IvParameterSpec ips = new IvParameterSpec(IV.getBytes());
                cipher.init(1, secretKey, ips);
            } else {
                cipher.init(1, secretKey);
            }
            return cipher.doFinal(plainBytes);
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("加密失败");
        }
    }

    public static byte[] decrypt(byte[] encryptedBytes, byte[] keyBytes, String IV)
        throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
        InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        SecretKey secretKey = new SecretKeySpec(keyBytes, KEY_ALGORITHM);
        if (StringUtils.isNotBlank(IV)) {
            IvParameterSpec ips = new IvParameterSpec(IV.getBytes());
            cipher.init(2, secretKey, ips);
        } else {
            cipher.init(2, secretKey);
        }
        return cipher.doFinal(encryptedBytes);
    }


    public static String encrypt(String data, String key) {
        if (key.length() < 16) {
            throw new RuntimeException("Invalid AES key length (must be 16 bytes)");
        } else if (key.length() > 16) {
            key = key.substring(0, 16);
        }
        try {
            Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
            byte[] byteContent = data.getBytes(ENCODING);
            cipher.init(Cipher.ENCRYPT_MODE, genKey(key));
            byte[] result = cipher.doFinal(byteContent);
            return parseByte2HexStr(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    public static String decrypt(String data, String key) {
        if (key.length() < 16) {
            throw new RuntimeException("Invalid AES key length (must be 16 bytes)");
        } else if (key.length() > 16) {
            key = key.substring(0, 16);
        }
        try {
            byte[] decryptFrom = parseHexStr2Byte(data);
            Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, genKey(key));
            byte[] result = cipher.doFinal(decryptFrom);
            return new String(result, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private static SecretKeySpec genKey(String key) {
        SecretKeySpec secretKey;
        try {
            secretKey = new SecretKeySpec(key.getBytes(ENCODING), KEY_ALGORITHM);
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec seckey = new SecretKeySpec(enCodeFormat, KEY_ALGORITHM);
            return seckey;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("genKey fail!", e);
        }
    }

    private static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }


    private static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

    public static void main(String[] args) {
        String result = "{\"code\":0,\"data\":\"5AD1311C1B574E8A3F1FA72F90D118A6BB25841399394BD59BD5ADF4DB8A5C577ACE2168763D5208CE3CB795B36829FD9133776B0E03244E03CD2C096354963DB05112991CC62DCF519E3C77637DF924152EA39BFA3242774A6ECA525151356B6C2ADE9C2BF9969540BAA36ED168A617DC4D3F47DE3BE49B3FCFE1C30B493CFBA1F01C9BB63256EBCC921FECB79D58EF34EEA89FD434E92C07C13419D6C114706A74D8A59439E4A734A59203E28377D584B97873436204FDE6DBE28EA4897DCB807E84E92A1D6F289EC85A0C1B8489CECD94C5FDC15B87E6E9BB0EA03ACB4760A3788ABB1F70C808F1ECEF0F72C5EF6F364FA7B919A13111AEE8A34804AB015B9FABEC50600DAD5381DA380DC71F17E0A42A18013FD4CEC8098D4E1E625A2CBE2E363AE50D6FB535F34D7601277BDFCD0E86D93CCA2FA1F2802A83AD98FB9EFC133ADE3353A4F729559735F1ECC894BC6430B5623210DB42A83B404036C1ABEA19F61ECE59E5D078A20B9CFA36355AD0E06ABAC59210AD9A1D189D5419A3C82A00D3AF206FB7444E9F0848798C75E26A2783EB73254C1FF15577BD5649FBAE1EB9F4A6AA3221060D1FDAB999B9AE4700279D8F1F35B9D3B1088C1B664AA0D3D8F504473C42945CFEEFF6C4EB16AA8EDDF43CF5F958D3AC65AA968EE20081252C9913AB339F0D9A21D6EF5A7862E2E8FC9C7114E8E7926A0ED9F1980E9FF09434AA6D8BA25ED324E4B67CE04B009841693E374E4F842C331D2D5729751CA5B1B18D3E0371B79F3910620B6ADD632D44CF0EAE9B72E5EA39EB321AF54A4B0F072AC139278304DAA8535630D9360C1201CBAAF12ECF1F2307A03E20371A9240193DAAF772A840D5CBCE6417495CF787853C1F83F1B6B85AC838CB44E839245112EF2AD58A354D2ADFA96FAB34C84C59B37B8A6A2C14592E33074014D65B1AADAD7829E199EA4C5414719F9F918AD71C8DAA0232F604E0130D68D4B87A191CC81E06\"}";
        String data = JSON.parseObject(result).getString("data");
        String  decryptStr= decrypt(data, "F80A15DE30C37C73");
        System.out.println(decryptStr);

    }

    public static void main1(String[] args) throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException,
        InvalidAlgorithmParameterException, BadPaddingException, IllegalBlockSizeException,
        UnsupportedEncodingException {
        System.out.println("phone=" + Base64.encodeBase64String(AESUtils.encrypt("18081031393".getBytes(),
            "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDlAWPo".substring(0, 32).getBytes(), "3IPLa89}668@23)!")));
        System.out.println("uid=" + Base64.encodeBase64String(AESUtils
            .encrypt("1285935".getBytes(), "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDlAWPo".substring(0, 32).getBytes(),
                "3IPLa89}668@23)!")));
        System.out.println("username=" + Base64.encodeBase64String(AESUtils
            .encrypt("王马哥".getBytes(), "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDlAWPo".substring(0, 32).getBytes(),
                "3IPLa89}668@23)!")));
        System.out.println("xxxoptions=" + Base64.encodeBase64String(AESUtils
            .encrypt("{\"uid\":2429725,\"phone\":\"18106517933\",\"nickname\":\"ROSE34631655376833717\"}".getBytes(),
                "g-WoH*JclfSprDcp!FKP_tQugE%+7y6j".substring(0, 32).getBytes(), "OtqO6YTyJo=LnNvP")));
        System.out.println(new String(AESUtils.decrypt(Base64.decodeBase64(
            "/dO+FUSneowJeNkJRIeGbvp6r8m4RipvTnKeBjIXlFhrg/YP0PUFXSYMGtTG2FvJZ/NYgWfGWOLLLAflgZhfnDEG+8t9+oTE9aH1tUijk9Y="),
            "g-WoH*JclfSprDcp!FKP_tQugE%+7y6j".getBytes(), "OtqO6YTyJo=LnNvP"), "utf-8"));
    }


}


