/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.impl.policy.mls;

import com.sun.xml.wss.impl.PolicyTypeUtil;
import com.sun.xml.wss.impl.MessageConstants;

/**
 * A policy representing a WSS1.1 SignatureConfirmation element.
 * Note: The SignatureConfirmationPolicy is WSSPolicy element that does not contain a
 * concrete FeatureBinding and/or KeyBinding.
 *
 * @author Ashutosh.Shahi@sun.com
 */
public class SignatureConfirmationPolicy extends WSSPolicy{

    private String signatureValue = MessageConstants._EMPTY;

    /**
     * DefaultConstructor
     */
    public SignatureConfirmationPolicy() {
        setPolicyIdentifier(PolicyTypeUtil.SIGNATURE_CONFIRMATION_POLICY_TYPE);
    }

    /**
     * sets the Value attribute for SignatureConfirmation in this SignatureConfirmationPolicy
     */
    public void setSignatureValue(String signatureValue){
        this.signatureValue = signatureValue;
    }

    /**
     * @return the Value attribute of SignatureConfirmation
     */
    public String getSignatureValue(){
        return this.signatureValue;
    }

    /**
     * @param policy the policy to be compared for equality
     * @return true if the argument policy is equal to this policy
     */
    @Override
    public boolean equals(WSSPolicy policy){

        boolean assrt = false;
        try{
            SignatureConfirmationPolicy scPolicy = (SignatureConfirmationPolicy)policy;
            assrt = signatureValue.equals(scPolicy.getSignatureValue());
        } catch(Exception e) {}

        return assrt;
    }

    /**
     * Equality comparison ignoring the targets
     * @param policy the policy to be compared for equality
     * @return true if the argument policy is equal to this policy
     */
    @Override
    public boolean equalsIgnoreTargets(WSSPolicy policy) {
        return equals(policy);
    }

    /**
     * Clone operator
     * @return clone of this policy
     */
    @Override
    public Object clone(){
        SignatureConfirmationPolicy scPolicy = new SignatureConfirmationPolicy();

        try{
            scPolicy.setUUID(this.getUUID());
            scPolicy.setSignatureValue(signatureValue);
        } catch (Exception e) {}

        return scPolicy;
    }

    /**
     * @return the type of policy
     */
    @Override
    public String getType(){
        return PolicyTypeUtil.SIGNATURE_CONFIRMATION_POLICY_TYPE;
    }
}
