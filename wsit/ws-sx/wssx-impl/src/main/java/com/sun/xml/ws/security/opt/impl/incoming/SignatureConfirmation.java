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

import com.sun.xml.stream.buffer.XMLStreamBuffer;
import com.sun.xml.ws.security.opt.api.NamespaceContextInfo;
import com.sun.xml.ws.security.opt.api.PolicyBuilder;
import com.sun.xml.ws.security.opt.api.SecurityElementWriter;
import com.sun.xml.ws.security.opt.api.SecurityHeaderElement;
import com.sun.xml.ws.security.opt.api.TokenValidator;
import com.sun.xml.wss.ProcessingContext;
import com.sun.xml.wss.XWSSecurityException;
import com.sun.xml.wss.impl.MessageConstants;
import com.sun.xml.wss.impl.policy.mls.SignatureConfirmationPolicy;
import com.sun.xml.wss.impl.policy.mls.WSSPolicy;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.stream.XMLStreamReader;
import com.sun.xml.stream.buffer.stax.StreamReaderBufferCreator;
import javax.xml.stream.XMLInputFactory;
import com.sun.xml.ws.security.opt.impl.util.XMLStreamReaderFactory;
import javax.xml.stream.XMLStreamException;
import java.util.HashMap;
import javax.xml.stream.StreamFilter;
import com.sun.xml.stream.buffer.XMLStreamBufferMark;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.sun.xml.wss.logging.LogDomainConstants;

/**
 *
 * @author Ashutosh.Shahi@sun.com
 */
public class SignatureConfirmation implements SecurityHeaderElement, TokenValidator, PolicyBuilder, NamespaceContextInfo, SecurityElementWriter{

    protected static final Logger log = Logger.getLogger(
            LogDomainConstants.FILTER_DOMAIN,
            LogDomainConstants.FILTER_DOMAIN_BUNDLE);

    private String id = "";
    private String namespaceURI = "";
    private String localName = "";
    private String signatureValue = null;

    private SignatureConfirmationPolicy scPolicy = null;
    private HashMap<String,String> nsDecls;
    private XMLStreamBuffer mark = null;

    /**
     * Creates a new instance of SignatureConfirmation
     */
    @SuppressWarnings("unchecked")
    public SignatureConfirmation(XMLStreamReader reader,StreamReaderBufferCreator creator,HashMap nsDecls, XMLInputFactory  staxIF) throws XMLStreamException{

        namespaceURI = reader.getNamespaceURI();
        localName = reader.getLocalName();
        id = reader.getAttributeValue(MessageConstants.WSU_NS,"Id");

        mark = new XMLStreamBufferMark(nsDecls,creator);
        creator.createElementFragment(XMLStreamReaderFactory.createFilteredXMLStreamReader(reader,new SCProcessor()),false);

        this.nsDecls = nsDecls;

        scPolicy = new SignatureConfirmationPolicy();
        scPolicy.setSignatureValue(signatureValue);
    }

    public String getSignatureValue(){
        return signatureValue;
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
    public void setId(String id) {
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
    public javax.xml.stream.XMLStreamReader readHeader() throws javax.xml.stream.XMLStreamException {
        return mark.readAsXMLStreamReader();
    }

    @Override
    public void writeTo(OutputStream os) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void writeTo(javax.xml.stream.XMLStreamWriter streamWriter) throws javax.xml.stream.XMLStreamException {
        mark.writeToXMLStreamWriter(streamWriter);
    }

    @Override
    public void validate(ProcessingContext context) throws XWSSecurityException {
        Object temp = context.getExtraneousProperty("SignatureConfirmation");
        List scList = null;
        if(temp != null && temp instanceof ArrayList)
            scList = (ArrayList)temp;
        if(scList != null){
            if(signatureValue == null){
                if(!scList.isEmpty()){
                    log.log(Level.SEVERE, com.sun.xml.wss.logging.impl.filter.LogStringsMessages.WSS_1435_SIGNATURE_CONFIRMATION_VALIDATION_FAILURE());
                    throw new XWSSecurityException("Failure in SignatureConfirmation Validation");
                }
            }else if(scList.contains(signatureValue)){// match the Value in received message
                //with the stored value
                scList.remove(signatureValue);
            }else{
                log.log(Level.SEVERE, com.sun.xml.wss.logging.impl.filter.LogStringsMessages.WSS_1435_SIGNATURE_CONFIRMATION_VALIDATION_FAILURE());
                throw new XWSSecurityException("Mismatch in SignatureConfirmation Element");
            }
        }
    }

    @Override
    public WSSPolicy getPolicy() {
        return scPolicy;
    }

    @Override
    public HashMap<String, String> getInscopeNSContext() {
        return nsDecls;
    }

    @Override
    public void writeTo(javax.xml.stream.XMLStreamWriter streamWriter, HashMap props) {
        throw new UnsupportedOperationException();
    }

    class SCProcessor implements StreamFilter{
        boolean elementRead = false;
        @Override
        public boolean accept(XMLStreamReader reader){
            if(reader.getEventType() == XMLStreamReader.END_ELEMENT ){
                if(reader.getLocalName() == localName && reader.getNamespaceURI() == namespaceURI){
                    elementRead = true;
                }
            }
            if(!elementRead && reader.getEventType() == XMLStreamReader.START_ELEMENT){
                signatureValue = reader.getAttributeValue(null,"Value");
            }
            return true;
        }
    }

}
