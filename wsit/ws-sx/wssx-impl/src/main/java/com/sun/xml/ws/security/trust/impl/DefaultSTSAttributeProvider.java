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

import com.sun.xml.ws.api.security.trust.Claims;
import com.sun.xml.ws.api.security.trust.STSAttributeProvider;
import com.sun.xml.wss.saml.Assertion;
import com.sun.xml.wss.saml.AssertionUtil;
import com.sun.xml.wss.saml.Attribute;
import com.sun.xml.wss.saml.AttributeStatement;
import com.sun.xml.wss.saml.AuthenticationStatement;
import com.sun.xml.wss.saml.NameID;
import com.sun.xml.wss.saml.NameIdentifier;
import com.sun.xml.wss.saml.SAMLException;
import com.sun.xml.wss.saml.util.SAMLUtil;


import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.security.auth.Subject;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

import org.w3c.dom.Element;

/**
 *
 * @author Jiandong Guo
 */
public class DefaultSTSAttributeProvider implements STSAttributeProvider{
   
    @Override
    public Map<QName, List<String>> getClaimedAttributes(final Subject subject, final String appliesTo, final String tokenType, final Claims claims){
        final Set<Principal> principals = subject.getPrincipals();
        final Map<QName, List<String>> attrs = new HashMap<>();
        if (principals != null && !principals.isEmpty()){
            final Iterator iterator = principals.iterator();
            while (iterator.hasNext()){
                final String name = principals.iterator().next().getName();
                if (name != null){
                    List<String> nameIds = new ArrayList<>();
                    nameIds.add(name);
                    attrs.put(new QName("http://sun.com", NAME_IDENTIFIER), nameIds);
                    break;
                }
            }       
        }else {
            //handle the case that the authentication token is SAML assertion
            Set<Object> set = subject.getPublicCredentials();
            Element samlAssertion = null;
            try {
                for (Object obj : set) {
                    if (obj instanceof XMLStreamReader) {
                        XMLStreamReader reader = (XMLStreamReader) obj;
                        //To create a DOM Element representing the Assertion :
                        samlAssertion = SAMLUtil.createSAMLAssertion(reader);
                    } else if (obj instanceof Element){
                        samlAssertion = (Element) obj;
                    }
                    break;
                }
                if (samlAssertion != null){
                    this.addAttributes(samlAssertion, attrs, false);
                }
            }catch (Exception ex){
                throw new RuntimeException(ex);
            }
        }

        // Check if it is the ActAs case
        if ("true".equals(claims.getOtherAttributes().get(new QName("ActAs")))){
            //ToDo: have a general mechanism to handle ActAs tokens

            // Get the ActAs token
            Element token = null;
            for (Object obj : claims.getSupportingProperties()){
                if (obj instanceof Subject){
                    token = (Element)((Subject)obj).getPublicCredentials().iterator().next();
                        break;
                }
            }

            try {
                if (token != null){
                    addAttributes(token, attrs, true);
                }
            }catch (Exception ex){
                throw new RuntimeException(ex);
            }
        }

        if (attrs.size() < 2){
            // Set up a dumy attribute value
            final QName key = new QName("http://sun.com", "token-requestor");
            List<String> tokenRequestor = new ArrayList<>();
            tokenRequestor.add("authenticated");
            attrs.put(key, tokenRequestor);
        }
       
        return attrs;
    }

    private void addAttributes(Element token, Map<QName, List<String>> attrs, boolean isActAs) throws SAMLException{
        // only handle the case of UsernameToken and SAML assertion here
        String name = null;
        String nameNS = null;
        String tokenName = token.getLocalName();
        if ("UsernameToken".equals(tokenName)){
            // an UsernameToken: get the user name
            name = token.getElementsByTagNameNS("*", "Username").item(0).getFirstChild().getNodeValue();
        } else if ("Assertion".equals(tokenName)){
            // an SAML assertion
            Assertion assertion = AssertionUtil.fromElement(token);

            com.sun.xml.wss.saml.Subject subject = null;
            NameID nameID = null;

            // SAML 2.0
            try {
                subject = assertion.getSubject();
            }catch (Exception ex){
                subject = null;
            }

            if (subject != null){
                nameID = subject.getNameId();
            }

            List<Object> statements = assertion.getStatements();
            for (Object s : statements){
                if (s instanceof AttributeStatement){
                    List<Attribute> samlAttrs = ((AttributeStatement)s).getAttributes();
                    for (Attribute samlAttr : samlAttrs){
                        String attrName = samlAttr.getName();
                        String attrNS = samlAttr.getNameFormat();
                        List<Object> samlAttrValues = samlAttr.getAttributes();
                        List<String> attrValues = new ArrayList<>();
                        for (Object samlAttrValue : samlAttrValues){
                            attrValues.add(((Element)samlAttrValue).getFirstChild().getNodeValue());
                        }
                        attrs.put(new QName(attrNS, attrName), attrValues);
                    }

                    // for SAML 1.0, 1.1
                    if (subject == null){
                        subject = ((AttributeStatement)s).getSubject();
                    }
                } else if (s instanceof AuthenticationStatement){
                    subject = ((AuthenticationStatement)s).getSubject();
                }
            }

            // Get the user identifier in the Subject:
            if (nameID != null){
                //SAML 2.0 case
                name = nameID.getValue();
                nameNS = nameID.getNameQualifier();
            }else{
                // SAML 1.0, 1.1. case
                NameIdentifier nameIdentifier = subject.getNameIdentifier();
                if (nameIdentifier != null){
                    name = nameIdentifier.getValue();
                    nameNS = nameIdentifier.getNameQualifier();
                }
            }
        }

        String idName = isActAs ? "ActAs" : NAME_IDENTIFIER;
        List<String> nameIds = new ArrayList<>();
        if (name != null){
            nameIds.add(name);
        }
        attrs.put(new QName(nameNS, idName), nameIds);
    }
}
