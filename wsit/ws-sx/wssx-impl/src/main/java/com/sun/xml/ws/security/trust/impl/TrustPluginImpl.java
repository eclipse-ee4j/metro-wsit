/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.trust.impl;

import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.Messages;
import com.sun.xml.ws.api.WSService;
import com.sun.xml.ws.api.WSService.InitParams;
import com.sun.xml.ws.api.security.trust.Claims;
import com.sun.xml.ws.api.security.trust.WSTrustException;
import com.sun.xml.ws.api.security.trust.client.SecondaryIssuedTokenParameters;
import com.sun.xml.ws.mex.client.MetadataClient;
import com.sun.xml.ws.mex.client.PortInfo;
import com.sun.xml.ws.mex.client.schema.Metadata;
import com.sun.xml.ws.policy.impl.bindings.AppliesTo;
import com.sun.xml.ws.security.IssuedTokenContext;
import com.sun.xml.ws.security.Token;
import com.sun.xml.ws.security.trust.GenericToken;
import com.sun.xml.ws.security.trust.STSIssuedTokenFeature;
import com.sun.xml.ws.security.trust.TrustPlugin;
import com.sun.xml.ws.security.trust.WSTrustClientContract;
import com.sun.xml.ws.security.trust.WSTrustConstants;
import com.sun.xml.ws.security.trust.WSTrustElementFactory;
import com.sun.xml.ws.security.trust.WSTrustFactory;
import com.sun.xml.ws.security.trust.WSTrustVersion;
import com.sun.xml.ws.security.trust.elements.ActAs;
import com.sun.xml.ws.security.trust.elements.BinarySecret;
import com.sun.xml.ws.security.trust.elements.Entropy;
import com.sun.xml.ws.security.trust.elements.Lifetime;
import com.sun.xml.ws.security.trust.elements.OnBehalfOf;
import com.sun.xml.ws.security.trust.elements.RequestedSecurityToken;
import com.sun.xml.ws.security.trust.elements.RequestSecurityToken;
import com.sun.xml.ws.security.trust.elements.RequestSecurityTokenResponse;
import com.sun.xml.ws.security.trust.elements.RequestSecurityTokenResponseCollection;
import com.sun.xml.ws.security.trust.elements.SecondaryParameters;
import com.sun.xml.ws.security.trust.util.WSTrustUtil;
import com.sun.xml.wss.impl.dsig.WSSPolicyConsumerImpl;
import java.net.URI;
import java.net.URL;
import java.net.URISyntaxException;
import java.net.MalformedURLException;
import java.security.SecureRandom;
import java.util.List;
import javax.xml.namespace.QName;

import com.sun.xml.wss.jaxws.impl.Constants;
import jakarta.xml.ws.Dispatch;
import jakarta.xml.ws.RespectBindingFeature;
import jakarta.xml.ws.Service;
import jakarta.xml.ws.WebServiceException;
import jakarta.xml.ws.WebServiceFeature;
import jakarta.xml.ws.BindingProvider;

import java.util.logging.Level;
import java.util.logging.Logger;
import com.sun.xml.ws.security.trust.logging.LogDomainConstants;

import jakarta.xml.ws.soap.AddressingFeature;

import com.sun.xml.ws.security.trust.logging.LogStringsMessages;


import com.sun.xml.ws.api.security.trust.client.STSIssuedTokenConfiguration;
import com.sun.xml.ws.api.server.Container;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.security.trust.elements.BaseSTSResponse;
import com.sun.xml.ws.security.trust.elements.UseKey;
import com.sun.xml.ws.security.trust.elements.ValidateTarget;
import java.security.KeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import jakarta.xml.bind.JAXBElement;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.transform.dom.DOMResult;
import org.w3c.dom.Node;

import com.sun.xml.ws.security.secext10.BinarySecurityTokenType;
import com.sun.xml.wss.impl.MessageConstants;
import com.sun.xml.security.core.ai.IdentityType;
import com.sun.xml.wss.WSITXMLFactory;
import java.security.cert.CertificateEncodingException;

/**
 *
 * @author hr124446
 */
public class TrustPluginImpl implements TrustPlugin {

