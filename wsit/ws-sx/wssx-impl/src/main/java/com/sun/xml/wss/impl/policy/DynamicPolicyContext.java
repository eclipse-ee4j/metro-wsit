/*
 * Copyright (c) 2010, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * $Id: DynamicPolicyContext.java,v 1.2 2010-10-21 15:37:33 snajper Exp $
 */

package com.sun.xml.wss.impl.policy;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Represents a SecurityPolicy identifier context resolved at runtime
 */
public abstract class DynamicPolicyContext {

    /* Represents extraneous properties */
    protected HashMap properties = new HashMap ();

    /**
     * get the named property
     * @param name property name
     * @return Object property value
     */
    protected Object getProperty (String name) {
        return properties.get(name);
    }

    /**
     * set the named property to value <code>value</code>.
     * @param name property name
     * @param value property value
     */
    @SuppressWarnings("unchecked")
    protected void setProperty (String name, Object value) {
        properties.put (name, value);
    }

    /**
     * remove the named property
     * @param name property to be removed
     */
    protected void removeProperty (String name) {
        properties.remove (name);
    }

    /**
     * @param name property to be checked for presence
     * @return true if the property <code>name</code> is present.
     */
    protected boolean containsProperty (String name) {
        return properties.containsKey(name);
    }

    /**
     * @return Iterator over the property names
     */
    protected Iterator getPropertyNames () {
        return properties.keySet ().iterator();
    }

   /**
    * @return Any <code>StaticPolicyContext</code> associated with this context.
    */
    public abstract StaticPolicyContext getStaticPolicyContext ();
}
