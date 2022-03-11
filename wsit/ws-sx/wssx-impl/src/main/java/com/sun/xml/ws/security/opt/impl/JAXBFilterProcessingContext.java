/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.opt.impl;

import com.sun.xml.ws.api.addressing.AddressingVersion;
import com.sun.xml.ws.security.opt.impl.incoming.SecurityContext;
import com.sun.xml.ws.security.opt.impl.message.MessageWrapper;
import com.sun.xml.wss.impl.MessageLayout;
import com.sun.xml.wss.impl.policy.mls.MessagePolicy;
import com.sun.xml.wss.logging.LogDomainConstants;
import java.security.Key;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import jakarta.xml.soap.SOAPMessage;
import javax.xml.namespace.QName;
import com.sun.xml.wss.ProcessingContext;
import com.sun.xml.wss.XWSSecurityException;
import com.sun.xml.wss.impl.SecurableSoapMessage;
import com.sun.xml.wss.impl.FilterProcessingContext;
import com.sun.xml.wss.impl.policy.SecurityPolicy;
import com.sun.xml.ws.security.opt.impl.outgoing.SecurityHeader;
import com.sun.xml.wss.impl.MessageConstants;
import com.sun.xml.ws.security.opt.impl.util.WSSElementFactory;
import com.sun.xml.ws.security.opt.impl.message.SecuredMessage;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.SOAPVersion;
import org.jvnet.staxex.NamespaceContextEx;
import com.sun.xml.stream.buffer.XMLStreamBuffer;
import com.sun.xml.wss.BasicSecurityProfile;

/**
 *
 * @author Ashutosh.Shahi@Sun.Com
 */

public class JAXBFilterProcessingContext extends FilterProcessingContext{
    protected static final Logger logger =  Logger.getLogger( LogDomainConstants.WSS_API_DOMAIN,
            LogDomainConstants.WSS_API_DOMAIN_BUNDLE);
    private int uid = 5001;
    private boolean isBSP = false;
    private Message pvMsg = null;//partiall verified incoming message;
    /**
     * JAX-WS representation of message
     **/
    SecuredMessage securedMessage = null;
    
    SOAPVersion soapVersion = SOAPVersion.SOAP_11;
    
    SecurityHeader secHeader = null;
    
    private SecurityContext securityContext = null;
    
    private Message message = null;
    private boolean isOneWay = false;
    private boolean isSAMLEK = false;
    private AddressingVersion addrVer = null;
    private NamespaceContextEx nsc = null;
    private XMLStreamBuffer xmlBuffer = null;
    private boolean isSSL = false;
    private HashMap<String, Key> currentSecretMap = new HashMap<>();
    private boolean disablePayloadBuffering = false;
    private boolean disbaleIncPrefix = false;
    private boolean encHeaderContent = false;
    private boolean allowMissingTimestamp = false;
    private boolean mustUnderstandValue = true;
    private BasicSecurityProfile bspContext = null;
    private String edId = null;
    /**
     * Creates a new instance of JAXBFilterProcessingContext
     */
    public JAXBFilterProcessingContext() {
        bspContext = new BasicSecurityProfile();
    }
    
    public JAXBFilterProcessingContext(ProcessingContext context) {
        //super(context);
        bspContext = new BasicSecurityProfile();
    }
    
    
    public JAXBFilterProcessingContext(Map invocationProps) {
        properties = invocationProps;
        bspContext = new BasicSecurityProfile();
    }
    
    
    
    public JAXBFilterProcessingContext(String messageIdentifier,
            SecurityPolicy securityPolicy,
            Message message, SOAPVersion soapVersion)
            throws XWSSecurityException {
        setSecurityPolicy(securityPolicy);
        setMessageIdentifier(messageIdentifier);
        securedMessage = new SecuredMessage(message, getSecurityHeader(), soapVersion);
        this.soapVersion = soapVersion;
        securedMessage.setRootElements(getNamespaceContext());
        bspContext = new BasicSecurityProfile();
    }
    
    public SecuredMessage getSecuredMessage(){
        return securedMessage;
    }
    
    public void isOneWayMessage(boolean value){
        this.isOneWay = value;
    }

    public void setEdIdforEh(String id) {
        this.edId = id;
    }
    public String getEdIdforEh(){
        return edId;
    }
    public void setJAXWSMessage(Message jaxWsMessage, SOAPVersion soapVersion){
        QName secQName = new QName(MessageConstants.WSSE_NS, "Security",
                MessageConstants.WSSE_PREFIX);
        secHeader = (SecurityHeader)jaxWsMessage.getHeaders().get(secQName, false);
        securedMessage = new SecuredMessage(jaxWsMessage,getSecurityHeader(), soapVersion);
        this.soapVersion = soapVersion;
        securedMessage.setRootElements(getNamespaceContext());
    }
    
    public void setMessage(Message message){
        this.message = message;
    }
    
    public Message getJAXWSMessage(){
        if(message != null)
            return message;
        else
            return new MessageWrapper(securedMessage,isOneWay);
    }
    
    public SOAPVersion getSOAPVersion(){
        return soapVersion;
    }
    
    public void setSOAPVersion(SOAPVersion sv){
        this.soapVersion = sv;
    }
    
    public boolean isSOAP12(){
        return (soapVersion == SOAPVersion.SOAP_12) ? true : false;
    }
    
    public SecurityHeader getSecurityHeader(){
        if(secHeader == null){
            secHeader = new WSSElementFactory(soapVersion).createSecurityHeader(getLayout() ,soapVersion.nsUri, mustUnderstandValue);
        }
        return secHeader;
    }
    
    @Override
    protected SecurableSoapMessage getSecureMessage() {
        return null;
    }
    
