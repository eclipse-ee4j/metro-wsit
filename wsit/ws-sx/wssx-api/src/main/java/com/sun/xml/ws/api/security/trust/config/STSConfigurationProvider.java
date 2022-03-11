/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.api.security.trust.config;

/**
 * This interface is used to find the <code>STSConfiguration</code>.
 * The usual services mechanism is used to find implementing class
 * of <code>STSConfigurationProvider</code>.
 *
 * @author Jiandong Guo
 */
public interface STSConfigurationProvider {

    STSConfiguration getSTSConfiguration();
}
