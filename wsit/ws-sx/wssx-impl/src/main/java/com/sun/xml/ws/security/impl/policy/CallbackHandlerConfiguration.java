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

import com.sun.xml.ws.policy.PolicyAssertion;
import java.util.Iterator;
import com.sun.xml.ws.policy.AssertionSet;
import com.sun.xml.ws.policy.sourcemodel.AssertionData;
import java.util.Collection;
import com.sun.xml.ws.security.policy.SecurityAssertionValidator;
import javax.xml.namespace.QName;

/**
 *
 * @author K.Venugopal@sun.com
 */
public class CallbackHandlerConfiguration extends PolicyAssertion implements com.sun.xml.ws.security.policy.CallbackHandlerConfiguration, SecurityAssertionValidator{
    
    private static final QName timestampTimeout  =  new QName("timestampTimeout");
    private boolean populated = false;
    private static final QName useXWSSCallbacks = new QName("useXWSSCallbacks");
    private AssertionFitness fitness = AssertionFitness.IS_VALID;
    private static final QName iterationsForPDK = new QName("iterationsForPDK");
    /** Creates a new instance of CallbackHandlerConfiguration */
    public CallbackHandlerConfiguration() {
    }
    
    public CallbackHandlerConfiguration(AssertionData name,Collection<PolicyAssertion> nestedAssertions, AssertionSet nestedAlternative) {
        super(name,nestedAssertions,nestedAlternative);
    }
    
    @Override
    public Iterator<? extends PolicyAssertion> getCallbackHandlers() {
        return this.getParametersIterator();
    }
    
    @Override
    public AssertionFitness validate(boolean isServer) {
        return populate(isServer);
    }
    
    private synchronized AssertionFitness populate(boolean isServer) {
        if(!populated){
            populated  = true;
        }
        return fitness;
    }

    @Override
    public String getTimestampTimeout() {
        if(this.getAttributes().containsKey(timestampTimeout)){
            return this.getAttributeValue(timestampTimeout);
        }
        return null;
    }

    @Override
    public String getiterationsForPDK() {
        if(this.getAttributes().containsKey(iterationsForPDK)) {
            return this.getAttributeValue(iterationsForPDK);
        }
        return "0";
    }
    @Override
    public String getUseXWSSCallbacks() {
        return this.getAttributeValue(useXWSSCallbacks);
    }
}
