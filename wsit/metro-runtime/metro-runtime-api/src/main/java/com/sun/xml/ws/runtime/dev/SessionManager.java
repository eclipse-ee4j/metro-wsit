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

import com.sun.istack.logging.Logger;
import com.sun.xml.ws.api.server.WSEndpoint;
import com.sun.xml.ws.commons.AbstractMOMRegistrationAware;
import com.sun.xml.ws.commons.WSEndpointCollectionBasedMOMListener;
import com.sun.xml.ws.commons.MOMRegistrationAware;
import com.sun.xml.ws.security.IssuedTokenContext;
import com.sun.xml.ws.security.SecurityContextTokenInfo;
import com.sun.xml.ws.util.ServiceFinder;
import org.glassfish.gmbal.AMXMetadata;
import org.glassfish.gmbal.Description;
import org.glassfish.gmbal.ManagedAttribute;
import org.glassfish.gmbal.ManagedObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 *
 * The <code>SessionManager</code> is used to obtain session information
 * This can be implemented using persistent storage mechanisms or using transient storage
 * Even if it is implemented using persistent storage the implementation should take care 
 * of backing by  a cache which will avoid the overhead of serialization and database 
 * operations
 * <p>
 * Additionally the <code>SessionManager</code> is responsible for managing the life cycle
 * events for the sessions. It exposes methods to create and terminate the session
 * Periodically the <code>SessionManager</code> will  check for sessions who have been inactive for
 * a  predefined amount of time and then will terminate those sessions
 *
 * @author Bhakti Mehta
 * @author Mike Grogan
 */

@ManagedObject
@Description("Session manager used by RM and SC")
@AMXMetadata(type="WSRMSCSessionManager")
public abstract class SessionManager extends AbstractMOMRegistrationAware {
    private static final Logger LOGGER = Logger.getLogger(SessionManager.class);
    
    private static Properties config = null;

    private static final Object LOCK = new Object();
    private static final Map<WSEndpoint, SessionManager> SESSION_MANAGERS = new HashMap<WSEndpoint, SessionManager>();
    private static final WSEndpointCollectionBasedMOMListener listener;
    public static final String TIMEOUT_INTERVAL = "session-timeout";
    public static final String SESSION_THRESHOLD = "session-threshold";

    
    static {
        listener = new WSEndpointCollectionBasedMOMListener(LOCK, "RM_SC_SessionManager", SESSION_MANAGERS);
        listener.initialize();
    }
    
     /**
     * @return the config
     */
    public static Properties getConfig() {
        return config;
    }

    /**
     * @param aConfig the config to set
     */
    public static void setConfig(Properties aConfig) {
        config = aConfig;
    }


    /**
     * Returns an existing session identified by the Key else null
     *
     * @param key The Session key.
     * @returns The Session with the given key.  <code>null</code> if none exists.
     */
    public abstract Session  getSession(String key) ;

    /**
     * Returns the Set of valid Session keys.
     *
     * @returns The Set of keys.
     */
    @ManagedAttribute
    @Description("The set of valid Session keys")
    public abstract Set<String> keys();

    /**
     * @returns The Collection of valid Sessions.
     */
    @ManagedAttribute
    @Description("The collection of valid Sessions")
    protected abstract Collection<Session> sessions();

    /**
     * Removed the Session with the given key.
     *
     * @param key The key of the Session to be removed.
     */
    public abstract void terminateSession(String key);

    /**
     * Creates a Session with the given key, using a new instance
     * of the specified Class as a holder for user-defined data.  The
     * specified Class must have a default ctor.
     *
     * @param key The Session key to be used.
     * @returns The new Session.. <code>null</code> if the given
     * class cannot be instantiated.
     * 
     */ 
    public abstract Session createSession(String key, Class clasz);
    
     /**
     * Creates a Session with the given key, using the specified Object
     * as a holder for user-defined data.
     *
     * @param key The Session key to be used.
     * @param obj The object to use as a holder for user data in the session.
     * @returns The new Session. 
     * 
     */ 
    public abstract Session createSession(String key, Object obj);
    
    public abstract Session createSession(String key, SecurityContextTokenInfo sctInfo);
    
     /**
     * Creates a Session with the given key, using an instance of 
     * java.util.Hashtable<String, String> asa holder for user-defined data.
     *
     * @param key The Session key to be used.
     * @returns The new Session.
     * 
     */ 
    public abstract Session createSession(String key);
    
     
    /**
     * Saves the state of the Session with the given key.
     *
     * @param key The key of the session to be saved
     */
    public abstract void saveSession(String key);
    
    /**
     * Return the valid SecurityContext for matching key
     *
     * @param key The key of the security context to be looked
     * @param expiryCheck indicates whether to check the token expiry or not, 
     *                    As in case of renew we don't need to check token expiry
     * @returns IssuedTokenContext for security context key
     */
    public abstract IssuedTokenContext getSecurityContext(String key, boolean checkExpiry);
    
    /**
     * Add the SecurityContext with key in local cache
     *
     * @param key The key of the security context to be stored     
     * @param itctx The IssuedTokenContext to be stored
     */
    public abstract void addSecurityContext(String key, IssuedTokenContext itctx);
    
    public static void removeSessionManager(WSEndpoint endpoint){
        synchronized (LOCK) {
            try {
                LOGGER.entering();
                Object o = SESSION_MANAGERS.remove(endpoint);
                LOGGER.config(String.format("removeSessionManager(%s): %s",
                                            endpoint, o));
                SessionManager sessionManager = (SessionManager) o;
                if (sessionManager != null && sessionManager.isRegisteredAtMOM()) {
                    listener.unregisterFromMOM(sessionManager, endpoint);
                }
            } finally {
                LOGGER.exiting();
            }
        }
    }

    /**
     * Returns the single instance of SessionManager
     * Use the usual services mechanism to find implementing class.  If not
     * found, use <code>com.sun.xml.ws.runtime.util.SessionManager</code> 
     * by default.
     *
     * @return The value of the <code>manager</code> field.
     */ 
    public static SessionManager getSessionManager(WSEndpoint endpoint, boolean isSC, Properties props) {
        synchronized (LOCK) {
            try {
                LOGGER.entering();
                SessionManager sm = SESSION_MANAGERS.get(endpoint);
                if (sm == null) {
                    ServiceFinder<SessionManager> finder = 
                        ServiceFinder.find(SessionManager.class);
                    if (finder != null && finder.toArray().length > 0) {
                        sm = finder.toArray()[0];
                    } else {
                        sm = new SessionManagerImpl(endpoint, isSC, props);
                    }
                    SESSION_MANAGERS.put(endpoint, sm);
                    if (listener.canRegisterAtMOM()) {
                        listener.registerAtMOM(sm, endpoint);
                    }
                    LOGGER.config(String.format("getSessionManager(%s): created: %s", endpoint, sm));
                } else {
                    LOGGER.config(String.format("getSessionManager(%s): found existing: %s", endpoint, sm));
                }
                return sm;
            } finally {
                LOGGER.exiting();
            }
        }
    }

    public static SessionManager getSessionManager(WSEndpoint endpoint, Properties props){
         return getSessionManager(endpoint, false, props);
     }
}

