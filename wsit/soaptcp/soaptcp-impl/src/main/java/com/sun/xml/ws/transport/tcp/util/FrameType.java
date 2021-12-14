/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.transport.tcp.util;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Alexey Stashok
 */

public final class FrameType {
    public static final int MESSAGE = 0;
    public static final int MESSAGE_START_CHUNK = 1;
    public static final int MESSAGE_CHUNK = 2;
    public static final int MESSAGE_END_CHUNK = 3;
    public static final int ERROR = 4;
    public static final int NULL = 5;
    
    private static final Set<Integer> typesContainParameters;
    
    static {
        typesContainParameters = new HashSet<>();
        typesContainParameters.add(MESSAGE);
        typesContainParameters.add(MESSAGE_START_CHUNK);
    }
    
    public static boolean isFrameContainsParams(final int msgId) {
        return typesContainParameters.contains(msgId);
    }
    
    public static boolean isLastFrame(final int msgId) {
        return msgId == MESSAGE || msgId == MESSAGE_END_CHUNK || 
                msgId == ERROR || msgId == NULL;
    }
}
