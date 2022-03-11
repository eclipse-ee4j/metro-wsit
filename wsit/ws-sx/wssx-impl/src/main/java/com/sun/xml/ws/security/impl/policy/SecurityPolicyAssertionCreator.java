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

import com.sun.xml.ws.policy.AssertionSet;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.sourcemodel.AssertionData;
import com.sun.xml.ws.policy.spi.AssertionCreationException;
import com.sun.xml.ws.policy.spi.PolicyAssertionCreator;
import com.sun.xml.ws.security.policy.SecurityPolicyVersion;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashSet;
import java.util.logging.Level;

/**
 *
 * @author K.Venugopal@sun.com
 */
public class SecurityPolicyAssertionCreator implements PolicyAssertionCreator{


    private static HashSet<String> implementedAssertions = new HashSet<>();
    private static final String [] nsSupportedList = new String[]{SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,
           SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri,
           "http://schemas.microsoft.com/ws/2005/07/securitypolicy",
           SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri};
    //    Constants.SUN_WSS_SECURITY_CLIENT_POLICY_NS,
    //    Constants.SUN_WSS_SECURITY_SERVER_POLICY_NS,
    //    Constants.SUN_SECURE_CLIENT_CONVERSATION_POLICY_NS,Constants.SUN_SECURE_SERVER_CONVERSATION_POLICY_NS
    //    ,Constants.SUN_TRUST_CLIENT_SECURITY_POLICY_NS,Constants.SUN_TRUST_SERVER_SECURITY_POLICY_NS};
    static{
        implementedAssertions.add(Constants.AlgorithmSuite);
        implementedAssertions.add(Constants.AsymmetricBinding);
        implementedAssertions.add(Constants.Address);
        implementedAssertions.add(Constants.EncryptedElements);
        implementedAssertions.add(Constants.EncryptedParts);
        implementedAssertions.add(Constants.EncryptionToken);
        implementedAssertions.add(Constants.EndorsingSupportingTokens);
        implementedAssertions.add(Constants.EndpointReference);

        implementedAssertions.add(Constants.HEADER);
        implementedAssertions.add(Constants.HttpsToken);
        implementedAssertions.add(Constants.IssuedToken);
        implementedAssertions.add(Constants.Issuer);
        implementedAssertions.add(Constants.InitiatorToken);
        implementedAssertions.add(Constants.InitiatorSignatureToken);
        implementedAssertions.add(Constants.InitiatorEncryptionToken);

        implementedAssertions.add(Constants.KerberosToken);

        implementedAssertions.add(Constants.Lifetime);
        implementedAssertions.add(Constants.Layout);

        implementedAssertions.add(Constants.ProtectionToken);

        implementedAssertions.add(Constants.RecipientToken);
        implementedAssertions.add(Constants.RecipientSignatureToken);
        implementedAssertions.add(Constants.RecipientEncryptionToken);
        implementedAssertions.add(Constants.RelToken);
        implementedAssertions.add(Constants.RequestSecurityTokenTemplate);
        implementedAssertions.add(Constants.RequiredElements);

        implementedAssertions.add(Constants.SamlToken);
        implementedAssertions.add(Constants.SecurityContextToken);
        implementedAssertions.add(Constants.SecureConversationToken);
        implementedAssertions.add(Constants.SignedElements);
        implementedAssertions.add(Constants.SignedSupportingTokens);
        implementedAssertions.add(Constants.SignedEndorsingSupportingTokens);
        implementedAssertions.add(Constants.SignedParts);
        implementedAssertions.add(Constants.SpnegoContextToken);
        implementedAssertions.add(Constants.SupportingTokens);
        implementedAssertions.add(Constants.SignatureToken);
        implementedAssertions.add(Constants.SymmetricBinding);

        implementedAssertions.add(Constants.TransportBinding);
        implementedAssertions.add(Constants.TransportToken);
        implementedAssertions.add(Constants.Trust10);

        implementedAssertions.add(Constants.UsernameToken);
        implementedAssertions.add(Constants.UseKey);

        implementedAssertions.add(Constants.Wss10);
        implementedAssertions.add(Constants.Wss11);
        implementedAssertions.add(Constants.X509Token);
        implementedAssertions.add(Constants.KeyStore);
        implementedAssertions.add(Constants.SessionManagerStore);
        implementedAssertions.add(Constants.TrustStore);
        implementedAssertions.add(Constants.CallbackHandler);
        implementedAssertions.add(Constants.CallbackHandlerConfiguration);
        implementedAssertions.add(Constants.Validator);
        implementedAssertions.add(Constants.ValidatorConfiguration);
        implementedAssertions.add(Constants.CertStore);
        implementedAssertions.add(Constants.KerberosConfig);
        implementedAssertions.add(Constants.RsaToken);

        // WS-SecurityPolicy 1.2 assertions
        implementedAssertions.add(Constants.KeyValueToken);
        implementedAssertions.add(Constants.EncryptedSupportingTokens);
        implementedAssertions.add(Constants.SignedEncryptedSupportingTokens);
        implementedAssertions.add(Constants.SignedEndorsingEncryptedSupportingTokens);
        implementedAssertions.add(Constants.EndorsingEncryptedSupportingTokens);
        implementedAssertions.add(Constants.Trust13);
        implementedAssertions.add(Constants.IssuerName);
        implementedAssertions.add(Constants.Claims);

    };
    /** Creates a new instance of SecurityPolicyAssertionCreator */

