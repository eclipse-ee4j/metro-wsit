/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.impl;

import com.sun.xml.wss.ProcessingContext;
import com.sun.xml.wss.WSITXMLFactory;
import com.sun.xml.wss.XWSSecurityException;
import com.sun.xml.wss.core.SecurityHeader;
import com.sun.xml.wss.impl.policy.mls.SignaturePolicy;
import com.sun.xml.wss.impl.policy.mls.Target;
import com.sun.xml.wss.impl.policy.mls.WSSPolicy;
import com.sun.xml.wss.impl.policy.verifier.TargetResolver;
import com.sun.xml.wss.logging.LogDomainConstants;
import com.sun.xml.wss.logging.LogStringsMessages;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author Ashutosh.Shahi@sun.com
 */
public class TargetResolverImpl implements TargetResolver{
    private ProcessingContext ctx = null;
    private FilterProcessingContext fpContext = null;
    private static Logger log = Logger.getLogger(
            LogDomainConstants.WSS_API_DOMAIN,
            LogDomainConstants.WSS_API_DOMAIN_BUNDLE);

    /** Creates a new instance of TargetResolverImpl */
    public TargetResolverImpl(ProcessingContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void resolveAndVerifyTargets(
            List<Target> actualTargets, List<Target> inferredTargets, WSSPolicy actualPolicy) throws XWSSecurityException {

        String policyType = PolicyTypeUtil.signaturePolicy(actualPolicy) ? "Signature" : "Encryption";
        boolean isEndorsing = false;

        if ( PolicyTypeUtil.signaturePolicy(actualPolicy)) {
            SignaturePolicy.FeatureBinding fp = (SignaturePolicy.FeatureBinding)actualPolicy.getFeatureBinding();
            if (fp.isEndorsingSignature()) {
                isEndorsing = true;
            }
        }

        fpContext = new FilterProcessingContext(ctx);
        SecurityHeader header = fpContext.getSecurableSoapMessage().findSecurityHeader();
        Document doc = header.getOwnerDocument();

        for(Target actualTarget : actualTargets){
            boolean found = false;
            String targetInPolicy = getTargetValue(doc,actualTarget);
            for(Target inferredTarget : inferredTargets){
                String targetInMessage = getTargetValue(doc,inferredTarget);
                if(targetInPolicy!=null && targetInPolicy.equals(targetInMessage)){
                    found = true;
                    break;
                }
            }
            if(!found && targetInPolicy!=null ){
                //check if message has the target
                //check if the message has the element
                NodeList nl = doc.getElementsByTagName(targetInPolicy);
                if(nl!=null && nl.getLength()>0){
                    log.log(Level.SEVERE,LogStringsMessages.WSS_0206_POLICY_VIOLATION_EXCEPTION());
                    log.log(Level.SEVERE,LogStringsMessages.WSS_0814_POLICY_VERIFICATION_ERROR_MISSING_TARGET(targetInPolicy, policyType));
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
    }

    private String getTargetValue(final Document doc, final Target actualTarget) {
        String targetInPolicy = null;
        if(actualTarget.getType() == Target.TARGET_TYPE_VALUE_QNAME){
            targetInPolicy = actualTarget.getQName().getLocalPart();
        }else if(actualTarget.getType() == Target.TARGET_TYPE_VALUE_URI){
            String val = actualTarget.getValue();
            String id = null;
            if(val.charAt(0) == '#')
                id = val.substring(1,val.length());
            else
                id = val;
            Element signedElement = doc.getElementById(id);
            if(signedElement != null){
                targetInPolicy = signedElement.getLocalName();
            }
        }
        return targetInPolicy;
    }

    @Override
    public boolean isTargetPresent(List<Target> actualTargets) throws XWSSecurityException {
        FilterProcessingContext fpContext = new FilterProcessingContext(ctx);
        SecurityHeader header = fpContext.getSecurableSoapMessage().findSecurityHeader();
        Document doc = header.getOwnerDocument();
        for(Target actualTarget : actualTargets){
            if(actualTarget.getType() == Target.TARGET_TYPE_VALUE_XPATH){
                String val = actualTarget.getValue();
                try{
                    XPathFactory xpathFactory = WSITXMLFactory.createXPathFactory(WSITXMLFactory.DISABLE_SECURE_PROCESSING);
                    XPath xpath = xpathFactory.newXPath();
                    xpath.setNamespaceContext(fpContext.getSecurableSoapMessage().getNamespaceContext());
                    XPathExpression xpathExpr = xpath.compile(val);
                    NodeList nodes = (NodeList)xpathExpr.evaluate(fpContext.getSecurableSoapMessage().getSOAPPart(),XPathConstants.NODESET);
                    if(nodes != null && nodes.getLength() >0){
                        return true;
                    }
                }catch(XPathExpressionException xpe){
                    throw new XWSSecurityException(xpe);
                }
            }else{
                String targetInPolicy = getTargetValue(doc,actualTarget);
                NodeList nl = doc.getElementsByTagName(targetInPolicy);
                if(nl!=null && nl.getLength()>0){
                    return true;
                }
            }
        }
        return false;
    }

}
