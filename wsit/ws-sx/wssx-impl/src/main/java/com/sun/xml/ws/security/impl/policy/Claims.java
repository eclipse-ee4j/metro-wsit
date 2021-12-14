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
import com.sun.xml.wss.WSITXMLFactory;

import java.io.ByteArrayInputStream;
import java.util.Collection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import jakarta.xml.ws.WebServiceException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author ashutosh.shahi@sun.com
 */
public class Claims extends PolicyAssertion implements com.sun.xml.ws.security.policy.Claims, SecurityAssertionValidator {

    private AssertionFitness fitness = AssertionFitness.IS_VALID;
    private boolean populated = false;
    private byte[] claimsBytes;
    private Element claimsElement = null;

    /**
     * Creates a new instance of Issuer
     */
    public Claims() {
    }

    public Claims(AssertionData name, Collection<PolicyAssertion> nestedAssertions, AssertionSet nestedAlternative) {
        super(name, nestedAssertions, nestedAlternative);
    }

    @Override
    public byte[] getClaimsAsBytes() {
        populate();
        return claimsBytes;
    }
    
    @Override
    public Element getClaimsAsElement(){
        populate();
        if(claimsElement == null){
            try{
            DocumentBuilderFactory dbf = WSITXMLFactory.createDocumentBuilderFactory(WSITXMLFactory.DISABLE_SECURE_PROCESSING);
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new ByteArrayInputStream(claimsBytes));
            claimsElement = (Element) doc.getElementsByTagNameNS("*", "Claims").item(0);
            } catch(Exception e){
                throw new WebServiceException(e);
            }
        }
        return claimsElement;
    }

    @Override
    public AssertionFitness validate(boolean isServer) {
        return populate(isServer);
    }

    private void populate() {
        populate(false);
    }

    private synchronized AssertionFitness populate(boolean b) {
        if (!populated) {
            claimsBytes = PolicyUtil.policyAssertionToBytes(this);
            populated = true;  
        }
        return fitness;
    }
}
