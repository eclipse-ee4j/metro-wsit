/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.impl.callback;

import com.sun.xml.wss.saml.AuthorityBinding;
import javax.security.auth.callback.Callback;
import org.w3c.dom.Element;
import javax.xml.stream.XMLStreamReader;

public class SAMLCallback extends XWSSCallback implements Callback {

    Element assertion;
    Element authorityBinding;
    //Assertion jaxbAssertion;
    AuthorityBinding authorityInfo;
    XMLStreamReader assertionStream;
    String confirmation = null;
    String version = null;
    String assertionId = null;
    public static final String SV_ASSERTION_TYPE = "SV-Assertion";
    public static final String HOK_ASSERTION_TYPE = "HOK-Assertion";
    public static final String V10_ASSERTION = "SAML10Assertion";
    public static final String V11_ASSERTION = "SAML11Assertion";
    public static final String V20_ASSERTION = "SAML20Assertion";

    /** Creates a new instance of SAMLCallback */
    public SAMLCallback() {
    }

    public void setAssertionElement(Element samlAssertion) {
        assertion = samlAssertion;
    }

    public void setAssertionReader(XMLStreamReader samlAssertion) {
        this.assertionStream = samlAssertion;
    }

    public Element getAssertionElement() {
        return assertion;
    }

    public XMLStreamReader getAssertionReader() {
        return this.assertionStream;
    }

    public void setAuthorityBindingElement(Element authority) {
        authorityBinding = authority;
    }

    public Element getAuthorityBindingElement() {
        return authorityBinding;
    }

    public AuthorityBinding getAuthorityBinding() {
        return authorityInfo;
    }

    public void setAuthorityBinding(AuthorityBinding auth) {
        authorityInfo = auth;
    }

    public void setConfirmationMethod(String meth) {
        confirmation = meth;
    }

    public String getConfirmationMethod() {
        return confirmation;
    }

    public String getSAMLVersion() {
        return version;
    }

    public void setSAMLVersion(String ver) {
        version = ver;
    }

    public void setAssertionId(String id) {
        assertionId = id;
    }

    public String getAssertionId() {
        return assertionId;
    }
}
