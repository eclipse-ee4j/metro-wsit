/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.opt.impl.dsig;

import java.security.spec.AlgorithmParameterSpec;
import java.util.ArrayList;
import java.util.List;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;

/**
 *
 * @author ashutoshshahi
 */
public class ExcC14NParameterSpec implements AlgorithmParameterSpec,
        C14NMethodParameterSpec, TransformParameterSpec{

    private List preList;

    /**
     * Indicates the default namespace ("#default").
     */
    public static final String DEFAULT = "#default";

    /**
     * Creates a <code>ExcC14NParameterSpec</code> with an empty prefix
     * list.
     */
    public ExcC14NParameterSpec() {
    preList = new ArrayList();
    }

    /**
     * Creates a <code>ExcC14NParameterSpec</code> with the specified list
     * of prefixes. The list is copied to protect against subsequent
     * modification.
     *
     * @param prefixList the inclusive namespace prefix list. Each entry in
     *    the list is a <code>String</code> that represents a namespace prefix.
     * @throws NullPointerException if <code>prefixList</code> is
     *    <code>null</code>
     * @throws ClassCastException if any of the entries in the list are not
     *    of type <code>String</code>
     */
    @SuppressWarnings("unchecked")
    public ExcC14NParameterSpec(List prefixList) {
    if (prefixList == null) {
        throw new NullPointerException("prefixList cannot be null");
    }
    this.preList = new ArrayList(prefixList);
        for (int i = 0, size = preList.size(); i < size; i++) {
            if (!(preList.get(i) instanceof String)) {
        throw new ClassCastException("not a String");
        }
    }
    }

    /**
     * Returns the inclusive namespace prefix list. Each entry in the list
     * is a <code>String</code> that represents a namespace prefix.
     *
     * @return the inclusive namespace prefix list (may be empty but never
     *    <code>null</code>)
     */
    public List getPrefixList() {
    return preList;
    }
}
