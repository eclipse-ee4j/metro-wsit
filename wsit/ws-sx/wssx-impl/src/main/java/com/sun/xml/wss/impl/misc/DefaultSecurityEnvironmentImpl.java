/*
 * Copyright (c) 2010, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * $Id: DefaultSecurityEnvironmentImpl.java,v 1.4 2010-10-21 15:37:30 snajper Exp $
 */

package com.sun.xml.wss.impl.misc;

import org.apache.xml.security.exceptions.Base64DecodingException;
import com.sun.xml.ws.api.server.WSEndpoint;
import com.sun.xml.ws.security.impl.kerberos.KerberosContext;
import com.sun.xml.ws.security.impl.kerberos.KerberosLogin;
import com.sun.xml.ws.security.opt.impl.util.SOAPUtil;
import com.sun.xml.wss.NonceManager;
import com.sun.xml.wss.ProcessingContext;
import com.sun.xml.wss.RealmAuthenticationAdapter;
import java.math.BigInteger;
import java.security.Key;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.crypto.SecretKey;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.security.auth.x500.X500Principal;
import javax.xml.namespace.QName;
import com.sun.xml.wss.core.Timestamp;
import com.sun.xml.wss.impl.FilterProcessingContext;
import com.sun.xml.wss.logging.LogDomainConstants;
import com.sun.xml.wss.impl.MessageConstants;
import com.sun.xml.wss.XWSSecurityException;
import com.sun.xml.wss.impl.WssSoapFaultException;
import com.sun.xml.wss.SecurityEnvironment;
import com.sun.xml.wss.core.reference.X509SubjectKeyIdentifier;
import com.sun.xml.wss.impl.SecurableSoapMessage;
import com.sun.xml.wss.impl.XWSSecurityRuntimeException;
import com.sun.xml.wss.impl.callback.PasswordCallback;
import com.sun.xml.wss.impl.callback.PasswordValidationCallback;
import com.sun.xml.wss.impl.callback.UsernameCallback;
import com.sun.xml.wss.impl.callback.SignatureKeyCallback;
import com.sun.xml.wss.impl.callback.EncryptionKeyCallback;
import com.sun.xml.wss.impl.callback.SignatureVerificationKeyCallback;
import com.sun.xml.wss.impl.callback.DecryptionKeyCallback;
import com.sun.xml.wss.impl.callback.CertificateValidationCallback;
import com.sun.xml.wss.impl.callback.DynamicPolicyCallback;
import com.sun.xml.wss.impl.callback.SAMLValidator;
import com.sun.xml.wss.impl.callback.TimestampValidationCallback;
import com.sun.xml.wss.saml.Assertion;
import com.sun.xml.wss.impl.policy.mls.AuthenticationTokenPolicy;
import com.sun.xml.wss.impl.configuration.DynamicApplicationContext;
import com.sun.xml.wss.logging.LogStringsMessages;
import com.sun.xml.wss.util.XWSSUtil;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import javax.security.auth.kerberos.KerberosPrincipal;
import javax.security.auth.login.LoginContext;
import javax.security.auth.x500.X500PrivateCredential;
import javax.xml.stream.XMLStreamReader;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSName;
import org.w3c.dom.Element;
import org.w3c.dom.Document;

//TODO: support PrefixNamespaceMappingCallback
public class DefaultSecurityEnvironmentImpl implements SecurityEnvironment {

    // Zone offset
    private static final long offset;
    static {
        Calendar c = new GregorianCalendar();
        long calculatedOffset = c.get(Calendar.ZONE_OFFSET);
        if (c.getTimeZone().inDaylightTime(c.getTime())) {
            calculatedOffset += c.getTimeZone().getDSTSavings();
        }
        offset = calculatedOffset;
    }
  
    /** logger */
    protected static final Logger log = Logger.getLogger(
            LogDomainConstants.WSS_API_DOMAIN, LogDomainConstants.WSS_API_DOMAIN_BUNDLE);
    // milliseconds (set to 5 mins), time for which a timestamp is considered fresh
    private final SimpleDateFormat calendarFormatter1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    private final SimpleDateFormat calendarFormatter2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'.'SSS'Z'");
    private CallbackHandler callbackHandler = null;
    private boolean isDefaultHandler = false;
    private X509Certificate selfCertificate = null;
    private Properties configAssertions = null;
    
    private long maxNonceAge = MessageConstants.MAX_NONCE_AGE;
    private String mnaProperty = null;    
    private String JAASLoginModuleForKeystore;
    private Subject loginContextSubjectForKeystore;
    private String keyStoreCBH;
    private CallbackHandler keystoreCbHandlerClass;
    
    public DefaultSecurityEnvironmentImpl(CallbackHandler cHandler) {
        callbackHandler = cHandler;
        if (callbackHandler instanceof DefaultCallbackHandler) {
            isDefaultHandler = true;
        }
        // keep the self certificate handy
//        if (callbackHandler != null && (callbackHandler instanceof DefaultCallbackHandler)) {
//            try {
//                X509Certificate defaultCert = null;
//                SignatureKeyCallback.PrivKeyCertRequest privKeyRequest =
//                        new SignatureKeyCallback.DefaultPrivKeyCertRequest();
//                SignatureKeyCallback sigKeyCallback = new SignatureKeyCallback(privKeyRequest);
//                Callback[] callbacks = new Callback[]{sigKeyCallback};
//                callbackHandler.handle(callbacks);
//                selfCertificate = privKeyRequest.getX509Certificate();
//            } catch (Exception e) {
//            //ignore for now
//            }
//        }
    }

    public DefaultSecurityEnvironmentImpl(CallbackHandler cHandler, Properties confAssertions) {
        this.configAssertions = confAssertions;
        callbackHandler = cHandler;
        if (callbackHandler instanceof DefaultCallbackHandler) {
            isDefaultHandler = true;
        }
        //store the relevant config assertions here        
        this.mnaProperty = configAssertions.getProperty(DefaultCallbackHandler.MAX_NONCE_AGE_PROPERTY);
        if (this.mnaProperty != null) {
            try {
                maxNonceAge = SecurityUtil.toLong(mnaProperty);
            } catch (XWSSecurityException ex) {
                log.log(Level.FINE, " Exception while converting maxNonceAge config property, Setting MaxNonceAge to Default value {0}", MessageConstants.MAX_NONCE_AGE);
                maxNonceAge = MessageConstants.MAX_NONCE_AGE;
            }
        }

        JAASLoginModuleForKeystore = configAssertions.getProperty(DefaultCallbackHandler.JAAS_KEYSTORE_LOGIN_MODULE);
        keyStoreCBH = configAssertions.getProperty(DefaultCallbackHandler.KEYSTORE_CBH);
        loginContextSubjectForKeystore = initJAASKeyStoreLoginModule();        
        // keep the self certificate handy
//        if (callbackHandler != null && myAlias != null && (callbackHandler instanceof DefaultCallbackHandler)) {
//            try {
//                X509Certificate defaultCert = null;
//                SignatureKeyCallback.PrivKeyCertRequest privKeyRequest =
//                        new SignatureKeyCallback.DefaultPrivKeyCertRequest();
//                SignatureKeyCallback sigKeyCallback = new SignatureKeyCallback(privKeyRequest);
//                Callback[] callbacks = new Callback[]{sigKeyCallback};
//                callbackHandler.handle(callbacks);
//                selfCertificate = privKeyRequest.getX509Certificate();
//            } catch (Exception e) {
//            //ignore for now
//            }
//        }
    }
    /*
     * Applicable only for the signing case
     */

    @Override
    public X509Certificate getDefaultCertificate(Map context) throws XWSSecurityException {

        X509Certificate cert = getPublicCredentialsFromLCSubject();
        if (cert != null){
            return cert;
        }
        X509Certificate defaultCert = null;

        SignatureKeyCallback.PrivKeyCertRequest privKeyRequest =
                new SignatureKeyCallback.DefaultPrivKeyCertRequest();
        SignatureKeyCallback sigKeyCallback = new SignatureKeyCallback(privKeyRequest);
        //we want to give all runtime properties to be used by CertSelectors
        if (context != null /*&& !isDefaultHandler*/) {
            ProcessingContext.copy(sigKeyCallback.getRuntimeProperties(), context);
        }
        Callback[] callbacks = new Callback[]{sigKeyCallback};
        try {
            callbackHandler.handle(callbacks);
        } catch (Exception e) {
            log.log(Level.SEVERE, LogStringsMessages.WSS_0216_CALLBACKHANDLER_HANDLE_EXCEPTION("SignatureKeyCallback.DefaultPrivKeyCertRequest"),
                    new Object[]{"SignatureKeyCallback.DefaultPrivKeyCertRequest"});
            log.log(Level.SEVERE, LogStringsMessages.WSS_0217_CALLBACKHANDLER_HANDLE_EXCEPTION_LOG(), e);
            throw new XWSSecurityException(e);
        }
        defaultCert = privKeyRequest.getX509Certificate();

        if (defaultCert == null) {
            log.log(Level.SEVERE, LogStringsMessages.WSS_0218_CANNOT_LOCATE_DEFAULT_CERT());
            throw new XWSSecurityException(
                    "Unable to locate a default certificate");
        }
        return defaultCert;
    }

