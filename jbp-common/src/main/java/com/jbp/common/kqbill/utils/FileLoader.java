package com.jbp.common.kqbill.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * 文件加载工具类
 */
public class FileLoader {
    private static final Logger logger= LoggerFactory.getLogger(FileLoader.class);

    /**
     * 读取证书文件流
     * @param certPath
     * @return
     */
    public static InputStream getCertFileStream(String certPath){
        try {
            if(certPath.startsWith("classpath:")){
                String path = certPath.substring( "classpath:".length());
                return FileLoader.class.getClassLoader().getResourceAsStream(path);
            }else{
                return new FileInputStream(new File(certPath));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
