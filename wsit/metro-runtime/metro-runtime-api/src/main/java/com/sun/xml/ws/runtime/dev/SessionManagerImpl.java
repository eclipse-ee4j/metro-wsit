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
 * SessionManagerImpl.java
 *
 */

package com.sun.xml.ws.runtime.dev;

import com.sun.xml.ws.api.ha.HaInfo;
import com.sun.xml.ws.api.server.WSEndpoint;
import com.sun.xml.ws.api.security.secconv.WSSecureConversationRuntimeException;
import com.sun.xml.ws.api.ha.HighAvailabilityProvider;
import com.sun.xml.ws.commons.ha.HaContext;
import com.sun.xml.ws.commons.ha.StickyKey;
import com.sun.xml.ws.security.IssuedTokenContext;
import com.sun.xml.ws.security.SecurityContextToken;
import com.sun.xml.ws.security.SecurityContextTokenInfo;
import com.sun.xml.ws.security.SecurityTokenReference;
import com.sun.xml.ws.security.Token;

import java.net.URI;
import java.security.Key;
import java.security.KeyPair;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import javax.security.auth.Subject;
import javax.xml.namespace.QName;

import jakarta.xml.ws.WebServiceException;

import org.glassfish.ha.store.api.BackingStore;
import org.glassfish.ha.store.api.BackingStoreFactory;

/**
 * In memory implementation of <code>SessionManager</code>
 *
 * @author Mike Grogan
 */
public class SessionManagerImpl extends SessionManager {
    
    /**
     * Map of session id --> session
     */
    private Map<String, Session> sessionMap
            = new HashMap<>();
    /**
     * Map of SecurityContextId --> IssuedTokenContext
     */
    private Map<String, IssuedTokenContext> issuedTokenContextMap
            = new HashMap<>();
    /**
     * Map of wsu:Instance --> SecurityContextTokenInfo
     */
    private Map<String, SecurityContextTokenInfo> securityContextTokenInfoMap
            = new HashMap<>();

    private final BackingStore<StickyKey, HASecurityContextTokenInfo> sctBs;
    

    
    /** Creates a new instance of SessionManagerImpl */
    public SessionManagerImpl(WSEndpoint endpoint, boolean isSC) {
        if (isSC){
            final BackingStoreFactory bsFactory = HighAvailabilityProvider.INSTANCE.getBackingStoreFactory(HighAvailabilityProvider.StoreType.IN_MEMORY);
            this.sctBs = HighAvailabilityProvider.INSTANCE.createBackingStore(
                bsFactory,
                endpoint.getServiceName() + ":" + endpoint.getPortName()+ "_SCT_BS",
                StickyKey.class,
                HASecurityContextTokenInfo.class);
        } else{
            sctBs = null;
        }
    }
    
    /** Creates a new instance of SessionManagerImpl */
    public SessionManagerImpl(WSEndpoint endpoint, boolean isSC, Properties config) {
        this(endpoint,isSC);
        SessionManagerImpl.setConfig(config);
    }
    
    /**
     * Returns an existing session identified by the Key else null
     *
     * @param key The Session key.
     * @return The Session with the given key.  <code>null</code> if none exists.
     */
    @Override
    public Session  getSession(String key) {
        Session session = sessionMap.get(key);
        if (session == null && HighAvailabilityProvider.INSTANCE.isHaEnvironmentConfigured() && sctBs != null){
            SecurityContextTokenInfo sctInfo = HighAvailabilityProvider.loadFrom(sctBs, new StickyKey(key), null);
            session = new Session(this, key, null);
            session.setSecurityInfo(sctInfo);
            sessionMap.put(key, session);
        }
        return session;
    }

    /**
     * Returns the Set of valid Session keys.
     *
     * @return The Set of keys.
     */
    @Override
    public Set<String> keys() {
        return sessionMap.keySet();
    }

    @Override
    protected Collection<Session> sessions() {
        return sessionMap.values();
    }

    /**
     * Removed the Session with the given key.
     *
     * @param key The key of the Session to be removed.
     */
    @Override
    public void terminateSession(String key) {
        sessionMap.remove(key);
        if (HighAvailabilityProvider.INSTANCE.isHaEnvironmentConfigured() && sctBs != null){
            HighAvailabilityProvider.removeFrom(sctBs, new StickyKey(key));
        }
    }