    public SignatureKeyCallback.PrivKeyCertRequest getDefaultPrivKeyCertRequest(Map context) throws XWSSecurityException {

        SignatureKeyCallback.PrivKeyCertRequest privKeyRequest =
                new SignatureKeyCallback.DefaultPrivKeyCertRequest();
        SignatureKeyCallback sigKeyCallback = new SignatureKeyCallback(privKeyRequest);
        //we want all runtime props to be available to certSelectors
        if (context != null /*&& !isDefaultHandler*/) {
            ProcessingContext.copy(sigKeyCallback.getRuntimeProperties(), context);
        }
        X500PrivateCredential cred = getPKCredentialsFromLCSubject();
        if (cred != null) {
            privKeyRequest.setX509Certificate(cred.getCertificate());
            privKeyRequest.setPrivateKey(cred.getPrivateKey());
            return privKeyRequest;
        }

        Callback[] callbacks = new Callback[]{sigKeyCallback};
        try {
            callbackHandler.handle(callbacks);
        } catch (Exception e) {
            log.log(Level.SEVERE, LogStringsMessages.WSS_0216_CALLBACKHANDLER_HANDLE_EXCEPTION("SignatureKeyCallback.DefaultPrivKeyCertRequest"),
                    new Object[]{"SignatureKeyCallback.DefaultPrivKeyCertRequest"});
            log.log(Level.SEVERE, LogStringsMessages.WSS_0217_CALLBACKHANDLER_HANDLE_EXCEPTION_LOG(), e);
            throw new XWSSecurityException(e);
        }
        return privKeyRequest;
    }

    public SignatureKeyCallback.AliasPrivKeyCertRequest getAliasPrivKeyCertRequest(String certIdentifier) throws XWSSecurityException {

        SignatureKeyCallback.AliasPrivKeyCertRequest request =
                new SignatureKeyCallback.AliasPrivKeyCertRequest(certIdentifier);
        X500PrivateCredential cred = getPKCredentialsFromLCSubject();
        if (cred != null && certIdentifier.equals(cred.getAlias())) {
            request.setX509Certificate(cred.getCertificate());
            request.setPrivateKey(cred.getPrivateKey());
            return request;
        }
        SignatureKeyCallback sigCallback = new SignatureKeyCallback(request);
        Callback[] callback = new Callback[]{sigCallback};
        try {
            callbackHandler.handle(callback);
        } catch (Exception e) {
            log.log(Level.SEVERE,LogStringsMessages.WSS_0216_CALLBACKHANDLER_HANDLE_EXCEPTION("SignatureKeyCallback.AliasPrivKeyCertRequest"),
                    new Object[]{"SignatureKeyCallback.AliasPrivKeyCertRequest"});
            log.log(Level.SEVERE, LogStringsMessages.WSS_0217_CALLBACKHANDLER_HANDLE_EXCEPTION_LOG(), e);
            throw new XWSSecurityException(e);
        }
        return request;
    }

    public PrivateKey getDefaultPrivateKey(Map context) throws XWSSecurityException {

        X500PrivateCredential cred = getPKCredentialsFromLCSubject();
        if(cred != null){
            return cred.getPrivateKey();
        }
        PrivateKey defaultPrivKey = null;

        SignatureKeyCallback.PrivKeyCertRequest privKeyRequest =
                new SignatureKeyCallback.DefaultPrivKeyCertRequest();
        SignatureKeyCallback sigKeyCallback = new SignatureKeyCallback(privKeyRequest);
        //we want to give all runtime props to CertSelector(s)
        if (context != null /*&& !isDefaultHandler*/) {
            ProcessingContext.copy(sigKeyCallback.getRuntimeProperties(), context);
        }
        Callback[] callbacks = new Callback[]{sigKeyCallback};
        try {
            callbackHandler.handle(callbacks);
        } catch (Exception e) {
            log.log(Level.SEVERE, LogStringsMessages.WSS_0216_CALLBACKHANDLER_HANDLE_EXCEPTION("SignatureKeyCallback.DefaultPrivKeyCertRequest"),
                    new Object[]{"SignatureKeyCallback.DefaultPrivKeyCertRequest"});
            log.log(Level.SEVERE, LogStringsMessages.WSS_0217_CALLBACKHANDLER_HANDLE_EXCEPTION_LOG(), e);
            throw new XWSSecurityException(e);
        }
        defaultPrivKey = privKeyRequest.getPrivateKey();

        if (defaultPrivKey == null) {
            log.log(Level.SEVERE, LogStringsMessages.WSS_0219_CANNOT_LOCATE_DEFAULT_PRIVKEY());
            throw new XWSSecurityException(
                    "Unable to locate a default certificate");
        }
        return defaultPrivKey;
    }

    @Override
    public SecretKey getSecretKey(Map context, String alias, boolean encryptMode)
            throws XWSSecurityException {

        SecretKey symmetricKey = null;

        if (encryptMode) {
            EncryptionKeyCallback.SymmetricKeyRequest symmKeyRequest =
                    new EncryptionKeyCallback.AliasSymmetricKeyRequest(alias);
            EncryptionKeyCallback encKeyCallback = new EncryptionKeyCallback(symmKeyRequest);
            //if (!isDefaultHandler) {
                ProcessingContext.copy(encKeyCallback.getRuntimeProperties(), context);
            //}
            Callback[] callbacks = new Callback[]{encKeyCallback};
            try {
                callbackHandler.handle(callbacks);
            } catch (Exception e) {
                log.log(Level.SEVERE, LogStringsMessages.WSS_0216_CALLBACKHANDLER_HANDLE_EXCEPTION("EncryptionKeyCallback.AliasSymmetricKeyRequest"),
                        new Object[]{"EncryptionKeyCallback.AliasSymmetricKeyRequest"});
                log.log(Level.SEVERE, LogStringsMessages.WSS_0217_CALLBACKHANDLER_HANDLE_EXCEPTION_LOG(), e);
                throw new XWSSecurityException(e);
            }
            symmetricKey = symmKeyRequest.getSymmetricKey();
        } else {
            DecryptionKeyCallback.SymmetricKeyRequest symmKeyRequest =
                    new DecryptionKeyCallback.AliasSymmetricKeyRequest(alias);
            DecryptionKeyCallback decryptKeyCallback = new DecryptionKeyCallback(symmKeyRequest);
            //if (!isDefaultHandler) {
                ProcessingContext.copy(decryptKeyCallback.getRuntimeProperties(), context);
            //}
            Callback[] callbacks = new Callback[]{decryptKeyCallback};
            try {
                callbackHandler.handle(callbacks);
            } catch (Exception e) {
                log.log(Level.SEVERE, LogStringsMessages.WSS_0216_CALLBACKHANDLER_HANDLE_EXCEPTION("DecryptionKeyCallback.AliasSymmetricKeyRequest"),
                        new Object[]{"DecryptionKeyCallback.AliasSymmetricKeyRequest"});
                log.log(Level.SEVERE,LogStringsMessages.WSS_0217_CALLBACKHANDLER_HANDLE_EXCEPTION_LOG(), e);
                throw new XWSSecurityException(e);
            }
            symmetricKey = symmKeyRequest.getSymmetricKey();
        }

        if (symmetricKey == null) {
            log.log(Level.SEVERE, LogStringsMessages.WSS_0220_CANNOT_LOCATE_SYMMETRICKEY_FOR_DECRYPT());
            throw new XWSSecurityException(
                    "Could not locate the symmetric key for alias '" + alias + "'");
        }
        return symmetricKey;
    }

    @Override
    public X509Certificate getCertificate(Map context, String alias, boolean forSigning)
            throws XWSSecurityException {

        X509Certificate cert = null;

        if (((alias == null) || ("".equals(alias)) && forSigning)) {
            return getDefaultCertificate(context);
        }
        cert = getPublicCredentialsFromLCSubject();
        if(cert != null){
            return cert;
        }
        if (forSigning) {            
            SignatureKeyCallback.PrivKeyCertRequest certRequest =
                    new SignatureKeyCallback.AliasPrivKeyCertRequest(alias);
            SignatureKeyCallback sigKeyCallback = new SignatureKeyCallback(certRequest);
//            if (!isDefaultHandler) {
            ProcessingContext.copy(sigKeyCallback.getRuntimeProperties(), context);
//            }
            Callback[] callbacks = new Callback[]{sigKeyCallback};
            try {
                callbackHandler.handle(callbacks);
            } catch (Exception e) {
                log.log(Level.SEVERE, LogStringsMessages.WSS_0216_CALLBACKHANDLER_HANDLE_EXCEPTION("SignatureKeyCallback.AliasPrivKeyCertRequest"),
                        new Object[]{"SignatureKeyCallback.AliasPrivKeyCertRequest"});
                log.log(Level.SEVERE, LogStringsMessages.WSS_0217_CALLBACKHANDLER_HANDLE_EXCEPTION_LOG(), e);
                throw new XWSSecurityException(e);
            }
            cert = certRequest.getX509Certificate();
        } else {           
            EncryptionKeyCallback.X509CertificateRequest certRequest =
                    new EncryptionKeyCallback.AliasX509CertificateRequest(alias);
            EncryptionKeyCallback encKeyCallback = new EncryptionKeyCallback(certRequest);
            //incase of EncryptionKeyCallback.AliasX509Request we need all runtime, properties
            //so we can pass them to CertSelector(s) if any...
            //if (!isDefaultHandler) {
            ProcessingContext.copy(encKeyCallback.getRuntimeProperties(), context);
            //} else {
            //    encKeyCallback.getRuntimeProperties().
            //           put(MessageConstants.AUTH_SUBJECT, context.get(MessageConstants.AUTH_SUBJECT));
            //}
            Callback[] callbacks = new Callback[]{encKeyCallback};
            try {
                callbackHandler.handle(callbacks);
            } catch (Exception e) {
                log.log(Level.SEVERE, LogStringsMessages.WSS_0216_CALLBACKHANDLER_HANDLE_EXCEPTION("EncryptionKeyCallback.AliasX509CertificateRequest"),
                        new Object[]{"EncryptionKeyCallback.AliasX509CertificateRequest"});
                log.log(Level.SEVERE, LogStringsMessages.WSS_0217_CALLBACKHANDLER_HANDLE_EXCEPTION_LOG(), e);
                throw new XWSSecurityException(e);
            }
            cert = certRequest.getX509Certificate();
        }

        if (cert == null) {
            String val = forSigning ? "Signature" : "Key Encryption";
            log.log(Level.SEVERE, LogStringsMessages.WSS_0221_CANNOT_LOCATE_CERT(val), new Object[]{val});
            throw new XWSSecurityException(
                    "Unable to locate certificate for the alias '" + alias + "'");
        }
        return cert;
    }

