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
 * $Id: SignChallengeImpl.java,v 1.2 2010-10-21 15:37:05 snajper Exp $
 */

package com.sun.xml.ws.security.trust.impl.wssx.elements;
import com.sun.xml.ws.security.trust.elements.SignChallenge;
import com.sun.xml.ws.security.trust.impl.wssx.bindings.SignChallengeType;

/**
 * Challenge that requires the other party to sign a specified set
 * of information.
 * 
 * @author Manveen Kaur
 */
public class SignChallengeImpl extends SignChallengeType implements SignChallenge {

    public SignChallengeImpl() {
        // empty constructor
    }
}