    private static final Logger log =
            Logger.getLogger(
            LogDomainConstants.TRUST_IMPL_DOMAIN,
            LogDomainConstants.TRUST_IMPL_DOMAIN_BUNDLE);

    /** Creates a new instance of TrustPluginImpl */
    public TrustPluginImpl() {

    }

    @Override
    public void process(IssuedTokenContext itc) throws WSTrustException{
        String signWith = null;
        String encryptWith = null;
        String appliesTo = itc.getEndpointAddress();
        STSIssuedTokenConfiguration stsConfig = (STSIssuedTokenConfiguration)itc.getSecurityPolicy().get(0);
        String stsURI = stsConfig.getSTSEndpoint();
        if(stsURI == null){
            log.log(Level.SEVERE,
                    LogStringsMessages.WST_0029_COULD_NOT_GET_STS_LOCATION(appliesTo));
            throw new WebServiceException(LogStringsMessages.WST_0029_COULD_NOT_GET_STS_LOCATION(appliesTo));
        }
        Token oboToken = stsConfig.getOBOToken();

        BaseSTSResponse result = null;
        try {
            final RequestSecurityToken request = createRequest(stsConfig, appliesTo, oboToken);

            result = invokeRST(request, stsConfig);

            final WSTrustClientContract contract = WSTrustFactory.createWSTrustClientContract();
            contract.handleRSTR(request, result, itc);
            KeyPair keyPair = (KeyPair)stsConfig.getOtherOptions().get(WSTrustConstants.USE_KEY_RSA_KEY_PAIR);
            if (keyPair != null){
                itc.setProofKeyPair(keyPair);
            }

            encryptWith = stsConfig.getEncryptWith();
            if (encryptWith != null) {
                itc.setEncryptWith(encryptWith);
            }

            signWith = stsConfig.getSignWith();
            if (signWith != null) {
                itc.setSignWith(signWith);
            }

        } catch (URISyntaxException ex){
            log.log(Level.SEVERE,
                    LogStringsMessages.WST_0016_PROBLEM_IT_CTX(stsURI, appliesTo), ex);
            throw new WSTrustException(LogStringsMessages.WST_0016_PROBLEM_IT_CTX(stsURI, appliesTo));
        }
    }

    @Override
    public void processValidate(IssuedTokenContext itc) throws WSTrustException{
        STSIssuedTokenConfiguration stsConfig = (STSIssuedTokenConfiguration)itc.getSecurityPolicy().get(0);
        String stsURI = stsConfig.getSTSEndpoint();
        if(stsURI == null){
            log.log(Level.SEVERE,
                    LogStringsMessages.WST_0029_COULD_NOT_GET_STS_LOCATION(null));
            throw new WebServiceException(LogStringsMessages.WST_0029_COULD_NOT_GET_STS_LOCATION(null));
        }
        BaseSTSResponse result = null;
        Token token = itc.getTarget();
        final RequestSecurityToken request = createRequestForValidatation(stsConfig, token);
        result = invokeRST(request, stsConfig);
        final WSTrustClientContract contract = WSTrustFactory.createWSTrustClientContract();
        contract.handleRSTR(request, result, itc);
    }

