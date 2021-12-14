/*
 * Copyright (c) 2010, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.commons.ha;

import java.io.Serializable;
import org.glassfish.ha.store.api.HashableKey;

/**
 *
 * @author Marek Potociar (marek.potociar at sun.com)
 */
public class StickyKey implements HashableKey, Serializable {

    final Serializable key;
    private final String hashKey;

    public StickyKey(Serializable key, String hashKey) {
        this.key = key;
        this.hashKey = hashKey;
    }

    public StickyKey(Serializable key) {
        this.key = key;
        this.hashKey = "HASHABLE_KEY_" + key.hashCode();
    }

    @Override
    public String getHashKey() {
        return hashKey;
    }

    @Override
    public boolean equals(Object that) {
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        return this.key.equals(((StickyKey) that).key);
    }

    @Override
    public int hashCode() {
        return this.key.hashCode();
    }

    @Override
    public String toString() {
        return "StickyKey{" + "key=" + key + ", hashKey=" + hashKey + '}';
    }
}
