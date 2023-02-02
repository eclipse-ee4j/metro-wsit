/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.secconv.impl.client;

import com.sun.xml.ws.api.security.secconv.client.SCTokenConfiguration;
import com.sun.xml.ws.security.IssuedTokenContext;
import com.sun.xml.ws.api.security.trust.WSTrustException;
import com.sun.xml.ws.api.security.trust.client.IssuedTokenProvider;
import com.sun.xml.ws.security.SecurityContextToken;
import com.sun.xml.ws.security.SecurityContextTokenInfo;
import com.sun.xml.ws.security.impl.policyconv.IntegrityAssertionProcessor;
import com.sun.xml.ws.security.impl.policyconv.SecurityPolicyUtil;
import com.sun.xml.ws.security.impl.policyconv.SignatureTargetCreator;
import com.sun.xml.ws.security.secconv.WSSCFactory;
import com.sun.xml.ws.security.secconv.WSSCPlugin;
import com.sun.xml.ws.security.secconv.WSSecureConversationException;
import com.sun.xml.ws.security.secconv.logging.LogDomainConstants;
import com.sun.xml.wss.impl.PolicyTypeUtil;
import com.sun.xml.wss.impl.policy.PolicyGenerationException;
import com.sun.xml.wss.impl.policy.SecurityPolicy;
import com.sun.xml.wss.impl.policy.mls.MessagePolicy;
import com.sun.xml.wss.impl.policy.mls.SignaturePolicy;
import com.sun.xml.wss.impl.policy.mls.SignatureTarget;
import com.sun.xml.wss.impl.policy.mls.TimestampPolicy;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Shyam Rao
 */
public class SCTokenProviderImpl implements IssuedTokenProvider {
    
    private final static WSSCPlugin scp = WSSCFactory.newSCPlugin();
    private static final Logger log =
            Logger.getLogger(
            LogDomainConstants.WSSC_IMPL_DOMAIN,
            LogDomainConstants.WSSC_IMPL_DOMAIN_BUNDLE);
    /**
     * Map of SecurityContextId --{@literal >} IssuedTokenContext
     */
    private Map<String, IssuedTokenContext> issuedTokenContextMap
            = new HashMap<String, IssuedTokenContext>();    
    /**
     * Map of wsu:Instance --{@literal >} SecurityContextTokenInfo
     */
    private Map<String, SecurityContextTokenInfo> securityContextTokenMap
            = new HashMap<String, SecurityContextTokenInfo>();    
    private boolean tokenExpired = false;    
    
    public void issue(IssuedTokenContext ctx)throws WSTrustException{        
        SCTokenConfiguration sctConfig = (SCTokenConfiguration)ctx.getSecurityPolicy().get(0);
        if(issuedTokenContextMap.get(sctConfig.getTokenId()) != null ){
            IssuedTokenContext tmpCtx = null;
            try{
                tmpCtx = getSecurityContextToken(sctConfig.getTokenId(), sctConfig.checkTokenExpiry());
            }catch(WSSecureConversationException ex){
                if(sctConfig.isClientOutboundMessage()){
                    if(log.isLoggable(Level.FINE)){
                        log.log(Level.FINE, "SecureConversationToken expired");
                    }
                    tokenExpired = true;
                    renew(ctx);
                    tokenExpired = false;                    
                    tmpCtx = issuedTokenContextMap.get(sctConfig.getTokenId());
                }else{
                    throw new WSSecureConversationException(ex);
                }
            }
            if(tmpCtx != null){
                ctx.setCreationTime(tmpCtx.getCreationTime());
                ctx.setExpirationTime(tmpCtx.getExpirationTime());
                ctx.setProofKey(tmpCtx.getProofKey());
                ctx.setSecurityToken(tmpCtx.getSecurityToken());                
                ctx.setAttachedSecurityTokenReference(tmpCtx.getAttachedSecurityTokenReference());
                ctx.setUnAttachedSecurityTokenReference(tmpCtx.getUnAttachedSecurityTokenReference());
                if(tmpCtx.getSecurityToken() != null && 
                        ((SecurityContextToken)tmpCtx.getSecurityToken()).getInstance() != null){
                    String sctInfoKey = ((SecurityContextToken)tmpCtx.getSecurityToken()).getIdentifier().toString()+"_"+
                            ((SecurityContextToken)tmpCtx.getSecurityToken()).getInstance();                    
                    ctx.setSecurityContextTokenInfo(getSecurityContextTokenInfo(sctInfoKey));
                }
            }else{
                throw new WSTrustException("IssuedTokenContext for Token id "+sctConfig.getTokenId() +" not found in the client cache.");
            }
        }else if(!sctConfig.isClientOutboundMessage()){
            ctx.getSecurityPolicy().clear();
        }else{
            scp.process(ctx);            
            String sctId = ((SecurityContextToken)ctx.getSecurityToken()).getIdentifier().toString();
            sctConfig =  new DefaultSCTokenConfiguration((DefaultSCTokenConfiguration)sctConfig, sctId);
            ctx.getSecurityPolicy().clear();
            ctx.getSecurityPolicy().add(sctConfig);
            addSecurityContextToken(((SecurityContextToken)ctx.getSecurityToken()).getIdentifier().toString(), ctx);
        }        
    } 
    