    private RequestSecurityToken createRequest(final STSIssuedTokenConfiguration stsConfig, final String appliesTo, final Token oboToken) throws URISyntaxException, WSTrustException, NumberFormatException{
        WSTrustVersion wstVer = WSTrustVersion.getInstance(stsConfig.getProtocol());
        WSTrustElementFactory fact = WSTrustElementFactory.newInstance(wstVer);
        final URI requestType = URI.create(wstVer.getIssueRequestTypeURI());
        AppliesTo applTo = null;
        if (appliesTo != null){
            applTo = WSTrustUtil.createAppliesTo(appliesTo);
            if (stsConfig.getOtherOptions().containsKey("Identity")){
                addServerIdentity(applTo, stsConfig.getOtherOptions().get("Identity"));
            }
        }

        final RequestSecurityToken rst= fact.createRSTForIssue(null,requestType, null,applTo,null,null,null);

        // Handle OnBehalfOf token
        if (oboToken != null){
            OnBehalfOf obo = fact.createOnBehalfOf(oboToken);
            rst.setOnBehalfOf(obo);
        }

        // Handle ActAs token
        Token actAsToken = (Token)stsConfig.getOtherOptions().get(STSIssuedTokenConfiguration.ACT_AS);
        if (actAsToken != null){
            ActAs actAs = fact.createActAs(actAsToken);
            rst.setActAs(actAs);
        }

        // Handle LifeTime requirement
        Integer lf = (Integer)stsConfig.getOtherOptions().get(STSIssuedTokenConfiguration.LIFE_TIME);
        if(lf != null){
            // Create Lifetime
            long lfValue = lf.longValue();
            if (lfValue > 0){
                long currentTime = WSTrustUtil.getCurrentTimeWithOffset();
                Lifetime lifetime = WSTrustUtil.createLifetime(currentTime, lfValue, wstVer);
                rst.setLifetime(lifetime);
            }
        }

        String tokenType = null;
        String keyType = null;
        long keySize = -1;
        String signWith = null;
        String encryptWith = null;
        String signatureAlgorithm = null;
        String encryptionAlgorithm = null;
        String keyWrapAlgorithm = null;
        String canonicalizationAlgorithm = null;
        Claims claims = null;
        if (wstVer.getNamespaceURI().equals(WSTrustVersion.WS_TRUST_13.getNamespaceURI())){
            SecondaryIssuedTokenParameters sitp = stsConfig.getSecondaryIssuedTokenParameters();
            if (sitp != null){
                SecondaryParameters sp = fact.createSecondaryParameters();
                tokenType = sitp.getTokenType();
                if (tokenType != null){
                    sp.setTokenType(URI.create(tokenType));
                }
                keyType = sitp.getKeyType();
                if (keyType != null){
                    sp.setKeyType(URI.create(keyType));
                }
                keySize = sitp.getKeySize();
                if (keySize > 0){
                    sp.setKeySize(keySize);
                }
                /*
                encryptWith = sitp.getEncryptWith();
                if (encryptWith != null){
                sp.setEncryptWith(URI.create(encryptWith));
                }
                signWith = sitp.getSignWith();
                if (signWith != null){
                sp.setSignWith(URI.create(signWith));
                }
                 */
                signatureAlgorithm = sitp.getSignatureAlgorithm();
                if (signatureAlgorithm != null){
                    sp.setSignatureAlgorithm(URI.create(signatureAlgorithm));
                }
                encryptionAlgorithm = sitp.getEncryptionAlgorithm();
                if (encryptionAlgorithm != null){
                    sp.setEncryptionAlgorithm(URI.create(encryptionAlgorithm));
                }

                canonicalizationAlgorithm = sitp.getCanonicalizationAlgorithm();
                if (canonicalizationAlgorithm != null){
                    sp.setCanonicalizationAlgorithm(URI.create(canonicalizationAlgorithm));
                }

                keyWrapAlgorithm = sitp.getKeyWrapAlgorithm();
                if(keyWrapAlgorithm != null){
                    sp.setKeyWrapAlgorithm(URI.create(keyWrapAlgorithm));
                }

                claims = sitp.getClaims();
                if (claims != null){
                    sp.setClaims(claims);
                }
                rst.setSecondaryParameters(sp);
            }
        }

        if (tokenType == null){
            tokenType = stsConfig.getTokenType();
            if (tokenType != null){
                rst.setTokenType(URI.create(tokenType));
            }
        }

        if (keyType == null){
            keyType = stsConfig.getKeyType();
            if (keyType != null){
                rst.setKeyType(URI.create(keyType));
            }
        }

        if (keySize < 1){
            keySize = stsConfig.getKeySize();
            if (keySize > 0){
                rst.setKeySize(keySize);
            }
        }

        /*
        if (encryptWith == null){
        encryptWith = stsConfig.getEncryptWith();
        if (encryptWith != null){
        rst.setEncryptWith(URI.create(encryptWith));
        }
        }

        if (signWith == null){
        signWith = stsConfig.getSignWith();
        if (signWith != null){
        rst.setSignWith(URI.create(signWith));
        }
        }
         */

        if (signatureAlgorithm == null){
            signatureAlgorithm = stsConfig.getSignatureAlgorithm();
            if (signatureAlgorithm != null){
                rst.setSignatureAlgorithm(URI.create(signatureAlgorithm));
            }
        }

        if (encryptionAlgorithm == null){
            encryptionAlgorithm = stsConfig.getEncryptionAlgorithm();
            if (encryptionAlgorithm != null){
                rst.setEncryptionAlgorithm(URI.create(encryptionAlgorithm));
            }
        }

        if (canonicalizationAlgorithm == null){
            canonicalizationAlgorithm = stsConfig.getCanonicalizationAlgorithm();
            if (canonicalizationAlgorithm != null){
                rst.setCanonicalizationAlgorithm(URI.create(canonicalizationAlgorithm));
            }
        }

        if (claims == null){
            claims = stsConfig.getClaims();
            if (claims != null){
                rst.setClaims(fact.createClaims(claims));
            }
        }

        int len = 32;
        if (keySize > 0){
            len = (int)keySize/8;
        }

        if (wstVer.getSymmetricKeyTypeURI().equals(keyType)){
            final SecureRandom secRandom = new SecureRandom();
            final byte[] nonce = new byte[len];
            secRandom.nextBytes(nonce);
            final BinarySecret binarySecret = fact.createBinarySecret(nonce, wstVer.getNonceBinarySecretTypeURI());
            final Entropy entropy = fact.createEntropy(binarySecret);
            rst.setEntropy(entropy);
            rst.setComputedKeyAlgorithm(URI.create(wstVer.getCKPSHA1algorithmURI()));
        }else if (wstVer.getPublicKeyTypeURI().equals(keyType) && keySize > 1 ){
            // Create a RSA key pairs for use with UseKey
            KeyPairGenerator kpg;
            try{
                kpg = KeyPairGenerator.getInstance("RSA");
            //RSAKeyGenParameterSpec rsaSpec = new RSAKeyGenParameterSpec((int)keySize, RSAKeyGenParameterSpec.F0);
            //kpg.initialize(rsaSpec);
            }catch (NoSuchAlgorithmException ex){
                throw new WSTrustException("Unable to create key pairs for UseKey", ex);
            }
            //catch (InvalidAlgorithmParameterException ex){
            //    throw new WSTrustException("Unable to create key pairs for UseKey", ex);
            //}
            kpg.initialize((int)keySize);
            KeyPair keyPair = kpg.generateKeyPair();

            // Create the Sig attribute Value for UseKey
            // String sig = "uuid-" + UUID.randomUUID().toString();

            // Create the UseKey element in RST
            KeyInfo keyInfo = createKeyInfo(keyPair.getPublic());
            final DocumentBuilderFactory docFactory = WSITXMLFactory.createDocumentBuilderFactory(WSITXMLFactory.DISABLE_SECURE_PROCESSING);
            Document doc = null;
            try{
                doc = docFactory.newDocumentBuilder().newDocument();
                keyInfo.marshal(new DOMStructure(doc), null);
            }catch(ParserConfigurationException ex){
                log.log(Level.SEVERE,
                        LogStringsMessages.WST_0039_ERROR_CREATING_DOCFACTORY(), ex);
                throw new WSTrustException(LogStringsMessages.WST_0039_ERROR_CREATING_DOCFACTORY(), ex);
            }catch(MarshalException ex){
                log.log(Level.SEVERE,
                        LogStringsMessages.WST_0039_ERROR_CREATING_DOCFACTORY(), ex);
                throw new WSTrustException(LogStringsMessages.WST_0039_ERROR_CREATING_DOCFACTORY(), ex);
            }
            Token token = new GenericToken(doc.getDocumentElement());
            UseKey useKey = fact.createUseKey(token, null);
            rst.setUseKey(useKey);

            // Put the key pair and the sig in the STSConfiguration
            stsConfig.getOtherOptions().put(WSTrustConstants.USE_KEY_RSA_KEY_PAIR, keyPair);
        //stsConfig.getOtherOptions().put(WSTrustConstants.USE_KEY_SIGNATURE_ID, sig); */
        }

        if(log.isLoggable(Level.FINE)) {
            log.log(Level.FINE,
                    LogStringsMessages.WST_1006_CREATED_RST_ISSUE(WSTrustUtil.elemToString(rst, wstVer)));
        }

        return rst;
    }

