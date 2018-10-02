/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.opt.api.keyinfo;

import com.sun.xml.wss.XWSSecurityException;
/**
 * 
 * Interface for processor of various token types like X509, SAML, SCT etc
 * @author K.Venugopal@sun.com
 */
public interface TokenBuilder {
    /**
     * Processes the token to obtain the keys etc
     * @return a <CODE>BuilderResult</CODE> with all token details set in it
     * @throws com.sun.xml.wss.XWSSecurityException exception if the various token information could not be obtained
     */
    BuilderResult process()throws XWSSecurityException;
}
