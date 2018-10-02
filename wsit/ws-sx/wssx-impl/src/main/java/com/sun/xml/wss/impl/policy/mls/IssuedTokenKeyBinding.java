/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * IssuedTokenKeyBinding.java
 *
 * Created on December 20, 2005, 5:24 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.sun.xml.wss.impl.policy.mls;

import com.sun.xml.wss.impl.PolicyTypeUtil;

/**
 *
 */
public class IssuedTokenKeyBinding extends KeyBindingBase implements LazyKeyBinding {
    
    String strId = null;
    private String realId;
    private String tokenType;
    
    
    /** Creates a new instance of IssuedTokenKeyBinding */
    public IssuedTokenKeyBinding() {
        setPolicyIdentifier(PolicyTypeUtil.ISSUED_TOKEN_KEY_BINDING);
    }
    
    public Object clone() {
        IssuedTokenKeyBinding itb = new IssuedTokenKeyBinding();
        //itb.setPolicyToken(this.getPolicyToken());
        itb.setUUID(this.getUUID());
        itb.setIncludeToken(this.getIncludeToken());
        itb.setPolicyTokenFlag(this.policyTokenWasSet());
        itb.setSTRID(this.strId);
        return itb;
    }
    
    public boolean equals(WSSPolicy policy) {
        if ( !PolicyTypeUtil.issuedTokenKeyBinding(policy)) {
            return false;
        }
        
        //TODO: Check the contents of IssuedTokenContext
        return true;
    }
    
    public boolean equalsIgnoreTargets(WSSPolicy policy) {
        return equals(policy);
    }
    
    public String getType() {
        return PolicyTypeUtil.ISSUED_TOKEN_KEY_BINDING;
    }
    
        /*
         * @param id the wsu:id of the wsse:SecurityTokenReference to
         * be generated for this Issued Token. Applicable while
         * sending a message (sender side policy)
         */
        public void setSTRID(String id) {
            if (isReadOnly()) {
                throw new RuntimeException("Can not set Issued Token STRID : Policy is ReadOnly");
            }
            
            this.strId = id;
        }
        
        /*
         * @return the wsu:id of the wsse:SecurityTokenReference to
         * be generated for this Issued Token, if specified,
         * null otherwise.
         */
        public String getSTRID() {
            return this.strId;
        }

    public String getRealId() {
        return realId;
    }

    public void setRealId(String realId) {
       this.realId = realId;
    }
    public void setTokenType(String tokenType) {
            this.tokenType = tokenType;
        }

        public String getTokenType() {
            return tokenType;
        }

}