    /**
     * Creates a Session with the given key, using a new instance
     * of the specified Class as a holder for user-defined data.  The
     * specified Class must have a default constructor.
     *
     * @param key The Session key to be used.
     * @return The new Session.. <code>null</code> if the given
     * class cannot be instantiated.
     * 
     */ 
    @Override
    public  Session createSession(String key, Class clasz) {
        //Issue 17328 - clear expired sessions after timeout
        Properties props = getConfig();
        String timeout = (String)props.get(TIMEOUT_INTERVAL);
        int timeOut = 30;
        if (timeout != null) {
            timeOut = Integer.parseInt(timeout);
        }
        for(Session session:sessionMap.values()) {
            SecurityContextTokenInfo securityInfo =  session.getSecurityInfo();
            Date expDate = securityInfo.getExpirationTime();
            Calendar expCal = Calendar.getInstance(Locale.getDefault());
            expCal.setTimeInMillis(expDate.getTime());
            if(Calendar.getInstance(Locale.getDefault()).compareTo(expCal)> (timeOut * 60 * 1000)) {
               terminateSession(session.getSessionKey()); 
            }
        }
        Session sess;
        try {
            sess = new Session(this, key, clasz.newInstance());
        } catch (InstantiationException | IllegalAccessException e) {
            return null;
        }

        sessionMap.put(key, sess);
        return sess;
        
    }
    
    
    /**
     * Creates a Session with the given key, using the specified Object
     * as a holder for user-defined data.
     *
     * @param key The Session key to be used.
     * @param obj The object to use as a holder for user data in the session.
     * @return The new Session. 
     * 
     */ 
    @Override
    public Session createSession(String key, Object obj) {
        Session session = new Session(this, key, Collections.synchronizedMap(new HashMap<String, String>()));
        sessionMap.put(key, session);
        
        return session;
    }
    
    @Override
    public Session createSession(String key, SecurityContextTokenInfo sctInfo) {
        Session session = new Session(this, key, Collections.synchronizedMap(new HashMap<String, String>()));
        session.setSecurityInfo(sctInfo);
        sessionMap.put(key, session);

        if (sctInfo != null && HighAvailabilityProvider.INSTANCE.isHaEnvironmentConfigured()){
            HASecurityContextTokenInfo hasctInfo = new HASecurityContextTokenInfo(sctInfo);
            HaInfo haInfo = HaContext.currentHaInfo();
            if (haInfo != null) {
                HaContext.udpateReplicaInstance(HighAvailabilityProvider.saveTo(sctBs, new StickyKey(key, haInfo.getKey()), hasctInfo, true));
            } else {
                final StickyKey stickyKey = new StickyKey(key);
                final String replicaId = HighAvailabilityProvider.saveTo(sctBs, stickyKey, hasctInfo, true);
                HaContext.updateHaInfo(new HaInfo(stickyKey.getHashKey(), replicaId, false));
            }
        }
        
        return session;
    }
    
     /**
     * Creates a Session with the given key, using an instance of 
     * synchronized {@code java.util.Map<String, String>} a sa holder for user-defined data.
     *
     * @param key The Session key to be used.
     * @return The new Session.
     * 
     */ 
    @Override
    public Session createSession(String key) {
       return createSession(key, Collections.synchronizedMap(new HashMap<String, String>()));
    }
    
     
    /**
     * Does nothing in this implementation.
     *
     * @param key The key of the session to be saved
     */
    @Override
    public void saveSession(String key) {
    }

     /**
     * Return the valid SecurityContext for matching key
     *
     * @param key The key of the security context to be looked
     * @return IssuedTokenContext for security context key
     */
    
