/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.policy;

/**
 *
 * @author K.Venugopal@sun.com
 */
public interface KeyStore {
    
    public String getLocation();
    
    public String getType();
    
    public char[] getPassword();
    
    public String getAlias();
    
    public String getKeyPassword();
    
    public String getAliasSelectorClassName();
    
    public String getKeyStoreCallbackHandler();

    public String getKeyStoreLoginModuleConfigName();
    
}
