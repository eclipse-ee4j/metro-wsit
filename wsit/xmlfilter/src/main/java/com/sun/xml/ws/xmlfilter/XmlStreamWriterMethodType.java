/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.xmlfilter;

/**
 *
 *
 * @author Marek Potociar (marek.potociar at sun.com)
 */
public enum XmlStreamWriterMethodType {

    WRITE_START_DOCUMENT("writeStartDocument", true),
    WRITE_END_DOCUMENT("writeEndDocument", true),
    WRITE_START_ELEMENT("writeStartElement", true),
    WRITE_END_ELEMENT("writeEndElement", true),
    WRITE_EMPTY_ELEMENT("writeEmptyElement", true),
    WRITE_ATTRIBUTE("writeAttribute", true),
    WRITE_CHARACTERS("writeCharacters", true),
    WRITE_PROCESSING_INSTRUCTION("writeProcessingInstruction", true),
    WRITE_ENTITY_REFERENCE("writeEntityRef", true),
    WRITE_CDATA("writeCData", true),
    WRITE_COMMENT("writeComment", true),
    WRITE_DTD("writeDTD", true),
    WRITE_DEFAULT_NAMESPACE("writeDefaultNamespace", true),
    WRITE_NAMESPACE("writeNamespace", true),
    //
    GET_NAMESPACE_CONTEXT("getNamespaceContext", false),
    GET_PREFIX("getPrefix", false),
    GET_PROPERTY("getProperty", false),
    //
    SET_DEFAULT_NAMESPACE("setDefaultNamespace", true),
    SET_NAMESPACE_CONTEXT("setNamespaceContext", true),
    SET_PREFIX("setPrefix", true),
    //
    CLOSE("close", false),
    FLUSH("flush", true),
    //
    UNKNOWN("", true);

    static XmlStreamWriterMethodType getMethodType(final String methodName) {
        if (methodName != null && methodName.length() > 0) {
            for (XmlStreamWriterMethodType type : values()) {
                if (type.methodName.equals(methodName)) {
                    return type;
                }
            }
        }
        return UNKNOWN;
    }
    private String methodName;
    private boolean filterable;

    private XmlStreamWriterMethodType(String methodName, boolean isFilterable) {
        this.methodName = methodName;
        this.filterable = isFilterable;
    }

    public String getMethodName() {
        return methodName;
    }

    public boolean isFilterable() {
        return filterable;
    }
}
