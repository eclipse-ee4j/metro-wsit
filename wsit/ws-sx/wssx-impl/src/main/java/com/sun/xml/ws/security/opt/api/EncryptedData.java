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
 * EncryptedData.java
 *
 * Created on August 4, 2006, 3:00 PM
 */

package com.sun.xml.ws.security.opt.api;

/**
 *
 * @author K.Venugopal@sun.com
 */
public interface EncryptedData {

    void encrypt();

    void decrypt();


}
