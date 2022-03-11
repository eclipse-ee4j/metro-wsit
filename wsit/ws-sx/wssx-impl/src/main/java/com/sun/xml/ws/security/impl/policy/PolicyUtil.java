/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.impl.policy;

import com.sun.xml.ws.api.addressing.AddressingVersion;
import com.sun.xml.ws.api.policy.ModelGenerator;
import com.sun.xml.ws.policy.AssertionSet;
import com.sun.xml.ws.policy.Policy;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.sourcemodel.PolicyModelMarshaller;
import com.sun.xml.ws.policy.sourcemodel.PolicySourceModel;
import com.sun.xml.ws.security.policy.AlgorithmSuiteValue;
import com.sun.xml.ws.security.policy.SecurityPolicyVersion;
import com.sun.xml.wss.WSITXMLFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.UUID;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import jakarta.xml.ws.WebServiceException;

import org.w3c.dom.Document;

/**
 *
 * @author K.Venugopal@sun.com Abhijit.Das@Sun.COM
 */
public class PolicyUtil {

    /** Creates a new instance of PolicyUtil */
    public PolicyUtil() {
    }

    public static boolean isSecurityPolicyNS(PolicyAssertion pa, SecurityPolicyVersion spVersion) {
        if ( spVersion.namespaceUri.equals(pa.getName().getNamespaceURI()) ||
                Constants.MS_SP_NS.equalsIgnoreCase(pa.getName().getNamespaceURI())) {
            return true;
        }
        return false;
    }

    public static boolean isSunPolicyNS(PolicyAssertion pa) {
        if ( Constants.SUN_WSS_SECURITY_SERVER_POLICY_NS.equals(pa.getName().getNamespaceURI()) ||
                Constants.SUN_WSS_SECURITY_CLIENT_POLICY_NS.equals(pa.getName().getNamespaceURI())) {
            return true;
        }
        return false;
    }

    public static boolean isAddressingNS(PolicyAssertion pa) {
        if ( AddressingVersion.MEMBER.getNsUri().equals(pa.getName().getNamespaceURI()) ) {
            return true;
        }
        if ( AddressingVersion.W3C.getNsUri().equals(pa.getName().getNamespaceURI()) ) {
            return true;
        }
        return false;
    }

    public static boolean isTrustNS(PolicyAssertion pa) {
        if ( Constants.TRUST_NS.equals(pa.getName().getNamespaceURI()) ||
                Constants.TRUST13_NS.equals(pa.getName().getNamespaceURI())) {
            return true;
        }
        return false;
    }

    public static boolean isMEXNS(final PolicyAssertion assertion) {
        if ( Constants.MEX_NS.equals(assertion.getName().getNamespaceURI()) ) {
            return true;
        }
        return false;
    }

    public static boolean isUtilityNS(PolicyAssertion pa) {
        if ( Constants.UTILITY_NS.equals(pa.getName().getNamespaceURI()) ) {
            return true;
        }
        return false;
    }

    public static boolean isXpathNS(PolicyAssertion pa) {
        if ( Constants.XPATH_NS.equals(pa.getName().getNamespaceURI()) ) {
            return true;
        }
        return false;
    }

