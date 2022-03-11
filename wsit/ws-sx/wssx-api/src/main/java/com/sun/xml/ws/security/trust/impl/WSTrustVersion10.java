/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.trust.impl;

import com.sun.xml.ws.security.trust.WSTrustVersion;

/**
 *
 * @author Jiandong
 */
public class WSTrustVersion10 extends WSTrustVersion{

    private String nsURI;

    public WSTrustVersion10(){
           nsURI =  "http://schemas.xmlsoap.org/ws/2005/02/trust";
    }
    @Override
    public String getNamespaceURI(){
        return nsURI;
    }

    @Override
    public  String getIssueRequestTypeURI(){
        return nsURI + "/Issue";
    }

    @Override
    public  String getRenewRequestTypeURI(){
        return nsURI + "/Renew";
    }

    @Override
    public  String getCancelRequestTypeURI(){
        return nsURI +"/Cancel";
    }

    @Override
    public  String getValidateRequestTypeURI(){
        return nsURI +"/Validate";
    }

    @Override
    public String getValidateStatuesTokenType(){
        return nsURI+"/RSTR/Status";
    }

    @Override
    public String getKeyExchangeRequestTypeURI(){
        return nsURI +"/KET";
    }

    @Override
    public  String getPublicKeyTypeURI(){
        return nsURI +"/PublicKey";
    }

    @Override
    public  String getSymmetricKeyTypeURI(){
        return nsURI +"/SymmetricKey";
    }

    @Override
    public  String getBearerKeyTypeURI(){
        return "http://schemas.xmlsoap.org/ws/2005/05/identity/NoProofKey";
    }

    @Override
    public  String getIssueRequestAction(){
        return nsURI + "/RST/Issue";
    }

    @Override
    public  String getIssueResponseAction(){
        return nsURI + "/RSTR/Issue";
    }

    @Override
    public  String getIssueFinalResoponseAction(){
        return nsURI + "/RSTR/Issue";
    }

    @Override
    public  String getRenewRequestAction(){
        return nsURI + "/RST/Renew";
    }

    @Override
    public  String getRenewResponseAction(){
        return nsURI + "/RSTR/Renew";
    }

    @Override
    public  String getRenewFinalResoponseAction(){
        return nsURI + "/RSTR/Renew";
    }
    @Override
    public  String getCancelRequestAction(){
        return nsURI + "/RST/Cancel";
    }

    @Override
    public  String getCancelResponseAction(){
        return nsURI + "/RSTR/Cancel";
    }

    @Override
    public  String getCancelFinalResoponseAction(){
        return nsURI + "/RSTR/Cancel";
    }

    @Override
    public  String getValidateRequestAction(){
        return nsURI + "/RST/Validate";
    }

    @Override
    public  String getValidateResponseAction(){
        return nsURI + "/RSTR/Validate";
    }

    @Override
    public  String getValidateFinalResoponseAction(){
        return nsURI + "/RSTR/Validate";
    }

    @Override
    public  String getCKPSHA1algorithmURI(){
        return nsURI + "/CK/PSHA1";
    }

    @Override
    public  String getCKHASHalgorithmURI(){
        return nsURI + "/CK/HASH";
    }

    @Override
    public  String getAsymmetricKeyBinarySecretTypeURI(){
        return nsURI + "/AsymmetricKey";
    }

    @Override
    public  String getNonceBinarySecretTypeURI(){
        return nsURI + "/Nonce";
    }

    @Override
    public String getValidStatusCodeURI(){
        return nsURI + "/status/valid";
    }

    @Override
    public String getInvalidStatusCodeURI(){
        return nsURI + "/status/invalid";
    }
}
