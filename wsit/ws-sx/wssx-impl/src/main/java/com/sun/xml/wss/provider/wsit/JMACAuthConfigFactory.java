/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.provider.wsit;

import com.sun.xml.wss.provider.wsit.logging.LogDomainConstants;
import com.sun.xml.wss.provider.wsit.logging.LogStringsMessages;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.message.config.AuthConfigFactory;
import javax.security.auth.message.config.AuthConfigProvider;
import javax.security.auth.message.config.RegistrationListener;
import jakarta.xml.ws.WebServiceException;
    
/**
 * This class implements methods in the abstract class AuthConfigFactory.
 * @author  Shing Wai Chan
 */
public class JMACAuthConfigFactory extends AuthConfigFactory {

    private static Logger logger =Logger.getLogger(
            LogDomainConstants.WSIT_PVD_DOMAIN,
            LogDomainConstants.WSIT_PVD_DOMAIN_BUNDLE);


    // locks are used to protect existence of maps 
    // not concurrent access within maps
    private static final  String AUTH_CONFIG_PROVIDER_PROP="META-INF/services/javax.security.auth.message.config.AuthConfigProvider";
    private static ReadWriteLock rwLock;
    private static Lock rLock;
    private static Lock wLock;

    private static Map<String, AuthConfigProvider> id2ProviderMap;
    private static Map<String, RegistrationContext> id2RegisContextMap;
    private static Map<String, List<RegistrationListener>> id2RegisListenersMap;
    private static Map<AuthConfigProvider, List<String>> provider2IdsMap;
    
    private static final String CONF_FILE_NAME = "auth.conf";
    private static  RegStoreFileParser regStore;
    private ClassLoader loader;
    static {
	rwLock = new ReentrantReadWriteLock(true);
	rLock = rwLock.readLock();
	wLock = rwLock.writeLock();   
    }

    // XXX read declarative persistent repository construct an
    // register AuthConfigProviders as appropriate.
    public JMACAuthConfigFactory(ClassLoader loader) {
        this.loader = loader;
        regStore = new RegStoreFileParser(System.getProperty("user.dir"),
              CONF_FILE_NAME, false);
	_loadFactory();
    }

    /**
     * Get a registered AuthConfigProvider from the factory.
     *
     * Get the provider of ServerAuthConfig and/or 
     * ClientAuthConfig objects registered for the identified message 
     * layer and application context.
     *
     * @param layer a String identifying the message layer
     *		for which the registered AuthConfigProvider is
     *          to be returned. This argument may be null.
     *
     * @param appContext a String that identifys the application messaging 
     *          context for which the registered AuthConfigProvider
     *          is to be returned. This argument may be null.
     *
     * @param listener the RegistrationListener whose 
     *          <code>notify</code> method is to be invoked 
     *          if the corresponding registration is unregistered or 
     *          replaced. The value of this argument may be null.
     *
     * @return the implementation of the AuthConfigProvider interface 
     *          registered at the factory for the layer and appContext
     *          or null if no AuthConfigProvider is selected.
     *
     * <p>All factories shall employ the following precedence rules to select
     * the registered AuthConfigProvider that matches the layer and appContext 
     * arguments:
     *<ul>
     * <li> The provider that is specifically registered for both the 
     * corresponding message layer and appContext 
     * shall be selected.
     * <li> if no provider is selected according to the preceding rule,
     * the provider specifically registered for the 
     * corresponding appContext and for all message layers
     * shall be selected.
     * <li> if no provider is selected according to the preceding rules,
     * the provider specifically registered for the
     * corresponding message layer and for all appContexts
     * shall be selected.
     * <li> if no provider is selected according to the preceding rules,
     * the provider registered for all message layers and for all 
     * appContexts shall be selected.
     * <li> if no provider is selected according to the preceding rules,
     * the factory shall terminate its search for a registered provider.
     *</ul>
     */
    public AuthConfigProvider 
            getConfigProvider(String layer, String appContext,
	    RegistrationListener listener) {

	AuthConfigProvider provider = null;
        String regisID = getRegistrationID(layer, appContext);
        rLock.lock();
	try {
	    provider = id2ProviderMap.get(regisID);
            if (provider == null) {
                provider = id2ProviderMap.get(getRegistrationID(null, appContext));
            }
	    if (provider == null) {
		provider = id2ProviderMap.get(getRegistrationID(layer, null));
	    }
	    if (provider == null) {
		provider = id2ProviderMap.get(getRegistrationID(null, null));
	    }
	} finally {
	    rLock.unlock();
	}

        if (listener != null) {
            // do this check first to try to optimize the multiple thread env
            boolean lregister = false;
            rLock.lock();
            try {
                List<RegistrationListener> listeners =
                    id2RegisListenersMap.get(regisID);
                if (listeners != null) {
                    lregister = listeners.contains(listener);
                }
            } finally {
                rLock.unlock();
            }

            if (!lregister) {
                wLock.lock();
                try {
                    List<RegistrationListener> listeners =
                        id2RegisListenersMap.get(regisID);
                    if (listeners == null) {
                        listeners = new ArrayList<RegistrationListener>();
                        id2RegisListenersMap.put(regisID, listeners);
                    }
                    if (!listeners.contains(listener)) {
                        listeners.add(listener);
                    }
                } finally {
                    wLock.unlock();
                }
            }
        }
        return provider;
    }

