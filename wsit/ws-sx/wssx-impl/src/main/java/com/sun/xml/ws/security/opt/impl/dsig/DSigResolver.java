/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.opt.impl.dsig;

import com.sun.xml.ws.api.message.Attachment;
import com.sun.xml.ws.security.opt.api.SecurityElement;
import com.sun.xml.ws.security.opt.api.SecurityHeaderElement;
import com.sun.xml.ws.security.opt.impl.crypto.SSBData;
import com.sun.xml.ws.security.opt.impl.message.SOAPBody;
import com.sun.xml.ws.security.opt.impl.message.SecuredMessage;
import java.util.ArrayList;
import javax.xml.crypto.Data;
import javax.xml.crypto.URIDereferencer;
import javax.xml.crypto.URIReference;
import javax.xml.crypto.URIReferenceException;
import javax.xml.crypto.XMLCryptoContext;
import com.sun.xml.ws.security.opt.crypto.JAXBData;

import javax.xml.stream.XMLStreamException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.HashMap;
import com.sun.xml.ws.security.opt.impl.crypto.JAXBDataImpl;
import com.sun.xml.wss.impl.MessageConstants;
import com.sun.xml.ws.security.opt.impl.JAXBFilterProcessingContext;
import com.sun.xml.wss.logging.LogDomainConstants;
import com.sun.xml.wss.logging.impl.opt.signature.LogStringsMessages;
import com.sun.xml.ws.security.opt.impl.util.JAXBUtil;
import com.sun.xml.ws.security.opt.impl.outgoing.SecurityHeader;
import com.sun.xml.wss.XWSSecurityException;
import com.sun.xml.ws.api.message.Header;
import com.sun.xml.ws.security.opt.impl.crypto.AttachmentData;
import com.sun.xml.wss.impl.policy.mls.AuthenticationTokenPolicy;

/**
 * Implementation of JSR 105 URIDereferencer interface for optimized path
 *
 * @author Ashutosh.Shahi@Sun.com
 */
public class DSigResolver implements URIDereferencer {

    private static DSigResolver resolver = new DSigResolver();
    private static final Logger logger = Logger.getLogger(LogDomainConstants.IMPL_OPT_SIGNATURE_DOMAIN,
            LogDomainConstants.IMPL_OPT_SIGNATURE_DOMAIN_BUNDLE);

    /**
     *
     * create a new instance of this class
     * @return URI Dereferencer instance.
     */
    public static URIDereferencer getInstance() {
        return resolver;
    }

    /**
     * resolve the URI of type "cid:" , "attachmentRef:", "http:", "#xyz".
     * @param uriRef
     * @param context
     * @throws URIReferenceException
     * @return
     */
    public Data dereference(final URIReference uriRef, final XMLCryptoContext context) throws URIReferenceException {
        String uri = null;
        uri = uriRef.getURI();
        if (logger.isLoggable(Level.FINEST)) {
            logger.log(Level.FINEST, LogStringsMessages.WSS_1750_URI_TOBE_DEREFERENCED(uri));
        }
        return dereferenceURI(uri, context);
    }

    /**
     * tries to locate the element being referenced by uri
     * @param url String
     * @param context XMLCryptoContext
     * @return Data
     * @throws URIReferenceException
     */
    private Data dereferenceURI(final String url, final XMLCryptoContext context) throws URIReferenceException {
        try {
            String uri = url;
            if (uri.startsWith("#SAML")) {
                AuthenticationTokenPolicy.SAMLAssertionBinding resolvedSAMLBinding =
                        (AuthenticationTokenPolicy.SAMLAssertionBinding) context.getProperty("SAML_CLIENT_CACHE");
                if (resolvedSAMLBinding != null) {
                    String id = resolvedSAMLBinding.getAssertionId();
                    uri = id;
                }
            }
            if (uri == null || uri.equals("")) {
                logger.log(Level.FINEST, "Empty Reference URI not supported");
                throw new UnsupportedOperationException("Empty Reference URI not supported");
            } else if (uri.charAt(0) == '#') {
                return dereferenceFragment(getIdFromFragmentRef(uri), context);
            } else if (uri.startsWith("cid:") || uri.startsWith("attachmentRef:")) {
                //throw new UnsupportedOperationException("Not supported in optimized path");
                return dereferenceAttachments(uri, context);
            } else if (uri.startsWith("http")) {
                throw new UnsupportedOperationException("Not supported in optimized path");
            } else {
                return dereferenceFragment(uri, context);
            }
        } catch (Exception e) {
            throw new URIReferenceException(e);
        }
    }

