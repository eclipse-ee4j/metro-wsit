/*
 * Copyright (c) 2010, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * SubjectLocality.java
 *
 * Created on August 18, 2005, 12:34 PM
 *
 */

package com.sun.xml.wss.saml;

/**
 * The <code>SubjectLocality</code> element specifies the DNS domain name
 * and IP address for the system entity that performed the authentication.
 * It exists as part of <code>AuthenticationStatement</code> element.
 *
 * <p>The following schema fragment specifies the expected content contained within
 * SAML SubjectLocality element.
 *
 * <pre>
 * &lt;complexType name="SubjectLocalityType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="DNSAddress" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="IPAddress" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 */
public interface SubjectLocality {

}
