/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.rm.protocol.wsrm200502;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.xml.bind.annotation.XmlAnyAttribute;
import jakarta.xml.bind.annotation.XmlAnyElement;
import javax.xml.namespace.QName;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "AckRequested", namespace = "http://schemas.xmlsoap.org/ws/2005/02/rm")
public class AckRequestedElement {

    @XmlElement(name = "Identifier", namespace = "http://schemas.xmlsoap.org/ws/2005/02/rm")
    protected Identifier identifier;
    @XmlElement(name = "MaxMessageNumberUsed", namespace = "http://schemas.xmlsoap.org/ws/2005/02/rm")
    protected BigInteger maxMessageNumberUsed;
    
    @XmlAnyElement(lax = true)
    protected List<Object> any;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    public AckRequestedElement() {

    }

    /*public QName getQName() {
    return  RMBuilder.getConstants().getAckRequestedQName();
    }*/
    //Introduce accessors using simple types rather than BigInteger and
    //Identifier
    public void setId(String id) {
        Identifier idType = new Identifier();
        idType.setValue(id);
        setIdentifier(idType);
    }

    public String getId() {
        return getIdentifier().getValue();
    }

    public void setMaxMessageNumber(long l) {
        setMaxMessageNumberUsed(BigInteger.valueOf(l));
    }

    public long getMaxMessageNumber() {

        BigInteger big;
        if (null == (big = getMaxMessageNumberUsed())) {
            return 0;
        }
        return big.longValue();
    }

    /**
     * Gets the value of the Identifier property.
     * 
     * @return The value of the property
     *     
     */
    public Identifier getIdentifier() {
        return identifier;
    }

    /**
     * Sets the value of the identifier property.
     * 
     * @param value The new value.
     *     
     */
    public void setIdentifier(Identifier value) {
        this.identifier = value;
    }

    /**
     * Gets the value of the maxMessageNumberUsed property.
     * 
     * @return The value of the property.
     *     
     */
    public BigInteger getMaxMessageNumberUsed() {
        return maxMessageNumberUsed;
    }

    /**
     * Sets the value of the maxMessageNumberUsed property.
     * 
     * @param value The new value.
     */
    public void setMaxMessageNumberUsed(BigInteger value) {
        this.maxMessageNumberUsed = value;
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
     * {@link Element }
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

