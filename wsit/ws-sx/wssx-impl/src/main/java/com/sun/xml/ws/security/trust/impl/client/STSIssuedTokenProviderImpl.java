/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.trust.impl.client;

import com.sun.xml.ws.security.IssuedTokenContext;
import com.sun.xml.ws.security.trust.TrustPlugin;
import com.sun.xml.ws.security.trust.WSTrustFactory;
import com.sun.xml.ws.api.security.trust.WSTrustException;
import com.sun.xml.ws.api.security.trust.client.IssuedTokenProvider;
import com.sun.xml.ws.api.security.trust.client.STSIssuedTokenConfiguration;
import com.sun.xml.wss.SubjectAccessor;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Set;
import javax.security.auth.Subject;

import com.sun.xml.ws.security.trust.logging.LogDomainConstants;
import com.sun.xml.ws.security.trust.logging.LogStringsMessages;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jiandong Guo
 */
public class STSIssuedTokenProviderImpl implements IssuedTokenProvider {

     private static final Logger log =
            Logger.getLogger(
            LogDomainConstants.TRUST_IMPL_DOMAIN,
            LogDomainConstants.TRUST_IMPL_DOMAIN_BUNDLE);
    
    @Override
    public void issue(IssuedTokenContext ctx)throws WSTrustException{
        getIssuedTokenContext(ctx);
    } 
    
    @Override
    public void cancel(IssuedTokenContext ctx) {
        
    }
    
    @Override
    public void renew(IssuedTokenContext ctx) {
        
    }
    
    @Override
    public void validate(IssuedTokenContext ctx)throws WSTrustException{
        TrustPlugin tp = WSTrustFactory.newTrustPlugin();
        tp.processValidate(ctx);
    }

    private void updateContext(IssuedTokenContext cached, IssuedTokenContext ctx) {
        ctx.setUnAttachedSecurityTokenReference(cached.getUnAttachedSecurityTokenReference());
        ctx.setSecurityToken(cached.getSecurityToken());
        ctx.setRequestorCertificate(cached.getRequestorCertificate());
        ctx.setProofKeyPair(cached.getProofKeyPair());
        ctx.setProofKey(cached.getProofKey());
        ctx.setExpirationTime(cached.getExpirationTime());
        ctx.setCreationTime(cached.getCreationTime());
        ctx.setAttachedSecurityTokenReference(cached.getAttachedSecurityTokenReference());
    }

    private void getIssuedTokenContext(IssuedTokenContext ctx)throws WSTrustException {
        STSIssuedTokenConfiguration config = (STSIssuedTokenConfiguration)ctx.getSecurityPolicy().get(0);
        ctx.setTokenIssuer(config.getSTSEndpoint());
        boolean shareToken = "true".equals(config.getOtherOptions().get(STSIssuedTokenConfiguration.SHARE_TOKEN));
        boolean renewExpiredToken = "true".equals(config.getOtherOptions().get(STSIssuedTokenConfiguration.RENEW_EXPIRED_TOKEN));
        String maxClockSkew = (String)config.getOtherOptions().get(STSIssuedTokenConfiguration.MAX_CLOCK_SKEW);
        Subject subject = SubjectAccessor.getRequesterSubject();
        if (shareToken && subject != null){
            Set pcs = subject.getPrivateCredentials(IssuedTokenContext.class);
            for (Object obj : pcs){
                IssuedTokenContext cached = (IssuedTokenContext)obj;

                // Check if the token is expired
                Calendar c = new GregorianCalendar();
                long offset = c.get(Calendar.ZONE_OFFSET);
                if (c.getTimeZone().inDaylightTime(c.getTime())) {
                    offset += c.getTimeZone().getDSTSavings();
                }
                long beforeTime = c.getTimeInMillis();
                long currentTime = beforeTime - offset;
                if (maxClockSkew != null){
                    currentTime = currentTime - Long.parseLong(maxClockSkew);
                }
                c.setTimeInMillis(currentTime);
                Date currentTimeInDateFormat = c.getTime();
                if(cached.getExpirationTime() != null && currentTimeInDateFormat.after(cached.getExpirationTime())){
                    // Remove the expired context
                    subject.getPrivateCredentials().remove(cached);

                    //if renewExpiredToke="true" is not set
                    if (!renewExpiredToken){
                        log.log(Level.SEVERE,
                        LogStringsMessages.WST_0046_TOKEN_EXPIRED(cached.getCreationTime(), cached.getExpirationTime(), currentTimeInDateFormat));
                        throw new WSTrustException(LogStringsMessages.WST_0046_TOKEN_EXPIRED(cached.getCreationTime(), cached.getExpirationTime(), currentTimeInDateFormat));
                    }
                } else if (cached.getTokenIssuer().equals(ctx.getTokenIssuer())){
                    updateContext(cached, ctx);
                    return;
                }
            }
        }
        
        TrustPlugin tp = WSTrustFactory.newTrustPlugin();
        tp.process(ctx);
        if (shareToken){
            if (subject == null){
                 subject = new Subject();
            }
            subject.getPrivateCredentials().add(ctx);
            SubjectAccessor.setRequesterSubject(subject);
        }
    }
}
