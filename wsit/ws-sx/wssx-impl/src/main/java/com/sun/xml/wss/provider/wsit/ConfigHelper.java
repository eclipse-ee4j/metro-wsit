/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.provider.wsit;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import jakarta.security.auth.message.AuthException;
import jakarta.security.auth.message.MessageInfo;
import jakarta.security.auth.message.config.AuthConfig;
import jakarta.security.auth.message.config.AuthConfigFactory;
import jakarta.security.auth.message.config.AuthConfigProvider;
import jakarta.security.auth.message.config.ClientAuthConfig;
import jakarta.security.auth.message.config.ClientAuthContext;
import jakarta.security.auth.message.config.RegistrationListener;
import jakarta.security.auth.message.config.ServerAuthConfig;
import jakarta.security.auth.message.config.ServerAuthContext;
import jakarta.security.auth.message.config.AuthConfigFactory.RegistrationContext;

/**
 * This is based Helper class for 196 Configuration.
 * This class implements RegistrationListener. 
 */
public abstract class ConfigHelper /*implements RegistrationListener*/ {
    private static final String DEFAULT_HANDLER_CLASS =
        "com.sun.enterprise.security.jmac.callback.ContainerCallbackHandler"; 

    private static final String HANDLER_CLASS_PROPERTY =
        "security.jmac.config.ConfigHelper.CallbackHandler";
    
    private static final  String JMAC_CALLBACK_PROP="META-INF/services/jakarta.security.auth.message.callback.CallbackHandler";
    private static String handlerClassName = null;
    protected AuthConfigFactory factory = null;

    private ReadWriteLock rwLock;
    private Lock rLock;
    private Lock wLock;

   
	
    protected String layer;
    protected String appCtxt;
    protected Map<Object, Object> map;
    protected CallbackHandler cbh;
    protected AuthConfigRegistrationWrapper listenerWrapper = null;

    protected void init(String layer, String appContext,
            Map<Object, Object> map, CallbackHandler cbh) {

        factory = AuthConfigFactory.getFactory();
	this.layer = layer;
	this.appCtxt = appContext;
	this.map = map;
	this.cbh = cbh;
        if (this.cbh == null) {
            this.cbh = getCallbackHandler();
        }

	this.rwLock = new ReentrantReadWriteLock(true);
        this.rLock = rwLock.readLock();
        this.wLock = rwLock.writeLock();
        
        listenerWrapper =  new  AuthConfigRegistrationWrapper(this.layer, this.appCtxt);
        
    }

    public void setJmacProviderRegisID(String jmacProviderRegisID) {
        this.listenerWrapper.setJmacProviderRegisID(jmacProviderRegisID);
    }
    
    public AuthConfigRegistrationWrapper getRegistrationWrapper() {
        return this.listenerWrapper;
    }
    
    public void setRegistrationWrapper(AuthConfigRegistrationWrapper wrapper) {
        this.listenerWrapper = wrapper;
    }
    
    public AuthConfigRegistrationWrapper.AuthConfigRegistrationListener getRegistrationListener() {
        return this.listenerWrapper.getListener();
    }

    public void disable() {
	listenerWrapper.disable();
    }

    public Object getProperty(String key) {
	return map == null ? null : map.get(key);
    }

    public String getAppContextID() {
	return appCtxt;
    }

    public ClientAuthConfig getClientAuthConfig() throws AuthException {
        return (ClientAuthConfig)getAuthConfig(false);
    }

    public ServerAuthConfig getServerAuthConfig() throws AuthException {
        return (ServerAuthConfig)getAuthConfig(true);
    }

    public ClientAuthContext getClientAuthContext(MessageInfo info, Subject s) 
    throws AuthException {
	ClientAuthConfig c = (ClientAuthConfig)getAuthConfig(false);
	if (c != null) {
	    return c.getAuthContext(c.getAuthContextID(info),s,map);
	}
	return null;
    }

    public ServerAuthContext getServerAuthContext(MessageInfo info, Subject s) 
    throws AuthException {
	ServerAuthConfig c = (ServerAuthConfig)getAuthConfig(true);
	if (c != null) {
	    return c.getAuthContext(c.getAuthContextID(info),s,map);
	}
	return null;
    }

    protected AuthConfig getAuthConfig(AuthConfigProvider p, boolean isServer) 
    throws AuthException {
	AuthConfig c = null; 
	if (p != null) {
	    if (isServer) { 
		c = p.getServerAuthConfig(layer, appCtxt, cbh);
	    } else {
		c = p.getClientAuthConfig(layer, appCtxt, cbh);
	    }
	}
	return c;
    }