    private RequestSecurityToken createRequestForValidatation(final STSIssuedTokenConfiguration stsConfig, final Token token){
        WSTrustVersion wstVer = WSTrustVersion.getInstance(stsConfig.getProtocol());
        WSTrustElementFactory fact = WSTrustElementFactory.newInstance(wstVer);
        final URI requestType = URI.create(wstVer.getValidateRequestTypeURI());
        final URI tokenType = URI.create(wstVer.getValidateStatuesTokenType());
        final RequestSecurityToken rst= fact.createRSTForValidate(tokenType, requestType);
        if (wstVer.getNamespaceURI().equals(WSTrustVersion.WS_TRUST_10_NS_URI)){
            rst.getAny().add(token.getTokenValue());
        }else {
            ValidateTarget vt = fact.createValidateTarget(token);
            rst.setValidateTarget(vt);
        }

        return rst;
    }

    @SuppressWarnings("unchecked")
    private BaseSTSResponse invokeRST(final RequestSecurityToken request, STSIssuedTokenConfiguration stsConfig) throws WSTrustException {

        String stsURI = stsConfig.getSTSEndpoint();
        STSIssuedTokenConfiguration rtConfig = (STSIssuedTokenConfiguration)stsConfig.getOtherOptions().get("RunTimeConfig");
        Dispatch<Message> dispatch = null;
        WSTrustVersion wstVer = WSTrustVersion.getInstance(stsConfig.getProtocol());
        WSTrustElementFactory fact = WSTrustElementFactory.newInstance(wstVer);
        if (rtConfig != null){
            dispatch = (Dispatch<Message>)rtConfig.getOtherOptions().get(stsURI);
        }else{
            dispatch = (Dispatch<Message>)stsConfig.getOtherOptions().get(stsURI);
        }

        if (dispatch == null){
            URI wsdlLocation = null;
            QName serviceName = null;
            QName portName = null;

            final String metadataStr = stsConfig.getSTSMEXAddress();
            if (metadataStr != null){
                wsdlLocation = URI.create(metadataStr);
            }else{
                final String namespace = stsConfig.getSTSNamespace();
                String wsdlLocationStr = stsConfig.getSTSWSDLLocation();
                if (wsdlLocationStr == null){
                    wsdlLocationStr = stsURI;
                }else{
                    final String serviceNameStr = stsConfig.getSTSServiceName();
                    if (serviceNameStr != null && namespace != null){
                          serviceName = new QName(namespace,serviceNameStr);
                    }

                    final String portNameStr = stsConfig.getSTSPortName();
                    if (portNameStr != null && namespace != null){
                          portName = new QName(namespace, portNameStr);
                    }
                }
                wsdlLocation = URI.create(wsdlLocationStr);
            }

            //WSTrustVersion wstVer = WSTrustVersion.getInstance(stsConfig.getProtocol());
            //WSTrustElementFactory fact = WSTrustElementFactory.newInstance(wstVer);
            if(serviceName == null || portName==null){
                //we have to get the serviceName and portName through MEX
                if (log.isLoggable(Level.FINE)) {
                    log.log(Level.FINE,
                            LogStringsMessages.WST_1012_SERVICE_PORTNAME_MEX(serviceName, portName));
                }

                final QName[] names = doMexRequest(wsdlLocation.toString(), stsURI);
                serviceName = names[0];
                portName = names[1];
            }

            Service service = null;
            try{
                // Work around for issue 338
                String url = wsdlLocation.toString();
                // if (url.endsWith("/mex")){
                //   int index = url.lastIndexOf("/mex");
                //  url = url.substring(0, index);
                //}

                /* Fix of JCAPS Issue 866 (Fix is : use the container got from JCAPS
                 * through JAX-WS and pass that into the client for the STS )
                 */
                Container container = (Container) stsConfig.getOtherOptions().get("CONTAINER");
                if(container != null){
                    InitParams initParams = new InitParams();
                    initParams.setContainer(container);
                    service = WSService.create(new URL(url), serviceName, initParams);
                }else{
                    service = Service.create(new URL(url), serviceName);
                }
            }catch (MalformedURLException ex){
                log.log(Level.SEVERE,
                        LogStringsMessages.WST_0041_SERVICE_NOT_CREATED(wsdlLocation.toString()), ex);
                throw new WebServiceException(LogStringsMessages.WST_0041_SERVICE_NOT_CREATED(wsdlLocation.toString()), ex);
            }
           //final Dispatch<Object> dispatch = service.createDispatch(portName, WSTrustElementFactory.getContext(wstVer), Service.Mode.PAYLOAD, new WebServiceFeature[]{new RespectBindingFeature(), new AddressingFeature(false)});
            WebServiceFeature[] wsFeatures = null;
           //STSIssuedTokenConfiguration rtConfig = (STSIssuedTokenConfiguration)stsConfig.getOtherOptions().get("RunTimeConfig");
            if (rtConfig != null){
                wsFeatures = new WebServiceFeature[]{new RespectBindingFeature(),
                                                     new AddressingFeature(false),
                                                     new STSIssuedTokenFeature(rtConfig)};
            }else{
                wsFeatures = new WebServiceFeature[]{new RespectBindingFeature(), new AddressingFeature(false)};
            }
            dispatch = service.createDispatch(portName, Message.class, Service.Mode.MESSAGE, wsFeatures);
            if (rtConfig != null){
                rtConfig.getOtherOptions().put(stsURI, dispatch);
            }else{
                stsConfig.getOtherOptions().put(stsURI, dispatch);
            }
        }
        //Dispatch<SOAPMessage> dispatch = service.createDispatch(portName, SOAPMessage.class, Service.Mode.MESSAGE, new WebServiceFeature[]{new AddressingFeature(false)});
        //WSBinding wsbinding = (WSBinding) dispatch.getBinding();
        //AddressingVersion addVer = wsbinding.getAddressingVersion();
        //SOAPVersion sv = wsbinding.getSOAPVersion();

        //dispatch = addAddressingHeaders(dispatch);
        if (stsURI != null){
            dispatch.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, stsURI);
        }
        dispatch.getRequestContext().put(WSTrustConstants.IS_TRUST_MESSAGE, "true");
        dispatch.getRequestContext().put(WSTrustConstants.TRUST_ACTION, getAction(wstVer, request.getRequestType().toString()));

