/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.opt.impl.incoming;

import com.sun.xml.ws.security.opt.api.PolicyBuilder;
import com.sun.xml.ws.security.opt.api.SecurityElementWriter;
import com.sun.xml.ws.security.opt.api.SecurityHeaderElement;
import com.sun.xml.ws.security.opt.impl.JAXBFilterProcessingContext;
import com.sun.xml.ws.security.opt.impl.incoming.processor.ReferenceListProcessor;
import com.sun.xml.wss.impl.policy.mls.EncryptionPolicy;
import com.sun.xml.wss.impl.policy.mls.WSSPolicy;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

/**
 *
 * @author Ashutosh.Shahi@sun.com
 */
public class ReferenceListHeader implements SecurityHeaderElement, SecurityElementWriter, PolicyBuilder{

    //private static String DATA_REFERENCE = "DataReference".intern();
    private static final int DATA_REFERENCE_ELEMENT = 1;

    private String id = "";
    private String namespaceURI = "";
    private String localName = "";
    private JAXBFilterProcessingContext pc = null;
    private ArrayList<String> referenceList = null;
    private ArrayList<String> pendingRefList = null;

    private EncryptionPolicy encPolicy = null;

    /** Creates a new instance of ReferenceListHeader */
    public ReferenceListHeader(XMLStreamReader reader,JAXBFilterProcessingContext pc) throws XMLStreamException{
        this.pc = pc;
        encPolicy = new EncryptionPolicy();
        encPolicy.setFeatureBinding(new EncryptionPolicy.FeatureBinding());
        process(reader);
    }

    @Override
    public boolean refersToSecHdrWithId(String id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(final String id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getNamespaceURI() {
        return namespaceURI;
    }

    @Override
    public String getLocalPart() {
        return localName;
    }

    @Override
    public XMLStreamReader readHeader() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void writeTo(OutputStream os) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void writeTo(XMLStreamWriter streamWriter) {
        throw new UnsupportedOperationException();
    }

    public List<String> getReferenceList() {
        return referenceList;
    }

    public List<String> getPendingReferenceList() {
        return pendingRefList;
    }
    @SuppressWarnings("unchecked")
    private void process(XMLStreamReader reader) throws XMLStreamException{
        id = reader.getAttributeValue(null,"Id");
        namespaceURI = reader.getNamespaceURI();
        localName = reader.getLocalName();

        ReferenceListProcessor rlp = new ReferenceListProcessor(encPolicy);
        rlp.process(reader);
        referenceList = rlp.getReferences();
        pendingRefList = (ArrayList<String>) referenceList.clone();
    }

    @Override
    public void writeTo(javax.xml.stream.XMLStreamWriter streamWriter, HashMap props) {
        throw new UnsupportedOperationException();
    }

    @Override
    public WSSPolicy getPolicy() {
        return encPolicy;
    }

}
