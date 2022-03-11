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
 * Encrypted supporting tokens are supporting tokens that are included in
 * the security header and MUST be encrypted when they appear in the security header.
 * Element encryption SHOULD be used for encrypting these tokens. The encrypted
 * supporting tokens can be added to any SOAP message and do not require the
 * message signature being present before the encrypted supporting tokens are added.
 *
 * @author Ashutosh.Shahi@sun.com
 */
public interface EncryptedSupportingTokens extends SupportingTokens{

}
