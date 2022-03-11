/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.impl.filter;

import com.sun.xml.stream.buffer.MutableXMLStreamBuffer;
import com.sun.xml.stream.buffer.stax.StreamWriterBufferCreator;
import com.sun.xml.ws.security.opt.api.SecurityHeaderElement;
import com.sun.xml.ws.security.opt.impl.JAXBFilterProcessingContext;
import com.sun.xml.ws.security.opt.impl.crypto.SSEData;
import com.sun.xml.ws.security.opt.impl.message.GSHeaderElement;
import com.sun.xml.ws.security.opt.impl.util.NamespaceContextEx;
import com.sun.xml.ws.security.opt.impl.util.WSSElementFactory;

import javax.xml.crypto.Data;
import java.util.HashMap;

import com.sun.xml.wss.impl.policy.mls.KeyBindingBase;
import com.sun.xml.wss.saml.SAMLException;
import com.sun.xml.wss.impl.SecurableSoapMessage;
import com.sun.xml.wss.XWSSecurityException;
import com.sun.xml.wss.impl.FilterProcessingContext;
import com.sun.xml.wss.saml.Assertion;
import com.sun.xml.wss.core.SecurityHeader;
import com.sun.xml.wss.core.SecurityTokenReference;
import com.sun.xml.wss.impl.configuration.DynamicApplicationContext;
import com.sun.xml.wss.impl.keyinfo.KeyIdentifierStrategy;
import com.sun.xml.wss.impl.policy.mls.AuthenticationTokenPolicy;

import com.sun.xml.wss.impl.MessageConstants;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import org.w3c.dom.Element;

/*
 *
 */
public class ExportSamlAssertionFilter {
    private static XMLStreamReader reader;
    private static MutableXMLStreamBuffer buffer;
    private static String id;
    private static String version;
    
