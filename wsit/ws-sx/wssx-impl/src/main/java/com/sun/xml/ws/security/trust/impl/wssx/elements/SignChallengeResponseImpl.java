/*
 * Copyright (c) 2010, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * $Id: SignChallengeResponseImpl.java,v 1.2 2010-10-21 15:37:05 snajper Exp $
 */

package com.sun.xml.ws.security.trust.impl.wssx.elements;
import com.sun.xml.ws.security.trust.elements.SignChallengeResponse;
import com.sun.xml.ws.security.trust.impl.wssx.bindings.SignChallengeType;

/**
 * Response to a challenge that requires the signing of a specified
 * set of information.
 * 
 * @author Manveen Kaur
 */
public class SignChallengeResponseImpl extends SignChallengeType implements SignChallengeResponse {    

    public SignChallengeResponseImpl (){
        
    }
}
