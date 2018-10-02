/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.impl.policy.verifier;

import com.sun.xml.wss.XWSSecurityException;
import com.sun.xml.wss.impl.policy.mls.Target;
import com.sun.xml.wss.impl.policy.mls.WSSPolicy;
import java.util.List;

/**
 *
 * @author Ashutosh.Shahi@sun.com
 */
public interface TargetResolver {
    
    void resolveAndVerifyTargets(
            List<Target> actualTargets, List<Target> inferredTargets, WSSPolicy actualPolicy)
            throws XWSSecurityException;
    boolean isTargetPresent(List<Target> actualTargets)throws XWSSecurityException;
    
}
