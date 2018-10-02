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
 * StatementType.java
 *
 * Created on July 25, 2005, 2:13 PM
 *
 */

package com.sun.xml.wss.saml.assertion.saml20.jaxb20;

/**
 *
 * @author abhijit.das
 */
public interface StatementType {

    /**
     * The Statement is not supported.
     */
    public final static int NOT_SUPPORTED                   = -1;
                                                                                                                             
    /**
     * The Statement is an Authentication Statement.
     */
    public final static int AUTHENTICATION_STATEMENT        = 1;
                                                                                                                             
    /**
     * The Statement is an Authorization Decision Statement.
     */
    public final static int AUTHORIZATION_DECISION_STATEMENT= 2;
                                                                                                                             
    /**
     * The Statement is an Attribute Statement.
     */
    public final static int ATTRIBUTE_STATEMENT             = 3;
    
}