        // Pass the keys and/or username, password to the message context
//        String userName = (String) stsConfig.getOtherOptions().get(com.sun.xml.wss.XWSSConstants.USERNAME_PROPERTY);
//        String password = (String) stsConfig.getOtherOptions().get(com.sun.xml.wss.XWSSConstants.PASSWORD_PROPERTY);
//        if (userName != null){
//            dispatch.getRequestContext().put(com.sun.xml.wss.XWSSConstants.USERNAME_PROPERTY, userName);
//        }
//        if (password != null){
//            dispatch.getRequestContext().put(com.sun.xml.wss.XWSSConstants.PASSWORD_PROPERTY, password);
//        }

//        KeyPair keyPair = (KeyPair)stsConfig.getOtherOptions().get(WSTrustConstants.USE_KEY_RSA_KEY_PAIR);
//        String id = (String)stsConfig.getOtherOptions().get(WSTrustConstants.USE_KEY_SIGNATURE_ID);
//        if (keyPair != null){
//            dispatch.getRequestContext().put(WSTrustConstants.USE_KEY_RSA_KEY_PAIR, keyPair);
//        }
//        if (id != null){
//            dispatch.getRequestContext().put(WSTrustConstants.USE_KEY_SIGNATURE_ID, id);
//        }

        setMessageProperties(dispatch.getRequestContext(), stsConfig);

