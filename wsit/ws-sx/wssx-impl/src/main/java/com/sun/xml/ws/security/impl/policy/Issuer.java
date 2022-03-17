/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.impl.policy;

import com.sun.xml.ws.security.addressing.policy.Address;
import com.sun.xml.ws.policy.AssertionSet;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.sourcemodel.AssertionData;
import java.util.Collection;
import java.util.Iterator;
import com.sun.xml.ws.security.policy.SecurityAssertionValidator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
/**
 *
 * @author Abhijit Das
 */
public class Issuer extends PolicyAssertion implements com.sun.xml.ws.security.policy.Issuer, SecurityAssertionValidator {
    private AssertionFitness fitness = AssertionFitness.IS_VALID;
    private Address address;
    private Address metadataAddress;
    private boolean populated = false;
    private PolicyAssertion refProps = null;
    private PolicyAssertion refParams = null;
    private PolicyAssertion serviceName = null;
    private String portType = null;
    private Element identityEle = null;

    /**
     * Creates a new instance of Issuer
     */
    public Issuer() {
    }

    public Issuer(AssertionData name,Collection<PolicyAssertion> nestedAssertions, AssertionSet nestedAlternative) {
        super(name,nestedAssertions,nestedAlternative);
    }

    @Override
    public AssertionFitness validate(boolean isServer) {
        return populate(isServer);
    }

    private void getAddressFromMetadata(PolicyAssertion addressingMetadata) {
        PolicyAssertion metadata = null;
        PolicyAssertion metadataSection = null;
        PolicyAssertion metadataReference = null;
        if(addressingMetadata != null){
            if ( addressingMetadata.hasParameters() ) {
                final Iterator <PolicyAssertion> iterator = addressingMetadata.getParametersIterator();
                while ( iterator.hasNext() ) {
                    final PolicyAssertion assertion = iterator.next();
                    if ( PolicyUtil.isMetadata(assertion)) {
                        metadata = assertion;
                        break;
                    }
                }
            }
            if(metadata != null){
                if ( metadata.hasParameters() ) {
                    final Iterator <PolicyAssertion> iterator = metadata.getParametersIterator();
                    while ( iterator.hasNext() ) {
                        final PolicyAssertion assertion = iterator.next();
                        if (PolicyUtil.isMetadataSection(assertion)){
                            metadataSection = assertion;
                            break;
                        }
                    }
                }
                if(metadataSection != null){
                    if ( metadataSection.hasParameters() ) {
                        final Iterator <PolicyAssertion> iterator = metadataSection.getParametersIterator();
                        while ( iterator.hasNext() ) {
                            final PolicyAssertion assertion = iterator.next();
                            if ( PolicyUtil.isMetadataReference(assertion)) {
                                metadataReference = assertion;
                                break;
                            }
                        }
                    }
                    if(metadataReference != null){
                        if ( metadataReference.hasParameters() ) {
                            final Iterator <PolicyAssertion> iterator = metadataReference.getParametersIterator();
                            while ( iterator.hasNext() ) {
                                final PolicyAssertion assertion = iterator.next();
                                if ( PolicyUtil.isAddress(assertion)) {
                                    metadataAddress = (Address)assertion;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    private void populate(){
        populate(false);
    }

    private synchronized AssertionFitness populate(boolean isServer) {
        if(!populated){
            if ( this.hasParameters() ) {
                Iterator <PolicyAssertion> it = this.getParametersIterator();
                while ( it.hasNext() ) {
                    PolicyAssertion assertion = it.next();
                    if ( PolicyUtil.isAddress(assertion)) {
                        this.address = (Address) assertion;
                    } else if(PolicyUtil.isPortType(assertion)){
                        this.portType = assertion.getValue();
                    } else if(PolicyUtil.isReferenceParameters(assertion)){
                        this.refParams = assertion;
                    } else if(PolicyUtil.isReferenceProperties(assertion)){
                        this.refProps = assertion;
                    } else if(PolicyUtil.isServiceName(assertion)){
                        this.serviceName = assertion;
                    } else if(PolicyUtil.isAddressingMetadata(assertion)){
                        getAddressFromMetadata(assertion);
                    } else if(Constants.IDENTITY.equals(assertion.getName().getLocalPart())){
                        Document doc = PolicyUtil.policyAssertionToDoc(assertion);
                        identityEle = (Element)doc.getElementsByTagNameNS("*", Constants.IDENTITY).item(0);
                    }
                }
            }
            populated = true;
        }
        return fitness;
    }

    @Override
    public Address getAddress() {
        populate();
        return address;
    }

    @Override
    public String getPortType(){
        populate();
        return portType;
    }

    @Override
    public PolicyAssertion getReferenceParameters(){
        populate();
        return refParams;
    }

    @Override
    public PolicyAssertion getReferenceProperties(){
        populate();
        return refProps;
    }

    @Override
    public PolicyAssertion getServiceName(){
        populate();
        return serviceName;
    }

    @Override
    public Element getIdentity(){
        populate();
        return identityEle;
    }

    @Override
    public Address getMetadataAddress() {
        populate();
        return metadataAddress;
    }
}
