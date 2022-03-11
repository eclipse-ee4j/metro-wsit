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
import com.sun.xml.stream.buffer.XMLStreamBufferException;
import com.sun.xml.stream.buffer.XMLStreamBufferMark;
import com.sun.xml.stream.buffer.stax.StreamReaderBufferCreator;
import com.sun.xml.ws.security.opt.api.NamespaceContextInfo;
import com.sun.xml.ws.security.opt.api.PolicyBuilder;
import com.sun.xml.ws.security.opt.api.SecurityElementWriter;
import com.sun.xml.ws.security.opt.api.SecurityHeaderElement;
import com.sun.xml.ws.security.opt.api.TokenValidator;
//import com.sun.xml.ws.security.opt.api.tokens.UsernameToken;
import com.sun.xml.ws.security.opt.impl.incoming.processor.UsernameTokenProcessor;
import com.sun.xml.ws.security.opt.impl.util.SOAPUtil;
import com.sun.xml.ws.security.opt.impl.util.XMLStreamReaderFactory;
import com.sun.xml.wss.NonceManager;
import com.sun.xml.wss.ProcessingContext;
import com.sun.xml.wss.XWSSecurityException;
import com.sun.xml.wss.impl.FilterProcessingContext;
import com.sun.xml.wss.impl.MessageConstants;
import com.sun.xml.wss.impl.misc.DefaultSecurityEnvironmentImpl;
import com.sun.xml.wss.impl.policy.mls.AuthenticationTokenPolicy;
import com.sun.xml.wss.impl.policy.mls.WSSPolicy;
import java.io.OutputStream;
import java.util.HashMap;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.util.logging.Level;
import com.sun.xml.wss.logging.LogDomainConstants;
import com.sun.xml.wss.logging.impl.filter.LogStringsMessages;
import java.util.logging.Logger;

/**
 *
 * @author Ashutosh.Shahi@sun.com
 */
