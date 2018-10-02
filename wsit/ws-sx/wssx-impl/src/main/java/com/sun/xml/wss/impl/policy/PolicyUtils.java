/*
 * Copyright (c) 2010, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.impl.policy;

import com.sun.xml.wss.impl.policy.mls.MessagePolicy;

/**
 *
 * @author vbkumarjayanti
 */
public class PolicyUtils {

    public static boolean isEmpty(SecurityPolicy msgPolicy) {
        if (msgPolicy == null) {
            return true;
        }
        //TODO: it will be best if SecurityPolicy interface had an isEmpty
        //will make that change after initial checkin for policy-alternatives
        if (msgPolicy instanceof MessagePolicy) {
            return (((MessagePolicy)msgPolicy).isEmpty());
        } else if (msgPolicy instanceof PolicyAlternatives) {
            PolicyAlternatives pol = (PolicyAlternatives)msgPolicy;
            return pol.isEmpty();
        }
        return false;
    }
}
