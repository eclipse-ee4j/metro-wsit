/*
 * Copyright (c) 1997, 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.assembler.metro.dev;

import com.sun.xml.ws.assembler.dev.ClientTubelineAssemblyContext;
import com.sun.xml.ws.security.secconv.SecureConversationInitiator;

/**
 *
 * @author Marek Potociar (marek.potociar at sun.com)
 */
public interface MetroClientTubelineAssemblyContext extends ClientTubelineAssemblyContext {

    SecureConversationInitiator getScInitiator();

    void setScInitiator(SecureConversationInitiator initiator);

}
