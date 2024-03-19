package com.jbp.common.kqbill.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GzipUtil {

    private static final Logger logger = LoggerFactory.getLogger(GzipUtil.class);

    /**
     * gzip压缩
     * @param data 待压缩数据
     * @return 压缩后的数据
     */
    public static String compress(String data) {
        if (StringUtils.isBlank(data)) {
            return "";
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip;
        try {
            gzip = new GZIPOutputStream(out);
            gzip.write(data.getBytes(StandardCharsets.UTF_8));
            gzip.close();
        } catch (IOException e) {
            logger.error("gzip compress err:", e);
        }
        return Base64.getEncoder().encodeToString(out.toByteArray());
    }

    /**
     * gzip 解压
     * @param data 待解压数据
     * @return 解压后的数据
     * @throws UnsupportedEncodingException 不支持的字符集
     */
    public static String uncompress(String data) throws UnsupportedEncodingException {
        if (StringUtils.isBlank(data)) {
            return "";
        }
        byte[] decode = Base64.getMimeDecoder().decode(data);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(decode);
        GZIPInputStream gzipStream = null;
        try {
            gzipStream = new GZIPInputStream(in);
            byte[] buffer = new byte[256];
            int n;
            while ((n = gzipStream.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }
        } catch (Exception e) {
            logger.error("gzip uncompress err:", e);
        } finally {
            try {
                out.close();
                if (gzipStream != null) {
                    gzipStream.close();
                }
            } catch (Exception e) {
                logger.error("gzip uncompress err:", e);
            }
        }
        return out.toString("utf-8");
    }

}
