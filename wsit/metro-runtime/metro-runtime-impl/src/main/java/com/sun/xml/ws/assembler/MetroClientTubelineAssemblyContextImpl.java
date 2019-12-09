/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.assembler;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.pipe.ClientTubeAssemblerContext;
import com.sun.xml.ws.assembler.metro.dev.MetroClientTubelineAssemblyContext;
import com.sun.xml.ws.security.secconv.SecureConversationInitiator;

/**
 * @author Marek Potociar (marek.potociar at sun.com)
 */
public class MetroClientTubelineAssemblyContextImpl extends DefaultClientTubelineAssemblyContext implements MetroClientTubelineAssemblyContext {

    private SecureConversationInitiator scInitiator;

    public MetroClientTubelineAssemblyContextImpl(@NotNull ClientTubeAssemblerContext context) {
        super(context);
    }

    public SecureConversationInitiator getScInitiator() {
        return scInitiator;
    }
    
    public void setScInitiator(SecureConversationInitiator initiator) {
        this.scInitiator = initiator;
    }
}