    /* (non-Javadoc)
     */
    @SuppressWarnings("unchecked")
    public static void process(FilterProcessingContext context) throws XWSSecurityException {
        
        //make a DynamicPolicyCallback to obtain the SAML assertion
        
        boolean isOptimized = false;
        SecurableSoapMessage secureMessage = null;
        SecurityHeader securityHeader = null;
        com.sun.xml.ws.security.opt.impl.outgoing.SecurityHeader optSecHeader = null;
        SecurityHeaderElement she = null;
        if(context instanceof JAXBFilterProcessingContext){
            isOptimized = true;
            optSecHeader = ((JAXBFilterProcessingContext)context).getSecurityHeader();
        } else{
            secureMessage = context.getSecurableSoapMessage();
            securityHeader = secureMessage.findOrCreateSecurityHeader();
        }
        
        AuthenticationTokenPolicy policy =
                (AuthenticationTokenPolicy)context.getSecurityPolicy();
        AuthenticationTokenPolicy.SAMLAssertionBinding samlPolicy =
                (AuthenticationTokenPolicy.SAMLAssertionBinding)policy.getFeatureBinding();
        
        if (samlPolicy.getIncludeToken() == KeyBindingBase.INCLUDE_ONCE) {
            throw new XWSSecurityException("Include Token ONCE not supported for SAMLToken Assertions");
        }
        
        if (samlPolicy.getAssertionType() !=
                AuthenticationTokenPolicy.SAMLAssertionBinding.SV_ASSERTION) {
            // should never be called this way
            throw new XWSSecurityException(
                    "Internal Error: ExportSamlAssertionFilter called for HOK assertion");
        }
        
        //AuthenticationTokenPolicy policyClone = (AuthenticationTokenPolicy)policy.clone();
        samlPolicy =
                (AuthenticationTokenPolicy.SAMLAssertionBinding)policy.getFeatureBinding();
        samlPolicy.isReadOnly(true);
        
        DynamicApplicationContext dynamicContext =
                new DynamicApplicationContext(context.getPolicyContext());
        dynamicContext.setMessageIdentifier(context.getMessageIdentifier());
        dynamicContext.inBoundMessage(false);
        
        AuthenticationTokenPolicy.SAMLAssertionBinding resolvedPolicy =
                context.getSecurityEnvironment().populateSAMLPolicy(context.getExtraneousProperties(), samlPolicy, dynamicContext);
        
        Assertion _assertion = null;
        Element assertionElement = resolvedPolicy.getAssertion();
        Element _authorityBinding = resolvedPolicy.getAuthorityBinding();
                
        if (assertionElement == null) {
            reader = resolvedPolicy.getAssertionReader();
            if (reader != null) {
                try {
                    reader.next(); //start document , so move to next event
                    id = reader.getAttributeValue(null, "AssertionID");
                    if (id == null) {
                        id = reader.getAttributeValue(null, "ID");
                    }
                    version = reader.getAttributeValue(null, "Version");
                    buffer = new MutableXMLStreamBuffer();
                    StreamWriterBufferCreator bCreator = new StreamWriterBufferCreator(buffer);
                    XMLStreamWriter writer_tmp = bCreator;
                    while (!(XMLStreamReader.END_DOCUMENT == reader.getEventType())) {
                       com.sun.xml.ws.security.opt.impl.util.StreamUtil.writeCurrentEvent(reader, writer_tmp);
                       reader.next();                       
                    }
                } catch (XMLStreamException ex) {
                   throw new XWSSecurityException(ex);
                }
            }
        } else {
            try {
                if (System.getProperty("com.sun.xml.wss.saml.binding.jaxb") == null) {
                    if (assertionElement.getAttributeNode("ID") != null) {
                        _assertion = com.sun.xml.wss.saml.assertion.saml20.jaxb20.Assertion.fromElement(assertionElement);
                    } else {
                        _assertion = com.sun.xml.wss.saml.assertion.saml11.jaxb20.Assertion.fromElement(assertionElement);
                    }
                }
            } catch (SAMLException ex) {
                //ignore
            }
        }

        if (samlPolicy.getIncludeToken() == KeyBindingBase.INCLUDE_NEVER ||
               samlPolicy.getIncludeToken() == KeyBindingBase.INCLUDE_NEVER_VER2) {
            if (_authorityBinding != null) {
                //nullify the assertion set by Callback since IncludeToken is never
                // do this because we have to maintain BackwardCompat with XWSS2.0
                assertionElement = null;
            }
        }
        
        if ((_assertion == null) && (_authorityBinding == null) && reader == null) {
            throw new XWSSecurityException(
                    "None of SAML Assertion,SAML Assertion Reader or  SAML AuthorityBinding information was set into " +
                    " the Policy by the CallbackHandler");
        }
        
        //TODO: check that the Confirmation Method of the assertion is indeed SV
        if (_assertion != null){
            if(_assertion.getVersion() == null && _authorityBinding == null){
                if(!isOptimized){
                    if ( System.getProperty("com.sun.xml.wss.saml.binding.jaxb") == null) {
                        _assertion.toElement(securityHeader);
                    }
                } else {
                    she = new GSHeaderElement(assertionElement, ((JAXBFilterProcessingContext) context).getSOAPVersion());
                    if (optSecHeader.getChildElement(she.getId()) == null) {
                        optSecHeader.add(she);
                    } else {
                        return;
                    }
                }
                HashMap tokenCache = context.getTokenCache();
                //assuming unique IDs
                tokenCache.put(_assertion.getAssertionID(), _assertion);
            } else if (_assertion.getVersion() != null){
                if(!isOptimized){
                    _assertion.toElement(securityHeader);
                } else {
                    she = new GSHeaderElement(assertionElement, ((JAXBFilterProcessingContext) context).getSOAPVersion());
                    if (optSecHeader.getChildElement(she.getId()) == null) {
                        optSecHeader.add(she);
                    } else {
                        return;
                    }
                }
                HashMap tokenCache = context.getTokenCache();
                //assuming unique IDs
                tokenCache.put(_assertion.getID(), _assertion);
            }  else {
                //Authoritybinding is set. So the Assertion should not be exported
                if (null == resolvedPolicy.getSTRID()) {
                    throw new XWSSecurityException(
                            "Unsupported configuration: required wsu:Id value " +
                            " for SecurityTokenReference to Remote SAML Assertion not found " +
                            " in Policy");
                }
            }
        } else if(reader != null) {
            she = new GSHeaderElement(buffer);
            she.setId(id);  // set the ID again to bring it to top 
            if (optSecHeader.getChildElement(she.getId()) == null) {
                optSecHeader.add(she);
            } else {
                return;
            }
        }
        
        if (null != resolvedPolicy.getSTRID()) {
            //generate and export an STR into the Header with the given ID
            if ((_assertion == null) && (null == resolvedPolicy.getAssertionId()) && reader == null) {
                throw new XWSSecurityException(
                        "None of SAML Assertion, SAML Assertion Reader or SAML Assertion Id information was set into " +
                        " the Policy by the CallbackHandler");
            }
            
            String assertionId = resolvedPolicy.getAssertionId();
            if (_assertion != null) {
                assertionId = _assertion.getAssertionID();
            } else {
                assertionId = (id != null) ? id : assertionId ;
            }
            if(!isOptimized){
                SecurityTokenReference tokenRef = new SecurityTokenReference(secureMessage.getSOAPPart());
                tokenRef.setWsuId(resolvedPolicy.getSTRID());
                // set wsse11:TokenType to SAML1.1 or SAML2.0
                if(_assertion != null && _assertion.getVersion() != null){
                    tokenRef.setTokenType(MessageConstants.WSSE_SAML_v2_0_TOKEN_TYPE);
                } else {
                    if (reader != null) {                        
                        if (version == "2.0") {
                            tokenRef.setTokenType(MessageConstants.WSSE_SAML_v2_0_TOKEN_TYPE);
                        } else {
                            tokenRef.setTokenType(MessageConstants.WSSE_SAML_v1_1_TOKEN_TYPE);
                        }
                    } else {
                        tokenRef.setTokenType(MessageConstants.WSSE_SAML_v1_1_TOKEN_TYPE);
                    }
                }
                
                if (_authorityBinding != null) {
                    tokenRef.setSamlAuthorityBinding(_authorityBinding, secureMessage.getSOAPPart());
                }
                
                KeyIdentifierStrategy strat = new KeyIdentifierStrategy(assertionId);
                strat.insertKey(tokenRef, context.getSecurableSoapMessage());
                securityHeader.insertHeaderBlock(tokenRef);
            } else{
                JAXBFilterProcessingContext optContext = (JAXBFilterProcessingContext)context;
                WSSElementFactory elementFactory = new WSSElementFactory(optContext.getSOAPVersion());
                com.sun.xml.ws.security.opt.impl.reference.KeyIdentifier ref = elementFactory.createKeyIdentifier();
                ref.setValue(assertionId);
                if(_assertion != null && _assertion.getVersion() != null){
                    ref.setValueType(MessageConstants.WSSE_SAML_v2_0_KEY_IDENTIFIER_VALUE_TYPE);
                } else{
                    if (reader != null) {                        
                        if (version == "2.0") {
                            ref.setValueType(MessageConstants.WSSE_SAML_v2_0_KEY_IDENTIFIER_VALUE_TYPE);
                        } else {
                            ref.setValueType(MessageConstants.WSSE_SAML_KEY_IDENTIFIER_VALUE_TYPE);
                        }
                    } else {
                        ref.setValueType(MessageConstants.WSSE_SAML_KEY_IDENTIFIER_VALUE_TYPE);
                    }
                }
                com.sun.xml.ws.security.opt.impl.keyinfo.SecurityTokenReference secTokRef = elementFactory.createSecurityTokenReference(ref);
                String strId = resolvedPolicy.getSTRID();
                secTokRef.setId(strId);
                if("true".equals(optContext.getExtraneousProperty("EnableWSS11PolicySender"))){
                    // set wsse11:TokenType to SAML1.1 or SAML2.0
                    if(_assertion != null && _assertion.getVersion() != null){
                        secTokRef.setTokenType(MessageConstants.WSSE_SAML_v2_0_TOKEN_TYPE);
                    }else{
                       if (reader != null) {                        
                        if (version == "2.0") {
                            secTokRef.setTokenType(MessageConstants.WSSE_SAML_v2_0_TOKEN_TYPE);
                        } else {
                            secTokRef.setTokenType(MessageConstants.WSSE_SAML_v1_1_TOKEN_TYPE);
                        }
                    } else {
                        secTokRef.setTokenType(MessageConstants.WSSE_SAML_v1_1_TOKEN_TYPE);
                    }
                    }
                    ((NamespaceContextEx)optContext.getNamespaceContext()).addWSS11NS();
                }
                Data data = new SSEData(she,false,optContext.getNamespaceContext());
                optContext.getElementCache().put(strId,data);
                optSecHeader.add(secTokRef);
            }
        }
        
    }    
}
