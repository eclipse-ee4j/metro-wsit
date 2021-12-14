/*
 * Copyright (c) 2010, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
* $Id: StatusImpl.java,v 1.2 2010-10-21 15:37:05 snajper Exp $
 */

package com.sun.xml.ws.security.trust.impl.wssx.elements;

import com.sun.xml.ws.api.security.trust.Status;
import com.sun.xml.ws.security.trust.WSTrustVersion;
import com.sun.xml.ws.security.trust.impl.wssx.bindings.StatusType;

/**
 * @author Manveen Kaur (manveen.kaur@sun.com).
 */
public class StatusImpl extends StatusType implements Status{
    
   public StatusImpl(String code, String reason) {
        setCode(code);
        setReason(reason);
    }
    
    public StatusImpl(StatusType statusType){
        setCode(statusType.getCode());
        setReason(statusType.getReason());
    }
    
     @Override
     public boolean isValid(){
        WSTrustVersion wstVer = WSTrustVersion.WS_TRUST_13 ;
        return wstVer.getValidStatusCodeURI().equals(getCode());
    }
}
