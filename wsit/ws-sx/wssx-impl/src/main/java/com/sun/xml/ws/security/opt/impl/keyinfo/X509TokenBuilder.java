/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.opt.impl.keyinfo;

import com.sun.xml.ws.security.opt.api.SecurityElement;
import com.sun.xml.ws.security.opt.api.keyinfo.BinarySecurityToken;
import com.sun.xml.ws.security.opt.api.keyinfo.BuilderResult;
import com.sun.xml.ws.security.opt.api.reference.DirectReference;
import com.sun.xml.ws.security.opt.impl.reference.KeyIdentifier;
import com.sun.xml.ws.security.opt.impl.reference.X509Data;
import com.sun.xml.ws.security.opt.impl.reference.X509IssuerSerial;
import com.sun.xml.wss.XWSSecurityException;
import com.sun.xml.wss.impl.MessageConstants;
import com.sun.xml.wss.impl.misc.SecurityUtil;
import com.sun.xml.wss.impl.policy.mls.AuthenticationTokenPolicy;
import com.sun.xml.ws.security.opt.impl.JAXBFilterProcessingContext;
import com.sun.xml.ws.security.opt.impl.crypto.SSEData;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import com.sun.xml.wss.logging.impl.opt.token.LogStringsMessages;

/**
 *
 * @author K.Venugopal@sun.com
 */
public class X509TokenBuilder extends TokenBuilder {

    AuthenticationTokenPolicy.X509CertificateBinding binding = null;

    /** Creates a new instance of X509TokenBuilder */
    public X509TokenBuilder(JAXBFilterProcessingContext context, AuthenticationTokenPolicy.X509CertificateBinding binding) {
        super(context);
        this.binding = binding;
    }

    /**
     * processes the token and obtain the keys
     * @return BuilderResult
     * @throws com.sun.xml.wss.XWSSecurityException
     */
    @SuppressWarnings("unchecked")
    public BuilderResult process() throws XWSSecurityException {

        String x509id = binding.getUUID();
        if (x509id == null || x509id.equals("")) {
            x509id = context.generateID();
        }
        SecurityUtil.checkIncludeTokenPolicyOpt(context, binding, x509id);

        String referenceType = binding.getReferenceType();
        if (logger.isLoggable(Level.FINEST)) {
            logger.log(Level.FINEST, LogStringsMessages.WSS_1851_REFERENCETYPE_X_509_TOKEN(referenceType));
        }
        BuilderResult result = new BuilderResult();
        if (referenceType.equals("Direct")) {
            BinarySecurityToken bst = createBinarySecurityToken(binding, binding.getX509Certificate());
            if (bst == null) {
                logger.log(Level.SEVERE, LogStringsMessages.WSS_1802_WRONG_TOKENINCLUSION_POLICY(), "creating binary security token failed");
                throw new XWSSecurityException(LogStringsMessages.WSS_1802_WRONG_TOKENINCLUSION_POLICY());
            }
            DirectReference dr = buildDirectReference(bst.getId(), MessageConstants.X509v3_NS);
            buildKeyInfo(dr, binding.getSTRID());
        } else if (referenceType.equals("Identifier")) {
            BinarySecurityToken bst = createBinarySecurityToken(binding, binding.getX509Certificate());
            buildKeyInfoWithKI(binding, MessageConstants.X509SubjectKeyIdentifier_NS);
            try {
                if (binding.getSTRID() != null) {
                    SecurityElement bsToken = elementFactory.createBinarySecurityToken(null, binding.getX509Certificate().getEncoded());
                    SSEData data = new SSEData(bsToken, false, context.getNamespaceContext());
                    context.getSTRTransformCache().put(binding.getSTRID(), data);
                }
            } catch (CertificateEncodingException ce) {
                logger.log(Level.SEVERE, LogStringsMessages.WSS_1814_ERROR_ENCODING_CERTIFICATE(), ce);
                throw new XWSSecurityException(LogStringsMessages.WSS_1814_ERROR_ENCODING_CERTIFICATE(), ce);
            }
        } else if (referenceType.equals(MessageConstants.THUMB_PRINT_TYPE)) {
            BinarySecurityToken bst = createBinarySecurityToken(binding, binding.getX509Certificate());
            KeyIdentifier ki = buildKeyInfoWithKI(binding, MessageConstants.ThumbPrintIdentifier_NS);
            try {
                if (binding.getSTRID() != null) {
                    SecurityElement bsToken = elementFactory.createBinarySecurityToken(null, binding.getX509Certificate().getEncoded());
                    SSEData data = new SSEData(bsToken, false, context.getNamespaceContext());
                    context.getSTRTransformCache().put(binding.getSTRID(), data);
                }
            } catch (CertificateEncodingException ce) {
                logger.log(Level.SEVERE, LogStringsMessages.WSS_1814_ERROR_ENCODING_CERTIFICATE(), ce);
                throw new XWSSecurityException(LogStringsMessages.WSS_1814_ERROR_ENCODING_CERTIFICATE(), ce);
            }
        } else if (referenceType.equals(MessageConstants.X509_ISSUER_TYPE)) {
            BinarySecurityToken bst = createBinarySecurityToken(binding, binding.getX509Certificate());
            X509Certificate xCert = binding.getX509Certificate();
            X509IssuerSerial xis = elementFactory.createX509IssuerSerial(xCert.getIssuerDN().getName(), xCert.getSerialNumber());
            X509Data x509Data = elementFactory.createX509DataWithIssuerSerial(xis);
            buildKeyInfo(x509Data, binding.getSTRID());
            try {
                if (binding.getSTRID() != null) {
                    SecurityElement bsToken = elementFactory.createBinarySecurityToken(null, binding.getX509Certificate().getEncoded());
                    SSEData data = new SSEData(bsToken, false, context.getNamespaceContext());
                    context.getSTRTransformCache().put(binding.getSTRID(), data);
                }
            } catch (CertificateEncodingException ce) {
                logger.log(Level.SEVERE, LogStringsMessages.WSS_1814_ERROR_ENCODING_CERTIFICATE(), ce);
                throw new XWSSecurityException(LogStringsMessages.WSS_1814_ERROR_ENCODING_CERTIFICATE(), ce);
            }
        } else {
            logger.log(Level.SEVERE, LogStringsMessages.WSS_1803_UNSUPPORTED_REFERENCE_TYPE(referenceType));
            throw new XWSSecurityException(LogStringsMessages.WSS_1803_UNSUPPORTED_REFERENCE_TYPE(referenceType));
        }
        result.setKeyInfo(keyInfo);
        return result;
    }
}
