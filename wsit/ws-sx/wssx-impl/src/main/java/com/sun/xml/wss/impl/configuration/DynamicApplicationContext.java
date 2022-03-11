/*
 * Copyright (c) 2010, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * $Id: DynamicApplicationContext.java,v 1.2 2010-10-21 15:37:25 snajper Exp $
 */

package com.sun.xml.wss.impl.configuration;

import java.util.HashMap;

import com.sun.xml.wss.impl.policy.StaticPolicyContext;
import com.sun.xml.wss.impl.policy.DynamicPolicyContext;

/**
 * Represents a concrete SecurityPolicy identifier context resolved at runtime,
 * An XWS-Security <code>DynamicPolicyCallback</code> is passed an instance of
 * a <code>DynamicApplicationContext</code>. A callback Handler handling
 * DynamicPolicyCallback can make use of information in this context
 * to dynamically determine the Security policy applicable for a request/response
 */
public class DynamicApplicationContext extends DynamicPolicyContext {

    private String messageIdentifier = "";
    private boolean inBoundMessage = false;

    private StaticPolicyContext context = null;

    /**
     * Create an empty DynamicApplicationContext
     */
    public DynamicApplicationContext () {}

    /**
     * Create a DynamicApplicationContext with an associated
     * StaticPolicyContext <code>context</code>
     * @param context the associated StaticPolicyContext
     */
    public DynamicApplicationContext (StaticPolicyContext context) {
        this.context = context;
    }

    /**
     * Set the messageIdentifier for this Message associated with this context
     */
    public void setMessageIdentifier (String messageIdentifier) {
        this.messageIdentifier = messageIdentifier;
    }

    /**
     * @return messageIdentifier for the Message associated with this context
     */
    public String getMessageIdentifier () {
        return this.messageIdentifier;
    }

    /**
     * Set the Message direction (inbound/outbound) to which this context corresponds to
     * @param inBound flag indicating the direction of the message
     */
    public void inBoundMessage (boolean inBound) {
        this.inBoundMessage = inBound;
    }

    /**
     * @return true if the context is for an inbound message
     */
    public boolean inBoundMessage () {
        return this.inBoundMessage;
    }

    /**
     * set the associated StaticPolicyContext for this context
     * @param context StaticPolicyContext
     */
    public void setStaticPolicyContext (StaticPolicyContext context) {
        this.context = context;
    }

    /**
     * @return the associated StaticPolicContext if any, null otherwise
     */
    @Override
    public StaticPolicyContext getStaticPolicyContext () {
        return context;
    }

    /**
     * @return HashMap of runtime properties in this context
     */
    public HashMap getRuntimeProperties () {
        return properties;
    }

    /**
     * equals operator
     * @param ctx DynamicApplicationContext with which to compare for equality
     * @return true if ctx is equal to this DynamicApplicationContext
     */
    public boolean equals (DynamicApplicationContext ctx) {
        boolean b1 =
               getStaticPolicyContext().equals (ctx.getStaticPolicyContext());
        if (!b1) return false;

        boolean b2 =
            (messageIdentifier.equalsIgnoreCase (ctx.getMessageIdentifier()) &&
                inBoundMessage == ctx.inBoundMessage);
        if (!b2) return false;

        return true;
    }
}
