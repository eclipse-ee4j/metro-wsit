/*
 * Copyright (c) 2010, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * $Id: ConfigurationStates.java,v 1.2 2010-10-21 15:37:47 snajper Exp $
 */

package com.sun.xml.wss.provider;

import javax.security.auth.message.MessagePolicy;

public interface ConfigurationStates {
     public static final int AUTHENTICATE_RECIPIENT_ONLY = 1;
     public static final int AUTHENTICATE_SENDER_TOKEN_ONLY = 2;
     public static final int AUTHENTICATE_SENDER_SIGNATURE_ONLY = 3;
     public static final int AUTHENTICATE_RECIPIENT_AUTHENTICATE_SENDER_TOKEN = 4;
     public static final int AUTHENTICATE_SENDER_TOKEN_AUTHENTICATE_RECIPIENT = 5;
     public static final int AUTHENTICATE_RECIPIENT_AUTHENTICATE_SENDER_SIGNATURE = 6;
     public static final int AUTHENTICATE_SENDER_SIGNATURE_AUTHENTICATE_RECIPIENT = 7;
     public static final int EMPTY_POLICY_STATE = 8;

     // resolve required config. state 
     int resolveConfigurationState(MessagePolicy policy,
             boolean isRequestPolicy, boolean isClientAuthModule);
}
