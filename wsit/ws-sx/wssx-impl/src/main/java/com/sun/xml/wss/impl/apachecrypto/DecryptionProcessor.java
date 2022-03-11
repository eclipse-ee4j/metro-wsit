/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * DecryptionProcessor.java
 *
 * Created on March 18, 2005, 3:31 PM
 */

package com.sun.xml.wss.impl.apachecrypto;

import com.sun.xml.wss.impl.misc.Base64;
import jakarta.mail.MessagingException;
import jakarta.xml.soap.SOAPException;
import org.apache.xml.security.encryption.EncryptedKey;
import org.apache.xml.security.encryption.XMLCipher;
import org.apache.xml.security.encryption.XMLEncryptionException;

import com.sun.xml.util.XMLCipherAdapter;
import com.sun.xml.wss.impl.FilterProcessingContext;
import com.sun.xml.wss.impl.PolicyTypeUtil;
import com.sun.xml.wss.impl.policy.SecurityPolicy;
import com.sun.xml.wss.impl.policy.mls.AuthenticationTokenPolicy;
import com.sun.xml.wss.impl.policy.mls.WSSPolicy;
import com.sun.xml.wss.impl.policy.verifier.EncryptionPolicyVerifier;
import com.sun.xml.wss.logging.LogDomainConstants;
import com.sun.xml.wss.impl.MessageConstants;
import com.sun.xml.wss.impl.PolicyViolationException;
import com.sun.xml.wss.impl.SecurableSoapMessage;
import com.sun.xml.wss.impl.policy.mls.Target;
import com.sun.xml.wss.impl.WssSoapFaultException;
import com.sun.xml.wss.XWSSecurityException;
import com.sun.xml.wss.core.EncryptedDataHeaderBlock;
import com.sun.xml.wss.core.EncryptedKeyHeaderBlock;
import com.sun.xml.wss.core.KeyInfoHeaderBlock;
import com.sun.xml.wss.core.ReferenceListHeaderBlock;
import com.sun.xml.wss.core.SecurityHeader;
import com.sun.xml.wss.impl.policy.mls.MessagePolicy;
import com.sun.xml.wss.impl.misc.KeyResolver;
import com.sun.xml.wss.impl.policy.mls.EncryptionPolicy;

import com.sun.xml.wss.impl.policy.mls.EncryptionTarget;
import com.sun.xml.wss.swa.MimeConstants;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.xml.soap.SOAPElement;
import jakarta.xml.soap.SOAPFactory;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import jakarta.mail.Header;
import jakarta.mail.internet.MimeBodyPart;
import javax.xml.namespace.QName;
import jakarta.xml.soap.AttachmentPart;
import jakarta.xml.soap.MimeHeader;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Kumar Jayanti
 * @author Anil Tappetla
 * @author Vishal Mahajan
 * @author K.Venugopal@sun.com
 */

public class DecryptionProcessor {
    protected static final Logger log =  Logger.getLogger( LogDomainConstants.IMPL_CRYPTO_DOMAIN,
            LogDomainConstants.IMPL_CRYPTO_DOMAIN_BUNDLE);



    /** Creates a new instance of DecryptionProcessor */
    public DecryptionProcessor() {
    }

    public static void decrypt(FilterProcessingContext context)throws XWSSecurityException{
        SecurableSoapMessage secureMessage = context.getSecurableSoapMessage();
        SecurityHeader wsseSecurity = secureMessage.findSecurityHeader();
        SOAPElement headerElement =  wsseSecurity.getCurrentHeaderElement();

        String localName = headerElement.getLocalName();
        if (log.isLoggable(Level.FINEST)) {
            log.log(Level.FINEST, "EncryptionProcessor:decrypt : LocalName is "+localName);
        }
        if(localName == null){
            context.setPVE(new PolicyViolationException(
                    "Expected one of EncryptedKey,EncryptedData,ReferenceList as per receiver"+
                    "requirements, found none"));
            context.isPrimaryPolicyViolation(true);
            return;
        }
        EncryptionPolicy inferredPolicy = null;
        if(context.getMode() == FilterProcessingContext.ADHOC || context.getMode() == FilterProcessingContext.POSTHOC){
            inferredPolicy = new EncryptionPolicy();
            context.setInferredPolicy(inferredPolicy);
        } /*else if (context.getMode() == FilterProcessingContext.WSDL_POLICY) {
            inferredPolicy = new EncryptionPolicy();
            context.getInferredSecurityPolicy().append(inferredPolicy);
        }*/

        SecretKey key =null;
        switch (localName) {
            case MessageConstants.ENCRYPTED_DATA_LNAME:
                processEncryptedData(headerElement, key, context);
                break;
            case MessageConstants.XENC_ENCRYPTED_KEY_LNAME:
                if (context.getMode() == FilterProcessingContext.WSDL_POLICY) {
                    inferredPolicy = new EncryptionPolicy();
                    context.getInferredSecurityPolicy().append(inferredPolicy);
                }
                processEncryptedKey(context, headerElement);
                break;
            case MessageConstants.XENC_REFERENCE_LIST_LNAME:
                if (context.getMode() == FilterProcessingContext.WSDL_POLICY) {
                    inferredPolicy = new EncryptionPolicy();
                    context.getInferredSecurityPolicy().append(inferredPolicy);
                }
                decryptReferenceList(headerElement, key, null, context);
                break;
            default:
                context.setPVE(new PolicyViolationException(
                        "Expected one of EncryptedKey,EncryptedData,ReferenceList as per receiver" +
                                "requirements, found " + localName));
                context.isPrimaryPolicyViolation(true);
                return;
        }

        if(context.getMode() == FilterProcessingContext.ADHOC){
            new EncryptionPolicyVerifier(context).verifyPolicy(context.getSecurityPolicy(),context.getInferredPolicy());
        }

    }

