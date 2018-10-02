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
 * $Id: ParticipantImpl.java,v 1.2 2010-10-21 15:36:54 snajper Exp $
 */

package com.sun.xml.ws.security.trust.impl.elements;

import com.sun.xml.ws.security.trust.elements.Participant;
import com.sun.xml.ws.security.trust.impl.bindings.ParticipantType;
        
/**
 * Specifies a participant that plays a role in the use of
 * the token or who are allowed to use the token.
 *
 * @author Manveen Kaur
 *
 */
public class ParticipantImpl extends ParticipantType implements Participant {

    public ParticipantImpl() {
        // empty default constructor
    }
    
}
