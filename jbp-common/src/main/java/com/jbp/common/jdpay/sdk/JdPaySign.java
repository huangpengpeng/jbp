package com.jbp.common.jdpay.sdk;


import com.jbp.common.jdpay.exception.JdPayException;
import com.jbp.common.jdpay.util.CertUtil;
import org.bouncycastle.asn1.DEROutputStream;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.*;
import org.bouncycastle.cms.jcajce.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.util.Store;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.x500.X500PrivateCredential;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class JdPaySign {

    private static final Logger logger = LoggerFactory.getLogger("JdPaySign");

    private static final String SIGN_ALGORITHMS = "SHA1WITHRSA";
    private static final String BC = "BC";

    private static JdPaySign INSTANCE = null;

    static {
        BouncyCastleProvider provider = new BouncyCastleProvider();
        Security.addProvider(provider);
    }

    private JdPaySign() {
    }

    /**
     * 单例，双重校验
     */
    public static JdPaySign getInstance() {
        if (INSTANCE == null) {
            synchronized (JdPaySign.class) {
                if (INSTANCE == null) {
                    INSTANCE = new JdPaySign();
                }
            }
        }
        return INSTANCE;
    }


    public byte[] attachSign(InputStream priCert, String password, byte[] data) {
        return this.sign(priCert, password, data, true);
    }

    public byte[] detachSign(InputStream priCert, String password, byte[] data) {
        return this.sign(priCert, password, data, false);
    }

    private byte[] sign(InputStream priCert, String password, byte[] data, boolean isDetach) {
        try {
            X500PrivateCredential privateCert = CertUtil.getPrivateCert(priCert, password.toCharArray());
            X509Certificate x509Certificate = privateCert.getCertificate();
            PrivateKey privateKey = privateCert.getPrivateKey();

            List<X509Certificate> certList = new ArrayList<X509Certificate>();
            certList.add(x509Certificate);
            Store certs = new JcaCertStore(certList);

            CMSSignedDataGenerator generator = new CMSSignedDataGenerator();
            ContentSigner sha1Signer = new JcaContentSignerBuilder(SIGN_ALGORITHMS).setProvider(BC).build(privateKey);
            generator.addSignerInfoGenerator(new JcaSignerInfoGeneratorBuilder(new JcaDigestCalculatorProviderBuilder().setProvider(BC).build()).build(sha1Signer, x509Certificate));
            generator.addCertificates(certs);

            CMSTypedData msg = new CMSProcessableByteArray(data);
            return generator.generate(msg, isDetach).getEncoded();
        } catch (CertificateException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RuntimeException("签名异常");
        }
    }

    @SuppressWarnings("rawtypes")
    public int verifyDetachSign(byte[] data, byte[] signData) {
        try {
            CMSProcessable content = new CMSProcessableByteArray(data);
            CMSSignedData s = new CMSSignedData(content, signData);
            Store certStore = s.getCertificates();
            SignerInformationStore signers = s.getSignerInfos();
            Collection c = signers.getSigners();
            Iterator it = c.iterator();
            int verified = 0, size = 0;
            while (it.hasNext()) {
                size++;
                SignerInformation signer = (SignerInformation) it.next();
                Collection certCollection = certStore.getMatches(signer.getSID());
                Iterator certIt = certCollection.iterator();
                X509CertificateHolder cert = (X509CertificateHolder) certIt.next();
                if (signer.verify(new JcaSimpleSignerInfoVerifierBuilder().setProvider("BC").build(cert))) {
                    verified++;
                }
            }
            if (size == verified) {
                return 1;
            }
        } catch (Exception e) {
            return 0;
        }
        return 0;
    }

    @SuppressWarnings({"rawtypes"})
    public byte[] verifyAttachSign(byte[] signData) {
        try {
            byte[] data = null;
            CMSSignedData s = new CMSSignedData(signData);
            Store certStore = s.getCertificates();
            SignerInformationStore signers = s.getSignerInfos();
            Collection c = signers.getSigners();
            Iterator it = c.iterator();
            int verified = 0, size = 0;

            while (it.hasNext()) {
                size++;
                SignerInformation signer = (SignerInformation) it.next();
                Collection certCollection = certStore.getMatches(signer.getSID());
                Iterator certIt = certCollection.iterator();
                X509CertificateHolder cert = (X509CertificateHolder) certIt.next();

                if (signer.verify(new JcaSimpleSignerInfoVerifierBuilder().setProvider("BC").build(cert))) {
                    verified++;
                    CMSTypedData cmsData = s.getSignedContent();
                    data = (byte[]) cmsData.getContent();
                }
            }
            if (size == verified) {
                return data;
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public byte[] encryptEnvelop(InputStream pubCert, byte[] bOrgData) {
        try {
            X509Certificate publicCert = CertUtil.getPublicCert(pubCert);
            CMSEnvelopedDataGenerator generator = new CMSEnvelopedDataGenerator();
            generator.addRecipientInfoGenerator(new JceKeyTransRecipientInfoGenerator(publicCert).setProvider(BC));
            CMSEnvelopedData enveloped = null;
            try {

                enveloped = generator.generate(new CMSProcessableByteArray(bOrgData), new JceCMSContentEncryptorBuilder(CMSAlgorithm.DES_EDE3_CBC).setProvider(BC).build());
            } catch (CMSException e) {
                throw new RuntimeException(e);
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            new DEROutputStream(out).writeObject(enveloped.toASN1Structure());
            byte[] result = out.toByteArray();
            out.close();
            return result;
        } catch (CertificateException e) {
            logger.error("加密异常:{}", e.getMessage());
            throw new JdPayException("加密异常");
        }  catch (IOException e) {
            logger.error("加密异常:{}", e.getMessage());
            throw new JdPayException("加密异常");
        }
    }

    public byte[] decryptEnvelop(InputStream priCert, String privateKeyPassword, byte[] bEnvelop) {
        try {
            CMSEnvelopedData enveloped = new CMSEnvelopedData(bEnvelop);
            RecipientInformationStore ris = enveloped.getRecipientInfos();
            if (ris == null) {
                logger.error("数字信封格式不对:{}", new String(bEnvelop));
                throw new RuntimeException("验签异常");
            }

            X500PrivateCredential privateCert = CertUtil.getPrivateCert(priCert, privateKeyPassword.toCharArray());
            PrivateKey privateKey = privateCert.getPrivateKey();

            byte[] sign = null;
            Collection recipients = ris.getRecipients();
            for (Object object : recipients) {
                RecipientInformation recipient = (RecipientInformation) object;
                sign = recipient.getContent(new JceKeyTransEnvelopedRecipient(privateKey).setProvider(BC));
            }
            return sign;
        } catch (CMSException e) {
            logger.error("验签异常:{}", e.getMessage());
            throw new RuntimeException("验签异常");
        } catch (CertificateException e) {
            logger.error("验签异常:{}", e.getMessage());
            throw new RuntimeException("验签异常");
        }
    }
}