    public static void processEncryptedKey(FilterProcessingContext context,
            SOAPElement xencEncryptedKey)throws XWSSecurityException{
        boolean isBSP = false;
        //EncryptionPolicy.FeatureBinding featureBinding  = null;
        try {
            xencEncryptedKey.normalize();

            //For storing EKSHA1 in Subject
            Element cipherData = (Element)xencEncryptedKey.getChildElements(new QName(MessageConstants.XENC_NS, "CipherData", MessageConstants.XENC_PREFIX)).next();
            String cipherValue = cipherData.getElementsByTagNameNS(MessageConstants.XENC_NS, "CipherValue").item(0).getTextContent();
            byte[] decodedCipher = Base64.decode(cipherValue);
            byte[] ekSha1 = MessageDigest.getInstance("SHA-1").digest(decodedCipher);
            String encEkSha1 = Base64.encode(ekSha1);
            context.setExtraneousProperty(MessageConstants.EK_SHA1_VALUE, encEkSha1);

            EncryptedKeyHeaderBlock encKeyHB = new EncryptedKeyHeaderBlock(xencEncryptedKey);
            String encryptionAlgorithm = encKeyHB.getEncryptionMethodURI();
            SecurityPolicy securityPolicy = context.getSecurityPolicy();

            if( securityPolicy != null && PolicyTypeUtil.encryptionPolicy(securityPolicy)) {
                isBSP = ((EncryptionPolicy)securityPolicy).isBSP();
                //featureBinding = (EncryptionPolicy.FeatureBinding )((EncryptionPolicy)securityPolicy).getFeatureBinding();
            }

            //TODO: not sure what is happening here, was this introduced by manveen
            EncryptionPolicy infPolicy  = null;

            if(context.getMode() != FilterProcessingContext.DEFAULT){
                infPolicy = (EncryptionPolicy)context.getInferredPolicy();

            }
//            if(infPolicy != null){
//                featureBinding = (EncryptionPolicy.FeatureBinding )infPolicy.getFeatureBinding();
//            }

            if (isBSP) {
                if (! (MessageConstants.RSA_15_KEY_TRANSPORT.equals(encryptionAlgorithm)
                || MessageConstants.RSA_OAEP_KEY_TRANSPORT.equals(encryptionAlgorithm)
                || MessageConstants.TRIPLE_DES_KEY_WRAP.equals(encryptionAlgorithm)
                || MessageConstants.AES_KEY_WRAP_128.equals(encryptionAlgorithm)
                || MessageConstants.AES_KEY_WRAP_256.equals(encryptionAlgorithm))) {
                    log.log(Level.SEVERE, "WSS1227.keyEncryptionAlg.Violation");
                    throw new XWSSecurityException("Violation of BSP5621.  KeyEncryption algorithm" +
                            "MUST be one of #rsa-1_5,#rsa-oaep-mgf1p,#kw-tripledes,#kw-aes256,#kw-aes128");
                }
            }

            XMLCipher xmlCipher = XMLCipher.getInstance(encryptionAlgorithm);
            EncryptedKey encryptedKey = xmlCipher.loadEncryptedKey(xencEncryptedKey);

            KeyInfoHeaderBlock keyInfo =  new KeyInfoHeaderBlock(encryptedKey.getKeyInfo());
            SOAPElement refListSoapElement = null;
            String commonDataEncAlgo = null;

            refListSoapElement =   (SOAPElement) xencEncryptedKey.getChildElements(
                    SOAPFactory.newInstance().createName(MessageConstants.XENC_REFERENCE_LIST_LNAME,
                    MessageConstants.XENC_PREFIX, MessageConstants.XENC_NS)).next();
            commonDataEncAlgo = getDataEncryptionAlgorithm(refListSoapElement,context.getSecurableSoapMessage());
            //TODO :: Move this away into Policy.
            if (isBSP) {
                if (! (MessageConstants.TRIPLE_DES_BLOCK_ENCRYPTION.equalsIgnoreCase(commonDataEncAlgo)
                || MessageConstants.AES_BLOCK_ENCRYPTION_128.equalsIgnoreCase(commonDataEncAlgo)
                || MessageConstants.AES_BLOCK_ENCRYPTION_256.equalsIgnoreCase(commonDataEncAlgo))) {
                    log.log(Level.SEVERE, "WSS1228.DataEncryptionAlg.Violation");
                    throw new XWSSecurityException("Violation of BSP5620 for DataEncryption Algo permitted values");
                }
            }

            Key key =  KeyResolver.getKey(keyInfo, false, context);
            xmlCipher.init(XMLCipher.UNWRAP_MODE, key);
            if(infPolicy != null){
                WSSPolicy keyBinding = (WSSPolicy)infPolicy.getKeyBinding();

                if(PolicyTypeUtil.x509CertificateBinding(keyBinding)){
                    ((AuthenticationTokenPolicy.X509CertificateBinding)keyBinding).setKeyAlgorithm(encryptionAlgorithm);
                }else if(PolicyTypeUtil.samlTokenPolicy(keyBinding)){
                    ((AuthenticationTokenPolicy.SAMLAssertionBinding)keyBinding).setKeyAlgorithm(encryptionAlgorithm);
                }
            }
            XMLCipher dataCipher = null;
            SecretKey symmetricKey;

            try {
                symmetricKey = (SecretKey) xmlCipher.decryptKey(encryptedKey,commonDataEncAlgo);
                dataCipher =  initXMLCipher(symmetricKey, commonDataEncAlgo);
            } catch (XMLEncryptionException xmlee) {
                log.log(Level.SEVERE,
                        "WSS1200.error.decrypting.key");
                throw SecurableSoapMessage.newSOAPFaultException(
                        MessageConstants.WSSE_FAILED_CHECK,
                        "Decryption of key encryption key failed",
                        xmlee);
            }
            //Store the SecretKey in context, for EKSHA1 support
            context.setExtraneousProperty(MessageConstants.SECRET_KEY_VALUE, symmetricKey);
            if(refListSoapElement != null)
                decryptReferenceList(refListSoapElement,symmetricKey,dataCipher,context);

        } catch (WssSoapFaultException wssSfe) {
            log.log(Level.SEVERE, "WSS1229.Error.Processing.EncrpytedKey");
            throw wssSfe;
        } catch (Exception e) {
            log.log(Level.SEVERE, "WSS1229.Error.Processing.EncrpytedKey");
            throw new XWSSecurityException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private static void decryptReferenceList(SOAPElement refListSoapElement,
            SecretKey key,XMLCipher dataCipher, FilterProcessingContext context)
            throws XWSSecurityException {

        SecurableSoapMessage secureMessage = context.getSecurableSoapMessage();
        ReferenceListHeaderBlock refList =  new ReferenceListHeaderBlock(refListSoapElement);

        NodeList dataRefElements = refList.getDataRefElements();
        int numberOfEncryptedElems = refList.size();
        EncryptionPolicy policy = null;

        ArrayList targets = null;
        //Set references = new HashSet();

        boolean partialReqsMet = false;

        ArrayList optionalTargets =null;
        ArrayList requiredTargets =null;
        ArrayList attachmentTargets = null;
        boolean skipAttachments = false;
        EncryptionTarget allCT = null;
        boolean verifyReq = false;
        if(context.getMode() == FilterProcessingContext.ADHOC){
            policy = (EncryptionPolicy)context.getSecurityPolicy();
            targets = ((EncryptionPolicy.FeatureBinding)policy.getFeatureBinding()).getTargetBindings();
            optionalTargets = new ArrayList();
            requiredTargets = new ArrayList();

            int i=0;
            while(i < targets.size()){
                EncryptionTarget et = (EncryptionTarget)targets.get(i++);
                if(et.getEnforce()){
                    String value =et.getValue();
                    if(value == MessageConstants.PROCESS_ALL_ATTACHMENTS){
                        if(attachmentTargets == null){
                            attachmentTargets = new ArrayList();
                        }
                        allCT = et;
                        skipAttachments = true;
                    }else{
                        requiredTargets.add(et);
                    }
                }else{
                    optionalTargets.add(et);
                }
            }
            if(requiredTargets.size() > 0 || skipAttachments){
                verifyReq = true;
            }
        } else if(context.getMode() == FilterProcessingContext.POSTHOC) {
            policy = new EncryptionPolicy();
            MessagePolicy messagePolicy = (MessagePolicy) context.getSecurityPolicy();
            messagePolicy.append(policy);
            //policy is passed to processencryptedata method.
        }

        for (int i = 0; i < numberOfEncryptedElems; i ++) {
            String refURI = ((SOAPElement) dataRefElements.item(i)).getAttribute("URI");
            SOAPElement encDataElement = null;
            EncryptedData ed = null;

            encDataElement =(SOAPElement) secureMessage.getElementById(refURI.substring(1));
            if(encDataElement.getLocalName()==MessageConstants.ENCRYPTED_HEADER_LNAME||encDataElement.getLocalName().equals(MessageConstants.ENCRYPTED_HEADER_LNAME)){
                Iterator itr = encDataElement.getChildElements();
                SOAPElement encDataElementChild = null;
                while(itr.hasNext()){
                    encDataElementChild = (SOAPElement)itr.next();
                }
                if(encDataElementChild==null){
                    throw new XWSSecurityException("No EncryptedData child element found in EncryptedHeader");
                }
                ed = processEncryptedData(encDataElementChild, key,dataCipher, context,requiredTargets,optionalTargets,policy,false);
            }else{
                ed =processEncryptedData(encDataElement, key,dataCipher, context,requiredTargets,optionalTargets,policy,false);
            }

            if(context.getMode() == FilterProcessingContext.ADHOC && verifyReq){
                if(ed.isAttachmentData() && skipAttachments){
                    attachmentTargets.add(ed);
                    continue;
                }
                //ArrayList targetElements = getAllTargetElements(secureMsg, requiredTargets, false);
                if(!verifyTargets(secureMessage,requiredTargets,ed,true)){
                    if(optionalTargets.size() == 0){
                        log.log(Level.SEVERE, "WSS1230.failed.receiverReq");
                        throw new XWSSecurityException("Receiver requirement "+
                                "for URI"+refURI+ " is not met");
                    }else{
                        if(!verifyTargets(secureMessage,optionalTargets,ed,false)){
                            log.log(Level.SEVERE, "WSS1230.failed.receiverReq");
                            throw new XWSSecurityException("Receiver requirement "+
                                    "for URI"+refURI+ " is not met");
                        }
                    }
                }
            }
        }
        if(skipAttachments){
            int count = secureMessage.countAttachments();
            if( count > attachmentTargets.size()){
                log.log(Level.SEVERE, "WSS1238.failed.receiverReq.attachments");
                throw new XWSSecurityException("Receiver requirement cid:* is not met,only "+
                        attachmentTargets.size()+" attachments out of "+count+" were encrypted");
            }
        }
        if(context.getMode() == FilterProcessingContext.ADHOC && requiredTargets.size() > 0){
            log.log(Level.SEVERE, "WSS1239.failed.receiverReq.more");
            throw new XWSSecurityException("More receiver requirements specified "+
                    "than present in the message");
        }
    }

    @SuppressWarnings("unchecked")
    public static void processEncryptedData(SOAPElement encDataElement,SecretKey key,
            FilterProcessingContext context) throws XWSSecurityException {
        EncryptionPolicy policy = null;
        ArrayList optionalTargets =null;
        ArrayList requiredTargets =null;
        ArrayList attachmentTargets = null;
        boolean verifyReq = false;
        boolean isBSP = false;
        if(context.getMode() == FilterProcessingContext.POSTHOC){
            policy = new EncryptionPolicy();
            MessagePolicy messagePolicy = (MessagePolicy) context.getSecurityPolicy();
            isBSP = messagePolicy.isBSP();
            policy.isBSP(isBSP);
            messagePolicy.append(policy);
        }else if(context.getMode() == FilterProcessingContext.ADHOC){
            policy = (EncryptionPolicy)context.getSecurityPolicy();
            //isBSP = policy.isBSP();
            ArrayList targets = ((EncryptionPolicy.FeatureBinding)policy.getFeatureBinding()).getTargetBindings();
            optionalTargets = new ArrayList();
            requiredTargets = new ArrayList();
            int i=0;
            while(i < targets.size()){
                EncryptionTarget et = (EncryptionTarget)targets.get(i++);
                if(et.getEnforce()){
                    String value = et.getValue();
                    if(value == MessageConstants.PROCESS_ALL_ATTACHMENTS){
                        //log
                        //TODO: localize the strings
                        log.log(Level.SEVERE, "WSS1201.cid_encrypt_all_notsupported");
                    }else{
                        requiredTargets.add(et);
                    }
                }else{
                    optionalTargets.add(et);
                }
            }
            if(requiredTargets.size() > 0){
                verifyReq = true;
            }
            String id = encDataElement.getAttribute("Id");
            EncryptedElement ed = (EncryptedElement)processEncryptedData(encDataElement,
                    key,null,context,requiredTargets,optionalTargets,policy,true);

            if(requiredTargets.size() > 1){
                log.log(Level.SEVERE, "WSS1240.failed.receiverReq.moretargets");
                throw new XWSSecurityException("Receiver requirement has more targets specified");
            }

            SecurableSoapMessage secureMsg = context.getSecurableSoapMessage();
            if(verifyReq && !verifyTargets(secureMsg,requiredTargets,ed,true)){
                if(optionalTargets.size() == 0){
                    log.log(Level.SEVERE, "WSS1241.failed.receiverReq.encryptedData");
                    throw new XWSSecurityException("Receiver requirement "+
                            "for EncryptedData with ID "+id+ " is not met");
                }else{
                    if(!verifyTargets(secureMsg,optionalTargets,ed,false)){
                        log.log(Level.SEVERE, "WSS1241.failed.receiverReq.encryptedData");
                        throw new XWSSecurityException("Receiver requirement "+
                                "for EncryptedData ID "+id+ " is not met");
                    }
                }
            }
        } else if (context.getMode() == FilterProcessingContext.DEFAULT ) {

            EncryptedElement ed = (EncryptedElement)processEncryptedData(encDataElement,
                    key,null,context,requiredTargets,optionalTargets,policy,true);

        } else if (context.getMode() == FilterProcessingContext.WSDL_POLICY) {
            context.getInferredSecurityPolicy().append(new EncryptionPolicy());
            EncryptedElement ed = (EncryptedElement)processEncryptedData(encDataElement,
                    key,null,context,requiredTargets,optionalTargets,policy,true);

        }
    }

    public static EncryptedData processEncryptedData(SOAPElement encDataElement,SecretKey key,
            XMLCipher dataCipher,FilterProcessingContext context,ArrayList requiredTargets,
            ArrayList optionalTargets,EncryptionPolicy encryptionPolicy,boolean updateSH) throws XWSSecurityException {

        EncryptedDataHeaderBlock xencEncryptedData = new EncryptedDataHeaderBlock(encDataElement);
        SecurableSoapMessage secureMessage = context.getSecurableSoapMessage();
        KeyInfoHeaderBlock keyInfo = xencEncryptedData.getKeyInfo();
        String algorithm = null;
        algorithm = xencEncryptedData.getEncryptionMethodURI();

        EncryptionPolicy inferredPolicy = (EncryptionPolicy)context.getInferredPolicy();
        EncryptionPolicy.FeatureBinding fb = null;

        //used for WSDL_POLICY mode
        EncryptionPolicy inferredWsdlEncPolicy = null;
        if(context.getMode() == FilterProcessingContext.WSDL_POLICY){
            try{
                int i = context.getInferredSecurityPolicy().size() - 1;
                inferredWsdlEncPolicy = (EncryptionPolicy)context.getInferredSecurityPolicy().get(i);
            } catch(Exception e){
                log.log(Level.SEVERE, "WSS1237.Error.Processing.EncrpytedData", e);
                throw new XWSSecurityException(e);
            }
        }

        if(inferredPolicy != null){
            fb = (EncryptionPolicy.FeatureBinding)inferredPolicy.getFeatureBinding();
            fb.setDataEncryptionAlgorithm(algorithm);

        }
        SecretKey symmetricKey = null;
        if (keyInfo == null ) {
            if(key == null){
                log.log(Level.SEVERE, "WSS1231.null.SymmetricKey");
                throw new XWSSecurityException("Symmetric Key is null");
            }
            symmetricKey = key;
        } else {
            context.setDataEncryptionAlgorithm(algorithm);
            symmetricKey = (SecretKey) KeyResolver.getKey(keyInfo, false, context);
            context.setDataEncryptionAlgorithm(null);
        }

        if (symmetricKey == null) {
            log.log(Level.SEVERE, "WSS1202.couldnot.locate.symmetrickey");
            throw new XWSSecurityException("Couldn't locate symmetricKey for decryption");
        }

        boolean isAttachment = false;
        String type = xencEncryptedData.getType();
        if (type.equals(MessageConstants.ATTACHMENT_CONTENT_ONLY_URI) ||
                type.equals(MessageConstants.ATTACHMENT_COMPLETE_URI)){
            isAttachment = true;
        }

        Node parent = null;
        Node prevSibling = null;
        boolean contentOnly = false;

        Element actualEncrypted = null;
        //String processedEncryptedDataId = xencEncryptedData.getId();
        AttachmentPart encryptedAttachment = null;
        AttachmentPart _attachmentBuffer = secureMessage.createAttachmentPart();
        if (isAttachment) {
            // decrypt attachment
            String uri = xencEncryptedData.getCipherReference(false, null).getAttribute("URI");
            contentOnly = type.equals(MessageConstants.ATTACHMENT_CONTENT_ONLY_URI);

            try {
                AttachmentPart p = secureMessage.getAttachmentPart(uri);
                Iterator j = p.getAllMimeHeaders();
                while (j.hasNext()) {
                    MimeHeader mh = (MimeHeader)j.next();
                    _attachmentBuffer.setMimeHeader(mh.getName(), mh.getValue());
                }
                _attachmentBuffer.setDataHandler(p.getDataHandler());
                encryptedAttachment = decryptAttachment(secureMessage, xencEncryptedData, symmetricKey);

            } catch (IOException | MessagingException | SOAPException ioe) {
                log.log(Level.SEVERE, "WSS1232.failedto.decrypt.attachment", ioe);
                throw new XWSSecurityException(ioe);
            }
            encDataElement.detachNode();
        } else {
            parent = encDataElement.getParentNode();
            prevSibling = encDataElement.getPreviousSibling();
            if( dataCipher == null){
                dataCipher = initXMLCipher(symmetricKey, algorithm);
            }
            if(parent.getLocalName()==MessageConstants.ENCRYPTED_HEADER_LNAME||parent.getLocalName().equals(MessageConstants.ENCRYPTED_HEADER_LNAME)){
                try{
                    encDataElement.getParentNode().getParentNode().replaceChild(encDataElement, parent);
                    parent = encDataElement.getParentNode();
                    prevSibling = encDataElement.getPreviousSibling();
                }catch(DOMException e) {
                    log.log(Level.SEVERE, "WSS1242.exception.dom" ,e);
                    throw new XWSSecurityException(e);
                }
            }
            decryptElementWithCipher(dataCipher, encDataElement, secureMessage);

            SOAPElement currentNode = null;
            if(updateSH && secureMessage.findSecurityHeader().getCurrentHeaderBlockElement() ==
                    encDataElement){
                if(prevSibling == null ){
                    currentNode = (SOAPElement)parent.getFirstChild();
                }else{
                    currentNode = (SOAPElement)prevSibling.getNextSibling();
                }
                secureMessage.findSecurityHeader().setCurrentHeaderElement(currentNode);
            }

            if (xencEncryptedData.getType().equals(MessageConstants.XENC_NS+"Content")) {
                actualEncrypted = (Element)resolveEncryptedNode(parent,prevSibling,true);
                contentOnly = true;
            }else{
                if (xencEncryptedData.getType().equals(MessageConstants.XENC_NS+"Element")) {
                    actualEncrypted = (Element)resolveEncryptedNode(parent,prevSibling,false);
                    contentOnly = false;
                }
            }
        }

        if(context.getMode() == FilterProcessingContext.POSTHOC){
            //log;
            if(encryptionPolicy == null){
                encryptionPolicy = new EncryptionPolicy();
            }
            EncryptionPolicy.FeatureBinding eFB = (EncryptionPolicy.FeatureBinding )
            encryptionPolicy.getFeatureBinding();
            EncryptionTarget encTarget = new EncryptionTarget();
            //target.addCipherReferenceTransform(transform
            encTarget.setDataEncryptionAlgorithm(algorithm);
            encTarget.setContentOnly(contentOnly);
            if(isAttachment){
                encTarget.addCipherReferenceTransform(type);
            }
            if(encryptedAttachment != null){
                encTarget.setValue(encryptedAttachment.getContentId());
            }else{
                String id = actualEncrypted.getAttribute("Id");

                if("".equals(id)){
                    id = actualEncrypted.getAttributeNS(MessageConstants.WSU_NS, "Id");
                }
                encTarget.setValue(id);
            }
            encTarget.setType(Target.TARGET_TYPE_VALUE_URI);
            encTarget.setElementData(actualEncrypted);
            Iterator transformItr = xencEncryptedData.getTransforms();
            if(transformItr != null){
                while(transformItr.hasNext()){
                    encTarget.addCipherReferenceTransform((String)transformItr.next());
                }
            }
            eFB.addTargetBinding(encTarget);
            return null;
        }else if(context.getMode() == FilterProcessingContext.ADHOC ||
                context.getMode() == FilterProcessingContext.DEFAULT){
            if(isAttachment){
                return new AttachmentData(encryptedAttachment.getContentId(),contentOnly);
            }
            return new EncryptedElement(actualEncrypted, contentOnly);
        } else if(context.getMode() == FilterProcessingContext.WSDL_POLICY){
            QName qname = new QName(actualEncrypted.getNamespaceURI(), actualEncrypted.getLocalName());
            EncryptionPolicy.FeatureBinding featureBinding =
                    (EncryptionPolicy.FeatureBinding)inferredWsdlEncPolicy.getFeatureBinding();
            EncryptionTarget target = new EncryptionTarget();
            if(actualEncrypted.getNamespaceURI() != null && (actualEncrypted.getNamespaceURI().equals(MessageConstants.WSSE_NS) ||
                    actualEncrypted.getNamespaceURI().equals(MessageConstants.WSSE11_NS) ||
                    actualEncrypted.getNamespaceURI().equals(MessageConstants.WSSC_NS) ||
                    actualEncrypted.getNamespaceURI().equals(MessageConstants.WSU_NS))){
                String id = actualEncrypted.getAttribute("Id");
                if("".equals(id)){
                    id = actualEncrypted.getAttributeNS(MessageConstants.WSU_NS, "Id");
                }
                target.setValue(id);
                target.setType(EncryptionTarget.TARGET_TYPE_VALUE_URI);
            } else{
                target.setQName(qname);
                target.setType(EncryptionTarget.TARGET_TYPE_VALUE_QNAME);
            }

            target.setDataEncryptionAlgorithm(algorithm);
            target.setContentOnly(contentOnly);
            featureBinding.addTargetBinding(target);
            if (qname.getLocalPart().equals("Assertion")) {
                //TODO: check NS URI also
                featureBinding.encryptsIssuedToken(true);
            }
        }
        return null;
    }

    private static String getDataEncryptionAlgorithm(SOAPElement referenceList, SecurableSoapMessage secureMsg)
    throws XWSSecurityException {
        try{
            ReferenceListHeaderBlock refList =  new ReferenceListHeaderBlock(referenceList);
            NodeList dataRefElements = refList.getDataRefElements();
            Element dataRef = (Element)dataRefElements.item(0);
            String refURI = dataRef.getAttribute("URI");

            SOAPElement encDataElement = null;
            encDataElement =(SOAPElement) secureMsg.getElementById(refURI.substring(1));
            NodeList nodeList = encDataElement.getElementsByTagNameNS(MessageConstants.XENC_NS,"EncryptionMethod");
            if(nodeList.getLength() <= 0){
                return MessageConstants.TRIPLE_DES_BLOCK_ENCRYPTION;
            }
            Element  em = (Element)nodeList.item(0);
            if(em != null){
                String algo = em.getAttribute("Algorithm");
                if("".equals(algo)){
                    return MessageConstants.TRIPLE_DES_BLOCK_ENCRYPTION;
                }
                return algo;
            }
        }catch(XWSSecurityException xe){
            log.log(Level.SEVERE, "WSS1233.failed.get.DataEncryptionAlgorithm", xe);
            throw xe;
        }catch(Exception ex){
            log.log(Level.SEVERE, "WSS1233.failed.get.DataEncryptionAlgorithm", ex);
            throw new XWSSecurityException(ex);
        }
        return MessageConstants.TRIPLE_DES_BLOCK_ENCRYPTION;
    }

    private static AttachmentPart decryptAttachment(SecurableSoapMessage ssm,
            EncryptedDataHeaderBlock edhb, SecretKey symmetricKey)
            throws java.io.IOException,jakarta.xml.soap.SOAPException,
            jakarta.mail.MessagingException, XWSSecurityException {

        String uri = edhb.getCipherReference(false, null).getAttribute("URI");
        boolean contentOnly = edhb.getType().equals(
                MessageConstants.ATTACHMENT_CONTENT_ONLY_URI);
        String mimeType = edhb.getMimeType();
        Element dsTransform = (Element)edhb.getTransforms().next();
        /*
        if ((dsTransform = (Element)edhb.getTransforms().next()) != null) {
            // log
            throw new XWSSecurityException("Only one ds:Transform element expected " +
                                           "in xenc:EncryptedData corresponding to " +
                                           "an AttachmentPart");
        }*/

        if (!dsTransform.getAttribute("Algorithm").equals(
                MessageConstants.ATTACHMENT_CONTENT_ONLY_TRANSFORM_URI)) {
            log.log(Level.SEVERE, "WSS1234.invalid.transform=");
            throw new XWSSecurityException("Unexpected ds:Transform, " + dsTransform.getAttribute("Algorithm"));
        }

        AttachmentPart part = ssm.getAttachmentPart(uri);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        part.getDataHandler().writeTo(baos);

        byte[] cipherInput  = baos.toByteArray();
        String tmp = edhb.getEncryptionMethodURI();

        // initialize Cipher
        Cipher decryptor = null;
        byte[] cipherOutput = null;
        try {
            decryptor = XMLCipherAdapter.constructCipher(tmp);

            //decryptor = Cipher.getInstance("DESede/CBC/ISO10126Padding");

            int ivLen = decryptor.getBlockSize();
            byte[] ivBytes = new byte[ivLen];

            System.arraycopy(cipherInput, 0, ivBytes, 0, ivLen);
            IvParameterSpec iv = new IvParameterSpec(ivBytes);

            decryptor.init(Cipher.DECRYPT_MODE, symmetricKey, iv);

            cipherOutput = decryptor.doFinal(cipherInput, ivLen, cipherInput.length-ivLen);
        } catch (Exception e) {
            log.log(Level.SEVERE, "WSS1232.failedto.decrypt.attachment", e);
            throw new XWSSecurityException(e);
        }

        InputStream is = new ByteArrayInputStream(cipherOutput);
        if (contentOnly) {
            // update headers and content
            part.setContentType(mimeType);
            //jakarta.mail.internet.ContentType contentType = new jakarta.mail.internet.ContentType(mimeType);

            String[] cLength = part.getMimeHeader(MimeConstants.CONTENT_LENGTH);
            if (cLength != null && !cLength[0].equals(""))
                part.setMimeHeader(MimeConstants.CONTENT_LENGTH, Integer.toString(cipherOutput.length));


            part.clearContent();
            part.setDataHandler(new jakarta.activation.DataHandler(new _DS(cipherOutput, mimeType)));

        } else {
            MimeBodyPart decryptedAttachment = new MimeBodyPart(is);
            // validate cid
            String dcId = decryptedAttachment.getContentID();
            if (dcId == null || !uri.substring(4).equals(dcId.substring(1, dcId.length()-1))) {
                log.log(Level.SEVERE, "WSS1234.unmatched.content-id");
                throw new XWSSecurityException("Content-Ids in encrypted and decrypted attachments donot match");
            }

            part.removeAllMimeHeaders();

            // copy headers
            Enumeration h_enum = decryptedAttachment.getAllHeaders();
            while (h_enum.hasMoreElements()) {
                Header hdr = (Header)h_enum.nextElement();
                String hname = hdr.getName();
                String hvale = hdr.getValue();
                part.setMimeHeader(hname, hvale);
            }

            // set content
            part.clearContent();
            part.setDataHandler(decryptedAttachment.getDataHandler());
        }

        return part;
    }

    private static boolean verifyTargets(SecurableSoapMessage ssm,ArrayList reqTargets,
            EncryptedData encData,boolean requiredTarget) throws XWSSecurityException {
        boolean found = false;
        for(int et=0;et < reqTargets.size(); et++){
            EncryptionTarget encTarget = (EncryptionTarget)reqTargets.get(et);

            if(encData.isElementData()){
                EncryptedElement elementData = (EncryptedElement)encData;
                if(encTarget.getType() == Target.TARGET_TYPE_VALUE_URI){
                    if(encTarget.isAttachment()){
                        continue;
                    }
                    Element element = ssm.getElementById(encTarget.getValue());
                    /*if(element == null && requiredTarget){
                        throw new XWSSecurityException("Not able to resolve"+
                        " receiver requirement target "+encTarget.getValue());
                    }*/
                    EncryptedElement ee = new EncryptedElement(element,encTarget.getContentOnly());
                    if(ee.equals((EncryptedElement)encData)){
                        found = true;
                        reqTargets.remove(et);
                        break;
                    }
                }else if(encTarget.getType() == Target.TARGET_TYPE_VALUE_QNAME){
                    QName qname = encTarget.getQName();
                    String localPart = qname.getLocalPart();
                    if(localPart.equals(elementData.getElement().getLocalName())){
                        ArrayList list = getAllTargetElements(ssm,encTarget,requiredTarget);
                        if(contains(list,(EncryptedElement)encData)){
                            reqTargets.remove(et);
                            found = true;
                            break;
                        }
                    }
                }else if (encTarget.getType() == Target.TARGET_TYPE_VALUE_XPATH){
                    ArrayList list = getAllTargetElements(ssm,encTarget,requiredTarget);
                    if(contains(list,(EncryptedElement)encData)){
                        reqTargets.remove(et);
                        found = true;
                        break;
                    }
                }
            }else {
                if(encTarget.getType() == Target.TARGET_TYPE_VALUE_URI && encTarget.isAttachment()){
                    if (encTarget.getValue().startsWith("cid:") && encTarget.getEnforce()){
                        AttachmentPart ap = ssm.getAttachmentPart(encTarget.getValue());
                        AttachmentData ad = (AttachmentData)encData;
                        if(ap != null && (ad.getCID() == ap.getContentId()) &&
                                (ad.isContentOnly() == encTarget.getContentOnly())){
                            found = true;
                            reqTargets.remove(et);
                            break;
                        }
                    }else{
                        reqTargets.remove(et);
                        found = true;
                        break;
                    }
                }
            }
        }
        return found;
    }


    private static boolean contains(List targetList,EncryptedElement ee){
        for(int i=0;i<targetList.size();i++){
            EncryptedElement ed = (EncryptedElement)targetList.get(i);
            if(ed.equals(ee))
                return true;
        }
        return false;
    }
    private static boolean isEquals(EncryptedData msgEd,EncryptedData reqEd){
        if(msgEd.isElementData() && reqEd.isElementData()){
            ((EncryptedElement)msgEd).equals((EncryptedElement)reqEd);
        }else if(msgEd.isAttachmentData() && reqEd.isAttachmentData()){
            ((AttachmentData)msgEd).equals((AttachmentData)reqEd);
        }
        return false;
    }



    private static ArrayList getAllTargetElements( SecurableSoapMessage ssm,
            EncryptionTarget target, boolean reqElements) throws XWSSecurityException {

        ArrayList result = new ArrayList();

        boolean contentOnly = target.getContentOnly();

        try {
            Object obj = ssm.getMessageParts(target);

            if (obj instanceof SOAPElement){
                contribute((Node)obj, result, contentOnly);
            }else if (obj instanceof NodeList){
                contribute((NodeList)obj, result, contentOnly);
            }else if (obj instanceof Node){
                contribute((Node)obj, result, contentOnly);
            }
        } catch (XWSSecurityException xwse) {
            if (reqElements) {
                log.log(Level.SEVERE, "WSS1235.failedto.get.targetElements", xwse);
                throw xwse;
            }
        }

        return result;
    }

    private static void contribute(NodeList targetElements,ArrayList result, boolean contentOnly) {
        for (int i = 0; i < targetElements.getLength(); i ++)
            contribute(targetElements.item(i), result, contentOnly);
    }
    @SuppressWarnings("unchecked")
    private static void contribute( Node element,ArrayList result, boolean contentOnly) {
        EncryptedElement targetElement =  new EncryptedElement((Element)element, contentOnly);
        result.add(targetElement);
    }
    @SuppressWarnings("unchecked")
    private static void contribute(AttachmentPart element, ArrayList result, boolean contentOnly) {
        AttachmentData targetElement =  new AttachmentData(element.getContentId(), contentOnly);
        result.add(targetElement);
    }

    private static Node resolveEncryptedNode(Node parent,Node prevSibling,boolean contentOnly) {
        Node actualEncrypted = null;
        if (!contentOnly) {
            if (prevSibling == null)
                actualEncrypted = parent.getFirstChild();
            else
                actualEncrypted = prevSibling.getNextSibling();
        } else
            actualEncrypted = parent;
        return actualEncrypted;
    }

    private static XMLCipher initXMLCipher(Key key,String algorithm)
    throws XWSSecurityException {

        XMLCipher xmlCipher;
        try {
            xmlCipher = XMLCipher.getInstance(algorithm);
            xmlCipher.init(XMLCipher.DECRYPT_MODE, key);
        } catch (XMLEncryptionException xee) {

            log.log(
                    Level.SEVERE,
                    "WSS1203.unableto.decrypt.message",
                    new Object[] { xee.getMessage()});
            throw new XWSSecurityException("Unable to decrypt message", xee);
        }
        return xmlCipher;
    }

    private static Document decryptElementWithCipher(
            XMLCipher xmlCipher,SOAPElement element,
            SecurableSoapMessage secureMessage) {

        Document document = null;
        // TODO the following normalize() call is a workaround for a bug
        // in xmlsec.jar where it appears to fail unless all Text
        // children are merged into one.
        element.normalize();
        try {
            document = xmlCipher.doFinal(secureMessage.getSOAPPart(), element);
        } catch (Exception e) {
            log.log(
                    Level.SEVERE,
                    "WSS1203.unableto.decrypt.message",
                    new Object[] { e.getMessage()});

            XWSSecurityException xse =
                    new XWSSecurityException("Unable to decrypt message", e);
            throw SecurableSoapMessage.newSOAPFaultException(
                    MessageConstants.WSSE_FAILED_CHECK,
                    "Unable to decrypt message",
                    xse);
        }
        return document;
    }

    private interface EncryptedData{
        boolean isElementData();
        boolean isAttachmentData();
    }

    private static class AttachmentData implements EncryptedData {
        private String cid = null;
        private boolean contentOnly = false;
        public AttachmentData(String cid , boolean co){
            this.cid = cid;
            contentOnly = co;
        }
        public String getCID(){
            return cid;
        }
        public boolean isContentOnly(){
            return contentOnly;
        }

        public boolean equals(AttachmentData data){
            return cid != null && cid.equals(data.getCID()) &&
                    (contentOnly == data.isContentOnly());
        }

        @Override
        public boolean isElementData(){
            return false;
        }

        @Override
        public boolean isAttachmentData(){
            return true;
        }
    }

    private static class EncryptedElement implements EncryptedData {
        private Element element;
        private boolean contentOnly;
        private EncryptionPolicy policy = null;

        public EncryptedElement(Element element, boolean contentOnly) {
            this.element = element;
            this.contentOnly = contentOnly;
        }

        public Element getElement() {
            return element;
        }

        public boolean getContentOnly() {
            return contentOnly;
        }

        public boolean equals(EncryptedElement element) {
            return (element.getElement() == this.element &&
                    element.getContentOnly() == this.contentOnly);
            //&& this.policy.equals(encryptedElement.getPolicy()));

        }

        public void setpolicy(EncryptionPolicy policy){
            this.policy = policy;
        }

        public EncryptionPolicy getPolicy(){
            return policy;
        }

        @Override
        public boolean isElementData(){
            return true;
        }

        @Override
        public boolean isAttachmentData(){
            return false;
        }
    }

    private static class _DS implements jakarta.activation.DataSource {
        byte[] _b = null;
        String _mt = null;

        _DS(byte[] b, String mt) { this._b = b; this._mt = mt; }

        @Override
        public java.io.InputStream getInputStream() {
            return new java.io.ByteArrayInputStream(_b);
        }

        @Override
        public java.io.OutputStream getOutputStream() {
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            baos.write(_b, 0, _b.length);
            return baos;
        }

        @Override
        public String getName() { return "_DS"; }

        @Override
        public String getContentType() { return _mt; }
    }
}
