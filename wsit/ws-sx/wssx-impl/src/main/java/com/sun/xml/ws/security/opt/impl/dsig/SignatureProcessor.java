/*
 * Copyright (c) 2010, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * SignatureProcessor.java
 *
 * Created on August 10, 2006, 2:56 PM
 */

package com.sun.xml.ws.security.opt.impl.dsig;

import com.sun.xml.ws.security.opt.api.keyinfo.BuilderResult;
import com.sun.xml.ws.security.opt.impl.util.NamespaceAndPrefixMapper;
import com.sun.xml.ws.security.opt.impl.util.NamespaceContextEx;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import com.sun.xml.ws.security.opt.crypto.jaxb.JAXBSignContext;
import com.sun.xml.wss.impl.policy.mls.SignaturePolicy;
import com.sun.xml.wss.impl.policy.mls.WSSPolicy;
import com.sun.xml.wss.logging.LogDomainConstants;
import com.sun.xml.wss.logging.impl.opt.signature.LogStringsMessages;
import com.sun.xml.wss.XWSSecurityException;
import com.sun.xml.wss.impl.MessageConstants;
import com.sun.xml.wss.impl.misc.Base64;
import com.sun.xml.ws.security.opt.impl.JAXBFilterProcessingContext;
import com.sun.xml.ws.security.opt.impl.outgoing.SecurityHeader;

import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.XMLSignature;
import java.security.Key;
import java.util.List;
import java.util.ArrayList;


/**
 *
 * @author Ashutosh.Shahi@sun.com
 */

public class SignatureProcessor {

    private static final Logger logger = Logger.getLogger(LogDomainConstants.IMPL_OPT_SIGNATURE_DOMAIN,
            LogDomainConstants.IMPL_OPT_SIGNATURE_DOMAIN_BUNDLE);

    /** Creates a new instance of SignatureProcessor */
    public SignatureProcessor() {
    }

    /**
     *
     * performs the signature
     * @param context JAXBFilterProcessingContext
     * @return errorCode
     */
    @SuppressWarnings("unchecked")
    public static int sign(JAXBFilterProcessingContext context) throws XWSSecurityException {
        try{
            SignaturePolicy signaturePolicy  = (SignaturePolicy)context.getSecurityPolicy();
            ((NamespaceContextEx)context.getNamespaceContext()).addSignatureNS();
            WSSPolicy keyBinding = (WSSPolicy)signaturePolicy.getKeyBinding();
            if(logger.isLoggable(Level.FINEST)){
                logger.log(Level.FINEST, "KeyBinding is "+keyBinding);
            }

            Key signingKey = null;

            SignatureElementFactory signFactory = new SignatureElementFactory();

            KeyInfo keyInfo = null;
            SecurityHeader securityHeader = context.getSecurityHeader();

            //Get the Signing key and KeyInfo from TokenProcessor
            TokenProcessor tokenProcessor = new TokenProcessor(signaturePolicy, context);
            BuilderResult builderResult = tokenProcessor.process();
            signingKey = builderResult.getDataProtectionKey();
            keyInfo = builderResult.getKeyInfo();

            if (keyInfo != null || !keyBinding.isOptional()){
                SignedInfo signedInfo = signFactory.constructSignedInfo(context);
                JAXBSignContext signContext = new JAXBSignContext(signingKey);
                signContext.setURIDereferencer(DSigResolver.getInstance());
                XMLSignature signature = signFactory.constructSignature(signedInfo, keyInfo, signaturePolicy.getUUID());
                signContext.put(MessageConstants.WSS_PROCESSING_CONTEXT, context);
                NamespaceAndPrefixMapper npMapper = new NamespaceAndPrefixMapper(context.getNamespaceContext(), context.getDisableIncPrefix());
                signContext.put(NamespaceAndPrefixMapper.NS_PREFIX_MAPPER, npMapper);
                signContext.putNamespacePrefix(MessageConstants.DSIG_NS, MessageConstants.DSIG_PREFIX);
                signature.sign(signContext);

                JAXBSignatureHeaderElement jaxBSign = new JAXBSignatureHeaderElement((com.sun.xml.ws.security.opt.crypto.dsig.Signature)signature,context.getSOAPVersion());
                securityHeader.add(jaxBSign);

                //For SignatureConfirmation
                List scList = (ArrayList)context.getExtraneousProperty("SignatureConfirmation");
                if(scList != null){
                    scList.add(Base64.encode(signature.getSignatureValue().getValue()));
                }
            }
            //End SignatureConfirmation specific code

        } catch(XWSSecurityException xe){
            logger.log(Level.SEVERE, LogStringsMessages.WSS_1701_SIGN_FAILED(), xe);
            throw xe;
        } catch(Exception ex){
            logger.log(Level.SEVERE, LogStringsMessages.WSS_1701_SIGN_FAILED(), ex);
            throw new XWSSecurityException(ex);
        }
        return 0;
    }

}
