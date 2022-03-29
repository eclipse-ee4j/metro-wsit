/*
 * Copyright (c) 2010, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * $Id: WssProviderSecurityEnvironment.java,v 1.2 2010-10-21 15:37:15 snajper Exp $
 */

package com.sun.xml.wss.impl;

import com.sun.xml.ws.security.impl.kerberos.KerberosContext;
import java.math.BigInteger;
import java.security.AccessController;

import java.security.Key;
import java.security.KeyStore;
import java.security.PublicKey;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.KeyStoreException;

import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.security.cert.CertPathBuilder;
import java.security.cert.X509CertSelector;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;

import java.text.SimpleDateFormat;

import java.util.Base64;
import java.util.Map;
import java.util.Set;
import java.util.Date;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.GregorianCalendar;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.x500.X500Principal;
import javax.security.auth.x500.X500PrivateCredential;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;


import javax.crypto.SecretKey;

import com.sun.xml.ws.api.server.WSEndpoint;
import com.sun.xml.wss.NonceManager;
import com.sun.xml.wss.SecurityEnvironment;
import com.sun.xml.wss.XWSSecurityException;
import com.sun.xml.wss.core.Timestamp;
import com.sun.xml.wss.core.reference.KeyIdentifierSPI;
import com.sun.xml.wss.impl.misc.DefaultSecurityEnvironmentImpl;

import com.sun.xml.wss.core.reference.X509SubjectKeyIdentifier;
import com.sun.xml.wss.impl.callback.CertificateValidationCallback;

import com.sun.xml.wss.saml.Assertion;
//import com.sun.xml.wss.saml.assertion.AuthorityBinding;

import com.sun.xml.wss.impl.policy.mls.AuthenticationTokenPolicy;
import com.sun.xml.wss.impl.configuration.DynamicApplicationContext;

import javax.xml.stream.XMLStreamReader;

import org.ietf.jgss.GSSName;
import org.w3c.dom.Element;
import org.w3c.dom.Document;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivilegedAction;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertStore;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateFactory;
import java.security.cert.CollectionCertStoreParameters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//import jakarta.security.auth.message.callback.PasswordValidationCallback;
import jakarta.security.auth.message.callback.CertStoreCallback;
import jakarta.security.auth.message.callback.PasswordValidationCallback;
import jakarta.security.auth.message.callback.PrivateKeyCallback;
import jakarta.security.auth.message.callback.SecretKeyCallback;
import jakarta.security.auth.message.callback.TrustStoreCallback;
import org.ietf.jgss.GSSCredential;

public class WssProviderSecurityEnvironment implements SecurityEnvironment {


    /* menu of module options - includes algorithm Ids, keystore aliases etc., */
    private Map _securityOptions;

    /* Callbacks */
    private CallbackHandler _handler;

    /* Map of aliases-key passwords obtained via Module Options */
    //Map aliases_keypwds = null;

    // value of the maximum skew between the local times of two
    // systems (in milliseconds).
    // Keeping it 1 minute.
    protected final long MAX_CLOCK_SKEW = 360000;

    // milliseconds (set to 5 mins), time for which a timestamp is considered fresh
    protected final long TIMESTAMP_FRESHNESS_LIMIT = 5 * 60 * 1000;

    private static final SimpleDateFormat calendarFormatter1 =
        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    private static final SimpleDateFormat calendarFormatter2 =
        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'.'SSS'Z'");

    public WssProviderSecurityEnvironment(CallbackHandler handler, Map options) {
           _handler = new PriviledgedHandler(handler);
           _securityOptions = options;

           if (_securityOptions != null) {
              String mo_aliases = (String)_securityOptions.get("ALIASES");
              String mo_keypwds = (String)_securityOptions.get("PASSWORDS");

              if (mo_aliases != null && mo_keypwds != null) {
                 StringTokenizer aliases = new StringTokenizer(mo_aliases, " ");
                 StringTokenizer keypwds = new StringTokenizer(mo_keypwds, " ");
//                 if (aliases.countTokens() != keypwds.countTokens())
                    // log.INFO

//                 while (aliases.hasMoreElements()) {
//                    aliases_keypwds.put(aliases.nextToken(), keypwds.nextToken());
//                 }
              }
           }
    }



    /*
     * @throws XWSSecurityException
     */
    @Override
    public PrivateKey getPrivateKey(Map context, String alias)
        throws XWSSecurityException {

        PrivateKey privateKey = null;
        try {
            PrivateKeyCallback.Request request =
                new PrivateKeyCallback.AliasRequest(alias);
            PrivateKeyCallback pkCallback = new PrivateKeyCallback(request);
            Callback[] callbacks = new Callback[] { pkCallback };
            _handler.handle(callbacks);
            privateKey = pkCallback.getKey();
        } catch (Exception e) {
             throw new XWSSecurityException(e);
        }

        if (privateKey == null) {
           throw new XWSSecurityException(
             "Unable to locate private key for the alias " + alias);
        }

        return privateKey;
    }

    /*
     * Retrieves the PrivateKey corresponding to the cert
     * with the given KeyIdentifier value
     *
     * @param keyIdentifier an Opaque identifier indicating
     * the X509 certificate
     *
     * @return the PrivateKey corresponding to the cert
     *  with the given KeyIdentifier value
     *
     * @throws XWSSecurityException
     */
    @Override
    public PrivateKey getPrivateKey(Map context, byte[] keyIdentifier)
        throws XWSSecurityException {
        /*
           use PrivateKeyCallback
        */
        try {
           Subject subject = getSubject(context);
           if (subject != null) {
              Set set = subject.getPrivateCredentials(X500PrivateCredential.class);
              if (set != null) {
                 Iterator it = set.iterator();
                 while (it.hasNext()) {
                    X500PrivateCredential cred = (X500PrivateCredential)it.next();
                    if (matchesKeyIdentifier(Base64.getMimeDecoder().decode(keyIdentifier),
                                             cred.getCertificate()))
                       return cred.getPrivateKey();
                 }
              }
           }

           PrivateKeyCallback.Request request = new PrivateKeyCallback.SubjectKeyIDRequest(
                                                                    keyIdentifier);
           PrivateKeyCallback pkCallback = new PrivateKeyCallback(request);
           Callback[] callbacks = new Callback[] { pkCallback };
           _handler.handle(callbacks);

           return pkCallback.getKey();
        } catch (Exception e) {
            throw new XWSSecurityException(e);
        }
    }

