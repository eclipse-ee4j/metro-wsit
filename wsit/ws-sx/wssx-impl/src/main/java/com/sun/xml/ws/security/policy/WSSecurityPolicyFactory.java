/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.policy;

import com.sun.xml.ws.policy.PolicyAssertion;

import javax.xml.namespace.QName;


/**
 *
 * @author K.Venugopal@sun.com
 */
public abstract class WSSecurityPolicyFactory{

    protected WSSecurityPolicyFactory() {}

    public static WSSecurityPolicyFactory getInstance(){
        //default
        throw new UnsupportedOperationException();
      //  return new com.sun.xml.ws.security.impl.policy.WSSecurityPolicyFactory();
    }

    public static WSSecurityPolicyFactory getInstance(String namespaceURI){
        throw new UnsupportedOperationException("This method is not supported");
    }

    public abstract EncryptedParts createEncryptedParts();
    public abstract SignedParts createSignedParts();
    public abstract SpnegoContextToken createSpnegoContextToken();
    public abstract TransportBinding createTransportBinding();
    public abstract TransportToken createTransportToken();
    public abstract AlgorithmSuite createAlgorithmSuite();
    public abstract UserNameToken createUsernameToken();
    public abstract SymmetricBinding createSymmetricBinding();
    public abstract AsymmetricBinding createASymmetricBinding();
    public abstract X509Token createX509Token();
    public abstract EndorsingSupportingTokens createEndorsingSupportingToken();
    public abstract IssuedToken createIssuedToken();
    public abstract PolicyAssertion createSecurityAssertion(QName name);
    public abstract PolicyAssertion createSecurityAssertion(QName qname, ClassLoader classLoader);

}
