/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0-b24-ea3
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2006.02.03 at 04:10:33 PM IST
//


package com.sun.xml.security.core.xenc;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlElementRefs;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ReferenceList element declaration.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;element name="ReferenceList"&gt;
 *   &lt;complexType&gt;
 *     &lt;complexContent&gt;
 *       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *         &lt;choice maxOccurs="unbounded"&gt;
 *           &lt;element name="DataReference" type="{http://www.w3.org/2001/04/xmlenc#}ReferenceType"/&gt;
 *           &lt;element name="KeyReference" type="{http://www.w3.org/2001/04/xmlenc#}ReferenceType"/&gt;
 *         &lt;/choice&gt;
 *       &lt;/restriction&gt;
 *     &lt;/complexContent&gt;
 *   &lt;/complexType&gt;
 * &lt;/element&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "dataReferenceOrKeyReference"
})
@XmlRootElement(name = "ReferenceList")
public class ReferenceList {

    @XmlElementRefs({
        @XmlElementRef(name = "KeyReference", namespace = "http://www.w3.org/2001/04/xmlenc#", type = JAXBElement.class),
        @XmlElementRef(name = "DataReference", namespace = "http://www.w3.org/2001/04/xmlenc#", type = JAXBElement.class)
    })
    protected List<JAXBElement<ReferenceType>> dataReferenceOrKeyReference;

    public ReferenceList() {
    }

    /**
     * Gets the value of the dataReferenceOrKeyReference property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dataReferenceOrKeyReference property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDataReferenceOrKeyReference().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link ReferenceType }{@code >}
     * {@link JAXBElement }{@code <}{@link ReferenceType }{@code >}
     *
     *
     */
    public List<JAXBElement<ReferenceType>> getDataReferenceOrKeyReference() {
        if (dataReferenceOrKeyReference == null) {
            dataReferenceOrKeyReference = new ArrayList<>();
        }
        return this.dataReferenceOrKeyReference;
    }

    public void setDataReferenceOrKeyReference(List<JAXBElement<ReferenceType>> dataReferenceOrKeyReference) {
        this.dataReferenceOrKeyReference = dataReferenceOrKeyReference;
    }

}
