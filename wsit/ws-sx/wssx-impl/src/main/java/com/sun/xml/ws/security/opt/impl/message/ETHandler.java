/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.opt.impl.message;

import com.sun.xml.ws.message.jaxb.JAXBHeader;
import com.sun.xml.ws.security.opt.api.SecurityElement;
import com.sun.xml.ws.security.opt.api.SecurityHeaderElement;
import com.sun.xml.ws.security.opt.impl.dsig.SignedMessageHeader;
import com.sun.xml.ws.security.opt.impl.outgoing.SecurityHeader;
import com.sun.xml.ws.security.opt.impl.util.JAXBUtil;
import com.sun.xml.ws.security.opt.impl.util.NamespaceContextEx;
import com.sun.xml.ws.security.opt.impl.util.WSSElementFactory;
import com.sun.xml.ws.security.opt.impl.crypto.JAXBDataImpl;
import com.sun.xml.ws.security.opt.impl.crypto.SSBData;
import com.sun.xml.ws.security.opt.impl.crypto.SSEData;
import com.sun.xml.ws.security.opt.impl.crypto.StreamHeaderData;
import com.sun.xml.ws.security.opt.impl.util.WSSNSPrefixWrapper;
import com.sun.xml.wss.XWSSecurityException;
import com.sun.xml.ws.security.opt.crypto.dsig.keyinfo.KeyInfo;
import com.sun.xml.wss.impl.MessageConstants;
import com.sun.xml.wss.impl.misc.SecurityUtil;
import com.sun.xml.wss.impl.policy.mls.EncryptionPolicy;
import com.sun.xml.ws.security.opt.impl.JAXBFilterProcessingContext;
import com.sun.xml.wss.impl.policy.mls.Target;

import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.xml.crypto.Data;
import javax.xml.namespace.QName;

import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.message.Attachment;
import com.sun.xml.ws.api.message.AttachmentSet;
import com.sun.xml.ws.security.opt.api.keyinfo.SecurityTokenReference;
import com.sun.xml.ws.security.opt.api.reference.DirectReference;
import com.sun.xml.ws.security.opt.api.reference.KeyIdentifier;
import com.sun.xml.ws.security.opt.api.reference.Reference;
import com.sun.xml.ws.security.opt.impl.attachment.AttachmentSetImpl;
import com.sun.xml.ws.security.opt.impl.attachment.EncryptedAttachment;
import com.sun.xml.wss.impl.policy.mls.EncryptionTarget;
import com.sun.xml.wss.logging.LogDomainConstants;
import com.sun.xml.wss.logging.impl.crypto.LogStringsMessages;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author K.Venugopal@sun.com
 */
public class ETHandler {
    
    private static final Logger logger = Logger.getLogger(LogDomainConstants.IMPL_OPT_CRYPTO_DOMAIN,
            LogDomainConstants.IMPL_OPT_CRYPTO_DOMAIN_BUNDLE);
    
    private WSSElementFactory wsf = null;
    private HashMap props = new HashMap();
    /** Creates a new instance of MessageETHandler */
    @SuppressWarnings("unchecked")
    public ETHandler(SOAPVersion soapVersion) {
        wsf = new WSSElementFactory(soapVersion);
        if(soapVersion == SOAPVersion.SOAP_11){
            props.put("org.glassfish.jaxb.runtime.marshaller.namespacePrefixMapper", new WSSNSPrefixWrapper(JAXBUtil.prefixMapper11));
        } else{
            props.put("org.glassfish.jaxb.runtime.marshaller.namespacePrefixMapper", new WSSNSPrefixWrapper(JAXBUtil.prefixMapper12));
        }
    }
    
