/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.impl.policyconv;

import com.sun.xml.ws.security.policy.AlgorithmSuite;
import com.sun.xml.wss.impl.MessageConstants;
import com.sun.xml.wss.impl.policy.mls.Parameter;
import com.sun.xml.wss.impl.policy.mls.SignatureTarget;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.namespace.QName;
import com.sun.xml.ws.security.impl.policy.Constants;

/**
 *
 * @author K.Venugopal@sun.com
 */
public class SignatureTargetCreator {
    private boolean enforce = false;
    private AlgorithmSuite algorithmSuite = null;
    private boolean contentOnly = false;
    /**
     * Creates a new instance of SignatureTargetCreator
     */
    public SignatureTargetCreator(boolean enforce,AlgorithmSuite algorithmSuite,boolean contentOnly) {
        this.enforce = enforce;
        this.algorithmSuite = algorithmSuite;
    }

    public SignatureTarget newURISignatureTarget(String uid){
        if ( uid != null ) {
            SignatureTarget target = new SignatureTarget();
            target.setType(SignatureTarget.TARGET_TYPE_VALUE_URI);
            target.setDigestAlgorithm(algorithmSuite.getDigestAlgorithm());
            target.setValue("#"+uid);
            addTransform(target);
            target.setEnforce(enforce);
            return target;
        }
        return null;
    }

    public SignatureTarget newXpathSignatureTarget(String xpathTarget){
        SignatureTarget target = new SignatureTarget();
        target.setType(SignatureTarget.TARGET_TYPE_VALUE_XPATH);
        target.setDigestAlgorithm(algorithmSuite.getDigestAlgorithm());
        target.setValue(xpathTarget);
        target.setContentOnly(contentOnly);
        target.setEnforce(enforce);
        return target;
    }

    public SignatureTarget newQNameSignatureTarget(QName name){
        SignatureTarget target = new SignatureTarget();
        target.setType(SignatureTarget.TARGET_TYPE_VALUE_QNAME);
        target.setDigestAlgorithm(algorithmSuite.getDigestAlgorithm());
        target.setContentOnly(contentOnly);
        target.setEnforce(enforce);
        target.setQName(name);
        return target;
    }

    public void addTransform(SignatureTarget target){
        SignatureTarget.Transform tr = target.newSignatureTransform();
        if(algorithmSuite != null && algorithmSuite.getAdditionalProps().contains(Constants.InclusiveC14N)){
            tr.setTransform(CanonicalizationMethod.INCLUSIVE);
        } else{
            tr.setTransform(CanonicalizationMethod.EXCLUSIVE);
        }

        if(algorithmSuite != null && algorithmSuite.getAdditionalProps().contains(Constants.InclusiveC14NWithCommentsForTransforms)){
            tr.setTransform(CanonicalizationMethod.INCLUSIVE_WITH_COMMENTS);
        } else if(algorithmSuite != null && algorithmSuite.getAdditionalProps().contains(Constants.ExclusiveC14NWithCommentsForTransforms)){
            tr.setTransform(CanonicalizationMethod.EXCLUSIVE_WITH_COMMENTS);
        }
        target.addTransform(tr);
    }

    public void addSTRTransform(SignatureTarget target){
        SignatureTarget.Transform tr = target.newSignatureTransform();
        tr.setTransform(MessageConstants.STR_TRANSFORM_URI);
        target.addTransform(tr);
        tr.setAlgorithmParameters(new Parameter("CanonicalizationMethod",CanonicalizationMethod.EXCLUSIVE));
    }

    void addAttachmentTransform(SignatureTarget target, String transformURI) {
        SignatureTarget.Transform tr = target.newSignatureTransform();
        tr.setTransform(transformURI);
        target.addTransform(tr);
    }

    //a new one for SingedSupportingTokens where we don't add transform by default
    //a decision on using STR-TX is pending and hence we delay adding the transform
    public SignatureTarget newURISignatureTargetForSSToken(String uid) {
          if ( uid != null ) {
            SignatureTarget target = new SignatureTarget();
            target.setType(SignatureTarget.TARGET_TYPE_VALUE_URI);
            target.setDigestAlgorithm(algorithmSuite.getDigestAlgorithm());
            target.setValue("#"+uid);
            target.setEnforce(enforce);
            return target;
        }
        return null;
    }
}