    /*
     * Retrieves the PrivateKey corresponding to the given cert
     *
     * @param cert an X509 certificate
     *
     * @return the PrivateKey corresponding to the cert
     *
     * @throws XWSSecurityException
     */
    @Override
    public PrivateKey getPrivateKey(Map context, X509Certificate cert)
        throws XWSSecurityException {
        /*
           use PrivateKeyCallback
        */
        try {
           Subject subject = getSubject(context);
           if (subject != null) {
              Set<X500PrivateCredential> set = subject.getPrivateCredentials(X500PrivateCredential.class);
              if (set != null) {
                 String issuerName = org.apache.xml.security.utils.
                                RFC2253Parser.normalize(
                                  cert.getIssuerX500Principal().getName());
                 Iterator<X500PrivateCredential> it = set.iterator();
                 while (it.hasNext()) {
                    X500PrivateCredential cred = it.next();
                    X509Certificate x509Cert = cred.getCertificate();
                    BigInteger serialNo = x509Cert.getSerialNumber();
                    X500Principal currentIssuerPrincipal = x509Cert.getIssuerX500Principal();
                    X500Principal issuerPrincipal = new X500Principal(issuerName);
                    if (serialNo.equals(cert.getSerialNumber())
                           && currentIssuerPrincipal.equals(issuerPrincipal)) {
                       return cred.getPrivateKey();
                    }
                   }
               }
           }

           PrivateKeyCallback.Request request = new PrivateKeyCallback.IssuerSerialNumRequest(
                                                       cert.getIssuerX500Principal(),                                                                   cert.getSerialNumber());
           PrivateKeyCallback pkCallback = new PrivateKeyCallback(request);
           Callback[] callbacks = new Callback[] { pkCallback };
           _handler.handle(callbacks);

           return pkCallback.getKey();
        } catch (Exception e) {
            throw new XWSSecurityException(e);
        }
     }

    /*
     * Retrieves the matching PrivateKey corresponding to cert whose
     * SerialNumber and IssuerName are given
     *
     * @param serialNumber X509Certificate SerialNumber
     * @param issuerName   X509Certificate IssuerName
     *
     * @return PrivateKey
     *
     * @throws XWSSecurityException
     */
    @Override
    public PrivateKey getPrivateKey(Map context, BigInteger serialNumber, String issuerName)
        throws XWSSecurityException {
        /*
           use PrivateKeyCallback
        */
        try {
           Subject subject = getSubject(context);
           if (subject != null) {
              Set set = subject.getPrivateCredentials(X500PrivateCredential.class);
              if (set != null) {
                 Iterator it = set.iterator();
                 while (it.hasNext()) {
                    X500PrivateCredential cred = (X500PrivateCredential)it.next();
                    X509Certificate x509Cert = cred.getCertificate();
                    BigInteger serialNo = x509Cert.getSerialNumber();

                     X500Principal currentIssuerPrincipal = x509Cert.getIssuerX500Principal();
                     X500Principal issuerPrincipal = new X500Principal(issuerName);
                     if (serialNo.equals(serialNumber)
                             && currentIssuerPrincipal.equals(issuerPrincipal)) {
                         return cred.getPrivateKey();
                     }
                 }
              }
           }

           PrivateKeyCallback.Request request = new PrivateKeyCallback.IssuerSerialNumRequest(
                                                       new X500Principal(issuerName),                                                                   serialNumber);
           PrivateKeyCallback pkCallback = new PrivateKeyCallback(request);
           Callback[] callbacks = new Callback[] { pkCallback };
           _handler.handle(callbacks);

           return pkCallback.getKey();
        } catch (Exception e) {
            throw new XWSSecurityException(e);
        }
    }

    /**
     * Retrieves a reasonable default value for the current user's
     * X509Certificate if one exists.
     *
     * @return the default certificate for the current user
     *
     */
    @Override
    public X509Certificate getDefaultCertificate(Map context)
        throws XWSSecurityException {
        /*
          use PrivateKeyCallback to get the
          certChain - return the first certificate
        */
        Subject subject = getSubject(context);
        if (subject != null) {
           Set set = subject.getPublicCredentials(X509Certificate.class);
           if (set != null && set.size() == 1)
              return ((X509Certificate)(set.toArray())[0]);
        }

        PrivateKeyCallback pkCallback = new PrivateKeyCallback(null);
        Callback[] _callbacks = new Callback[] { pkCallback };
        try {
            _handler.handle(_callbacks);
        } catch (Exception e) {
            throw new XWSSecurityException(e);
        }

        Certificate[] chain = pkCallback.getChain();
        if (chain == null) {
           throw new XWSSecurityException(
            "Empty certificate chain returned by PrivateKeyCallback");
        }
        return (X509Certificate)chain[0];
    }

