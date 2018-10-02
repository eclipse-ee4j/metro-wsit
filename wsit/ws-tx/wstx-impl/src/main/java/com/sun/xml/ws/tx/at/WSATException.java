/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.at;

import javax.transaction.xa.XAException;

/**
 * WS-AT Exception mapping of XAException
 */
public class WSATException extends Exception {
	
		private static final long serialVersionUID = 8473938065230309413L;
		
    public int errorCode;  // named exactly as in XAException including the lack of a getter

    public WSATException(String s) {
        super(s);
    }

    public WSATException(Exception ex) {
        super(ex);
    }

    public WSATException(String s, XAException xae) {
        super(s, xae);
        errorCode = xae.errorCode;
    }

}
