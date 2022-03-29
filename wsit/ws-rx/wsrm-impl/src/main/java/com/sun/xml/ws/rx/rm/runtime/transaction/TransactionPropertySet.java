/*
 * Copyright (c) 2013, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.rm.runtime.transaction;

import com.oracle.webservices.api.message.BasePropertySet;

import java.lang.invoke.MethodHandles;

/**
*
*/
public class TransactionPropertySet extends BasePropertySet {
    public static final String TX_OWNED_PROPERTY = "com.sun.xml.ws.rx.rm.runtime.transaction.owned";

    //Do we own the TX? This would be set to true when we begin the TX.
    private boolean owned = false;

    /**
     * Default constructor.
     */
    public TransactionPropertySet() {}

    @Property(TX_OWNED_PROPERTY)
    public boolean isTransactionOwned() {
        return owned;
    }

    public void setTransactionOwned(boolean flag) {
        owned = flag;
    }

    ////////////////////////////////////////////////////
    //
    // PropertySet boilerplate
    //

    private static final PropertyMap model;

    static {
        model = parse(TransactionPropertySet.class, MethodHandles.lookup());
    }

    @Override
    protected PropertyMap getPropertyMap() {
        return model;
    }
}
