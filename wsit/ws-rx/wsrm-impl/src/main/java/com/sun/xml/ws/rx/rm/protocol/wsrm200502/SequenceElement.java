/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.rm.protocol.wsrm200502;

import com.sun.xml.ws.rx.rm.localization.LocalizationMessages;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyAttribute;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * SequenceElement is based on a JAXB Schema Compiler generated class that serializes
 * and deserialized the <code>SequenceType</code> defined in the WS-RM schema.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SequenceType")
@XmlRootElement(name = "Sequence", namespace = "http://schemas.xmlsoap.org/ws/2005/02/rm")
public class SequenceElement {

    @XmlElement(name = "Identifier", namespace = "http://schemas.xmlsoap.org/ws/2005/02/rm")
    protected Identifier identifier;
    @XmlElement(name = "MessageNumber", namespace = "http://schemas.xmlsoap.org/ws/2005/02/rm")
    protected Long messageNumber;
    @XmlElement(name = "LastMessage", namespace = "http://schemas.xmlsoap.org/ws/2005/02/rm")
    protected LastMessage lastMessage;
    @XmlAnyElement(lax = true)
    protected List<Object> any;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<>();

    public SequenceElement() {
    }

    public String getLocalPart() {
        return "Sequence";
    }

    /**
     * Mutator for the Id property.  Maps to the Identifier property in the underlying
     * JAXB class.
     *
     * @param idString The new value.
     */
    public void setId(String idString) {
        Identifier newId = new Identifier();
        newId.setValue(idString);
        setIdentifier(newId);
    }

    /**
     * Accessor for the Id property.  Maps to the Identifier property in the underlying
     * JAXB class
     * @return The sequence id
     */
    public String getId() {
        return getIdentifier().getValue();
    }

    /**
     * Mutator for the Last property that maps to the LastMessage property in the
     * underlying JAXB class
     *
     * @param last The value of the property.
     */
    public void setLast(boolean last) {
        if (last) {
            setLastMessage(new LastMessage());
        } else {
            setLastMessage(null);
        }
    }

    /**
     * Accessor for the Last property that maps to the LastMessage property in the
     * underlying JAXB class
     *
     * @return The value of the property.
     */
    public boolean getLast() {
        return getLastMessage() != null;
    }

    /**
     * Gets the value of the identifier property.
     *
     * @return The property value
     */
    public Identifier getIdentifier() {
        return identifier;
    }

    /**
     * Sets the value of the identifier property.
     *
     * @param value The new value.
     */
    public void setIdentifier(Identifier value) {
        this.identifier = value;
    }

    /**
     * Gets the value of the messageNumber property.
     *
     * @return The value of the property.
     *
     */
    public Long getMessageNumber() {
        return messageNumber;
    }

    /**
     * Sets the value of the messageNumber property.
     *
     * @param value The new value.
     *
     */
    public void setMessageNumber(Long value) {
        this.messageNumber = value;
    }

    /**
     * Accessor for the Number property which maps to the MessageNumber property in
     * the underlying JAXB class.
     *
     * @return The Message number.
     */
    public long getNumber() {
        return getMessageNumber();
    }

    /**
     * Gets the value of the lastMessage property.
     *
     * @return The value of the property
     *          non-null indicates that a Last child will be serialized on
     *          the Sequence element.
     *
     */
    public LastMessage getLastMessage() {
        return lastMessage;
    }

    /**
     * Sets the value of the lastMessage property.
     *
     * @param value The new value.  Either null or a member
     * of the placeholder inner LastMessage class.
     *
     *
     */
    public void setLastMessage(LastMessage value) {
        this.lastMessage = value;
    }

    /**
     * Gets the value of the any property.
     *
     * @return The value of the property.
     *
     *
     */
    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<>();
        }
        return this.any;
    }

    /**
     * Gets a map that contains attributes that aren't bound to any typed property on this class.
     *
     * @return The map of attributes.
     */
    public Map<QName, String> getOtherAttributes() {
        return otherAttributes;
    }

    /**
     * <p>Java class for anonymous complex type.  That acts as a
     * placeholder in the <code>lastMessage</code> field.
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class LastMessage {
    }

    @Override
    public String toString() {
        return LocalizationMessages.WSRM_4005_SEQUENCE_TOSTRING_STRING(getId(), getNumber(), getLast() ? "true" : "false");
    }
}