    /**
     * Registers within the factory, a provider 
     * of ServerAuthConfig and/or ClientAuthConfig objects for a 
     * message layer and application context identifier.
     * 
     * <P>At most one registration may exist within the factory for a 
     * given combination of message layer 
     * and appContext. Any pre-existing
     * registration with identical values for layer and appContext is replaced 
     * by a subsequent registration. When replacement occurs, the registration
     * identifier, layer, and appContext identifier remain unchanged,
     * and the AuthConfigProvider (with initialization properties) and 
     * description are replaced. 
     *
     *<p>Within the lifetime of its Java process, a factory must assign unique
     * registration identifiers to registrations, and must never 
     * assign a previously used registration identifier to a registration 
     * whose message layer and or appContext identifier differ from 
     * the previous use.
     * 
     * <p>Programmatic registrations performed via this method must update
     * (according to the replacement rules described above), the persistent 
     * declarative representation of provider registrations employed by the 
     * factory constructor.
     *
     * @param className the fully qualified name of an AuthConfigProvider
     *          implementation class. This argument must not be null.
     *
     * @param properties a Map object containing the initialization 
     *          properties to be passed to the provider constructor.
     *          This argument may be null. When this argument is not null, 
     *          all the values and keys occuring in the Map must be of 
     *          type String.
     *
     * @param layer a String identifying the message layer
     *		for which the provider will be registered at the factory.
     *          A null value may be passed as an argument for this parameter,
     *          in which case, the provider is registered at all layers.
     *
     * @param appContext a String value that may be used by a runtime
     *          to request a configuration object from this provider.
     *          A null value may be passed as an argument for this parameter,
     *          in which case, the provider is registered for all 
     *          configuration ids (at the indicated layers).
     *
     * @param description a text String descripting the provider.
     *          this value may be null.
     *
     * @return a String identifier assigned by 
     *          the factory to the provider registration, and that may be 
     *          used to remove the registration from the provider.
     *
     * @exception SecurityException if the caller does not have
     *		permission to register a provider at the factory.
     *
     * @exception AuthException if the provider 
     *          construction or registration fails.
     */
    @SuppressWarnings("unchecked")
    public String registerConfigProvider(String className,
					 Map properties, 
					 String layer, String appContext, 
					 String description) { 
        //XXX do we need doPrivilege here
        AuthConfigProvider provider =
            _constructProvider(className, properties, null);
        return _register(provider,properties,
            layer,appContext,description,true);
    }

    public String registerConfigProvider(AuthConfigProvider provider,
            String layer, String appContext, String description) {
	return _register(provider,null,layer,appContext,description,false);
    }

    /**
     * Remove the identified provider registration from the factory
     * and invoke any listeners associated with the removed registration.
     *
     * @param registrationID a String that identifies a provider registration
     *          at the factory
     *
     * @return true if there was a registration with the specified identifier 
     *          and it was removed. Return false if the registraionID was
     *          invalid.
     *
     * @exception SecurityException if the caller does not have
     *		permission to unregister the provider at the factory.
     *
     */
    public boolean removeRegistration(String registrationID) {
        return _unRegister(registrationID);
    }

