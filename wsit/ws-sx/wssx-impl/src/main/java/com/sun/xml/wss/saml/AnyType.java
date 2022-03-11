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
 * AnyType.java
 *
 * Created on August 18, 2005, 12:06 PM
 *
 */

package com.sun.xml.wss.saml;

import java.util.List;

/**
 * Java content class for anyType complex type.
 * <p>The following schema fragment specifies the expected content contained within this java content object. (defined at  line unknown)
 * <p>
 * <pre>
 * &lt;complexType name="anyType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;any/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 */
public interface AnyType {

    /**
     * Set the content
     * @param content List of contents
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link java.lang.String}
     * {@link java.lang.Object}
     */
    void setContent(List content);
}
