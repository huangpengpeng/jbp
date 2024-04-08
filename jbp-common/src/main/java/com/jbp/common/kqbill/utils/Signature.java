package com.jbp.common.kqbill.utils;

import com.bill99.crypto.utils.FileLoader;
import com.jbp.common.kqbill.contants.Bill99ConfigInfo;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 * 快钱加密
 */
public class Signature {

    /**
     * 加密
     */
    public static String signMsg(String signMsg) {
        String base64 = "";
        try {
            KeyStore ks = KeyStore.getInstance("PKCS12");

            InputStream certFileStream = FileLoader.getCertFileStream(Bill99ConfigInfo.DE_PRI_PATH);
            BufferedInputStream ksbufin = new BufferedInputStream(certFileStream);
            char[] keyPwd = Bill99ConfigInfo.DE_PRI_PWD.toCharArray();
            ks.load(ksbufin, keyPwd);
            PrivateKey priK = (PrivateKey) ks.getKey(Bill99ConfigInfo.DE_PRI_NAME, keyPwd);
            java.security.Signature signature = java.security.Signature.getInstance("SHA256withRSA");
            signature.initSign(priK);
            signature.update(signMsg.getBytes("utf-8"));
            sun.misc.BASE64Encoder encoder = new sun.misc.BASE64Encoder();
            base64 = encoder.encode(signature.sign());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return base64;
    }


    /**
     * 解密
     */
    public static boolean enCodeByCer(String val, String msg) {
        boolean flag = false;
        try {
//            String file = Signature.class.getResource("CFCA_sandbox.cer").toURI().getPath();

            String file = "/Users/franky/work/jbp/jbp-common/src/main/java/com/jbp/common/kqbill/utils/CFCA_sandbox.cer";
            System.out.println(file);
            FileInputStream inStream = new FileInputStream(file);
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) cf.generateCertificate(inStream);
            PublicKey pk = cert.getPublicKey();
            java.security.Signature signature = java.security.Signature.getInstance("SHA256withRSA");
            signature.initVerify(pk);
            signature.update(val.getBytes());
            sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();
            System.out.println(new String(decoder.decodeBuffer(msg)));
            flag = signature.verify(decoder.decodeBuffer(msg));
            System.out.println(flag);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("no");
        }
        return flag;
    }




}