    @Override
    public X509Certificate getCertificate(Map context, PublicKey publicKey, boolean forSign)
            throws XWSSecurityException {       
        X509Certificate cert = getPublicCredentialsFromLCSubject();
        if(cert != null && cert.getPublicKey().equals(publicKey)){
            return cert;
        }
        if (!forSign) {
            SignatureVerificationKeyCallback.PublicKeyBasedRequest pubKeyReq =
                    new SignatureVerificationKeyCallback.PublicKeyBasedRequest(publicKey);
            SignatureVerificationKeyCallback verifyKeyCallback = new SignatureVerificationKeyCallback(pubKeyReq);
//            if (!isDefaultHandler) {
            ProcessingContext.copy(verifyKeyCallback.getRuntimeProperties(), context);
//            }
            Callback[] callbacks = new Callback[]{verifyKeyCallback};
            try {
                callbackHandler.handle(callbacks);
            } catch (UnsupportedCallbackException e1) {
            //ignore;
            } catch (Exception e) {
                log.log(Level.SEVERE, LogStringsMessages.WSS_0216_CALLBACKHANDLER_HANDLE_EXCEPTION("SignatureVerificationKeyCallback.PublicKeyBasedRequest"),
                        new Object[]{"SignatureVerificationKeyCallback.PublicKeyBasedRequest"});
                log.log(Level.SEVERE, LogStringsMessages.WSS_0217_CALLBACKHANDLER_HANDLE_EXCEPTION_LOG(), e);
                throw new XWSSecurityException(e);
            }
            return pubKeyReq.getX509Certificate();
        } else {            
            EncryptionKeyCallback.PublicKeyBasedRequest pubKeyReq =
                    new EncryptionKeyCallback.PublicKeyBasedRequest(publicKey);
            EncryptionKeyCallback encCallback = new EncryptionKeyCallback(pubKeyReq);
//            if (!isDefaultHandler) {
            ProcessingContext.copy(encCallback.getRuntimeProperties(), context);
//            }
            Callback[] callbacks = new Callback[]{encCallback};
            try {
                callbackHandler.handle(callbacks);
            } catch (UnsupportedCallbackException e1) {
            //ignore;
            } catch (Exception e) {
                log.log(Level.SEVERE, LogStringsMessages.WSS_0216_CALLBACKHANDLER_HANDLE_EXCEPTION("EncryptionKeyCallback.PublicKeyBasedRequest"),
                        new Object[]{"EncryptionKeyCallback.PublicKeyBasedRequest"});
                log.log(Level.SEVERE, LogStringsMessages.WSS_0217_CALLBACKHANDLER_HANDLE_EXCEPTION_LOG(), e);
                throw new XWSSecurityException(e);
            }
            return pubKeyReq.getX509Certificate();
        }
    }

    @Override
    public PrivateKey getPrivateKey(Map context, String alias)
            throws XWSSecurityException {

        PrivateKey privKey = null;

        if (alias == null) {
            return getDefaultPrivateKey(context);
        }
        X500PrivateCredential cred =  getPKCredentialsFromLCSubject();
        if(cred != null && cred.getAlias().equals(alias)){
            return cred.getPrivateKey();
        }
        SignatureKeyCallback.PrivKeyCertRequest privKeyRequest =
                new SignatureKeyCallback.AliasPrivKeyCertRequest(alias);
        SignatureKeyCallback sigKeyCallback = new SignatureKeyCallback(privKeyRequest);
//        if (!isDefaultHandler) {
        ProcessingContext.copy(sigKeyCallback.getRuntimeProperties(), context);
//        }
        Callback[] callbacks = new Callback[]{sigKeyCallback};
        try {
            callbackHandler.handle(callbacks);
        } catch (Exception e) {
            log.log(Level.SEVERE, LogStringsMessages.WSS_0216_CALLBACKHANDLER_HANDLE_EXCEPTION("SignatureKeyCallback.AliasPrivKeyCertRequest"),
                    new Object[]{"SignatureKeyCallback.AliasPrivKeyCertRequest"});
            log.log(Level.SEVERE, LogStringsMessages.WSS_0217_CALLBACKHANDLER_HANDLE_EXCEPTION_LOG(), e);
            throw new XWSSecurityException(e);
        }
        privKey = privKeyRequest.getPrivateKey();

        if (privKey == null) {
            log.log(Level.SEVERE, LogStringsMessages.WSS_0222_CANNOT_LOCATE_PRIVKEY(alias), new Object[]{alias});
            throw new XWSSecurityException(
                    "Unable to locate private key for the alias " + alias);
        }
        return privKey;
    }

    @Override
    public PrivateKey getPrivateKey(Map context, byte[] identifier, String valueType)
            throws XWSSecurityException {
        if (MessageConstants.KEY_INDETIFIER_TYPE.equals(valueType)) {
            return getPrivateKey(context, identifier);
        }

        X500PrivateCredential cred = getPKCredentialsFromLCSubject();
        try {
            if (cred != null && matchesThumbPrint(Base64.decode(identifier), cred.getCertificate())) {
                return cred.getPrivateKey();
            }
        } catch (Exception ex) {
            log.log(Level.SEVERE, null, ex);
            throw new XWSSecurityException(ex);
        }
        PrivateKey privateKey = null;

        DecryptionKeyCallback.PrivateKeyRequest privKeyRequest =
                new DecryptionKeyCallback.ThumbprintBasedRequest(identifier);
        DecryptionKeyCallback decryptKeyCallback = new DecryptionKeyCallback(privKeyRequest);
//        if (!isDefaultHandler) {
        ProcessingContext.copy(decryptKeyCallback.getRuntimeProperties(), context);
//        }
        Callback[] callbacks = new Callback[]{decryptKeyCallback};
        try {
            callbackHandler.handle(callbacks);
        } catch (Exception e) {
            log.log(Level.SEVERE, LogStringsMessages.WSS_0216_CALLBACKHANDLER_HANDLE_EXCEPTION("DecryptionKeyCallback.ThumbprintBasedRequest"),
                    new Object[]{"DecryptionKeyCallback.ThumbprintBasedRequest"});
            log.log(Level.SEVERE, LogStringsMessages.WSS_0217_CALLBACKHANDLER_HANDLE_EXCEPTION_LOG(), e);
            throw new XWSSecurityException(e);
        }
        privateKey = privKeyRequest.getPrivateKey();

        if (privateKey == null) {
            // not found so throw an exception
            log.log(Level.SEVERE, LogStringsMessages.WSS_0222_CANNOT_LOCATE_PRIVKEY(identifier), new Object[]{identifier});
            throw new XWSSecurityException(
                    "No Matching private key for " + Base64.encode(identifier) + " thumb print identifier found");
        }
        return privateKey;
    }

    @Override
    public PrivateKey getPrivateKey(Map context, byte[] keyIdentifier)
            throws XWSSecurityException {

        PrivateKey privateKey = null;
        X500PrivateCredential cred = getPKCredentialsFromLCSubject();
         try {
            if (cred != null && matchesKeyIdentifier(Base64.decode(keyIdentifier), cred.getCertificate())) {
                return cred.getPrivateKey();
            }
        } catch (Base64DecodingException ex) {
            log.log(Level.SEVERE, null, ex);
            throw new XWSSecurityException(ex);
        }
        DecryptionKeyCallback.PrivateKeyRequest privKeyRequest =
                new DecryptionKeyCallback.X509SubjectKeyIdentifierBasedRequest(keyIdentifier);
        DecryptionKeyCallback decryptKeyCallback = new DecryptionKeyCallback(privKeyRequest);
//        if (!isDefaultHandler) {
        ProcessingContext.copy(decryptKeyCallback.getRuntimeProperties(), context);
//        }
        Callback[] callbacks = new Callback[]{decryptKeyCallback};
        try {
            callbackHandler.handle(callbacks);
        } catch (Exception e) {
            log.log(Level.SEVERE, LogStringsMessages.WSS_0216_CALLBACKHANDLER_HANDLE_EXCEPTION("DecryptionKeyCallback.X509SubjectKeyIdentifierBasedRequest"),
                    new Object[]{"DecryptionKeyCallback.X509SubjectKeyIdentifierBasedRequest"});
            log.log(Level.SEVERE, LogStringsMessages.WSS_0217_CALLBACKHANDLER_HANDLE_EXCEPTION_LOG(), e);
            throw new XWSSecurityException(e);
        }
        privateKey = privKeyRequest.getPrivateKey();

        if (privateKey == null) {
            // not found so throw an exception
            log.log(Level.SEVERE, LogStringsMessages.WSS_0222_CANNOT_LOCATE_PRIVKEY(keyIdentifier), new Object[]{keyIdentifier});
            throw new XWSSecurityException(
                    "No Matching private key for " + Base64.encode(keyIdentifier) + " subject key identifier found");
        }
        return privateKey;
    }

