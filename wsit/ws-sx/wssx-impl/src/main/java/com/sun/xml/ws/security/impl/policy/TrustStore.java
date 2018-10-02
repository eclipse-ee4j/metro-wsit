/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.impl.policy;

import com.sun.xml.ws.policy.AssertionSet;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.sourcemodel.AssertionData;
import java.util.Collection;
import javax.xml.namespace.QName;

/**
 *
 * @author K.Venugopal@sun.com
 */
public class TrustStore extends KeyStore implements com.sun.xml.ws.security.policy.TrustStore{
    
    private static QName  peerAlias = new QName("peeralias");
    private static QName  stsAlias = new QName("stsalias");
    private static QName  serviceAlias = new QName("servicealias");
    private static QName certSelector = new QName("certSelector");
    private static QName callbackHandler = new QName("callbackHandler");
    /** Creates a new instance of TrustStore */
    public TrustStore() {
    }
    
    public TrustStore(AssertionData name,Collection<PolicyAssertion> nestedAssertions, AssertionSet nestedAlternative) {
        super(name,nestedAssertions,nestedAlternative);
    }
    public String getPeerAlias() {
        return this.getAttributeValue(peerAlias);
    }
    
    public String getSTSAlias() {
        return this.getAttributeValue(stsAlias);
    }
    
    public String getServiceAlias() {
        return this.getAttributeValue(serviceAlias);
    }
    
    public String getCertSelectorClassName() {
        return this.getAttributeValue(certSelector);
    }

    public String getTrustStoreCallbackHandler() {
        return this.getAttributeValue(callbackHandler);
    }
}
