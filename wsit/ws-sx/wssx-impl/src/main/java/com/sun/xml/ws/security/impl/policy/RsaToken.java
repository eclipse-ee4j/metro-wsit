/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
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
import com.sun.xml.ws.security.policy.SecurityAssertionValidator;
import com.sun.xml.ws.security.policy.SecurityPolicyVersion;
import java.util.Collection;
import javax.xml.namespace.QName;

/**
 * RsaToken should be used with SecurityPolicy submission namespace (2005/07) and a namespace of
 * http://schemas.microsoft.com/ws/2005/07/securitypolicy. It should be replaced with KeyValueToken
 * for SecurityPolicy 1.2
 *
 * @author ashutosh.shahi@sun.com
 */
public class RsaToken  extends PolicyAssertion implements com.sun.xml.ws.security.policy.RsaToken, Cloneable, SecurityAssertionValidator{
    
    private AssertionFitness fitness = AssertionFitness.IS_VALID;
    private boolean populated = false;
    private SecurityPolicyVersion spVersion = SecurityPolicyVersion.SECURITYPOLICY200507;
    private final QName itQname;
    private String includeToken;
    private String id = null;    
    /** Creates a new instance of RsaToken */
    public RsaToken() {
        id= PolicyUtil.randomUUID();
        itQname = new QName(spVersion.namespaceUri, Constants.IncludeToken);
        includeToken = spVersion.includeTokenAlways;
    }
    
    public RsaToken(AssertionData name,Collection<PolicyAssertion> nestedAssertions, AssertionSet nestedAlternative) {
        super(name,nestedAssertions,nestedAlternative);
        id= PolicyUtil.randomUUID();
        
        String nsUri = getName().getNamespaceURI();
        if(Constants.MS_SP_NS.equals(nsUri)){
            spVersion = SecurityPolicyVersion.SECURITYPOLICY200507;
        }
        itQname = new QName(spVersion.namespaceUri, Constants.IncludeToken);
        includeToken = spVersion.includeTokenAlways;
    }
    
    @Override
    public SecurityAssertionValidator.AssertionFitness validate(boolean isServer) {
        return populate(isServer);
    }
    
    @Override
    public String getIncludeToken() {
        populate();
        return includeToken;
    }
    
    public void setIncludeToken(String type) {
        includeToken = type;
    }
    
    @Override
    public String getTokenId() {
        return id;
    }
    
    @Override
    public SecurityPolicyVersion getSecurityPolicyVersion() {
        return spVersion;
    }

    private void populate() {
        populate(false);
    }

    private SecurityAssertionValidator.AssertionFitness populate(boolean isServer) {
        if(!populated){
            if(this.getAttributeValue(itQname)!=null){
                this.includeToken = this.getAttributeValue(itQname);
            }
            
            populated = true;
        }
        return fitness;
    }
    
}
