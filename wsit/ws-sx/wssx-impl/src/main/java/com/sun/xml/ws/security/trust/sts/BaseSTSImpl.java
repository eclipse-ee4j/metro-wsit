/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.trust.sts;

import com.sun.xml.ws.api.security.trust.BaseSTS;
import com.sun.xml.ws.api.security.trust.WSTrustContract;
import com.sun.xml.ws.api.security.trust.WSTrustException;
import com.sun.xml.ws.api.security.trust.config.STSConfiguration;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.security.IssuedTokenContext;
import com.sun.xml.ws.security.impl.IssuedTokenContextImpl;
import com.sun.xml.ws.security.impl.policy.Constants;
import com.sun.xml.ws.security.trust.WSTrustVersion;
import com.sun.xml.ws.security.trust.WSTrustConstants;
import com.sun.xml.ws.security.trust.WSTrustElementFactory;
import com.sun.xml.ws.security.trust.WSTrustFactory;
import com.sun.xml.ws.security.trust.elements.BaseSTSRequest;
import com.sun.xml.ws.security.trust.elements.BaseSTSResponse;
import com.sun.xml.ws.security.trust.elements.RequestSecurityToken;
import com.sun.xml.ws.security.trust.impl.DefaultSTSConfiguration;
import com.sun.xml.ws.security.trust.impl.DefaultTrustSPMetadata;
import com.sun.xml.ws.security.trust.util.WSTrustUtil;
import com.sun.xml.wss.SecurityEnvironment;
import com.sun.xml.wss.SubjectAccessor;
import com.sun.xml.wss.XWSSecurityException;

import java.util.Iterator;
import javax.xml.namespace.QName;

import javax.xml.transform.TransformerException;

import jakarta.xml.ws.WebServiceException;
import javax.xml.transform.Source;
import jakarta.xml.ws.handler.MessageContext;
//import jakarta.xml.ws.BindingType;
//import jakarta.xml.ws.RespectBinding;

import com.sun.xml.ws.policy.impl.bindings.AppliesTo;
import com.sun.xml.wss.WSITXMLFactory;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * The Base class of an STS implementation. This could be used to implement
 * the actual STS. The sub class could override the methods of this class to
 * customize the implementation.
 */
//@RespectBinding
//@BindingType
public abstract class BaseSTSImpl implements BaseSTS {
    /**
     * The default value of the timeout for the tokens issued by this STS
     */
    public static final int DEFAULT_TIMEOUT = 36000;
    
    public static final String DEFAULT_ISSUER = "SampleSunSTS";
    /**
     * The xml element tag for STS Configuration
     */
    public static final String STS_CONFIGURATION = "STSConfiguration";
    /**
     * The default implementation class for the Trust contract. This
     * class issues SAML tokens.
     */
    public static final String DEFAULT_IMPL = 
            "com.sun.xml.ws.security.trust.impl.IssueSamlTokenContractImpl";
    /**
     * The default value for AppliesTo if appliesTo is not specified.
     */
    public static final String DEFAULT_APPLIESTO = "default";
    /**
     * The String AppliesTo
     */
    public static final String APPLIES_TO = "AppliesTo";
    /**
     * The String LifeTime that is used to specify lifetime of the tokens 
     * issued by this STS.
     */
    public static final String LIFETIME = "LifeTime";
    /**
     * The String CertAlias that is used in the configuration.
     * This identifies the alias of the Service that this STS serves.
     */
    public static final String ALIAS = "CertAlias";
    /**
     * The String encrypt-issued-key
     */
    public static final String ENCRYPT_KEY = "encryptIssuedKey";
    /**
     * The String encrypt-issued-token
     */
    public static final String ENCRYPT_TOKEN = "encryptIssuedToken";
    /**
     * The String Contract.
     */
    public static final String CONTRACT = "Contract";
    
    public static final String ISSUER = "Issuer";
    /**
     * The String TokenType.
     */
    public static final String TOKEN_TYPE = "TokenType";
    
    /**
     * The String KeyType.
     */
    public static final String KEY_TYPE = "KeyType";
    
    /**
     * The String ServiceProviders.
     */
    public static final String SERVICE_PROVIDERS = "ServiceProviders";
    /**
     * The String endPoint.
     */
    public static final String END_POINT = "endPoint";
    
    private static final QName Q_EK = new QName("",ENCRYPT_KEY);
    
    private static final QName Q_ET = new QName("",ENCRYPT_TOKEN);
    
    private static final QName Q_EP = new QName("",END_POINT);

    protected WSTrustVersion wstVer = WSTrustVersion.WS_TRUST_10;


