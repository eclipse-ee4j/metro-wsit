/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.opt.impl.crypto;

import com.sun.xml.ws.api.message.Header;
import com.sun.xml.ws.message.jaxb.JAXBHeader;
import com.sun.xml.ws.security.opt.api.SecurityElement;
import com.sun.xml.ws.security.opt.api.SecurityElementWriter;
import com.sun.xml.wss.XWSSecurityException;
import com.sun.xml.ws.security.opt.crypto.JAXBData;
import com.sun.xml.wss.logging.LogDomainConstants;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.jvnet.staxex.NamespaceContextEx;
import com.sun.xml.wss.logging.impl.opt.LogStringsMessages;

/**
 *
 * @author K.Venugopal@sun.com
 */
public class JAXBDataImpl implements JAXBData {

    private static final Logger logger = Logger.getLogger(LogDomainConstants.IMPL_OPT_DOMAIN,
            LogDomainConstants.IMPL_OPT_DOMAIN_BUNDLE);

    private JAXBElement jb;
    private JAXBContext jc= null;
    private Header header = null;
    private SecurityElement securityElement = null;
    private boolean contentOnly = false;
    private NamespaceContextEx nsContext = null;

    /** Creates a new instance of JAXBDataImpl */

    public JAXBDataImpl(JAXBElement jb,JAXBContext jc,boolean contentOnly, NamespaceContextEx nsContext) {
        this.jb = jb;
        this.jc = jc;
        this.contentOnly = contentOnly;
        this.nsContext = nsContext;
    }

    public JAXBDataImpl(Header header,boolean contentOnly, NamespaceContextEx nsContext,JAXBContext jcc) {
        this.header = header;
        this.contentOnly = contentOnly;
        this.nsContext = nsContext;
        this.jc = jcc;
    }

    public JAXBDataImpl(SecurityElement se, NamespaceContextEx nsContext,boolean contentOnly) {
        this.securityElement = se;
        this.contentOnly = contentOnly;
        this.nsContext = nsContext;
    }

    /** Creates a new instance of JAXBDataImpl */
    public JAXBDataImpl(JAXBElement jb,JAXBContext jc, NamespaceContextEx nsContext) {
        this.jb = jb;
        this.jc = jc;
        this.nsContext = nsContext;
    }


    public JAXBDataImpl(Header header) {
        this.header = header;
    }

    public JAXBDataImpl(SecurityElement se) {
        this.securityElement = se;
    }

    public JAXBElement getJAXBElement(){
        return jb;
    }

    public void writeTo(XMLStreamWriter writer)throws XWSSecurityException{
        if(securityElement != null){
            try {
                ((SecurityElementWriter)securityElement).writeTo(writer);
            } catch (XMLStreamException ex) {
                logger.log(Level.SEVERE, LogStringsMessages.WSS_1609_ERROR_SERIALIZING_ELEMENT(securityElement.getLocalPart()));
                throw new XWSSecurityException(LogStringsMessages.WSS_1609_ERROR_SERIALIZING_ELEMENT(securityElement.getLocalPart()),ex);
            }
            return;
        }

        if(header != null){
            try {
                header.writeTo(writer);
            } catch (XMLStreamException ex) {
                logger.log(Level.SEVERE, LogStringsMessages.WSS_1609_ERROR_SERIALIZING_ELEMENT(header.getLocalPart()));
                throw new XWSSecurityException(LogStringsMessages.WSS_1609_ERROR_SERIALIZING_ELEMENT(header.getLocalPart()),ex);
            }
            return;
        }

        Marshaller mh;
        try {
            mh = jc.createMarshaller();
            mh.marshal(jb,writer);
        }catch (JAXBException ex) {
            logger.log(Level.SEVERE, LogStringsMessages.WSS_1610_ERROR_MARSHALLING_JBOBJECT(jb.getName()));
            throw new XWSSecurityException(
                    LogStringsMessages.WSS_1610_ERROR_MARSHALLING_JBOBJECT(jb.getName()),ex);
        }
    }

    public void writeTo(OutputStream os) throws XWSSecurityException {
        Marshaller mh;
        try {
            if (header != null && header instanceof JAXBHeader) {
                final JAXBHeader hdr = ((JAXBHeader) header);             
                Object obj = header.readAsJAXB(jc.createUnmarshaller());
                mh = jc.createMarshaller();
                //mh.setProperty("com.sun.xml.bind.c14n", true);
                mh.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
                mh.marshal(obj, os);
            } else {
                mh = jc.createMarshaller();
                mh.setProperty("com.sun.xml.bind.c14n", true);
                mh.marshal(jb, os);
            }

        } catch (jakarta.xml.bind.JAXBException ex) {
            logger.log(Level.SEVERE, LogStringsMessages.WSS_1610_ERROR_MARSHALLING_JBOBJECT(jb.getName()));
            throw new XWSSecurityException(LogStringsMessages.WSS_1610_ERROR_MARSHALLING_JBOBJECT(jb.getName()), ex);
        }
    }

    public NamespaceContextEx getNamespaceContext() {
        return nsContext;
    }

    public SecurityElement getSecurityElement() {
        return securityElement;
    }
}
