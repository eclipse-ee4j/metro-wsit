/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.secconv;

/**
 * A Factory for creating WS-SecureConversation contract instances.
 */

public class WSSCFactory {

    private WSSCFactory(){
        //empty constructor
    }

    public static WSSCPlugin newSCPlugin() {
        return new WSSCPlugin();
    }

    public static WSSCContract newWSSCContract(final WSSCVersion wsscVer) {
        final WSSCContract contract = new WSSCContract();
        contract.init(wsscVer);

        return contract;
    }

    public static WSSCClientContract newWSSCClientContract() {
        return new WSSCClientContract();
    }
}