public class UsernameTokenHeader implements com.sun.xml.ws.security.opt.api.tokens.UsernameToken, SecurityHeaderElement,
        TokenValidator, PolicyBuilder, NamespaceContextInfo, SecurityElementWriter{

    private static Logger log = Logger.getLogger(
            LogDomainConstants.IMPL_FILTER_DOMAIN,
            LogDomainConstants.IMPL_FILTER_DOMAIN_BUNDLE);

    private String localPart = null;
    private String namespaceURI = null;
    private String id = "";

    private XMLStreamBuffer mark = null;
    private UsernameTokenProcessor filter = new UsernameTokenProcessor();

    private AuthenticationTokenPolicy.UsernameTokenBinding utPolicy = null;

    private HashMap<String,String> nsDecls;
    //private UsernameToken unToken;

    /** Creates a new instance of UsernameTokenHeader */
    @SuppressWarnings("unchecked")
    public UsernameTokenHeader(XMLStreamReader reader, StreamReaderBufferCreator creator,
            HashMap nsDecls, XMLInputFactory  staxIF) throws XMLStreamException, XMLStreamBufferException  {
        localPart = reader.getLocalName();
        namespaceURI = reader.getNamespaceURI();
        id = reader.getAttributeValue(MessageConstants.WSU_NS,"Id");

        mark = new XMLStreamBufferMark(nsDecls,creator);
        XMLStreamReader utReader = XMLStreamReaderFactory.createFilteredXMLStreamReader(reader,filter) ;
        creator.createElementFragment(utReader,true);
        this.nsDecls = nsDecls;

        utPolicy = new AuthenticationTokenPolicy.UsernameTokenBinding();
        utPolicy.setUUID(id);

        utPolicy.setUsername(filter.getUsername());
        utPolicy.setPassword(filter.getPassword());
        if (MessageConstants.PASSWORD_DIGEST_NS.equals(filter.getPasswordType())){
            utPolicy.setDigestOn(true);
        }
        if(filter.getNonce() != null){
            utPolicy.setUseNonce(true);
        }
        if(filter.getCreated() != null){
            utPolicy.setUseCreated(true);
        }
    }

    @Override
    public void validate(ProcessingContext context) throws XWSSecurityException {
        boolean authenticated = false;
        if (filter.getPassword() == null && filter.getPasswordDigest() == null) {
            utPolicy.setNoPassword(true);
        }
        if (filter.getSalt() != null) {
            utPolicy.setNoPassword(false);
        }

        if (filter.getPassword() == null && filter.getCreated() == null &&
            MessageConstants.PASSWORD_DIGEST_NS.equals(filter.getPasswordType())) {
                 throw SOAPUtil.newSOAPFaultException(
                        MessageConstants.WSSE_INVALID_SECURITY,
                        "Cannot validate Password Digest since Creation Time was not Specified",
                        null, true);
        }

        if(filter.getNonce() != null || filter.getCreated() != null){ //SP1.3
            validateNonceOrCreated(context);
        }

        if (MessageConstants.PASSWORD_DIGEST_NS.equals(filter.getPasswordType())) {
            authenticated = context.getSecurityEnvironment().authenticateUser(
                    context.getExtraneousProperties(), filter.getUsername(), filter.getPasswordDigest(),
                    filter.getNonce(), filter.getCreated());
            if(!authenticated){
                log.log(Level.SEVERE, LogStringsMessages.WSS_1408_FAILED_SENDER_AUTHENTICATION());
                throw SOAPUtil.newSOAPFaultException(
                        MessageConstants.WSSE_FAILED_AUTHENTICATION,
                        "Authentication of Username Password Token Failed",
                        null, true);
            }
        } else if (filter.getPassword() != null) {
            authenticated = context.getSecurityEnvironment().authenticateUser(context.getExtraneousProperties(),
                    filter.getUsername(), filter.getPassword());
            if(!authenticated){
                log.log(Level.SEVERE, LogStringsMessages.WSS_1408_FAILED_SENDER_AUTHENTICATION());
                throw SOAPUtil.newSOAPFaultException(
                        MessageConstants.WSSE_FAILED_AUTHENTICATION,
                        "Authentication of Username Password Token Failed",
                        null, true);

            }
        }


        if (MessageConstants.debug) {
            log.log(Level.FINEST, "Password Validated.....");
        }

        context.getSecurityEnvironment().updateOtherPartySubject(
                DefaultSecurityEnvironmentImpl.getSubject((FilterProcessingContext)context),filter.getUsername(), filter.getPassword());
    }

    @Override
    public WSSPolicy getPolicy() {
        return utPolicy;
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
        return localPart;
    }


    @Override
    public XMLStreamReader readHeader() throws XMLStreamException {
        return mark.readAsXMLStreamReader();
    }

    @Override
    public void writeTo(OutputStream os) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void writeTo(XMLStreamWriter streamWriter) throws XMLStreamException {
        mark.writeToXMLStreamWriter(streamWriter);
    }

    @Override
    public String getUsernameValue() {
        return filter.getUsername();
    }

    @Override
    public void setUsernameValue(String username) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getPasswordValue() {
        return filter.getPassword();
    }

    @Override
    public void setPasswordValue(String passwd) {
        throw new UnsupportedOperationException();
    }
    public void setSalt(String receivedSalt){
        throw new UnsupportedOperationException();
    }
    public String getSalt(){
        return filter.getSalt();
    }
    public void setIterations(int iterate){
        throw new UnsupportedOperationException();
    }
    public String getIterations(){
        return filter.getIterations();
    }
    @Override
    public HashMap<String, String> getInscopeNSContext() {
        return nsDecls;
    }
    @Override
    public void writeTo(javax.xml.stream.XMLStreamWriter streamWriter, HashMap props) {
        throw new UnsupportedOperationException();
    }

    private void validateNonceOrCreated(ProcessingContext context) throws XWSSecurityException {
        if (filter.getCreated() != null) {
            context.getSecurityEnvironment().validateCreationTime(
                    context.getExtraneousProperties(), filter.getCreated(),
                    MessageConstants.MAX_CLOCK_SKEW, MessageConstants.TIMESTAMP_FRESHNESS_LIMIT);
        }
        if (filter.getNonce() != null) {
            try {
                if (!context.getSecurityEnvironment().validateAndCacheNonce(
                        context.getExtraneousProperties(), filter.getNonce(), filter.getCreated(), MessageConstants.MAX_NONCE_AGE)) {
                    XWSSecurityException xwse =
                            new XWSSecurityException(
                            "Invalid/Repeated Nonce value for Username Token");
                    throw DefaultSecurityEnvironmentImpl.newSOAPFaultException(
                            MessageConstants.WSSE_FAILED_AUTHENTICATION,
                            "Invalid/Repeated Nonce value for Username Token",
                            xwse);
                }
            } catch (NonceManager.NonceException ex) {
                throw SOAPUtil.newSOAPFaultException(
                        MessageConstants.WSSE_FAILED_AUTHENTICATION,
                        "Invalid/Repeated Nonce value for Username Token",
                        ex, true);
            }
        }
    }
}