    /**
     * tries to locate the attachments referenced by the uri
     * @param uri String
     * @param context XMLCryptoContext
     * @return  Data
     * @throws URIReferenceException
     */
    Data dereferenceAttachments(final String uri, final XMLCryptoContext context) throws URIReferenceException {
        JAXBFilterProcessingContext filterContext = (JAXBFilterProcessingContext) context.get(MessageConstants.WSS_PROCESSING_CONTEXT);
        SecuredMessage secureMsg = filterContext.getSecuredMessage();
        Attachment attachment = secureMsg.getAttachment(uri);
        if (attachment == null) {
            throw new URIReferenceException("Attachment Resource with Identifier  " + uri + " was not found");
        }
        AttachmentData attachData = new AttachmentData(attachment);
        return attachData;
    }

    /**
     * tries to dereference the uri
     * @param uri String
     * @param context XMLCryptoContext
     * @return Data
     * @throws XWSSecurityException
     */
    Data dereferenceFragment(final String uri, final XMLCryptoContext context) throws XWSSecurityException {
        JAXBFilterProcessingContext filterContext = (JAXBFilterProcessingContext) context.get(MessageConstants.WSS_PROCESSING_CONTEXT);
        HashMap elementCache = filterContext.getElementCache();
        try {
            if (elementCache.size() > 0) {
                Object obj = elementCache.get(uri);
                if (obj != null && obj instanceof Header) {
                    Header reqdHeader = (Header) obj;
                    JAXBContext jaxbContext = JAXBUtil.getJAXBContext();
                    JAXBElement jb = reqdHeader.readAsJAXB(jaxbContext.createUnmarshaller());
                    JAXBData jData = new JAXBDataImpl(jb, jaxbContext, filterContext.getNamespaceContext());
                    return jData;
                }
            }

            return getDataById(filterContext, uri);
        } catch (JAXBException jbe) {
            throw new XWSSecurityException(jbe);
        } catch (XMLStreamException sxe) {
            throw new XWSSecurityException(sxe);
        }
    }

    private static String getIdFromFragmentRef(final String ref) {
        char start = ref.charAt(0);
        if (start == '#') {
            return ref.substring(1);
        }
        return ref;
    }

    /**
     * gets the data referenced by the uri
     * @param context JAXBFilterProcessingContext
     * @param uri String
     * @return Data
     * @throws JAXBException
     * @throws XMLStreamException
     * @throws XWSSecurityException
     */
    private Data getDataById(final JAXBFilterProcessingContext context,
            final String uri) throws JAXBException, XMLStreamException,
            XWSSecurityException {
        SecuredMessage secMessage = context.getSecuredMessage();
        ArrayList headerList = secMessage.getHeaders();
        // Look for Id or wsu:Id attribute in all elements
        SecurityHeaderElement reqdHeader = null;
        for (int i = 0; i < headerList.size(); i++) {
            Object header = headerList.get(i);
            if (header instanceof SecurityHeaderElement) {
                // header already wrapped by a SecurityheaderElement
                SecurityHeaderElement she = (SecurityHeaderElement) header;
                if (uri.equals(she.getId())) {
                    reqdHeader = she;
                    break;
                }
            }
        }

        // check inside the Securityheader
        if (reqdHeader == null) {
            SecurityHeader secHeader = context.getSecurityHeader();
            SecurityHeaderElement she = secHeader.getChildElement(uri);
            if (she != null && !(MessageConstants.WSSE_SECURITY_TOKEN_REFERENCE_LNAME.equals(she.getLocalPart()) &&
                    MessageConstants.WSSE_NS.equals(she.getNamespaceURI()))) {
                reqdHeader = she;
            }
        }

        // if matches, convert the element to JAXBData
        if (reqdHeader != null) {
            // how to get contentOnly???
            return new JAXBDataImpl(reqdHeader, context.getNamespaceContext(), false);
        } else {
            try {
                Object body = secMessage.getBody();
                if (body instanceof SecurityElement) {
                    SecurityElement se = (SecurityElement) body;
                    if (uri.equals(se.getId())) {
                        return new JAXBDataImpl(se, context.getNamespaceContext(), false);
                    }
                } else if (body instanceof SOAPBody) {
                    SOAPBody soapBody = (SOAPBody) body;
                    if (uri.equals(soapBody.getId())) {
                        return new SSBData(soapBody, false, context.getNamespaceContext());
                    //write to streamwriter data and return
                    } else if (uri.equals(soapBody.getBodyContentId())) {
                        return new SSBData(soapBody, true, context.getNamespaceContext());
                    }
                }
            } catch (XWSSecurityException ex) {
                logger.log(Level.SEVERE, LogStringsMessages.WSS_1704_ERROR_RESOLVING_ID(uri), ex);
                throw new XWSSecurityException(ex);
            }
        }
        Data data = null;
        data = (Data) context.getSTRTransformCache().get(uri);
        if (data != null) {
            return data;
        }
        data = (Data) context.getElementCache().get(uri);
        return data;
    }
}



