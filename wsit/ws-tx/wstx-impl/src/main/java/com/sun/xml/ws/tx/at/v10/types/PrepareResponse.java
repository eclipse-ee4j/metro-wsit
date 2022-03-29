/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.at.v10.types;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>{@code
 * <complexType>
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <attribute name="vote" type="{http://schemas.xmlsoap.org/ws/2004/10/wsat}Vote" />
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "PrepareResponse")
public class PrepareResponse {

    @XmlAttribute
    protected Vote vote;

    /**
     * Default constructor.
     */
    public PrepareResponse() {}

    /**
     * Gets the value of the vote property.
     *
     * @return
     *     possible object is
     *     {@link Vote }
     *
     */
    public Vote getVote() {
        return vote;
    }

    /**
     * Sets the value of the vote property.
     *
     * @param value
     *     allowed object is
     *     {@link Vote }
     *
     */
    public void setVote(Vote value) {
        this.vote = value;
    }

}