    public void cancel(IssuedTokenContext ctx)throws WSTrustException{
        SCTokenConfiguration sctConfig = (SCTokenConfiguration)ctx.getSecurityPolicy().get(0);
        if(issuedTokenContextMap.get(sctConfig.getTokenId()) != null ){              
            scp.processCancellation(ctx);            
            clearSessionCache(sctConfig.getTokenId(), ctx);
        }            
    }
        
    public void renew(IssuedTokenContext ctx)throws WSTrustException{
        SCTokenConfiguration sctConfig = (SCTokenConfiguration)ctx.getSecurityPolicy().get(0);
        MessagePolicy msgPolicy = (MessagePolicy)sctConfig.getOtherOptions().get("MessagePolicy");
        if(issuedTokenContextMap.get(sctConfig.getTokenId()) != null ){
            ctx = issuedTokenContextMap.get(sctConfig.getTokenId());
            SCTokenConfiguration origSCTConfig = (SCTokenConfiguration)ctx.getSecurityPolicy().get(0);            
            if(this.tokenExpired && origSCTConfig.isRenewExpiredSCT()){
                scp.processRenew(ctx);
                String sctInfoKey = ((SecurityContextToken)ctx.getSecurityToken()).getIdentifier().toString()+"_"+
                        ((SecurityContextToken)ctx.getSecurityToken()).getInstance();                
                addSecurityContextTokenInfo(sctInfoKey, ctx.getSecurityContextTokenInfo());
            }else{
                throw new WSSecureConversationException("SecureConversation session for session Id:" + sctConfig.getTokenId() +"has expired.");
            }
        }else if(msgPolicy != null ){
            try{
                if(sctConfig.addRenewPolicy()){
                    appendEndorsingSCTRenewPolicy(msgPolicy);
                }else{
                    deleteRenewPolicy(msgPolicy);
                }
            }catch(PolicyGenerationException e){
                throw new WSTrustException(e.getMessage());
            }
        }        
    }
    
    public void validate(IssuedTokenContext ctx)throws WSTrustException{
        
    }
    
    private void addSecurityContextToken(String key, IssuedTokenContext itctx){
        issuedTokenContextMap.put(key, itctx);
    }
    
    private void addSecurityContextTokenInfo(String key, SecurityContextTokenInfo sctInfo){
        securityContextTokenMap.put(key, sctInfo);
    }

    private void clearSessionCache(String sctId, IssuedTokenContext ctx){        
        securityContextTokenMap.remove(sctId+"_"+ ((SecurityContextToken)ctx.getSecurityToken()).getInstance()); 
        issuedTokenContextMap.remove(sctId);
    }

    /**
     * Return the valid SecurityContext for matching key
     *
     * @param key The key of the security context to be looked
     * @param expiryCheck indicates whether to check the token expiry or not, 
     *                    As in case of renew we don't need to check token expiry
     * @returns IssuedTokenContext for security context key
     */
    
