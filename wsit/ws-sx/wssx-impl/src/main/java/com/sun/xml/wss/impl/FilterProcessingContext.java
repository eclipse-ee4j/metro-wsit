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
 * $Id: FilterProcessingContext.java,v 1.2 2010-10-21 15:37:15 snajper Exp $
 */

package com.sun.xml.wss.impl;

import java.util.HashMap;

import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.xml.soap.SOAPMessage;

import com.sun.xml.wss.logging.LogDomainConstants;

import com.sun.xml.wss.impl.policy.mls.WSSPolicy;
import com.sun.xml.wss.impl.policy.SecurityPolicy;

import com.sun.xml.wss.impl.policy.mls.AuthenticationTokenPolicy;
import com.sun.xml.wss.impl.policy.mls.SymmetricKeyBinding;
import com.sun.xml.wss.*;

import javax.crypto.SecretKey;
import java.security.Key;
import org.w3c.dom.Node;

public class FilterProcessingContext extends ProcessingContextImpl {

    /**
     * Processing modes indicate if policy resolution happened
     * that can be applied on the message.
     *
     * ADHOC indicates that policy is available and its elements
     * are applied to the message as-is, with specific handling
     * for secondary policies.
     *
     *
     * POSTHOC indicates that the header be processed as-is, without
     * assuming a security policy. In such cases, security policy
     * inferred from the message is validated post-hoc with one
     * that is resolved later.
     *
     *
     * DEFAULT indicates that no security policy is available for
     * processing and the header is processed as-is, with specific
     * handling for secondary header elements.
     *
     * WSDL_POLICY indicates a policy is to obtained from WSDL, but is
     * is currently not known due to Encrypted Body
     *
     */
    public static final int ADHOC = 0;
    public static final int POSTHOC = 1;
    public static final int DEFAULT = 2;
    public static final int WSDL_POLICY = 3;
    private byte[] digestValue = null;
    private byte[] canonicalizedData = null;
    /**
     * Default mode
     */
    private int mode = DEFAULT;
    /**
     * Filters would not throw exceptions for primary or optional
     * policy violations and rather would set the Throwable instance,
     * representing the violation, in FPC.
     *
     * NOTE: Tweak in the design for achieving cleaner handling
     * for certain aspects of the specification and implementation
     * viz.a.viz., flexibility of occurrence of secondary header
     * elements and optional policies.
     */
    private boolean primaryPolicyViolation = false;
    private boolean optionalPolicyViolation = false;
    private Throwable _PolicyViolation = null;
    /**
     * If DynamicPolicyCallback handling should be enabled
     */
    private boolean enableDynamicPolicyCallback = false;
    /**
     * Cache of BinarySecurityToken(s)
     *
     * Cache look-up happens for cases when the token is referenced
     * twice in the same message. Note that in 2.0 implementation
     * tokens are de-referenced in lazy fashion, only upon encountering
     * references to them.
     *
     * For sender side optimizations concerning reducing the export of
     * the token to a single occasion per message, the Cache would require
     * more dimensions (>2).
     */
    private HashMap tokenCache = new HashMap();
    /**
     * Cache for looking up EncryptedKey ids against X509 id
     */
    private HashMap encryptedKeyCache = new HashMap();
    /**
     * Cache for storing all the X509 tokens that are inserted into the Security Header
     */
    private HashMap insertedX509Cache = new HashMap();
    /**
     * List of processed SOAP Attachment wsu:Id(s)
     *
     * TODO: Not required. Use SecurityHeader.setCurrentHeaderElement rather
     * private ArrayList processedAttachments = new ArrayList();
     */
    //hack to allow only a single timestamp to be exported
    // TODO : revisit
    private boolean timestampExported = false;
    //Cache to maintain a list of elements vs id attributes
    //
    private HashMap elementCache = new HashMap();
    //x509 key binding
    private AuthenticationTokenPolicy.X509CertificateBinding x509CertificateBinding;
    private AuthenticationTokenPolicy.KerberosTokenBinding kerberosTokenBinding;
    private AuthenticationTokenPolicy.UsernameTokenBinding usernameTokenBinding;
    private WSSPolicy inferredPolicy = null;
    //symmetric key binding
    private SymmetricKeyBinding symmetricKeyBinding;
    private String dataEncAlgo = null;
    private SecretKey currentSecret = null;
    //added to handle Encrypt Before Signing
    private Node currentRefList = null;
    private static Logger log = Logger.getLogger(
            LogDomainConstants.WSS_API_DOMAIN,
            LogDomainConstants.WSS_API_DOMAIN_BUNDLE);
    private HashMap strTransformCache = new HashMap();

    public FilterProcessingContext() {
    }

    /**
     * @param context ProcessingContext
     *
     */
    public FilterProcessingContext(ProcessingContext context)
            throws XWSSecurityException {
        copy(this, context);
    }

    /**
     * @param filterMode boolean
     * @param messageIdentifier String
     * @param securityPolicy SecurityPolicy
     * @param message SOAPMessage
     *
     */
    public FilterProcessingContext(int filterMode,
            String messageIdentifier,
            SecurityPolicy securityPolicy,
            SOAPMessage message)
            throws XWSSecurityException {

        this.mode = filterMode;
        setSecurityPolicy(securityPolicy);
        setMessageIdentifier(messageIdentifier);
        setSOAPMessage(message);
    }

