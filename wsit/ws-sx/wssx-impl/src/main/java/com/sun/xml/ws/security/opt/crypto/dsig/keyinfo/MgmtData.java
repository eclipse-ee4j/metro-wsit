/*
 * Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.opt.crypto.dsig.keyinfo;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author lukas
 */
@XmlRootElement(name="MgmtData", namespace = "http://www.w3.org/2000/09/xmldsig#")
public class MgmtData implements javax.xml.crypto.XMLStructure {

    private String data;

    public MgmtData() {
    }

    @Override
    public boolean isFeatureSupported(String feature) {
        return false;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

}
