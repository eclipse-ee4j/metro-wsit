/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.opt.impl.keyinfo;

import org.apache.xml.security.encryption.XMLCipher;
import com.sun.xml.stream.buffer.MutableXMLStreamBuffer;
import com.sun.xml.stream.buffer.stax.StreamWriterBufferCreator;
import com.sun.xml.ws.security.opt.api.SecurityHeaderElement;
import com.sun.xml.ws.security.opt.api.keyinfo.BuilderResult;
import com.sun.xml.ws.security.opt.impl.enc.JAXBEncryptedKey;
import com.sun.xml.ws.security.opt.impl.incoming.SAMLAssertion;
import com.sun.xml.ws.security.opt.impl.reference.DirectReference;
import com.sun.xml.ws.security.opt.impl.reference.KeyIdentifier;
import com.sun.xml.ws.security.opt.impl.message.GSHeaderElement;
import com.sun.xml.wss.XWSSecurityException;
import com.sun.xml.wss.impl.MessageConstants;
import com.sun.xml.wss.impl.misc.SecurityUtil;
import com.sun.xml.wss.impl.policy.mls.AuthenticationTokenPolicy;
import com.sun.xml.ws.security.opt.impl.util.NamespaceContextEx;
import com.sun.xml.ws.security.opt.impl.JAXBFilterProcessingContext;
import com.sun.xml.wss.impl.policy.mls.PrivateKeyBinding;
import com.sun.xml.wss.logging.impl.opt.token.LogStringsMessages;

import java.security.Key;
import java.util.HashMap;
import java.util.logging.Level;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import org.w3c.dom.Element;

/**
 *
 * @author K.Venugopal@sun.com
 */
public class SamlTokenBuilder extends TokenBuilder{
    