    protected AuthConfig getAuthConfig(boolean isServer) throws AuthException {

	ConfigData d = null;
	AuthConfig c = null;
	boolean disabled = false;
	AuthConfigProvider lastP = null;

	try {
	    rLock.lock();
	    disabled = (!listenerWrapper.isEnabled());
	    if (!disabled) {
                d = listenerWrapper.getConfigData();
		if (d != null) {
		    c = (isServer ? d.sConfig : d.cConfig);
		    lastP = d.provider;
		}
	    }
	    
	} finally {
	    rLock.unlock();
	    if (disabled || c != null || (d != null && lastP == null)) {
		return c;
	    }
	} 


	// d == null || (d != null && lastP != null && c == null)
	if (d == null) {
	    try {
		wLock.lock();
                if (listenerWrapper.getConfigData()== null) {
                    AuthConfigProvider nextP =
                        factory.getConfigProvider(layer,appCtxt,this.getRegistrationListener());
                    if (nextP != null) {
                        listenerWrapper.setConfigData(new ConfigData(nextP, getAuthConfig(nextP, isServer)));
                    } else {
                        listenerWrapper.setConfigData(new ConfigData());
                    }
                }
                d = listenerWrapper.getConfigData();
	    } finally {
		wLock.unlock();
	    }
	} 

        return ((isServer)? d.sConfig : d.cConfig);
    }

    /**
     * Check if there is a provider register for a given layer and appCtxt.
     */
    protected boolean hasExactMatchAuthProvider() {
        boolean exactMatch = false;
        // XXX this may need to be optimized
        AuthConfigProvider p = 
                factory.getConfigProvider(layer, appCtxt, null);
        if (p != null) {
            String[] IDs = factory.getRegistrationIDs(p);
            for (String i : IDs) {
                RegistrationContext c = factory.getRegistrationContext(i);
                if (layer.equals(c.getMessageLayer()) && 
                        appCtxt.equals(c.getAppContext())) {
                    exactMatch = true;
                    break;
                }
            }
        }

        return exactMatch;
    }

    /**
     * Get the callback default handler
     */
    private CallbackHandler getCallbackHandler() {

        CallbackHandler rvalue = getDefaultCallbackHandler();
        //TODO: get it from META-INF
        return rvalue;
    }
    
    public static URL loadFromClasspath(final String configFileName, ClassLoader cl) {
        if (cl == null) {
            return ClassLoader.getSystemResource(configFileName);
        } else {
            return cl.getResource(configFileName);
        }
    }
    
    @SuppressWarnings("unchecked")
    public  CallbackHandler getDefaultCallbackHandler() {
        // get the default handler class
        try {
            final ClassLoader loader =
                            Thread.currentThread().getContextClassLoader();
            CallbackHandler rvalue = 
                (CallbackHandler)AccessController.doPrivileged
                (new PrivilegedExceptionAction() {
                    @Override
                    public Object run() {
                            //JMAC Property takes precedence
                            URL url = loadFromClasspath(JMAC_CALLBACK_PROP, loader);
                            if (url != null) {
                                InputStream is = null;
                                try {
                                    is = url.openStream();
                                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                                    int val = is.read();
                                    while (val != -1) {
                                        os.write(val);
                                        val = is.read();
                                     }
                                    handlerClassName = os.toString();
                                    
                                 } catch (IOException ex) {
                                     Logger.getLogger(ConfigHelper.class.getName()).log(Level.SEVERE, null, ex);
                                 } finally {
                                     try {
                                         is.close();
                                     } catch (IOException ex) {
                                      Logger.getLogger(ConfigHelper.class.getName()).log(Level.SEVERE, null, ex);
                                     }
                                 }
                            }
                        if (handlerClassName == null) {
                            handlerClassName = System.getProperty(
                                HANDLER_CLASS_PROPERTY, DEFAULT_HANDLER_CLASS);
                        }
                        final String className = handlerClassName;
                        //Class c = Class.forName(className, true, loader);
                        //return c.newInstance();
                        return loadGFHandler(className, loader);
                    }
                });
            return rvalue;

        } catch(PrivilegedActionException pae) {
            throw new RuntimeException(pae.getException());
        }
    }

