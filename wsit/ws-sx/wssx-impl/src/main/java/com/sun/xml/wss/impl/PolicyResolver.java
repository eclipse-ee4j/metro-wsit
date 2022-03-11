/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.impl;

import com.sun.xml.wss.ProcessingContext;
import com.sun.xml.wss.impl.policy.SecurityPolicy;

/**
 *
 * @author Ashutosh.Shahi@sun.com
 */
public interface PolicyResolver {

    //MessagePolicy resolvePolicy(ProcessingContext ctx);
    SecurityPolicy resolvePolicy(ProcessingContext ctx);

}
