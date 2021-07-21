/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.rm.protocol.wsrm200502;

import javax.xml.namespace.QName;
import javax.xml.ws.EndpointReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AcceptType", propOrder = {
"acksTo",
"any"
})
public class AcceptType {

    @XmlElement(name = "AcksTo", namespace = "http://schemas.xmlsoap.org/ws/2005/02/rm")
    protected EndpointReference acksTo;
    @XmlAnyElement(lax = true)
    protected List<Object> any = new ArrayList<Object>();
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the value of the acksTo property.
     *
     * @return
     *     possible object is
     *     {@link com.sun.xml.ws.api.addressing.WSEndpointReference }
     *
     */
    public EndpointReference getAcksTo() {
        /*   for (int i = 0 ; i < any.size(); i++) {
        
        if (any.get(i) instanceof WSEndpointReference) {
        return (WSEndpointReference)any.get(i);
        }
        }
        return null;*/
        return acksTo;
    }

    /**
     * Sets the value of the acksTo property.
     *
     * @param value
     *     allowed object is
     *     {@link com.sun.xml.ws.api.addressing.WSEndpointReference }
     *
     */
    public void setAcksTo(EndpointReference value) {
        //this.any.add(value);
        this.acksTo = value;
    }

    /**
     * Gets the value of the any property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the any property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAny().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * {@link org.w3c.dom.Element }
     * 
     * 
     */
    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<Object>();
        }
        return this.any;
    }

    /**
     * Gets a map that contains attributes that aren't bound to any typed property on this class.
     * 
     * <p>
     * the map is keyed by the name of the attribute and 
     * the value is the string value of the attribute.
     * 
     * the map returned by this method is live, and you can add new attribute
     * by updating the map directly. Because of this design, there's no setter.
     * 
     * 
     * @return
     *     always non-null
     */
    public Map<QName, String> getOtherAttributes() {
        return otherAttributes;
    }
}
