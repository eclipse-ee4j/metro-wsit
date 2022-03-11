/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.impl.policy.mls;

/**
 * Indicates that a Binding should have its uid re-read when the actual signature is created
 * because the id is not known at startup time, for example because the binding
 * refers to a token which must be generated externally.
 *
 */
public interface LazyKeyBinding {

    String getRealId();

    void setRealId(String realId);

    String getSTRID();
}
