/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
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
import com.sun.xml.ws.security.opt.impl.reference.DirectReference;
import com.sun.xml.ws.security.opt.impl.tokens.UsernameToken;
import com.sun.xml.wss.XWSSecurityException;
import com.sun.xml.wss.impl.MessageConstants;
import com.sun.xml.wss.impl.misc.SecurityUtil;
import com.sun.xml.wss.impl.policy.mls.AuthenticationTokenPolicy;
import java.util.logging.Level;
import com.sun.xml.wss.logging.impl.opt.token.LogStringsMessages;

/**
 *
 * @author suresh
 */
public class UsernameTokenBuilder extends TokenBuilder {

    AuthenticationTokenPolicy.UsernameTokenBinding binding = null;

    public UsernameTokenBuilder(JAXBFilterProcessingContext context, AuthenticationTokenPolicy.UsernameTokenBinding binding) {
        super(context);
        this.binding = binding;
    }
    /**
     * processes the token ,builds keyinfo and sets it in BuilderResult
     * @return BuilderResult
     */
    @Override
    public BuilderResult process() throws XWSSecurityException {
        String untokenId = binding.getUUID();
        if (untokenId == null || untokenId.equals("")) {
            untokenId = context.generateID();
        }
        SecurityUtil.checkIncludeTokenPolicyOpt(context, binding, untokenId);
        String referenceType = binding.getReferenceType();
        BuilderResult result = new BuilderResult();
        if (MessageConstants.DIRECT_REFERENCE_TYPE.equals(referenceType)) {
            UsernameToken unToken = createUsernameToken(binding, binding.getUsernameToken());
            if (unToken == null) {
                logger.log(Level.SEVERE, LogStringsMessages.WSS_1856_NULL_USERNAMETOKEN());
                throw new XWSSecurityException("Username Token is NULL");
            }
            DirectReference dr = buildDirectReference(unToken.getId(), MessageConstants.USERNAME_STR_REFERENCE_NS);
            buildKeyInfo(dr, binding.getSTRID());
        }
        result.setKeyInfo(keyInfo);
        return result;
    }
}
