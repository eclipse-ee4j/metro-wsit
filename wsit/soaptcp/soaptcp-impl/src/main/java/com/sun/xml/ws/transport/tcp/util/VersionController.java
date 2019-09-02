/*
 * Copyright (c) 1997, 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.transport.tcp.util;

/**
 * @author Alexey Stashok
 */
public final class VersionController {

    private static final VersionController instance = new VersionController(
            new Version(1, 0), new Version(1, 0));

    private final Version framingVersion;
    private final Version connectionManagementVersion;

    private VersionController(final Version framingVersion,
            final Version connectionManagementVersion) {
        this.framingVersion = framingVersion;
        this.connectionManagementVersion = connectionManagementVersion;
    }

    public static VersionController getInstance() {
        return instance;
    }

    public Version getFramingVersion() {
        return framingVersion;
    }

    public Version getConnectionManagementVersion() {
        return connectionManagementVersion;
    }

    /**
    *  Method checks compatibility of server and client versions
    */
    public boolean isVersionSupported(final Version framingVersion,
            final Version connectionManagementVersion) {

        return this.framingVersion.equals(framingVersion) &&
                this.connectionManagementVersion.equals(connectionManagementVersion);
    }

    /**
    *  Method returns closest to given framing version, which current implementation supports
    */
    public Version getClosestSupportedFramingVersion(Version framingVersion) {
        return this.framingVersion;
    }

    /**
    *  Method returns closest to given connection management version, which current implementation supports
    */
    public Version getClosestSupportedConnectionManagementVersion(Version connectionManagementVersion) {
        return this.connectionManagementVersion;
    }
}
