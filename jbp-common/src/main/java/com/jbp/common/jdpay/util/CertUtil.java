package com.jbp.common.jdpay.util;

import javax.security.auth.x500.X500PrivateCredential;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

public class CertUtil {

    private static final String PKCS12 = "PKCS12";

    public static X500PrivateCredential getPrivateCert(InputStream pfxCert, char[] privateKeyPassword) throws CertificateException {
        KeyStore keyStore;
        String keyStoreAlias = null;

        /* Load KeyStore contents from file */
        try {
            keyStore = KeyStore.getInstance(CertUtil.PKCS12);
            keyStore.load(pfxCert, privateKeyPassword);

            /* Get aliases */
            Enumeration aliases = keyStore.aliases();
            if (aliases != null) {
                while (aliases.hasMoreElements()) {
                    keyStoreAlias = (String) aliases.nextElement();
                    Certificate[] certs = keyStore.getCertificateChain(keyStoreAlias);
                    if (certs == null || certs.length == 0) {
                        continue;
                    }
                    X509Certificate cert = (X509Certificate) certs[0];
                    if (matchUsage(cert.getKeyUsage(), 1)) {
                        try {
                            cert.checkValidity();
                        } catch (CertificateException e) {
                            continue;
                        }
                        break;
                    }
                }
            }
        } catch (GeneralSecurityException e) {
            throw new CertificateException("Error initializing keystore");
        } catch (IOException e) {
            throw new CertificateException("Error initializing keystore");
        }

        if (keyStoreAlias == null) {
            throw new CertificateException("None certificate for sign in this keystore");
        }

        /* Get certificate chain and create a certificate path */
        Certificate[] fromKeyStore;
        try {
            fromKeyStore = keyStore.getCertificateChain(keyStoreAlias);
            if (fromKeyStore == null
                    || fromKeyStore.length == 0
                    || !(fromKeyStore[0] instanceof X509Certificate)) {
                throw new CertificateException("Unable to find X.509 certificate chain in keystore");
            }
        } catch (KeyStoreException e) {
            throw new CertificateException("Error using keystore");
        }

        /* Get PrivateKey */
        Key privateKey;
        try {
            privateKey = keyStore.getKey(keyStoreAlias, privateKeyPassword);
            if (!(privateKey instanceof PrivateKey)) {
                throw new CertificateException("Unable to recover key from keystore");
            }
        } catch (KeyStoreException e) {
            throw new CertificateException("Error using keystore");
        } catch (NoSuchAlgorithmException e) {
            throw new CertificateException("Error using keystore");
        } catch (UnrecoverableKeyException e) {
            throw new CertificateException("Unable to recover key from keystore");
        }

        X509Certificate certificate = (X509Certificate) fromKeyStore[0];
        return new X500PrivateCredential(certificate, (PrivateKey) privateKey, keyStoreAlias);
    }

    public static X509Certificate getPublicCert(InputStream publicCert) throws CertificateException {
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X509");
            Certificate certificate = cf.generateCertificate(publicCert);
            return (X509Certificate) certificate;
        } catch (CertificateException e) {
            throw new CertificateException("Error loading public key certificate");
        }
    }

    private static boolean matchUsage(boolean[] keyUsage, int usage) {
        if (usage == 0 || keyUsage == null) {
            return true;
        }
        for (int i = 0; i < Math.min(keyUsage.length, 32); i++) {
            if ((usage & (1 << i)) != 0 && !keyUsage[i]) {
                return false;
            }
        }
        return true;
    }

}
