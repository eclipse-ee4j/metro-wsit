/*
 * Copyright (c) 2011, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.impl;

import com.sun.xml.wss.WSITXMLFactory;
import com.sun.xml.wss.XWSSecurityException;
import com.sun.xml.wss.impl.dsig.NamespaceContextImpl;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.NamespaceContext;
import jakarta.xml.soap.Name;
import jakarta.xml.soap.SOAPPart;
import jakarta.xml.soap.SOAPBody;
import jakarta.xml.soap.SOAPHeader;
import jakarta.xml.soap.MimeHeader;
import jakarta.xml.soap.SOAPMessage;
import jakarta.xml.soap.MimeHeaders;
import jakarta.xml.soap.SOAPElement;
import jakarta.xml.soap.SOAPFactory;
import jakarta.xml.soap.SOAPEnvelope;
import jakarta.xml.soap.SOAPConstants;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.AttachmentPart;
import javax.xml.namespace.QName;
import jakarta.activation.DataHandler;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.apache.xml.security.Init;

import com.sun.xml.ws.security.opt.impl.util.SOAPUtil;
import com.sun.xml.wss.impl.policy.mls.Target;
import com.sun.xml.wss.swa.MimeConstants;
import com.sun.xml.wss.core.SecurityHeader;
import com.sun.xml.wss.logging.LogDomainConstants;

import org.w3c.dom.Node;

import com.sun.xml.wss.logging.LogStringsMessages;
import com.sun.xml.wss.util.NodeListImpl;

public final class SecurableSoapMessage extends SOAPMessage {

    private NamespaceContext nsContext;
    Random rnd = new Random();
    static XPathFactory xpathFactory =null;

    private SOAPMessage soapMessage;
    private boolean optimized = false;
    private SOAPElement wsseSecurity;
    private boolean doNotSetMU= false;
    private static final Logger log = Logger.getLogger(
            LogDomainConstants.WSS_API_DOMAIN,
            LogDomainConstants.WSS_API_DOMAIN_BUNDLE);

    static {
        Init.init();

        //Workaround for: Apache XML Security 1.5.6 do not support Canonicalizer.ALGO_ID_C14N_PHYSICAL in file 'java/org/apache/xml/security/resource/config.xml'
        try {
          org.apache.xml.security.c14n.Canonicalizer.register(org.apache.xml.security.c14n.Canonicalizer.ALGO_ID_C14N_PHYSICAL, org.apache.xml.security.c14n.implementations.CanonicalizerPhysical.class.getName());
        } catch (org.apache.xml.security.exceptions.AlgorithmAlreadyRegisteredException e){
          //log.log(Level.SEVERE, org.apache.xml.security.c14n.Canonicalizer.ALGO_ID_C14N_PHYSICAL+" registered failed!", e);
          //Do nothing, log info can cause some SQE test cases faile.
        } catch (ClassNotFoundException e) {
          log.log(Level.SEVERE, org.apache.xml.security.c14n.Canonicalizer.ALGO_ID_C14N_PHYSICAL+" registered failed!", e);
        }
        xpathFactory = WSITXMLFactory.createXPathFactory(WSITXMLFactory.DISABLE_SECURE_PROCESSING);
    }

    public SecurableSoapMessage() {}

    /**
     */
    public SecurableSoapMessage(SOAPMessage soapMessage)throws XWSSecurityException {
        init(soapMessage);
    }

    public void init(SOAPMessage soapMessage) {
        this.soapMessage = soapMessage;
        if(log.isLoggable(Level.FINEST)){
        log.log(Level.FINEST, LogStringsMessages.WSS_0100_CREATE_FOR_CREATING_IMPL(this.getClass().getName()) );
        }
    }

    public SOAPEnvelope getEnvelope() throws XWSSecurityException {
        SOAPEnvelope envelope = null;

        try {
            envelope = getSOAPPart().getEnvelope();
        } catch (Exception e) {
            log.log(Level.SEVERE, LogStringsMessages.WSS_0399_SOAP_ENVELOPE_EXCEPTION(), e);
            throw new XWSSecurityException(e);
        }

        return envelope;
    }

    /**
     * Finds SOAPHeader.
     *
     * @param doCreate create one if none exists
     *
     * @return the soap Header or null if none and doCreate is false
     */
    private SOAPHeader findSoapHeader(boolean doCreate) throws XWSSecurityException {

        try {
            SOAPHeader header = getSOAPPart().getEnvelope().getHeader();
            if (header != null)
                return header;

            if (doCreate)
                return getSOAPPart().getEnvelope().addHeader();

        } catch (SOAPException e) {
            log.log(Level.SEVERE, LogStringsMessages.WSS_0369_SOAP_EXCEPTION(e.getMessage()));
            throw new XWSSecurityException(e);
        }

        return null;
    }

    /**
     * Finds wsse:Security Header Block.
     *
     * @param doCreate create one if none exists
     *
     * @return wsse:Security header
     *
     */
    public SecurityHeader findWsseSecurityHeaderBlock(
            boolean doCreate,
            boolean mustUnderstand)
            throws XWSSecurityException {

        if (wsseSecurity != null) {
            // if security header has been detached from the soap header
            if (wsseSecurity.getParentNode() == null)
                wsseSecurity = null;
            else
                return (SecurityHeader) wsseSecurity;
        }

        SOAPHeader header = findSoapHeader(doCreate);
        if (null == header) return null;

        // Putting work-around for Bug Id: 5060366
        org.w3c.dom.NodeList headerChildNodes = header.getChildNodes();
        if (headerChildNodes != null) {
            org.w3c.dom.Node currentNode;
            for (int i = 0; i < headerChildNodes.getLength(); i ++) {
                currentNode = headerChildNodes.item(i);
                if (MessageConstants.WSSE_SECURITY_LNAME.equals(
                        currentNode.getLocalName()) &&
                        MessageConstants.WSSE_NS.equals(
                        currentNode.getNamespaceURI())) {
                    wsseSecurity = (SOAPElement) currentNode;
                    break;
                }
            }
        }

        if (wsseSecurity == null && !doCreate) return null;

        if (wsseSecurity == null && doCreate) {
            // Create header block
            wsseSecurity =
                    (SOAPElement) getSOAPPart().createElementNS(
                    MessageConstants.WSSE_NS,
                    MessageConstants.WSSE_SECURITY_QNAME);
            wsseSecurity.setAttributeNS(
                    MessageConstants.NAMESPACES_NS,
                    "xmlns:" + MessageConstants.WSSE_PREFIX,
                    MessageConstants.WSSE_NS);
            if (mustUnderstand && !this.doNotSetMU) {
                wsseSecurity.setAttributeNS(
                        getEnvelope().getNamespaceURI(),
                        getEnvelope().getPrefix() + ":mustUnderstand",
                        "1");
            }
            XMLUtil.prependChildElement(header, wsseSecurity, getSOAPPart());
        }

        if(wsseSecurity != null){
            wsseSecurity = new SecurityHeader(wsseSecurity);
        } else{
            throw new XWSSecurityException("Internal Error: wsse:Security Header found null");
        }

        return (SecurityHeader) wsseSecurity;
    }

    /**
     * Finds wsse:Security Header
     *
     * @return returns null if wsse:Security header not found
     *
     */
    public SecurityHeader findSecurityHeader() throws XWSSecurityException {
        return findWsseSecurityHeaderBlock(false, false);
    }

    /**
     * Finds or creates wsse:Security Header
     *
     * @return wsse:Security header
     *
     */
    public SecurityHeader findOrCreateSecurityHeader()
    throws XWSSecurityException {
        return findWsseSecurityHeaderBlock(true, true);
    }

    /**
     * Delete security header
     */
    public void deleteSecurityHeader() {
        try {
            findSecurityHeader();
            if (null != wsseSecurity) {
                wsseSecurity.detachNode();
                wsseSecurity = null;
            }
        } catch (XWSSecurityException e) {
            log.log(Level.SEVERE, LogStringsMessages.WSS_0370_ERROR_DELETING_SECHEADER(), e.getMessage());
        }
    }

    /**
     * Make Security Header Non-MustUnderstand
     */
    public void resetMustUnderstandOnSecHeader() {
        try {
            findSecurityHeader();
            if (null != wsseSecurity) {
                wsseSecurity.removeAttributeNS(this.getEnvelope().getNamespaceURI(), "mustUnderstand");
            }
        } catch (XWSSecurityException e) {
            log.log(Level.SEVERE, LogStringsMessages.WSS_0370_ERROR_DELETING_SECHEADER(), e.getMessage());
        }
    }

    /**
     * Create and initialize a SecurityHeaderException, and throw a fault based
     * on it.
     *
     * The faultstring for this exception is wsse:InvalidSecurity as per
     * section 12 on Error Handling of the wss SOAPMessageSecurity spec (draft
     * 17).
     *
     * This fault stands for An error was discovered processing the
     * wsse:Security header.
     */

    public void generateSecurityHeaderException(String exceptionMessage)
    throws XWSSecurityException {
        SecurityHeaderException she =
                new SecurityHeaderException(exceptionMessage);
        // an error was discovered processing the header
        generateFault(
                newSOAPFaultException(MessageConstants.WSSE_INVALID_SECURITY,
                "Error while processing Security Header",
                she));
        log.log(Level.SEVERE, LogStringsMessages.WSS_0370_ERROR_PROCESSING_SECHEADER(), she);
        throw she;
    }

    /**
     * Create and initialize a WssSoapFaultException. This method is used in
     * conjunction with generateClientFault.
     */
    public static WssSoapFaultException newSOAPFaultException(
            String faultstring,
            Throwable th) {
        String fault = SOAPUtil.isEnableFaultDetail() ?
            faultstring : SOAPUtil.getLocalizedGenericError();
        WssSoapFaultException sfe =
                new WssSoapFaultException(null, fault, null, null);
        if (SOAPUtil.isEnableFaultDetail()) {
            sfe.initCause(th);
        }
        return sfe;
    }

    /**
     * Create and initialize a WssSoapFaultException. This method is used in
     * conjunction with generateClientFault.
     */
    public static WssSoapFaultException newSOAPFaultException(
            QName faultCode,
            String faultstring,
            Throwable th) {
        String fault = SOAPUtil.isEnableFaultDetail() ?
            faultstring : SOAPUtil.getLocalizedGenericError();
        QName fc = SOAPUtil.isEnableFaultDetail() ? faultCode : MessageConstants.WSSE_INVALID_SECURITY;
        WssSoapFaultException sfe =
                new WssSoapFaultException(fc, fault, null, null);
        if (SOAPUtil.isEnableFaultDetail()) {
            sfe.initCause(th);
        }
        return sfe;
    }

    /**
     */
    public void generateFault(WssSoapFaultException sfe)
    throws XWSSecurityException {

        try {
            SOAPBody body = soapMessage.getSOAPBody();
            body.removeContents();
            QName faultCode = sfe.getFaultCode();
            Name faultCodeName;
            if (faultCode == null) {
                faultCodeName = SOAPFactory.newInstance().createName(
                        "Client",
                        null,
                        SOAPConstants.URI_NS_SOAP_ENVELOPE);
            } else {

                faultCodeName = SOAPFactory.newInstance().createName(
                        faultCode.getLocalPart(),
                        faultCode.getPrefix(),
                        faultCode.getNamespaceURI());
            }
            final String msg = (SOAPUtil.isEnableFaultDetail())
                    ? sfe.getFaultString() : SOAPUtil.getLocalizedGenericError();
            body.addFault(faultCodeName, msg);
            // TODO RFE add "actor" and throwable info to "detail"
        } catch (SOAPException e) {
            log.log(Level.SEVERE, LogStringsMessages.WSS_0371_ERROR_GENERATE_FAULT(e.getMessage()));
            throw new XWSSecurityException(e);
        }

    }

    @Override
    public SOAPPart getSOAPPart() {
        return soapMessage.getSOAPPart();
    }

    @Override
    public SOAPBody getSOAPBody() throws SOAPException {
        try {
            return soapMessage.getSOAPBody();
        } catch (Exception e) {
            log.log(Level.SEVERE, LogStringsMessages.WSS_0398_SOAP_BODY_EXCEPTION(), e);
            throw new SOAPException(e);
        }
    }

    public SOAPMessage getSOAPMessage() {
        return soapMessage;
    }

    public void setSOAPMessage(SOAPMessage soapMsg) throws XWSSecurityException {
        init(soapMsg);
    }

    @Override
    public void addAttachmentPart(AttachmentPart AttachmentPart) {
        soapMessage.addAttachmentPart(AttachmentPart);
    }

    @Override
    public int countAttachments() {
        return soapMessage.countAttachments();
    }

    @Override
    public AttachmentPart createAttachmentPart() {
        return soapMessage.createAttachmentPart();
    }

    @Override
    public AttachmentPart createAttachmentPart(Object content, String contentType) {
        return soapMessage.createAttachmentPart(content, contentType);
    }

    @Override
    public AttachmentPart createAttachmentPart(DataHandler dataHandler) {
        return soapMessage.createAttachmentPart(dataHandler);
    }

    @Override
    public boolean equals(Object obj) {
        return soapMessage.equals(obj);
    }

    @Override
    public Iterator<AttachmentPart> getAttachments() {
        return soapMessage.getAttachments();
    }

    @Override
    public Iterator<AttachmentPart> getAttachments(MimeHeaders headers) {
        return soapMessage.getAttachments(headers);
    }

    @Override
    public String getContentDescription() {
        return soapMessage.getContentDescription();
    }

    @Override
    public MimeHeaders getMimeHeaders() {
        return soapMessage.getMimeHeaders();
    }

    @Override
    public Object getProperty(String property) throws SOAPException {
        return soapMessage.getProperty(property);
    }

    @Override
    public SOAPHeader getSOAPHeader() throws SOAPException {
        return soapMessage.getSOAPHeader();
    }

    @Override
    public int hashCode() {
        return soapMessage.hashCode();
    }

    @Override
    public void removeAllAttachments() {
        soapMessage.removeAllAttachments();
    }

    @Override
    public boolean saveRequired() {
        return soapMessage.saveRequired();
    }

    @Override
    public void setContentDescription(String description) {
        soapMessage.setContentDescription(description);
    }

    @Override
    public void setProperty(String property, Object value)
    throws SOAPException {
        soapMessage.setProperty(property, value);
    }

    @Override
    public String toString() {
        return soapMessage.toString();
    }

    @Override
    public void writeTo(OutputStream out) throws SOAPException, IOException {
        soapMessage.writeTo(out);
    }

    @Override
    public void saveChanges() throws SOAPException {
        soapMessage.saveChanges();
    }

    public NamespaceContext getNamespaceContext() throws XWSSecurityException{
        if(nsContext == null){
            nsContext = new NamespaceContextImpl();

            ((NamespaceContextImpl)nsContext).add(
                    getEnvelope().getPrefix(), getEnvelope().getNamespaceURI());
            if (getEnvelope().getNamespaceURI() == MessageConstants.SOAP_1_2_NS) {
                ((NamespaceContextImpl)nsContext).add("SOAP-ENV", MessageConstants.SOAP_1_2_NS);
                ((NamespaceContextImpl)nsContext).add("env", MessageConstants.SOAP_1_2_NS);
            }
        }
        return nsContext;
    }

    /**
     * @return an ID unique w.r.t this SOAPMessage
     */
    public String generateId() {


        int intRandom = rnd.nextInt();
        return "XWSSGID-"+ System.currentTimeMillis() + intRandom;
    }

    /**
     */
    public void generateWsuId(Element element) throws XWSSecurityException {
        // assign the wsu:Id to the element
        element.setAttributeNS(MessageConstants.WSU_NS, "wsu:Id", generateId());
    }

    /**
     * @param id ID specified should be unique in the message.
     */
    public void generateWsuId(Element element, String id) {
        // assign the wsu:Id to the element
        element.setAttributeNS(MessageConstants.WSU_NS, "wsu:Id", id);
    }

    public SOAPElement getElementByWsuId(String id)
    throws XWSSecurityException {

        Element  element = getSOAPPart().getElementById(id);
        if (element != null) {
            if (MessageConstants.debug) {
                log.log(Level.FINE, "Document.getElementById() returned {0}", element);
            }

            return (SOAPElement)element;
        }

        if (MessageConstants.debug) {
            log.log(Level.FINE, "Document.getElementById() FAILED......''{0}''", id);
        }

        SOAPElement result = null;
        String xpath = "//*[@wsu:Id='" + id + "']";
        try {

            XPath xPATH = xpathFactory.newXPath();
            xPATH.setNamespaceContext(getNamespaceContext());
            XPathExpression xpathExpr = xPATH.compile(xpath);
            NodeList elements = (NodeList)xpathExpr.evaluate(this.getSOAPPart(),XPathConstants.NODESET);


            if (elements != null)
                result = (SOAPElement) elements.item(0);
        } catch (Exception e) {
            log.log(Level.SEVERE,
                    LogStringsMessages.WSS_0374_ERROR_APACHE_XPATH_API(id, e.getMessage()),
                    new Object[] {id, e.getMessage()});
                    throw new XWSSecurityException(e);
        }

        return result;
    }

    /*
     * Locate an element references using either
     * 1) Local ID attributes on XML Signature Elements
     * 2) Local ID attributes on XML Encryption Elements
     * 3) Global wsu:Id attributes
     */
    public Element getElementById(String id) throws XWSSecurityException {

        if(id.startsWith("#"))
            id = id.substring(1);
        Element  element = getSOAPPart().getElementById(id);
        if (element != null) {
            if (MessageConstants.debug) {
                log.log(Level.FINE, "Document.getElementById() returned {0}", element);
            }
            return element;
        }

        if (MessageConstants.debug) {
            log.log(Level.FINE, "Document.getElementById() FAILED......''{0}''", id);
        }

        Element result = null;
        result = getElementByWsuId(id);

        if (result == null) {

            Document soapPart = getSOAPPart();
            NodeList assertions =
                    soapPart.getElementsByTagNameNS(MessageConstants.SAML_v1_0_NS,
                                            MessageConstants.SAML_ASSERTION_LNAME);
            if(assertions.getLength() <= 0 || assertions.item(0) == null){
                assertions =
                        soapPart.getElementsByTagNameNS(MessageConstants.SAML_v2_0_NS,
                                            MessageConstants.SAML_ASSERTION_LNAME);
            }

            String assertionId;
            int len = assertions.getLength();
            if (len > 0) {
                for (int i=0; i < len; i++) {
                    SOAPElement elem = (SOAPElement)assertions.item(i);
                    if(elem.getAttributeNode("ID")!= null){
                        assertionId = elem.getAttribute(MessageConstants.SAML_ID_LNAME);
                    }else{
                        assertionId = elem.getAttribute(MessageConstants.SAML_ASSERTIONID_LNAME);
                    }
                    if (id.equals(assertionId)) {
                        result = elem;
                        break;
                    }
                }
            }
        }

        if (result == null) {
            NodeList elems;
            String xpath =  "//*[@Id='" + id + "']";
            try {
                XPath xPATH = xpathFactory.newXPath();
                xPATH.setNamespaceContext(getNamespaceContext());
                XPathExpression xpathExpr = xPATH.compile(xpath);
                elems = (NodeList)xpathExpr.evaluate(this.getSOAPPart(),XPathConstants.NODESET);


            } catch (Exception e) {
                log.log(Level.SEVERE,
                         LogStringsMessages.WSS_0375_ERROR_APACHE_XPATH_API(id, e.getMessage()),
                        new Object[] {id, e.getMessage()});
                        throw new XWSSecurityException(e);
            }

            if (elems == null || elems.getLength() == 0) {
                log.log(Level.SEVERE, LogStringsMessages.WSS_0285_ERROR_NO_ELEMENT());
                throw new XWSSecurityException(
                        "No elements exist with Id/WsuId: " + id);
            }

            for (int i=0; i < elems.getLength(); i++) {
                Element elem = (Element)elems.item(i);
                String namespace = elem.getNamespaceURI();
                if (namespace.equals(MessageConstants.DSIG_NS) ||
                        namespace.equals(MessageConstants.XENC_NS)) {
                    result = elem;
                    break;
                }
            }

            if (elems.getLength() > 1) {
                log.log(Level.SEVERE, LogStringsMessages.WSS_0286_INVALID_NOOF_ELEMENTS());
                throw new XWSSecurityException(
                        "More than one element exists with Id/WsuId: " + id);
            }
        }

        return result;
    }



    public AttachmentPart getAttachmentPart(String uri) throws XWSSecurityException {
        AttachmentPart _part = null;
        String uri_tmp = uri;

        try {
            if (uri.startsWith("cid:")) {
                // rfc2392
                uri = "<"+uri.substring("cid:".length())+">";

                MimeHeaders headersToMatch = new MimeHeaders();
                headersToMatch.addHeader(MimeConstants.CONTENT_ID, uri);

                Iterator i = this.getAttachments(headersToMatch);
                _part = (i == null) ? null : (AttachmentPart)i.next();
                if (_part == null){
                    uri = uri_tmp;
                    uri = uri.substring("cid:".length());
                    headersToMatch = new MimeHeaders();
                    headersToMatch.addHeader(MimeConstants.CONTENT_ID, uri);

                    i = this.getAttachments(headersToMatch);
                    _part = (i == null) ? null : (AttachmentPart)i.next();
                }
                if (_part == null){
                    throw new XWSSecurityException("Unable to Locate AttachmentPart for uri "+uri);
                }
            } else
                if (uri.startsWith(MessageConstants.ATTACHMENTREF)) {
                // auto-generated JAXRPC CID
                Iterator j = this.getAttachments();

                while (j.hasNext()) {
                    AttachmentPart p = (AttachmentPart)j.next();
                    String cl = p.getContentId();
                    if (cl != null) {
                        // obtain the partname
                        int eqIndex = cl.indexOf("=");
                        if (eqIndex > -1) {
                            cl = cl.substring(1, eqIndex);
                            if (cl.equalsIgnoreCase(uri.substring
                                    (MessageConstants.ATTACHMENTREF.length()))) {
                                _part = p;
                                break;
                            }
                        }
                    }
                }
                } else {
                String clocation = convertAbsolute2Relative(uri);

                MimeHeaders headersToMatch = new MimeHeaders();
                headersToMatch.addHeader(MimeConstants.CONTENT_LOCATION, clocation);

                Iterator i = this.getAttachments(headersToMatch);
                _part = (i == null) ? null : (AttachmentPart)i.next();

                if (_part == null /*&& !uriNew.toString().startsWith("thismessage:/")*/) {
                    // log
                    clocation = uri;
                    headersToMatch.removeAllHeaders();
                    headersToMatch.addHeader(MimeConstants.CONTENT_LOCATION, clocation);

                    i = this.getAttachments(headersToMatch);
                    _part = (i == null) ? null : (AttachmentPart)i.next();
                }
                }

        } catch (Exception se) {
            log.log(Level.SEVERE, LogStringsMessages.WSS_0287_ERROR_EXTRACTING_ATTACHMENTPART(), se);
            throw new XWSSecurityException(se);
        }

        return _part;
    }

    private String convertAbsolute2Relative(String clocation) {
        MimeHeaders mimeHeaders = this.getMimeHeaders();

        String enclsgClocation = null;

        if (mimeHeaders != null) {
            Iterator clocs = mimeHeaders.getMatchingHeaders(
                    new String[] {MimeConstants.CONTENT_LOCATION});
                    if (clocs != null) {
                        MimeHeader mh = (MimeHeader)clocs.next();
                        if (mh != null) enclsgClocation = mh.getValue();
                    }
        }

        /* absolute URI can be of the form - http://xxx, thismessage:/xxx, baseUri+xxx */
        if (enclsgClocation != null && clocation.startsWith(enclsgClocation))
            clocation = clocation.substring(enclsgClocation.length());
        else
            if (clocation.startsWith("thismessage:/"))
                clocation = clocation.substring("thismessage:/".length());

        return clocation;
    }

    public static String getIdFromFragmentRef(String ref) {
        char start = ref.charAt(0);
        if (start == '#') {
            return ref.substring(1);
        }
        return ref;
    }


    public Object getMessageParts(Target target) throws XWSSecurityException {
        Object retValue = null;
        String type = target.getType();
        String value = target.getValue();
        boolean throwFault = false;
        boolean headersOnly = target.isSOAPHeadersOnly();

        switch (type) {
            case Target.TARGET_TYPE_VALUE_QNAME:

                try {
                    if (value == Target.BODY) {

                        final SOAPElement se;
//                    if(!isOptimized()){
                        se = this.getSOAPBody();
//                    }
//                    else{
//                        se = ((com.sun.xml.messaging.saaj.soap.ExpressMessage)soapMessage).getEMBody();
//                    }
                        retValue = new NodeList() {
                            Node node = se;

                            @Override
                            public int getLength() {
                                return 1;
                            }

                            @Override
                            public Node item(int num) {
                                if (num == 0) {
                                    return node;
                                } else {
                                    return null;
                                }
                            }
                        };
                    } else {
                        QName name = QName.valueOf(value);
                        if (!headersOnly) {
                            if ("".equals(name.getNamespaceURI())) {
                                retValue = this.getSOAPPart().getElementsByTagNameNS("*", name.getLocalPart());
                            } else {
                                retValue = this.getSOAPPart().getElementsByTagNameNS(name.getNamespaceURI(), name.getLocalPart());
                            }
                        } else {
                            // process headers of a SOAPMessage
                            retValue = new NodeListImpl();
                            NodeList hdrChilds = this.getSOAPHeader().getChildNodes();
                            for (int i = 0; i < hdrChilds.getLength(); i++) {
                                Node child = hdrChilds.item(i);
                                if (child.getNodeType() == Node.ELEMENT_NODE) {
                                    if ("".equals(name.getNamespaceURI())) {
                                        if (name.getLocalPart().equals(child.getLocalName()))
                                            ((NodeListImpl) retValue).add(child);
                                    } else {
                                        // FIXME: Hack to get addressing members from both namespaces, as microsoft uses both of them in a soap message
                                        if (name.getNamespaceURI().equals(MessageConstants.ADDRESSING_MEMBER_SUBMISSION_NAMESPACE) ||
                                                name.getNamespaceURI().equals(MessageConstants.ADDRESSING_W3C_NAMESPACE)) {
                                            if ((child.getNamespaceURI().equals(MessageConstants.ADDRESSING_MEMBER_SUBMISSION_NAMESPACE) ||
                                                    child.getNamespaceURI().equals(MessageConstants.ADDRESSING_W3C_NAMESPACE))) {
                                                if (!"".equals(name.getLocalPart())) {
                                                    if (name.getLocalPart().equals(child.getLocalName()))
                                                        ((NodeListImpl) retValue).add(child);
                                                } else {
                                                    ((NodeListImpl) retValue).add(child);
                                                }
                                            }
                                        } else {
                                            if (!"".equals(name.getLocalPart())) {
                                                if (name.getNamespaceURI().equals(child.getNamespaceURI()) &&
                                                        name.getLocalPart().equals(child.getLocalName()))
                                                    ((NodeListImpl) retValue).add(child);
                                            } else {
                                                if (name.getNamespaceURI().equals(child.getNamespaceURI()))
                                                    ((NodeListImpl) retValue).add(child);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    log.log(Level.SEVERE, LogStringsMessages.WSS_0288_FAILED_GET_MESSAGE_PARTS_QNAME(), e);
                    throw new XWSSecurityRuntimeException(e);
                }
                if (retValue == null || ((NodeList) retValue).getLength() == 0) throwFault = true;

                break;
            case Target.TARGET_TYPE_VALUE_XPATH:
                try {
                    XPath xpath = xpathFactory.newXPath();

                    xpath.setNamespaceContext(getNamespaceContext());
                    //              XPathExpression expr = xpath.compile("//*[@wsu:Id]");
                    //XPathExpression expr = xpath.compile("//*");
                    XPathExpression xpathExpr = xpath.compile(value);
                    retValue = xpathExpr.evaluate(this.getSOAPPart(), XPathConstants.NODESET);


                /*    retValue =
                    XPathAPI.selectNodeList(
                    this.getSOAPPart(),
                    value,
                    this.getNSContext());*/
                } catch (Exception e) {
                    log.log(Level.SEVERE, LogStringsMessages.WSS_0289_FAILED_GET_MESSAGE_PARTS_X_PATH(), e);
                    throw new XWSSecurityRuntimeException(e);
                }
                if (retValue == null || ((NodeList) retValue).getLength() == 0) throwFault = true;
                break;
            case Target.TARGET_TYPE_VALUE_URI:
                try {
                    retValue = this.getElementById(value);
                } catch (XWSSecurityException xwse) {
                    try {
                        retValue = getAttachmentPart(value);
                        if (retValue == null) throwFault = true;
                    } catch (Exception se) {
                        log.log(Level.SEVERE, LogStringsMessages.WSS_0290_FAILED_GET_MESSAGE_PARTS_URI(), se);
                        throw new XWSSecurityException("No message part can be identified by the Target: " + value);
                    }
                }
                break;
        }

        if (throwFault) {
            if(log.isLoggable(Level.FINE)){
            log.log(Level.FINE, "No message part can be identified by the Target:{0}", value);
            }
            //throw new XWSSecurityException("No message part can be identified by the Target: " + value);
            //Do not throw an exception, acc. to WS-SecurityPolicy, it ok if a target is not found in message
            return null;
        }

        return retValue;
    }

    @Override
    public  AttachmentPart getAttachment(SOAPElement element)
    throws SOAPException {
        log.log(Level.SEVERE, LogStringsMessages.WSS_0291_UNSUPPORTED_OPERATION_GET_ATTACHMENT());
        throw new UnsupportedOperationException("Operation Not Supported");
        //soapMessage.getAttachment(element);
    }

    @Override
    public void removeAttachments(MimeHeaders hdrs) {
        log.log(Level.SEVERE, LogStringsMessages.WSS_0292_UNSUPPORTED_OPERATION_REMOVE_ATTACHMENT());
        throw new UnsupportedOperationException("Operation Not Supported");
        //soapMessage.removeAttachments(hdrs);
    }

    public boolean isOptimized() {
        return optimized;
    }

    public void setOptimized(boolean optimized) {
        this.optimized = optimized;
    }

    public void setDoNotSetMU(boolean doNotSetMU) {
        this.doNotSetMU = doNotSetMU;
    }
}