    @Override
    public PrivateKey getPrivateKey(Map context, BigInteger serialNumber, String issuerName)
            throws XWSSecurityException {
        
        X500PrivateCredential cred = getPKCredentialsFromLCSubject();
        if (cred != null) {
            X509Certificate x509Cert = cred.getCertificate();
            BigInteger serialNo = x509Cert.getSerialNumber();
            
            //Fix for WSIT issue 
            X500Principal currentIssuerPrincipal = x509Cert.getIssuerX500Principal();
            X500Principal issuerPrincipal = new X500Principal(issuerName);
            if (serialNo.equals(serialNumber) &&
                    currentIssuerPrincipal.equals(issuerPrincipal)) {
                return cred.getPrivateKey();
            }
        }

        PrivateKey privateKey = null;

        DecryptionKeyCallback.PrivateKeyRequest privKeyRequest =
                new DecryptionKeyCallback.X509IssuerSerialBasedRequest(issuerName, serialNumber);
        DecryptionKeyCallback decryptKeyCallback = new DecryptionKeyCallback(privKeyRequest);
//        if (!isDefaultHandler) {
        ProcessingContext.copy(decryptKeyCallback.getRuntimeProperties(), context);
//        }       
        Callback[] callbacks = new Callback[]{decryptKeyCallback};
        try {
            callbackHandler.handle(callbacks);
        } catch (Exception e) {
            log.log(Level.SEVERE, LogStringsMessages.WSS_0216_CALLBACKHANDLER_HANDLE_EXCEPTION("DecryptionKeyCallback.X509IssuerSerialBasedRequest"),
                    new Object[]{"DecryptionKeyCallback.X509IssuerSerialBasedRequest"});
            log.log(Level.SEVERE, LogStringsMessages.WSS_0217_CALLBACKHANDLER_HANDLE_EXCEPTION_LOG(), e);
            throw new XWSSecurityException(e);
        }
        privateKey = privKeyRequest.getPrivateKey();

        if (privateKey == null) {
            // not found so throw an exception
            log.log(Level.SEVERE, LogStringsMessages.WSS_0222_CANNOT_LOCATE_PRIVKEY(serialNumber + ":" + issuerName),
                    new Object[]{serialNumber + ":" + issuerName});
            throw new XWSSecurityException(
                    "No Matching private key for serial number " + serialNumber + " and issuer name " + issuerName + " found");
        }

        return privateKey;
    }

    @Override
    public PublicKey getPublicKey(Map context, byte[] identifier, String valueType)
            throws XWSSecurityException {
        return getCertificate(context, identifier, valueType).getPublicKey();
    }

    @Override
    public PublicKey getPublicKey(Map context, byte[] keyIdentifier)
            throws XWSSecurityException {
        return getCertificate(context, keyIdentifier).getPublicKey();
    }

    @Override
    public X509Certificate getCertificate(Map context, byte[] identifier, String valueType)
            throws XWSSecurityException {
        if (MessageConstants.KEY_INDETIFIER_TYPE.equals(valueType)) {
            return getCertificate(context, identifier);
        }

        //Else if it is Thumbprint
        X509Certificate cert = null;
        cert = getPublicCredentialsFromLCSubject();
        try {
            if (cert != null && matchesThumbPrint(Base64.decode(identifier), cert)) {
                return cert;
            }
        } catch (Base64DecodingException ex) {
            log.log(Level.SEVERE, LogStringsMessages.WSS_0816_BASE_64_DECODING_ERROR(), ex);
            throw new XWSSecurityException(ex);
        } 

        SignatureVerificationKeyCallback.X509CertificateRequest certRequest =
                new SignatureVerificationKeyCallback.ThumbprintBasedRequest(identifier);
        SignatureVerificationKeyCallback verifyKeyCallback = new SignatureVerificationKeyCallback(certRequest);
        //if (!isDefaultHandler) {
            ProcessingContext.copy(verifyKeyCallback.getRuntimeProperties(), context);
        //}
        Callback[] callbacks = new Callback[]{verifyKeyCallback};
        try {
            callbackHandler.handle(callbacks);
        } catch (Exception e) {
            log.log(Level.SEVERE, LogStringsMessages.WSS_0216_CALLBACKHANDLER_HANDLE_EXCEPTION("SignatureVerificationKeyCallback.ThumbprintBasedRequest"),
                    new Object[]{"SignatureVerificationKeyCallback.ThumbprintBasedRequest"});
            log.log(Level.SEVERE, LogStringsMessages.WSS_0217_CALLBACKHANDLER_HANDLE_EXCEPTION_LOG(), e);
            throw new XWSSecurityException(e);

        }
        cert = certRequest.getX509Certificate();

        if (cert == null) {
            // not found so throw an exception
            log.log(Level.SEVERE, LogStringsMessages.WSS_0221_CANNOT_LOCATE_CERT(identifier), new Object[]{identifier});
            throw new XWSSecurityException("No Matching public key for " + Base64.encode(identifier) + " thumb print identifier found");
        }
        return cert;
    }

    @Override
    public X509Certificate getCertificate(Map context, byte[] keyIdentifier)
            throws XWSSecurityException {

        X509Certificate cert = null;
        cert = getPublicCredentialsFromLCSubject();
        try {
            if (cert != null && matchesKeyIdentifier(Base64.decode(keyIdentifier), cert)) {
                return cert;
            }
        } catch (Base64DecodingException ex) {
            log.log(Level.SEVERE, LogStringsMessages.WSS_0816_BASE_64_DECODING_ERROR(), ex);
            throw new XWSSecurityException(ex);
        } 

        SignatureVerificationKeyCallback.X509CertificateRequest certRequest =
                new SignatureVerificationKeyCallback.X509SubjectKeyIdentifierBasedRequest(keyIdentifier);
        SignatureVerificationKeyCallback verifyKeyCallback = new SignatureVerificationKeyCallback(certRequest);
        //if (!isDefaultHandler) {
            ProcessingContext.copy(verifyKeyCallback.getRuntimeProperties(), context);
        //}
        Callback[] callbacks = new Callback[]{verifyKeyCallback};
        try {
            callbackHandler.handle(callbacks);
        } catch (Exception e) {
            log.log(Level.SEVERE, LogStringsMessages.WSS_0216_CALLBACKHANDLER_HANDLE_EXCEPTION("SignatureVerificationKeyCallback.X509SubjectKeyIdentifierBasedRequest"),
                    new Object[]{"SignatureVerificationKeyCallback.X509SubjectKeyIdentifierBasedRequest"});
            log.log(Level.SEVERE, LogStringsMessages.WSS_0217_CALLBACKHANDLER_HANDLE_EXCEPTION_LOG(), e);
            throw new XWSSecurityException(e);

        }
        cert = certRequest.getX509Certificate();

        if (cert == null) {
            // not found so throw an exception
            log.log(Level.SEVERE, LogStringsMessages.WSS_0221_CANNOT_LOCATE_CERT(keyIdentifier), new Object[]{keyIdentifier});
            throw new XWSSecurityException("No Matching public key for " + Base64.encode(keyIdentifier) + " subject key identifier found");
        }
        return cert;
    }

    @Override
    public PublicKey getPublicKey(Map context, BigInteger serialNumber, String issuerName)
            throws XWSSecurityException {

        return getCertificate(context, serialNumber, issuerName).getPublicKey();
    }

    @Override
    public X509Certificate getCertificate(Map context, BigInteger serialNumber, String issuerName)
            throws XWSSecurityException {

        X509Certificate cert = null;
        cert = getPublicCredentialsFromLCSubject();
        
        if (cert != null) {
            BigInteger serialNo = cert.getSerialNumber();
            //Fix for WSIT issue 
            X500Principal currentIssuerPrincipal = cert.getIssuerX500Principal();
            X500Principal issuerPrincipal = new X500Principal(issuerName);
            if (serialNo.equals(serialNumber) &&
                    currentIssuerPrincipal.equals(issuerPrincipal)) {
                return cert;
            }
        }       

        SignatureVerificationKeyCallback.X509CertificateRequest certRequest =
                new SignatureVerificationKeyCallback.X509IssuerSerialBasedRequest(issuerName, serialNumber);
        SignatureVerificationKeyCallback verifyKeyCallback = new SignatureVerificationKeyCallback(certRequest);
        //if (!isDefaultHandler) {
            ProcessingContext.copy(verifyKeyCallback.getRuntimeProperties(), context);
        //}
        Callback[] callbacks = new Callback[]{verifyKeyCallback};
        try {
            callbackHandler.handle(callbacks);
        } catch (Exception e) {
            log.log(Level.SEVERE, LogStringsMessages.WSS_0216_CALLBACKHANDLER_HANDLE_EXCEPTION("SignatureVerificationKeyCallback.X509IssuerSerialBasedRequest"),
                    new Object[]{"SignatureVerificationKeyCallback.X509IssuerSerialBasedRequest"});
            log.log(Level.SEVERE, LogStringsMessages.WSS_0217_CALLBACKHANDLER_HANDLE_EXCEPTION_LOG(), e);
            throw new XWSSecurityException(e);
        }
        cert = certRequest.getX509Certificate();

        if (cert == null) {
            // not found so throw an exception
            log.log(Level.SEVERE, LogStringsMessages.WSS_0221_CANNOT_LOCATE_CERT(serialNumber + ":" + issuerName), new Object[]{serialNumber + ":" + issuerName});
            throw new XWSSecurityException(
                    "No Matching public key for serial number " + serialNumber + " and issuer name " + issuerName + " found");
        }

        return cert;
    }

