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

import java.util.Iterator;


/**
 * Supporting tokens are included in the security header and may optionally include
 * additional message parts to sign and/or encrypt.
 * @author K.Venugopal@sun.com
 */
public interface SupportingTokens extends Token{    
   
    /**
     * returns the {@link AlgorithmSuite} which will identify algorithms to use.
     * @return {@link AlgorithmSuite} or null
     */
    public AlgorithmSuite getAlgorithmSuite();
  
    /**
     * List of targets that need to be protected.
     * @return {@link java.util.Iterator } over targets that need to be protected.
     */
    public Iterator<SignedParts> getSignedParts();
    public Iterator<SignedElements> getSignedElements();
    public Iterator<EncryptedParts> getEncryptedParts();
    public Iterator<EncryptedElements> getEncryptedElements();
   
    /**
     * All tokens are set.
     * @return {@link java.util.Iterator } over tokens that are to be included in the message
     */
    public Iterator getTokens();
}
