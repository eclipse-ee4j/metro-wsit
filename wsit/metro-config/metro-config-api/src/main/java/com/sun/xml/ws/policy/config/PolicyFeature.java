/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.policy.config;

import com.sun.xml.ws.api.FeatureConstructor;
import com.sun.xml.ws.policy.PolicyMap;

import javax.xml.ws.WebServiceFeature;

import org.glassfish.gmbal.ManagedAttribute;
import org.glassfish.gmbal.ManagedData;

/**
 * WebServiceFeature for a WS-Policy expression {@link javax.xml.ws.WebServiceFeature}.
 *
 * @author Fabian Ritzmann
 */
@ManagedData
public class PolicyFeature extends WebServiceFeature {

    public static final String ID = "com.sun.xml.ws.policy.PolicyFeature";

    private final PolicyMap policyMap;

    @FeatureConstructor({
        "policyMap"
    })
    public PolicyFeature(PolicyMap policyMap) {
        this.enabled = true;
        this.policyMap = policyMap;
    }

    @Override
    @ManagedAttribute
    public String getID() {
        return ID;
    }

    @ManagedAttribute
    public PolicyMap getPolicyMap() {
        return this.policyMap;
    }

}
