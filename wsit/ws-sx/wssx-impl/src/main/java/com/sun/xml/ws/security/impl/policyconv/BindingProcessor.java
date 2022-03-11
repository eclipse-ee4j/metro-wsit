/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.impl.policyconv;

import com.sun.istack.NotNull;
import com.sun.xml.ws.policy.PolicyException;
import com.sun.xml.ws.security.policy.Binding;
import com.sun.xml.ws.security.policy.EncryptedElements;
import com.sun.xml.ws.security.policy.EncryptedParts;
import com.sun.xml.ws.security.policy.EncryptedSupportingTokens;
import com.sun.xml.ws.security.policy.EndorsingEncryptedSupportingTokens;
import com.sun.xml.ws.security.policy.EndorsingSupportingTokens;
import com.sun.xml.ws.security.policy.SignedElements;
import com.sun.xml.ws.security.policy.SignedEncryptedSupportingTokens;
import com.sun.xml.ws.security.policy.SignedEndorsingEncryptedSupportingTokens;
import com.sun.xml.ws.security.policy.SignedEndorsingSupportingTokens;
import com.sun.xml.ws.security.policy.SignedParts;
import com.sun.xml.ws.security.policy.SignedSupportingTokens;
import com.sun.xml.ws.security.policy.SupportingTokens;
import com.sun.xml.ws.security.policy.WSSAssertion;
import com.sun.xml.wss.impl.MessageConstants;
import com.sun.xml.wss.impl.PolicyTypeUtil;
import com.sun.xml.wss.impl.policy.mls.AuthenticationTokenPolicy;
import com.sun.xml.wss.impl.policy.mls.DerivedTokenKeyBinding;
import com.sun.xml.wss.impl.policy.mls.EncryptionPolicy;
import com.sun.xml.wss.impl.policy.mls.EncryptionTarget;
import com.sun.xml.wss.impl.policy.mls.IssuedTokenKeyBinding;
import com.sun.xml.wss.impl.policy.mls.KeyBindingBase;
import com.sun.xml.wss.impl.policy.mls.SecureConversationTokenKeyBinding;
import com.sun.xml.wss.impl.policy.mls.SignaturePolicy;
import com.sun.xml.wss.impl.policy.mls.SignatureTarget;
import com.sun.xml.wss.impl.policy.mls.Target;
import com.sun.xml.wss.impl.policy.mls.TimestampPolicy;
import com.sun.xml.wss.impl.policy.mls.WSSPolicy;
import java.util.Vector;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.namespace.QName;

/**
 *
 * @author K.Venugopal@sun.com
 */
public abstract class BindingProcessor {

    protected String protectionOrder = Binding.SIGN_ENCRYPT;
    protected boolean isServer = false;
    protected boolean isIncoming = false;
    protected SignaturePolicy primarySP = null;
    protected EncryptionPolicy primaryEP = null;
    //current secondary encryption policy
    protected EncryptionPolicy sEncPolicy = null;
    protected SignaturePolicy sSigPolicy = null;
    protected XWSSPolicyContainer container = null;
    protected Vector<SignedParts> signedParts = null;
    protected Vector<EncryptedParts> encryptedParts = null;
    protected Vector<SignedElements> signedElements = null;
    protected Vector<EncryptedElements> encryptedElements = null;
    protected PolicyID pid = null;
    protected TokenProcessor tokenProcessor = null;
    protected IntegrityAssertionProcessor iAP = null;
    protected EncryptionAssertionProcessor eAP = null;
    private WSSAssertion wss11 = null;
    private boolean isIssuedTokenAsEncryptedSupportingToken = false;
    protected boolean foundEncryptTargets = false;

    /** Creates a new instance of BindingProcessor */
    public BindingProcessor() {
        this.pid = new PolicyID();
    }