    @SuppressWarnings("unchecked")
    public List buildEDList( EncryptionPolicy policy,final Target target ,JAXBFilterProcessingContext context,Key key,KeyInfo ki) throws XWSSecurityException{
        
        SecuredMessage message = context.getSecuredMessage();
        ArrayList edList = new ArrayList();
        if(target.getType() == Target.TARGET_TYPE_VALUE_QNAME){
            QName name = target.getQName();
            if(name == Target.BODY_QNAME){
                Object obj = message.getBody();
                String dataEncAlg =  SecurityUtil.getDataEncryptionAlgo(context);
                if(dataEncAlg.length() == 0){
                    if (context.getAlgorithmSuite() != null) {
                        dataEncAlg = context.getAlgorithmSuite().getEncryptionAlgorithm();
                    }
                }
                Data data = null;
                if(obj instanceof SOAPBody){
                    data =  new SSBData((SOAPBody)obj,true, context.getNamespaceContext());
                    SecurityHeaderElement ed = (SecurityHeaderElement) wsf.createEncryptedData(context.generateID(),data,dataEncAlg,ki,key,true);
                    edList.add(ed);
                    SOAPBody sb =(SOAPBody) message.getBody();
                    SOAPBody nsb = new SOAPBody(ed,context.getSOAPVersion());
                    nsb.setId(sb.getId());
                    message.replaceBody(nsb);
                }else if(obj instanceof SecurityElement){
                    data = new SSEData((SecurityElement)obj, true, context.getNamespaceContext(), props);
                    SecurityHeaderElement ed = (SecurityHeaderElement) wsf.createEncryptedData(context.generateID(),data,dataEncAlg,ki,key,true);
                    edList.add(ed);
                    SOAPBody nsb = new SOAPBody(ed,context.getSOAPVersion());
                    nsb.setId(((SecurityElement)obj).getId());
                    message.replaceBody(nsb);
                }
                return edList;
            }
            
            // Look for Id or wsu:Id attribute in all elements
            java.util.Iterator headers = null;
            if(name.getNamespaceURI().equals(MessageConstants.ADDRESSING_MEMBER_SUBMISSION_NAMESPACE) ||
                    name.getNamespaceURI().equals(MessageConstants.ADDRESSING_W3C_NAMESPACE)){
                if(!"".equals(name.getLocalPart()))
                    headers = message.getHeaders(name.getLocalPart(), null);
                else{
                    headers = message.getHeaders(MessageConstants.ADDRESSING_MEMBER_SUBMISSION_NAMESPACE);
                    if(!headers.hasNext())
                        headers = message.getHeaders(MessageConstants.ADDRESSING_W3C_NAMESPACE);
                }
            } else {
                if(!"".equals(name.getLocalPart()))
                    headers = message.getHeaders(name.getLocalPart(), name.getNamespaceURI());
                else
                    headers = message.getHeaders(name.getNamespaceURI());
            }
            
            while(headers.hasNext()){
                Object header = headers.next();
                SecurityHeaderElement ed = toMessageHeader(policy,target,context,key,header,ki, true);
                edList.add(ed);
            }
            
            if(!edList.isEmpty()){
                return edList;
            }
            SecurityHeader sh = context.getSecurityHeader();
            
            Iterator itr = sh.getHeaders(name.getLocalPart(),name.getNamespaceURI());
            while(itr.hasNext()){
                SecurityHeaderElement hdr = (SecurityHeaderElement)itr.next();
                if(hdr != null){
                    SecurityHeaderElement ed = toMessageHeader(policy,target,context,key,hdr,ki, false);
                    edList.add(ed);
                }
            }
            return edList;
        }else if(target.getType() == Target.TARGET_TYPE_VALUE_URI){
            
            if(MessageConstants.PROCESS_ALL_ATTACHMENTS.equals(target.getValue())){
                handleAttachments(context, edList, key,ki ,target);
            } else{
                SecurityHeaderElement se = handleURI(policy,target,context,key,ki);
                edList.add(se);
            }
            return edList;
            //TODO
            // throw new UnsupportedOperationException("Target Type "+target.getType() +" is not supported by EncryptionProcessor");
        }
        throw new UnsupportedOperationException("Target Type "+target.getType() +" is not supported by EncryptionProcessor");
        
    }
    
    
    protected SecurityHeaderElement handleURI(EncryptionPolicy policy,Target target ,JAXBFilterProcessingContext context,Key key,KeyInfo ki) throws XWSSecurityException{
        String dataEncAlg =  SecurityUtil.getDataEncryptionAlgo(context);
        boolean contentOnly = target.getContentOnly();
        Object header = context.getSecurityHeader().getChildElement(target.getValue());
        if(header != null){
            
            if(header instanceof SecurityTokenReference){
                SecurityTokenReference str = (SecurityTokenReference)header;
                Reference reference = str.getReference();
                String refValue = null;
                if(MessageConstants.KEY_INDETIFIER_TYPE.equals(reference.getType())){
                    refValue = ((KeyIdentifier)reference).getReferenceValue();
                } else if(MessageConstants.DIRECT_REFERENCE_TYPE.equals(reference.getType())){
                    refValue = ((DirectReference)reference).getURI();
                }
                if(refValue != null){
                    if(refValue.startsWith("#")){
                        refValue = refValue.substring(1);
                    }
                    header = context.getSecurityHeader().getChildElement(refValue);
                }
            }    
            
            Data data = toData(header,contentOnly, context);
            SecurityHeaderElement ed = (SecurityHeaderElement) wsf.createEncryptedData(context.generateID(),data,dataEncAlg,ki,key,target.getContentOnly());
            context.getSecurityHeader().replace((SecurityHeaderElement) header, ed);
            return ed;
        }else{
            header = context.getSecuredMessage().getHeader(target.getValue());
            return toMessageHeader(policy,target,context,key,header,ki, true);
        }
    }
    
