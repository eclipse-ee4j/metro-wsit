/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sun.xml.wss.provider.wsit;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.server.WSEndpoint;
import com.sun.xml.security.core.ai.IdentityType;
import com.sun.xml.stream.buffer.XMLStreamBufferResult;
import com.sun.xml.ws.api.addressing.WSEndpointReference;
import com.sun.xml.ws.api.addressing.WSEndpointReference.EPRExtension;
import com.sun.xml.ws.api.server.EndpointReferenceExtensionContributor;
import com.sun.xml.ws.security.opt.impl.util.CertificateRetriever;
import com.sun.xml.ws.security.impl.policy.PolicyUtil;
import com.sun.xml.ws.security.opt.impl.util.JAXBUtil;
import com.sun.xml.ws.security.secext10.BinarySecurityTokenType;
import com.sun.xml.wss.XWSSecurityException;
import com.sun.xml.wss.impl.MessageConstants;

import com.sun.xml.wss.impl.misc.SecurityUtil;
import com.sun.xml.wss.logging.LogDomainConstants;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 *
 * @author suresh
 */
public class IdentityEPRExtnContributor extends EndpointReferenceExtensionContributor {

    private Certificate cs = null;
    QName ID_QNAME = null;
    private static Logger log = Logger.getLogger(LogDomainConstants.WSS_API_DOMAIN,
            LogDomainConstants.WSS_API_DOMAIN_BUNDLE);

    public IdentityEPRExtnContributor() {
    }

     @SuppressWarnings("unchecked")
    public <T> T getSPI(@NotNull Class<T> spiType) {
        //if id policy enabled &&
        if (spiType.isAssignableFrom(EndpointReferenceExtensionContributor.class)) {
            return (T) new IdentityEPRExtnContributor();

        }
        return null;
    }

    public EPRExtension getEPRExtension(WSEndpoint wse, WSEndpointReference.EPRExtension extension) {

        if (extension != null) {
            return extension;
        }
        QName eprQName = new QName("http://schemas.sun.com/2006/03/wss/server", "EnableEPRIdentity");
        CertificateRetriever cr = new CertificateRetriever();
        boolean found = cr.checkforEPRIdentity(wse, eprQName);
        if (found == false) {
            return null;
        } else {
            //log.log(Level.INFO, "EnableEPRIdentity assertion is enabled");
            try {
                URL url = SecurityUtil.loadFromClasspath("META-INF/ServerCertificate.cert");
                if (url != null) {
                    CertificateFactory certFact = CertificateFactory.getInstance("X.509");
                    InputStream is = url.openStream();
                    this.cs = certFact.generateCertificate(is);
                    is.close();
                } else {
                    cs = cr.getServerKeyStore(wse);
                    if (cs == null) {
                        return null;
                    }
                }
            } catch (CertificateException ex) {
                log.log(Level.SEVERE,com.sun.xml.wss.logging.LogStringsMessages.WSS_0818_ERROR_PUTTING_CERTIFICATE_EPRIDENTITY(), ex);

                throw new RuntimeException(ex);
            } catch (IOException ex) {
                log.log(Level.SEVERE, com.sun.xml.wss.logging.LogStringsMessages.WSS_0818_ERROR_PUTTING_CERTIFICATE_EPRIDENTITY(), ex);
                throw new RuntimeException(ex);
            } catch (XWSSecurityException ex) {
                log.log(Level.SEVERE, com.sun.xml.wss.logging.LogStringsMessages.WSS_0818_ERROR_PUTTING_CERTIFICATE_EPRIDENTITY(), ex);
                throw new RuntimeException(ex);
            }
        }       

        return new EPRExtension() {

            public XMLStreamReader readAsXMLStreamReader() throws XMLStreamException {
                XMLStreamReader reader = null;
                try {

                    String id = PolicyUtil.randomUUID();
                    BinarySecurityTokenType bst = new BinarySecurityTokenType();
                    bst.setValueType(MessageConstants.X509v3_NS);
                    bst.setId(id);
                    bst.setEncodingType(MessageConstants.BASE64_ENCODING_NS);
                    if (cs != null) {
                        bst.setValue(cs.getEncoded());
                    }
                    JAXBElement<BinarySecurityTokenType> bstElem = new com.sun.xml.ws.security.secext10.ObjectFactory().createBinarySecurityToken(bst);
                    IdentityType identityElement = new IdentityType();
                    identityElement.getDnsOrSpnOrUpn().add(bstElem);

                    reader = readHeader(identityElement);

                } catch (CertificateEncodingException ex) {
                    log.log(Level.SEVERE, com.sun.xml.wss.logging.LogStringsMessages.WSS_0818_ERROR_PUTTING_CERTIFICATE_EPRIDENTITY(), ex);
                    throw new RuntimeException(ex);
                }
                return reader;
            }

            public QName getQName() {               
                return new QName("http://schemas.xmlsoap.org/ws/2006/02/addressingidentity", "Identity");
            }
        };
    }

    public QName getQName() {
        return ID_QNAME;
    }

    public XMLStreamReader readHeader(IdentityType identityElem) throws XMLStreamException {
        XMLStreamBufferResult xbr = new XMLStreamBufferResult();
        JAXBElement<IdentityType> idElem =
                (new com.sun.xml.security.core.ai.ObjectFactory()).createIdentity(identityElem);
        try {
            JAXBContext context = JAXBUtil.getCustomIdentityJAXBContext(); 
            Marshaller m = context.createMarshaller();
            m.setProperty("com.sun.xml.bind.xmlDeclaration", false);
            m.marshal(idElem, xbr);
        } catch (JAXBException je) {
            log.log(Level.SEVERE, com.sun.xml.wss.logging.LogStringsMessages.WSS_0818_ERROR_PUTTING_CERTIFICATE_EPRIDENTITY(), je);
            throw new XMLStreamException(je);
        }
        return xbr.getXMLStreamBuffer().readAsXMLStreamReader();
    }
}
