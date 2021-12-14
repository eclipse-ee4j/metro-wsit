/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.opt.impl.util;

import com.sun.xml.wss.impl.MessageConstants;

/**
 *
 * @author K.Venugopal@sun.com
 */
public class CanonicalNamespacePrefixMapper extends WSSNamespacePrefixMapper{
    
    /** Creates a new instance of CanonicalNamespacePrefixMapper */
    public CanonicalNamespacePrefixMapper() {
    }
    
    @Override
    public String[] getPreDeclaredNamespaceUris() {
        return new String[] { MessageConstants.WSSE_PREFIX,MessageConstants.WSSE_NS,MessageConstants.WSSE11_PREFIX,MessageConstants.WSSE11_NS,
        MessageConstants.XENC_PREFIX,MessageConstants.XENC_NS,MessageConstants.DSIG_PREFIX,MessageConstants.DSIG_NS };
    }
    
}
