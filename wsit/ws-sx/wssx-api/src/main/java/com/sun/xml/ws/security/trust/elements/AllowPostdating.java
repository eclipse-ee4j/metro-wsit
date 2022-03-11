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
 * $Id: AllowPostdating.java,v 1.2 2010-10-21 15:35:40 snajper Exp $
 */

package com.sun.xml.ws.security.trust.elements;

/**
 * TODO: Interop Issue with WS-Trust Spec since it does not
 * specify the contents for AllowPostDating
 *
 *&lt;xs:element name="AllowPostdating" type="wst:AllowPostdatingType"/&gt;
 *&lt;xs:complexType name="AllowPostdatingType"/&gt;
 *
 * This indicates that returned tokens should allow requests for postdated
 * tokens.
 *
 * @author Kumar Jayanti
 */
public interface AllowPostdating {

}
