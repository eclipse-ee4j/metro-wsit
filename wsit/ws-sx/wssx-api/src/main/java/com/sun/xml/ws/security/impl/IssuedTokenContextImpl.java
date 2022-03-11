/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.impl;

import com.sun.xml.ws.security.SecurityContextTokenInfo;
import com.sun.xml.ws.security.IssuedTokenContext;
import com.sun.xml.ws.security.Token;
import org.apache.xml.security.encryption.EncryptedKey;
import org.apache.xml.security.encryption.XMLEncryptionException;
import org.apache.xml.security.encryption.XMLCipher;
import com.sun.xml.wss.XWSSecurityException;
import java.net.URI;
import java.security.Key;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.security.KeyPair;

import java.util.Date;

import java.util.HashMap;
import java.util.Map;
import javax.security.auth.Subject;

/**
 *
 * @author Abhijit Das
 */
public class IssuedTokenContextImpl implements IssuedTokenContext {


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
    public Key getDecipheredOtherPartyEntropy(Key privKey) throws XWSSecurityException {
        try {
            return getDecipheredOtherPartyEntropy(getOtherPartyEntropy(), privKey);
        } catch ( XMLEncryptionException xee) {
            throw new XWSSecurityException(xee);
        }
    }



    private Key getDecipheredOtherPartyEntropy(Object encryptedKey, Key privKey) throws XMLEncryptionException {
        if ( encryptedKey instanceof EncryptedKey ) {
            EncryptedKey encKey = (EncryptedKey)encryptedKey;
            XMLCipher cipher = XMLCipher.getInstance();
            cipher.setKEK(privKey);
            cipher.decryptKey(encKey);
            return null;
        } else {
            return null;
        }
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
