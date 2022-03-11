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

import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.spi.PolicyAssertionValidator;
import com.sun.xml.ws.security.policy.SecurityAssertionValidator;
import com.sun.xml.ws.security.policy.SecurityPolicyVersion;
import java.util.ArrayList;
import javax.xml.namespace.QName;
/**
 *
 * @author K.Venugopal@sun.com
 */
public class SecurityPolicyValidator implements PolicyAssertionValidator{
    private static final ArrayList<QName> supportedAssertions = new ArrayList<>();
    static{
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.CanonicalizationAlgorithm));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.Basic256));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.Basic192));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.Basic128));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.TripleDes));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.Basic256Rsa15));

        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.Basic192Rsa15));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.Basic192Rsa15));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.TripleDesRsa15));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.Basic256Sha256));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.Basic256Rsa15));

        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.Basic192Sha256));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.Basic128Sha256));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.Basic192Sha256));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.TripleDesSha256));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.Basic256Sha256Rsa15));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.Basic192Sha256Rsa15));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.Basic128Sha256Rsa15));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.TripleDesSha256Rsa15));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.InclusiveC14N));
        supportedAssertions.add(new QName(Constants.SUN_WSS_SECURITY_SERVER_POLICY_NS, Constants.InclusiveC14NWithComments));
        supportedAssertions.add(new QName(Constants.SUN_WSS_SECURITY_SERVER_POLICY_NS, Constants.ExclusiveC14NWithComments));
        //     supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,SoapNormalization10));

        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.STRTransform10));
        //supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,XPath10));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.XPathFilter20));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.Strict));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.Lax));

        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.LaxTsFirst));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.LaxTsLast));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.IncludeTimestamp));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.EncryptBeforeSigning));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.EncryptSignature));

        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.ProtectTokens));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.OnlySignEntireHeadersAndBody));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.Body));
        //supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,Header));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.XPath));

        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.WssUsernameToken10));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.WssUsernameToken11));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.Issuer));

        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.RequestSecurityTokenTemplate));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.RequireDerivedKeys));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.RequireExternalReference));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.RequireInternalReference));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.RequireKeyIdentifierReference));

        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.RequireIssuerSerialReference));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.RequireEmbeddedTokenReference));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.RequireThumbprintReference));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.WssX509V1Token10));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.WssX509V3Token10));

        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.WssX509Pkcs7Token10));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.WssX509PkiPathV1Token10));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.WssX509V1Token11));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.WssX509V3Token11));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.WssX509Pkcs7Token11));

        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.WssX509PkiPathV1Token11));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.WssKerberosV5ApReqToken11));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.WssGssKerberosV5ApReqToken11));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.SC10SecurityContextToken));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.WssSamlV10Token10));

        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.WssSamlV11Token10));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.WssSamlV10Token11));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.WssSamlV11Token11));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.WssSamlV20Token11));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.WssRelV10Token10));

        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.WssRelV20Token10));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.WssRelV10Token11));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.WssRelV20Token11));
        //supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,X509V3Token));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.SupportingTokens));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.SignedSupportingTokens));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.EndorsingSupportingTokens));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.SignedEndorsingSupportingTokens));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.MustSupportRefKeyIdentifier));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.MustSupportRefIssuerSerial));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.MustSupportRefExternalURI));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.MustSupportRefEmbeddedToken));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.MustSupportRefKeyIdentifier));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.MustSupportRefIssuerSerial));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.MustSupportRefExternalURI));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.MustSupportRefEmbeddedToken));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.MustSupportRefThumbprint));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.MustSupportRefEncryptedKey));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.MustSupportClientChallenge));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.MustSupportServerChallenge));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.RequireClientEntropy));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.RequireServerEntropy));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.MustSupportIssuedTokens));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, Constants.NoPassword));

        // SecurityPolicy 1.2 assertions
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.CanonicalizationAlgorithm));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.Basic256));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.Basic192));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.Basic128));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.TripleDes));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.Basic256Rsa15));

        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.Basic192Rsa15));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.Basic192Rsa15));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.TripleDesRsa15));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.Basic256Sha256));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.Basic256Rsa15));

        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.Basic192Sha256));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.Basic128Sha256));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.Basic192Sha256));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.TripleDesSha256));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.Basic256Sha256Rsa15));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.Basic192Sha256Rsa15));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.Basic128Sha256Rsa15));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.TripleDesSha256Rsa15));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.InclusiveC14N));
        //     supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri,SoapNormalization10));

        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.STRTransform10));
        //supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri,XPath10));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.XPathFilter20));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.Strict));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.Lax));

        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.LaxTsFirst));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.LaxTsLast));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.IncludeTimestamp));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.EncryptBeforeSigning));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.EncryptSignature));

        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.ProtectTokens));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.OnlySignEntireHeadersAndBody));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.Body));
        //supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri,Header));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.XPath));

        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.WssUsernameToken10));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.WssUsernameToken11));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.Issuer));

        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.RequestSecurityTokenTemplate));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.RequireDerivedKeys));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.RequireExternalReference));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.RequireInternalReference));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.RequireKeyIdentifierReference));

        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.RequireIssuerSerialReference));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.RequireEmbeddedTokenReference));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.RequireThumbprintReference));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.WssX509V1Token10));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.WssX509V3Token10));

        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.WssX509Pkcs7Token10));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.WssX509PkiPathV1Token10));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.WssX509V1Token11));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.WssX509V3Token11));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.WssX509Pkcs7Token11));

        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.WssX509PkiPathV1Token11));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.WssKerberosV5ApReqToken11));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.WssGssKerberosV5ApReqToken11));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.SC10SecurityContextToken));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.WssSamlV10Token10));

        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.WssSamlV11Token10));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.WssSamlV10Token11));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.WssSamlV11Token11));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.WssSamlV20Token11));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.WssRelV10Token10));

        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.WssRelV20Token10));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.WssRelV10Token11));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.WssRelV20Token11));
        //supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri,X509V3Token));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.SupportingTokens));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.SignedSupportingTokens));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.EndorsingSupportingTokens));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.SignedEndorsingSupportingTokens));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.EncryptedSupportingTokens));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.SignedEncryptedSupportingTokens));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.EndorsingEncryptedSupportingTokens));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.SignedEndorsingEncryptedSupportingTokens));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.MustSupportRefKeyIdentifier));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.MustSupportRefIssuerSerial));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.MustSupportRefExternalURI));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.MustSupportRefEmbeddedToken));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.MustSupportRefKeyIdentifier));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.MustSupportRefIssuerSerial));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.MustSupportRefExternalURI));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.MustSupportRefEmbeddedToken));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.MustSupportRefThumbprint));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.MustSupportRefEncryptedKey));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.MustSupportClientChallenge));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.MustSupportServerChallenge));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.RequireClientEntropy));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.RequireServerEntropy));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.MustSupportIssuedTokens));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.NoPassword));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.RequireClientCertificate));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.HttpBasicAuthentication));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.HttpDigestAuthentication));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.RequireRequestSecurityTokenCollection));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.RequireAppliesTo));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, Constants.RsaKeyValue));

        //Security Policy 200512 : ADDED for Nov 07 Plugfest
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.CanonicalizationAlgorithm));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.Basic256));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.Basic192));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.Basic128));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.TripleDes));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.Basic256Rsa15));

        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.Basic192Rsa15));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.Basic192Rsa15));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.TripleDesRsa15));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.Basic256Sha256));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.Basic256Rsa15));

        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.Basic192Sha256));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.Basic128Sha256));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.Basic192Sha256));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.TripleDesSha256));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.Basic256Sha256Rsa15));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.Basic192Sha256Rsa15));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.Basic128Sha256Rsa15));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.TripleDesSha256Rsa15));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.InclusiveC14N));

        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.STRTransform10));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.XPathFilter20));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.Strict));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.Lax));

        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.LaxTsFirst));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.LaxTsLast));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.IncludeTimestamp));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.EncryptBeforeSigning));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.EncryptSignature));

        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.ProtectTokens));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.OnlySignEntireHeadersAndBody));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.Body));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.XPath));

        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.WssUsernameToken10));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.WssUsernameToken11));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.Issuer));

        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.RequestSecurityTokenTemplate));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.RequireDerivedKeys));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.RequireExternalReference));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.RequireInternalReference));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.RequireKeyIdentifierReference));

        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.RequireIssuerSerialReference));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.RequireEmbeddedTokenReference));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.RequireThumbprintReference));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.WssX509V1Token10));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.WssX509V3Token10));

        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.WssX509Pkcs7Token10));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.WssX509PkiPathV1Token10));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.WssX509V1Token11));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.WssX509V3Token11));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.WssX509Pkcs7Token11));

        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.WssX509PkiPathV1Token11));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.WssKerberosV5ApReqToken11));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.WssGssKerberosV5ApReqToken11));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.SC10SecurityContextToken));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.WssSamlV10Token10));

        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.WssSamlV11Token10));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.WssSamlV10Token11));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.WssSamlV11Token11));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.WssSamlV20Token11));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.WssRelV10Token10));

        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.WssRelV20Token10));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.WssRelV10Token11));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.WssRelV20Token11));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.SupportingTokens));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.SignedSupportingTokens));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.EndorsingSupportingTokens));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.SignedEndorsingSupportingTokens));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.MustSupportRefKeyIdentifier));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.MustSupportRefIssuerSerial));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.MustSupportRefExternalURI));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.MustSupportRefEmbeddedToken));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.MustSupportRefKeyIdentifier));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.MustSupportRefIssuerSerial));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.MustSupportRefExternalURI));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.MustSupportRefEmbeddedToken));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.MustSupportRefThumbprint));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.MustSupportRefEncryptedKey));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.MustSupportClientChallenge));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.MustSupportServerChallenge));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.RequireClientEntropy));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.RequireServerEntropy));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.MustSupportIssuedTokens));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri, Constants.NoPassword));
        supportedAssertions.add(new QName("http://docs.oasis-open.org/ws-sx/ws-securitypolicy/200802","Created"));
        supportedAssertions.add(new QName("http://docs.oasis-open.org/ws-sx/ws-securitypolicy/200802","Nonce"));
        //-----------------------------------------------------------

        // Trust10 Assertions
        supportedAssertions.add(new QName(Constants.TRUST_NS, Constants.RequestSecurityToken));
        supportedAssertions.add(new QName(Constants.TRUST_NS, Constants.RequestType));
        supportedAssertions.add(new QName(Constants.TRUST_NS, Constants.TokenType));
        supportedAssertions.add(new QName(Constants.TRUST_NS, Constants.AuthenticationType));
        supportedAssertions.add(new QName(Constants.TRUST_NS, Constants.OnBehalfOf));
        supportedAssertions.add(new QName(Constants.TRUST_NS, Constants.KeyType));
        supportedAssertions.add(new QName(Constants.TRUST_NS, Constants.KeySize));
        supportedAssertions.add(new QName(Constants.TRUST_NS, Constants.SignatureAlgorithm));
        supportedAssertions.add(new QName(Constants.TRUST_NS, Constants.EncryptionAlgorithm));
        supportedAssertions.add(new QName(Constants.TRUST_NS, Constants.CanonicalizationAlgorithm));
        supportedAssertions.add(new QName(Constants.TRUST_NS, Constants.ComputedKeyAlgorithm));
        supportedAssertions.add(new QName(Constants.TRUST_NS, Constants.Encryption));
        supportedAssertions.add(new QName(Constants.TRUST_NS, Constants.ProofEncryption));
        supportedAssertions.add(new QName(Constants.TRUST_NS, Constants.UseKey));
        supportedAssertions.add(new QName(Constants.TRUST_NS, Constants.SignWith));
        supportedAssertions.add(new QName(Constants.TRUST_NS, Constants.EncryptWith));

        //Trust13 Assertions
        supportedAssertions.add(new QName(Constants.TRUST13_NS, Constants.RequestSecurityToken));
        supportedAssertions.add(new QName(Constants.TRUST13_NS, Constants.RequestType));
        supportedAssertions.add(new QName(Constants.TRUST13_NS, Constants.TokenType));
        supportedAssertions.add(new QName(Constants.TRUST13_NS, Constants.AuthenticationType));
        supportedAssertions.add(new QName(Constants.TRUST13_NS, Constants.OnBehalfOf));
        supportedAssertions.add(new QName(Constants.TRUST13_NS, Constants.KeyType));
        supportedAssertions.add(new QName(Constants.TRUST13_NS, Constants.KeySize));
        supportedAssertions.add(new QName(Constants.TRUST13_NS, Constants.SignatureAlgorithm));
        supportedAssertions.add(new QName(Constants.TRUST13_NS, Constants.EncryptionAlgorithm));
        supportedAssertions.add(new QName(Constants.TRUST13_NS, Constants.CanonicalizationAlgorithm));
        supportedAssertions.add(new QName(Constants.TRUST13_NS, Constants.ComputedKeyAlgorithm));
        supportedAssertions.add(new QName(Constants.TRUST13_NS, Constants.Encryption));
        supportedAssertions.add(new QName(Constants.TRUST13_NS, Constants.ProofEncryption));
        supportedAssertions.add(new QName(Constants.TRUST13_NS, Constants.UseKey));
        supportedAssertions.add(new QName(Constants.TRUST13_NS, Constants.SignWith));
        supportedAssertions.add(new QName(Constants.TRUST13_NS, Constants.EncryptWith));

        supportedAssertions.add(new QName(Constants.SUN_WSS_SECURITY_SERVER_POLICY_NS,"EnableEPRIdentity"));
        supportedAssertions.add(new QName("http://schemas.sun.com/2006/03/wss/server","EncSCCancel"));
        supportedAssertions.add(new QName("http://schemas.sun.com/2006/03/wss/client","EncSCCancel"));
        supportedAssertions.add(new QName(Constants.SUN_WSS_SECURITY_SERVER_POLICY_NS,"DisableStreamingSecurity"));
        supportedAssertions.add(new QName(Constants.SUN_WSS_SECURITY_CLIENT_POLICY_NS,"DisableStreamingSecurity"));
        supportedAssertions.add(new QName(Constants.SUN_WSS_SECURITY_SERVER_POLICY_NS,"DisableTimestampSigning"));
        supportedAssertions.add(new QName(Constants.SUN_WSS_SECURITY_CLIENT_POLICY_NS,"DisableTimestampSigning"));
        supportedAssertions.add(new QName(Constants.SUN_WSS_SECURITY_SERVER_POLICY_NS,"EncryptHeaderContent"));
        supportedAssertions.add(new QName(Constants.SUN_WSS_SECURITY_CLIENT_POLICY_NS,"EncryptHeaderContent"));
        supportedAssertions.add(new QName(Constants.SUN_WSS_SECURITY_SERVER_POLICY_NS,"EncryptRMLifecycleMessage"));
        supportedAssertions.add(new QName(Constants.SUN_WSS_SECURITY_CLIENT_POLICY_NS,"EncryptRMLifecycleMessage"));
        supportedAssertions.add(new QName(Constants.SUN_WSS_SECURITY_SERVER_POLICY_NS,"DisableInclusivePrefixList"));
        supportedAssertions.add(new QName(Constants.SUN_WSS_SECURITY_CLIENT_POLICY_NS,"DisableInclusivePrefixList"));
        supportedAssertions.add(new QName(Constants.SUN_WSS_SECURITY_SERVER_POLICY_NS,"DisablePayloadBuffering"));
        supportedAssertions.add(new QName(Constants.SUN_WSS_SECURITY_CLIENT_POLICY_NS,"DisablePayloadBuffering"));
        supportedAssertions.add(new QName(Constants.SUN_WSS_SECURITY_SERVER_POLICY_NS,"AllowMissingTimestamp"));
        supportedAssertions.add(new QName(Constants.SUN_WSS_SECURITY_CLIENT_POLICY_NS,"AllowMissingTimestamp"));
        supportedAssertions.add(new QName(Constants.SUN_WSS_SECURITY_SERVER_POLICY_NS,"UnsetSecurityMUValue"));
        supportedAssertions.add(new QName(Constants.SUN_WSS_SECURITY_CLIENT_POLICY_NS,"UnsetSecurityMUValue"));
        // newly added by M.P.
        supportedAssertions.add(new QName(Constants.SUN_WSS_SECURITY_SERVER_POLICY_NS,"KeyStore"));
        supportedAssertions.add(new QName(Constants.SUN_WSS_SECURITY_SERVER_POLICY_NS,"TrustStore"));

        supportedAssertions.add(new QName(Constants.SUN_WSS_SECURITY_CLIENT_POLICY_NS,"KeyStore"));
        supportedAssertions.add(new QName(Constants.SUN_WSS_SECURITY_CLIENT_POLICY_NS,"TrustStore"));

        supportedAssertions.add(new QName(Constants.SUN_WSS_SECURITY_SERVER_POLICY_NS,"SessionManagerStore"));

        // Kerberos information from custom assertions
        supportedAssertions.add(new QName(Constants.SUN_WSS_SECURITY_SERVER_POLICY_NS,"KerberosConfig"));
        supportedAssertions.add(new QName(Constants.SUN_WSS_SECURITY_CLIENT_POLICY_NS,"KerberosConfig"));

        supportedAssertions.add(new QName(Constants.SUN_SECURE_CLIENT_CONVERSATION_POLICY_NS,"SCClientConfiguration"));
        supportedAssertions.add(new QName(Constants.SUN_SECURE_SERVER_CONVERSATION_POLICY_NS,"SCConfiguration"));

        supportedAssertions.add(new QName(Constants.SUN_TRUST_CLIENT_SECURITY_POLICY_NS,"PreconfiguredSTS"));
        supportedAssertions.add(new QName(Constants.SUN_TRUST_SERVER_SECURITY_POLICY_NS,"STSConfiguration"));

        supportedAssertions.add(new QName(Constants.SUN_WSS_SECURITY_CLIENT_POLICY_NS,Constants.CertStore));
        supportedAssertions.add(new QName(Constants.SUN_WSS_SECURITY_SERVER_POLICY_NS,Constants.CertStore));
        supportedAssertions.add(new QName(Constants.SUN_WSS_SECURITY_CLIENT_POLICY_NS,Constants.BSP10));
        supportedAssertions.add(new QName(Constants.SUN_WSS_SECURITY_SERVER_POLICY_NS,Constants.BSP10));

        // Identity Selector Interoproperability Profile
        supportedAssertions.add(new QName("http://schemas.xmlsoap.org/ws/2005/05/identity", "RequireFederatedIdentityProvisioning"));
    }

    /** Creates a new instance of SecurityPolicyValidator. To be used by appropriate service finder */
    public SecurityPolicyValidator() {
    }

    @Override
    public Fitness validateClientSide(PolicyAssertion policyAssertion) {
        String uri = policyAssertion.getName().getNamespaceURI();

        if(uri.equals(Constants.SUN_WSS_SECURITY_SERVER_POLICY_NS) || uri.equals(Constants.SUN_TRUST_SERVER_SECURITY_POLICY_NS)){
            return Fitness.UNSUPPORTED;
        }

        if (policyAssertion instanceof SecurityAssertionValidator) {
            SecurityAssertionValidator.AssertionFitness fitness =((SecurityAssertionValidator)policyAssertion).validate(false);
            if(fitness == SecurityAssertionValidator.AssertionFitness.IS_VALID){
                return Fitness.SUPPORTED;
            }else {
                return Fitness.UNSUPPORTED;
            }

            //return ((SecurityAssertionValidator)policyAssertion).validate() ? Fitness.SUPPORTED : Fitness.UNSUPPORTED;
        } else if (supportedAssertions.contains(policyAssertion.getName())) {
            return Fitness.SUPPORTED;
        } else {
            return Fitness.UNKNOWN;
        }
    }

    @Override
    public Fitness validateServerSide(PolicyAssertion policyAssertion) {
        String uri = policyAssertion.getName().getNamespaceURI();

        if(uri.equals(Constants.SUN_WSS_SECURITY_CLIENT_POLICY_NS)
                || uri.equals(Constants.SUN_SECURE_CLIENT_CONVERSATION_POLICY_NS) || uri.equals(Constants.SUN_TRUST_CLIENT_SECURITY_POLICY_NS)){
            return Fitness.UNSUPPORTED;
        }

        if (policyAssertion instanceof SecurityAssertionValidator) {
            return (((SecurityAssertionValidator)policyAssertion).validate(true) == SecurityAssertionValidator.AssertionFitness.IS_VALID )? Fitness.SUPPORTED : Fitness.UNSUPPORTED;
        } else if (supportedAssertions.contains(policyAssertion.getName())) {
            return Fitness.SUPPORTED;
        } else {
            return Fitness.UNKNOWN;
        }
    }

    @Override
    public String[] declareSupportedDomains() {
        return new String[] {
            SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,
            SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri,
            SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri,
                Constants.TRUST_NS,
                Constants.SUN_WSS_SECURITY_CLIENT_POLICY_NS,
                Constants.SUN_WSS_SECURITY_SERVER_POLICY_NS,
                Constants.SUN_SECURE_CLIENT_CONVERSATION_POLICY_NS,

        };
    }
}
