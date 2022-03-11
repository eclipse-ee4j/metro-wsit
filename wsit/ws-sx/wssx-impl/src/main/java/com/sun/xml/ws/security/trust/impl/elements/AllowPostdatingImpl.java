/*
 * Copyright (c) 2010, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * $Id: AllowPostdatingImpl.java,v 1.2 2010-10-21 15:36:54 snajper Exp $
 */

package com.sun.xml.ws.security.trust.impl.elements;

import com.sun.xml.ws.security.trust.elements.AllowPostdating;
import com.sun.xml.ws.security.trust.impl.bindings.AllowPostdatingType;

/**
 * This indicates that returned tokens should allow requests for postdated
 * tokens.
 *
 * @author Manveen Kaur
 */
public class AllowPostdatingImpl extends AllowPostdatingType implements AllowPostdating {

    public AllowPostdatingImpl() {
        //empty c'tor
    }
}