    /*
    WSIT Configuration should not allow protect primary signature
    property to be set if we determine there will be no signature.
     */
    protected void protectPrimarySignature() throws PolicyException {
        if (primarySP == null) {
            return;
        }
        boolean encryptSignConfirm = (isServer && !isIncoming) || (!isServer && isIncoming);
        if (Binding.ENCRYPT_SIGN.equals(protectionOrder)) {
            EncryptionPolicy ep = getSecondaryEncryptionPolicy();
            EncryptionPolicy.FeatureBinding epFB = (EncryptionPolicy.FeatureBinding) ep.getFeatureBinding();
            EncryptionTarget et = eAP.getTargetCreator().newURIEncryptionTarget(primarySP.getUUID());
            SecurityPolicyUtil.setName(et, primarySP);
            epFB.addTargetBinding(et);
            if (foundEncryptTargets && (isWSS11() && requireSC()) && encryptSignConfirm && getBinding().getSignatureProtection()) {
                eAP.process(Target.SIGNATURE_CONFIRMATION, epFB);
            }
        } else {
            EncryptionPolicy.FeatureBinding epFB = (EncryptionPolicy.FeatureBinding) primaryEP.getFeatureBinding();
            EncryptionTarget et = eAP.getTargetCreator().newURIEncryptionTarget(primarySP.getUUID());
            SecurityPolicyUtil.setName(et, primarySP);
            epFB.addTargetBinding(et);
            if (foundEncryptTargets && (isWSS11() && requireSC()) && encryptSignConfirm && getBinding().getSignatureProtection()) {
                eAP.process(Target.SIGNATURE_CONFIRMATION, epFB);
            }
        }
    }

    protected void protectTimestamp(TimestampPolicy tp) {
        if (primarySP != null) {
            SignatureTarget target = iAP.getTargetCreator().newURISignatureTarget(tp.getUUID());
            iAP.getTargetCreator().addTransform(target);
            SecurityPolicyUtil.setName(target, tp);
            SignaturePolicy.FeatureBinding spFB = (SignaturePolicy.FeatureBinding) primarySP.getFeatureBinding();
            spFB.addTargetBinding(target);
        }
    }

    //TODO:WS-SX Spec:If we have a secondary signature should it protect the token too ?
    protected void protectToken(WSSPolicy token) {
        if (primarySP == null) {
            return;
        }
        if ((isServer && isIncoming) || (!isServer && !isIncoming)) {//token protection is from client to service only
            protectToken(token, false);
        }
    }

    protected void protectToken(@NotNull WSSPolicy token, boolean ignoreSTR) {
        String uuid = token.getUUID();
        String uid = null;
        String includeToken = ((KeyBindingBase) token).getIncludeToken();
        boolean strIgnore = false;
        QName qName = null;

        //dont compute STR Transform when the include token type is Always or AlwaysToRecipient
        if (includeToken.endsWith("Always") || includeToken.endsWith("AlwaysToRecipient") || includeToken.endsWith("Once")) {
            strIgnore = true;
        }
        if (PolicyTypeUtil.usernameTokenBinding(token)) {
            uid = token.getUUID();
            if (uid == null) {
                uid = pid.generateID();
                ((AuthenticationTokenPolicy.UsernameTokenBinding) token).setSTRID(uid);
            }
            // includeToken = ((AuthenticationTokenPolicy.UsernameTokenBinding) kb).getIncludeToken();
            strIgnore = true;
            qName = new QName(MessageConstants.WSSE_NS, MessageConstants.USERNAME_TOKEN_LNAME);
        } else if (PolicyTypeUtil.x509CertificateBinding(token)) {
            uid = ((AuthenticationTokenPolicy.X509CertificateBinding) token).getSTRID();
            if (uid == null) {
                uid = pid.generateID();
                ((AuthenticationTokenPolicy.X509CertificateBinding) token).setSTRID(uid);
            }
            qName = new QName(MessageConstants.WSSE_NS, MessageConstants.WSSE_BINARY_SECURITY_TOKEN_LNAME);
        } else if (PolicyTypeUtil.samlTokenPolicy(token)) {
            //uid = ((AuthenticationTokenPolicy.SAMLAssertionBinding) token).getSTRID();
            uid = generateSAMLSTRID();
            //if(uid == null){
            // uid = pid.generateID();
            ((AuthenticationTokenPolicy.SAMLAssertionBinding) token).setSTRID(uid);
            //}
            qName = new QName(MessageConstants.WSSE_NS, MessageConstants.SAML_ASSERTION_LNAME);
        } else if (PolicyTypeUtil.issuedTokenKeyBinding(token)) {
            IssuedTokenKeyBinding itb = ((IssuedTokenKeyBinding) token);
            uid = itb.getSTRID();
            if (MessageConstants.WSSE_SAML_v1_1_TOKEN_TYPE.equals(itb.getTokenType()) ||
                    MessageConstants.WSSE_SAML_v2_0_TOKEN_TYPE.equals(itb.getTokenType())) {
                uid = generateSAMLSTRID();
                itb.setSTRID(uid);
                uuid = uid;
            }
            if (uid == null) {
                uid = pid.generateID();
                itb.setSTRID(uid);
            }
        } else if (PolicyTypeUtil.secureConversationTokenKeyBinding(token)) {
            SecureConversationTokenKeyBinding sctBinding = (SecureConversationTokenKeyBinding) token;
            //sctBinding TODO ::Fix this incomplete code
        }

        //when the include token is Never , the sig. reference should refer to the security token reference of KeyInfo
        // also in case of saml token we have to use the id #_SAML, so ,
        if (includeToken.endsWith("Never") || PolicyTypeUtil.samlTokenPolicy(token) || PolicyTypeUtil.issuedTokenKeyBinding(token)) {
            uuid = uid;
        }
        //TODO:: Handle DTK and IssuedToken.
        if (!ignoreSTR) {
            if (uuid != null) {
                SignatureTargetCreator stc = iAP.getTargetCreator();
                SignatureTarget st = stc.newURISignatureTarget(uuid);
                if (!strIgnore) {
                    stc.addSTRTransform(st);
                    st.setPolicyQName(qName);
                }else {
                    stc.addTransform(st);
                }
                SignaturePolicy.FeatureBinding fb = (com.sun.xml.wss.impl.policy.mls.SignaturePolicy.FeatureBinding) primarySP.getFeatureBinding();
                fb.addTargetBinding(st);
            }
        } else {
            SignatureTargetCreator stc = iAP.getTargetCreator();
            SignatureTarget st = null;
            if (PolicyTypeUtil.derivedTokenKeyBinding(token)) {
                WSSPolicy kbd = ((DerivedTokenKeyBinding) token).getOriginalKeyBinding();
                if (PolicyTypeUtil.symmetricKeyBinding(kbd)) {
                    st = stc.newURISignatureTarget(uuid);
                } else {
                    st = stc.newURISignatureTarget(uuid);
                }
            } else {
                st = stc.newURISignatureTarget(uuid);
            }
            if (st != null) {  //when st is null, request simply goes with out signing the token;
               if (!strIgnore) {
                    stc.addSTRTransform(st);
                    st.setPolicyQName(qName);
                } else {
                    stc.addTransform(st);
                }
                SignaturePolicy.FeatureBinding fb = (com.sun.xml.wss.impl.policy.mls.SignaturePolicy.FeatureBinding) primarySP.getFeatureBinding();
                fb.addTargetBinding(st);
            }
        }
    }

