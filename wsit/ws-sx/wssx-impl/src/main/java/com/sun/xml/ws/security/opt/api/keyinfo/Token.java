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
 * Token.java
 *
 * Created on August 2, 2006, 10:46 AM
 */

package com.sun.xml.ws.security.opt.api.keyinfo;

/**
 *
 * @author K.Venugopal@sun.com
 */
public interface Token {
    /*
     * returns wsu:id attribute value
     */
    String getId();

}
