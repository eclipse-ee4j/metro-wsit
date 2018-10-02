/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.trust;

import com.sun.xml.ws.api.FeatureConstructor;
import com.sun.xml.ws.api.security.trust.client.STSIssuedTokenConfiguration;
import javax.xml.ws.WebServiceFeature;
import org.glassfish.gmbal.ManagedAttribute;
import org.glassfish.gmbal.ManagedData;

/**
 *
 * @author Jiandong Guo
 */
@ManagedData
public class STSIssuedTokenFeature extends WebServiceFeature{
    public static final String ID = "com.sun.xml.ws.security.trust.STSIssuedTokenFeature";

    private final STSIssuedTokenConfiguration stsIssuedTokenConfig;

    @FeatureConstructor({"stsIssuedTokenConfig"})
    public STSIssuedTokenFeature(STSIssuedTokenConfiguration stsIssuedTokenConfig) {
        enabled = true;
        this.stsIssuedTokenConfig = stsIssuedTokenConfig;
    }

    @Override
    @ManagedAttribute
    public String getID() {
        return ID;
    }

    @ManagedAttribute
    public STSIssuedTokenConfiguration getSTSIssuedTokenConfiguration() {
        return this.stsIssuedTokenConfig;
    }
}
