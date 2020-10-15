/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
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
import com.sun.xml.wss.saml.util.SAMLUtil;
import java.util.*;
import javax.xml.namespace.*;
import javax.xml.stream.XMLStreamReader;
import org.w3c.dom.Element;

/**
 *  <wst:Claims Dialect=?http://schemas.xmlsoap.org/ws/2005/05/identity
 *       xmlns:wst="http://docs.oasis-open.org/ws-sx/ws-trust/200512"
 *       xmlns:ic="http://schemas.xmlsoap.org/ws/2005/05/identity">
 *      <ic:ClaimType Uri=?http://schemas.xmlsoap.org/ws/2005/05/identity/claims/locality?/>
 *      <ic:ClaimType Uri=?http://schemas.xmlsoap.org/ws/2005/05/identity/claims/role?/>
 *  </wst:Claims>
 * @author jdg
 */

public class SampleSTSAttributeProvider implements STSAttributeProvider {
    
    public Map<QName, List<String>> getClaimedAttributes(Subject subj, String appliesTo, String tokenType, Claims claims){
        String role = null;
        String id = null;
        String locality = null;

        // process the SAML assertion in the subject
        Set<Object> set = subj.getPublicCredentials();
        Assertion assertion = null;
        for (Object obj : set) {
            if (obj instanceof XMLStreamReader) {
                XMLStreamReader reader = (XMLStreamReader) obj;
                try {
                    assertion = AssertionUtil.fromElement(SAMLUtil.createSAMLAssertion(reader));
                }catch(Exception ex){
                    throw new RuntimeException(ex);
                }
            }
        }
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
                List<Attribute> attrs = ((AttributeStatement)s).getAttributes();
                for (Attribute attr : attrs){
                    String attrName = attr.getName();
                    List<Object> attrValues = attr.getAttributes();

                    Element attrValue = (Element)attrValues.get(0);
                    String attrStrValue = attrValue.getFirstChild().getNodeValue();
                    if ("Role".equals(attrName)){
                        role = attrStrValue;
                    }else if ("Locality".equals(attrName)){
                        locality = attrStrValue;
                    }
                }
                // For SAML v.1.1 or 1.0
                if (subject == null){
                    subject = ((AttributeStatement)s).getSubject();
                }
            }else if (s instanceof AuthenticationStatement){
                subject = ((AuthenticationStatement)s).getSubject();
            }
        }
      
        if (nameID != null){
             //SAML 2.0 case
             id = nameID.getValue();
        }else{
            // SAML 1.0, 1.1. case
            NameIdentifier nameIdentifier = subject.getNameIdentifier();
            if (nameIdentifier != null){
                id = nameIdentifier.getValue();
            }
        }
        
        // Create the attributes for new SAML Assertion
        Map<QName, List<String>> attrs = new HashMap<QName, List<String>>();

        QName nameIdQName = new QName("http://sun.com",STSAttributeProvider.NAME_IDENTIFIER);
        List<String> nameIdAttrs = new ArrayList<String>();
        nameIdAttrs.add(id);
        attrs.put(nameIdQName,nameIdAttrs);

        if (claims != null){
            MyClaims myClaims = new MyClaims(claims);
            List<String> claimTypes = myClaims.getClaimsTypes();
            for (String claimType : claimTypes){
                if (MyClaims.ROLE.equals(claimType)){
                    QName testQName = new QName("http://sun.com","Role");
                    List<String> testAttrs = new ArrayList<String>();
                    testAttrs.add(role);
                    attrs.put(testQName,testAttrs);
                } else if (MyClaims.LOCALITY.equals(claimType)){
                    QName testQName = new QName("http://sun.com","Locality");
                    List<String> testAttrs = new ArrayList<String>();
                    testAttrs.add(locality);
                    attrs.put(testQName,testAttrs);
                }
            }
        }
        return attrs;
    }  
}