    protected abstract EncryptionPolicy getSecondaryEncryptionPolicy() throws PolicyException;

    private String generateSAMLSTRID() {
        return "SAML" +
                pid.generateID();
    }

    protected void addPrimaryTargets() throws PolicyException {
        SignaturePolicy.FeatureBinding spFB = null;
        if (primarySP != null) {
            spFB = (SignaturePolicy.FeatureBinding) primarySP.getFeatureBinding();
        }
        EncryptionPolicy.FeatureBinding epFB = null;
        if (primaryEP != null) {
            epFB = (EncryptionPolicy.FeatureBinding) primaryEP.getFeatureBinding();
        }

        if (spFB != null) {
            if (spFB.getCanonicalizationAlgorithm() == null || spFB.getCanonicalizationAlgorithm().equals("")) {
                spFB.setCanonicalizationAlgorithm(CanonicalizationMethod.EXCLUSIVE);
            }

            //TODO:: Merge SignedElements.

            for (SignedElements se : signedElements) {
                iAP.process(se, spFB);
            }
            /*
            If Empty SignParts is present then remove rest of the SignParts
            as we will be signing all HEADERS and Body. Question to WS-SX:
            Are SignedParts headers targeted to ultimate reciever role.
             */
            for (SignedParts sp : signedParts) {
                if (SecurityPolicyUtil.isSignedPartsEmpty(sp)) {
                    signedParts.removeAllElements();
                    signedParts.add(sp);
                    break;
                }
            }
            for (SignedParts sp : signedParts) {
                iAP.process(sp, spFB);
            }

            if (isWSS11() && requireSC()) {
                iAP.process(Target.SIGNATURE_CONFIRMATION, spFB);
            }
        }

        if (epFB != null) {
            for (EncryptedParts ep : encryptedParts) {
                foundEncryptTargets = true;
                eAP.process(ep, epFB);
            }

            for (EncryptedElements encEl : encryptedElements) {
                foundEncryptTargets = true;
                eAP.process(encEl, epFB);
            }
        }
    }