    @Override
    public boolean validateCertificate(X509Certificate cert, Map context) {

        CertificateValidationCallback certValCallback = new CertificateValidationCallback(cert, context);
        Callback[] callbacks = new Callback[]{certValCallback};
        try {
            callbackHandler.handle(callbacks);
        } catch (Exception e) {
            log.log(Level.SEVERE, LogStringsMessages.WSS_0223_FAILED_CERTIFICATE_VALIDATION());
            throw newSOAPFaultException(
                    MessageConstants.WSSE_INVALID_SECURITY_TOKEN,
                    "Certificate validation failed",
                    e);
        }
        if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "Certificate Validation called on certificate {0}", cert.getSubjectDN());
        }
        return certValCallback.getResult();
        
    }

    @Override
    public void updateOtherPartySubject(
            final Subject subject, final String username, final String password) {
    //do nothing....
    }

    private X500PrivateCredential getPKCredentialsFromLCSubject() {
        if (loginContextSubjectForKeystore != null) {
            Set set = loginContextSubjectForKeystore.getPrivateCredentials(X500PrivateCredential.class);
            if (set != null) {
                Iterator it = set.iterator();
                if (it.hasNext()) {
                    X500PrivateCredential cred = (X500PrivateCredential) it.next();
                    return cred;
                }
            }
        }
        return null;
    }

    private X509Certificate getPublicCredentialsFromLCSubject() {
        X500PrivateCredential cred =  getPKCredentialsFromLCSubject();
        if(cred != null){
            return cred.getCertificate();
        }
        return null;
    }

    private Subject initJAASKeyStoreLoginModule() {
        if (JAASLoginModuleForKeystore == null) {
            return null;
        }
        LoginContext lc = null;
        try {
            if (keyStoreCBH != null) {
                keystoreCbHandlerClass = (CallbackHandler) loadClass(keyStoreCBH).newInstance();
                lc = new LoginContext(JAASLoginModuleForKeystore, keystoreCbHandlerClass);
            } else {
                lc = new LoginContext(JAASLoginModuleForKeystore);
            }
            lc.login();
            return lc.getSubject();
        } catch (InstantiationException ex) {
            log.log(Level.SEVERE, LogStringsMessages.WSS_0817_KEYSTORE_LOGIN_MODULE_LOGIN_ERROR() , ex);
            throw new XWSSecurityRuntimeException(ex);
        } catch (IllegalAccessException ex) {
            log.log(Level.SEVERE, LogStringsMessages.WSS_0817_KEYSTORE_LOGIN_MODULE_LOGIN_ERROR(), ex);
            throw new XWSSecurityRuntimeException(ex);
        } catch (XWSSecurityException ex) {
            log.log(Level.SEVERE, LogStringsMessages.WSS_0817_KEYSTORE_LOGIN_MODULE_LOGIN_ERROR(), ex);
            throw new XWSSecurityRuntimeException(ex);
        } catch (LoginException ex) {
            log.log(Level.SEVERE, LogStringsMessages.WSS_0817_KEYSTORE_LOGIN_MODULE_LOGIN_ERROR(), ex);
            throw new XWSSecurityRuntimeException(ex);
        }
    }

    private boolean matchesKeyIdentifier(
        byte[] keyIdMatch,
        X509Certificate x509Cert) throws XWSSecurityException {

        byte[] keyId = X509SubjectKeyIdentifier.getSubjectKeyIdentifier(x509Cert);
        if (keyId == null) {
            // Cert does not contain a key identifier
            return false;
        }

        if (Arrays.equals(keyIdMatch, keyId)) {
            return true;
        }
        return false;
    }

    private boolean matchesThumbPrint(
        byte[] keyIdMatch,
        X509Certificate x509Cert) throws XWSSecurityException {

        byte[] keyId = XWSSUtil.getThumbprintIdentifier(x509Cert);
        if (keyId == null) {
            // Cert does not contain a key identifier
            return false;
        }

        if (Arrays.equals(keyIdMatch, keyId)) {
            return true;
        }
        return false;
    }

    private void updateUsernameInSubject(
            final Subject subject, final String username, final String password) {

        AccessController.doPrivileged(new PrivilegedAction<>() {

            @Override
            public Object run() {
                String x500Name = "CN=" + username;
                // we can remove this and make a CallerPrincipalCallback
                Principal principal = null;
                try {
                    principal = new X500Principal(x500Name);
                    subject.getPrincipals().add(principal);
                } catch (Throwable t) {


                    //not all principals can be X500Names
                    //ignore if there was an Exception
                }
                subject.getPublicCredentials().add(username);
                //do not update password in subject ?.
//                if (password != null) {
//                    subject.getPrivateCredentials().add(password);
//                }
                return null; // nothing to return
            }
        });
    }

    @Override
    public void updateOtherPartySubject(
            final Subject subject,
            final X509Certificate cert) {
        AccessController.doPrivileged(new PrivilegedAction<>() {

            @Override
            public Object run() {
                Principal principal = cert.getSubjectX500Principal();
                subject.getPrincipals().add(principal);
                subject.getPublicCredentials().add(cert);
                return null; // nothing to return
            }
        });
    }

    @Override
    public void updateOtherPartySubject(
            final Subject subject,
            final Assertion assertion) {
         if (callbackHandler instanceof DefaultCallbackHandler) {
                if (((DefaultCallbackHandler)callbackHandler).getSAMLValidator() 
                        instanceof SAMLValidator)
                return;
        }
        AccessController.doPrivileged(new PrivilegedAction<>() {

            @Override
            public Object run() {
                subject.getPublicCredentials().add(assertion);
                return null; // nothing to return
            }
        });
    }

    public void updateOtherPartySubject(
            final Subject subject,
            final Key secretKey) {
//        AccessController.doPrivileged(new PrivilegedAction() {
//
//            public Object run() {
//                subject.getPublicCredentials().add(secretKey);
//                return null; // nothing to return
//            }
//        });
    }

    public void updateOtherPartySubject(
            final Subject subject,
            final String ek) {
//        AccessController.doPrivileged(new PrivilegedAction() {
//
//            public Object run() {
//                String encryptedKey = "EK" + ek;
//                subject.getPublicCredentials().add(encryptedKey);
//                return null; // nothing to return
//            }
//        });
    }

    public static Subject getSubject(final Map context) {
        Subject otherPartySubject =
                (Subject) context.get(MessageConstants.AUTH_SUBJECT);
        if (otherPartySubject != null) {
            return otherPartySubject;
        }
        otherPartySubject =
                AccessController.doPrivileged(
                        new PrivilegedAction<>() {
                            @Override
                            @SuppressWarnings("unchecked")
                            public Subject run() {
                                Subject otherPartySubj = new Subject();
                                context.put(MessageConstants.AUTH_SUBJECT, otherPartySubj);
                                return otherPartySubj;
                            }
                        });
        return otherPartySubject;
    }

    public static Subject getSubject(final FilterProcessingContext context) {
        Subject otherPartySubject = (Subject) context.getExtraneousProperty(MessageConstants.AUTH_SUBJECT);
        if (otherPartySubject != null) {
            return otherPartySubject;
        }
        otherPartySubject =
                AccessController.doPrivileged(
                        new PrivilegedAction<>() {
                            @Override
                            public Subject run() {
                                Subject otherPartySubj = new Subject();
                                context.setExtraneousProperty(MessageConstants.AUTH_SUBJECT, otherPartySubj);
                                return otherPartySubj;
                            }
                        });
        return otherPartySubject;
    }

    @Override
    public PrivateKey getPrivateKey(Map context, X509Certificate cert)
            throws XWSSecurityException {

        PrivateKey privateKey = null;
        X500PrivateCredential cred = getPKCredentialsFromLCSubject();
        if (cred != null) {
            X509Certificate x509Cert = cred.getCertificate();
            if (x509Cert.equals(cert)) {
                return cred.getPrivateKey();
            }
        }
        DecryptionKeyCallback.PrivateKeyRequest privateKeyRequest =
                new DecryptionKeyCallback.X509CertificateBasedRequest(cert);
        DecryptionKeyCallback decryptKeyCallback = new DecryptionKeyCallback(privateKeyRequest);
//        if (!isDefaultHandler) {
        ProcessingContext.copy(decryptKeyCallback.getRuntimeProperties(), context);
//        }
        Callback[] callbacks = new Callback[]{decryptKeyCallback};
        try {
            callbackHandler.handle(callbacks);
        } catch (Exception e) {
            log.log(Level.SEVERE, LogStringsMessages.WSS_0216_CALLBACKHANDLER_HANDLE_EXCEPTION("DecryptionKeyCallback.X509CertificateBasedRequest"),
                    new Object[]{"DecryptionKeyCallback.X509CertificateBasedRequest"});
            log.log(Level.SEVERE, LogStringsMessages.WSS_0217_CALLBACKHANDLER_HANDLE_EXCEPTION_LOG(), e);
            throw new XWSSecurityException(e);
        }
        privateKey = privateKeyRequest.getPrivateKey();

        if (privateKey == null) {
            log.log(Level.SEVERE, LogStringsMessages.WSS_0222_CANNOT_LOCATE_PRIVKEY("given certificate"), new Object[]{"given certificate"});
            throw new XWSSecurityException(
                    "Could not retrieve private Key matching the given certificate");
        }
        return privateKey;
    }

    @Override
    public PrivateKey getPrivateKey(Map context, PublicKey publicKey, boolean forSign)
            throws XWSSecurityException {
        X500PrivateCredential cred = getPKCredentialsFromLCSubject();
        if (cred != null) {
            X509Certificate x509Cert = cred.getCertificate();
            if (x509Cert.getPublicKey().equals(publicKey)) {
                return cred.getPrivateKey();
            }
        }
        if (forSign) {
            SignatureKeyCallback.PublicKeyBasedPrivKeyCertRequest req =
                    new SignatureKeyCallback.PublicKeyBasedPrivKeyCertRequest(publicKey);
            SignatureKeyCallback skc = new SignatureKeyCallback(req);
//            if (!isDefaultHandler) {
            ProcessingContext.copy(skc.getRuntimeProperties(), context);
//            }
            Callback[] callbacks = new Callback[]{skc};
            try {
                callbackHandler.handle(callbacks);
            } catch (Exception e) {
                log.log(Level.SEVERE, LogStringsMessages.WSS_0216_CALLBACKHANDLER_HANDLE_EXCEPTION("SignatureKeyCallback.PublicKeyBasedPrivKeyCertRequest"),
                        new Object[]{"SignatureKeyCallback.PublicKeyBasedPrivKeyCertRequest"});
                log.log(Level.SEVERE, LogStringsMessages.WSS_0217_CALLBACKHANDLER_HANDLE_EXCEPTION_LOG(), e);
                throw new XWSSecurityException(e);
            }
            return req.getPrivateKey();
        } else {            
            DecryptionKeyCallback.PublicKeyBasedPrivKeyRequest req =
                    new DecryptionKeyCallback.PublicKeyBasedPrivKeyRequest(publicKey);
            DecryptionKeyCallback dkc = new DecryptionKeyCallback(req);
//            if (!isDefaultHandler) {
            ProcessingContext.copy(dkc.getRuntimeProperties(), context);
//            }
            Callback[] callbacks = new Callback[]{dkc};
            try {
                callbackHandler.handle(callbacks);
            } catch (Exception e) {
                log.log(Level.SEVERE, LogStringsMessages.WSS_0216_CALLBACKHANDLER_HANDLE_EXCEPTION("DecryptionKeyCallback.PublicKeyBasedPrivKeyRequest"),
                        new Object[]{"DecryptionKeyCallback.PublicKeyBasedPrivKeyRequest"});
                log.log(Level.SEVERE, LogStringsMessages.WSS_0217_CALLBACKHANDLER_HANDLE_EXCEPTION_LOG(), e);
                throw new XWSSecurityException(e);
            }
            return req.getPrivateKey();
        }
    }

    @Override
    public Subject getSubject() {
        log.log(Level.SEVERE, LogStringsMessages.WSS_0224_UNSUPPORTED_ASSOCIATED_SUBJECT());
        throw new UnsupportedOperationException(
                "This environment does not have an associated Subject");
    }

    @Override
    public boolean authenticateUser(
            Map context,
            String username,
            String passwordDigest,
            String nonce,
            String created)
            throws XWSSecurityException {

        PasswordValidationCallback.DigestPasswordRequest request =
                new PasswordValidationCallback.DigestPasswordRequest(
                username, passwordDigest, nonce, created);
        PasswordValidationCallback passwordValidationCallback =
                new PasswordValidationCallback(request);
        ProcessingContext.copy(passwordValidationCallback.getRuntimeProperties(), context);
        Callback[] callbacks = new Callback[]{passwordValidationCallback};

        boolean result = false;
        try {
            callbackHandler.handle(callbacks);
            RealmAuthenticationAdapter adapter = passwordValidationCallback.getRealmAuthenticationAdapter();
            if (passwordValidationCallback.getValidator() != null) {
                result = passwordValidationCallback.getResult();
                if (result == true) {
                    updateUsernameInSubject(getSubject(context), username, null);
                }
            } else if (adapter != null) {
                result = adapter.authenticate(getSubject(context), username, passwordDigest, nonce, created, context);
            } else {
                log.log(Level.SEVERE,LogStringsMessages.WSS_0295_PASSWORD_VAL_NOT_CONFIG_USERNAME_VAL());
                throw new XWSSecurityException("Error: No PasswordValidator Configured for UsernameToken Validation");
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, LogStringsMessages.WSS_0225_FAILED_PASSWORD_VALIDATION_CALLBACK(), e);
            throw new XWSSecurityException(e);
        }
        if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "Username Authentication done for {0}", username);
        }
        return result;
    }

    @Override
    public boolean authenticateUser(Map context, String username, String password)
            throws XWSSecurityException {

        PasswordValidationCallback.PlainTextPasswordRequest request =
                new PasswordValidationCallback.PlainTextPasswordRequest(username, password);
        PasswordValidationCallback passwordValidationCallback =
                new PasswordValidationCallback(request);
        ProcessingContext.copy(passwordValidationCallback.getRuntimeProperties(), context);
        Callback[] callbacks = new Callback[]{passwordValidationCallback};
        boolean result = false;
        try {
            callbackHandler.handle(callbacks);
            RealmAuthenticationAdapter adapter = passwordValidationCallback.getRealmAuthenticationAdapter();
            if (passwordValidationCallback.getValidator() != null) {
                result = passwordValidationCallback.getResult();
                if (result == true) {
                    updateUsernameInSubject(getSubject(context), username, password);
                }
            } else if (adapter != null) {
                result = adapter.authenticate(getSubject(context), username, password, context);
            } else {
                log.log(Level.SEVERE, LogStringsMessages.WSS_0295_PASSWORD_VAL_NOT_CONFIG_USERNAME_VAL());
                throw new XWSSecurityException("Error: No PasswordValidator Configured for UsernameToken Validation");
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, LogStringsMessages.WSS_0225_FAILED_PASSWORD_VALIDATION_CALLBACK(), e);
            throw new XWSSecurityException(e);
        }
        return result;
    }

    @Override
    public String authenticateUser(Map context, String username )
            throws XWSSecurityException {

        PasswordValidationCallback.DerivedKeyPasswordRequest request =
                new PasswordValidationCallback.DerivedKeyPasswordRequest(username);
        PasswordValidationCallback passwordValidationCallback =
                new PasswordValidationCallback(request);
        ProcessingContext.copy(passwordValidationCallback.getRuntimeProperties(), context);
        Callback[] callbacks = new Callback[]{passwordValidationCallback};
        boolean result = false;
        try {
            callbackHandler.handle(callbacks);
            RealmAuthenticationAdapter adapter = passwordValidationCallback.getRealmAuthenticationAdapter();
            if (passwordValidationCallback.getValidator() != null) {
                result = passwordValidationCallback.getResult();

                if (result == true) {
                updateUsernameInSubject(getSubject(context), username, null);
                }
            } 
        } catch (Exception e) {
            log.log(Level.SEVERE, LogStringsMessages.WSS_0225_FAILED_PASSWORD_VALIDATION_CALLBACK(), e);
            throw new XWSSecurityException(e);
        }
        return request.getPassword();
    }

    //Default creation time validation code. This will be
    //executed when user does not provide TimestampVlidation.
    private void defaultValidateCreationTime(
            String creationTime,
            long maxClockSkew,
            long timestampFreshnessLimit)
            throws XWSSecurityException {

        Date created = null;
        try {
            synchronized (calendarFormatter1) {
                created = calendarFormatter1.parse(creationTime);
            }
        } catch (java.text.ParseException e) {
            synchronized (calendarFormatter2) {
                try {
                    created = calendarFormatter2.parse(creationTime);
                } catch (java.text.ParseException ex) {
                    log.log(Level.SEVERE, LogStringsMessages.WSS_0226_FAILED_VALIDATING_DEFAULT_CREATION_TIME(), ex);
                    throw new XWSSecurityException(ex);
                    
                }
            }
        }


        Date current = getFreshnessAndSkewAdjustedDate(maxClockSkew, timestampFreshnessLimit);

        if (created.before(current)) {
            log.log(Level.SEVERE, LogStringsMessages.WSS_0227_INVALID_OLDER_CREATION_TIME());
            throw SOAPUtil.newSOAPFaultException(MessageConstants.WSSE_INVALID_SECURITY_TOKEN,  "The creation time is older than " +
                    " currenttime - timestamp-freshness-limit - max-clock-skew", null, true);
        }

        Date currentTime =
                getGMTDateWithSkewAdjusted(new GregorianCalendar(), maxClockSkew, true);
        if (currentTime.before(created)) {
            log.log(Level.SEVERE, LogStringsMessages.WSS_0228_INVALID_AHEAD_CREATION_TIME());
            throw SOAPUtil.newSOAPFaultException(MessageConstants.WSSE_INVALID_SECURITY_TOKEN,
                    "The creation time is ahead of the current time.", null, true);
        }
    }

    /**
     *
     */
    @Override
    public void validateCreationTime(
            Map context,
            String creationTime,
            long maxClockSkew,
            long timestampFreshnessLimit)
            throws XWSSecurityException {

        TimestampValidationCallback.UTCTimestampRequest request =
                new TimestampValidationCallback.UTCTimestampRequest(
                creationTime,
                null,
                maxClockSkew,
                timestampFreshnessLimit);

        request.isUsernameToken(true);
        TimestampValidationCallback timestampValidationCallback =
                new TimestampValidationCallback(request);
        if (!isDefaultHandler) {
            ProcessingContext.copy(timestampValidationCallback.getRuntimeProperties(), context);
        }
        Callback[] callbacks = new Callback[]{timestampValidationCallback};
        boolean unSupported = false;
        try {
            callbackHandler.handle(callbacks);
        } catch (UnsupportedCallbackException e) {
            unSupported = true;
        } catch (Exception e) {
            log.log(Level.SEVERE, LogStringsMessages.WSS_0226_FAILED_VALIDATING_DEFAULT_CREATION_TIME());
            throw new XWSSecurityException(e);
        }
        
        if (unSupported) {
            defaultValidateCreationTime(creationTime, maxClockSkew, timestampFreshnessLimit);
            return;
        }

        try {
            timestampValidationCallback.getResult();
        } catch (TimestampValidationCallback.TimestampValidationException e) {
            log.log(Level.SEVERE, LogStringsMessages.WSS_0229_FAILED_VALIDATING_TIME_STAMP(), e);
            throw SOAPUtil.newSOAPFaultException(MessageConstants.WSSE_INVALID_SECURITY_TOKEN, e.getMessage(), e, true);
        }
    }

    //TODO implement this using callbacks
    public boolean validateSamlIssuer(String issuer) {
        log.log(Level.SEVERE, LogStringsMessages.WSS_0230_UNSUPPORTED_VALIDATING_SAML_ISSUER());
        throw new UnsupportedOperationException("SAML Issuer Validation not yet supported");
    }

    //TODO implement this using callbacks
    public boolean validateSamlUser(
            String user, String domain, String format) {
        log.log(Level.SEVERE, LogStringsMessages.WSS_0231_UNSUPPORTED_VALIDATING_SAML_USER());
        throw new UnsupportedOperationException("SAML User Validation not yet supported");
    }

    @Override
    public String getUsername(Map context) throws XWSSecurityException {
        UsernameCallback usernameCallback = new UsernameCallback();
        /*if (!isDefaultHandler) {*/
        ProcessingContext.copy(usernameCallback.getRuntimeProperties(), context);
        /*}*/
        Callback[] callbacks = new Callback[]{usernameCallback};
        try {
            callbackHandler.handle(callbacks);
        } catch (Exception e) {
            log.log(Level.SEVERE, LogStringsMessages.WSS_0216_CALLBACKHANDLER_HANDLE_EXCEPTION("UsernameCallback"),
                    new Object[]{"UsernameCallback"});
            log.log(Level.SEVERE, LogStringsMessages.WSS_0217_CALLBACKHANDLER_HANDLE_EXCEPTION_LOG(), e);
            throw new XWSSecurityException(e);
        }
        return usernameCallback.getUsername();
    }

    @Override
    public String getPassword(Map context) throws XWSSecurityException {
        PasswordCallback passwordCallback = new PasswordCallback();
        /*if (!isDefaultHandler) {*/
        ProcessingContext.copy(passwordCallback.getRuntimeProperties(), context);
        /*}*/
        Callback[] callbacks = new Callback[]{passwordCallback};
        try {
            callbackHandler.handle(callbacks);
        } catch (Exception e) {
            log.log(Level.SEVERE, LogStringsMessages.WSS_0225_FAILED_PASSWORD_VALIDATION_CALLBACK(), e);
            throw new XWSSecurityException(e.getMessage(), e);
        }
        return passwordCallback.getPassword();
    }

    //Default expiration time validation code. This will be
    //executed when user does not provide TimestampVlidation.
    private void defaultValidateExpirationTime(
            String expirationTime, long maxClockSkew, long timestampFreshnessLimit) {

        if (expirationTime != null) {
            Date expires=null;
            try {
                synchronized (calendarFormatter1) {
                    expires = calendarFormatter1.parse(expirationTime);
                }
            } catch (java.text.ParseException pe) {
                synchronized (calendarFormatter2) {
                    try {
                        expires = calendarFormatter2.parse(expirationTime);
                    } catch (java.text.ParseException e) {
                        log.log(Level.SEVERE, LogStringsMessages.WSS_0394_ERROR_PARSING_EXPIRATIONTIME());
                        throw SOAPUtil.newSOAPFaultException(MessageConstants.WSSE_INVALID_SECURITY_TOKEN, e.getMessage(), e, true);
                    }
                }
            }


            Date currentTime =
                    getGMTDateWithSkewAdjusted(new GregorianCalendar(), maxClockSkew, false);
            if (expires.before(currentTime)) {
                log.log(Level.SEVERE, LogStringsMessages.WSS_0393_CURRENT_AHEAD_OF_EXPIRES());
                throw SOAPUtil.newSOAPFaultException(MessageConstants.WSU_MESSAGE_EXPIRED,"The current time is ahead of the expiration time in Timestamp",null, true);
            }
        }

    }

    @Override
    public void validateTimestamp(
            Map context, Timestamp timestamp, long maxClockSkew, long freshnessLimit)
            throws XWSSecurityException {
        validateTimestamp(context, timestamp.getCreated(), timestamp.getExpires(),
                maxClockSkew, freshnessLimit);
    }

    @Override
    public void validateTimestamp(Map context, String created,
                                  String expires, long maxClockSkew, long freshnessLimit)
            throws XWSSecurityException {
        if (expiresBeforeCreated(created, expires)) {
            XWSSecurityException xwsse = new XWSSecurityException("Message expired!");
            log.log(Level.SEVERE, LogStringsMessages.WSS_0232_EXPIRED_MESSAGE());
            throw newSOAPFaultException(
                    MessageConstants.WSU_MESSAGE_EXPIRED,
                    "Message expired!",
                    xwsse);
        }

        TimestampValidationCallback.UTCTimestampRequest request =
                new TimestampValidationCallback.UTCTimestampRequest(
                created,
                expires,
                maxClockSkew,
                freshnessLimit);

        TimestampValidationCallback timestampValidationCallback =
                new TimestampValidationCallback(request);
        if (!isDefaultHandler) {
            ProcessingContext.copy(timestampValidationCallback.getRuntimeProperties(), context);
        }
        Callback[] callbacks = new Callback[]{timestampValidationCallback};
        boolean unSupported = false;
        try {
            callbackHandler.handle(callbacks);
        } catch (UnsupportedCallbackException e) {
            unSupported = true;    
        } catch (Exception e) {
            log.log(Level.SEVERE,LogStringsMessages.WSS_0229_FAILED_VALIDATING_TIME_STAMP(), e);
            throw new XWSSecurityException(e);
        }

        if (unSupported) {
            //System.out.println("Validate Timestamp ...");
            defaultValidateCreationTime(created, maxClockSkew, freshnessLimit);
            defaultValidateExpirationTime(expires, maxClockSkew, freshnessLimit);
            return;
        }
        
        try {
            timestampValidationCallback.getResult();
        } catch (TimestampValidationCallback.TimestampValidationException e) {
            log.log(Level.SEVERE, LogStringsMessages.WSS_0229_FAILED_VALIDATING_TIME_STAMP(), e);
            throw SOAPUtil.newSOAPFaultException(MessageConstants.WSSE_INVALID_SECURITY_TOKEN, e.getMessage(), e, true);
        }
    }

    /**
     * Create and initialize a WssSoapFaultException. This method is used in
     * conjunction with generateClientFault.
     */
    public static WssSoapFaultException newSOAPFaultException(
            QName faultCode,
            String faultstring,
            Throwable th) {
        WssSoapFaultException sfe =
                new WssSoapFaultException(faultCode, faultstring, null, null);
        sfe.initCause(th);
        return sfe;
    }

    private static Date getGMTDateWithSkewAdjusted(
            Calendar c, long maxClockSkew, boolean addSkew) {
        //long offset = c.get(Calendar.ZONE_OFFSET);
        //if (c.getTimeZone().inDaylightTime(c.getTime())) {
        //    offset += c.getTimeZone().getDSTSavings();
        //}
        long beforeTime = c.getTimeInMillis();
        long currentTime = beforeTime - offset;

        if (addSkew) {
            currentTime = currentTime + maxClockSkew;
        } else {
            currentTime = currentTime - maxClockSkew;
        }

        c.setTimeInMillis(currentTime);
        return c.getTime();
    }

    private static Date getFreshnessAndSkewAdjustedDate(
            long maxClockSkew, long timestampFreshnessLimit) {
        Calendar c = new GregorianCalendar();
        //long offset = c.get(Calendar.ZONE_OFFSET);
        //if (c.getTimeZone().inDaylightTime(c.getTime())) {
        //    offset += c.getTimeZone().getDSTSavings();
        //}
        long beforeTime = c.getTimeInMillis();
        long currentTime = beforeTime - offset;

        // allow for clock_skew and timestamp_freshness
        long adjustedTime = currentTime - maxClockSkew - timestampFreshnessLimit;
        c.setTimeInMillis(adjustedTime);

        return c.getTime();
    }

    private boolean expiresBeforeCreated(
            String creationTime, String expirationTime) throws XWSSecurityException {
        Date created = null;
        Date expires = null;
        try {
            synchronized (calendarFormatter1) {
                created = calendarFormatter1.parse(creationTime);
                if (expirationTime != null) {
                    expires = calendarFormatter1.parse(expirationTime);
                }
            }
        } catch (java.text.ParseException pe) {
            synchronized (calendarFormatter2) {
                try {
                    created = calendarFormatter2.parse(creationTime);
                    if (expirationTime != null) {
                        expires = calendarFormatter2.parse(expirationTime);
                    }
                } catch (java.text.ParseException xpe) {
                    log.log(Level.SEVERE, LogStringsMessages.WSS_0233_INVALID_EXPIRE_BEFORE_CREATION(), xpe);
                    throw new XWSSecurityException(xpe.getMessage());
                }
            }
        }


        if ((expires != null) && expires.equals(created)) {
            return true;
        }

        if ((expires != null) && expires.before(created)) {
            return true;
        }

        return false;
    }

    
    @Override
    public void validateSAMLAssertion(Map context, Element assertion) throws XWSSecurityException {

        AuthenticationTokenPolicy authPolicy = new AuthenticationTokenPolicy();
        AuthenticationTokenPolicy.SAMLAssertionBinding samlPolicy =
                (AuthenticationTokenPolicy.SAMLAssertionBinding) authPolicy.newSAMLAssertionFeatureBinding();
        samlPolicy.setAssertion(assertion);

        DynamicPolicyCallback dynamicCallback =
                new DynamicPolicyCallback(samlPolicy, null);
        //let runtime properties be visible here
//        if (!isDefaultHandler) {
        ProcessingContext.copy(dynamicCallback.getRuntimeProperties(), context);
//        } else {
//            dynamicCallback.getRuntimeProperties().
//                    put(MessageConstants.AUTH_SUBJECT, context.get(MessageConstants.AUTH_SUBJECT));
//        }
        try {
            Callback[] callbacks = new Callback[]{dynamicCallback};
            callbackHandler.handle(callbacks);
        } catch (Exception e) {
            log.log(Level.SEVERE,LogStringsMessages.WSS_0234_FAILED_VALIDATE_SAML_ASSERTION(), e);
            throw SOAPUtil.newSOAPFaultException(
                    MessageConstants.WSSE_FAILED_AUTHENTICATION,
                    "Validation failed for SAML Assertion ", e, true);
        }
    }

    @Override
    public Element locateSAMLAssertion(Map context, Element binding, String assertionId, Document ownerDoc)
            throws XWSSecurityException {

        AuthenticationTokenPolicy authPolicy = new AuthenticationTokenPolicy();
        AuthenticationTokenPolicy.SAMLAssertionBinding samlPolicy =
                (AuthenticationTokenPolicy.SAMLAssertionBinding) authPolicy.newSAMLAssertionFeatureBinding();
        samlPolicy.setAuthorityBinding(binding);
        samlPolicy.setAssertionId(assertionId);

        DynamicPolicyCallback dynamicCallback =
                new DynamicPolicyCallback(samlPolicy, null);
        //let runtime props be visible here
//        if (!isDefaultHandler) {
            ProcessingContext.copy(dynamicCallback.getRuntimeProperties(), context);
//        }
        try {
            Callback[] callbacks = new Callback[]{dynamicCallback};
            callbackHandler.handle(callbacks);
        } catch (Exception e) {
            log.log(Level.SEVERE, LogStringsMessages.WSS_0235_FAILED_LOCATE_SAML_ASSERTION(), e);
            throw new XWSSecurityException(e);
        }
        Element assertion = samlPolicy.getAssertion();
        if (assertion == null) {
            log.log(Level.SEVERE, LogStringsMessages.WSS_0236_NULL_SAML_ASSERTION());
            throw new XWSSecurityException("SAML Assertion not set into Policy by CallbackHandler");
        }

        return assertion;
    }

    @Override
    public AuthenticationTokenPolicy.SAMLAssertionBinding populateSAMLPolicy(Map fpcontext, AuthenticationTokenPolicy.SAMLAssertionBinding policy,
                                                                             DynamicApplicationContext context)
            throws XWSSecurityException {

        DynamicPolicyCallback dynamicCallback =
                new DynamicPolicyCallback(policy, context);
        if (context != null /* && !isDefaultHandler*/) {
            ProcessingContext.copy(dynamicCallback.getRuntimeProperties(), fpcontext);
        }
        try {
            Callback[] callbacks = new Callback[]{dynamicCallback};
            callbackHandler.handle(callbacks);
        } catch (Exception e) {
            log.log(Level.SEVERE, LogStringsMessages.WSS_0237_FAILED_DYNAMIC_POLICY_CALLBACK(), e);
            throw new XWSSecurityException(e);
        }
        return (AuthenticationTokenPolicy.SAMLAssertionBinding) dynamicCallback.getSecurityPolicy();
    }

    @Override
    public CallbackHandler getCallbackHandler() {
        return callbackHandler;
    }
    
    private void validateSamlVersion(Assertion assertion) {
        BigInteger major = assertion.getMajorVersion();
        BigInteger minor = assertion.getMinorVersion();

        if (major.intValue() != 1) {
            log.log(Level.SEVERE, LogStringsMessages.WSS_0404_SAML_INVALID_VERSION());
            throw SecurableSoapMessage.newSOAPFaultException(
                    MessageConstants.WSSE_INVALID_SECURITY_TOKEN,
                    "Major version is not 1 for SAML Assertion:" + assertion.getAssertionID(),
                    new Exception(
                    "Major version is not 1 for SAML Assertion"));
        }

        if ((minor.intValue() != 0) && (minor.intValue() != 1)) {
            log.log(Level.SEVERE, LogStringsMessages.WSS_0404_SAML_INVALID_VERSION());
            throw SecurableSoapMessage.newSOAPFaultException(
                    MessageConstants.WSSE_INVALID_SECURITY_TOKEN,
                    "Minor version is not 0/1 for SAML Assertion:" + assertion.getAssertionID(),
                    new Exception(
                    "Minor version is not 0/1 for SAML Assertion"));
        }
    }

    private void validateIssuer(
            SecurableSoapMessage secMessage,
            Assertion assertion) {
    }

    private void validateSamlUser(
            SecurableSoapMessage secMessage,
            Assertion assertion) {
        String user = null;

    }

    @Override
    @SuppressWarnings("unchecked")
    public void validateSAMLAssertion(Map context, XMLStreamReader assertion) throws XWSSecurityException {

        AuthenticationTokenPolicy authPolicy = new AuthenticationTokenPolicy();
        AuthenticationTokenPolicy.SAMLAssertionBinding samlPolicy =
                (AuthenticationTokenPolicy.SAMLAssertionBinding) authPolicy.newSAMLAssertionFeatureBinding();
        samlPolicy.setAssertion(assertion);

        DynamicPolicyCallback dynamicCallback =
                new DynamicPolicyCallback(samlPolicy, null);
//        if (!isDefaultHandler) {
            ProcessingContext.copy(dynamicCallback.getRuntimeProperties(), context);
//        } else {
            if (context.get(MessageConstants.AUTH_SUBJECT) == null) {
            dynamicCallback.getRuntimeProperties().
                    put(MessageConstants.AUTH_SUBJECT, getSubject(context));
            }
//        }
        try {
            Callback[] callbacks = new Callback[]{dynamicCallback};
            callbackHandler.handle(callbacks);
        } catch (Exception e) {
            log.log(Level.SEVERE, LogStringsMessages.WSS_0234_FAILED_VALIDATE_SAML_ASSERTION(), e);
            throw SOAPUtil.newSOAPFaultException(
                    MessageConstants.WSSE_FAILED_AUTHENTICATION,
                    "Validation failed for SAML Assertion ", e, true);
        }
    }

    @Override
    public void updateOtherPartySubject(final Subject subject, final XMLStreamReader assertion) {
        if (callbackHandler instanceof DefaultCallbackHandler) {
                if (((DefaultCallbackHandler)callbackHandler).getSAMLValidator() 
                        instanceof SAMLValidator)
                return;
        }
        AccessController.doPrivileged(new PrivilegedAction<>() {
            @Override
            public Object run() {
                subject.getPublicCredentials().add(assertion);
                return null; // nothing to return
            }
        });
    }

    @Override
    public boolean isSelfCertificate(X509Certificate cert) {
//        if (this.selfCertificate != null && this.selfCertificate.equals(cert)) {
//            return true;
//        }
        return false;
    }

    @Override
    public void updateOtherPartySubject(Subject subject, Subject bootStrapSubject) {
        SecurityUtil.copySubject(subject, bootStrapSubject);
    }

    @Override
    public KerberosContext doKerberosLogin() throws XWSSecurityException {
        String loginModule = configAssertions.getProperty(DefaultCallbackHandler.KRB5_LOGIN_MODULE);
        String servicePrincipal = configAssertions.getProperty(DefaultCallbackHandler.KRB5_SERVICE_PRINCIPAL);
        boolean credentialDelegation = Boolean.valueOf(configAssertions.getProperty(DefaultCallbackHandler.KRB5_CREDENTIAL_DELEGATION));
        if (loginModule == null || loginModule.equals("")) {
            throw new XWSSecurityException("Login Module for Kerberos login is not set or could not be obtained");
        }
        if (servicePrincipal == null || servicePrincipal.equals("")) {
            throw new XWSSecurityException("Kerberos Service Principal is not set or could not be obtained");
        }
        return new KerberosLogin().login(loginModule, servicePrincipal, credentialDelegation);
    }

    @Override
    public KerberosContext doKerberosLogin(byte[] tokenValue) throws XWSSecurityException {
        String loginModule = configAssertions.getProperty(DefaultCallbackHandler.KRB5_LOGIN_MODULE);
        return new KerberosLogin().login(loginModule, tokenValue);
    }
    
    @Override
    public void updateOtherPartySubject(final Subject subject,
                                        final GSSName clientCred,
                                        final GSSCredential gssCred) {
        AccessController.doPrivileged(new PrivilegedAction<>() {
            @Override
            public Object run() {
                KerberosPrincipal kerbPrincipal = new KerberosPrincipal(clientCred.toString());
                subject.getPrincipals().add(kerbPrincipal);
                subject.getPublicCredentials().add(clientCred);
                if (gssCred != null) {
                    subject.getPrivateCredentials().add(gssCred);
                }
                return null; // nothing to return
            }
        });
    }

    @Override
    public boolean validateAndCacheNonce(Map context, String nonce, String created, long nonceAge) throws XWSSecurityException {
        NonceManager nonceMgr = null;
        if (this.mnaProperty != null) {
            nonceMgr = NonceManager.getInstance(this.maxNonceAge, (WSEndpoint)context.get(MessageConstants.WSENDPOINT));
        } else {
            nonceMgr = NonceManager.getInstance(nonceAge, (WSEndpoint)context.get(MessageConstants.WSENDPOINT));
        }   
        
        return nonceMgr.validateNonce(nonce, created);
    }


    private Class loadClass(String classname) throws XWSSecurityException {
        if (classname == null) {
            return null;
        }
        Class ret = null;
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader != null) {
            try {
                ret = loader.loadClass(classname);
                return ret;
            } catch (ClassNotFoundException e) {
                // ignore
                if (log.isLoggable(Level.FINE)) {
                    log.log(Level.FINE, "LoadClass: could not load class " + classname, e);
                }
            }
        }
        // if context classloader didnt work, try this
        loader = this.getClass().getClassLoader();
        try {
            ret = loader.loadClass(classname);
            return ret;
        } catch (ClassNotFoundException e) {
            // ignore
            if (log.isLoggable(Level.FINE)) {
                log.log(Level.FINE, "LoadClass: could not load class " + classname, e);
            }
        }
        log.log(Level.SEVERE, com.sun.xml.wss.logging.impl.misc.LogStringsMessages.WSS_1521_ERROR_GETTING_USER_CLASS());
        throw new XWSSecurityException("Could not find User Class " + classname);
    }
       
        
}
