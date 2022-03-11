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

import com.sun.xml.wss.impl.PolicyTypeUtil;
import com.sun.xml.wss.impl.policy.MLSPolicy;
import com.sun.xml.wss.impl.policy.SecurityPolicy;
import com.sun.xml.ws.security.policy.MessageLayout;
import com.sun.xml.wss.impl.policy.PolicyGenerationException;
import com.sun.xml.wss.impl.policy.mls.AuthenticationTokenPolicy;
import com.sun.xml.wss.impl.policy.mls.EncryptionPolicy;
import com.sun.xml.wss.impl.policy.mls.MessagePolicy;
import com.sun.xml.wss.impl.policy.mls.SignaturePolicy;
import com.sun.xml.wss.impl.policy.mls.Target;
import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;

/**
 *
 * @author Abhijit.Das@Sun.COM
 */
public class XWSSPolicyContainer {
    private boolean isServer;
    private boolean isIncoming;

    private boolean encPoliciesContain(QName qName, List<SecurityPolicy> encPolicies) {
        if (qName.equals(Target.BODY_QNAME)) {
            return false;
        }
        for (SecurityPolicy sp : encPolicies) {
            if (PolicyTypeUtil.encryptionPolicy(sp)) {
                EncryptionPolicy.FeatureBinding fb = (EncryptionPolicy.FeatureBinding) ((EncryptionPolicy) sp).getFeatureBinding();
                ArrayList targets = fb.getTargetBindings();
                for (int i = 0; i < targets.size(); i++) {
                    Target t = (Target) targets.get(i);
                    if (t.getType() == Target.TARGET_TYPE_VALUE_QNAME) {
                        if (qName.equals(t.getQName())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private void fixEncryptedTargetsInSignature(MessagePolicy msgPolicy,boolean isWSS11) {
        boolean encryptBeforeSign = false;
        boolean seenEncryptPolicy = false;
        boolean seenSignPolicy = false;
        List<SecurityPolicy> encPolicies = new ArrayList<>();

        for (Object policy : msgPolicy.getPrimaryPolicies()) {

            if (policy instanceof SecurityPolicy) {
                SecurityPolicy secPolicy = (SecurityPolicy) policy;
                if (PolicyTypeUtil.signaturePolicy(secPolicy)) {
                    seenSignPolicy = true;
                    if (!seenEncryptPolicy && isIncoming) {
                        encryptBeforeSign = true;
                    }
                } else if (PolicyTypeUtil.encryptionPolicy(secPolicy)) {
                    seenEncryptPolicy  = true;
                    if (!seenSignPolicy && !isIncoming) {
                        encryptBeforeSign = true;
                    }
                    encPolicies.add(secPolicy);
                }
            }
        }
        if (encryptBeforeSign) {
            for (Object policy : msgPolicy.getPrimaryPolicies()) {
                boolean containsEncryptedHeader  =false;
                if (policy instanceof SecurityPolicy) {
                    SecurityPolicy secPolicy = (SecurityPolicy) policy;
                    if (PolicyTypeUtil.signaturePolicy(secPolicy)) {
                        SignaturePolicy.FeatureBinding sfb = (SignaturePolicy.FeatureBinding) ((SignaturePolicy) secPolicy).getFeatureBinding();
                        ArrayList targets = sfb.getTargetBindings();
                        for (int i = 0; i < targets.size(); i++) {
                            Target t = (Target) targets.get(i);
                            if (t.getType() == Target.TARGET_TYPE_VALUE_QNAME) {
                                if (encPoliciesContain(t.getQName(), encPolicies)) {
                                    if(isWSS11){
                                        if (!containsEncryptedHeader) {
                                            QName encHeaderQName = new QName("http://docs.oasis-open.org/wss/oasis-wss-wssecurity-secext-1.1.xsd", "EncryptedHeader");
                                            t.setQName(encHeaderQName);
                                            containsEncryptedHeader = true;
                                        } else {
                                            targets.remove(i);
                                        }
                                    }else {
                                        if (!containsEncryptedHeader) {
                                            QName encDataQName = new QName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "EncryptedData");
                                             t.setQName(encDataQName);
                                            containsEncryptedHeader = true;
                                        } else {
                                            targets.remove(i);
                                        }
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }
    }

    private enum Section {
        ClientIncomingPolicy,
        ClientOutgoingPolicy,
        ServerIncomingPolicy,
        ServerOutgoingPolicy
    }

    private Section section;
    private List<SecurityPolicy> policyList;
    private List<SecurityPolicy> effectivePolicyList;
    private MessageLayout mode;
    private int foundTimestamp = -1;

    private boolean modified = false;

    /** Creates a new instance of PolicyConverter */
    public XWSSPolicyContainer(MessageLayout mode, boolean isServer, boolean isIncoming) {
        this.mode = mode;
        this.isServer = isServer;
        this.isIncoming = isIncoming;
        setMessageMode(isServer, isIncoming);
        effectivePolicyList = new ArrayList<>();
    }
    public XWSSPolicyContainer(boolean isServer,boolean isIncoming) {
        setMessageMode(isServer, isIncoming);
        this.isServer = isServer;
        this.isIncoming = isIncoming;
        effectivePolicyList = new ArrayList<>();
    }
    public void setMessageMode(boolean isServer, boolean isIncoming) {
        if ( isServer && isIncoming) {
            section = Section.ServerIncomingPolicy;
        } else if ( isServer && !isIncoming) {
            section = Section.ServerOutgoingPolicy;
        } else if ( !isServer && isIncoming) {
            section = Section.ClientIncomingPolicy;
        } else if ( !isServer && !isIncoming) {
            section = Section.ClientOutgoingPolicy;
        }
    }
    public void setPolicyContainerMode(MessageLayout mode){
        this.mode = mode;
    }
    /**
     * Insert into policyList
     *
     *
     */
    public void insert(SecurityPolicy secPolicy ) {
        if(secPolicy == null){
            return;
        }
        if ( policyList == null ) {
            policyList = new ArrayList<>();
        }
        if ( isSupportingToken(secPolicy)) {
            switch (section) {
            case ServerOutgoingPolicy:
            case ClientIncomingPolicy:
                return;
            }
        }
        modified = true;
        policyList.add(secPolicy);
    }
    public MessagePolicy getMessagePolicy(boolean isWSS11)throws PolicyGenerationException {
        if ( modified ) {
            convert();
            modified = false;
        }
        MessagePolicy msgPolicy = new MessagePolicy();

        msgPolicy.appendAll(effectivePolicyList);
        removeEmptyPrimaryPolicies(msgPolicy);
        fixEncryptedTargetsInSignature(msgPolicy,isWSS11);
        return msgPolicy;

    }
    private void removeEmptyPrimaryPolicies(MessagePolicy msgPolicy) {
        for ( Object policy : msgPolicy.getPrimaryPolicies() ) {
            if ( policy instanceof SecurityPolicy) {
                SecurityPolicy secPolicy = (SecurityPolicy)policy;
                if ( PolicyTypeUtil.signaturePolicy(secPolicy)) {
                    if (((SignaturePolicy.FeatureBinding)((SignaturePolicy)secPolicy).getFeatureBinding()).getTargetBindings().size() == 0 ) {
                        msgPolicy.remove(secPolicy);
                    }
                } else if ( PolicyTypeUtil.encryptionPolicy(secPolicy)) {
                    if (((EncryptionPolicy.FeatureBinding)((EncryptionPolicy)secPolicy).getFeatureBinding()).getTargetBindings().size() == 0 ) {
                        msgPolicy.remove(secPolicy);
                    }
                }
            }
        }
    }

    /**
     * Insert SecurityPolicy after supporting tokens.
     *
     */
    //private void appendAfterToken(SecurityPolicy xwssPolicy , Section section) {
    private void appendAfterToken(SecurityPolicy xwssPolicy) {
        int pos = -1;
        for ( SecurityPolicy secPolicy : effectivePolicyList) {
            if ( isSupportingToken(secPolicy) || isTimestamp(secPolicy)) {
            } else {
                pos = effectivePolicyList.indexOf(secPolicy);
                break;
            }
        }
        if ( pos != -1 ) {
            effectivePolicyList.add(pos, xwssPolicy);
        } else {
            effectivePolicyList.add(xwssPolicy);
        }
    }
    /**
     * Insert SecurityPolicy before supporting Tokens.
     *
     */
    private void prependBeforeToken(SecurityPolicy xwssPolicy ) {
        int pos = -1;
        for ( SecurityPolicy secPolicy : effectivePolicyList) {
            if ( !isSupportingToken(secPolicy)) {
            } else {
                pos = effectivePolicyList.indexOf(secPolicy);
            }
        }
        if ( pos != -1 ) {
            effectivePolicyList.add(pos, xwssPolicy);
        } else {
            effectivePolicyList.add(xwssPolicy);
        }
    }
    /**
     *
     * Add Security policy.
     */
    private void append(SecurityPolicy xwssPolicy ) {
        effectivePolicyList.add(xwssPolicy);
    }
    /**
     * Add SecurityPolicy.
     *
     */
    private void prepend(SecurityPolicy xwssPolicy) {
        effectivePolicyList.add(0, xwssPolicy);
    }
    /**
     *
     * @return - true if xwssPolicy is SupportingToken policy else false.
     */
    private boolean isSupportingToken( SecurityPolicy xwssPolicy ) {
        if ( xwssPolicy == null ) {
            return false;
        }
        //UsernameToken, SAML Token Policy, X509Certificate, issued token
        if ( PolicyTypeUtil.authenticationTokenPolicy(xwssPolicy)) {
            MLSPolicy binding = ((AuthenticationTokenPolicy)xwssPolicy).getFeatureBinding();
            return PolicyTypeUtil.usernameTokenPolicy(binding) ||
                    PolicyTypeUtil.samlTokenPolicy(binding) ||
                    PolicyTypeUtil.x509CertificateBinding(binding) ||
                    PolicyTypeUtil.issuedTokenKeyBinding(binding);
        }
        return false;
    }
    /**
     *
     * @return - true if xwssPolicy is TimestampPolicy else false.
     */
    private boolean isTimestamp( SecurityPolicy xwssPolicy ) {
        return xwssPolicy != null && PolicyTypeUtil.timestampPolicy(xwssPolicy);
    }
    /**
     *
     * Lax mode
     */
    private void convertLax() {
        for ( SecurityPolicy xwssPolicy : policyList ) {
            if ( isTimestamp(xwssPolicy )) {
                foundTimestamp = policyList.indexOf(xwssPolicy);
                prepend(xwssPolicy);
                continue;
            }

            if ( !isSupportingToken(xwssPolicy)) {
                switch(section) {
                case ClientIncomingPolicy:
                    prepend(xwssPolicy);
                    break;
                case ClientOutgoingPolicy:
                    append(xwssPolicy);
                    break;
                case ServerIncomingPolicy:
                    appendAfterToken(xwssPolicy);
                    break;
                case ServerOutgoingPolicy:
                    append(xwssPolicy);
                    break;
                }
            } else if ( isSupportingToken(xwssPolicy) || isTimestamp(xwssPolicy)) {
                prepend(xwssPolicy);


            }
        }
    }
    /**
     *
     * Strict mode.
     */
    private void convertStrict() {
        for ( SecurityPolicy xwssPolicy : policyList ) {
            if ( isSupportingToken(xwssPolicy)) {
                prepend(xwssPolicy);

            } else if ( isTimestamp(xwssPolicy)) {
                prepend(xwssPolicy);
            } else {
                switch (section ) {
                case ClientIncomingPolicy:
                    appendAfterToken(xwssPolicy);
                    break;
                case ClientOutgoingPolicy:
                    append(xwssPolicy);
                    break;
                case ServerIncomingPolicy:
                    appendAfterToken(xwssPolicy);
                    break;
                case ServerOutgoingPolicy:
                    append(xwssPolicy);
                    break;
                }
            }
        }
    }
    /**
     * LaxTsFirst mode.
     *
     */
    private void convertLaxTsFirst() {
        convertLax();
        if ( foundTimestamp != -1 ) {
            switch (section ) {
            case ClientOutgoingPolicy:
                effectivePolicyList.add(0, effectivePolicyList.remove(foundTimestamp));
                break;
            case ServerOutgoingPolicy:
                effectivePolicyList.add(0, effectivePolicyList.remove(foundTimestamp));
                break;
            }
        }

    }
    /**
     * LaxTsLast mode.
     *
     */
    private void convertLaxTsLast() {
        convertLax();
        if ( foundTimestamp != -1 ) {
            switch (section) {
            case ClientOutgoingPolicy:
                effectivePolicyList.add(effectivePolicyList.size() -1, effectivePolicyList.remove(foundTimestamp));
                break;
            case ServerOutgoingPolicy:
                effectivePolicyList.add(effectivePolicyList.size() -1, effectivePolicyList.remove(foundTimestamp));
                break;
            }
        }
    }
    /**
     *
     * Convert WS-Security Policy to XWSS policy.
     */
    public void convert() {
        if ( MessageLayout.Lax == mode ) {
            convertLax();
        } else if ( MessageLayout.Strict == mode ) {
            convertStrict();
        } else if ( MessageLayout.LaxTsFirst == mode ) {
            convertLaxTsFirst();
        } else if ( MessageLayout.LaxTsLast == mode ) {
            convertLaxTsLast();
        }
    }
}

