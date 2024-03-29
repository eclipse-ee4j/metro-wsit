/*
 * Copyright (c) 2011, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.impl.policy.verifier;

import com.sun.xml.ws.security.spi.AlternativeSelector;
import com.sun.xml.wss.ProcessingContext;
import com.sun.xml.wss.impl.PolicyTypeUtil;
import com.sun.xml.wss.impl.policy.SecurityPolicy;
import com.sun.xml.wss.impl.policy.mls.MessagePolicy;
import com.sun.xml.wss.impl.policy.mls.WSSPolicy;
import java.util.List;

/**
 * A class which can select specific alternatives of a Username or a SAMLToken appearing
 * as SignedSupportingTokens.
 */
public class UsernameOrSAMLAlternativeSelector implements AlternativeSelector {

    private enum SupportingTokenType {

        USERNAME, SAML, UNKNOWN
    }

    public UsernameOrSAMLAlternativeSelector() {}

    @Override
    public MessagePolicy selectAlternative(ProcessingContext ctx, List<MessagePolicy> alternatives, SecurityPolicy recvdPolicy) {
        //TODO: assert that the number of alternatives is two only
        //it can handle the alternatives as defined in the following oracle security profiles :
        //1. wss11_saml_or_username_token_with_message_protection_service_policy
        //2. OR wss_saml_or_username_token_over_ssl_service_policy
        SupportingTokenType reqMsgTokenType = determineTokenType(recvdPolicy);
        for (MessagePolicy mp : alternatives) {
            SupportingTokenType alternativeTokenType = determineTokenType(mp);
            if (reqMsgTokenType != SupportingTokenType.UNKNOWN && reqMsgTokenType.equals(alternativeTokenType)) {
                return mp;
            }
        }
        return null;
    }

    @Override
    public boolean supportsAlternatives(List<MessagePolicy> alternatives) {
         if (alternatives.size() != 2) {
             return false;
         }
         SupportingTokenType firstAlternativeType = determineTokenType(alternatives.get(0));

         if(firstAlternativeType == SupportingTokenType.UNKNOWN) {
             return false;
         }

         SupportingTokenType secondAlternativeType = determineTokenType(alternatives.get(1));

         if(secondAlternativeType == SupportingTokenType.UNKNOWN) {
             return false;
         }

        return firstAlternativeType != secondAlternativeType;
    }

    private SupportingTokenType determineTokenType(SecurityPolicy recvdPolicy) {
        SupportingTokenType ret = SupportingTokenType.UNKNOWN;
        if (recvdPolicy instanceof MessagePolicy) {
            MessagePolicy pol = (MessagePolicy) recvdPolicy;
            for (int i = 0; i < pol.size(); i++) {
                try {
                    WSSPolicy p = (WSSPolicy) pol.get(i);
                    if (PolicyTypeUtil.usernameTokenBinding(p) || PolicyTypeUtil.usernameTokenBinding(p.getFeatureBinding())) {
                        ret = SupportingTokenType.USERNAME;
                        break;
                    } else if (PolicyTypeUtil.samlTokenPolicy(p) || PolicyTypeUtil.samlTokenPolicy(p.getFeatureBinding())) {
                        ret = SupportingTokenType.SAML;
                        break;
                    }
                } catch (Exception e) {
                    //nothing to do.
                }
            }
        }
        return ret;
    }

}