  /** Implementation of the invoke method of the Provider interface
   *
   *  @param  rstElement The message comprising of RequestSecurityToken.
   *  @return The response message comprising of RequestSecurityTokenResponse
   *  @throws WebServiceException if there is an error processing request.
   *          The cause of the WebServiceException may be set to a subclass
   *          of ProtocolException to control the protocol level
   *          representation of the exception.
  **/
    @Override
    public Source invoke(final Source rstElement){
        final STSConfiguration config = getConfiguration();
        Source rstrEle = null;
        try{
            // Get RequestSecurityToken
            final WSTrustElementFactory eleFac = WSTrustElementFactory.newInstance(wstVer);
            final RequestSecurityToken rst = parseRST(rstElement, config);
            
            String appliesTo = null;
            final AppliesTo applTo = rst.getAppliesTo();
            if(applTo != null){
                appliesTo = WSTrustUtil.getAppliesToURI(applTo);
            }
            
            if (appliesTo == null){
                appliesTo = DEFAULT_APPLIESTO;
            }
            
            if(rst.getRequestType().toString().equals(wstVer.getIssueRequestTypeURI())){
                rstrEle = issue(config, appliesTo, eleFac, rst);                
            }else if(rst.getRequestType().toString().equals(wstVer.getCancelRequestTypeURI())){
                rstrEle = cancel(config, appliesTo, eleFac, rst);
            }else if(rst.getRequestType().toString().equals(wstVer.getRenewRequestTypeURI())){
                rstrEle = renew(config, appliesTo, eleFac, rst);
            }else if(rst.getRequestType().toString().equals(wstVer.getValidateRequestTypeURI())){
                rstrEle = validate(config, appliesTo, eleFac, rst);
            }            
        } catch (Exception ex){
            //ex.printStackTrace();
            throw new WebServiceException(ex);
        }
        
        return rstrEle;
    }
    
     /** The actual STS class should override this method to return the 
     *  correct MessageContext
     * 
     * @return The MessageContext
     */
    protected abstract MessageContext getMessageContext();

    STSConfiguration getConfiguration() {
        final MessageContext msgCtx = getMessageContext();
        //final CallbackHandler handler = (CallbackHandler)msgCtx.get(WSTrustConstants.STS_CALL_BACK_HANDLER);
        final SecurityEnvironment secEnv = (SecurityEnvironment)msgCtx.get(WSTrustConstants.SECURITY_ENVIRONMENT);
        WSTrustVersion wstVersion = (WSTrustVersion)msgCtx.get(WSTrustConstants.WST_VERSION);
        String authnCtxClass = (String)msgCtx.get(WSTrustConstants.AUTHN_CONTEXT_CLASS);
        if (wstVersion != null){
            wstVer = wstVersion;
        }
        //Get Runtime STSConfiguration
        STSConfiguration rtConfig = WSTrustFactory.getRuntimeSTSConfiguration();
        if (rtConfig != null){
            if (rtConfig.getCallbackHandler() == null){
                rtConfig.getOtherOptions().put(WSTrustConstants.SECURITY_ENVIRONMENT, secEnv);
            }
            if (wstVersion == null){
                wstVersion = (WSTrustVersion)rtConfig.getOtherOptions().get(WSTrustConstants.WST_VERSION);
                if (wstVersion != null){
                    wstVer = wstVersion;
                }
            }
           
            rtConfig.getOtherOptions().put(WSTrustConstants.WST_VERSION, wstVer);
            
            return rtConfig;
        }
        
        // Get default STSConfiguration
        DefaultSTSConfiguration config = new DefaultSTSConfiguration();
        config.getOtherOptions().put(WSTrustConstants.SECURITY_ENVIRONMENT, secEnv);
        //config.setCallbackHandler(handler);
        final Iterator iterator = (Iterator)msgCtx.get(
                Constants.SUN_TRUST_SERVER_SECURITY_POLICY_NS);
        if (iterator == null){
            throw new WebServiceException("STS configuration information is not available");
        }
        
        while(iterator.hasNext()) {
            final PolicyAssertion assertion = (PolicyAssertion)iterator.next();
            if (!STS_CONFIGURATION.equals(assertion.getName().getLocalPart())) {
                continue;
            }
            config.setEncryptIssuedToken(Boolean.parseBoolean(assertion.getAttributeValue(Q_ET)));
            config.setEncryptIssuedKey(Boolean.parseBoolean(assertion.getAttributeValue(Q_EK)));
            final Iterator<PolicyAssertion> stsConfig =
                    assertion.getNestedAssertionsIterator();
            while(stsConfig.hasNext()){
                final PolicyAssertion serviceSTSPolicy = stsConfig.next();
                if(LIFETIME.equals(serviceSTSPolicy.getName().getLocalPart())){
                    config.setIssuedTokenTimeout(Integer.parseInt(serviceSTSPolicy.getValue()));
                    
                    continue;
                }
                if(CONTRACT.equals(serviceSTSPolicy.getName().getLocalPart())){
                    config.setType(serviceSTSPolicy.getValue());
                    continue;
                }
                if(ISSUER.equals(serviceSTSPolicy.getName().getLocalPart())){
                    config.setIssuer(serviceSTSPolicy.getValue());
                    continue;
                }
                
                if(SERVICE_PROVIDERS.equals(serviceSTSPolicy.getName().getLocalPart())){
                    final Iterator<PolicyAssertion> serviceProviders =
                    serviceSTSPolicy.getNestedAssertionsIterator();
                    String endpointUri = null;
                    while(serviceProviders.hasNext()){
                        final PolicyAssertion serviceProvider = serviceProviders.next();
                        endpointUri = serviceProvider.getAttributeValue(Q_EP);
                        if (endpointUri == null){
                             endpointUri = serviceProvider.getAttributeValue(new QName("", END_POINT.toLowerCase()));
                        }
                        final DefaultTrustSPMetadata data = new DefaultTrustSPMetadata(endpointUri);
                        final Iterator<PolicyAssertion> spConfig = serviceProvider.getNestedAssertionsIterator();
                        while(spConfig.hasNext()){
                            final PolicyAssertion policy = spConfig.next();
                            if(ALIAS.equals(policy.getName().getLocalPart())){
                                data.setCertAlias(policy.getValue());
                            }else if (TOKEN_TYPE.equals(policy.getName().getLocalPart())){
                                data.setTokenType(policy.getValue());
                            }else if (KEY_TYPE.equals(policy.getName().getLocalPart())){
                                data.setKeyType(policy.getValue());
                            }
                        }
                        
                        config.addTrustSPMetadata(data, endpointUri);
                    }
                }
            }
        }
        config.getOtherOptions().put(WSTrustConstants.WST_VERSION, wstVer);
        
        if(authnCtxClass != null){
            config.getOtherOptions().put(WSTrustConstants.AUTHN_CONTEXT_CLASS, authnCtxClass);
        }
        config.getOtherOptions().putAll(msgCtx);
      
        return config;
    }

