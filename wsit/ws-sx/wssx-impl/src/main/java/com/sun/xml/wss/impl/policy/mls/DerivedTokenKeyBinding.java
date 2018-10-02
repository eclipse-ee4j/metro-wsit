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
 * DerivedTokenKeyBinding.java
 *
 * Created on December 20, 2005, 5:22 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.sun.xml.wss.impl.policy.mls;

import com.sun.xml.wss.impl.PolicyTypeUtil;

/**
 *
 * @author Abhijit Das
 */
public class DerivedTokenKeyBinding extends KeyBindingBase {
    
    private WSSPolicy originalKeyBinding = null;
    
    
    /** Creates a new instance of DerivedTokenKeyBinding */
    public DerivedTokenKeyBinding() {
        setPolicyIdentifier(PolicyTypeUtil.DERIVED_TOKEN_KEY_BINDING);
    }
    
    public Object clone() {
        DerivedTokenKeyBinding dkt = new DerivedTokenKeyBinding();
        dkt.setOriginalKeyBinding((WSSPolicy)getOriginalKeyBinding().clone());
        dkt.setUUID(this.getUUID());
        return dkt;
    }
    
    public boolean equals(WSSPolicy policy) {
        if ( !PolicyTypeUtil.derivedTokenKeyBinding(policy)) {
            return false;
        }
        
        WSSPolicy dkt = ((DerivedTokenKeyBinding)policy).getOriginalKeyBinding();
        if ( dkt.getType().intern() != getOriginalKeyBinding().getType().intern() )
            return false;
        //TODO: check the contents (dkt.getValue() and derivedTokenKeyBinding.getValue()
        return true;
    }
    
    public boolean equalsIgnoreTargets(WSSPolicy policy) {
        return equals(policy);
    }
    
    public String getType() {
        return PolicyTypeUtil.DERIVED_TOKEN_KEY_BINDING;
    }
    
    public WSSPolicy getOriginalKeyBinding() {
        return originalKeyBinding;
    }
    
    public void setOriginalKeyBinding(WSSPolicy originalKeyBinding) {
        this.originalKeyBinding = originalKeyBinding;
    }

}