    /**
     * Disassociate the listener from all the provider
     * registrations whose layer and appContext values are matched
     * by the corresponding arguments to this method.
     *
     * @param listener the RegistrationListener to be detached.
     *
     * @param layer a String identifying the message layer or null.
     *
     * @param appContext a String value identifying the application contex
     *          or null.
     *
     * @return an array of String values where each value identifies a 
     *          provider registration from which the listener was removed.
     *          This method never returns null; it returns an empty array if 
     *          the listener was not removed from any registrations.
     *
     * @exception SecurityException if the caller does not have
     *		permission to detach the listener from the factory.
     *
     */
    public String[] detachListener(RegistrationListener listener,
            String layer, String appContext) {
        String regisID = getRegistrationID(layer, appContext);
        wLock.lock();
        try {   
            RegistrationListener ler = null;
            List<RegistrationListener> listeners =
                id2RegisListenersMap.get(regisID);
            if (listeners != null && listeners.remove(listener)) {
                   ler = listener;
            }
            return (ler != null)? new String[]{ regisID } : new String[0];
        } finally {
            wLock.unlock();
        }
    }

    /**
     * Get the registration identifiers for all registrations of the 
     * provider instance at the factory.
     *
     * @param provider the AuthConfigurationProvider whose registration
     *          identifiers are to be returned. This argument may be
     *          null, in which case, it indicates that the the id's of
     *          all active registration within the factory are returned.
     *
     * @return an array of String values where each value identifies a 
     * provider registration at the factory. This method never returns null;
     * it returns an empty array when their are no registrations at the 
     * factory for the identified provider.
     */
    public String[] getRegistrationIDs(AuthConfigProvider provider) {
        rLock.lock();
        try {
            Collection<String> regisIDs = null;
            if (provider != null) {
                regisIDs = provider2IdsMap.get(provider);
            } else {
                Collection<List<String>> collList = provider2IdsMap.values();
                if (collList != null) {
                    regisIDs = new HashSet<String>();
                    for (List<String> listIds : collList) {
                         if (listIds != null) {
                             regisIDs.addAll(listIds);
                         }
                    }
                }
            }
            return ((regisIDs != null)?
                regisIDs.toArray(new String[regisIDs.size()]) :
                new String[0]);
        } finally {
            rLock.unlock();
        }
    }

    /**
     * Get the the registration context for the identified registration.
     *
     * @param registrationID a String that identifies a provider registration
     *          at the factory
     *
     * @return a RegistrationContext or null. When a Non-null value is
     * returned, it is a copy of the registration context corresponding to the
     * registration. Null is returned when the registration identifier does
     * not correpond to an active registration
      */
    public RegistrationContext getRegistrationContext(String registrationID) {
        rLock.lock();
	try {
	    return id2RegisContextMap.get(registrationID);
	} finally {
	    rLock.unlock();
	}
    }

   /**
     * Cause the factory to reprocess its persisent declarative 
     * representation of provider registrations. 
     *
     * <p> A factory should only replace an existing registration when 
     * a change of provider implementation class or initialization 
     * properties has occured. re
     *
     * @exception AuthException if an error occured during the 
     *          reinitialization.
     *
     * @exception SecurityException if the caller does not have permission
     *		to refresh the factory.
     */
    public void refresh() {
	_loadFactory();
    }