        //RequestSecurityTokenResponse rstr = null;
        // try{
        //  MessageFactory factory = sv.saajMessageFactory;
        //  SOAPMessage message = factory.createMessage();
        //   message.getSOAPBody().addDocument(fact.toElement(request).getOwnerDocument());
        //   SOAPHeader header = message.getSOAPHeader();
        // SOAPHeaderElement action = header.addHeaderElement(addVer.actionTag);
        // action.addTextNode(WSTrustConstants.REQUEST_SECURITY_TOKEN_ISSUE_ACTION);
        // SOAPHeaderElement to = header.addHeaderElement(addVer.toTag);
        // to.addTextNode(stsURI.toString());
        // SOAPHeaderElement msgID = header.addHeaderElement(addVer.messageIDTag);
        // msgID.addTextNode("uuid:" + UUID.randomUUID().toString());
        // SOAPHeaderElement replyTo = header.addHeaderElement(addVer.replyToTag);
        // SOAPElement add = replyTo.addChildElement(new QName(addVer.nsUri, "Address"));
        // add.addTextNode(AddressingVersion.W3C.getAnonymousUri());
        //SOAPMessage response = (SOAPMessage)dispatch.invoke(message);
        // SOAPBody rsp = response.getSOAPBody();
        // Element rspEle = rsp.extractContentAsDocument().getDocumentElement();
        // rstr = fact.createRSTRFrom(rspEle);
        // } catch(Exception ex){
        // ex.printStackTrace();
        // }

