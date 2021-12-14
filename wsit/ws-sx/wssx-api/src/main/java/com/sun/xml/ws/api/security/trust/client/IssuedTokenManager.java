/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.api.security.trust.client;

import com.sun.xml.ws.api.security.secconv.client.SCTokenConfiguration;
import com.sun.xml.ws.security.IssuedTokenContext;
import com.sun.xml.ws.security.impl.IssuedTokenContextImpl;

import com.sun.xml.ws.api.security.trust.WSTrustException;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Jiandong Guo
 */
public class IssuedTokenManager {
    private final Map<String, IssuedTokenProvider> itpMap = new HashMap<>();
    private final Map<String, String> itpClassMap = new HashMap<>();
    private static IssuedTokenManager manager = new IssuedTokenManager();
    
    /** Creates a new instance of IssuedTokenManager */
    private IssuedTokenManager() {
        addDefaultProviders();
    }
    
    public static IssuedTokenManager getInstance(){
        synchronized (IssuedTokenManager.class) {
             return manager;
         }
    }
    
    public IssuedTokenContext createIssuedTokenContext(IssuedTokenConfiguration config, String appliesTo){
        IssuedTokenContext ctx = new IssuedTokenContextImpl();
        ctx.getSecurityPolicy().add(config);
        ctx.setEndpointAddress(appliesTo);
        
        return ctx;
    }
    
    public void getIssuedToken(IssuedTokenContext ctx)throws WSTrustException {
        IssuedTokenConfiguration config = (IssuedTokenConfiguration)ctx.getSecurityPolicy().get(0);
        IssuedTokenProvider provider = getIssuedTokenProvider(config.getProtocol());
        provider.issue(ctx);
    }
    
    public void renewIssuedToken(IssuedTokenContext ctx)throws WSTrustException {
        IssuedTokenConfiguration config = (IssuedTokenConfiguration)ctx.getSecurityPolicy().get(0);
        IssuedTokenProvider provider = getIssuedTokenProvider(config.getProtocol());
        provider.renew(ctx);
    }
    
    public void cancelIssuedToken(IssuedTokenContext ctx)throws WSTrustException {
        IssuedTokenConfiguration config = (IssuedTokenConfiguration)ctx.getSecurityPolicy().get(0);
        IssuedTokenProvider provider = getIssuedTokenProvider(config.getProtocol());
        provider.cancel(ctx);
    }
    
    public void validateIssuedToken(IssuedTokenContext ctx)throws WSTrustException {
        IssuedTokenConfiguration config = (IssuedTokenConfiguration)ctx.getSecurityPolicy().get(0);
        IssuedTokenProvider provider = getIssuedTokenProvider(config.getProtocol());
        provider.validate(ctx);
    }
    
    private void addDefaultProviders(){
        itpClassMap.put(STSIssuedTokenConfiguration.PROTOCOL_10, "com.sun.xml.ws.security.trust.impl.client.STSIssuedTokenProviderImpl");
        itpClassMap.put(STSIssuedTokenConfiguration.PROTOCOL_13, "com.sun.xml.ws.security.trust.impl.client.STSIssuedTokenProviderImpl");
        itpClassMap.put(SCTokenConfiguration.PROTOCOL_10, "com.sun.xml.ws.security.secconv.impl.client.SCTokenProviderImpl");
        itpClassMap.put(SCTokenConfiguration.PROTOCOL_13, "com.sun.xml.ws.security.secconv.impl.client.SCTokenProviderImpl");
    }

    private IssuedTokenProvider getIssuedTokenProvider(String protocol) throws WSTrustException {
        IssuedTokenProvider itp = null;
        synchronized (itpMap){
            itp = itpMap.get(protocol);
            if (itp == null){
                String type = itpClassMap.get(protocol);
                if (type != null){
                    try {
                        Class<?> clazz = null;
                        final ClassLoader loader = Thread.currentThread().getContextClassLoader();

                        if (loader == null) {
                            clazz = Class.forName(type);
                        } else {
                            clazz = loader.loadClass(type);
                        }

                        if (clazz != null) {
                            @SuppressWarnings("unchecked")
                            Class<IssuedTokenProvider> typedClass = (Class<IssuedTokenProvider>)clazz;
                            itp = typedClass.newInstance();
                            itpMap.put(protocol, itp);
                        }
                    } catch (Exception e) {
                        throw new WSTrustException("IssueTokenProvider for the protocol: "+protocol+ "is not supported", e);
                    }
                }else{
                    throw new WSTrustException("IssueTokenProvider for the protocol: "+protocol+ "is not supported");
                }
            }
        }
        
        return itp;
    }
}
