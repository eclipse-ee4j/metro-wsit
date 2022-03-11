/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package common;

import javax.security.auth.Subject;
import com.sun.xml.ws.api.security.trust.*;
import com.sun.xml.wss.saml.Assertion;
import com.sun.xml.wss.saml.AssertionUtil;
import com.sun.xml.wss.saml.Attribute;
import com.sun.xml.wss.saml.AttributeStatement;
import com.sun.xml.wss.saml.AuthenticationStatement;
import com.sun.xml.wss.saml.NameID;
import com.sun.xml.wss.saml.NameIdentifier;
import com.sun.xml.wss.saml.SAMLException;
import java.security.Principal;
import java.util.*;
import javax.xml.namespace.*;
import org.w3c.dom.Element;

public class SampleSTSAttributeProvider implements STSAttributeProvider {

    public Map<QName, List<String>> getClaimedAttributes(Subject subject, String appliesTo, String tokenType, Claims claims){
        String name = null;

        Set<Principal> principals = subject.getPrincipals();
        if (principals != null){
            final Iterator iterator = principals.iterator();
            while (iterator.hasNext()){
                String cnName = principals.iterator().next().getName();
                int pos = cnName.indexOf("=");
                name = cnName.substring(pos+1);
                break;
            }
        }

    Map<QName, List<String>> attrs = new HashMap<QName, List<String>>();

        // Add user id
    QName nameIdQName = new QName("http://sun.com",STSAttributeProvider.NAME_IDENTIFIER);
    List<String> nameIdAttrs = new ArrayList<String>();
    nameIdAttrs.add(name);
    attrs.put(nameIdQName,nameIdAttrs);

         // Add attributes

         // Check if it is the ActAs case
        if ("true".equals(claims.getOtherAttributes().get(new QName("ActAs")))){
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
        } else {
            // Add Role attribute
            QName testQName = new QName("http://sun.com","Role");
            List<String> testAttrs = new ArrayList<String>();
            testAttrs.add(getUserRole(name));
            attrs.put(testQName,testAttrs);
        }

    return attrs;
    }

    private String getUserRole(String userName){
        if ("alice".equals(userName)){
            return "staff ";
        }

        if ("bob".equals(userName)){
            return "manager";
        }

        return "staff";
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
                        List<String> attrValues = new ArrayList<String>();
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
        List<String> nameIds = new ArrayList<String>();
        if (name != null){
            nameIds.add(name);
        }
        attrs.put(new QName(nameNS, idName), nameIds);
    }
}