        Message reqMsg = Messages.createUsingPayload(fact.toSource(request), ((WSBinding)dispatch.getBinding()).getSOAPVersion());
        Message respMsg = dispatch.invoke(reqMsg);
        Source respSrc = respMsg.readPayloadAsSource();
        final BaseSTSResponse resp = parseRSTR(respSrc, wstVer);
        //if(wstVer.getNamespaceURI().equals(WSTrustVersion.WS_TRUST_13.getNamespaceURI())){
          //  rstr = fact.createRSTRCollectionFrom(((JAXBElement)dispatch.invoke(fact.toJAXBElement(request))));
        //}else{
          //  rstr = fact.createRSTRFrom((JAXBElement)dispatch.invoke(fact.toJAXBElement(request)));
        //}

        if(log.isLoggable(Level.FINE)) {
            log.log(Level.FINE,
                    LogStringsMessages.WST_1007_CREATED_RSTR_ISSUE(WSTrustUtil.elemToString(resp, wstVer)));
        }

        return resp;
    }

    /**
     * This method uses mex client api to issue a mex request and return the
     * matching service name and port name
     * @param stsURI URI to the STS. Mex request will be issued to this address
     * @return List of 2 QName objects. The first one will be serviceName
     * and the second one will be portName.
     */
    protected static QName[]  doMexRequest(final String wsdlLocation, final String stsURI) throws WSTrustException {

        final QName[] serviceInfo = new QName[2];
        final MetadataClient mexClient = new MetadataClient();

        final Metadata metadata = mexClient.retrieveMetadata(wsdlLocation);

        //this method gives the names of services and the corresponding port details
        if(metadata != null){
            final List<PortInfo> ports = mexClient.getServiceInformation(metadata);

            //we have to iterate through this to get the appropriate serviceName and portname
            for(PortInfo port : ports){
                final String uri = port.getAddress();

                //if the stsAddress what we have matches the address of this port, return
                //this port information
                 if(URI.create(uri).getPath().equals(URI.create(stsURI).getPath())){
                    serviceInfo[0]= port.getServiceName();
                    serviceInfo[1]= port.getPortName();
                    break;
                }

            }

            if(serviceInfo[0]==null || serviceInfo[1]==null){
                log.log(Level.SEVERE,
                        LogStringsMessages.WST_0042_NO_MATCHING_SERVICE_MEX(stsURI));
                throw new WSTrustException(
                        LogStringsMessages.WST_0042_NO_MATCHING_SERVICE_MEX(stsURI));
            }
        }else{
            log.log(Level.SEVERE,
                    LogStringsMessages.WST_0017_SERVICE_PORTNAME_ERROR(wsdlLocation));
            throw new WSTrustException(
                    LogStringsMessages.WST_0017_SERVICE_PORTNAME_ERROR(wsdlLocation));
        }

        return serviceInfo;
    }

    private KeyInfo createKeyInfo(final PublicKey pubKey)throws WSTrustException{
        KeyInfoFactory kif = WSSPolicyConsumerImpl.getInstance().getKeyInfoFactory();
        KeyValue kv = null;
        try{
            kv = kif.newKeyValue(pubKey);
        }catch (KeyException ex){
            throw new WSTrustException("Unable to create key value", ex);
        }
        List<KeyValue> kvs = new ArrayList<>();
        kvs.add(kv);
        return kif.newKeyInfo(kvs);
    }

    private String getAction(WSTrustVersion wstVer, String requestType){
        if (wstVer.getIssueRequestTypeURI().equals(requestType)){
            return wstVer.getIssueRequestAction();
        }
        if (wstVer.getValidateRequestTypeURI().equals(requestType)){
            return wstVer.getValidateRequestAction();
        }
        if (wstVer.getRenewRequestTypeURI().equals(requestType)){
            return wstVer.getRenewRequestAction();
        }
        if (wstVer.getCancelRequestTypeURI().equals(requestType)){
            return wstVer.getCancelRequestAction();
        }

        return wstVer.getIssueRequestAction();
    }

    private BaseSTSResponse parseRSTR(Source source, WSTrustVersion wstVer) throws WSTrustException{
        Element ele = null;
        try{
            DOMResult result = new DOMResult();
            Transformer tf = WSITXMLFactory.createTransformerFactory(WSITXMLFactory.DISABLE_SECURE_PROCESSING).newTransformer();
            tf.transform(source, result);

            Node node = result.getNode();
            if (node instanceof Document){
                ele = ((Document)node).getDocumentElement();
            } else if (node instanceof Element){
                ele = (Element)node;
            }
        }catch(Exception xe){
            throw new WSTrustException("Error occurred while trying to parse RSTR stream", xe);
        }

        RequestedSecurityToken rdst = null;
        WSTrustElementFactory fact = WSTrustElementFactory.newInstance(wstVer);
        NodeList list = ele.getElementsByTagNameNS(ele.getNamespaceURI(), "RequestedSecurityToken");
        if (list.getLength() > 0){
            Element issuedToken = (Element)list.item(0).getChildNodes().item(0);
            GenericToken token = new GenericToken(issuedToken);
            rdst = fact.createRequestedSecurityToken(token);
        }
        BaseSTSResponse rstr;
        if(wstVer.getNamespaceURI().equals(WSTrustVersion.WS_TRUST_13.getNamespaceURI())){
            rstr = fact.createRSTRCollectionFrom(ele);
            ((RequestSecurityTokenResponseCollection)rstr).getRequestSecurityTokenResponses().get(0).setRequestedSecurityToken(rdst);
        }else{
            rstr = fact.createRSTRFrom(ele);
            ((RequestSecurityTokenResponse)rstr).setRequestedSecurityToken(rdst);
        }
        return rstr;
    }

    private void setMessageProperties(Map<String, Object> context, STSIssuedTokenConfiguration stsConfig){
        context.putAll(stsConfig.getOtherOptions());
        if (context.containsKey(Constants.SC_ASSERTION)){
            context.remove(Constants.SC_ASSERTION);
        }
    }

    private void addServerIdentity(AppliesTo aplTo, Object identity) throws WSTrustException {
        if (identity instanceof Element){
            aplTo.getAny().add(identity);
        }else if (identity instanceof X509Certificate){
            // Create Identity element with a BinarySecurityTOken for
            // the server certificate

            // Create BinarySecurityToken
            String id = UUID.randomUUID().toString();
            BinarySecurityTokenType bst = new BinarySecurityTokenType();
            bst.setValueType(MessageConstants.X509v3_NS);
            bst.setId(id);
            bst.setEncodingType(MessageConstants.BASE64_ENCODING_NS);
            try {
                bst.setValue(((X509Certificate)identity).getEncoded());
            }catch (CertificateEncodingException ex){
                throw new WSTrustException(ex.getMessage());
            }
            JAXBElement<BinarySecurityTokenType> bstElem = new com.sun.xml.ws.security.secext10.ObjectFactory().createBinarySecurityToken(bst);

            // Cretae Identity element
            IdentityType idElem = new IdentityType();
            idElem.getDnsOrSpnOrUpn().add(bstElem);
            aplTo.getAny().add(new com.sun.xml.security.core.ai.ObjectFactory().createIdentity(idElem));
        }
    }
}
