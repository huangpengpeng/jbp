package com.jbp.common.encryptapi;

import org.springframework.core.MethodParameter;

public class NeedCrypto {

    private NeedCrypto() {
    }

    /**
     * 是否需要对结果加密
     * 1.类上标注或者方法上标注,并且都为true
     * 2.有一个标注为false就不需要加密
     */
    static boolean needEncrypt(MethodParameter returnType) {
        boolean encrypt = false;
        boolean classPresentAnno = returnType.getContainingClass().isAnnotationPresent(EncryptResponse.class);
        boolean methodPresentAnno = returnType.getMethod().isAnnotationPresent(EncryptResponse.class);

        if (classPresentAnno) {
            //类上标注的是否需要加密
            encrypt = returnType.getContainingClass().getAnnotation(EncryptResponse.class).value();
            //类不加密，所有都不加密
            if (!encrypt) {
                return false;
            }
        }

        if (methodPresentAnno) {
            //方法上标注的是否需要加密
            encrypt = returnType.getMethod().getAnnotation(EncryptResponse.class).value();
        }
        return encrypt;
    }
}



