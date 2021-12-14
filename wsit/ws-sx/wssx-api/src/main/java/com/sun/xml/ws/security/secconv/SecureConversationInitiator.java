/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.secconv;

import jakarta.xml.bind.JAXBElement;
import com.sun.xml.ws.api.message.Packet;

public interface SecureConversationInitiator {
     JAXBElement startSecureConversation(Packet packet) throws WSSecureConversationException;
}
