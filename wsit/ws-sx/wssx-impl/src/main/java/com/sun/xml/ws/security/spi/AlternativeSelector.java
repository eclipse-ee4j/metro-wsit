/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.spi;

import com.sun.xml.wss.ProcessingContext;
import com.sun.xml.wss.impl.policy.SecurityPolicy;
import com.sun.xml.wss.impl.policy.mls.MessagePolicy;
import java.util.List;

/**
 *
 * @author vbkumarjayanti
 */
public interface AlternativeSelector {

    public MessagePolicy selectAlternative(ProcessingContext ctx, List<MessagePolicy> alternatives, SecurityPolicy recvdPolicy);

    public boolean supportsAlternatives(List<MessagePolicy> alternatives);
}
