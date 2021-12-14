/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.impl.policy.mls;

import com.sun.xml.wss.impl.PolicyTypeUtil;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents mandatory header elements that need to present in the message.
 * @author K.Venugopal@sun.com
 */
public class MandatoryTargetPolicy extends WSSPolicy {
    
    
    /** Creates a new instance of MandatoryTargetPolicy */
    public MandatoryTargetPolicy() {
    }
    
    /**
     * 
     * @return clone
     */
    @Override
    public Object clone() {
        MandatoryTargetPolicy mp = new MandatoryTargetPolicy();
        WSSPolicy wp = (WSSPolicy) getFeatureBinding();
        if(wp != null){
            WSSPolicy nwp = (WSSPolicy)wp.clone();
            mp.setFeatureBinding(nwp);
        }
        return mp;
    }
    
    /**
     * 
     * @return true of policy is equal to this policy
     */
    @Override
    public boolean equals(WSSPolicy policy) {
        if(policy.getType() == PolicyTypeUtil.MANDATORY_TARGET_POLICY_TYPE){
            WSSPolicy p1 = (WSSPolicy) policy.getFeatureBinding();
            if(p1 == null || getFeatureBinding() == null){
                return false;
            }
            return p1.equals(getFeatureBinding());
        }
        return false;
    }
    
    /**
     * 
     * @return true if argument policy is equal to this policy ignoring targets
     */
    @Override
    public boolean equalsIgnoreTargets(WSSPolicy policy) {
        throw new UnsupportedOperationException();
    }
    
    /**
     * 
     * @return the type of the policy
     */
    @Override
    public String getType() {
        return PolicyTypeUtil.MANDATORY_TARGET_POLICY_TYPE;
    }
    
    
    public static class FeatureBinding extends WSSPolicy {
        private List<Target> targets = new ArrayList<>();
        
        
        /**
         * adds the Target representing the Header element that must be present in the message.
         * Will by default set enforce flag on Target element to true.
         */
        public void addTargetBinding(Target target){
            targets.add(target);
            target.setEnforce(true);
        }
        
        /**
         * 
         * @return list of Target elements
         */
        public List<Target> getTargetBindings(){
            return targets;
        }
        
        /**
         * 
         * @return clone
         */
        @Override
        public Object clone() {
            FeatureBinding binding = new FeatureBinding();
            for(Target t: targets){
                binding.addTargetBinding(t);
            }
            return binding;
        }
        
        /**
         * 
         * @return true if this policy is equal to the argument policy
         */
        @Override
        public boolean equals(WSSPolicy policy) {
            boolean retVal = false;
            if(policy.getType() == PolicyTypeUtil.MANDATORY_TARGET_FEATUREBINDING_TYPE){
                List<Target> tList = ((MandatoryTargetPolicy.FeatureBinding)policy).getTargetBindings();
                for(Target t: tList){ 
                    if(!targets.contains(t)){
                        break;
                    }
                }
                retVal = true;
            }
            return retVal;
        }
        
        /**
         * 
         * @return true if this policy is equal to the argument policy ignoring targets
         */
        @Override
        public boolean equalsIgnoreTargets(WSSPolicy policy) {
            throw new UnsupportedOperationException();
        }
        
        /**
         * 
         * @return type of the policy
         */
        @Override
        public String getType() {
            return PolicyTypeUtil.MANDATORY_TARGET_FEATUREBINDING_TYPE;
        }
        
    }
    
}
