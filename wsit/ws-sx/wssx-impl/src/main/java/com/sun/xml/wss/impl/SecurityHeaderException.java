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
 * $Id: SecurityHeaderException.java,v 1.2 2010-10-21 15:37:15 snajper Exp $
 */

package com.sun.xml.wss.impl;

import com.sun.xml.wss.*;

/**
 * A SecurityHeaderException indicates that there is a problem with the
 * security header elements and subelements.
 * It indicates that there is an error in the input message to a MessageFilter.  
 * For example, a ds:keyInfo element may not contain a reference to a
 * security token. If such a reference is missing, then to
 * indicate this problem, an instance of this Exception would be thrown.
 * 
 * <p>
 * This is as opposed to a problem with processing the message itself.  An
 * example would be a MessageFilter that needs to look up data in a
 * database that is not currently available. A XWSSecurityException would
 * be thrown in the latter case.
 *
 * @author Edwin Goei
 * @author Manveen Kaur
 *
 */
public class SecurityHeaderException extends XWSSecurityException {
    private static final long serialVersionUID = 2145304194483023007L;

    public SecurityHeaderException(String message) {
        super(message);
    }

    public SecurityHeaderException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public SecurityHeaderException(Throwable cause) {
        super(cause);
    }
}
