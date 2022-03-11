/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.impl.policyconv;

/**
 *
 * @author K.Venugopal@sun.com
 */
public class PolicyID {

    int id =0;
    /** Creates a new instance of PolicyID */
    public PolicyID() {
    }

    public String generateID(){
        id++;
        return "_" + id;
    }

}
