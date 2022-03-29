/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.trust;

import com.sun.xml.ws.security.trust.impl.WSTrustVersion10;
import com.sun.xml.ws.security.trust.impl.wssx.WSTrustVersion13;

/**
 *
 * @author Jiandong
 */
public abstract class WSTrustVersion {

    public static final WSTrustVersion WS_TRUST_10 = new WSTrustVersion10();

    public static final WSTrustVersion WS_TRUST_13 = new WSTrustVersion13();

    public static final String WS_TRUST_10_NS_URI = "http://schemas.xmlsoap.org/ws/2005/02/trust";
    public static final String WS_TRUST_13_NS_URI = "http://docs.oasis-open.org/ws-sx/ws-trust/200512";
    public static WSTrustVersion getInstance(String nsURI){
        if (nsURI.equals(WS_TRUST_13.getNamespaceURI())){
            return WS_TRUST_13;
        }

        return WS_TRUST_10;
    }

    protected WSTrustVersion() {}

    public abstract String getNamespaceURI();

    public abstract String getIssueRequestTypeURI();

    public abstract String getRenewRequestTypeURI();

    public abstract String getCancelRequestTypeURI();

    public abstract String getValidateRequestTypeURI();

    public abstract String getValidateStatuesTokenType();

    public abstract String getKeyExchangeRequestTypeURI();

    public abstract String getPublicKeyTypeURI();

    public abstract String getSymmetricKeyTypeURI();

    public abstract String getBearerKeyTypeURI();

    public abstract String getIssueRequestAction();

    public abstract String getIssueResponseAction();

    public abstract String getIssueFinalResoponseAction();

    public abstract String getRenewRequestAction();

    public abstract String getRenewResponseAction();

    public abstract String getRenewFinalResoponseAction();

    public abstract String getCancelRequestAction();

    public abstract String getCancelResponseAction();

    public abstract String getCancelFinalResoponseAction();

    public abstract String getValidateRequestAction();

    public abstract String getValidateResponseAction();

    public abstract String getValidateFinalResoponseAction();

    public String getFinalResponseAction(String reqAction){
        if (reqAction.equals(getIssueRequestAction())){
            return getIssueFinalResoponseAction();
        }

        if (reqAction.equals(getRenewRequestAction())){
            return getRenewFinalResoponseAction();
        }

        if (reqAction.equals(getCancelRequestAction())){
            return getCancelFinalResoponseAction();
        }

        if (reqAction.equals(getValidateRequestAction())){
            return getValidateFinalResoponseAction();
        }

        return null;
    }

    public abstract String getCKPSHA1algorithmURI();

    public abstract String getCKHASHalgorithmURI();

    public abstract String getAsymmetricKeyBinarySecretTypeURI();

    public abstract String getNonceBinarySecretTypeURI();

    public abstract String getValidStatusCodeURI();

    public abstract String getInvalidStatusCodeURI();

}
