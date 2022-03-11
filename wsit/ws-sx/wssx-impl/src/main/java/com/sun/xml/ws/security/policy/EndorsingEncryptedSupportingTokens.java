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
 * Endorsing, encrypted supporting tokens are Endorsing supporting tokens that
 * are also encrypted when they appear in the wsse:SecurityHeader. Element
 * Encryption SHOULD be used for encrypting the supporting tokens.
 *
 * @author Ashutosh.Shahi@sun.com
 */
public interface EndorsingEncryptedSupportingTokens extends EndorsingSupportingTokens{

}