    @Override
    public IssuedTokenContext getSecurityContext(String key, boolean checkExpiry){
        IssuedTokenContext ctx = issuedTokenContextMap.get(key);        
        if(ctx == null){
            // recovery of security context in case of crash
            boolean recovered = false;
            Session session = getSession(key);            
            if (session != null) {
                // recreate context info based on data stored in the session
                SecurityContextTokenInfo sctInfo = session.getSecurityInfo();
                if (sctInfo != null) {
                    ctx = sctInfo.getIssuedTokenContext();
                    // Add it to the Session Manager's local cache, after possible crash                
                    addSecurityContext(key, ctx);               
                    recovered = true;
                }
            }
            
            if (!recovered){                
                throw new WebServiceException("Could not locate SecureConversation session for Id:" + key);
            }
        }else if (ctx.getSecurityContextTokenInfo() == null && ctx.getSecurityToken() != null){
            String sctInfoKey = ((SecurityContextToken)ctx.getSecurityToken()).getIdentifier().toString()+"_"+
                            ((SecurityContextToken)ctx.getSecurityToken()).getInstance();                    
            //ctx.setSecurityContextTokenInfo(securityContextTokenInfoMap.get(((SecurityContextToken)ctx.getSecurityToken()).getInstance()));
            ctx.setSecurityContextTokenInfo(securityContextTokenInfoMap.get(sctInfoKey));
        }

        if (ctx != null && checkExpiry){
            // Expiry check of security context token
            Calendar c = new GregorianCalendar();
            long offset = c.get(Calendar.ZONE_OFFSET);
            if (c.getTimeZone().inDaylightTime(c.getTime())) {
                offset += c.getTimeZone().getDSTSavings();
            }
            long beforeTime = c.getTimeInMillis();
            long currentTime = beforeTime - offset;
            
            c.setTimeInMillis(currentTime);
            
            Date currentTimeInDateFormat = c.getTime();
            if(!(currentTimeInDateFormat.after(ctx.getCreationTime())
                && currentTimeInDateFormat.before(ctx.getExpirationTime()))){
                throw new WSSecureConversationRuntimeException(new QName("RenewNeeded"), "The provided context token has expired");
            }            
        }
        
        return ctx;
    }

    /**
     * Add the SecurityContext with key in local cache
     *
     * @param key The key of the security context to be stored
     * @param itctx The IssuedTokenContext to be stored
     */
    @Override
    public void addSecurityContext(String key, IssuedTokenContext itctx){
        issuedTokenContextMap.put(key, itctx);
        SecurityContextTokenInfo sctInfo = itctx.getSecurityContextTokenInfo();
        if(sctInfo.getInstance() != null){
            String sctInfoKey = sctInfo.getIdentifier().toString()+"_"+ sctInfo.getInstance();                    
            //securityContextTokenInfoMap.put(((SecurityContextToken)itctx.getSecurityToken()).getInstance(), itctx.getSecurityContextTokenInfo());
            securityContextTokenInfoMap.put(sctInfoKey, sctInfo);
        }
    }
    
    static class HASecurityContextTokenInfo implements SecurityContextTokenInfo{

        private static final long serialVersionUID = 1877856944264153552L;
        String identifier = null;
        String extId = null;
        String instance = null;
        byte[] secret = null;
        Map<String, byte[]> secretMap = new HashMap<>();
        Date creationTime = null;
        Date expirationTime = null;

        public HASecurityContextTokenInfo() {
            
        }
        
        public HASecurityContextTokenInfo(SecurityContextTokenInfo sctInfo) {
            identifier = sctInfo.getIdentifier();
            extId = sctInfo.getExternalId();
            instance = sctInfo.getInstance();
            secret = sctInfo.getSecret();
            creationTime = sctInfo.getCreationTime();
            expirationTime = sctInfo.getExpirationTime();
            Set<String> instKeys = sctInfo.getInstanceKeys();
            for (String instKey : instKeys){
                secretMap.put(instKey, sctInfo.getInstanceSecret(instance));
            }
        }

    
        @Override
        public String getIdentifier() {
            return identifier;
        }
    
        @Override
        public void setIdentifier(final String identifier) {
            this.identifier = identifier;
        }

        /*
         * external Id corresponds to the wsu Id on the token.
         */
        @Override
        public String getExternalId() {
            return extId;
        }

        @Override
        public void setExternalId(final String externalId) {
            this.extId = externalId;
        }
    
        @Override
        public String getInstance() {
            return instance;
        }

        @Override
        public void setInstance(final String instance) {
            this.instance = instance;
        }

        @Override
        public byte[] getSecret() {
            byte [] newSecret = new byte[secret.length];
            System.arraycopy(secret,0,newSecret,0,secret.length);
            return newSecret;
        }

        @Override
        public byte[] getInstanceSecret(final String instance) {
            return secretMap.get(instance);
        }

