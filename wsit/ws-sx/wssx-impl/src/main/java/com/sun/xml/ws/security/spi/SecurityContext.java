/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.spi;

import javax.security.auth.Subject;

/**
 * Provides a way to obtain credentials from an
 * encompassing runtime into the Metro Pipeline runtime
 */
public interface SecurityContext {

    /**
     * @return the subject containing credentials from the encompassing runtime, null if none is available
     */
    Subject getSubject();

    /**
     *
     */
    void setSubject(Subject subject);
}
