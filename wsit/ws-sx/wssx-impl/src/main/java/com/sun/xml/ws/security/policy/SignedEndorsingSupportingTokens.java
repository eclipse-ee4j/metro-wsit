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
 * Signed endorsing tokens sign the entire ds:Signature element produced from  the message signature and 
 * are themselves signed by that message signature, that is both tokens (the token used for the message 
 * signature and the signed endorsing token) sign each other.
 *
 * @author K.Venugopal@sun.com
 */
public interface SignedEndorsingSupportingTokens extends SupportingTokens{
    
}
