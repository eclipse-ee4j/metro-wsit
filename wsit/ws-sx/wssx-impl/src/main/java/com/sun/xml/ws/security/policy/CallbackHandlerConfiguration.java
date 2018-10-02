/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.policy;

import com.sun.xml.ws.policy.PolicyAssertion;
import java.util.Iterator;

/**
 *
 * @author K.Venugopal@sun.com
 */
public interface CallbackHandlerConfiguration {   
    public Iterator<? extends PolicyAssertion> getCallbackHandlers();
    public String getTimestampTimeout();
    public String getUseXWSSCallbacks();
    public String getiterationsForPDK();
}
