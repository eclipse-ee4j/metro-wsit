/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.opt.impl.incoming;

import com.sun.xml.ws.api.message.Header;
import com.sun.xml.ws.api.message.HeaderList;
import com.sun.xml.ws.api.message.MessageHeaders;
import com.sun.xml.ws.security.opt.api.SecurityHeaderElement;
import com.sun.xml.ws.security.opt.impl.JAXBFilterProcessingContext;
import com.sun.xml.wss.ProcessingContext;
import com.sun.xml.wss.XWSSecurityException;
import com.sun.xml.wss.impl.MessageConstants;
import com.sun.xml.wss.impl.PolicyTypeUtil;
import com.sun.xml.wss.impl.policy.mls.SignaturePolicy;
import com.sun.xml.wss.impl.policy.mls.SignatureTarget;
import com.sun.xml.wss.impl.policy.mls.SignatureTarget.Transform;
import com.sun.xml.wss.impl.policy.mls.Target;
import com.sun.xml.wss.impl.policy.mls.WSSPolicy;
import com.sun.xml.wss.impl.policy.verifier.TargetResolver;
import com.sun.xml.wss.logging.LogDomainConstants;
import com.sun.xml.wss.logging.LogStringsMessages;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ashutosh.Shahi@sun.com
 */
public class TargetResolverImpl implements TargetResolver {

    private ProcessingContext ctx = null;
    private StringBuffer tokenList = new StringBuffer();
    private static Logger log = Logger.getLogger(
            LogDomainConstants.WSS_API_DOMAIN,
            LogDomainConstants.WSS_API_DOMAIN_BUNDLE);