    protected SecurityHeaderElement toMessageHeader(EncryptionPolicy policy,Target target ,JAXBFilterProcessingContext context,
            Key key,Object header,KeyInfo ki, boolean isEncryptedHeaders) throws XWSSecurityException{
        SecuredMessage message = context.getSecuredMessage();
        String dataEncAlg =  SecurityUtil.getDataEncryptionAlgo(context);
        boolean contentOnly = target.getContentOnly();
        
        boolean encHeaderContent = context.getEncHeaderContent();
        if(encHeaderContent && !"true".equals(context.getExtraneousProperty("EnableWSS11PolicySender"))){
            contentOnly = true;
        }
        
        Data data = toData(header,contentOnly, context);
        SecurityHeaderElement ed = null;
        
        if(contentOnly){
            ed = (SecurityHeaderElement) wsf.createEncryptedData(context.generateID(),data,dataEncAlg,ki,key,contentOnly);
            if(header instanceof com.sun.xml.ws.security.opt.impl.message.Header){
                throw new XWSSecurityException("Implementation does not support encrypting content which is already encrypted ");
            } else if(header instanceof SignedMessageHeader){
                EncryptedSignedMessageHeader encHdr = new EncryptedSignedMessageHeader((SignedMessageHeader)header, ed);
                message.replaceHeader(header, encHdr);
            } else{
                com.sun.xml.ws.security.opt.impl.message.Header hdr = new com.sun.xml.ws.security.opt.impl.message.Header((com.sun.xml.ws.api.message.Header)header,ed);
                message.replaceHeader(header,hdr);
            }
            
        }else{
            if(isEncryptedHeaders && "true".equals(context.getExtraneousProperty("EnableWSS11PolicySender"))){
                ed = (SecurityHeaderElement) wsf.createEncryptedHeader(context.generateID(),context.generateID(),data,dataEncAlg,ki,key,contentOnly);
                ((NamespaceContextEx)context.getNamespaceContext()).addWSS11NS();
            } else{
                ed = (SecurityHeaderElement) wsf.createEncryptedData(context.generateID(),data,dataEncAlg,ki,key,contentOnly);
            }
            if(!message.replaceHeader(header,ed)){
                context.getSecurityHeader().replace((SecurityHeaderElement) header, ed);
            }
        }
        return ed;
    }
    
    
    protected Data toData(Object header,boolean contentOnly, JAXBFilterProcessingContext context)throws XWSSecurityException{
        if(header instanceof SecurityElement){
            return new SSEData((SecurityElement)header,contentOnly, context.getNamespaceContext(), props);
        }if(header instanceof JAXBHeader){
            return new  JAXBDataImpl((com.sun.xml.ws.api.message.Header) header, contentOnly, context.getNamespaceContext(),JAXBUtil.getSEIJAXBContext());
        }else if(header instanceof com.sun.xml.ws.api.message.Header){
            return new StreamHeaderData((com.sun.xml.ws.api.message.Header)header,contentOnly, context.getNamespaceContext());
        }else{
            throw new XWSSecurityException("Unsupported Header type");
        }
    }
    @SuppressWarnings("unchecked")
    private void handleAttachments(JAXBFilterProcessingContext context, ArrayList edList, Key key,KeyInfo ki,Target target) throws XWSSecurityException {
        SecuredMessage message = context.getSecuredMessage();
        AttachmentSet as = message.getAttachments();
        if(as != null && as.isEmpty()){
            logger.log(Level.WARNING, LogStringsMessages.WSS_1244_NO_ATTACHMENT_FOUND());
            return;
        }
        String dataEncAlg =  SecurityUtil.getDataEncryptionAlgo(context);
        AttachmentSet newAttachmentSet = new AttachmentSetImpl();
        for(Attachment attachment : as){
            SecurityHeaderElement ed = (SecurityHeaderElement)wsf.createEncryptedData(context.generateID(), attachment, dataEncAlg, ki, key, (EncryptionTarget)target);
            context.getSecurityHeader().add(ed);
            edList.add(ed);
            Attachment encryptedAttachment = new EncryptedAttachment(attachment, dataEncAlg, key);
            newAttachmentSet.add(encryptedAttachment);
        }
        message.setAttachments(newAttachmentSet);
    }
}
