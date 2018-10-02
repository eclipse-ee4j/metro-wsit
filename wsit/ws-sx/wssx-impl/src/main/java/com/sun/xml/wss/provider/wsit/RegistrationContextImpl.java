/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.provider.wsit;

import javax.security.auth.message.config.AuthConfigFactory.RegistrationContext;

/*
 * Class used by GFAuthConfigFactory and EntryInfo.
 *
 * This class will not be used outside of its package.
 */
final class RegistrationContextImpl implements RegistrationContext {
    private final String messageLayer;
    private final String appContext;
    private final String description;
    private final boolean isPersistent;

    RegistrationContextImpl(String messageLayer, String appContext,
        String description, boolean persistent) {
        
        this.messageLayer = messageLayer;
        this.appContext = appContext;
        this.description = description;
        this.isPersistent = persistent;
    }

    // helper method to create impl class
    RegistrationContextImpl(RegistrationContext ctx) {
        this.messageLayer = ctx.getMessageLayer();
        this.appContext = ctx.getAppContext();
        this.description = ctx.getDescription();
        this.isPersistent = ctx.isPersistent();
    }
    
    public String getMessageLayer() {
        return messageLayer;
    }

    public String getAppContext() {
        return appContext;
    }

    public String getDescription() {
        return description;
    }

    public boolean isPersistent() {
        return isPersistent;
    }
 
    public boolean equals(Object o) {
        if (o == null || !(o instanceof RegistrationContext)) {
            return false;
        }
        RegistrationContext target = (RegistrationContext) o;
        return ( EntryInfo.matchStrings(
            messageLayer, target.getMessageLayer()) &&
            EntryInfo.matchStrings(appContext, target.getAppContext()) &&
            EntryInfo.matchStrings(description, target.getDescription()) );
    }
}