    /** Creates a new instance of TargetResolverImpl */
    public TargetResolverImpl(ProcessingContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void resolveAndVerifyTargets(
            List<Target> actualTargets, List<Target> inferredTargets, WSSPolicy actualPolicy)
            throws XWSSecurityException {

        String policyType = PolicyTypeUtil.signaturePolicy(actualPolicy) ? "Signature" : "Encryption";
        boolean isEndorsing = false;

        if (PolicyTypeUtil.signaturePolicy(actualPolicy)) {
            SignaturePolicy.FeatureBinding fp = (SignaturePolicy.FeatureBinding) actualPolicy.getFeatureBinding();
            if (fp.isEndorsingSignature()) {
                isEndorsing = true;
            }
        }

        for (Target actualTarget : actualTargets) {
            if("Signature".equals(policyType) && ((SignatureTarget) actualTarget).isITNever()){
                //ignore resolving the target when the target is S.S or S.E.S.Token target because this breaks oracle interop
                //need to refine it later if something goes wrong
                continue;
            }
            boolean found = false;
            String targetInPolicy = getTargetValue(actualTarget);
            for (Target inferredTarget : inferredTargets) {
                String targetInMessage = getTargetValue(inferredTarget);
                if (targetInPolicy != null && targetInPolicy.equals(targetInMessage)) {
                    found = true;
                    break;
                }
            }
            if (targetInPolicy != null && targetInPolicy.equals("BinarySecurityToken") && !found) {
                if (!containsSTRTransform(actualTarget, inferredTargets)) {
                     log.log(Level.SEVERE, LogStringsMessages.WSS_0206_POLICY_VIOLATION_EXCEPTION());
                     log.log(Level.SEVERE, LogStringsMessages.WSS_0814_POLICY_VERIFICATION_ERROR_MISSING_TARGET(targetInPolicy, policyType));
                     throw new XWSSecurityException("Policy verification error:" +
                            "Missing target " + targetInPolicy + " for " + policyType);
                }
                continue;
            }
            if (!found && targetInPolicy != null) {
                //check if message has the target
                //check if the message has the element

                if (presentInMessage(targetInPolicy)) {
                    log.log(Level.SEVERE, LogStringsMessages.WSS_0206_POLICY_VIOLATION_EXCEPTION());
                    log.log(Level.SEVERE, LogStringsMessages.WSS_0814_POLICY_VERIFICATION_ERROR_MISSING_TARGET(targetInPolicy, policyType));
                    if (isEndorsing) {
                        throw new XWSSecurityException("Policy verification error:" +
                                "Missing target " + targetInPolicy + " for Endorsing " + policyType);
                    } else {
                        throw new XWSSecurityException("Policy verification error:" +
                                "Missing target " + targetInPolicy + " for " + policyType);
                    }

                }
            }
        }
        if ("Signature".equals(policyType)) {
            if (countSTRTransforms(actualTargets,true) != countSTRTransforms(inferredTargets, false)) {
                if (isEndorsing) {
                    throw new XWSSecurityException("Policy verification error:" +
                            "Missing reference for one of { "+ tokenList.toString() + "} for Endorsing " + policyType);
                } else {
                    tokenList.delete(tokenList.lastIndexOf(","),tokenList.length());
                    throw new XWSSecurityException("Policy verification error:" +
                            "Missing reference for one of { "+ tokenList + "}  for " + policyType);
                }
            }
        }
    }

    private boolean containsSTRTransform(Target actualTarget, List<Target> inferredTargets) {
        for (Target inferredTarget : inferredTargets) {
            SignatureTarget st = (SignatureTarget) inferredTarget;
            ArrayList ar = st.getTransforms();
            Iterator it = ar.iterator();
            while (it.hasNext()) {
                SignatureTarget.Transform str = (Transform) it.next();
                if (str.getTransform().equals(MessageConstants.STR_TRANSFORM_URI)) {
                    //if(actualTarget.getValue().equals(st.getValue())){
                    return true;
                //}
                }
            }
        }
        return false;
    }
    private int countSTRTransforms(List<Target> targets, boolean isActualTarget) {
        int strTransformCount = 0;
        for (Target target : targets) {
            if (target instanceof SignatureTarget) {
                SignatureTarget st = (SignatureTarget) target;
                ArrayList ar = st.getTransforms();
                Iterator it = ar.iterator();
                while (it.hasNext()) {
                    SignatureTarget.Transform str = (Transform) it.next();
                    if (str.getTransform().equals(MessageConstants.STR_TRANSFORM_URI)) {
                        strTransformCount++;
                        if(isActualTarget){
                            String localPart = (st.getPolicyQName() != null)?st.getPolicyQName().getLocalPart():"";
                            tokenList.append(localPart);tokenList.append(", ");
                        }
                    }
                }
            }
        }
        return strTransformCount;
    }

    private String getTargetValue(Target target) {
        String targetInPolicy = null;
        if (Target.TARGET_TYPE_VALUE_QNAME.equals(target.getType())) {
            targetInPolicy = target.getQName().getLocalPart();
        } else if (Target.TARGET_TYPE_VALUE_URI.equals(target.getType())) {
            if (target.getPolicyQName() != null) {
                targetInPolicy = target.getPolicyQName().getLocalPart();
            } else {
                String val = target.getValue();
                String id = null;
                if (val.charAt(0) == '#') {
                    id = val.substring(1);
                } else {
                    id = val;
                }
                targetInPolicy = getElementById(id);
                if (targetInPolicy == null && id.startsWith("SAML")) {
                    return "Assertion";
                }
            }
        }

        return targetInPolicy;
    }

    private String getElementById(String id) {
        SecurityContext sc = ((JAXBFilterProcessingContext) ctx).getSecurityContext();

        MessageHeaders headers = sc.getNonSecurityHeaders();
        // look in non-security headers
        // FIXME: RJE -- remove cast when MessageContext support hasHeaders
        if (headers != null && ((HeaderList) headers).size() > 0) {
            Iterator<Header> listItr = headers.getHeaders();
            while (listItr.hasNext()) {
                GenericSecuredHeader header = (GenericSecuredHeader) listItr.next();
                if (header.hasID(id)) {
                    return header.getLocalPart();
                }
            }
        }

        // look in processed headers
        ArrayList processedHeaders = sc.getProcessedSecurityHeaders();
        for (int j = 0; j < processedHeaders.size(); j++) {
            SecurityHeaderElement header = (SecurityHeaderElement) processedHeaders.get(j);
            if (id.equals(header.getId())) {
                return header.getLocalPart();
            }
        }

        // look in buffered headers
        ArrayList bufferedHeaders = sc.getBufferedSecurityHeaders();
        for (int j = 0; j < bufferedHeaders.size(); j++) {
            SecurityHeaderElement header = (SecurityHeaderElement) bufferedHeaders.get(j);
            if (id.equals(header.getId())) {
                return header.getLocalPart();
            }
        }
        return null;
    }

    private boolean presentInMessage(String targetInPolicy) {

        if (MessageConstants.SOAP_BODY_LNAME.equals(targetInPolicy)) {
            return true;
        }

        SecurityContext sc = ((JAXBFilterProcessingContext) ctx).getSecurityContext();

        // FIXME: RJE - Remove cast once MessageContext supports asList(), hasHeaders()
        HeaderList headers = (HeaderList) sc.getNonSecurityHeaders();
        // look in non-security headers
        if (headers != null && headers.size() > 0) {
            Iterator<Header> listItr = headers.listIterator();
            while (listItr.hasNext()) {
                GenericSecuredHeader header = (GenericSecuredHeader) listItr.next();
                if (header != null && header.getLocalPart().equals(targetInPolicy)) {
                    return true;
                }
            }
        }

        // look in processed headers
        ArrayList processedHeaders = sc.getProcessedSecurityHeaders();
        for (int j = 0; j < processedHeaders.size(); j++) {
            SecurityHeaderElement header = (SecurityHeaderElement) processedHeaders.get(j);
            if (header != null && header.getLocalPart().equals(targetInPolicy)) {
                return true;
            }
        }

        // look in buffered headers
        ArrayList bufferedHeaders = sc.getBufferedSecurityHeaders();
        for (int j = 0; j < bufferedHeaders.size(); j++) {
            SecurityHeaderElement header = (SecurityHeaderElement) bufferedHeaders.get(j);
            if (header != null && header.getLocalPart().equals(targetInPolicy)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isTargetPresent(List<Target> actualTargets) {

        for (Target actualTarget : actualTargets) {
            String targetInPolicy = getTargetValue(actualTarget);
            if (presentInMessage(targetInPolicy)) {
                return true;
            }
        }
        return false;
    }
}