    /**
     * Overrides setSecurityPolicy in PC - allows only WSSPolicy
     * instances to be set. Resets internal state of FPC.
     *
     * @param policy SecurityPolicy
     *
     */
    @Override
    public void setSecurityPolicy(SecurityPolicy policy)
            throws XWSSecurityException {
        primaryPolicyViolation = false;
        optionalPolicyViolation = false;

        _PolicyViolation = null;

        if (!(policy instanceof WSSPolicy) && !(PolicyTypeUtil.messagePolicy(policy)) && !(PolicyTypeUtil.applicationSecurityConfiguration(policy)) &&
                !(PolicyTypeUtil.dynamicSecurityPolicy(policy))) {
            log.log(Level.SEVERE, "WSS0801.illegal.securitypolicy");
            throw new XWSSecurityException(
                    "Illegal SecurityPolicy Type: required one of " +
                    " WSSPolicy/MessagePolicy/ApplicationSecurityConfiguration");
        }

        super.setSecurityPolicy(policy);
    }

    /**
     * @param exception Throwable representing exception for policy violation
     */
    public void setPVE(Throwable exception) {
        _PolicyViolation = exception;
    }

    /**
     * @return _policyViolation
     */
    public Throwable getPVE() {
        return _PolicyViolation;
    }

    /**
     * @param mode set filter processing mode
     */
    public void setMode(int mode) {
        this.mode = mode;
    }

    /**
     * @return mode
     */
    public int getMode() {
        return this.mode;
    }

    /**
     * @param enable boolean
     */
    public void enableDynamicPolicyCallback(boolean enable) {
        this.enableDynamicPolicyCallback = enable;
    }

    /**
     * @return enableDynamicPolicyCallback
     */
    public boolean makeDynamicPolicyCallback() {
        return this.enableDynamicPolicyCallback;
    }

    /**
     */
    public void isPrimaryPolicyViolation(boolean assrt) {
        this.primaryPolicyViolation = assrt;
    }

    /**
     * @return primaryPolicyViolation
     */
    public boolean isPrimaryPolicyViolation() {
        return primaryPolicyViolation;
    }

    /**
     */
    public void isOptionalPolicyViolation(boolean assrt) {
        this.optionalPolicyViolation = assrt;
    }

    /**
     * @return optionalPolicyViolation
     */
    public boolean isOptionalPolicyViolation() {
        return optionalPolicyViolation;
    }

    /**
     * return the token cache.
     */
    public HashMap getTokenCache() {
        return tokenCache;
    }

    /**
     * return the encryptedKey Cache
     */
    public HashMap getEncryptedKeyCache() {
        return encryptedKeyCache;
    }

    public HashMap getInsertedX509Cache() {
        return insertedX509Cache;
    }

    /*
     *Set if a Timestamp was exported
     */
    public void timestampExported(boolean flag) {
        timestampExported = flag;
    }

    /*
     *@return true if a Timestamp was exported
     */
    public boolean timestampExported() {
        return timestampExported;
    }

    public HashMap getElementCache() {
        return elementCache;
    }

    public HashMap getSTRTransformCache() {
        return strTransformCache;
    }

    public void setX509CertificateBinding(
            AuthenticationTokenPolicy.X509CertificateBinding x509CertificateBinding) {
        this.x509CertificateBinding = x509CertificateBinding;
    }

    public AuthenticationTokenPolicy.X509CertificateBinding getX509CertificateBinding() {
        return x509CertificateBinding;
    }

    public void setUsernameTokenBinding(
            AuthenticationTokenPolicy.UsernameTokenBinding untBinding) {
        this.usernameTokenBinding = untBinding;
    }

    public AuthenticationTokenPolicy.UsernameTokenBinding getusernameTokenBinding() {
        return usernameTokenBinding;
    }

    public void setKerberosTokenBinding(
            AuthenticationTokenPolicy.KerberosTokenBinding kerberosTokenBinding) {
        this.kerberosTokenBinding = kerberosTokenBinding;
    }

    public AuthenticationTokenPolicy.KerberosTokenBinding getKerberosTokenBinding() {
        return kerberosTokenBinding;
    }

    public void setSymmetricKeyBinding(SymmetricKeyBinding symmetricKeyBinding) {
        this.symmetricKeyBinding = symmetricKeyBinding;
    }

    public SymmetricKeyBinding getSymmetricKeyBinding() {
        return symmetricKeyBinding;
    }

    public void setDataEncryptionAlgorithm(String alg) {
        this.dataEncAlgo = alg;
    }

    public String getDataEncryptionAlgorithm() {
        return this.dataEncAlgo;
    }

    @Override
    public SecurableSoapMessage getSecurableSoapMessage() {
        return secureMessage;
    }

    @Override
    public void reset() {
        elementCache.clear();
        tokenCache.clear();
    }

    public WSSPolicy getInferredPolicy() {
        return inferredPolicy;
    }

    public void setInferredPolicy(WSSPolicy policy) {
        this.inferredPolicy = policy;
    }

    public byte[] getDigestValue() {
        return digestValue;
    }

    public void setDigestValue(byte[] digestValue) {
        this.digestValue = digestValue;
    }

    public byte[] getCanonicalizedData() {
        return canonicalizedData;
    }

    public void setCanonicalizedData(byte[] canonicalizedData) {
        this.canonicalizedData = canonicalizedData;
    }

    public void setCurrentSecret(Key secret) {
        this.currentSecret = (SecretKey) secret;
    }

    public SecretKey getCurrentSecret() {
        return this.currentSecret;
    }

    public Node getCurrentRefList() {
        return currentRefList;
    }

    public void setCurrentReferenceList(Node blk) {
        currentRefList = blk;
    }
}
