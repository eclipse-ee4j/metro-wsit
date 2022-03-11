/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.impl.policyconv;

import com.sun.xml.ws.policy.AssertionSet;
import com.sun.xml.ws.policy.NestedPolicy;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.sourcemodel.AssertionData;
import com.sun.xml.ws.security.impl.policy.PolicyUtil;
import com.sun.xml.ws.security.policy.AsymmetricBinding;
import com.sun.xml.ws.security.policy.Claims;
import com.sun.xml.ws.security.policy.Issuer;
import com.sun.xml.ws.security.policy.IssuerName;
import com.sun.xml.ws.security.policy.SecureConversationToken;
import com.sun.xml.ws.security.policy.SecurityPolicyVersion;
import com.sun.xml.ws.security.policy.SupportingTokens;
import com.sun.xml.ws.security.policy.SymmetricBinding;
import com.sun.xml.ws.security.policy.Token;
import com.sun.xml.wss.impl.policy.mls.MessagePolicy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 *
 * @author K.Venugopal@sun.com
 */
public class SCTokenWrapper extends PolicyAssertion implements SecureConversationToken{

    private SecureConversationToken scToken = null;
    private MessagePolicy messagePolicy = null;
    private List<PolicyAssertion> issuedTokenList = null;
    private List<PolicyAssertion> kerberosTokenList = null;
    private boolean cached = false;
    private SecurityPolicyVersion spVersion = SecurityPolicyVersion.SECURITYPOLICY200507;

    /** Creates a new instance of SCTokenWrapper */
    public SCTokenWrapper(PolicyAssertion scToken,MessagePolicy mp) {
        super(AssertionData.createAssertionData(
                                scToken.getName(),
                                scToken.getValue(),
                                scToken.getAttributes(),
                                scToken.isOptional(),
                                scToken.isIgnorable()
                            ),
                getAssertionParameters(scToken),
                (scToken.getNestedPolicy()== null ? null : scToken.getNestedPolicy().getAssertionSet()));
        this.scToken = (SecureConversationToken)scToken;
        this.messagePolicy = mp;

        String nsUri = scToken.getName().getNamespaceURI();
        if(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri.equals(nsUri)){
            spVersion = SecurityPolicyVersion.SECURITYPOLICY200507;
        } else if(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri.equals(nsUri)){
            spVersion = SecurityPolicyVersion.SECURITYPOLICY12NS;
        }
    }

    private static Collection<PolicyAssertion> getAssertionParameters(PolicyAssertion scToken){
        if(scToken.hasParameters()){
            Iterator<PolicyAssertion> itr = scToken.getParametersIterator();
            if(itr.hasNext()){// will have only one assertion set. TODO:Cross check with marek.
                return Collections.singletonList(itr.next());
            }
        }
        return null;

    }

    public SecureConversationToken getSecureConversationToken() {
        return scToken;
    }

    public void setSecureConversationToken(SecureConversationToken scToken) {
        this.scToken = scToken;
    }

    public MessagePolicy getMessagePolicy() {
        return messagePolicy;
    }

    public void setMessagePolicyp(MessagePolicy mp) {
        this.messagePolicy = mp;
    }


    @Override
    public boolean isRequireDerivedKeys() {
        return this.scToken.isRequireDerivedKeys();
    }

    @Override
    public boolean isMustNotSendCancel() {
        return this.scToken.isMustNotSendCancel();
    }

    @Override
    public boolean isMustNotSendRenew() {
        return this.scToken.isMustNotSendRenew();
    }

    @Override
    public String getTokenType() {
        return this.scToken.getTokenType();
    }

    @Override
    public Issuer getIssuer() {
        return this.scToken.getIssuer();
    }

    @Override
    public IssuerName getIssuerName() {
        return this.scToken.getIssuerName();
    }

    @Override
    public Claims getClaims(){
        return this.scToken.getClaims();
    }

    @Override
    public NestedPolicy getBootstrapPolicy() {
        return this.scToken.getBootstrapPolicy();
    }


    @Override
    public String getIncludeToken() {
        return this.scToken.getIncludeToken();
    }

    @Override
    public String getTokenId() {
        return this.scToken.getTokenId();
    }


    public List<PolicyAssertion> getIssuedTokens(){
        if(!cached){
            if(this.hasNestedPolicy()){
                getTokens(this.getNestedPolicy());
                cached = true;
            }
        }
        return issuedTokenList;
    }

    public List<PolicyAssertion> getKerberosTokens(){
        if(!cached){
            if(this.hasNestedPolicy()){
                getTokens(this.getNestedPolicy());
                cached = true;
            }
        }
        return kerberosTokenList;
    }

    private void getTokens(NestedPolicy policy){
        issuedTokenList = new ArrayList<>();
        kerberosTokenList = new ArrayList<>();
        AssertionSet assertionSet = policy.getAssertionSet();
        for(PolicyAssertion pa:assertionSet){
            if(PolicyUtil.isBootstrapPolicy(pa, spVersion)){
                NestedPolicy np = pa.getNestedPolicy();
                AssertionSet bpSet = np.getAssertionSet();
                for(PolicyAssertion assertion:bpSet){
                    if(PolicyUtil.isAsymmetricBinding(assertion, spVersion)){
                        AsymmetricBinding sb =  (AsymmetricBinding)assertion;
                         Token iToken = sb.getInitiatorToken();
                        if (iToken != null){
                            addToken(iToken);
                        }else{
                            addToken(sb.getInitiatorSignatureToken());
                            addToken(sb.getInitiatorEncryptionToken());
                        }

                        Token rToken = sb.getRecipientToken();
                        if (rToken != null){
                            addToken(rToken);
                        }else{
                            addToken(sb.getRecipientSignatureToken());
                            addToken(sb.getRecipientEncryptionToken());
                        }
                    }else if(PolicyUtil.isSymmetricBinding(assertion, spVersion)){
                        SymmetricBinding sb = (SymmetricBinding)assertion;
                        Token token = sb.getProtectionToken();
                        if(token != null){
                            addToken(token);
                        }else{
                            addToken(sb.getEncryptionToken());
                            addToken(sb.getSignatureToken());
                        }
                    }else if(PolicyUtil.isSupportingTokens(assertion, spVersion)){
                        SupportingTokens st = (SupportingTokens)assertion;
                        Iterator itr = st.getTokens();
                        while(itr.hasNext()){
                            addToken((Token)itr.next());
                        }
                    }
                }
            }

        }
    }

    private void addToken(Token token){
        if (token != null) {
            if (PolicyUtil.isIssuedToken((PolicyAssertion) token, spVersion)) {
                issuedTokenList.add((PolicyAssertion) token);
            } else if (PolicyUtil.isKerberosToken((PolicyAssertion) token, spVersion)) {
                kerberosTokenList.add((PolicyAssertion) token);
            }
        }
    }

    @Override
    public Set getTokenRefernceTypes() {
        return this.scToken.getTokenRefernceTypes();
    }

    public void addBootstrapPolicy(NestedPolicy policy) {
    }

    @Override
    public SecurityPolicyVersion getSecurityPolicyVersion() {
        return spVersion;
    }
}