        @Override
        public void addInstance(final String instance, final byte[] key) {
            byte [] newKey = new byte[key.length];
            System.arraycopy(key,0,newKey,0,key.length);
            if (instance == null) {
                this.secret = newKey;
            } else {
                secretMap.put(instance, newKey);
            }
        }
    
        @Override
        public Date getCreationTime() {
            return new Date(creationTime.getTime());
        }

        @Override
        public void setCreationTime(final Date creationTime) {
            this.creationTime = new Date(creationTime.getTime());
        }

        @Override
        public Date getExpirationTime() {
            return new Date(expirationTime.getTime());
        }

        @Override
        public void setExpirationTime(final Date expirationTime) {
            this.expirationTime = new Date(expirationTime.getTime());
        }

        @Override
        public Set getInstanceKeys() {
          return secretMap.keySet();
        }
    
        @Override
        public IssuedTokenContext getIssuedTokenContext() {

            final IssuedTokenContext itc = new HAIssuedTokenContext();
            itc.setCreationTime(getCreationTime());
            itc.setExpirationTime(getExpirationTime());
            itc.setProofKey(getSecret());
            itc.setSecurityContextTokenInfo(this);
        
            return itc;
        }

        @Override
        public IssuedTokenContext getIssuedTokenContext(SecurityTokenReference reference) {
            return null;
        }
        
        @Override
        public String toString(){
            String str = "Identifier=" + identifier + " : Secret=" + Arrays.toString(secret) +
                         " : ExternalId=" +  this.extId + " : Creation Time=" +
                         this.creationTime + " : Expiration Time=" + this.expirationTime;
            return str;
        }
    }
    
    static class HAIssuedTokenContext implements IssuedTokenContext {
        X509Certificate x509Certificate = null;
        Token securityToken = null;
        Token associatedProofToken = null;
        Token secTokenReference = null;
        Token unAttachedSecTokenReference = null;
        ArrayList<Object> securityPolicies = new ArrayList<>();
        Object otherPartyEntropy = null;
        Object selfEntropy = null;
        URI computedKeyAlgorithm;
        String sigAlgorithm;
        String encAlgorithm;
        String canonicalizationAlgorithm;
        String signWith;
        String encryptWith;
        byte[] proofKey = null; // used in SecureConversation
        SecurityContextTokenInfo sctInfo = null; // used in SecureConversation
        Date creationTime = null;
        Date expiryTime = null;
        String username = null;
        String endPointAddress = null;
        Subject subject;
        KeyPair proofKeyPair;
        String authType = null;
        String tokenType = null;
        String keyType = null;
        String tokenIssuer = null;
        Token target = null;

        Map<String, Object> otherProps = new HashMap<>();

        @Override
        public X509Certificate getRequestorCertificate() {
            return x509Certificate;
        }

        @Override
        public void setRequestorCertificate(X509Certificate cert) {
            this.x509Certificate = cert;
        }

        @Override
        public Subject getRequestorSubject(){
            return subject;
        }

        @Override
        public void setRequestorSubject(Subject subject){
            this.subject = subject;
        }

        @Override
        public String getRequestorUsername() {
            return username;
        }

        @Override
        public void setRequestorUsername(String username) {
            this.username = username;
        }


        @Override
        public void setSecurityToken(Token securityToken) {
            this.securityToken = securityToken;
        }

        @Override
        public Token getSecurityToken() {
            return securityToken;
        }

        @Override
        public void setAssociatedProofToken(Token associatedProofToken) {
            this.associatedProofToken = associatedProofToken;
        }

        @Override
        public Token getAssociatedProofToken() {
            return associatedProofToken;
        }

        @Override
        public Token getAttachedSecurityTokenReference() {
            return secTokenReference;
        }

        @Override
        public void setAttachedSecurityTokenReference(Token secTokenReference) {
            this.secTokenReference = secTokenReference;
        }

        @Override
        public Token getUnAttachedSecurityTokenReference() {
            return unAttachedSecTokenReference;
        }

        @Override
        public void setUnAttachedSecurityTokenReference(Token secTokenReference) {
            this.unAttachedSecTokenReference = secTokenReference;
        }

        @Override
        public ArrayList<Object> getSecurityPolicy() {
            return securityPolicies;
        }

