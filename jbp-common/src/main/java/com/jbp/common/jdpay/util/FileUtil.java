package com.jbp.common.jdpay.util;

import com.jbp.common.kqbill.utils.FileLoader;

import java.io.*;

public class FileUtil {

    public FileUtil() {
    }

    public static byte[] readFile(String filename) {
        InputStream fileStream = FileLoader.getCertFileStream(filename);
        return FileLoader.inputStream2byte(fileStream);

    }
}