    private AuthenticationTokenPolicy.SAMLAssertionBinding keyBinding = null;
    private boolean forSign = false;
    private String id;    
    private MutableXMLStreamBuffer buffer;
    private XMLStreamReader reader;
    /** Creates a new instance of SamlTokenProcessor */
    public SamlTokenBuilder(JAXBFilterProcessingContext context,AuthenticationTokenPolicy.SAMLAssertionBinding samlBinding,boolean forSign) {
        super(context);
        this.forSign = forSign;
        this.keyBinding = samlBinding;
    }
    /**
     * 
     * @return BuilderResult
     */
    @SuppressWarnings("unchecked")
    @Override
    public BuilderResult process() throws XWSSecurityException {
        BuilderResult result = new BuilderResult();
        String assertionId;
        
        SecurityHeaderElement she = null;
        
        Element samlAssertion = keyBinding.getAssertion();
        if (samlAssertion == null) {
             reader = keyBinding.getAssertionReader();
            if (reader != null) {
                try {
                    reader.next(); //start document , so move to next event
                    id = reader.getAttributeValue(null, "AssertionID");
                    if (id == null) {
                        id = reader.getAttributeValue(null, "ID");
                    }
                    //version = reader.getAttributeValue(null, "Version");
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
        }

        if (samlAssertion != null) {
            she = new GSHeaderElement(samlAssertion);
        }else if (reader != null) {
            she = new GSHeaderElement(buffer);
            she.setId(id);  // set the ID again to bring it to top            
        }
        JAXBEncryptedKey ek;
        String asID;
        String idVal = "";
        String keyEncAlgo = XMLCipher.RSA_v1dot5;        
        Key samlkey = null;
        if(samlAssertion != null){
            asID = samlAssertion.getAttributeNS(null,"AssertionID");
            if(she == null){
                logger.log(Level.SEVERE, LogStringsMessages.WSS_1811_NULL_SAML_ASSERTION());
                throw new XWSSecurityException("SAML Assertion is NULL");
            }
            if(asID == null || asID.length() ==0){
                idVal = samlAssertion.getAttributeNS(null,"ID");
                she.setId(idVal);
            }else{
                she.setId(asID);
            }
        }else {
            if (she == null) {
                she = (SecurityHeaderElement) context.getExtraneousProperty(MessageConstants.INCOMING_SAML_ASSERTION);
            }
            if (she == null) {
                logger.log(Level.SEVERE, LogStringsMessages.WSS_1811_NULL_SAML_ASSERTION());
                throw new XWSSecurityException("SAML Assertion is NULL");
            }
            idVal = asID = she.getId();
        }
        if(logger.isLoggable(Level.FINEST)){
            logger.log(Level.FINEST, "SAML Assertion id:{0}", asID);
        }
        
        Key dataProtectionKey;
        if(forSign){
            PrivateKeyBinding privKBinding  = (PrivateKeyBinding)keyBinding.getKeyBinding();
            dataProtectionKey = privKBinding.getPrivateKey();
            if (dataProtectionKey == null) {
                logger.log(Level.SEVERE, LogStringsMessages.WSS_1810_NULL_PRIVATEKEY_SAML());
                throw new XWSSecurityException("PrivateKey null inside PrivateKeyBinding set for SAML Policy ");
            }
            
            if(context.getSecurityHeader().getChildElement(she.getId()) == null){
                context.getSecurityHeader().add(she);
            }
            
        } else {
            SecurityHeaderElement assertion = (SecurityHeaderElement) context.getExtraneousProperty(MessageConstants.INCOMING_SAML_ASSERTION);
            samlkey = ((SAMLAssertion) assertion).getKey();
            /*
            x509Cert = context.getSecurityEnvironment().getCertificate(
                    context.getExtraneousProperties() ,(PublicKey)key, false);
            if (x509Cert == null) {
                logger.log(Level.SEVERE, LogStringsMessages.WSS_1812_MISSING_CERT_SAMLASSERTION());
                throw new XWSSecurityException("Could not locate Certificate corresponding to Key in SubjectConfirmation of SAML Assertion");
            }
            */
            if (!"".equals(keyBinding.getKeyAlgorithm())) {
                keyEncAlgo = keyBinding.getKeyAlgorithm();
            }
            String dataEncAlgo = SecurityUtil.getDataEncryptionAlgo(context);
            dataProtectionKey = SecurityUtil.generateSymmetricKey(dataEncAlgo);
        }
        Element authorityBinding = keyBinding.getAuthorityBinding();
        //assertionId = keyBinding.getAssertionId();
        
        
        
        String referenceType = keyBinding.getReferenceType();
        if (referenceType.equals(MessageConstants.EMBEDDED_REFERENCE_TYPE)) {
            logger.log(Level.SEVERE, LogStringsMessages.WSS_1813_UNSUPPORTED_EMBEDDEDREFERENCETYPE_SAML());
            throw new XWSSecurityException("Embedded Reference Type for SAML Assertions not supported yet");
        }
        
        assertionId = she.getId();
        
        //todo reference different keyreference types.
        SecurityTokenReference samlSTR;
        if(authorityBinding == null){
            KeyIdentifier keyIdentifier = new KeyIdentifier(context.getSOAPVersion());
            keyIdentifier.setValue(assertionId);
            if(MessageConstants.SAML_v2_0_NS.equals(she.getNamespaceURI())){
                keyIdentifier.setValueType(MessageConstants.WSSE_SAML_v2_0_KEY_IDENTIFIER_VALUE_TYPE);
            } else{
                keyIdentifier.setValueType(MessageConstants.WSSE_SAML_KEY_IDENTIFIER_VALUE_TYPE);
            }
            samlSTR = elementFactory.createSecurityTokenReference(keyIdentifier);
            if (idVal != null) {
                samlSTR.setTokenType(MessageConstants.WSSE_SAML_v2_0_TOKEN_TYPE);
            }else{
                samlSTR.setTokenType(MessageConstants.WSSE_SAML_v1_1_TOKEN_TYPE);
            }
            //((SecurityTokenReferenceType)samlSTR).getAny().add(authorityBinding);
            ((NamespaceContextEx)context.getNamespaceContext()).addWSS11NS();
            buildKeyInfo(samlSTR);
        } else{
            //TODO: handle authorityBinding != null
        }
        
        
        if(!forSign){
            HashMap ekCache = context.getEncryptedKeyCache();
            ek = (JAXBEncryptedKey)elementFactory.createEncryptedKey(context.generateID(),keyEncAlgo,super.keyInfo,samlkey,dataProtectionKey);
            context.getSecurityHeader().add(ek);
            String ekId = ek.getId();
            DirectReference dr = buildDirectReference(ekId,MessageConstants.EncryptedKey_NS);
            result.setKeyInfo(buildKeyInfo(dr,""));
        }else{
            result.setKeyInfo(super.keyInfo);
        }
        
        HashMap sentSamlKeys = (HashMap) context.getExtraneousProperty(MessageConstants.STORED_SAML_KEYS);
        if(sentSamlKeys == null)
            sentSamlKeys = new HashMap();
        sentSamlKeys.put(assertionId, dataProtectionKey);
        context.setExtraneousProperty(MessageConstants.STORED_SAML_KEYS, sentSamlKeys);
        
        result.setDataProtectionKey(dataProtectionKey);
        
        return result;
    }
    
}
