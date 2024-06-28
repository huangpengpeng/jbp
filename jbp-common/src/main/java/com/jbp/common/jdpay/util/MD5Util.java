package com.jbp.common.jdpay.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {
    public static String getMD5Code(String str) throws NoSuchAlgorithmException {
        byte unencoded[] = null;
        try {
            unencoded = str.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        MessageDigest md5 = null;

        md5 = MessageDigest.getInstance("MD5");

        md5.update(unencoded);
        byte encoded[] = md5.digest();
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < encoded.length; i++) {
            if ((encoded[i] & 255) < 16) {
                buf.append("0");
            }
            buf.append(Long.toString(encoded[i] & 255, 16));
        }

        return buf.toString().toUpperCase();
    }
	/*public static void main(String args[]){
		System.out.println(MD5.getMD5Code("23232323ABCDS"));
	}*/

    public static String getMD5NoUpper(String str) throws NoSuchAlgorithmException {
        byte unencoded[] = null;
        try {
            unencoded = str.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        MessageDigest md5 = null;

        md5 = MessageDigest.getInstance("MD5");

        md5.update(unencoded);
        byte encoded[] = md5.digest();
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < encoded.length; i++) {
            if ((encoded[i] & 255) < 16) {
                buf.append("0");
            }
            buf.append(Long.toString(encoded[i] & 255, 16));
        }

        return buf.toString();
    }

    public static String md5(String text, String salt) {
        StringBuilder sb = new StringBuilder();

        try {
            byte[] e = (text + salt).getBytes();
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(e);
            e = messageDigest.digest();

            for (int i = 0; i < e.length; ++i) {
                if ((e[i] & 255) < 16) {
                    sb.append("0");
                }

                sb.append(Long.toString((long) (e[i] & 255), 16));
            }
        } catch (Exception var6) {
            throw new RuntimeException(var6);
        }

        return sb.toString().toLowerCase();
    }

    /**
     * MD5方法
     *
     * @param text 明文
     * @return 密文
     * @throws Exception
     */
    public static String md5(String text) throws Exception {
        byte[] bytes = (text).getBytes("UTF-8");
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        messageDigest.update(bytes);
        bytes = messageDigest.digest();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            if ((bytes[i] & 0xff) < 0x10) {
                sb.append("0");
            }
            sb.append(Long.toString(bytes[i] & 0xff, 16));
        }
        return sb.toString().toLowerCase();
    }

    /**
     * MD5验证方法
     *
     * @param text 明文
     * @param md5  密文
     * @return true/false
     * @throws Exception
     */
    public static boolean verify(String text, String md5) throws Exception {
        String md5Text = md5(text);
        if (md5Text.equalsIgnoreCase(md5)) {
            return true;
        } else {
            return false;
        }
    }

    public static String md5Lower32(String saltHead, String str, String saltTail) {
        String result = null;
        try {
            if (saltHead == null) {
                saltHead = "";
            }
            if (saltTail == null) {
                saltTail = "";
            }
            byte[] bytes = new byte[0];
            bytes = (saltHead + str + saltTail).getBytes("UTF-8");
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(bytes);
            bytes = messageDigest.digest();
            result = byte2HexUpperCase(bytes).toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static String byte2HexUpperCase(byte[] bts) {
        StringBuilder des = new StringBuilder();
        String tmp;
        for (byte bt : bts) {
            tmp = Integer.toHexString(bt & 255);
            if (tmp.length() == 1) {
                des.append("0");
            }
            des.append(tmp);
        }
        return des.toString().toUpperCase();
    }
}

