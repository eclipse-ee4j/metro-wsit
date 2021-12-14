/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.opt.api;

import com.sun.xml.security.core.xenc.ReferenceList;
import java.security.Key;
import javax.xml.crypto.XMLStructure;

/**
 * 
 * @author K.Venugopal@sun.com
 */
public interface EncryptedKey extends XMLStructure {
    /**
     *
     */
    ReferenceList getReferenceList();
    /**
     *
     */
    void setReferenceList(ReferenceList list);
    /**
     *
     */
    Key getKey();    
}