    public static boolean isAlgorithmAssertion(PolicyAssertion pa, SecurityPolicyVersion spVersion){
        if ( isSecurityPolicyNS(pa, spVersion) ) {
            if(pa.getName().getLocalPart().equals(Constants.AlgorithmSuite)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isToken(PolicyAssertion pa, SecurityPolicyVersion spVersion){
        if ( !isSecurityPolicyNS(pa, spVersion)) {
            return false;
        }

        if(pa.getName().getLocalPart().equals(Constants.EncryptionToken) ) {
            return true;
        }else if(pa.getName().getLocalPart().equals(Constants.SignatureToken)) {
            return true;
        }else if(pa.getName().getLocalPart().equals(Constants.InitiatorToken)) {
            return true;
        }else if(pa.getName().getLocalPart().equals(Constants.InitiatorSignatureToken)) {
            return true;
        }else if(pa.getName().getLocalPart().equals(Constants.InitiatorEncryptionToken)) {
            return true;
        }else if(pa.getName().getLocalPart().equals(Constants.HttpsToken)) {
            return true;
        }else if(pa.getName().getLocalPart().equals(Constants.IssuedToken)) {
            return true;
        }else if(pa.getName().getLocalPart().equals(Constants.KerberosToken)) {
            return true;
        }else if(pa.getName().getLocalPart().equals(Constants.ProtectionToken)) {
            return true;
        }else if(pa.getName().getLocalPart().equals(Constants.RecipientToken)) {
            return true;
        }else if(pa.getName().getLocalPart().equals(Constants.RecipientSignatureToken)) {
            return true;
        }else if(pa.getName().getLocalPart().equals(Constants.RecipientEncryptionToken)) {
            return true;
        }else if(pa.getName().getLocalPart().equals(Constants.SupportingTokens)) {
            return true;
        }else if(pa.getName().getLocalPart().equals(Constants.SC10SecurityContextToken)) {
            return true;
        }else if(pa.getName().getLocalPart().equals(Constants.SamlToken)) {
            return true;
        }else if(pa.getName().getLocalPart().equals(Constants.UsernameToken)) {
            return true;
        }else if(pa.getName().getLocalPart().equals(Constants.X509Token)) {
            return true;
        }else if(pa.getName().getLocalPart().equals(Constants.SecureConversationToken)) {
            return true;
        }else if(pa.getName().getLocalPart().equals(Constants.TransportToken)) {
            return true;
        }else if(pa.getName().getLocalPart().equals(Constants.RsaToken)){
            return true;
        }else if(pa.getName().getLocalPart().equals(Constants.KeyValueToken)){
            return true;
        }
        return false;
    }

    public static boolean isBootstrapPolicy(PolicyAssertion assertion, SecurityPolicyVersion spVersion){
        if ( !isSecurityPolicyNS(assertion, spVersion) ) {
            return false;
        }

        if(assertion.getName().getLocalPart().equals(Constants.BootstrapPolicy)) {
            return true;
        }
        return false;
    }

    public static boolean isTarget(PolicyAssertion assertion, SecurityPolicyVersion spVersion) {
        if ( !isSecurityPolicyNS(assertion, spVersion) ) {
            return false;
        }

        String name = assertion.getName().getLocalPart();
        if(name.equals(Constants.EncryptedParts) ||
                name.equals(Constants.SignedParts) ||
                name.equals(Constants.SignedElements) ||
                name.equals(Constants.EncryptedElements)) {
            return true;
        }
        return false;
    }

    public static boolean isXPath(PolicyAssertion assertion, SecurityPolicyVersion spVersion ) {
        if ( !isSecurityPolicyNS(assertion, spVersion) ) {
            return false;
        }

        if ( assertion.getName().getLocalPart().equals(Constants.XPath) ) {
            return true;
        }
        return false;
    }

    public static boolean isXPathFilter20(PolicyAssertion assertion) {
        if ( !isXpathNS(assertion) ) {
            return false;
        }

        if ( assertion.getName().getLocalPart().equals(Constants.XPathFilter20) ) {
            return true;
        }
        return false;
    }

    public static boolean isRequiredKey(PolicyAssertion assertion) {
        return false;
    }

    public static boolean isTokenType(PolicyAssertion assertion, SecurityPolicyVersion spVersion){

        if ( !isSecurityPolicyNS(assertion, spVersion)) {
            return false;
        }

        if ( assertion.getName().getLocalPart().equals(Constants.WssX509V1Token10)) {
            return true;
        } else if ( assertion.getName().getLocalPart().equals(Constants.WssX509V3Token10)) {
            return true;
        } else if ( assertion.getName().getLocalPart().equals(Constants.WssX509Pkcs7Token10)) {
            return true;
        } else if ( assertion.getName().getLocalPart().equals(Constants.WssX509PkiPathV1Token10)) {
            return true;
        } else if ( assertion.getName().getLocalPart().equals(Constants.WssX509V1Token11)) {
            return true;
        } else if ( assertion.getName().getLocalPart().equals(Constants.WssX509V3Token11)) {
            return true;
        } else if ( assertion.getName().getLocalPart().equals(Constants.WssX509Pkcs7Token11)) {
            return true;
        } else if ( assertion.getName().getLocalPart().equals(Constants.WssX509PkiPathV1Token11)) {
            return true;
        }
        return false;
    }

    public static boolean isTokenReferenceType(PolicyAssertion assertion, SecurityPolicyVersion spVersion) {

        if ( !isSecurityPolicyNS(assertion, spVersion)) {
            return false;
        }

        if ( assertion.getName().getLocalPart().equals(Constants.RequireKeyIdentifierReference)) {
            return true;
        } else if ( assertion.getName().getLocalPart().equals(Constants.RequireThumbprintReference)) {
            return true;
        } else if ( assertion.getName().getLocalPart().equals(Constants.RequireEmbeddedTokenReference)) {
            return true;
        } else if ( assertion.getName().getLocalPart().equals(Constants.RequireIssuerSerialReference)) {
            return true;
        }
        return false;
    }

    public static boolean isUsernameTokenType(PolicyAssertion assertion, SecurityPolicyVersion spVersion) {
        if ( !isSecurityPolicyNS(assertion, spVersion)) {
            return false;
        }

        if(assertion.getName().getLocalPart().equals(Constants.WssUsernameToken10) ||
                assertion.getName().getLocalPart().equals(Constants.WssUsernameToken11)) {
            return true;
        }
        return false;
    }

    public static boolean useCreated(PolicyAssertion assertion, SecurityPolicyVersion spVersion) {
       if(assertion.getName().getLocalPart().equals(Constants.Created)
               /*&& spVersion.namespaceUri.equals(SP13_NS)*/) {
            return true;
        }
       return false;
    }

    public static boolean useNonce(PolicyAssertion assertion, SecurityPolicyVersion spVersion) {
        if(assertion.getName().getLocalPart().equals(Constants.Nonce) /*&&
                spVersion.namespaceUri.equals(SP13_NS)*/) {
            return true;
        }
       return false;
    }

    public static boolean isHttpsToken(PolicyAssertion assertion, SecurityPolicyVersion spVersion) {
        if ( !isSecurityPolicyNS(assertion, spVersion)) {
            return false;
        }

        if(assertion.getName().getLocalPart().equals(Constants.HttpsToken)) {
            return true;
        }
        return false;
    }

    public static boolean isSecurityContextToken(PolicyAssertion token, SecurityPolicyVersion spVersion) {
        if ( !isSecurityPolicyNS(token, spVersion)) {
            return false;
        }

        if(token.getName().getLocalPart().equals(Constants.SecurityContextToken)) {
            return true;
        }
        return false;
    }

    public static boolean isSecurityContextTokenType(PolicyAssertion token, SecurityPolicyVersion spVersion) {
        if ( !isSecurityPolicyNS(token, spVersion)) {
            return false;
        }
        String localPart = token.getName().getLocalPart();
        if(localPart.equals(Constants.SC10SecurityContextToken)) {
            return true;
        }
        return false;
    }

    public static boolean isKerberosToken(PolicyAssertion token, SecurityPolicyVersion spVersion) {
        if ( !isSecurityPolicyNS(token, spVersion)) {
            return false;
        }

        if(token.getName().getLocalPart().equals(Constants.KerberosToken)) {
            return true;
        }
        return false;
    }

    public static boolean isKerberosTokenType(PolicyAssertion token, SecurityPolicyVersion spVersion) {
        if ( !isSecurityPolicyNS(token, spVersion)) {
            return false;
        }
        String localPart = token.getName().getLocalPart();
        if(localPart.equals(Constants.WssKerberosV5ApReqToken11)) {
            return true;
        }else if(localPart.equals(Constants.WssGssKerberosV5ApReqToken11)){
            return true;
        }
        return false;
    }

    public static boolean isKeyValueTokenType(PolicyAssertion token, SecurityPolicyVersion spVersion){
        if ( !isSecurityPolicyNS(token, spVersion)) {
            return false;
        }
        String localPart = token.getName().getLocalPart();
        if(localPart.equals(Constants.RsaKeyValue) && SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri.equals(
                spVersion.namespaceUri)){
            return true;
        }
        return false;
    }

    public static boolean isRelToken(PolicyAssertion token, SecurityPolicyVersion spVersion) {
        if ( !isSecurityPolicyNS(token, spVersion)) {
            return false;
        }

        if(token.getName().getLocalPart().equals(Constants.RelToken)) {
            return true;
        }
        return false;
    }

    public static boolean isRelTokenType(PolicyAssertion token, SecurityPolicyVersion spVersion) {
        if ( !isSecurityPolicyNS(token, spVersion)) {
            return false;
        }
        String localPart = token.getName().getLocalPart();
        if(localPart.equals(Constants.WssRelV10Token10)) {
            return true;
        }else if(localPart.equals(Constants.WssRelV10Token11)){
            return true;
        }else if(localPart.equals(Constants.WssRelV20Token10)){
            return true;
        }else if(localPart.equals(Constants.WssRelV20Token11)){
            return true;
        }
        return false;
    }

    public static boolean isIncludeTimestamp(PolicyAssertion assertion, SecurityPolicyVersion spVersion) {
        if ( !isSecurityPolicyNS(assertion, spVersion)) {
            return false;
        }

        if(assertion.getName().getLocalPart().equals(Constants.IncludeTimestamp)) {
            return true;
        }
        return false;
    }

    public static boolean disableTimestampSigning(PolicyAssertion assertion) {
        if ( !isSunPolicyNS(assertion )) {
            return false;
        }

        if(assertion.getName().getLocalPart().equals(Constants.DisableTimestampSigning)){
            return true;
        }
        return false;
    }

    public static boolean isEncryptBeforeSign(PolicyAssertion assertion, SecurityPolicyVersion spVersion) {
        if ( !isSecurityPolicyNS(assertion, spVersion)) {
            return false;
        }

        if(assertion.getName().getLocalPart().equals(Constants.EncryptBeforeSigning)) {
            return true;
        }
        return false;
    }

    public static boolean isSignBeforeEncrypt(PolicyAssertion assertion, SecurityPolicyVersion spVersion) {
        if ( !isSecurityPolicyNS(assertion, spVersion)) {
            return false;
        }

        if(assertion.getName().getLocalPart().equals(Constants.SignBeforeEncrypting)) {
            return true;
        }
        return false;
    }

    public static boolean isContentOnlyAssertion(PolicyAssertion assertion, SecurityPolicyVersion spVersion) {
        if ( !isSecurityPolicyNS(assertion, spVersion)) {
            return false;
        }

        if(assertion.getName().getLocalPart().equals(Constants.OnlySignEntireHeadersAndBody)) {
            return true;
        }
        return false;
    }

    public static boolean isMessageLayout(PolicyAssertion assertion, SecurityPolicyVersion spVersion) {
        if ( !isSecurityPolicyNS(assertion, spVersion)) {
            return false;
        }

        if(assertion.getName().getLocalPart().equals(Constants.Layout)) {
            return true;
        }
        return false;
    }

    public static boolean isEncryptParts(PolicyAssertion assertion, SecurityPolicyVersion spVersion ){
        if ( !isSecurityPolicyNS(assertion, spVersion)) {
            return false;
        }

        if(assertion.getName().getLocalPart().equals(Constants.EncryptedParts)) {
            return true;
        }
        return false;
    }

    public static boolean isEncryptedElements(PolicyAssertion assertion, SecurityPolicyVersion spVersion ){
        if ( !isSecurityPolicyNS(assertion, spVersion)) {
            return false;
        }

        if(assertion.getName().getLocalPart().equals(Constants.EncryptedElements)) {
            return true;
        }
        return false;
    }

    public static boolean isSignedParts(PolicyAssertion assertion, SecurityPolicyVersion spVersion){
        if ( !isSecurityPolicyNS(assertion, spVersion)) {
            return false;
        }

        if(assertion.getName().getLocalPart().equals(Constants.SignedParts)) {
            return true;
        }
        return false;
    }

    public static boolean isSignedElements(PolicyAssertion assertion, SecurityPolicyVersion spVersion){
        if ( !isSecurityPolicyNS(assertion, spVersion)) {
            return false;
        }

        if(assertion.getName().getLocalPart().equals(Constants.SignedElements)) {
            return true;
        }
        return false;
    }


    public static boolean isSignedSupportingToken(PolicyAssertion policyAssertion, SecurityPolicyVersion spVersion) {
        if ( !isSecurityPolicyNS(policyAssertion, spVersion)) {
            return false;
        }

        if(policyAssertion.getName().getLocalPart().equals(Constants.SignedSupportingTokens)) {
            return true;
        }
        return false;
    }

    public static boolean isEndorsedSupportingToken(PolicyAssertion policyAssertion, SecurityPolicyVersion spVersion) {
        if ( !isSecurityPolicyNS(policyAssertion, spVersion)) {
            return false;
        }

        if(policyAssertion.getName().getLocalPart().equals(Constants.EndorsingSupportingTokens)) {
            return true;
        }
        return false;
    }

    public static boolean isSignedEndorsingSupportingToken(PolicyAssertion policyAssertion, SecurityPolicyVersion spVersion) {
        if ( !isSecurityPolicyNS(policyAssertion, spVersion)) {
            return false;
        }

        if(policyAssertion.getName().getLocalPart().equals(Constants.SignedEndorsingSupportingTokens)) {
            return true;
        }
        return false;
    }

    public static boolean isSignedEncryptedSupportingToken(PolicyAssertion policyAssertion, SecurityPolicyVersion spVersion) {
        if ( !isSecurityPolicyNS(policyAssertion, spVersion)) {
            return false;
        }

        // SignedEncryptedSupportingTokens in only supported in SecurityPolicy 1.2 namespace
        if(policyAssertion.getName().getLocalPart().equals(Constants.SignedEncryptedSupportingTokens) &&
                policyAssertion.getName().getNamespaceURI().equals(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri)) {
            return true;
        }
        return false;
    }

    public static boolean isEncryptedSupportingToken(PolicyAssertion policyAssertion, SecurityPolicyVersion spVersion) {
        if ( !isSecurityPolicyNS(policyAssertion, spVersion)) {
            return false;
        }

        // EncryptedSupportingTokens in only supported in SecurityPolicy 1.2 namespace
        if(policyAssertion.getName().getLocalPart().equals(Constants.EncryptedSupportingTokens) &&
                policyAssertion.getName().getNamespaceURI().equals(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri)) {
            return true;
        }
        return false;
    }

    public static boolean isEndorsingEncryptedSupportingToken(PolicyAssertion policyAssertion, SecurityPolicyVersion spVersion) {
        if ( !isSecurityPolicyNS(policyAssertion, spVersion)) {
            return false;
        }

        // EndorsingEncryptedSupportingTokens in only supported in SecurityPolicy 1.2 namespace
        if(policyAssertion.getName().getLocalPart().equals(Constants.EndorsingEncryptedSupportingTokens) &&
                policyAssertion.getName().getNamespaceURI().equals(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri)) {
            return true;
        }
        return false;
    }

    public static boolean isSignedEndorsingEncryptedSupportingToken(PolicyAssertion policyAssertion, SecurityPolicyVersion spVersion) {
        if ( !isSecurityPolicyNS(policyAssertion, spVersion)) {
            return false;
        }

        // SignedEndorsingEncryptedSupportingTokens in only supported in SecurityPolicy 1.2 namespace
        if(policyAssertion.getName().getLocalPart().equals(Constants.SignedEndorsingEncryptedSupportingTokens) &&
                policyAssertion.getName().getNamespaceURI().equals(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri)) {
            return true;
        }
        return false;
    }


    public static boolean isBinding(PolicyAssertion policyAssertion, SecurityPolicyVersion spVersion) {

        if ( !isSecurityPolicyNS(policyAssertion, spVersion)) {
            return false;
        }

        String name = policyAssertion.getName().getLocalPart();
        if(name.equals(Constants.SymmetricBinding) ||
                name.equals(Constants.AsymmetricBinding) ||
                name.equals(Constants.TransportBinding)) {

            return true;
        }
        return false;
    }

    public static boolean isUsernameToken(PolicyAssertion token, SecurityPolicyVersion spVersion) {
        if ( !isSecurityPolicyNS(token, spVersion)) {
            return false;
        }

        if(token.getName().getLocalPart().equals(Constants.UsernameToken)) {
            return true;
        }
        return false;
    }

    public static boolean isSamlToken(PolicyAssertion token, SecurityPolicyVersion spVersion) {
        if ( !isSecurityPolicyNS(token, spVersion)) {
            return false;
        }

        if(token.getName().getLocalPart().equals(Constants.SamlToken)) {
            return true;
        }
        return false;
    }

    public static boolean isSamlTokenType(PolicyAssertion token, SecurityPolicyVersion spVersion) {
        if ( !isSecurityPolicyNS(token, spVersion)) {
            return false;
        }
        String localPart = token.getName().getLocalPart();
        if(localPart.equals(Constants.WssSamlV10Token10)) {
            return true;
        }else if(localPart.equals(Constants.WssSamlV10Token11)){
            return true;
        }else if(localPart.equals(Constants.WssSamlV11Token10)){
            return true;
        }else if(localPart.equals(Constants.WssSamlV20Token11)){
            return true;
        }else if(localPart.equals(Constants.WssSamlV11Token11)){
            return true;
        }
        return false;
    }

    public static boolean isIssuedToken(PolicyAssertion token, SecurityPolicyVersion spVersion) {
        if ( !isSecurityPolicyNS(token, spVersion)) {
            return false;
        }

        if(token.getName().getLocalPart().equals(Constants.IssuedToken)) {
            return true;
        }
        return false;
    }

    public static boolean isSecureConversationToken(PolicyAssertion token, SecurityPolicyVersion spVersion) {
        if ( !isSecurityPolicyNS(token, spVersion)) {
            return false;
        }

        if(token.getName().getLocalPart().equals(Constants.SecureConversationToken)) {
            return true;
        }
        return false;
    }

    public static boolean isX509Token(PolicyAssertion policyAssertion, SecurityPolicyVersion spVersion) {
        if ( !isSecurityPolicyNS(policyAssertion, spVersion)) {
            return false;
        }

        if(policyAssertion.getName().getLocalPart().equals(Constants.X509Token)) {
            return true;
        }
        return false;
    }

    public static boolean isKeyValueToken(PolicyAssertion policyAssertion, SecurityPolicyVersion spVersion) {
        if ( !isSecurityPolicyNS(policyAssertion, spVersion)) {
            return false;
        }
        if(policyAssertion.getName().getLocalPart().equals(Constants.KeyValueToken) && SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri.equals(
                spVersion.namespaceUri)) {
            return true;
        }
        return false;
    }

    // RsaToken is Microsoft's proprietary assertion
    public static boolean isRsaToken(PolicyAssertion policyAssertion, SecurityPolicyVersion spVersion) {
        if ( !isSecurityPolicyNS(policyAssertion, spVersion)) {
            return false;
        }
        if(policyAssertion.getName().getLocalPart().equals(Constants.RsaToken) && SecurityPolicyVersion.MS_SECURITYPOLICY200507.namespaceUri.equals(
                spVersion.namespaceUri)) {
            return true;
        }
        return false;
    }

    public static boolean isAsymmetricBinding(PolicyAssertion assertion, SecurityPolicyVersion spVersion){
        if ( !isSecurityPolicyNS(assertion, spVersion)) {
            return false;
        }

        if(assertion.getName().getLocalPart().equals(Constants.AsymmetricBinding)) {
            return true;
        }
        return false;
    }

    public static boolean isAsymmetricBinding(QName assertion, SecurityPolicyVersion spVersion){
        if ( assertion.getLocalPart().equals(Constants.AsymmetricBinding) &&
                assertion.getNamespaceURI().equals(spVersion.namespaceUri)) {
            return true;
        }
        return false;
    }

    public static boolean isTransportBinding(PolicyAssertion assertion, SecurityPolicyVersion spVersion){
        if ( !isSecurityPolicyNS(assertion, spVersion)) {
            return false;
        }

        if(assertion.getName().getLocalPart().equals(Constants.TransportBinding)) {
            return true;
        }
        return false;
    }

    public static boolean isTransportBinding(QName assertion, SecurityPolicyVersion spVersion){
        if ( assertion.getLocalPart().equals(Constants.TransportBinding) &&
                assertion.getNamespaceURI().equals(spVersion.namespaceUri)) {
            return true;
        }
        return false;
    }

    public static boolean isSymmetricBinding(PolicyAssertion assertion, SecurityPolicyVersion spVersion){
        if ( !isSecurityPolicyNS(assertion, spVersion)) {
            return false;
        }

        if(assertion.getName().getLocalPart().equals(Constants.SymmetricBinding)) {
            return true;
        }
        return false;
    }

    public static boolean isSymmetricBinding(QName assertion, SecurityPolicyVersion spVersion){
        if ( assertion.getLocalPart().equals(Constants.SymmetricBinding) &&
                assertion.getNamespaceURI().equals(spVersion.namespaceUri)) {
            return true;
        }
        return false;
    }

    public static boolean isSupportingTokens(PolicyAssertion assertion, SecurityPolicyVersion spVersion){
        if ( !isSecurityPolicyNS(assertion, spVersion)) {
            return false;
        }

        if(isSignedSupportingToken(assertion, spVersion) || isEndorsedSupportingToken(assertion, spVersion)||
                isSignedEndorsingSupportingToken(assertion, spVersion) || isSupportingToken(assertion, spVersion) ||
                isSignedEncryptedSupportingToken(assertion, spVersion) || isEncryptedSupportingToken(assertion, spVersion) ||
                isEndorsingEncryptedSupportingToken(assertion, spVersion) || isSignedEndorsingEncryptedSupportingToken(assertion, spVersion)){
            return true;
        }
        return false;
    }


    public static boolean isSupportingToken(PolicyAssertion assertion, SecurityPolicyVersion spVersion){
        if ( !isSecurityPolicyNS(assertion, spVersion )) {
            return false;
        }

        if(assertion.getName().getLocalPart().equals(Constants.SupportingTokens)){
            return true;
        }
        return false;
    }


    public static boolean isSupportClientChallenge(PolicyAssertion assertion, SecurityPolicyVersion spVersion) {
        if ( !isSecurityPolicyNS(assertion, spVersion)) {
            return false;
        }

        if(assertion.getName().getLocalPart().equals(Constants.MustSupportClientChallenge)) {
            return true;
        }
        return false;
    }

    public static boolean isSupportServerChallenge(PolicyAssertion assertion, SecurityPolicyVersion spVersion) {
        if ( !isSecurityPolicyNS(assertion, spVersion)) {
            return false;
        }

        if(assertion.getName().getLocalPart().equals(Constants.MustSupportServerChallenge)) {
            return true;
        }
        return false;
    }

    public static boolean isWSS10PolicyContent(PolicyAssertion assertion, SecurityPolicyVersion spVersion){
        if ( !isSecurityPolicyNS(assertion, spVersion)) {
            return false;
        }

        if(assertion.getName().getLocalPart().equals(Constants.MustSupportRefKeyIdentifier)) {
            return true;
        }else if( assertion.getName().getLocalPart().equals(Constants.MustSupportRefIssuerSerial)) {
            return true;
        }else if(assertion.getName().getLocalPart().equals(Constants.RequireExternalUriReference)) {
            return true;
        }else if(assertion.getName().getLocalPart().equals(Constants.RequireEmbeddedTokenReference)) {
            return true;
        }
        return false;
    }

    public static boolean isWSS11PolicyContent(PolicyAssertion assertion, SecurityPolicyVersion spVersion){
        if ( !isSecurityPolicyNS(assertion, spVersion)) {
            return false;
        }

        if(assertion.getName().getLocalPart().equals(Constants.MustSupportRefKeyIdentifier)) {
            return true;
        }else if( assertion.getName().getLocalPart().equals(Constants.MustSupportRefIssuerSerial)) {
            return true;
        }else if(assertion.getName().getLocalPart().equals(Constants.MustSupportRefThumbprint)) {
            return true;
        }else if(assertion.getName().getLocalPart().equals(Constants.MustSupportRefEncryptedKey)) {
            return true;
        }else if(assertion.getName().getLocalPart().equals(Constants.RequireSignatureConfirmation)) {
            return true;
        }else if(assertion.getName().getLocalPart().equals(Constants.RequireExternalUriReference)) {
            return true;
        }else if(assertion.getName().getLocalPart().equals(Constants.RequireEmbeddedTokenReference)) {
            return true;
        }
        return false;
    }

    /**
     * introduced for SecurityPolicy 1.2
     */
    public static boolean isRequireClientCertificate(PolicyAssertion assertion, SecurityPolicyVersion spVersion) {
        if ( !isSecurityPolicyNS(assertion, spVersion)) {
            return false;
        }

        // RequireClientCertificate as a policy assertion is only supported in SP 1.2 namespace
        if(assertion.getName().getLocalPart().equals(Constants.RequireClientCertificate) &&
                assertion.getName().getNamespaceURI().equals(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri)){
            return true;
        }
        return false;
    }

    /**
     * introduced for SecurityPolicy 1.2
     */
    public static boolean isHttpBasicAuthentication(PolicyAssertion assertion, SecurityPolicyVersion spVersion) {
        if ( !isSecurityPolicyNS(assertion, spVersion)) {
            return false;
        }

        // HttpBasicAuthentication as a policy assertion is only supported in SP 1.2 namespace
        if(assertion.getName().getLocalPart().equals(Constants.HttpBasicAuthentication) &&
                assertion.getName().getNamespaceURI().equals(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri)){
            return true;
        }
        return false;
    }

    /**
     * introduced for SecurityPolicy 1.2
     */
    public static boolean isHttpDigestAuthentication(PolicyAssertion assertion, SecurityPolicyVersion spVersion) {
        if ( !isSecurityPolicyNS(assertion, spVersion)) {
            return false;
        }

        // HttpDigestAuthentication as a policy assertion is only supported in SP 1.2 namespace
        if(assertion.getName().getLocalPart().equals(Constants.HttpDigestAuthentication) &&
                assertion.getName().getNamespaceURI().equals(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri)){
            return true;
        }
        return false;
    }

    public static boolean isRequireClientEntropy(PolicyAssertion assertion, SecurityPolicyVersion spVersion) {
        if ( !isSecurityPolicyNS(assertion, spVersion)) {
            return false;
        }

        if(assertion.getName().getLocalPart().equals(Constants.RequireClientEntropy)) {
            return true;
        }
        return false;
    }

    public static boolean isRequireServerEntropy(PolicyAssertion assertion, SecurityPolicyVersion spVersion) {
        if ( !isSecurityPolicyNS(assertion, spVersion)) {
            return false;


        }

        if(assertion.getName().getLocalPart().equals(Constants.RequireServerEntropy)) {
            return true;
        }
        return false;
    }

    public static boolean isSupportIssuedTokens(PolicyAssertion assertion, SecurityPolicyVersion spVersion) {
        if ( !isSecurityPolicyNS(assertion, spVersion)) {
            return false;
        }

        if(assertion.getName().getLocalPart().equals(Constants.MustSupportIssuedTokens)) {
            return true;
        }
        return false;
    }

    public static boolean isRequestSecurityTokenCollection(PolicyAssertion assertion, SecurityPolicyVersion spVersion) {
        if ( !isSecurityPolicyNS(assertion, spVersion)) {
            return false;
        }

        if(assertion.getName().getLocalPart().equals(Constants.RequireRequestSecurityTokenCollection) &&
                assertion.getName().getNamespaceURI().equals(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri)) {
            return true;
        }
        return false;
    }

    public static boolean isAppliesTo(PolicyAssertion assertion, SecurityPolicyVersion spVersion) {
        if ( !isSecurityPolicyNS(assertion, spVersion)) {
            return false;
        }

        if(assertion.getName().getLocalPart().equals(Constants.RequireAppliesTo) &&
                assertion.getName().getNamespaceURI().equals(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri)) {
            return true;
        }
        return false;
    }

    public static boolean isIssuer(PolicyAssertion assertion, SecurityPolicyVersion spVersion) {
        if ( !isSecurityPolicyNS(assertion, spVersion)) {
            return false;
        }

        if(assertion.getName().getLocalPart().equals(Constants.Issuer)) {
            return true;
        }
        return false;
    }

    public static boolean isIssuerName(PolicyAssertion assertion, SecurityPolicyVersion spVersion) {
       if ( !isSecurityPolicyNS(assertion, spVersion)) {
            return false;
        }

       // Issuer Name only supported for 1.2 namespace
        if(assertion.getName().getLocalPart().equals(Constants.IssuerName) &&
                assertion.getName().getNamespaceURI().equals(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri)) {
            return true;
        }
        return false;
    }

    public static boolean isWSS10(PolicyAssertion assertion, SecurityPolicyVersion spVersion){
        if ( !isSecurityPolicyNS(assertion, spVersion)) {
            return false;
        }
        if(assertion.getName().getLocalPart().equals(Constants.Wss10)) {
            return true;
        }
        return false;
    }

    public static boolean isWSS11(PolicyAssertion assertion, SecurityPolicyVersion spVersion){
        if ( !isSecurityPolicyNS(assertion, spVersion)) {
            return false;
        }

        if(assertion.getName().getLocalPart().equals(Constants.Wss11)) {
            return true;
        }
        return false;
    }

    public static boolean isTrust10(PolicyAssertion assertion, SecurityPolicyVersion spVersion){
        if ( !isSecurityPolicyNS(assertion, spVersion)) {
            return false;
        }

        // Trust10 assertion is allowed only in 2005/07 namespace
        if(assertion.getName().getLocalPart().equals(Constants.Trust10) &&
                assertion.getName().getNamespaceURI().equals(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri)) {
            return true;
        }
        return false;
    }

    public static boolean isTrust13(PolicyAssertion assertion, SecurityPolicyVersion spVersion){
        if ( !isSecurityPolicyNS(assertion, spVersion)) {
            return false;
        }

        // Trust13 assertion is allowed only in 1.2  namespace
        if(assertion.getName().getLocalPart().equals(Constants.Trust13) &&
                assertion.getName().getNamespaceURI().equals(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri)) {
            return true;
        }
        return false;
    }

        public static boolean isMustNotSendCancel(PolicyAssertion assertion, SecurityPolicyVersion spVersion){
        if ( !isSecurityPolicyNS(assertion, spVersion)) {
            return false;
        }

        // MustNotSendCancel assertion is allowed only in 1.2  namespace
        if(assertion.getName().getLocalPart().equals(Constants.MustNotSendCancel) &&
                assertion.getName().getNamespaceURI().equals(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri)) {
            return true;
        }
        return false;
    }

    public static boolean isMustNotSendRenew(PolicyAssertion assertion, SecurityPolicyVersion spVersion){
        if ( !isSecurityPolicyNS(assertion, spVersion)) {
            return false;
        }

        // MustNotSendCancel assertion is allowed only in 1.2  namespace
        if(assertion.getName().getLocalPart().equals(Constants.MustNotSendRenew) &&
                assertion.getName().getNamespaceURI().equals(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri)) {
            return true;
        }
        return false;
    }

    public static boolean isBody(PolicyAssertion assertion, SecurityPolicyVersion spVersion){
        if ( !isSecurityPolicyNS(assertion, spVersion)) {
            return false;
        }

        if(assertion.getName().getLocalPart().equals(Constants.Body)){
            return true;
        }
        return false;
    }

    public static boolean isAttachments(PolicyAssertion assertion, SecurityPolicyVersion spVersion){
        if ( !isSecurityPolicyNS(assertion, spVersion)) {
            return false;
        }
        // sp:Attachments assertion is allowed only in 1.2  namespace
        if(assertion.getName().getLocalPart().equals(Constants.Attachments) &&
                assertion.getName().getNamespaceURI().equals(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri)) {
            return true;
        }
        return false;
    }

    public static boolean isAttachmentCompleteTransform(PolicyAssertion assertion, SecurityPolicyVersion spVersion){
        if ( !isSecurityPolicyNS(assertion, spVersion)) {
            return false;
        }
        // sp:AttachmentCompleteSignatureTransform assertion is allowed only in 1.2  namespace
        if(assertion.getName().getLocalPart().equals(Constants.AttachmentCompleteSignatureTransform) &&
                assertion.getName().getNamespaceURI().equals(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri)) {
            return true;
        }
        return false;
    }

    public static boolean isAttachmentContentTransform(PolicyAssertion assertion, SecurityPolicyVersion spVersion){
        if ( !isSecurityPolicyNS(assertion, spVersion)) {
            return false;
        }
        // sp:ContentSignatureTransform assertion is allowed only in 1.2  namespace
        if(assertion.getName().getLocalPart().equals(Constants.ContentSignatureTransform) &&
                assertion.getName().getNamespaceURI().equals(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri)) {
            return true;
        }
        return false;
    }

    public static boolean isRequireDerivedKeys(PolicyAssertion assertion, SecurityPolicyVersion spVersion ) {
        if ( !isSecurityPolicyNS(assertion, spVersion)) {
            return false;
        }

        if ( Constants.RequireDerivedKeys.toString().equals(assertion.getName().getLocalPart().toString())) {
            return true;
        }
        return false;
    }

    public static AlgorithmSuiteValue isValidAlgorithmSuiteValue(PolicyAssertion assertion, SecurityPolicyVersion spVersion) {
        if ( !isSecurityPolicyNS(assertion, spVersion)) {
            return null;
        }

        if ( assertion.getName().getLocalPart().equals(Constants.Basic256) ) {
            return AlgorithmSuiteValue.Basic256;
        } else if ( assertion.getName().getLocalPart().equals(Constants.Basic192)) {
            return AlgorithmSuiteValue.Basic192;
        } else if ( assertion.getName().getLocalPart().equals(Constants.Basic128)) {
            return AlgorithmSuiteValue.Basic128;
        } else if ( assertion.getName().getLocalPart().equals(Constants.TripleDes)) {
            return AlgorithmSuiteValue.TripleDes;
        } else if ( assertion.getName().getLocalPart().equals(Constants.Basic256Rsa15)) {
            return AlgorithmSuiteValue.Basic256Rsa15;
        } else if ( assertion.getName().getLocalPart().equals(Constants.Basic192Rsa15)) {
            return AlgorithmSuiteValue.Basic192Rsa15;
        } else if ( assertion.getName().getLocalPart().equals(Constants.Basic128Rsa15)) {
            return AlgorithmSuiteValue.Basic128Rsa15;
        } else if ( assertion.getName().getLocalPart().equals(Constants.TripleDesRsa15)) {
            return AlgorithmSuiteValue.TripleDesRsa15;
        } else if ( assertion.getName().getLocalPart().equals(Constants.Basic256Sha256)) {
            return AlgorithmSuiteValue.Basic256Sha256;
        } else if ( assertion.getName().getLocalPart().equals(Constants.Basic192Sha256)) {
            return AlgorithmSuiteValue.Basic192Sha256;
        } else if ( assertion.getName().getLocalPart().equals(Constants.Basic128Sha256)) {
            return AlgorithmSuiteValue.Basic128Sha256;
        } else if ( assertion.getName().getLocalPart().equals(Constants.TripleDesSha256)) {
            return AlgorithmSuiteValue.TripleDesSha256;
        } else if ( assertion.getName().getLocalPart().equals(Constants.Basic256Sha256Rsa15)) {
            return AlgorithmSuiteValue.Basic256Sha256Rsa15;
        } else if ( assertion.getName().getLocalPart().equals(Constants.Basic192Sha256Rsa15)) {
            return AlgorithmSuiteValue.Basic192Sha256Rsa15;
        } else if ( assertion.getName().getLocalPart().equals(Constants.Basic128Sha256Rsa15)) {
            return AlgorithmSuiteValue.Basic128Sha256Rsa15;
        } else if ( assertion.getName().getLocalPart().equals(Constants.TripleDesSha256Rsa15)) {
            return AlgorithmSuiteValue.TripleDesSha256Rsa15;
        }
        return null;
    }

    public static boolean isInclusiveC14N(PolicyAssertion assertion, SecurityPolicyVersion spVersion ) {
        if ( !isSecurityPolicyNS(assertion, spVersion)) {
            return false;
        }

        if ( assertion.getName().getLocalPart().equals(Constants.InclusiveC14N)) {
            return true;
        }
        return false;

    }

    public static boolean isInclusiveC14NWithComments(PolicyAssertion assertion ) {

        if(!isSunPolicyNS(assertion)){
            return false;
        }
        if ( assertion.getName().getLocalPart().equals(Constants.InclusiveC14NWithComments)) {
            return true;
        }
        return false;
    }

    public static boolean isInclusiveC14NWithCommentsForTransforms(PolicyAssertion assertion ) {

        if(!isSunPolicyNS(assertion)){
            return false;
        }
        if ( assertion.getName().getLocalPart().equals(Constants.InclusiveC14NWithComments)) {
            if("true".equals(assertion.getAttributeValue(new QName(Constants.SUN_WSS_SECURITY_SERVER_POLICY_NS, "forTransforms"))))
                return true;
        }
        return false;
    }

    public static boolean isInclusiveC14NWithCommentsForCm(PolicyAssertion assertion ) {

        if(!isSunPolicyNS(assertion)){
            return false;
        }
        if ( assertion.getName().getLocalPart().equals(Constants.InclusiveC14NWithComments)) {
            if("true".equals(assertion.getAttributeValue(new QName(Constants.SUN_WSS_SECURITY_SERVER_POLICY_NS, "forCm"))))
                return true;
        }
        return false;
    }

    public static boolean isExclusiveC14NWithComments(PolicyAssertion assertion ) {
        if(!isSunPolicyNS(assertion)){
            return false;
        }
        if ( assertion.getName().getLocalPart().equals(Constants.ExclusiveC14NWithComments)) {
            return true;
        }
        return false;
    }

    public static boolean isExclusiveC14NWithCommentsForTransforms(PolicyAssertion assertion ) {
        if(!isSunPolicyNS(assertion)){
            return false;
        }
        if ( assertion.getName().getLocalPart().equals(Constants.ExclusiveC14NWithComments)) {
            if("true".equals(assertion.getAttributeValue(new QName(Constants.SUN_WSS_SECURITY_SERVER_POLICY_NS, "forTransforms"))))
                return true;
        }
        return false;
    }

    public static boolean isExclusiveC14NWithCommentsForCm(PolicyAssertion assertion ) {
        if(!isSunPolicyNS(assertion)){
            return false;
        }
        if ( assertion.getName().getLocalPart().equals(Constants.ExclusiveC14NWithComments)) {
            if("true".equals(assertion.getAttributeValue(new QName(Constants.SUN_WSS_SECURITY_SERVER_POLICY_NS, "forCm"))))
                return true;
        }
        return false;
    }

    public static boolean isSTRTransform10(PolicyAssertion assertion, SecurityPolicyVersion spVersion) {
        if ( !isSecurityPolicyNS(assertion, spVersion)) {
            return false;
        }

        if ( assertion.getName().getLocalPart().equals(Constants.STRTransform10)) {
            return true;
        }
        return false;
    }

    public static boolean isInitiatorToken(PolicyAssertion assertion, SecurityPolicyVersion spVersion) {
        if ( !isSecurityPolicyNS(assertion, spVersion)) {
            return false;
        }

        if ( assertion.getName().getLocalPart().equals(Constants.InitiatorToken)) {
            return true;
        }
        return false;
    }

     public static boolean isInitiatorEncryptionToken(PolicyAssertion assertion, SecurityPolicyVersion spVersion) {
        if ( !isSecurityPolicyNS(assertion, spVersion)) {
            return false;
        }

        if ( assertion.getName().getLocalPart().equals(Constants.InitiatorEncryptionToken)) {
            return true;
        }
        return false;
    }

    public static boolean isInitiatorSignatureToken(PolicyAssertion assertion, SecurityPolicyVersion spVersion) {
        if ( !isSecurityPolicyNS(assertion, spVersion)) {
            return false;
        }

        if ( assertion.getName().getLocalPart().equals(Constants.InitiatorSignatureToken)) {
            return true;
        }
        return false;
    }


    public static boolean isRecipientToken(PolicyAssertion assertion, SecurityPolicyVersion spVersion) {
        if ( !isSecurityPolicyNS(assertion, spVersion)) {
            return false;
        }

        if ( assertion.getName().getLocalPart().equals(Constants.RecipientToken)) {
            return true;
        }
        return false;
    }

    public static boolean isRecipientSignatureToken(PolicyAssertion assertion, SecurityPolicyVersion spVersion) {
        if ( !isSecurityPolicyNS(assertion, spVersion)) {
            return false;
        }

        if ( assertion.getName().getLocalPart().equals(Constants.RecipientSignatureToken)) {
            return true;
        }
        return false;
    }

    public static boolean isRecipientEncryptionToken(PolicyAssertion assertion, SecurityPolicyVersion spVersion) {
        if ( !isSecurityPolicyNS(assertion, spVersion)) {
            return false;
        }

        if ( assertion.getName().getLocalPart().equals(Constants.RecipientEncryptionToken)) {
            return true;
        }
        return false;
    }


    public static boolean isProtectTokens(PolicyAssertion assertion, SecurityPolicyVersion spVersion) {
        if ( !isSecurityPolicyNS(assertion, spVersion)) {
            return false;
        }

        if ( assertion.getName().getLocalPart().equals(Constants.ProtectTokens)) {
            return true;
        }
        return false;
    }

    public static boolean isEncryptSignature(PolicyAssertion assertion, SecurityPolicyVersion spVersion) {
        if ( !isSecurityPolicyNS(assertion, spVersion)) {
            return false;
        }

        if ( assertion.getName().getLocalPart().equals(Constants.EncryptSignature)) {
            return true;
        }
        return false;
    }

    public static boolean isCreated(PolicyAssertion assertion) {
        if ( !isUtilityNS(assertion)) {
            return false;
        }

        if ( assertion.getName().getLocalPart().equals(Constants.Created)) {
            return true;
        }
        return false;
    }

    public static boolean isExpires(PolicyAssertion assertion) {
        if (!isUtilityNS(assertion)) {
            return false;
        }

        if ( assertion.getName().getLocalPart().equals(Constants.Expires)) {
            return true;
        }
        return false;
    }

    public static boolean isSignatureToken(PolicyAssertion assertion, SecurityPolicyVersion spVersion) {
        if ( !isSecurityPolicyNS(assertion, spVersion)) {
            return false;
        }

        if ( assertion.getName().getLocalPart().equals(Constants.SignatureToken)) {
            return true;
        }

        return false;
    }

    public static boolean isEncryptionToken(PolicyAssertion assertion, SecurityPolicyVersion spVersion) {
        if ( !isSecurityPolicyNS(assertion, spVersion)) {
            return false;
        }

        if ( assertion.getName().getLocalPart().equals(Constants.EncryptionToken)) {
            return true;
        }
        return false;
    }

    public static boolean isProtectionToken(PolicyAssertion assertion, SecurityPolicyVersion spVersion) {
        if ( !isSecurityPolicyNS(assertion, spVersion)) {
            return false;
        }

        if ( assertion.getName().getLocalPart().equals(Constants.ProtectionToken)) {
            return true;
        }
        return false;
    }

    public static boolean isAddress(PolicyAssertion assertion ) {
        if ( !isAddressingNS(assertion)) {
            return false;
        }

        if ( assertion.getName().getLocalPart().equals(Constants.Address)) {
            return true;
        }

        return false;
    }

    public static boolean isAddressingMetadata(final PolicyAssertion assertion) {
        if ( !PolicyUtil.isAddressingNS(assertion)) {
            return false;
        }

        if ( assertion.getName().getLocalPart().equals(Constants.Metadata)) {
            return true;
        }
        return false;
    }

    public static boolean isMetadata(final PolicyAssertion assertion ) {
        if ( !isMEXNS(assertion)) {
            return false;
        }

        if ( assertion.getName().getLocalPart().equals(Constants.Metadata)) {
            return true;
        }

        return false;
    }

    public static boolean isMetadataSection(final PolicyAssertion assertion) {
        if ( !isMEXNS(assertion)) {
            return false;
        }

        if ( assertion.getName().getLocalPart().equals(Constants.MetadataSection)) {
            return true;
        }

        return false;
    }

    public static boolean isMetadataReference(final PolicyAssertion assertion) {
        if ( !isMEXNS(assertion)) {
            return false;
        }

        if ( assertion.getName().getLocalPart().equals(Constants.MetadataReference)) {
            return true;
        }

        return false;
    }

    public static boolean isRequestSecurityTokenTemplate(PolicyAssertion assertion, SecurityPolicyVersion spVersion) {
        if ( !isSecurityPolicyNS(assertion, spVersion)) {
            return false;
        }

        if ( assertion.getName().getLocalPart().equals(Constants.RequestSecurityTokenTemplate)) {
            return true;
        }
        return false;
    }

    public static boolean isRequireExternalUriReference(PolicyAssertion assertion, SecurityPolicyVersion spVersion) {
        if ( !isSecurityPolicyNS(assertion, spVersion)) {
            return false;
        }

        if ( assertion.getName().getLocalPart().equals(Constants.RequireExternalUriReference)) {
            return true;
        }

        return false;
    }

    public static boolean isRequireExternalReference(PolicyAssertion assertion, SecurityPolicyVersion spVersion) {
        if ( !isSecurityPolicyNS(assertion, spVersion)) {
            return false;
        }

        if ( assertion.getName().getLocalPart().equals(Constants.RequireExternalReference)) {
            return true;
        }

        return false;
    }

    public static boolean isRequireInternalReference(PolicyAssertion assertion, SecurityPolicyVersion spVersion) {
        if ( !isSecurityPolicyNS(assertion, spVersion)) {
            return false;
        }

        if ( assertion.getName().getLocalPart().equals(Constants.RequireInternalReference)) {
            return true;
        }

        return false;
    }

    public static boolean isEndpointReference(PolicyAssertion assertion) {
        if ( !isAddressingNS(assertion)) {
            return false;
        }

        if ( assertion.getName().getLocalPart().equals(Constants.EndpointReference)) {
            return true;
        }
        return false;
    }

    public static boolean isLax(PolicyAssertion assertion, SecurityPolicyVersion spVersion) {
        if ( !isSecurityPolicyNS(assertion, spVersion)) {
            return false;
        }

        if ( assertion.getName().getLocalPart().equals(Constants.Lax)) {
            return true;
        }
        return false;
    }

    public static boolean isLaxTsFirst(PolicyAssertion assertion, SecurityPolicyVersion spVersion) {
        if ( !isSecurityPolicyNS(assertion, spVersion)) {
            return false;
        }

        if ( assertion.getName().getLocalPart().equals(Constants.LaxTsFirst)) {
            return true;
        }
        return false;
    }

    public static boolean isLaxTsLast(PolicyAssertion assertion, SecurityPolicyVersion spVersion) {
        if ( !isSecurityPolicyNS(assertion, spVersion)) {
            return false;
        }

        if ( assertion.getName().getLocalPart().equals(Constants.LaxTsLast)) {
            return true;
        }
        return false;
    }

    public static boolean isStrict(PolicyAssertion assertion, SecurityPolicyVersion spVersion) {
        if ( !isSecurityPolicyNS(assertion, spVersion)) {
            return false;
        }

        if ( assertion.getName().getLocalPart().equals(Constants.Strict)) {
            return true;
        }
        return false;
    }

    public static boolean isKeyType(PolicyAssertion assertion) {
        if ( !isTrustNS(assertion)) {
            return false;
        }

        if ( assertion.getName().getLocalPart().equals(Constants.KeyType)) {
            return true;
        }

        return false;
    }

    public static boolean isKeySize(PolicyAssertion assertion) {
        if ( !isTrustNS(assertion)) {
            return false;
        }

        if ( assertion.getName().getLocalPart().equals(Constants.KeySize)) {
            return true;
        }

        return false;
    }

    public static boolean isUseKey(PolicyAssertion assertion) {
        if ( !isTrustNS(assertion)) {
            return false;
        }

        if ( assertion.getName().getLocalPart().equals(Constants.UseKey)) {
            return true;
        }

        return false;
    }

    public static boolean isEncryption(PolicyAssertion assertion) {
        if ( !isTrustNS(assertion)) {
            return false;
        }

        if ( assertion.getName().getLocalPart().equals(Constants.Encryption)) {
            return true;
        }
        return false;
    }

    public static boolean isProofEncryption(PolicyAssertion assertion) {
        if ( !isTrustNS(assertion)) {
            return false;
        }

        if ( assertion.getName().getLocalPart().equals(Constants.ProofEncryption)) {
            return true;
        }
        return false;
    }

    public static boolean isLifeTime(PolicyAssertion assertion) {
        if ( !isTrustNS(assertion)) {
            return false;
        }

        if ( assertion.getName().getLocalPart().equals(Constants.Lifetime)) {
            return true;
        }
        return false;
    }

    public static boolean isHeader(PolicyAssertion assertion, SecurityPolicyVersion spVersion) {
        if ( !isSecurityPolicyNS(assertion, spVersion)) {
            return false;
        }
        if ( assertion.getName().getLocalPart().equals(Constants.HEADER)) {
            return true;
        }
        return false;
    }

    public static boolean isRequireKeyIR(PolicyAssertion assertion, SecurityPolicyVersion spVersion) {
        if (!isSecurityPolicyNS(assertion, spVersion)) {
            return false;
        }
        if ( assertion.getName().getLocalPart().equals(Constants.RequireKeyIdentifierReference)) {
            return true;
        }
        return false;
    }

    public static boolean isSignWith(PolicyAssertion assertion) {
        if(!isTrustNS(assertion)){
            return false;
        }
        if(Constants.SignWith.equals(assertion.getName().getLocalPart())){
            return true;
        }
        return false;
    }

    public static boolean isEncryptWith(PolicyAssertion assertion) {
        if(!isTrustNS(assertion)){
            return false;
        }
        if(Constants.EncryptWith.equals(assertion.getName().getLocalPart())){
            return true;
        }
        return false;
    }

    public static boolean isRequestType(PolicyAssertion assertion) {
        if(!isTrustNS(assertion)){
            return false;
        }
        if(Constants.RequestType.equals(assertion.getName().getLocalPart())){
            return true;
        }
        return false;
    }

    public static boolean isSignatureAlgorithm(PolicyAssertion assertion) {
        if(!isTrustNS(assertion)){
            return false;
        }
        if(Constants.SignatureAlgorithm.equals(assertion.getName().getLocalPart())){
            return true;
        }
        return false;
    }

    public static boolean isComputedKeyAlgorithm(PolicyAssertion assertion) {
        if(!isTrustNS(assertion)){
            return false;
        }
        if(Constants.ComputedKeyAlgorithm.equals(assertion.getName().getLocalPart())){
            return true;
        }
        return false;
    }

    public static boolean isCanonicalizationAlgorithm(PolicyAssertion assertion) {
        if(!isTrustNS(assertion)){
            return false;
        }
        if(Constants.CanonicalizationAlgorithm.equals(assertion.getName().getLocalPart())){
            return true;
        }
        return false;
    }

    public static boolean isEncryptionAlgorithm(PolicyAssertion assertion) {
        if(!isTrustNS(assertion)){
            return false;
        }
        if(Constants.EncryptionAlgorithm.equals(assertion.getName().getLocalPart())){
            return true;
        }
        return false;
    }

    public static boolean isAuthenticationType(PolicyAssertion assertion) {
        if(!isTrustNS(assertion)){
            return false;
        }
        if(Constants.AuthenticationType.equals(assertion.getName().getLocalPart())){
            return true;
        }
        return false;
    }

    public static boolean isKeyWrapAlgorithm(PolicyAssertion assertion) {
        if(!Constants.TRUST13_NS.equals(assertion.getName().getNamespaceURI())){
            return false;
        }
        if(Constants.KeyWrapAlgorithm.equals(assertion.getName().getLocalPart())){
            return true;
        }
        return false;
    }

    public static boolean isSC10SecurityContextToken(PolicyAssertion assertion, SecurityPolicyVersion spVersion){
        if ( !isSecurityPolicyNS(assertion, spVersion)) {
            return false;
        }

        if(assertion.getName().getLocalPart().equals(Constants.SC10SecurityContextToken)) {
            return true;
        }
        return false;
    }

    public static boolean isConfigPolicyAssertion(PolicyAssertion assertion){
        String uri = assertion.getName().getNamespaceURI();
        if(Constants.SUN_SECURE_CLIENT_CONVERSATION_POLICY_NS.equals(uri) || Constants.SUN_TRUST_CLIENT_SECURITY_POLICY_NS.equals(uri) ||
                Constants.SUN_SECURE_SERVER_CONVERSATION_POLICY_NS.equals(uri) || Constants.SUN_TRUST_SERVER_SECURITY_POLICY_NS.equals(uri) ||
                Constants.SUN_WSS_SECURITY_CLIENT_POLICY_NS.equals(uri) || Constants.SUN_WSS_SECURITY_SERVER_POLICY_NS.equals(uri) ){
            return true;
        }
        return false;
    }

    public static boolean isTrustTokenType(PolicyAssertion assertion) {
        if(!isTrustNS(assertion)){
            return false;
        }
        if(Constants.TokenType.equals(assertion.getName().getLocalPart())){
            return true;
        }
        return false;
    }

    public static boolean isPortType(PolicyAssertion assertion) {
        if ( !isAddressingNS(assertion)) {
            return false;
        }

        if ( assertion.getName().getLocalPart().equals(Constants.PortType)) {
            return true;
        }
        return false;
    }

    public  static boolean isReferenceParameters(PolicyAssertion assertion) {
        if ( !isAddressingNS(assertion)) {
            return false;
        }

        if ( assertion.getName().getLocalPart().equals(Constants.ReferenceParameters)) {
            return true;
        }
        return false;
    }

    public static boolean isReferenceProperties(PolicyAssertion assertion) {
        if ( !isAddressingNS(assertion)) {
            return false;
        }

        if ( assertion.getName().getLocalPart().equals(Constants.ReferenceProperties)) {
            return true;
        }
        return false;
    }

    public static boolean isServiceName(PolicyAssertion assertion) {
        if ( !isAddressingNS(assertion)) {
            return false;
        }

        if ( assertion.getName().getLocalPart().equals(Constants.ServiceName)) {
            return true;
        }
        return false;
    }

    public static boolean isRequiredElements(PolicyAssertion assertion, SecurityPolicyVersion spVersion){
        if(isSecurityPolicyNS(assertion, spVersion)){
            return false;
        }

        if(assertion.getName().getLocalPart().equals(Constants.RequiredElements)){
            return true;
        }
        return false;
    }

    public static boolean isClaimsElement(PolicyAssertion assertion){
        if(!isTrustNS(assertion)){
            return false;
        }
        if(Constants.Claims.equals(assertion.getName().getLocalPart())){
            return true;
        }
        return false;
    }

    public static boolean isEntropyElement(PolicyAssertion assertion){
        if(!isTrustNS(assertion)){
            return false;
        }
        if(Constants.Entropy.equals(assertion.getName().getLocalPart())){
            return true;
        }
        return false;
    }


    public static boolean hasPassword(PolicyAssertion assertion, SecurityPolicyVersion spVersion){
        if(!isSecurityPolicyNS(assertion, spVersion)){
            return false;
        }

        if(assertion.getName().getLocalPart().equals(Constants.NoPassword)){
            return true;
        }
        return false;
    }

    public static boolean isHashPassword(PolicyAssertion assertion, SecurityPolicyVersion spVersion){
        if(!isSecurityPolicyNS(assertion, spVersion)){
            return false;
        }

        if(assertion.getName().getLocalPart().equals(Constants.HashPassword) &&
                assertion.getName().getNamespaceURI().equals(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri)){
            return true;
        }
        return false;
    }

    public static String randomUUID() {
         UUID uid = UUID.randomUUID();
         String id= "uuid_" + uid;
         return id;
    }

    public static byte[] policyAssertionToBytes(final PolicyAssertion token){
        try{
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            XMLOutputFactory xof = XMLOutputFactory.newInstance();
            XMLStreamWriter writer = xof.createXMLStreamWriter(baos);

            AssertionSet set = AssertionSet.createAssertionSet(Arrays.asList(token));
            Policy policy = Policy.createPolicy(Arrays.asList(set));
            PolicySourceModel sourceModel = ModelGenerator.getGenerator().translate(policy);
            PolicyModelMarshaller pm = PolicyModelMarshaller.getXmlMarshaller(true);
            pm.marshal(sourceModel, writer);
            writer.close();

            return baos.toByteArray();
         }catch (Exception e){
            throw new WebServiceException(e);
        }
    }

    public static Document policyAssertionToDoc(final PolicyAssertion token){
        try{
            byte[] byteArray = policyAssertionToBytes(token);

            DocumentBuilderFactory dbf = WSITXMLFactory.createDocumentBuilderFactory(WSITXMLFactory.DISABLE_SECURE_PROCESSING);
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new ByteArrayInputStream(byteArray));

            return doc;
        }catch (Exception e){
            throw new WebServiceException(e);
        }
    }

    public static SecurityPolicyVersion getSecurityPolicyVersion(String nsUri) {
        SecurityPolicyVersion spVersion= null;
         if(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri.equals(nsUri)){
            spVersion = SecurityPolicyVersion.SECURITYPOLICY200507;
        } else if(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri.equals(nsUri)){
            spVersion = SecurityPolicyVersion.SECURITYPOLICY12NS;
        } else if (SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri.equals(nsUri)) {
            spVersion = SecurityPolicyVersion.SECURITYPOLICY200512;
        }else if (SecurityPolicyVersion.MS_SECURITYPOLICY200507.namespaceUri.equals(nsUri)) {
            spVersion = SecurityPolicyVersion.MS_SECURITYPOLICY200507;
        }
        return spVersion;
    }
}