    private Source issue(final STSConfiguration config, final String appliesTo, 
            final WSTrustElementFactory eleFac, final BaseSTSRequest rst) 
            throws WSTrustException {
        
        // Create the RequestSecurityTokenResponse message
        final WSTrustContract<BaseSTSRequest, BaseSTSResponse> contract = WSTrustFactory.newWSTrustContract(config, 
                appliesTo);
        final IssuedTokenContext context = new IssuedTokenContextImpl();
        try {
            context.setRequestorSubject(SubjectAccessor.getRequesterSubject(getMessageContext()));            
        } catch (XWSSecurityException ex) {
            throw new WSTrustException("error getting subject",ex);
        }

        final BaseSTSResponse response = contract.issue(rst, context);
        
        return eleFac.toSource(response);
    }

    private Source cancel(final STSConfiguration config,
            final String appliesTo, final WSTrustElementFactory eleFac,
            final BaseSTSRequest rst) {
        return null;
    }
    
    private Source renew(final STSConfiguration config,final String appliesTo, 
            final WSTrustElementFactory eleFac, final RequestSecurityToken rst) 
            throws WSTrustException {
        Source rstrEle;

        // Create the RequestSecurityTokenResponse message
        final WSTrustContract<BaseSTSRequest, BaseSTSResponse> contract = WSTrustFactory.newWSTrustContract(config, 
                appliesTo);
        final IssuedTokenContext context = new IssuedTokenContextImpl();
        
        final BaseSTSResponse rstr = contract.renew(rst, context);

        rstrEle = eleFac.toSource(rstr);
        return rstrEle;
    }
    
    private Source validate(final STSConfiguration config,final String appliesTo, 
            final WSTrustElementFactory eleFac, final BaseSTSRequest rst) 
            throws WSTrustException {
        Source rstrEle;

        // Create the RequestSecurityTokenResponse message
        final WSTrustContract<BaseSTSRequest, BaseSTSResponse> contract = WSTrustFactory.newWSTrustContract(config, 
                appliesTo);
        final IssuedTokenContext context = new IssuedTokenContextImpl();
        
        final BaseSTSResponse rstr = contract.validate(rst, context);

        rstrEle = eleFac.toSource(rstr);
        return rstrEle;
    }

    private RequestSecurityToken parseRST(Source source, STSConfiguration config) throws WSTrustException{
        Element ele = null;
        try{
            DOMResult result = new DOMResult();
            TransformerFactory tfactory = WSITXMLFactory.createTransformerFactory(WSITXMLFactory.DISABLE_SECURE_PROCESSING);
            Transformer tf = tfactory.newTransformer();
            tf.transform(source, result);

            Node node = result.getNode();
            if (node instanceof Document){
                ele = ((Document)node).getDocumentElement();
            } else if (node instanceof Element){
                ele = (Element)node;
            }
        }catch(Exception xe){
            throw new WSTrustException("Error occurred while trying to parse RST stream", xe);
        }
        WSTrustElementFactory fact = WSTrustElementFactory.newInstance(wstVer);
        RequestSecurityToken rst = fact.createRSTFrom(ele);

        // handling SAML assertion in RST; assume there is one one
        // in it fro OnBehalfOf, ActAs or ValidateTarget.
        NodeList list = ele.getElementsByTagNameNS("*", "Assertion");
        if (list.getLength() > 0){
            Element assertion = (Element)list.item(0);
            config.getOtherOptions().put(WSTrustConstants.SAML_ASSERTION_ELEMENT_IN_RST, assertion);
        }

        return rst;
    }
}