    /**
     * Authenticate the user against a list of known username-password
     * pairs.
     *
     * @return true if the username-password pair is valid
     */
    @Override
    public boolean authenticateUser(Map context, String username, String password)
           throws XWSSecurityException {
        /*
          use PasswordValidationCallback
        */
        char[] pwd = (password == null) ? null : password.toCharArray();
        //PasswordValidationCallback pvCallback = new PasswordValidationCallback(username, pwd);
        PasswordValidationCallback pvCallback = new PasswordValidationCallback(
               this.getRequesterSubject(context),username, pwd);
        Callback[] callbacks = new Callback[] { pvCallback };
        try {
           _handler.handle(callbacks);
        } catch (Exception e) {
           throw new XWSSecurityException(e);
        }

        // zero the password
        if (pwd != null)
           pvCallback.clearPassword();

        return pvCallback.getResult();
    }
    @Override
    public String authenticateUser(Map context, String username) {

        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Authenticate the user given the password digest.
     *
     * @return true if the password digest is valid
     */
    @Override
    public boolean authenticateUser(
        Map context,
        String username,
        String passwordDigest,
        String nonce,
        String created) {
        /*
          can not implement
        */
        return false;
    }

    /**
     * Validate an X509Certificate.
     * @return true, if the cert is a valid one, false o/w.
     * @throws XWSSecurityException
     *     if there is some problem during validation.
     */
    @Override
    public boolean validateCertificate(X509Certificate cert, Map context)
        throws XWSSecurityException {
        /*
          use TrustStore and CertStore
        */
        try {
            cert.checkValidity();
        } catch (CertificateExpiredException e) {
            throw new XWSSecurityException("X509Certificate Expired", e);
        } catch (CertificateNotYetValidException e) {
            throw new XWSSecurityException("X509Certificate not yet valid", e);
        }

        // for self-signed certificate
        if(cert.getIssuerX500Principal().equals(cert.getSubjectX500Principal())){
            if(isTrustedSelfSigned(cert)){
                return true;
            }else{
                throw new XWSSecurityException("Validation of self signed certificate failed");
            }
        }

        X509CertSelector certSelector = new X509CertSelector();
        certSelector.setCertificate(cert);

        PKIXBuilderParameters parameters;
        CertPathBuilder builder = null;
        CertPathValidator certValidator = null;
        CertPath certPath = null;
        List<Certificate> certChainList = new ArrayList<>();
        boolean caFound = false;
        Principal certChainIssuer = null;
        int noOfEntriesInTrustStore = 0;
        boolean isIssuerCertMatched = false;
        try {
            Callback[] callbacks = null;
            CertStoreCallback csCallback = null;
            TrustStoreCallback tsCallback = null;

            if (tsCallback == null && csCallback == null) {
               csCallback = new CertStoreCallback();
               tsCallback = new TrustStoreCallback();
               callbacks = new Callback[] { csCallback, tsCallback };
            } else if (csCallback == null) {
               csCallback = new CertStoreCallback();
               callbacks = new Callback[] { csCallback };
            } else if (tsCallback == null) {
               tsCallback = new TrustStoreCallback();
               callbacks = new Callback[] { tsCallback };
            }

           try {
             _handler.handle(callbacks);
           } catch (Exception e) {
             throw new XWSSecurityException(e);
           }

            parameters = new PKIXBuilderParameters(tsCallback.getTrustStore(), certSelector);
            parameters.setRevocationEnabled(false);
            if (KeyIdentifierSPI.isIBMVM) {
                //requires the actual cert to be in a certstore
                CertStore cs = CertStore.getInstance("Collection",
                        new CollectionCertStoreParameters(Collections.singleton(cert)));
                parameters.addCertStore(cs);
            } else {
                parameters.addCertStore(csCallback.getCertStore());
            }

            Certificate[] certChain = null;
            String certAlias = tsCallback.getTrustStore().getCertificateAlias(cert);
            if(certAlias != null){
                certChain = tsCallback.getTrustStore().getCertificateChain(certAlias);
            }
            if(certChain == null){
                certChainList.add(cert);
                certChainIssuer = cert.getIssuerX500Principal();
                noOfEntriesInTrustStore = tsCallback.getTrustStore().size();
        }else{
        certChainList = Arrays.asList(certChain);
        }
            while(!caFound && noOfEntriesInTrustStore-- != 0 && certChain == null){
                Enumeration<String> aliases = tsCallback.getTrustStore().aliases();
                while (aliases.hasMoreElements()) {
                    String alias = aliases.nextElement();
                    Certificate certificate = tsCallback.getTrustStore().getCertificate(alias);
                    if (certificate == null || !"X.509".equals(certificate.getType()) || certChainList.contains(certificate)) {
                        continue;
                    }
                    X509Certificate x509Cert = (X509Certificate) certificate;
                    if(certChainIssuer.equals(x509Cert.getSubjectX500Principal())){
                        certChainList.add(certificate);
                        if(x509Cert.getSubjectX500Principal().equals(x509Cert.getIssuerX500Principal())){
                            caFound = true;
                            break;
                        }else{
                            certChainIssuer = x509Cert.getIssuerX500Principal();
                            if(!isIssuerCertMatched){
                            isIssuerCertMatched = true;
                            }
                        }
                    }else{
                    }
                }
                if(!caFound){
                    if(!isIssuerCertMatched){
                        break;
                    }else{
                        isIssuerCertMatched = false;
                    }
                }
            }
            try{
                CertificateFactory cf = CertificateFactory.getInstance("X.509");
                certPath = cf.generateCertPath(certChainList);
                certValidator = CertPathValidator.getInstance("PKIX");
            }catch(Exception e){
                throw new CertificateValidationCallback.CertificateValidationException(e.getMessage(), e);
            }
        } catch (Exception e) {
            // Log Message
            throw new XWSSecurityException(e);
        }

        try {
             certValidator.validate(certPath, parameters);
        } catch (Exception e) {
            // log message
            return false;
        }

        return true;
    }

    private boolean isTrustedSelfSigned(X509Certificate cert) throws XWSSecurityException {
        try {
            Callback[] callbacks = null;
            CertStoreCallback csCallback = null;
            TrustStoreCallback tsCallback = null;

            if (tsCallback == null && csCallback == null) {
                csCallback = new CertStoreCallback();
                tsCallback = new TrustStoreCallback();
                callbacks = new Callback[]{csCallback, tsCallback};
            } else if (csCallback == null) {
                csCallback = new CertStoreCallback();
                callbacks = new Callback[]{csCallback};
            } else if (tsCallback == null) {
                tsCallback = new TrustStoreCallback();
                callbacks = new Callback[]{tsCallback};
            }

            try {
                _handler.handle(callbacks);
            } catch (Exception e) {
                throw new XWSSecurityException(e);
            }

            if (tsCallback.getTrustStore() == null) {
                return false;
            }

            Enumeration aliases = tsCallback.getTrustStore().aliases();
            while (aliases.hasMoreElements()) {
                String alias = (String) aliases.nextElement();
                Certificate certificate = tsCallback.getTrustStore().getCertificate(alias);
                if (certificate == null || !"X.509".equals(certificate.getType())) {
                    continue;
                }
                X509Certificate x509Cert = (X509Certificate) certificate;
                if (x509Cert != null && x509Cert.equals(cert)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            throw new XWSSecurityException(e);
        }
    }

    /**
     * @param keyIdMatch
     *            KeyIdentifier to search for
     * @return the matching Certificate
     */
    public X509Certificate getMatchingCertificate(Map context, byte[] keyIdMatch)
        throws XWSSecurityException {

        Subject subject = getSubject(context);
        if (subject != null) {
           Set set = subject.getPrivateCredentials(X500PrivateCredential.class);
           if (set != null) {
              Iterator it = set.iterator();
              while (it.hasNext()) {
                 X500PrivateCredential cred = (X500PrivateCredential)it.next();
                 X509Certificate cert = cred.getCertificate();
                 if (matchesKeyIdentifier(keyIdMatch, cert))
                    return cert;
              }
           }
        }

        PrivateKeyCallback.Request request = new PrivateKeyCallback.SubjectKeyIDRequest(
                                                                    keyIdMatch);
        PrivateKeyCallback pkCallback = new PrivateKeyCallback(request);
        TrustStoreCallback tsCallback = new TrustStoreCallback();

        Callback[] callbacks = new Callback[] { pkCallback, tsCallback };

        try {
          _handler.handle(callbacks);
        } catch (Exception e) {
           throw new XWSSecurityException(e);
        }

        Certificate[] chain = pkCallback.getChain();
        if (chain != null) {
           for (int i=0; i<chain.length; i++) {
               X509Certificate x509Cert = (X509Certificate)chain[i];
               if (matchesKeyIdentifier(keyIdMatch, x509Cert))
                  return x509Cert;
           }
        }

        // if not found, look in Truststore
        //TODO: i should probably look inside the CertStore and not in TrustStore
        KeyStore trustStore = tsCallback.getTrustStore();
        if (trustStore != null) {
           X509Certificate otherPartyCert = getMatchingCertificate(keyIdMatch, trustStore);
           if (otherPartyCert != null) return otherPartyCert;
        }

        // if still not found, throw Exception
        throw new XWSSecurityException(
            "No Matching Certificate for :"
                + Arrays.toString(keyIdMatch)
                + " found in KeyStore or TrustStore");
    }

    public X509Certificate getMatchingCertificate(Map context, BigInteger serialNumber, String issuerName)
        throws XWSSecurityException {

        Subject subject = getSubject(context);
        if (subject != null) {
           Set set = subject.getPrivateCredentials(X500PrivateCredential.class);
           if (set != null) {
              Iterator it = set.iterator();
              while (it.hasNext()) {
                 X500PrivateCredential cred = (X500PrivateCredential)it.next();
                 X509Certificate x509Cert = cred.getCertificate();
                 BigInteger serialNo = x509Cert.getSerialNumber();

                 X500Principal currentIssuerPrincipal = x509Cert.getIssuerX500Principal();
                 X500Principal issuerPrincipal = new X500Principal(issuerName);
                 if (serialNo.equals(serialNumber)
                           && currentIssuerPrincipal.equals(issuerPrincipal)) {
                      return x509Cert;
                 }
              }
           }
        }

        PrivateKeyCallback.Request request = new PrivateKeyCallback.IssuerSerialNumRequest(
                                                       new X500Principal(issuerName),
                                                       serialNumber);
        PrivateKeyCallback pkCallback = new PrivateKeyCallback(request);
        TrustStoreCallback tsCallback = new TrustStoreCallback();

        Callback[] callbacks = new Callback[] { pkCallback, tsCallback };

        try {
          _handler.handle(callbacks);
        } catch (Exception e) {
           throw new XWSSecurityException(e);
        }

        Certificate[] chain = pkCallback.getChain();
        if (chain != null) {
           for (int i=0; i < chain.length; i++) {
               X509Certificate x509Cert = (X509Certificate)chain[i];
               if (
                   matchesIssuerSerialAndName(
                                   serialNumber,
                                   issuerName,
                                   x509Cert)) return x509Cert;
           }
        } else {
           // log
        }

        // if not found, look in Truststore
        //TODO: I should probably look inside CertStore instead of TrustStore
        KeyStore trustStore = tsCallback.getTrustStore();
        if (trustStore != null) {
            X509Certificate otherPartyCert = getMatchingCertificate(serialNumber,
                                                                    issuerName,
                                                                    trustStore);
            if (otherPartyCert != null) return otherPartyCert;
        } else {
            // log
        }

        // if still not found, throw Exception
        throw new XWSSecurityException(
            "No Matching Certificate for :"
                + " found in KeyStore or TrustStore");
    }

    /**
     * @param keyIdMatch
     *            KeyIdentifier to search for
     * @return the matching Certificate
     */
    public X509Certificate getMatchingCertificate(Map context, byte[] keyIdMatch, String valueType)
        throws XWSSecurityException {

        if (MessageConstants.KEY_INDETIFIER_TYPE.equals(valueType)){
            return getMatchingCertificate(context, keyIdMatch);
        }
        // handle thumbprint here
        Subject subject = getSubject(context);
        if (subject != null) {
           Set set = subject.getPrivateCredentials(X500PrivateCredential.class);
           if (set != null) {
              Iterator it = set.iterator();
              while (it.hasNext()) {
                 X500PrivateCredential cred = (X500PrivateCredential)it.next();
                 X509Certificate cert = cred.getCertificate();
                 if (matchesThumbPrint(keyIdMatch, cert))
                    return cert;
              }
           }
        }

        // TODO: change this once we get support for this.
        //PrivateKeyCallback.Request request = new PrivateKeyCallback.ThumbPrintRequest(keyIdMatch);
        PrivateKeyCallback.Request request = new PrivateKeyCallback.SubjectKeyIDRequest(keyIdMatch);
        PrivateKeyCallback pkCallback = new PrivateKeyCallback(request);
        TrustStoreCallback tsCallback = new TrustStoreCallback();

        Callback[] callbacks = new Callback[] { pkCallback, tsCallback };

        try {
          _handler.handle(callbacks);
        } catch (Exception e) {
           throw new XWSSecurityException(e);
        }

        Certificate[] chain = pkCallback.getChain();
        if (chain != null) {
           for (int i=0; i<chain.length; i++) {
               X509Certificate x509Cert = (X509Certificate)chain[i];
               if (matchesThumbPrint(keyIdMatch, x509Cert))
                  return x509Cert;
           }
        }

        // if not found, look in Truststore
        //TODO: i guess i need to look inside the CertStore and not TrustStore
        KeyStore trustStore = tsCallback.getTrustStore();
        if (trustStore != null) {
           X509Certificate otherPartyCert = getMatchingCertificate(keyIdMatch, trustStore, valueType);
           if (otherPartyCert != null) return otherPartyCert;
        }

        // if still not found, throw Exception
        throw new XWSSecurityException(
            "No Matching Certificate for :"
                + Arrays.toString(keyIdMatch)
                + " found in KeyStore or TrustStore");
    }

    @Override
    public SecretKey getSecretKey(Map context, String alias, boolean encryptMode)
        throws XWSSecurityException {
        /*
           Use SecretKeyCallback
        */
        SecretKeyCallback.Request request = new SecretKeyCallback.AliasRequest(alias);
        SecretKeyCallback skCallback = new SecretKeyCallback(request);
        Callback[] callbacks = new Callback[] { skCallback };
        try {
           _handler.handle(callbacks);
        } catch (Exception e) {
            throw new XWSSecurityException(e);
        }

        return skCallback.getKey();
    }

    @Override
    public X509Certificate getCertificate(Map context, String alias, boolean forSigning)
        throws XWSSecurityException {
        X509Certificate cert = null;
        try {
            PrivateKeyCallback pkCallback = null;
            if (forSigning) {
                try {
                    Subject subject = getSubject(context);
                    if (subject != null) {
                       Set set = subject.getPrivateCredentials(X500PrivateCredential.class);
                       if (set != null) {
                          Iterator it = set.iterator();
                          while (it.hasNext()) {
                             X500PrivateCredential cred = (X500PrivateCredential)it.next();
                             if (cred.getAlias().equals(alias))
                                return cred.getCertificate();
                          }
                       }
                     }

                     PrivateKeyCallback.Request request = new PrivateKeyCallback.AliasRequest(alias);
                     pkCallback = new PrivateKeyCallback(request);
                     Callback[] callbacks = new Callback[] { pkCallback };
                     _handler.handle(callbacks);
                } catch (Exception e) {
                     throw new XWSSecurityException(e);
                }

                Certificate[] chain = pkCallback.getChain();
                if (chain != null)
                   cert = (X509Certificate)chain[0];
                else
                   ;//log
            } else {
                TrustStoreCallback tsCallback = new TrustStoreCallback();
                Callback[] _callbacks = new Callback[] { tsCallback };
                _handler.handle(_callbacks);

                // look for dynamic certificate first
                cert = getDynamicCertificate(context, tsCallback.getTrustStore());
                //System.out.println("got dynamic cert " + cert);
                // look for alias
                if (cert == null) {
                    if (tsCallback.getTrustStore() != null) {
                       cert = (X509Certificate) tsCallback.getTrustStore().getCertificate(alias);
                    }
                }
           }
        } catch (Exception e) {
            throw new XWSSecurityException(e);
        }

        if (cert == null) {
           throw new XWSSecurityException(
             "Unable to locate certificate for the alias '" + alias + "'");
        }

        return cert;
    }

    private boolean matchesKeyIdentifier(
        byte[] keyIdMatch,
        X509Certificate x509Cert) throws XWSSecurityException {

        byte[] keyId = X509SubjectKeyIdentifier.getSubjectKeyIdentifier(x509Cert);
        if (keyId == null) {
            // Cert does not contain a key identifier
            return false;
        }

        return Arrays.equals(keyIdMatch, keyId);
    }


    public static byte[] getThumbprintIdentifier(X509Certificate cert)
       throws XWSSecurityException {
        byte[] thumbPrintIdentifier = null;

        try {
            thumbPrintIdentifier = MessageDigest.getInstance("SHA-1").digest(cert.getEncoded());
        } catch ( NoSuchAlgorithmException ex ) {
            throw new XWSSecurityException("Digest algorithm SHA-1 not found");
        } catch ( CertificateEncodingException ex) {
            throw new XWSSecurityException("Error while getting certificate's raw content");
        }
        return thumbPrintIdentifier;
    }

    private boolean matchesThumbPrint(
        byte[] keyIdMatch,
        X509Certificate x509Cert) throws XWSSecurityException {

        byte[] keyId = getThumbprintIdentifier(x509Cert);
        if (keyId == null) {
            // Cert does not contain a key identifier
            return false;
        }

        return Arrays.equals(keyIdMatch, keyId);
    }

    private X509Certificate getMatchingCertificate(
        byte[] keyIdMatch,
        KeyStore kStore)
        throws XWSSecurityException {

        if (kStore == null) {
            return null;
        }

        try {
            Enumeration enum1 = kStore.aliases();
            while (enum1.hasMoreElements()) {
                String alias = (String) enum1.nextElement();

                Certificate cert = kStore.getCertificate(alias);
                if (cert == null || !"X.509".equals(cert.getType())) {
                    continue;
                }

                X509Certificate x509Cert = (X509Certificate) cert;
                byte[] keyId = X509SubjectKeyIdentifier.getSubjectKeyIdentifier(x509Cert);
                if (keyId == null) {
                    // Cert does not contain a key identifier
                    continue;
                }

                if (Arrays.equals(keyIdMatch, keyId)) {
                    return x509Cert;
                }
            }
        } catch (KeyStoreException kEx) {
            throw new XWSSecurityException(kEx);
        }
        return null;
    }

    private X509Certificate getMatchingCertificate(
        byte[] keyIdMatch,
        KeyStore kStore,
        String valueType)
        throws XWSSecurityException {

        if (MessageConstants.KEY_INDETIFIER_TYPE.equals(valueType)){
            return getMatchingCertificate(keyIdMatch, kStore);
        }

        // now handle thumbprint here
        if (kStore == null) {
            return null;
        }

        try {
            Enumeration enum1 = kStore.aliases();
            while (enum1.hasMoreElements()) {
                String alias = (String) enum1.nextElement();

                Certificate cert = kStore.getCertificate(alias);
                if (cert == null || !"X.509".equals(cert.getType())) {
                    continue;
                }

                X509Certificate x509Cert = (X509Certificate) cert;
                byte[] keyId = getThumbprintIdentifier(x509Cert);

                if (Arrays.equals(keyIdMatch, keyId)) {
                    return x509Cert;
                }
            }
        } catch (KeyStoreException kEx) {
            throw new XWSSecurityException(kEx);
        }
        return null;
    }

    private boolean matchesIssuerSerialAndName(
        BigInteger serialNumberMatch,
        String issuerNameMatch,
        X509Certificate x509Cert) {

        BigInteger serialNumber = x509Cert.getSerialNumber();

        X500Principal currentIssuerPrincipal = x509Cert.getIssuerX500Principal();
        X500Principal issuerPrincipal = new X500Principal(issuerNameMatch);

        return serialNumber.equals(serialNumberMatch)
                && currentIssuerPrincipal.equals(issuerPrincipal);
    }

    private X509Certificate getMatchingCertificate(
        BigInteger serialNumber,
        String issuerName,
        KeyStore kStore)
        throws XWSSecurityException {

        if (kStore == null) {
            return null;
        }
        try {
            Enumeration enum1 = kStore.aliases();
            while (enum1.hasMoreElements()) {
                String alias = (String) enum1.nextElement();

                Certificate cert = kStore.getCertificate(alias);
                if (cert == null || !"X.509".equals(cert.getType())) {
                    continue;
                }

                X509Certificate x509Cert = (X509Certificate) cert;
                BigInteger serialNo = x509Cert.getSerialNumber();

                X500Principal currentIssuerPrincipal = x509Cert.getIssuerX500Principal();
                X500Principal issuerPrincipal = new X500Principal(issuerName);
                if (serialNo.equals(serialNumber)
                       && currentIssuerPrincipal.equals(issuerPrincipal)) {
                   return x509Cert;
                }
            }
        } catch (KeyStoreException kEx) {
            throw new XWSSecurityException(kEx);
        }
        return null;
    }

    @Override
    public void updateOtherPartySubject(
            final Subject subject,
            final String username,
            final String password) {
        AccessController.doPrivileged(
                new PrivilegedAction<>() {
                    @Override
                    public Object run() {
                        String x500Name = "CN=" + username;
                        Principal principal = new X500Principal(x500Name);
                        subject.getPrincipals().add(principal);
                        subject.getPrivateCredentials().add(password);
                        return null;
                    }
                });

    }

    @Override
    public void updateOtherPartySubject(
        final Subject subject,
        final X509Certificate cert) {
        AccessController.doPrivileged(
                new PrivilegedAction<>() {
                    @Override
                    public Object run() {
                        Principal principal = cert.getSubjectX500Principal();
                        subject.getPrincipals().add(principal);
                        subject.getPublicCredentials().add(cert);
                        return null;
                    }
                });
    }


    @Override
    public void updateOtherPartySubject(
        final Subject subject,
        final Assertion assertion) {

        //subject.getPublicCredentials().add(assertion);
    }


    @Override
    public PublicKey getPublicKey(Map context, BigInteger serialNumber, String issuerName)
        throws XWSSecurityException {
      return getMatchingCertificate(context, serialNumber, issuerName).getPublicKey();
    }

    public PublicKey getPublicKey(String keyIdentifier)
        throws XWSSecurityException {
        try {
            return getMatchingCertificate(null,
                getDecodedBase64EncodedData(keyIdentifier))
                .getPublicKey();
        } catch (Exception e) {
            throw new XWSSecurityException(e);
        }
    }

    @Override
    public PublicKey getPublicKey(Map context, byte[] keyIdentifier)
        throws XWSSecurityException {
        try {
            return getMatchingCertificate(context, keyIdentifier).getPublicKey();
        } catch (Exception e) {
            throw new XWSSecurityException(e);
        }
    }

    @Override
    public PublicKey getPublicKey(Map context, byte[] identifier, String valueType)
    throws XWSSecurityException {
        return getMatchingCertificate(context, identifier, valueType).getPublicKey();
    }

    private byte[] getDecodedBase64EncodedData(String encodedData)
        throws XWSSecurityException {
        try {
            return Base64.getMimeDecoder().decode(encodedData);
        } catch (IllegalArgumentException e) {
            throw new SecurityHeaderException(
                "Unable to decode Base64 encoded data",
                e);
        }
    }

    @Override
    public X509Certificate getCertificate(
        Map context,
        BigInteger serialNumber,
        String issuerName)
        throws XWSSecurityException {
        return getMatchingCertificate(context, serialNumber, issuerName);
    }

    public X509Certificate getCertificate(String keyIdentifier)
        throws XWSSecurityException {
        try {
            byte[] decoded = getDecodedBase64EncodedData(keyIdentifier);
            return getMatchingCertificate(null, decoded);
        } catch (Exception e) {
            throw new XWSSecurityException(e);
        }
    }

    @Override
    public PrivateKey getPrivateKey(Map context, PublicKey publicKey, boolean forSign) {
        return null;
    }

    @Override
    public X509Certificate getCertificate(Map context, byte[] ski) {
        return null;
    }
    @Override
    public X509Certificate getCertificate(Map context, PublicKey publicKey, boolean forSign) {
        return null;
    }

    @Override
    public X509Certificate getCertificate(Map context, byte[] identifier, String valueType) {
        // on the lines of other getCertificates here return null for now.
        return null;
    }


    public boolean validateSamlIssuer(String issuer) {
        return true;
    }

    public boolean validateSamlUser(String user, String domain, String format) {
        return true;
    }
    @SuppressWarnings("unchecked")
    public void setSubject(Subject subject, Map context) {
        context.put(MessageConstants.SELF_SUBJECT, subject);
    }

    @SuppressWarnings("unchecked")
    public void setRequesterSubject(Subject subject, Map context) {
        context.put(MessageConstants.AUTH_SUBJECT, subject);
    }

    @Override
    public Subject getSubject() {
        return null;
    }

    public Subject getSubject(Map context) {
        return (Subject)context.get(MessageConstants.SELF_SUBJECT);
    }

    public Subject getRequesterSubject(Map context) {
        return (Subject)context.get(MessageConstants.AUTH_SUBJECT);
    }

    private Date getGMTDateWithSkewAdjusted(Calendar c, boolean addSkew) {
        long offset = c.get(Calendar.ZONE_OFFSET);
        if (c.getTimeZone().inDaylightTime(c.getTime())) {
            offset += c.getTimeZone().getDSTSavings();
        }
        long beforeTime = c.getTimeInMillis();
        long currentTime = beforeTime - offset;

        if (addSkew)
            currentTime = currentTime + MAX_CLOCK_SKEW;
        else
            currentTime = currentTime - MAX_CLOCK_SKEW;

        c.setTimeInMillis(currentTime);
        return c.getTime();
    }

    /**
     * Not implemented: AuthModules use Callbacks internally
     */
    @Override
    public String getUsername(Map context) {

        NameCallback nameCallback    = new NameCallback("Username: ");
        try {
            Callback[] cbs = new Callback[] {nameCallback};
            _handler.handle(cbs);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

       return nameCallback.getName();
    }

    /**
     * Not implemented: AuthModules use Callbacks internally
     */
    @Override
    public String getPassword(Map context) {

        PasswordCallback pwdCallback = new PasswordCallback("Password: ", false);
        try {
            Callback[] cbs = new Callback[] {pwdCallback };
            _handler.handle(cbs);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (pwdCallback.getPassword() == null)
            return null;

        return new String(pwdCallback.getPassword());
    }

   @Override
   public boolean validateAndCacheNonce(Map context, String nonce, String created, long maxNonceAge)
       throws XWSSecurityException {
       NonceManager nonceMgr = null;
       nonceMgr = NonceManager.getInstance(maxNonceAge, (WSEndpoint)context.get(MessageConstants.WSENDPOINT));
       return nonceMgr.validateNonce(nonce, created);
   }

    @Override
    public void validateTimestamp(Map context, String created,
                                  String expires, long maxClockSkew, long freshnessLimit)
               throws XWSSecurityException{
        if (expiresBeforeCreated(created, expires)) {
            XWSSecurityException xwsse = new XWSSecurityException("Message expired!");
            throw DefaultSecurityEnvironmentImpl.newSOAPFaultException(
                MessageConstants.WSU_MESSAGE_EXPIRED,
                "Message expired!",
                xwsse);
        }
        validateCreationTime(context, created, maxClockSkew, freshnessLimit);
        validateExpirationTime(expires, maxClockSkew, freshnessLimit);
    }


   @Override
   public void validateTimestamp(Map context, Timestamp timestamp, long maxClockSkew, long freshnessLimit)
   throws XWSSecurityException {
        validateTimestamp(context, timestamp.getCreated(), timestamp.getExpires(),
                maxClockSkew, freshnessLimit);
    }

    private static boolean expiresBeforeCreated (String creationTime, String expirationTime) throws XWSSecurityException {
        Date created = null;
        Date expires = null;
        try {
            try {
                synchronized(calendarFormatter1) {
                    created = calendarFormatter1.parse(creationTime);
                }
                if (expirationTime != null) {
                    synchronized(calendarFormatter1) {
                        expires = calendarFormatter1.parse(expirationTime);
                    }
                }

            } catch (java.text.ParseException pe) {
                synchronized(calendarFormatter2) {
                    created = calendarFormatter2.parse(creationTime);
                }
                if (expirationTime != null) {
                    synchronized(calendarFormatter2) {
                       expires = calendarFormatter2.parse(expirationTime);
                    }
                }
            }
         } catch (java.text.ParseException pe) {
             throw new XWSSecurityException(pe.getMessage());
         }

        return (expires != null) && expires.before(created);
    }

    @Override
    public void validateCreationTime(
    Map context,
    String creationTime,
    long maxClockSkew,
    long timestampFreshnessLimit)
    throws XWSSecurityException {
        Date created;
        try {
            synchronized(calendarFormatter1) {
                created = calendarFormatter1.parse(creationTime);
            }
        } catch (java.text.ParseException pe) {
            try {
                synchronized(calendarFormatter2) {
                    created = calendarFormatter2.parse(creationTime);
                }
            } catch (java.text.ParseException pe1) {
                throw new XWSSecurityException(
                    "Exception while parsing Creation Time :" + pe1.getMessage());
            }
        }

        Date current = null;
        current = getFreshnessAndSkewAdjustedDate(maxClockSkew, timestampFreshnessLimit);

        if (created.before(current)) {
            XWSSecurityException xwsse = new XWSSecurityException(
                "Creation Time is older than configured Timestamp Freshness Interval!");
            throw DefaultSecurityEnvironmentImpl.newSOAPFaultException(
                MessageConstants.WSSE_INVALID_SECURITY,
                "Creation Time is older than configured Timestamp Freshness Interval!",
                xwsse);
        }

        Date currentTime = getGMTDateWithSkewAdjusted(new GregorianCalendar(), maxClockSkew, true);

        if (currentTime.before(created)) {
            XWSSecurityException xwsse = new XWSSecurityException("Creation Time ahead of Current Time!");
            throw DefaultSecurityEnvironmentImpl.newSOAPFaultException(
                MessageConstants.WSSE_INVALID_SECURITY,
                "Creation Time ahead of Current Time!",
                xwsse);
        }

    }

    private void validateExpirationTime(
        String expirationTime, long maxClockSkew, long timestampFreshnessLimit)
        throws XWSSecurityException {
        if (expirationTime != null) {
            Date expires;
            try {
                synchronized(calendarFormatter1) {
                    expires = calendarFormatter1.parse(expirationTime);
                }
            } catch (java.text.ParseException pe) {
                try {
                    synchronized(calendarFormatter2) {
                        expires = calendarFormatter2.parse(expirationTime);
                    }
                } catch (java.text.ParseException pe1) {
                    throw new XWSSecurityException(
                        "Exception while parsing Expiration Time :" + pe1.getMessage());
                }
            }

            Date currentTime = getGMTDateWithSkewAdjusted(new GregorianCalendar(), maxClockSkew, false);

            if (expires.before(currentTime)) {
                XWSSecurityException xwsse = new XWSSecurityException("Message Expired!");
                throw DefaultSecurityEnvironmentImpl.newSOAPFaultException(
                    MessageConstants.WSU_MESSAGE_EXPIRED,
                    "Message Expired!",
                    xwsse);
            }
        }
    }

    @Override
    public CallbackHandler getCallbackHandler() {
        return _handler;
    }

    @Override
    public void validateSAMLAssertion(Map context, Element assertion) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public Element locateSAMLAssertion(
        Map context, Element binding, String assertionId, Document ownerDoc) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public AuthenticationTokenPolicy.SAMLAssertionBinding
         populateSAMLPolicy(Map fpcontext, AuthenticationTokenPolicy.SAMLAssertionBinding policy,
         DynamicApplicationContext context) {
        throw new UnsupportedOperationException("Not supported");
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

    private static Date getFreshnessAndSkewAdjustedDate(
    long maxClockSkew, long timestampFreshnessLimit) {
        Calendar c = new GregorianCalendar();
        long offset = c.get(Calendar.ZONE_OFFSET);
        if (c.getTimeZone().inDaylightTime(c.getTime())) {
            offset += c.getTimeZone().getDSTSavings();
        }
        long beforeTime = c.getTimeInMillis();
        long currentTime = beforeTime - offset;

        // allow for clock_skew and timestamp_freshness
        long adjustedTime = currentTime - maxClockSkew - timestampFreshnessLimit;
        c.setTimeInMillis(adjustedTime);

        return c.getTime();
    }

    private X509Certificate getDynamicCertificate(Map context, KeyStore trustStore) {

        X509Certificate cert = null;

        Subject requesterSubject = getRequesterSubject(context);
        String keyId = (String)context.get(MessageConstants.REQUESTER_KEYID);
        String issuerName = (String)context.get(MessageConstants.REQUESTER_ISSUERNAME);
        BigInteger issuerSerial = (BigInteger)context.get(MessageConstants.REQUESTER_SERIAL);

        if (keyId != null) {
            try {
                cert = getMatchingCertificate(keyId.getBytes(), trustStore);
                if (cert != null)
                    return cert;
            } catch (XWSSecurityException e) {}
        } else if ((issuerName != null) && (issuerSerial != null)) {
            try {
                cert = getMatchingCertificate(issuerSerial, issuerName, trustStore);
                if (cert != null)
                    return cert;
            } catch (XWSSecurityException e) {}
        } else if (requesterSubject != null) {
            Set publicCredentials = requesterSubject.getPublicCredentials();
            for (Iterator it = publicCredentials.iterator(); it.hasNext();) {
                Object cred = it.next();
                if(cred instanceof java.security.cert.X509Certificate){
                    cert = (java.security.cert.X509Certificate)cred;
                }
            }
            return cert;
        }
        return null;
    }

    public void updateOtherPartySubject(Subject subj, String encryptedKey){
        //TODO:
    }

     public void updateOtherPartySubject(
        Subject subject,
        Key secretKey) {
     }

    @Override
    public PrivateKey getPrivateKey(Map context, byte[] keyIdentifier, String valueType)
        throws XWSSecurityException {
        if ( MessageConstants.KEY_INDETIFIER_TYPE.equals(valueType)) {
            return getPrivateKey(context, keyIdentifier);
        }

        try {
           Subject subject = getSubject(context);
           if (subject != null) {
              Set set = subject.getPrivateCredentials(X500PrivateCredential.class);
              if (set != null) {
                 Iterator it = set.iterator();
                 while (it.hasNext()) {
                    X500PrivateCredential cred = (X500PrivateCredential)it.next();
                    if (matchesThumbPrint(Base64.getMimeDecoder().decode(keyIdentifier),
                                             cred.getCertificate()))
                       return cred.getPrivateKey();
                 }
              }
           }

           // TODO: change this
           //PrivateKeyCallback.Request request = new PrivateKeyCallback.ThumbPrintRequest(
           //                                                         keyIdentifier);
           PrivateKeyCallback.Request request = new PrivateKeyCallback.SubjectKeyIDRequest(
                                                                    keyIdentifier);

           PrivateKeyCallback pkCallback = new PrivateKeyCallback(request);
           Callback[] callbacks = new Callback[] { pkCallback };
           _handler.handle(callbacks);

           return pkCallback.getKey();
        } catch (Exception e) {
            throw new XWSSecurityException(e);
        }

    }

    @Override
    public void validateSAMLAssertion(Map context, XMLStreamReader assertion) {
         throw new UnsupportedOperationException("Not supported");
    }


    @Override
    public void updateOtherPartySubject(Subject subject, XMLStreamReader assertion) {
        //TODO:
    }

    @Override
    public boolean isSelfCertificate(X509Certificate cert) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public void updateOtherPartySubject(Subject subject, Subject bootStrapSubject) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public KerberosContext doKerberosLogin() {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public KerberosContext doKerberosLogin(byte[] tokenValue) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public void updateOtherPartySubject(Subject subject, GSSName clientCred, GSSCredential gssCred) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    static class PriviledgedHandler implements CallbackHandler {

        CallbackHandler delegate = null;

        public PriviledgedHandler(CallbackHandler handler) {
            delegate = handler;
        }

        @Override
        public void handle(final Callback[] callbacks) {
            AccessController.doPrivileged(new PrivilegedAction() {

                @Override
                public Object run() {
                    try {
                        delegate.handle(callbacks);
                        return null;
                    } catch (Exception ex) {
                        throw new XWSSecurityRuntimeException(ex);
                    }
                }
            });
        }
    }

}
