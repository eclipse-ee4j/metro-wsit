/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.opt.api;
import com.sun.xml.wss.impl.policy.mls.WSSPolicy;
/**
 * This interface is implemeted by all SecurityHeaderElement's that are capable of building 
 * XWSS policy from the incoming message.
 * @author K.Venugopal@sun.com
 */
public interface PolicyBuilder {
     /**
     * returns the {@link com.sun.xml.wss.impl.policy.mls.WSSPolicy} that was created for the SecurityHeaderElement
     * @return {@link com.sun.xml.wss.impl.policy.mls.WSSPolicy}
     */
    WSSPolicy getPolicy();
}
