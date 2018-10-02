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
 * CertStoreConfig.java
 *
 * Created on March 1, 2007, 3:37 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.sun.xml.ws.security.policy;

/**
 *
 * @author Kumar Jayanti
 */
public interface CertStoreConfig {
    
    public String getCallbackHandlerClassName();
    public String getCertSelectorClassName();
    public String getCRLSelectorClassName();
    
}
