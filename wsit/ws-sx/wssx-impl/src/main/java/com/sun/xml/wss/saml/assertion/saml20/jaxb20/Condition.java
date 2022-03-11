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
 * $Id: Condition.java,v 1.2 2010-10-21 15:38:03 snajper Exp $
 */

package com.sun.xml.wss.saml.assertion.saml20.jaxb20;

import com.sun.xml.wss.saml.internal.saml20.jaxb20.ConditionAbstractType;


/**
 * This is an abstract class which servers as an extension point for new
 * conditions.  This is one of the element within the <code>Conditions</code>
 * object.  Extension elements based on this class MUST use xsi:type attribute
 * to indicate the derived type.
 */
public abstract class Condition extends ConditionAbstractType
    implements com.sun.xml.wss.saml.Condition {

}
