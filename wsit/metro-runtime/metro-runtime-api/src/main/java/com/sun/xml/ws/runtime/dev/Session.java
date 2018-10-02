/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.runtime.dev;

import com.sun.xml.ws.security.SecurityContextTokenInfo;

import org.glassfish.gmbal.Description;
import org.glassfish.gmbal.ManagedAttribute;
import org.glassfish.gmbal.ManagedData;

/**
 * The <code> Session </Session> object is used to manage state between multiple requests
 * from the same client. It contains a session key field to uniquely identify the Session, 
 * a <code>SecurityInfo</code> field that contains the security parameters used to
 * protect the session and  userdata field that can store the state for multiple 
 * requests from the client.
 *
 * @author Bhakti Mehta
 * @author Mike Grogan
 */
@ManagedData
    @Description("RM and SC session information")
public class Session {

    /**
     * Well-known invocationProperty names
     */
    public static final String SESSION_ID_KEY = "com.sun.xml.ws.sessionid";
    public static final String SESSION_KEY = "com.sun.xml.ws.session";
    /**
     * Session manager that can handle Sessions of this exact type.
     * (Different SessionManagers might use different subclasses of this Class)
     */
    private SessionManager manager;
    /*
     * These fields might be persisted
     */
    /**
     * Unique key based either on the SCT or RM sequence id for the session
     */
    private String key;
    /**
     * A container for user-defined data that will be exposed in WebServiceContext.
     */
    private Object userData;

    private SecurityContextTokenInfo securityInfo;

    protected Session(){
    }

    /**
     * Public constructor
     *
     * @param manager - A <code>SessionManager</code> that can handle <code>Sessions</code>
     * of this type.  
     * @param key - The unique session id
     * @param data - Holder for user-defined data.
     */
    public Session(SessionManager manager, String key, Object userData) {
        this();
        
        this.manager = manager;
        this.userData = userData;
        this.key = key;
    }

    /**
     * Accessor for Session Key.
     *
     * @returns The session key
     */
    @ManagedAttribute
    @Description("Session key")
    public String getSessionKey() {
        return key;
    }

    /**
     * Accessor for the <code>userData</code> field.
     *
     * @return The value of the field.
     */
    public Object getUserData() {
        return userData;
    }

    /**
     * Accessor for the <code>securityInfo</code> field.
     * 
     * @returns The value of the field.
     */
    @ManagedAttribute
    @Description("Security context token info")
    public SecurityContextTokenInfo getSecurityInfo() {
        return securityInfo;
    }

    /**
     * Mutator for the <code>securityInfo</code> field.
     * 
     * @returns The value of the field.
     */
    public void setSecurityInfo(SecurityContextTokenInfo securityInfo) {
        this.securityInfo = securityInfo;
    }

    /**
     * Saves the state of the session using whatever persistence mechanism the
     * <code>SessionManager</code> offers.
     */
    public void save() {
        manager.saveSession(getSessionKey());
    }
}