    public SecurityPolicyAssertionCreator() {

    }


    @Override
    public String[] getSupportedDomainNamespaceURIs() {
        return nsSupportedList;
    }
    protected Class getClass(AssertionData assertionData) throws AssertionCreationException{
        String className ="";
        try {
            className = assertionData.getName().getLocalPart();
            //made mistake here, we are now in SCF and cannot change the classname
            //will make a change for WSIT 1.1
            if (Constants.CertStore.equals(className)) {
                className = "CertStoreConfig";
            }
            return Class.forName("com.sun.xml.ws.security.impl.policy." + className);
        } catch (ClassNotFoundException ex) {
            if(Constants.logger.isLoggable(Level.SEVERE)){
                Constants.logger.log(Level.SEVERE,LogStringsMessages.SP_0110_ERROR_LOCATING_CLASS(Constants.SECURITY_POLICY_PACKAGE_DIR +className),ex);
            }
            throw new AssertionCreationException(assertionData,ex);
        }
    }

    @Override
    public PolicyAssertion createAssertion(AssertionData assertionData, Collection<PolicyAssertion> nestedAssertions, AssertionSet nestedAlternative, PolicyAssertionCreator policyAssertionCreator) throws AssertionCreationException {
        String localName = assertionData.getName().getLocalPart();
        if(implementedAssertions.contains(localName)){
            Class cl=null;
            cl = getClass(assertionData);
            //            try {
            Constructor cons = null;
            try {

                cons = getConstructor(cl);

                //cl.getConstructor(javax.xml.stream.events.StartElement.class);
            } catch (NoSuchMethodException ex) {
                if(Constants.logger.isLoggable(Level.SEVERE)){
                    Constants.logger.log(Level.SEVERE,LogStringsMessages.SP_0111_ERROR_OBTAINING_CONSTRUCTOR(assertionData.getName()),ex);
                }
                throw new AssertionCreationException(assertionData,ex);
            }catch (SecurityException ex) {
                if(Constants.logger.isLoggable(Level.SEVERE)){
                    Constants.logger.log(Level.SEVERE,LogStringsMessages.SP_0111_ERROR_OBTAINING_CONSTRUCTOR(assertionData.getName()),ex);
                }


                throw new AssertionCreationException(assertionData,ex);
            }
            if(cons != null){
                try {
                    return (PolicyAssertion)cons.newInstance(assertionData,nestedAssertions,nestedAlternative);
                } catch (IllegalArgumentException ex) {
                    if(Constants.logger.isLoggable(Level.SEVERE)){
                        Constants.logger.log(Level.SEVERE,LogStringsMessages.SP_0112_ERROR_INSTANTIATING(assertionData.getName()));
                    }

                    throw new AssertionCreationException(assertionData,ex);
                } catch (InvocationTargetException ex) {
                    if(Constants.logger.isLoggable(Level.SEVERE)){
                        Constants.logger.log(Level.SEVERE,LogStringsMessages.SP_0112_ERROR_INSTANTIATING(assertionData.getName()));
                    }
                    throw new AssertionCreationException(assertionData,ex);
                } catch (InstantiationException ex) {
                    if(Constants.logger.isLoggable(Level.SEVERE)){
                        Constants.logger.log(Level.SEVERE,LogStringsMessages.SP_0112_ERROR_INSTANTIATING(assertionData.getName()));
                    }
                    throw new AssertionCreationException(assertionData,ex);
                } catch (IllegalAccessException ex) {
                    if(Constants.logger.isLoggable(Level.SEVERE)){
                        Constants.logger.log(Level.SEVERE,LogStringsMessages.SP_0112_ERROR_INSTANTIATING(assertionData.getName()));
                    }
                    throw new AssertionCreationException(assertionData,ex);
                }
            }else{
                try{
                    return (PolicyAssertion)cl.newInstance();
                } catch (InstantiationException ex) {
                    if(Constants.logger.isLoggable(Level.SEVERE)){
                        Constants.logger.log(Level.SEVERE,LogStringsMessages.SP_0112_ERROR_INSTANTIATING(assertionData.getName()));
                    }
                    throw new AssertionCreationException(assertionData,ex);
                } catch (IllegalAccessException ex) {
                    if(Constants.logger.isLoggable(Level.SEVERE)){
                        Constants.logger.log(Level.SEVERE,LogStringsMessages.SP_0112_ERROR_INSTANTIATING(assertionData.getName()));
                    }
                    throw new AssertionCreationException(assertionData,ex);
                }
            }


        }
        return policyAssertionCreator.createAssertion(assertionData,nestedAssertions,nestedAlternative,policyAssertionCreator);

    }
    @SuppressWarnings("unchecked")
    private Constructor getConstructor(Class cl) throws NoSuchMethodException{
        return cl.getConstructor(com.sun.xml.ws.policy.sourcemodel.AssertionData.class,java.util.Collection.class,com.sun.xml.ws.policy.AssertionSet.class);
    }

}