    protected CallbackHandler loadGFHandler(String jmacHandler, ClassLoader loader) {   
        String classname =  jmacHandler;
        Class ret = null;
        try {
            try {
                if (loader != null) {
                    ret = loader.loadClass(classname);
                }
            }catch(ClassNotFoundException e) {
                
            }
            
            if (ret == null) {
                // if context classloader didnt work, try this
                loader = this.getClass().getClassLoader();
                ret = loader.loadClass(classname);
            }
            
            if (ret != null) {
                CallbackHandler handler = (CallbackHandler)ret.newInstance();
                return handler;
            }
        } catch (ClassNotFoundException e) {
            // ignore
            
        } catch(InstantiationException | IllegalAccessException e) {
            
        }
        if (DEFAULT_HANDLER_CLASS.equals(classname)) {
            return null;
        }
        throw new RuntimeException("Failed to Load CallbackHandler:" + classname);
    }

    private static class ConfigData {

	private AuthConfigProvider provider; 
	private AuthConfig sConfig; 
	private AuthConfig cConfig; 

	ConfigData() {
	    provider = null;
	    sConfig = null;
	    cConfig = null;
	}

	ConfigData(AuthConfigProvider p, AuthConfig a) {
	    provider = p;
	    if (a == null) {
		sConfig = null;
		cConfig = null;
	    } else if (a instanceof ServerAuthConfig) {
		sConfig = a;
		cConfig = null;
	    } else if (a instanceof ClientAuthConfig) {
		sConfig = null;
		cConfig = a;
	    } else {
		throw new IllegalArgumentException();
	    }
	}
    }
    
    //Adding extra inner class because specializing the Linstener Impl class would 
    //make the GF 196 implementation Non-Replaceable.
    // This class would hold a RegistrationListener within.
    public class AuthConfigRegistrationWrapper {
        
        private String layer;
        private String appCtxt;
        private String jmacProviderRegisID = null;
        private boolean enabled;
        private ConfigData data;
        
        private Lock wLock;
        private ReadWriteLock rwLock;
        
        AuthConfigRegistrationListener listener;
        int referenceCount = 1;
        
        public AuthConfigRegistrationWrapper(String layer, String appCtxt) {
            this.layer = layer;
            this.appCtxt = appCtxt;
            this.rwLock = new ReentrantReadWriteLock(true);
	    this.wLock = rwLock.writeLock();
            enabled = (factory != null);
            listener = new AuthConfigRegistrationListener(layer, appCtxt);
        }
        
        public AuthConfigRegistrationListener getListener() {
            return listener;
        }
        
        public void setListener(AuthConfigRegistrationListener listener) {
            this.listener = listener;
        }
        
        public void disable() {
            try {
                this.wLock.lock();
                setEnabled(false);
            } finally {
                data = null;
                this.wLock.unlock();
            }
            if (factory != null) {
                String[] ids = factory.detachListener(this.listener,layer,appCtxt);
                if (ids != null) {
                    for (int i=0; i < ids.length; i++) {
                        factory.removeRegistration(ids[i]);
                    }
                }
                if (getJmacProviderRegisID() != null) {
                    factory.removeRegistration(getJmacProviderRegisID());
                }
            }
        }

        //detach the listener, but dont remove-registration
        public void disableWithRefCount() {
            if (referenceCount <= 1) {
               disable();
            } else {
                try {
                    this.wLock.lock();
                    referenceCount--;
                } finally {
                    this.wLock.unlock();
                }
                
            }
        }
        
        public void incrementReference() {
            try {
                this.wLock.lock();
                referenceCount++;
            } finally {
                this.wLock.unlock();
            }
        }
        
        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getJmacProviderRegisID() {
            return this.jmacProviderRegisID;
        }
        
        public void setJmacProviderRegisID(String jmacProviderRegisID) {
            this.jmacProviderRegisID = jmacProviderRegisID;
        }
        
        public ConfigData getConfigData() {
            return data;
        }
        
        public void setConfigData(ConfigData data) {
            this.data = data;
        }
         
    
        public class AuthConfigRegistrationListener implements RegistrationListener {
            
            private String layer;
            private String appCtxt;
            
            public AuthConfigRegistrationListener(String layer, String appCtxt) {
                this.layer = layer;
                this.appCtxt = appCtxt;
            }
            
            @Override
            public void notify(String layer, String appContext) {
                if (this.layer.equals(layer) &&
                        ((this.appCtxt == null && appContext == null) ||
                        (appContext != null && appContext.equals(this.appCtxt)))) {
                    try {
                        wLock.lock();
                        data = null;
                    } finally {
                        wLock.unlock();
                    }
                }
            }
            
        }
    }
    
}
