/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.xmlfilter;

/**
 *
 * @author Marek Potociar (marek.potociar at sun.com)
 */
public enum ProcessingStateChange {
    START_BUFFERING(),
    RESTART_BUFFERING(), // releases old buffer and starts new
    STOP_BUFFERING(),
    START_FILTERING(),
    STOP_FILTERING(),
    NO_CHANGE();
}
