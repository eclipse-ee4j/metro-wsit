/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.policy;

/**
 * Signed tokens are included in the message signature as defined above and may
 * optionally include additional message parts to sign and/or encrypt.
 *
 * @author K.Venugopal@sun.com
 */
public interface SignedSupportingTokens extends SupportingTokens{

}
