/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * $Id: PolicyCallbackHandler1.java,v 1.2 2010-10-21 15:38:38 snajper Exp $
 */

package com.sun.xml.wss.callback;

import java.io.IOException;

import java.util.Enumeration;
import java.util.Properties;
import java.util.Arrays;
import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;

import java.text.SimpleDateFormat;

import java.math.BigInteger;

import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.CertPathBuilder;
import java.security.cert.Certificate;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.PKIXCertPathBuilderResult;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;

import javax.crypto.SecretKey;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import com.sun.xml.wss.impl.callback.*;
import com.sun.xml.wss.impl.policy.mls.AuthenticationTokenPolicy;
import com.sun.xml.wss.impl.policy.SecurityPolicy;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import javax.security.auth.x500.X500Principal;



/**
 * A sample implementation of a CallbackHandler.
 */
public  class PolicyCallbackHandler1 implements CallbackHandler {

    private String keyStoreURL;
    private String keyStorePassword;
    private String keyStoreType;

    private String trustStoreURL;
    private String trustStorePassword;
    private String trustStoreType;

    private String symmKeyStoreURL;
    private String symmKeyStorePassword;
    private String symmKeyStoreType;

    private KeyStore keyStore;
    private KeyStore trustStore;
    private KeyStore symmKeyStore;

    private static final String fileSeparator = System.getProperty("file.separator");

    private static final UnsupportedCallbackException unsupported = 
        new UnsupportedCallbackException(null, "Unsupported Callback Type Encountered");