    /*
     * Contains the default providers used when none are
     * configured in a factory configuration file.
     */
    static List<EntryInfo> getDefaultProviders() {
        /*
        List<EntryInfo> entries = new ArrayList<EntryInfo>(2);
        entries.add(new EntryInfo(
            "com.sun.xml.wss.provider.wsit.WSITAuthConfigProvider", null));
        return entries;*/
        List<EntryInfo> entries = new ArrayList<EntryInfo>(1);
        URL url = loadFromClasspath(AUTH_CONFIG_PROVIDER_PROP);
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
                String classname = os.toString();
                entries.add(new EntryInfo(classname, null));

            } catch (IOException ex) {
                logger.log(Level.SEVERE, LogStringsMessages.WSITPVD_0062_ERROR_LOAD_DEFAULT_PROVIDERS(), ex);
                throw new WebServiceException(ex);
            } finally {
                try {
                    is.close();
                } catch (IOException ex) {
                    logger.log(Level.WARNING, LogStringsMessages.WSITPVD_0062_ERROR_LOAD_DEFAULT_PROVIDERS(), ex);
                }
            }
        } else {
            entries.add(new EntryInfo(
                    "com.sun.xml.wss.provider.wsit.WSITAuthConfigProvider", null));
        }
        return entries;
        
    }
    
    public static URL loadFromClasspath(final String configFileName) {
        final ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader == null) {
            return ClassLoader.getSystemResource(configFileName);
        } else {
            return loader.getResource(configFileName);
        }
    }
    
    private static String getRegistrationID(String layer, String appContext) {
        String regisID = null;

        // __0                          (null, null)
        // __1<appContext>              (null, appContext)
        // __2<layer>                   (layer, null)
        // __3<nn>_<layer><appContext>  (layer, appContext)
        
        if (layer != null) {
            regisID = (appContext != null) ? 
                "__3" + layer.length() + "_" + layer + appContext :
                "__2" + layer;
        } else {
            regisID = (appContext != null) ?
                "__1" + appContext :
                "__0";
        }
        return regisID;
    }

    /**
     * This API decomposes the given regisID into layer and appContext.
     * @param regisID
     * @return a String array with layer and appContext
     */
    private static String[] decomposeRegisID(String regisID) {
        String layer = null;
        String appContext = null;
        if (regisID.equals("__0")) {
            // null, null
        } else if (regisID.startsWith("__1")) {
            appContext = (regisID.length() == 3)?
                   "" : regisID.substring(3);
        } else if (regisID.startsWith("__2")) {
            layer = (regisID.length() == 3)?
                   "" : regisID.substring(3);
        } else if (regisID.startsWith("__3")) {
            int ind = regisID.indexOf('_', 3);
            if (regisID.length() > 3 && ind > 0) {
                String numberString = regisID.substring(3, ind);
                int n;
                try {
                     n = Integer.parseInt(numberString);
                } catch(Exception ex) {
                     throw new IllegalArgumentException();
                }
                layer = regisID.substring(ind + 1, ind + 1 + n);
                appContext = regisID.substring(ind + 1 + n);
            } else {
                throw new IllegalArgumentException();
            }
        } else {
            throw new IllegalArgumentException();
        }

        return new String[] { layer, appContext };
    }

    @SuppressWarnings("unchecked")
    private AuthConfigProvider _constructProvider
    (String className, Map properties, AuthConfigFactory factory) {
        //XXX do we need doPrivilege here
        AuthConfigProvider provider = null;
	if (className != null) {
	    try {
                if (loader == null) {
                    loader = Thread.currentThread().getContextClassLoader();
                }
		Class c = Class.forName(className, true, loader);
		Constructor<AuthConfigProvider> constr =
		    c.getConstructor(Map.class, AuthConfigFactory.class);
		provider = constr.newInstance
		    (new Object[] {properties, factory} );
	    } catch(Exception ex) {
                if (logger.isLoggable(Level.FINE)) {
                    logger.log(Level.FINE,
                        "Cannot load AuthConfigProvider: " + className, ex); 
                } else if (logger.isLoggable(Level.WARNING)) {
                    logger.log(Level.WARNING,
                        LogStringsMessages.WSITPVD_0060_JMAC_JMAC_FACTORY_UNABLETO_LOAD_PROVIDER(className),
                         new String [] { className, ex.toString() });
                }
	    }
	}
	return provider;
    }
     
    //XXX need to update persistent state and notify effected listeners
    private static String _register(AuthConfigProvider provider,
				    Map<String, String> properties,
				    String layer, 
				    String appContext, 
				    String description, 
				    boolean persist) {
 
	String regisID = getRegistrationID(layer, appContext);
	RegistrationContext rc = 
	    new RegistrationContextImpl(layer,appContext,description,persist);
	RegistrationContext prevRegisContext = null; 
        List<RegistrationListener> listeners = null;
        wLock.lock();
	try {
	    prevRegisContext = id2RegisContextMap.get(regisID);
            AuthConfigProvider prevProvider = id2ProviderMap.get(regisID);
	    id2ProviderMap.put(regisID, provider);
	    id2RegisContextMap.put(regisID, rc);

            if (prevProvider != null) {
                List<String> prevRegisIDs = provider2IdsMap.get(prevProvider);
                prevRegisIDs.remove(regisID);
                if ((!prevProvider.equals(provider)) &&
                        prevRegisIDs.size() == 0) { // cleanup
                    provider2IdsMap.remove(prevProvider);
                }
            }
            List<String> regisIDs = provider2IdsMap.get(provider);
            if (regisIDs == null) {
                regisIDs = new ArrayList<String>();
                provider2IdsMap.put(provider, regisIDs);
            }
            regisIDs.add(regisID);

            if ((provider != null && (!provider.equals(prevProvider))) ||
                    (provider == null && prevProvider != null)) {
                listeners = id2RegisListenersMap.get(regisID);
            }
	} finally {
	    wLock.unlock();
	    if (persist) {
		_storeRegistration(regisID, rc, provider,properties);
	    } else if (prevRegisContext != null && prevRegisContext.isPersistent()) {
		_deleteStoredRegistration(regisID, prevRegisContext);
	    }
	}

        // outside wLock to prevent dead lock
        if (listeners != null && listeners.size() > 0) {
            for (RegistrationListener listener : listeners) {
                listener.notify(layer, appContext);
            }
        }

        return regisID;
   }

    //XXX need to update persistent state and notify effected listeners
    private static boolean _unRegister(String regisID) {
	boolean rvalue = false;
	RegistrationContext rc = null;;
        List<RegistrationListener> listeners = null;
        String[] dIds = decomposeRegisID(regisID);
        wLock.lock();
	try {   
	    rc = id2RegisContextMap.remove(regisID);
	    AuthConfigProvider provider = id2ProviderMap.remove(regisID);
            List<String> regisIDs = provider2IdsMap.get(provider);
            if (regisIDs != null) {
                regisIDs.remove(regisID);
            }
            if (regisIDs == null || regisIDs.size() == 0) {
                provider2IdsMap.remove(provider);
            }

            listeners = id2RegisListenersMap.remove(regisID);
	    rvalue = (provider != null);
	} finally {
	    wLock.unlock();
	    if (rc != null && rc.isPersistent()) {
		_deleteStoredRegistration(regisID, rc);
	    }
	}

        // outside wLock to prevent dead lock
        if (listeners != null && listeners.size() > 0) {
            for (RegistrationListener listener : listeners) {
                listener.notify(dIds[0], dIds[1]);
            }
        }

        return rvalue;
    }

    // the following methods implement the factory's persistence layer

    // XXX complete the implementations 
    // XXX the WSIT and GF providers should not (ubtimately) be hardwired

    private void _loadFactory() {
        wLock.lock();
	try {
	    id2ProviderMap = new HashMap<String, AuthConfigProvider>();
	    id2RegisContextMap = new HashMap<String, RegistrationContext>();
            id2RegisListenersMap =
                new HashMap<String, List<RegistrationListener>>();
            provider2IdsMap = new HashMap<AuthConfigProvider, List<String>>();
	} finally {
	    wLock.unlock();
	}
	try {
            for (EntryInfo info : regStore.getPersistedEntries()) {
                if (info.isConstructorEntry()) {
                    _constructProvider(info.getClassName(),
                        info.getProperties(), this);
                } else {
                    for (RegistrationContext ctx : info.getRegContexts()) {
                        registerConfigProvider(info.getClassName(),
                            info.getProperties(), ctx.getMessageLayer(),
                            ctx.getAppContext(), ctx.getDescription());
                    }
                }
            }
	} catch (Exception e) {
            if (logger.isLoggable(Level.WARNING)) {
                logger.log(Level.WARNING,
                    LogStringsMessages.WSITPVD_0061_JMAC_AUTHCONFIG_LOADER_FAILURE());
            }
	}

    }

    private static void _storeRegistration(String regId,
        RegistrationContext ctx, AuthConfigProvider p, Map<String, String> properties) {
        
	String className = null;
	if (p != null) {
	    className = p.getClass().getName();
	}
        if (ctx.isPersistent()) {
            regStore.store(className, ctx, properties);
        }
    }

    private static void _deleteStoredRegistration(String regId,
        RegistrationContext ctx) {
        
        if (ctx.isPersistent()) {
            regStore.delete(ctx);
        }
    }
    
}
