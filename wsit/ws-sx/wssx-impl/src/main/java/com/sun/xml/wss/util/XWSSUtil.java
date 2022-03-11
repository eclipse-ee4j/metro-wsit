/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.util;

import com.sun.xml.wss.XWSSConstants;
import com.sun.xml.wss.XWSSecurityException;
import com.sun.xml.wss.core.reference.X509SubjectKeyIdentifier;
import com.sun.xml.wss.impl.MessageConstants;
import com.sun.xml.wss.impl.XWSSecurityRuntimeException;
import com.sun.xml.wss.impl.misc.SecurityUtil;
import com.sun.xml.wss.logging.LogDomainConstants;
import java.util.Arrays;
import java.util.Enumeration;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import java.io.IOException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CertSelector;
import java.security.cert.CertificateEncodingException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKey;
import javax.security.auth.x500.X500Principal;

/**
 *
 * @author Abhijit Das
 */
public abstract class XWSSUtil {

     /** logger */
    protected static final Logger log =  Logger.getLogger(
            LogDomainConstants.WSS_API_DOMAIN,LogDomainConstants.WSS_API_DOMAIN_BUNDLE);
    /**
     * 
     * @param ski byte[] representing SubjectKeyIdentifier
     * @param trustStore java.security.KeyStore 
     * @return X509Certificate from trustStore if present otherwise null.
     */
    
