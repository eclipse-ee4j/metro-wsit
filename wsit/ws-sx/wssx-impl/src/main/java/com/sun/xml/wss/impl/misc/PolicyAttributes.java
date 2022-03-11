/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.impl.misc;

/**
 *
 * @author K.Venugopal@sun.com
 */
public class PolicyAttributes {

    private boolean issuedTokens = false;
    private boolean secureConversation = false;
    private boolean reliableMessaging = false;
    private boolean supportingTokens = false;
    private boolean endorsingST = false;//endorsing supporting tokens
    private boolean signedEndorsingST = false;//Signed endorsing supporting tokens
    private boolean signedST = false;
    private boolean protectSignature = false;
    private boolean protectTokens = false;


    /** Creates a new instance of PolicyAttributes */
    public PolicyAttributes() {
    }

    public boolean isProtectTokens() {
        return protectTokens;
    }

    public void setProtectTokens(boolean protectTokens) {
        this.protectTokens = protectTokens;
    }

    public boolean isIssuedTokens() {
        return issuedTokens;
    }

    public void setIssuedTokens(boolean issuedTokens) {
        this.issuedTokens = issuedTokens;
    }

    public boolean isSecureConversation() {
        return secureConversation;
    }

    public void setSecureConversation(boolean secureConversation) {
        this.secureConversation = secureConversation;
    }

    public boolean isReliableMessaging() {
        return reliableMessaging;
    }

    public void setReliableMessaging(boolean reliableMessaging) {
        this.reliableMessaging = reliableMessaging;
    }

    public boolean isSupportingTokens() {
        return supportingTokens;
    }

    public void setSupportingTokens(boolean supportingTokens) {
        this.supportingTokens = supportingTokens;
    }

    public boolean isEndorsingST() {
        return endorsingST;
    }

    public void setEndorsingST(boolean endorsingST) {
        this.endorsingST = endorsingST;
    }

    public boolean isSignedEndorsingST() {
        return signedEndorsingST;
    }

    public void setSignedEndorsingST(boolean signedEndorsingST) {
        this.signedEndorsingST = signedEndorsingST;
    }

    public boolean isSignedST() {
        return signedST;
    }

    public void setSignedST(boolean signedST) {
        this.signedST = signedST;
    }

    public boolean isProtectSignature() {
        return protectSignature;
    }

    public void setProtectSignature(boolean protectSignature) {
        this.protectSignature = protectSignature;
    }
}