        @Override
        public void setOtherPartyEntropy(Object otherPartyEntropy) {
            this.otherPartyEntropy = otherPartyEntropy;
        }

        @Override
        public Object getOtherPartyEntropy() {
            return otherPartyEntropy;
        }

        @Override
        public Key getDecipheredOtherPartyEntropy(Key privKey) {
            return null;
        }

        @Override
        public void setSelfEntropy(Object selfEntropy) {
            this.selfEntropy = selfEntropy;
        }

        @Override
        public Object getSelfEntropy() {
            return selfEntropy;
        }


        @Override
        public URI getComputedKeyAlgorithmFromProofToken() {
            return computedKeyAlgorithm;
        }

        public void setComputedKeyAlgorithmFromProofToken(URI computedKeyAlgorithm) {
            this.computedKeyAlgorithm = computedKeyAlgorithm;
        }

        @Override
        public void setProofKey(byte[] key){
            this.proofKey = key;
        }

        @Override
        public byte[] getProofKey() {
            return proofKey;
        }

        @Override
        public void setProofKeyPair(KeyPair keys){
            this.proofKeyPair = keys;
        }

        @Override
        public KeyPair getProofKeyPair(){
            return this.proofKeyPair;
        }

        @Override
        public void setAuthnContextClass(String authType){
            this.authType = authType;
        }

        @Override
        public String getAuthnContextClass(){
            return this.authType;
        }

        @Override
        public Date getCreationTime() {
            return creationTime;
        }

        @Override
        public Date getExpirationTime() {
            return expiryTime;
        }

        @Override
        public void setCreationTime(Date date) {
            creationTime = date;
        }

        @Override
        public void  setExpirationTime(Date date) {
            expiryTime = date;
        }

        /**
         * set the endpointaddress
         */
        @Override
        public void  setEndpointAddress(String endPointAddress){
            this.endPointAddress = endPointAddress;
        }

        /**
         *get the endpoint address
         */
        @Override
        public String getEndpointAddress(){
            return this.endPointAddress;
        }

        @Override
        public void destroy() {

        }

        @Override
        public SecurityContextTokenInfo getSecurityContextTokenInfo() {
            return sctInfo;
        }

        @Override
        public void setSecurityContextTokenInfo(SecurityContextTokenInfo sctInfo) {
            this.sctInfo = sctInfo;
        }

        @Override
        public Map<String, Object> getOtherProperties() {
            return this.otherProps;
        }

        @Override
        public void setTokenType(String tokenType) {
            this.tokenType = tokenType;
        }

        @Override
        public String getTokenType() {
            return tokenType;
        }

        @Override
        public void setKeyType(String keyType) {
            this.keyType = keyType;
        }

        @Override
        public String getKeyType() {
            return keyType;
        }

        @Override
        public void setAppliesTo(String appliesTo) {
            this.endPointAddress = appliesTo;
        }

        @Override
        public String getAppliesTo() {
            return endPointAddress;
        }

        @Override
        public void setTokenIssuer(String issuer) {
            this.tokenIssuer = issuer;
        }

        @Override
        public String getTokenIssuer() {
            return tokenIssuer;
        }

        @Override
        public void setSignatureAlgorithm(String sigAlg){
            this.sigAlgorithm = sigAlg;
        }

        @Override
        public String getSignatureAlgorithm(){
            return sigAlgorithm;
        }

        @Override
        public void setEncryptionAlgorithm(String encAlg){
            this.encAlgorithm = encAlg;
        }

        @Override
        public String getEncryptionAlgorithm(){
            return encAlgorithm;
        }

        @Override
        public void setCanonicalizationAlgorithm(String canonAlg){
            this.canonicalizationAlgorithm = canonAlg;
        }

        @Override
        public String getCanonicalizationAlgorithm(){
            return canonicalizationAlgorithm;
        }

        @Override
        public void setSignWith(String signWithAlgo){
            this.signWith = signWithAlgo;
        }

        @Override
        public String getSignWith(){
            return signWith;
        }    

        @Override
        public void setEncryptWith(String encryptWithAlgo){
            this.encryptWith = encryptWithAlgo;
        }

        @Override
        public String getEncryptWith(){
            return encryptWith;
        }

        @Override
        public void setTarget(Token target) {
            this.target = target;
        }

        @Override
        public Token getTarget() {
            return target;
        }
    }
}
