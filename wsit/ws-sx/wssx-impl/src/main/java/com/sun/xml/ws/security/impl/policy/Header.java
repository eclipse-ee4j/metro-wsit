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


import com.sun.xml.ws.policy.AssertionSet;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.PolicyException;
import com.sun.xml.ws.policy.sourcemodel.AssertionData;
import java.util.Collection;
import java.util.Map;
import javax.xml.namespace.QName;
/**
 *
 * @author K.Venugopal@sun.com
 */
public class Header extends PolicyAssertion implements com.sun.xml.ws.security.policy.Header{

    String name ="";
    String uri = "";
    int hashCode = 0;
    /**
     * Creates a new instance of Header
     */
    @Deprecated public Header(String localName , String uri) {
        Map<QName,String> attrs = this.getAttributes();
        attrs.put(NAME,localName);
        attrs.put(URI,uri);
    }

    public Header(AssertionData name,Collection<PolicyAssertion> nestedAssertions, AssertionSet nestedAlternative)  throws PolicyException {
        super(name,nestedAssertions,nestedAlternative);


        String tmp = this.getAttributeValue(NAME);
        if(tmp != null){
            this.name = tmp;
        }

        this.uri = this.getAttributeValue(URI);

        if(uri == null || uri.length() == 0){
            throw new PolicyException("Namespace attribute is required under Header element ");
        }

    }

    public boolean equals(Object object){
        if(object instanceof Header){
            Header header = (Header)object;
            if(header.getLocalName() != null && header.getLocalName().equals(getLocalName())){
                if(header.getURI().equals(getURI())){
                    return true;
                }
            }
        }
        return false;
    }

    public int hashCode(){
        if(hashCode ==0){
            if(uri!=null){
                hashCode =uri.hashCode();
            }
            if(name !=null){
                hashCode =hashCode+name.hashCode();
            }
        }
        return hashCode;
    }

    @Override
    public String getLocalName() {
        return name;
    }

    @Override
    public String getURI() {
        return uri;
    }
}
