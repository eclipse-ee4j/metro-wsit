/*
 * Copyright (c) 2010, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * $Id: StaticPolicyContext.java,v 1.2 2010-10-21 15:37:33 snajper Exp $
 */

package com.sun.xml.wss.impl.policy;

/**
 * Represents a static policy identifying context.
 * An identifying context forms the key to a Security System
 * where different policies are applied to incoming and outgoing
 * messages based on the context. An example of a context would
 * be the Operation Name of the operation whose execution would result
 * in the outgoing message.
 */
public interface StaticPolicyContext {}
