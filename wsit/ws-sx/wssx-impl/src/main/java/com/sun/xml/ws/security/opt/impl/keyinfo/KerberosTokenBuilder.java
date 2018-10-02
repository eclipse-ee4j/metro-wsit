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

import com.sun.xml.ws.security.opt.api.keyinfo.BuilderResult;
import com.sun.xml.ws.security.opt.impl.JAXBFilterProcessingContext;
import com.sun.xml.ws.security.opt.impl.crypto.OctectStreamData;
import com.sun.xml.wss.XWSSecurityException;
import com.sun.xml.wss.impl.MessageConstants;
import com.sun.xml.wss.impl.policy.mls.AuthenticationTokenPolicy;
import com.sun.xml.ws.security.opt.api.keyinfo.BinarySecurityToken;
import com.sun.xml.ws.security.opt.api.reference.DirectReference;
import com.sun.xml.wss.logging.impl.opt.token.LogStringsMessages;
import java.util.logging.Level;

/**
 *
 * @author ashutosh.shahi@sun.com
 */
public class KerberosTokenBuilder extends TokenBuilder {
    
    AuthenticationTokenPolicy.KerberosTokenBinding binding = null;
    
    public KerberosTokenBuilder(JAXBFilterProcessingContext context,
            AuthenticationTokenPolicy.KerberosTokenBinding binding) {
        super(context);
        this.binding = binding;
    }
     /**
     *
     * @return  BuilderResult
     * @throws com.sun.xml.wss.XWSSecurityException
     */
    @SuppressWarnings("unchecked")
    public BuilderResult process() throws XWSSecurityException {
        /*String id = binding.getUUID();
        if(id == null || id.equals("")){
        id = context.generateID();
        }*/
        
        setIncludeTokenPolicy();
        
        String referenceType = binding.getReferenceType();
        if(logger.isLoggable(Level.FINEST)){
            logger.log(Level.FINEST, LogStringsMessages.WSS_1853_REFERENCETYPE_KERBEROS_TOKEN(referenceType));
        }
        BuilderResult result = new BuilderResult();
        
        if(referenceType.equals(MessageConstants.DIRECT_REFERENCE_TYPE)){
            BinarySecurityToken bst = createKerberosBST(binding, binding.getTokenValue());
            if(bst == null){
                logger.log(Level.SEVERE, LogStringsMessages.WSS_1802_WRONG_TOKENINCLUSION_POLICY());
                throw new XWSSecurityException(LogStringsMessages.WSS_1802_WRONG_TOKENINCLUSION_POLICY());
            }
            DirectReference dr = buildDirectReference(bst.getId(), MessageConstants.KERBEROS_V5_GSS_APREQ);
            buildKeyInfo(dr,binding.getSTRID());
        } else if(referenceType.equals(MessageConstants.KEY_INDETIFIER_TYPE)){
            BinarySecurityToken bst = createKerberosBST(binding,binding.getTokenValue());
            buildKeyInfoWithKIKerberos(binding, MessageConstants.KERBEROS_v5_APREQ_IDENTIFIER);
            if(binding.getSTRID() != null){
                OctectStreamData osd = new OctectStreamData(new String(binding.getTokenValue()));
                context.getElementCache().put(binding.getSTRID(),osd);
            }
        } else{
            logger.log(Level.SEVERE, LogStringsMessages.WSS_1803_UNSUPPORTED_REFERENCE_TYPE(referenceType));
            throw new XWSSecurityException(LogStringsMessages.WSS_1803_UNSUPPORTED_REFERENCE_TYPE(referenceType));
        }
        result.setKeyInfo(keyInfo);
        return result;
    }
    /**
     * sets the include token policy reference type in the binding
     * @throws com.sun.xml.wss.XWSSecurityException
     */
    @SuppressWarnings("static-access")
    private void setIncludeTokenPolicy() throws XWSSecurityException{
        // no referencetype adjustment if it is not WS-SecurityPolicy
        if(!binding.policyTokenWasSet()){
            return;
        }
        String itVersion = binding.getIncludeToken();
        if(binding.INCLUDE_ALWAYS.equals(itVersion) 
                || binding.INCLUDE_ALWAYS_TO_RECIPIENT.equals(itVersion)
                || binding.INCLUDE_ALWAYS_VER2.equals(itVersion)
                || binding.INCLUDE_ALWAYS_TO_RECIPIENT_VER2.equals(itVersion)){
            // This should never happen as Always and AlwaysToRecipient 
            // are not allowed for Kerberos Tokens
            logger.log(Level.SEVERE, LogStringsMessages.WSS_1822_KERBEROS_ALWAYS_NOTALLOWED());
            throw new XWSSecurityException(LogStringsMessages.WSS_1822_KERBEROS_ALWAYS_NOTALLOWED());
        } else if(binding.INCLUDE_NEVER.equals(itVersion) ||
               binding.INCLUDE_NEVER_VER2.equals(itVersion) ){
            binding.setReferenceType(MessageConstants.KEY_INDETIFIER_TYPE);
        } else if(binding.INCLUDE_ONCE.equals(itVersion)
                || binding.INCLUDE_ONCE_VER2.equals(itVersion)){
            binding.setReferenceType(MessageConstants.DIRECT_REFERENCE_TYPE);
        }
    }
    
}
