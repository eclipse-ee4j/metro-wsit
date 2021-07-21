/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.coord.common.types;

import javax.xml.namespace.QName;
import java.util.Map;

/**
     * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>{@code
 * <complexType>
 *   <simpleContent>
 *     <extension base="<http://www.w3.org/2001/XMLSchema>anyURI">
 *     </extension>
 *   </simpleContent>
 * </complexType>
 * }</pre>
 *
 *
 */
public abstract class BaseIdentifier<I> {

    protected I delegate;

    protected BaseIdentifier(I delegate) {
        this.delegate = delegate;
    }

    public I getDelegate() {
        return delegate;
    }

    /**
     * Gets the value of the value property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public abstract String getValue();

    /**
     * Sets the value of the value property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public abstract void setValue(String value);

    public abstract Map<QName, String> getOtherAttributes();

    public abstract QName getQName();


}