    public static X509Certificate getCertificateFromTrustStore(byte[] ski, KeyStore trustStore)
        throws IOException {

        try {
            Enumeration aliases = trustStore.aliases();
            while (aliases.hasMoreElements()) {
                String alias = (String) aliases.nextElement();
                Certificate cert = trustStore.getCertificate(alias);
                if (cert == null || !"X.509".equals(cert.getType())) {
                    continue;
                }
                X509Certificate x509Cert = (X509Certificate)cert;
                byte[] keyId = X509SubjectKeyIdentifier.getSubjectKeyIdentifier(x509Cert);
                if (keyId == null) {
                    // Cert does not contain a key identifier
                    continue;
                }
                if (Arrays.equals(ski, keyId)) {
                    return x509Cert;
                }
            }
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
        return null;
    }
    
    
    /**
     * 
     * @param issuerName Certificate Issuer Name
     * @param serialNumber Serial number of the certificate
     * @param trustStore java.security.Keystore
     * @return java.security.X509Certificate
     */
     public static X509Certificate getCertificateFromTrustStore(
        String issuerName,
        BigInteger serialNumber, KeyStore trustStore )
        throws IOException {

        try {
            Enumeration aliases = trustStore.aliases();
            while (aliases.hasMoreElements()) {
                String alias = (String) aliases.nextElement();
                Certificate cert = trustStore.getCertificate(alias);
                if (cert == null || !"X.509".equals(cert.getType())) {
                    continue;
                }
                X509Certificate x509Cert = (X509Certificate) cert;
                
                
                X500Principal thisIssuerPrincipal = x509Cert.getIssuerX500Principal();
                X500Principal issuerPrincipal = new X500Principal(issuerName);

                BigInteger thisSerialNumber = x509Cert.getSerialNumber();              
               
                if (thisIssuerPrincipal.equals(issuerPrincipal)
                        && thisSerialNumber.equals(serialNumber)) {
                    return x509Cert;
                }
            }
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
        return null;
     }
     
    /**
     *
     */
     public static PrivateKey getPrivateKey(byte[] ski, KeyStore keyStore, String keyStorePassword) throws IOException {

        try {
            Enumeration aliases = keyStore.aliases();
            while (aliases.hasMoreElements()) {
                String alias = (String) aliases.nextElement();
                if (!keyStore.isKeyEntry(alias))
                    continue;
                Certificate cert = keyStore.getCertificate(alias);
                if (cert == null || !"X.509".equals(cert.getType())) {
                    continue;
                }
                X509Certificate x509Cert = (X509Certificate) cert;
                byte[] keyId = X509SubjectKeyIdentifier.getSubjectKeyIdentifier(x509Cert);
                if (keyId == null) {
                    // Cert does not contain a key identifier
                    continue;
                }
                if (Arrays.equals(ski, keyId)) {
                    // Asuumed key password same as the keystore password
                    return (PrivateKey) keyStore.getKey(alias, keyStorePassword.toCharArray());
                }
            }
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
        return null;
     }
     
     
    /**
     *
     */
     public static PrivateKey getPrivateKey(
        String issuerName,
        BigInteger serialNumber, KeyStore keyStore, String keyStorePassword)
        throws IOException {

        try {
            Enumeration aliases = keyStore.aliases();
            while (aliases.hasMoreElements()) {
                String alias = (String) aliases.nextElement();
                if (!keyStore.isKeyEntry(alias))
                    continue;
                Certificate cert = keyStore.getCertificate(alias);
                if (cert == null || !"X.509".equals(cert.getType())) {
                    continue;
                }
                X509Certificate x509Cert = (X509Certificate) cert;
                
                X500Principal thisIssuerPrincipal = x509Cert.getIssuerX500Principal();
                X500Principal issuerPrincipal = new X500Principal(issuerName);

                BigInteger thisSerialNumber = x509Cert.getSerialNumber();

                if (thisIssuerPrincipal.equals(issuerPrincipal)
                        && thisSerialNumber.equals(serialNumber)) {
                    return (PrivateKey) keyStore.getKey(alias, keyStorePassword.toCharArray());
                }
            }
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
        return null;
     }
     
     
    /**
     *
     */
     public static PrivateKey getPrivateKey(X509Certificate certificate, KeyStore keyStore, String keyStorePassword)
        throws IOException {

        try {
            Enumeration aliases = keyStore.aliases();
            while (aliases.hasMoreElements()) {
                String alias = (String) aliases.nextElement();
                if (!keyStore.isKeyEntry(alias))
                    continue;
                Certificate cert = keyStore.getCertificate(alias);
                if (cert != null && cert.equals(certificate))
                    return (PrivateKey) keyStore.getKey(alias, keyStorePassword.toCharArray());
            }
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
        return null;
     }
     
    /**
     *
     */
     public static SecretKey generateSymmetricKey(String algorithm) throws XWSSecurityException {
         return SecurityUtil.generateSymmetricKey(algorithm);
     }

     public static X509Certificate matchesProgrammaticInfo(Object obj, byte[] keyIdentifier, String valueType) {
         if (obj == null) {
             return null;
         }
         if (obj instanceof X509Certificate) {
            try {
                X509Certificate cert = (X509Certificate) obj;
                byte[] keyId = null;
                if (MessageConstants.KEY_INDETIFIER_TYPE.equals(valueType)) {
                    keyId = X509SubjectKeyIdentifier.getSubjectKeyIdentifier(cert);
                } else if (MessageConstants.THUMB_PRINT_TYPE.equals(valueType)) {
                    keyId = getThumbprintIdentifier(cert);
                }
                if (keyId != null) {
                    if (Arrays.equals(keyIdentifier, keyId)) {
                        return cert;
                    }
                }
            } catch (XWSSecurityException ex) {
                log.log(Level.SEVERE, null, ex);
                throw new XWSSecurityRuntimeException(ex);
            }
        }
        return null;
    }

   public static X509Certificate matchesProgrammaticInfo(Object obj, PublicKey publicKey) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof X509Certificate) {
            X509Certificate cert = (X509Certificate) obj;
            if (cert.getPublicKey().equals(publicKey)) {
                return cert;
            }
        }
        return null;
    }

    public static X509Certificate matchesProgrammaticInfo(Object obj, BigInteger serialNumber, String issuerName) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof X509Certificate) {
            X509Certificate cert = (X509Certificate) obj;
            if (cert.getSerialNumber().equals(serialNumber)) {
               
                X500Principal thisIssuerPrincipal = cert.getIssuerX500Principal();
                X500Principal issuerPrincipal = new X500Principal(issuerName);
                if (thisIssuerPrincipal.equals(issuerPrincipal)) {
                    return cert;
                }
            }
        }
        return null;
    }

    public static PrivateKey getProgrammaticPrivateKey(Map context) {
        if (context == null) {
            return null;
        }
        Object obj = context.get(XWSSConstants.PRIVATEKEY_PROPERTY);
        if (obj instanceof PrivateKey) {
            return (PrivateKey)obj;
        } else {
            if (obj != null) {
                log.log(Level.SEVERE,"value of PRIVATEKEY_PROPERTY is not a PrivateKey" );
                throw new XWSSecurityRuntimeException("value of PRIVATEKEY_PROPERTY is not a PrivateKey");
            }
        }
        return null;
    }

    public static byte[] getThumbprintIdentifier(X509Certificate cert)
       throws XWSSecurityException {
        byte[] thumbPrintIdentifier = null;

        try {
            thumbPrintIdentifier = MessageDigest.getInstance("SHA-1").digest(cert.getEncoded());
        } catch ( NoSuchAlgorithmException ex ) {
            log.log(Level.SEVERE, "WSS0708.no.digest.algorithm");
            throw new XWSSecurityException("Digest algorithm SHA-1 not found");
        } catch ( CertificateEncodingException ex) {
            log.log(Level.SEVERE, "WSS0709.error.getting.rawContent");
            throw new XWSSecurityException("Error while getting certificate's raw content");
        }
        return thumbPrintIdentifier;
    }

    public static CertSelector getCertSelector(Class<?> certSelectorClass, Map context) {
        CertSelector selector = null;
        if (certSelectorClass != null) {
            Constructor ctor = null;
            try {
                ctor = certSelectorClass.getConstructor(Map.class);
            } catch (SecurityException | NoSuchMethodException ex) {
                //ignore and use default CTOR
            }
            if (ctor != null) {
                try {
                    selector = (CertSelector) ctor.newInstance(context);
                    return selector;
                } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException | InstantiationException ex) {
                    log.log(Level.SEVERE, "WSS0812.exception.instantiating.certselector", ex);
                    throw new RuntimeException(ex);
                }
            } else {
                try {
                    selector = (CertSelector) certSelectorClass.newInstance();
                    return selector;
                } catch (InstantiationException | IllegalAccessException ex) {
                    log.log(Level.SEVERE, "WSS0812.exception.instantiating.certselector", ex);
                    throw new RuntimeException(ex);
                }
            }
        } else {
            return null;
        }
    }

     
}