    @Override
    protected void setSecureMessage(SecurableSoapMessage msg) {
        
    }
    
    /**
     * set the SOAP Message into the ProcessingContext.
     * @param message SOAPMessage
     */
    @Override
    public void setSOAPMessage(SOAPMessage message) {
        
    }
    
    /**
     * @return the SOAPMessage from the context
     */
    @Override
    public SOAPMessage getSOAPMessage() {
        /*try{
            return getJAXWSMessage().readAsSOAPMessage();
        } catch(SOAPException se){
            logger.log(Level.SEVERE,"WSS0809.msgconversion.error",se);
            //throw runtime exception.
        }
        return null;*/
        throw new UnsupportedOperationException();
    }
    
    @Override
    public  void copy(ProcessingContext ctxx1, ProcessingContext ctxx2)
            throws XWSSecurityException {
        if(ctxx2 instanceof JAXBFilterProcessingContext){
            JAXBFilterProcessingContext ctx1 = (JAXBFilterProcessingContext)ctxx1;
            JAXBFilterProcessingContext ctx2 = (JAXBFilterProcessingContext)ctxx2;
            super.copy(ctx1, ctx2);
            ctx1.setJAXWSMessage(ctx2.getJAXWSMessage(), ctx2.soapVersion);
        } else{
            super.copy(ctxx1, ctxx2);
        }
    }
    
    public synchronized  String generateID(){
        uid++;
        return "_" + uid;
    }
    
    public SecurityContext getSecurityContext() {
        return securityContext;
    }
    
    public void setSecurityContext(SecurityContext securityContext) {
        this.securityContext = securityContext;
    }
    
    public NamespaceContextEx getNamespaceContext(){
        if (nsc == null) {
            if (soapVersion == SOAPVersion.SOAP_11) {
                nsc = new com.sun.xml.ws.security.opt.impl.util.NamespaceContextEx(false);
            } else {
                nsc = new com.sun.xml.ws.security.opt.impl.util.NamespaceContextEx(true);
            }
            if (getWSSAssertion() != null) {
                if (getWSSAssertion().getType().equals("1.1")) {
                    ((com.sun.xml.ws.security.opt.impl.util.NamespaceContextEx) nsc).addWSS11NS();
                    ((com.sun.xml.ws.security.opt.impl.util.NamespaceContextEx) nsc).addWSSNS();
                } else {
                    ((com.sun.xml.ws.security.opt.impl.util.NamespaceContextEx) nsc).addWSSNS();
                }
            } else {
                ((com.sun.xml.ws.security.opt.impl.util.NamespaceContextEx) nsc).addWSSNS();
            }

            ((com.sun.xml.ws.security.opt.impl.util.NamespaceContextEx) nsc).addXSDNS();
        }
        return nsc;
    }
    
    public boolean isSAMLEK(){
        return this.isSAMLEK;
    }
    
    public void isSAMLEK(boolean isSAMLEK){
        this.isSAMLEK = isSAMLEK;
    }
    
    public AddressingVersion getAddressingVersion(){
        return addrVer;
    }
    
    public void setAddressingVersion(AddressingVersion addrVer){
        this.addrVer = addrVer;
    }
    
    public void setCurrentBuffer(XMLStreamBuffer buffer){
        this.xmlBuffer = buffer;
    }
    
    public XMLStreamBuffer getCurrentBuffer(){
        return xmlBuffer;
    }
    
    public void setSecure(boolean value){
        this.isSSL = value;
    }
    
    public boolean isSecure(){
        return this.isSSL;
    }
    
    public int getLayout(){
        MessagePolicy mp = (MessagePolicy)getSecurityPolicy();
        if(mp != null){
            if(MessageLayout.Strict  == mp.getLayout()){
                return 1;
            }else if(MessageLayout.Lax == mp.getLayout()){
                return 0;
            }else if(MessageLayout.LaxTsFirst == mp.getLayout()){
                return 2;
            }else if(MessageLayout.LaxTsLast == mp.getLayout()){
                return 3;
            }
        }
        return 1;
    }
    
    public void addToCurrentSecretMap(String ekId, Key key){
        currentSecretMap.put(ekId, key);
    }
    
    public Key getCurrentSecretFromMap(String ekId){
        return currentSecretMap.get(ekId);
    }
    
    public boolean getDisablePayloadBuffering(){
        return disablePayloadBuffering;
    }
    
    public void setDisablePayloadBuffering(boolean value){
        this.disablePayloadBuffering = value;
    }
    
    public boolean getDisableIncPrefix(){
        return disbaleIncPrefix;
    }
    
    public void setDisableIncPrefix(boolean disableIncPrefix){
        this.disbaleIncPrefix = disableIncPrefix;
    }
    
    public boolean getEncHeaderContent(){
        return encHeaderContent;
    }
    
    public void setEncHeaderContent(boolean encHeaderContent){
        this.encHeaderContent = encHeaderContent;
    }
    
    public void setBSP(boolean value){
        this.isBSP = value;
    }
    
    public boolean isBSP(){
        return isBSP;
    }
    
    public BasicSecurityProfile getBSPContext(){
        return bspContext;
    }
    
    public Message getPVMessage(){
        return pvMsg;
    }
    
    public void setPVMessage(Message msg){
        this.pvMsg = msg;
    }
    
    public boolean isMissingTimestampAllowed(){
        return allowMissingTimestamp;
    }
    
    public void setAllowMissingTimestamp(boolean allowMissingTimestamp){
        this.allowMissingTimestamp = allowMissingTimestamp;
    }
    
    public boolean getMustUnderstandValue(){
        return this.mustUnderstandValue;
    }
    
    public void setMustUnderstandValue(boolean muValue){
        this.mustUnderstandValue = muValue;
    }
}

