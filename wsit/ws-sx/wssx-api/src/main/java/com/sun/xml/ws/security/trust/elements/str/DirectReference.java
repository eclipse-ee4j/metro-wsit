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
 * $Id: DirectReference.java,v 1.2 2010-10-21 15:35:41 snajper Exp $
 */

package com.sun.xml.ws.security.trust.elements.str;

import java.net.URI;

/**
 * Reference Interface
 */
public interface DirectReference extends Reference {

    URI getURIAttr();

    URI getValueTypeURI();
        
}
