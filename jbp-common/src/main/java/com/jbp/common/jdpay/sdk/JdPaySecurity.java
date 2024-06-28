package com.jbp.common.jdpay.sdk;



import org.apache.xmlbeans.impl.util.Base64;

import java.io.InputStream;

public class JdPaySecurity {

    public String signEnvelop(InputStream signCert, String password, InputStream envelopCert, byte[] orgData) {
        byte[] signData = JdPaySign.getInstance().attachSign(signCert, password, orgData);
        byte[] envelop = JdPaySign.getInstance().encryptEnvelop(envelopCert, signData);
        return new String(Base64.encode(envelop));
    }

    public byte[] verifyEnvelop(InputStream envelopCert, String password, byte[] envelopData) {
        byte[] signData = JdPaySign.getInstance().decryptEnvelop(envelopCert, password, envelopData);
        return JdPaySign.getInstance().verifyAttachSign(signData);
    }
}