    protected boolean requireSC() {
        if (wss11 != null && wss11.getRequiredProperties() != null) {
            return wss11.getRequiredProperties().contains(WSSAssertion.REQUIRE_SIGNATURE_CONFIRMATION);
        }
        return false;
    }

    protected abstract Binding getBinding();

    public void processSupportingTokens(SupportingTokens st) throws PolicyException {

        SupportingTokensProcessor stp = new SupportingTokensProcessor(st,
                tokenProcessor, getBinding(), container, primarySP, getEncryptionPolicy(), pid);
        stp.process();
    }

    public void processSupportingTokens(SignedSupportingTokens st) throws PolicyException {

        SignedSupportingTokensProcessor stp = new SignedSupportingTokensProcessor(st,
                tokenProcessor, getBinding(), container, primarySP, getEncryptionPolicy(), pid);
        stp.process();
    }

    public void processSupportingTokens(EndorsingSupportingTokens est) throws PolicyException {

        EndorsingSupportingTokensProcessor stp = new EndorsingSupportingTokensProcessor(est,
                tokenProcessor, getBinding(), container, primarySP, getEncryptionPolicy(), pid);
        stp.process();
    }

    public void processSupportingTokens(SignedEndorsingSupportingTokens est) throws PolicyException {
        SignedEndorsingSupportingTokensProcessor stp = new SignedEndorsingSupportingTokensProcessor(est,
                tokenProcessor, getBinding(), container, primarySP, getEncryptionPolicy(), pid);
        stp.process();

    }

    public void processSupportingTokens(SignedEncryptedSupportingTokens sest) throws PolicyException {
        SignedEncryptedSupportingTokensProcessor setp = new SignedEncryptedSupportingTokensProcessor(sest,
                tokenProcessor, getBinding(), container, primarySP, getEncryptionPolicy(), pid);
        setp.process();
        isIssuedTokenAsEncryptedSupportingToken(setp.isIssuedTokenAsEncryptedSupportingToken());
    }

    public void processSupportingTokens(EncryptedSupportingTokens est) throws PolicyException {
        EncryptedSupportingTokensProcessor etp = new EncryptedSupportingTokensProcessor(est,
                tokenProcessor, getBinding(), container, primarySP, getEncryptionPolicy(), pid);
        etp.process();
        isIssuedTokenAsEncryptedSupportingToken(etp.isIssuedTokenAsEncryptedSupportingToken());
    }

    public void processSupportingTokens(EndorsingEncryptedSupportingTokens est) throws PolicyException {
        EndorsingEncryptedSupportingTokensProcessor etp = new EndorsingEncryptedSupportingTokensProcessor(est,
                tokenProcessor, getBinding(), container, primarySP, getEncryptionPolicy(), pid);
        etp.process();
        isIssuedTokenAsEncryptedSupportingToken(etp.isIssuedTokenAsEncryptedSupportingToken());
    }

    public void processSupportingTokens(SignedEndorsingEncryptedSupportingTokens est) throws PolicyException {
        SignedEndorsingEncryptedSupportingTokensProcessor etp = new SignedEndorsingEncryptedSupportingTokensProcessor(est,
                tokenProcessor, getBinding(), container, primarySP, getEncryptionPolicy(), pid);
        etp.process();
        isIssuedTokenAsEncryptedSupportingToken(etp.isIssuedTokenAsEncryptedSupportingToken());
    }

    protected SignaturePolicy getSignaturePolicy() {
        if (Binding.SIGN_ENCRYPT.equals(getBinding().getProtectionOrder())) {
            return primarySP;
        } else {
            return sSigPolicy;
        }
    }

    private EncryptionPolicy getEncryptionPolicy() throws PolicyException {
        if (Binding.SIGN_ENCRYPT.equals(getBinding().getProtectionOrder())) {
            return primaryEP;
        } else {
            return getSecondaryEncryptionPolicy();
        }
    }

    protected abstract void close();

    public boolean isWSS11() {
        return wss11 != null;
    }

    public void setWSS11(WSSAssertion wss11) {
        this.wss11 = wss11;
    }

    public boolean isIssuedTokenAsEncryptedSupportingToken() {
        return this.isIssuedTokenAsEncryptedSupportingToken;
    }

    private void isIssuedTokenAsEncryptedSupportingToken(boolean value) {
        this.isIssuedTokenAsEncryptedSupportingToken = value;
    }
}
