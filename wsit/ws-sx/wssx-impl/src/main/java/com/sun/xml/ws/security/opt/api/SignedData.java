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
 * SignedData.java
 *
 * Created on August 23, 2006, 3:29 PM
 */

package com.sun.xml.ws.security.opt.api;

/**
 * Represents message part that is signed.
 * @author Ashutosh.Shahi@sun.com
 */
public interface SignedData {

    /**
     *
     */
    void setDigestValue(byte[] digestValue);

    /**
     *
     * @return digestvalue
     */
    byte[] getDigestValue();

}
