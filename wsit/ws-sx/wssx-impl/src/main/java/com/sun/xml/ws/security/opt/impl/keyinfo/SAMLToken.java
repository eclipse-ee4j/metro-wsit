/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.opt.impl.keyinfo;

import com.sun.xml.ws.security.opt.api.SecurityElementWriter;
import com.sun.xml.ws.security.opt.api.SecurityHeaderElement;
import com.sun.xml.ws.security.opt.impl.util.JAXBUtil;
import com.sun.xml.wss.saml.Assertion;
import java.io.OutputStream;
import java.util.HashMap;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import com.sun.xml.ws.api.SOAPVersion;
/**
 *
 * @author K.Venugopal@sun.com
 */
public class SAMLToken implements SecurityHeaderElement, SecurityElementWriter {
    private Assertion samlToken =null;
    private JAXBContext jxbContext = null;
    private SOAPVersion soapVersion = null;
    /** Creates a new instance of SAMLToken */
    public SAMLToken(Assertion assertion,JAXBContext jxbContext,SOAPVersion soapVersion) {
        this.samlToken = assertion;
        this.jxbContext = jxbContext;
        this.soapVersion = soapVersion;

    }

    @Override
    public boolean refersToSecHdrWithId(String id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getId() {
        return samlToken.getAssertionID();
    }

    @Override
    public void setId(String id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getNamespaceURI() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getLocalPart() {
        throw new UnsupportedOperationException();
    }

    @Override
    public XMLStreamReader readHeader() {
        throw new UnsupportedOperationException();
    }
    /**
     * writes the SAML assertion to the XMLStreamWriter
     * @param streamWriter XMLStreamWriter
     */
    @Override
    public void writeTo(XMLStreamWriter streamWriter) throws XMLStreamException {
        try{
            Marshaller marshaller = jxbContext.createMarshaller();
            if(SOAPVersion.SOAP_11 == soapVersion){
                marshaller.setProperty("org.glassfish.jaxb.namespacePrefixMapper", JAXBUtil.prefixMapper11);
            }else{
                marshaller.setProperty("org.glassfish.jaxb.namespacePrefixMapper", JAXBUtil.prefixMapper12);
            }
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT,true);
            marshaller.setProperty("org.glassfish.jaxb.xmlDeclaration", false);
            marshaller.marshal(samlToken,streamWriter);

        }catch(jakarta.xml.bind.PropertyException pe){
            //log here
            throw new XMLStreamException("Error occurred while setting security marshaller properties",pe);
        }catch(JAXBException je){
            //log here
            throw new XMLStreamException("Error occurred while marshalling SAMLAssertion",je);
        }
    }

    @Override
    public void writeTo(XMLStreamWriter streamWriter, HashMap props) {
    }

    @Override
    public void writeTo(OutputStream os) {
    }

}