    public PolicyCallbackHandler1(String side) throws Exception {

        Properties properties = new Properties();

        if (side.equals("server")) {
       String serverPropsFile ="security/keystore/server-security-env.properties";                
        properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(serverPropsFile));

        } else {
            // we are on the client side
        String clientPropsFile = "security/keystore/client-security-env.properties";
            properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(clientPropsFile));
       }

        this.keyStoreURL =  properties.getProperty("keystore.url");
        this.keyStoreType = properties.getProperty("keystore.type");
        this.keyStorePassword = properties.getProperty("keystore.password");

        this.trustStoreURL = properties.getProperty("truststore.url");
        this.trustStoreType = properties.getProperty("truststore.type");
        this.trustStorePassword = properties.getProperty("truststore.password");

        initTrustStore();
        initKeyStore();
    }


    @Override
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        
        for (int i=0; i < callbacks.length; i++) {
            
            if (callbacks[i] instanceof PasswordValidationCallback) {
                PasswordValidationCallback cb = (PasswordValidationCallback) callbacks[i];
                if (cb.getRequest() instanceof PasswordValidationCallback.PlainTextPasswordRequest) {
                    cb.setValidator(new PlainTextPasswordValidator());
                    
                } else if (cb.getRequest() instanceof PasswordValidationCallback.DigestPasswordRequest) {
                    PasswordValidationCallback.DigestPasswordRequest request =
                    (PasswordValidationCallback.DigestPasswordRequest) cb.getRequest();
                    String username = request.getUsername();
                    if ("Ron".equals(username)) {
                        request.setPassword("noR");
                        cb.setValidator(new PasswordValidationCallback.DigestPasswordValidator());
                    }
                } else {
                    throw unsupported;
                }
                
            } else if (callbacks[i] instanceof TimestampValidationCallback) {
                TimestampValidationCallback cb = (TimestampValidationCallback) callbacks[i];
                cb.setValidator(new DefaultTimestampValidator());
                                                                                
            } else if (callbacks[i] instanceof SignatureVerificationKeyCallback) {
                SignatureVerificationKeyCallback cb = (SignatureVerificationKeyCallback)callbacks[i];
                
                if (cb.getRequest() instanceof SignatureVerificationKeyCallback.X509SubjectKeyIdentifierBasedRequest) {
                    // subject keyid request
                    SignatureVerificationKeyCallback.X509SubjectKeyIdentifierBasedRequest request =
                    (SignatureVerificationKeyCallback.X509SubjectKeyIdentifierBasedRequest) cb.getRequest();
                    if (trustStore == null)
                        initTrustStore();
                    X509Certificate cert =
                    getCertificateFromTrustStore(
                    request.getSubjectKeyIdentifier());
                    request.setX509Certificate(cert);
                    
                } else if (cb.getRequest() instanceof SignatureVerificationKeyCallback.X509IssuerSerialBasedRequest) {
                    // issuer serial request
                    SignatureVerificationKeyCallback.X509IssuerSerialBasedRequest request =
                    (SignatureVerificationKeyCallback.X509IssuerSerialBasedRequest) cb.getRequest();
                    if (trustStore == null)
                        initTrustStore();
                    X509Certificate cert =
                    getCertificateFromTrustStore(
                    request.getIssuerName(),
                    request.getSerialNumber());
                    request.setX509Certificate(cert);
                    
                } else if (cb.getRequest() instanceof SignatureVerificationKeyCallback.ThumbprintBasedRequest) {
                    SignatureVerificationKeyCallback.ThumbprintBasedRequest request =
                    (SignatureVerificationKeyCallback.ThumbprintBasedRequest) cb.getRequest();
                    if (trustStore == null)
                        initTrustStore();
                    X509Certificate cert =
                    getCertificateFromTrustStoreForThumbprint(
                    request.getThumbprintIdentifier());
                    request.setX509Certificate(cert);

                } else  {
                    throw unsupported;
                }
                
            } else if (callbacks[i] instanceof SignatureKeyCallback) {
                SignatureKeyCallback cb = (SignatureKeyCallback)callbacks[i];
                
                if (cb.getRequest() instanceof SignatureKeyCallback.DefaultPrivKeyCertRequest) {
                    // default priv key cert req
                    SignatureKeyCallback.DefaultPrivKeyCertRequest request =
                    (SignatureKeyCallback.DefaultPrivKeyCertRequest) cb.getRequest();
                    if (keyStore == null)
                        initKeyStore();
                    getDefaultPrivKeyCert(request);
                    
                } else if (cb.getRequest() instanceof SignatureKeyCallback.AliasPrivKeyCertRequest) {
                    SignatureKeyCallback.AliasPrivKeyCertRequest request =
                    (SignatureKeyCallback.AliasPrivKeyCertRequest) cb.getRequest();
                    String alias = request.getAlias();
                    if (keyStore == null)
                        initKeyStore();
                    try {
                        X509Certificate cert =
                        (X509Certificate) keyStore.getCertificate(alias);
                        request.setX509Certificate(cert);
                        // Assuming key passwords same as the keystore password
                        PrivateKey privKey =
                        (PrivateKey) keyStore.getKey(alias, keyStorePassword.toCharArray());
                        request.setPrivateKey(privKey);
                    } catch (Exception e) {
                        throw new IOException(e.getMessage());
                    }
                    
                } else {
                    throw unsupported;
                }
                
            } else if (callbacks[i] instanceof DecryptionKeyCallback) {
                DecryptionKeyCallback cb = (DecryptionKeyCallback)callbacks[i];
                
                if (cb.getRequest() instanceof  DecryptionKeyCallback.X509SubjectKeyIdentifierBasedRequest) {
                    DecryptionKeyCallback.X509SubjectKeyIdentifierBasedRequest request =
                    (DecryptionKeyCallback.X509SubjectKeyIdentifierBasedRequest) cb.getRequest();
                    byte[] ski = request.getSubjectKeyIdentifier();
                    if (keyStore == null)
                        initKeyStore();
                    PrivateKey privKey = getPrivateKey(ski);
                    request.setPrivateKey(privKey);
                    
                } else if (cb.getRequest() instanceof DecryptionKeyCallback.X509IssuerSerialBasedRequest) {
                    DecryptionKeyCallback.X509IssuerSerialBasedRequest request =
                    (DecryptionKeyCallback.X509IssuerSerialBasedRequest) cb.getRequest();
                    String issuerName = request.getIssuerName();
                    BigInteger serialNumber = request.getSerialNumber();
                    if (keyStore == null)
                        initKeyStore();
                    PrivateKey privKey = getPrivateKey(issuerName, serialNumber);
                    request.setPrivateKey(privKey);
                    
                } else if (cb.getRequest() instanceof DecryptionKeyCallback.X509CertificateBasedRequest) {
                    DecryptionKeyCallback.X509CertificateBasedRequest request =
                    (DecryptionKeyCallback.X509CertificateBasedRequest) cb.getRequest();
                    X509Certificate cert = request.getX509Certificate();
                    if (keyStore == null)
                        initKeyStore();
                    PrivateKey privKey = getPrivateKey(cert);
                    request.setPrivateKey(privKey);
                    
                } else if (cb.getRequest() instanceof DecryptionKeyCallback.AliasSymmetricKeyRequest) {
                    DecryptionKeyCallback.AliasSymmetricKeyRequest request =
                    (DecryptionKeyCallback.AliasSymmetricKeyRequest) cb.getRequest();
                    if (symmKeyStore == null)
                        initSymmKeyStore();
                    String alias = request.getAlias();
                    try {
                        // Assuming key password same as key store password
                        SecretKey symmKey =
                        (SecretKey) symmKeyStore.getKey(alias, symmKeyStorePassword.toCharArray());
                        request.setSymmetricKey(symmKey);
                    } catch (Exception e) {
                        throw new IOException(e.getMessage());
                    }
                    
                } else if (cb.getRequest() instanceof  DecryptionKeyCallback.ThumbprintBasedRequest) {
                    DecryptionKeyCallback.ThumbprintBasedRequest request =
                    (DecryptionKeyCallback.ThumbprintBasedRequest) cb.getRequest();
                    byte[] ski = request.getThumbprintIdentifier();
                    if (keyStore == null)
                        initKeyStore();
                    PrivateKey privKey = getPrivateKeyForThumbprint(ski);
                    request.setPrivateKey(privKey);
                } else  {
                    throw unsupported;
                }
                
            } else if (callbacks[i] instanceof EncryptionKeyCallback) {
                EncryptionKeyCallback cb = (EncryptionKeyCallback)callbacks[i];
                
                if (cb.getRequest() instanceof EncryptionKeyCallback.AliasX509CertificateRequest) {
                    EncryptionKeyCallback.AliasX509CertificateRequest request =
                    (EncryptionKeyCallback.AliasX509CertificateRequest) cb.getRequest();
                    if (trustStore == null)
                        initTrustStore();
                    String alias = request.getAlias();

                    if ("".equals(alias) || (alias == null)) {
                        getDefaultCertificateFromTrustStore(request);
                    } else {

                        try {
                            X509Certificate cert =
                            (X509Certificate) trustStore.getCertificate(alias);
                            request.setX509Certificate(cert);
                        } catch (Exception e) {
                            throw new IOException(e.getMessage());
                        }
                    }
                    
                } else if (cb.getRequest() instanceof EncryptionKeyCallback.AliasSymmetricKeyRequest) {
                    EncryptionKeyCallback.AliasSymmetricKeyRequest request =
                    (EncryptionKeyCallback.AliasSymmetricKeyRequest) cb.getRequest();
                    if (symmKeyStore == null)
                        initSymmKeyStore();
                    String alias = request.getAlias();
                    try {
                        // Assuming key password same as key store password
                        SecretKey symmKey =
                        (SecretKey) symmKeyStore.getKey(alias, symmKeyStorePassword.toCharArray());
                        request.setSymmetricKey(symmKey);
                    } catch (Exception e) {
                        throw new IOException(e.getMessage());
                    }
                    
                } else {
                    throw unsupported;
                }
                
            } else if (callbacks[i] instanceof CertificateValidationCallback) {
                CertificateValidationCallback cb = (CertificateValidationCallback)callbacks[i];
                cb.setValidator(new X509CertificateValidatorImpl());
                
            } else  if (callbacks[i] instanceof DynamicPolicyCallback) {
                DynamicPolicyCallback dp = (DynamicPolicyCallback)callbacks[i];
                SecurityPolicy policy = dp.getSecurityPolicy();
                // This simplistic callback will simply set a Dummy Assertion
                // An actual implementation can locate the saml assertion and set it
                if (policy instanceof AuthenticationTokenPolicy.SAMLAssertionBinding) {
                                                                                                                        
                    AuthenticationTokenPolicy.SAMLAssertionBinding samlBinding =
                    (AuthenticationTokenPolicy.SAMLAssertionBinding)
                    ((AuthenticationTokenPolicy.SAMLAssertionBinding)policy).clone();
                    if ((samlBinding.getAssertion() == null) && (samlBinding.getAuthorityBinding() == null)) {
                        //populateAssertion(samlBinding, dp);
                    } else if (samlBinding.getAssertion() != null) {
                        //validateSAMLAssertion(samlBinding);
                    } else if ((samlBinding.getAuthorityBinding() != null) && (samlBinding.getAssertionId() != null)) {
                        //locateSAMLAssertion(samlBinding);
                    } else {
                        throw new UnsupportedCallbackException(null, "Missing information from SAML Policy");
                    }
                                                                                                                        
                } else {
                    throw unsupported;
                }

            } else {
                throw unsupported;
            }
        }
    }
    private void initTrustStore() throws IOException {
        try {
            trustStore = KeyStore.getInstance(trustStoreType);
            trustStore.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(trustStoreURL), trustStorePassword.toCharArray());
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    private void initKeyStore() throws IOException {
        try {
            keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(keyStoreURL), keyStorePassword.toCharArray());
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    private void initSymmKeyStore() throws IOException {
        try {
            symmKeyStore = KeyStore.getInstance(symmKeyStoreType);
            symmKeyStore.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(symmKeyStoreURL), symmKeyStorePassword.toCharArray());
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    private X509Certificate getCertificateFromTrustStore(byte[] ski)
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
                byte[] keyId = getSubjectKeyIdentifier(x509Cert);
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

    private X509Certificate getCertificateFromTrustStore(
        String issuerName,
        BigInteger serialNumber)
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
                if (thisIssuerPrincipal.equals(issuerPrincipal) &&
                    thisSerialNumber.equals(serialNumber)) {
                    return x509Cert;
                }
            }
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
        return null;
    }

    public PrivateKey getPrivateKey(byte[] ski) throws IOException {

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
                byte[] keyId = getSubjectKeyIdentifier(x509Cert);
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

    public PrivateKey getPrivateKey(
        String issuerName,
        BigInteger serialNumber)
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
                
                if (thisIssuerPrincipal.equals(issuerPrincipal) &&
                    thisSerialNumber.equals(serialNumber)) {
                    return (PrivateKey) keyStore.getKey(alias, keyStorePassword.toCharArray());
                }
            }
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
        return null;
    }

    public PrivateKey getPrivateKey(X509Certificate certificate)
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


    private void getDefaultCertificateFromTrustStore(
        EncryptionKeyCallback.AliasX509CertificateRequest req) {
           
            try {
                Enumeration aliases = trustStore.aliases();
	        while (aliases.hasMoreElements()) {
                    String currentAlias = (String) aliases.nextElement();
                    if (!"certificate-authority".equals(currentAlias)) {
                        X509Certificate thisCertificate = (X509Certificate)
                            trustStore.getCertificate(currentAlias);
                        req.setX509Certificate(thisCertificate);
                        return;
                   }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
    }

    private void getDefaultPrivKeyCert(
        SignatureKeyCallback.DefaultPrivKeyCertRequest request)
        throws IOException {

        String uniqueAlias = null;
        try {
            Enumeration aliases = keyStore.aliases();
            while (aliases.hasMoreElements()) {
                String currentAlias = (String) aliases.nextElement();
                if (keyStore.isKeyEntry(currentAlias)) {
                    Certificate thisCertificate = keyStore.getCertificate(currentAlias);
                    if (thisCertificate != null) {
                        if (thisCertificate instanceof X509Certificate) {
                            if (uniqueAlias == null) {
                                uniqueAlias = currentAlias;
                            } else {
                                // Not unique!
                                uniqueAlias = null;
                                break;
                            }
                        }
                    }
                }
            }
            if (uniqueAlias != null) {
                request.setX509Certificate(
                    (X509Certificate) keyStore.getCertificate(uniqueAlias));
                request.setPrivateKey(
                    (PrivateKey) keyStore.getKey(uniqueAlias, keyStorePassword.toCharArray()));
            }
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    /**
     * total header length
     */
    private static final byte HEADER_LENGTH = 4;

    /**
     * length of a header value
     */
    private static final byte HEADER_FIELD_LENGTH = 1;

    /**
     * position (0-based) of the header field that contains the expected length of the actual value in byte
     */
    private static final byte HEADER_FIELD_VALUE_LENGTH = 3;

    private static byte[] getSubjectKeyIdentifier(X509Certificate cert) {
        String SUBJECT_KEY_IDENTIFIER_OID = "2.5.29.14";
        byte[] subjectKeyIdentifier = cert.getExtensionValue(SUBJECT_KEY_IDENTIFIER_OID);
        if (subjectKeyIdentifier == null)
            return null;

        try {
            if (subjectKeyIdentifier.length < HEADER_LENGTH) {
                throw new IllegalArgumentException("subjectKeyIdentifier too short, header is missing");
            }

            final int valueLength = (new BigInteger(
                    Arrays.copyOfRange(subjectKeyIdentifier, HEADER_FIELD_VALUE_LENGTH, HEADER_FIELD_VALUE_LENGTH + HEADER_FIELD_LENGTH)))
                    .intValue();
            final int expectedTotalLength = HEADER_LENGTH + valueLength;
            if (subjectKeyIdentifier.length < expectedTotalLength) {
                throw new IllegalArgumentException(String.format("subjectKeyIdentifier too short (expected=%d, actual=%d)", expectedTotalLength, subjectKeyIdentifier.length));
            } else if (subjectKeyIdentifier.length > expectedTotalLength) {
                throw new IllegalArgumentException(String.format("subjectKeyIdentifier too big (expected=%d, actual=%d)", expectedTotalLength, subjectKeyIdentifier.length));
            }
            return Arrays.copyOfRange(subjectKeyIdentifier, HEADER_LENGTH, HEADER_LENGTH + valueLength);

        } catch (Exception e) {
            return null;
        }
    }


    private static class PlainTextPasswordValidator implements PasswordValidationCallback.PasswordValidator {

        @Override
        public boolean validate(PasswordValidationCallback.Request request) {
            
            PasswordValidationCallback.PlainTextPasswordRequest plainTextRequest =
                (PasswordValidationCallback.PlainTextPasswordRequest) request;
            return "Ron".equals(plainTextRequest.getUsername()) &&
                    "noR".equals(plainTextRequest.getPassword());
        }
    }


    private class DefaultTimestampValidator implements TimestampValidationCallback.TimestampValidator {

        @Override
        public void validate(TimestampValidationCallback.Request request)
            throws TimestampValidationCallback.TimestampValidationException {


            // validate timestamp creation and expiration time.
            TimestampValidationCallback.UTCTimestampRequest utcTimestampRequest =
                (TimestampValidationCallback.UTCTimestampRequest) request;


            SimpleDateFormat calendarFormatter1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            SimpleDateFormat calendarFormatter2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'.'sss'Z'");
            Date created = null;
            Date expired = null;
 
            try {
                try {
                    created = calendarFormatter1.parse(utcTimestampRequest.getCreated());
                    if ( utcTimestampRequest.getExpired() != null ) 
                        expired = calendarFormatter1.parse(utcTimestampRequest.getExpired());
                } catch (java.text.ParseException pe) {
                    created = calendarFormatter2.parse(utcTimestampRequest.getCreated());
                    if ( utcTimestampRequest.getExpired() != null ) 
                        expired = calendarFormatter2.parse(utcTimestampRequest.getExpired());
                }
            } catch ( java.text.ParseException pe ) {
                throw new TimestampValidationCallback.TimestampValidationException(pe.getMessage());
            }

            long maxClockSkew = utcTimestampRequest.getMaxClockSkew();
            long timestampFreshnessLimit = utcTimestampRequest.getTimestampFreshnessLimit();

            // validate creation time
            validateCreationTime(created, maxClockSkew, timestampFreshnessLimit);
             
            // validate expiration time
            if ( expired != null )
                validateExpirationTime(expired, maxClockSkew, timestampFreshnessLimit);
        }
    }

    public void validateExpirationTime(
        Date expires, long maxClockSkew, long timestampFreshnessLimit)
        throws TimestampValidationCallback.TimestampValidationException {
                
        //System.out.println("Validate Expiration time called");
        Date currentTime =
            getGMTDateWithSkewAdjusted(new GregorianCalendar(), maxClockSkew, false);
        if (expires.before(currentTime)) {
            throw new TimestampValidationCallback.TimestampValidationException(
                "The current time is ahead of the expiration time in Timestamp");
        }
    }

    public void validateCreationTime(
        Date created,
        long maxClockSkew,
        long timestampFreshnessLimit)
        throws TimestampValidationCallback.TimestampValidationException {

        //System.out.println("Validate Creation time called");
        Date current = getFreshnessAndSkewAdjustedDate(maxClockSkew, timestampFreshnessLimit);
            
        if (created.before(current)) {
            throw new TimestampValidationCallback.TimestampValidationException(
                "The creation time is older than " +
                " currenttime - timestamp-freshness-limit - max-clock-skew");
        }
            
        Date currentTime =
            getGMTDateWithSkewAdjusted(new GregorianCalendar(), maxClockSkew, true);
        if (currentTime.before(created)) {
            throw new TimestampValidationCallback.TimestampValidationException(
                "The creation time is ahead of the current time.");
        }
    }

    private static Date getFreshnessAndSkewAdjustedDate(
    long maxClockSkew, long timestampFreshnessLimit) {
        Calendar c = new GregorianCalendar();
        long offset = c.get(Calendar.ZONE_OFFSET);
        if (c.getTimeZone().inDaylightTime(c.getTime())) {
            offset += c.getTimeZone().getDSTSavings();
        }
        long beforeTime = c.getTimeInMillis();
        long currentTime = beforeTime - offset;
        
        long adjustedTime = currentTime - maxClockSkew - timestampFreshnessLimit;
        c.setTimeInMillis(adjustedTime);
        
        return c.getTime();
    }


    private static Date getGMTDateWithSkewAdjusted(
    Calendar c, long maxClockSkew, boolean addSkew) {
        long offset = c.get(Calendar.ZONE_OFFSET);
        if (c.getTimeZone().inDaylightTime(c.getTime())) {
            offset += c.getTimeZone().getDSTSavings();
        }
        long beforeTime = c.getTimeInMillis();
        long currentTime = beforeTime - offset;
        
        if (addSkew)
            currentTime = currentTime + maxClockSkew;
        else
            currentTime = currentTime - maxClockSkew;
        
        c.setTimeInMillis(currentTime);
        return c.getTime();
    }
    
    private class X509CertificateValidatorImpl implements CertificateValidationCallback.CertificateValidator {

        @Override
        public boolean validate(X509Certificate certificate)
            throws CertificateValidationCallback.CertificateValidationException {

            if (isSelfCert(certificate)) {
                return true;
            }
                                                                                
            try {
                certificate.checkValidity();
            } catch (CertificateExpiredException e) {
                e.printStackTrace();
                throw new CertificateValidationCallback.CertificateValidationException("X509Certificate Expired", e);
            } catch (CertificateNotYetValidException e) {
                e.printStackTrace();
                throw new CertificateValidationCallback.CertificateValidationException("X509Certificate not yet valid", e);
            }
                                                                                
            X509CertSelector certSelector = new X509CertSelector();
            certSelector.setCertificate(certificate);
                                                                                
            PKIXBuilderParameters parameters;
            CertPathBuilder builder;
            try {
                parameters = new PKIXBuilderParameters(trustStore, certSelector);
                parameters.setRevocationEnabled(false);
                builder = CertPathBuilder.getInstance("PKIX");
            } catch (Exception e) {
                e.printStackTrace();
                throw new CertificateValidationCallback.CertificateValidationException(e.getMessage(), e);
            }
                                                                                
            try {
                PKIXCertPathBuilderResult result =
                    (PKIXCertPathBuilderResult) builder.build(parameters);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        private boolean isSelfCert(X509Certificate cert)
            throws CertificateValidationCallback.CertificateValidationException {
            try {
                if (keyStore == null)
                    initKeyStore();
                Enumeration aliases = keyStore.aliases();
                while (aliases.hasMoreElements()) {
                    String alias = (String) aliases.nextElement();
                    if (keyStore.isKeyEntry(alias)) {
                        X509Certificate x509Cert =
                            (X509Certificate) keyStore.getCertificate(alias);
                        if (x509Cert != null) {
                            if (x509Cert.equals(cert))
                                return true;
                        }
                    }
                }
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                throw new CertificateValidationCallback.CertificateValidationException(e.getMessage(), e);
            }
        }
    }

   
     private X509Certificate getCertificateFromTrustStoreForThumbprint(byte[] ski)
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
                byte[] keyId = getThumbprintIdentifier(x509Cert);
                if (keyId == null) {
                    // Cert does not contain a key identifier
                    continue;
                }
                //System.out.println("Alias = " + alias + " ti=" + Base64.encode(keyId));
                if (Arrays.equals(ski, keyId)) {
                    return x509Cert;
                }
            }
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
        return null;
    }

     public static byte[] getThumbprintIdentifier(X509Certificate cert)
       throws Exception {
        byte[] thumbPrintIdentifier = null;
                                                                                                                      
        try {
            thumbPrintIdentifier = MessageDigest.getInstance("SHA-1").digest(cert.getEncoded());
        } catch ( NoSuchAlgorithmException ex ) {
            throw new Exception("Digest algorithm SHA-1 not found");
        } catch ( CertificateEncodingException ex) {
            throw new Exception("Error while getting certificate's raw content");
        }
        return thumbPrintIdentifier;
    }
   
    
    public PrivateKey getPrivateKeyForThumbprint(byte[] ski) throws IOException {
                                                                                                                                         
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
                byte[] keyId = getThumbprintIdentifier(x509Cert);
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

}