    private IssuedTokenContext getSecurityContextToken(String key, boolean expiryCheck) throws WSSecureConversationException{
        IssuedTokenContext ctx = issuedTokenContextMap.get(key);                        

        if (ctx != null && expiryCheck){
            SCTokenConfiguration sctConfig = (SCTokenConfiguration)ctx.getSecurityPolicy().get(0);
            String maxClockSkew  = (String)sctConfig.getOtherOptions().get(SCTokenConfiguration.MAX_CLOCK_SKEW);
            
            // Expiry check of security context token
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
           // if(!(currentTimeInDateFormat.after(ctx.getCreationTime())
              //  && currentTimeInDateFormat.before(ctx.getExpirationTime())))
            if(!currentTimeInDateFormat.before(ctx.getExpirationTime())){
                throw new WSSecureConversationException("SecureConversation session for session Id: " + key +" has expired.");
            }            
        }        
        return ctx;
    }
    
    private SecurityContextTokenInfo getSecurityContextTokenInfo(String key){
        SecurityContextTokenInfo ctx = securityContextTokenMap.get(key);                               
        return ctx;
    }
    
    private void appendEndorsingSCTRenewPolicy(final MessagePolicy policy) throws PolicyGenerationException{
        SignaturePolicy sp = scp.getRenewSignaturePolicy();
        SignaturePolicy.FeatureBinding spFB = (SignaturePolicy.FeatureBinding)sp.getFeatureBinding();
        List list = policy.getPrimaryPolicies();
        Iterator i = list.iterator();
        boolean addedSigTarget = false;
        while (i.hasNext()) {
            SecurityPolicy primaryPolicy = (SecurityPolicy) i.next();
            if(PolicyTypeUtil.signaturePolicy(primaryPolicy)){
                SignaturePolicy sigPolicy = (SignaturePolicy)primaryPolicy;
                IntegrityAssertionProcessor iAP = new IntegrityAssertionProcessor(scp.getAlgorithmSuite(), true);
                SignatureTargetCreator stc = iAP.getTargetCreator();
                SignatureTarget sigTarget = stc.newURISignatureTarget(sigPolicy.getUUID());
                SecurityPolicyUtil.setName(sigTarget, sigPolicy);
                spFB.addTargetBinding(sigTarget);
                spFB.isEndorsingSignature(true);
                addedSigTarget = true;
                break;
            }
        }

        // If no primary signature (e.g. TransportBinding), sign the
        // TimeStamp.
        if (!addedSigTarget){
            List sList = policy.getSecondaryPolicies();
            Iterator j = sList.iterator();
            while (j.hasNext()) {
                SecurityPolicy secPolicy = (SecurityPolicy) j.next();
                if(PolicyTypeUtil.timestampPolicy(secPolicy)){
                    TimestampPolicy tsPolicy = (TimestampPolicy)secPolicy;
                    IntegrityAssertionProcessor iAP = new IntegrityAssertionProcessor(scp.getAlgorithmSuite(), true);
                    SignatureTargetCreator stc = iAP.getTargetCreator();
                    SignatureTarget sigTarget = stc.newURISignatureTarget(tsPolicy.getUUID());
                    SecurityPolicyUtil.setName(sigTarget, tsPolicy);
                    spFB.addTargetBinding(sigTarget);
                    spFB.isEndorsingSignature(true);
                    addedSigTarget = true;
                    break;
                }
            }
        }
        if (addedSigTarget){
            policy.append((SecurityPolicy)sp);
        }
    }
    
    private void deleteRenewPolicy(final MessagePolicy policy){
        ArrayList list = policy.getPrimaryPolicies();
        Iterator i = list.iterator();
        while (i.hasNext()) {
            SecurityPolicy primaryPolicy = (SecurityPolicy) i.next();
            if(PolicyTypeUtil.signaturePolicy(primaryPolicy)){
                SignaturePolicy sigPolicy = (SignaturePolicy)primaryPolicy;
                if(sigPolicy.getUUID().equals("_99")){
                    policy.remove((SecurityPolicy)sigPolicy);
                    break;
                }
            }
        }
    }
}

