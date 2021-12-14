/*
 * Copyright (c) 2010, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * JAXBCryptoContext.java
 *
 * Created on January 24, 2006, 11:53 AM
 */

package com.sun.xml.ws.security.opt.crypto.jaxb;

import java.util.HashMap;
import javax.xml.crypto.KeySelector;
import javax.xml.crypto.URIDereferencer;

/**
 *
 * @author Abhijit Das
 */
public class JAXBCryptoContext implements javax.xml.crypto.XMLCryptoContext {
    
    private String baseURI = null;
    private KeySelector keySelector = null;
    private URIDereferencer uriDereferencer = null;
    private HashMap namespacePrefix = null; 
    private String defaultNamespacePrefix = null;
    private HashMap property = null;
    private HashMap context = null;
    
    /** Creates a new instance of JAXBCryptoContext */
    public JAXBCryptoContext() {
    }

    /**
     * Returns the base URI.
     *
     * @return the base URI, or <code>null</code> if not specified
     * @see #setBaseURI(String)
     */
    @Override
    public String getBaseURI() {
        return baseURI;
    }

     /**
     * Sets the base URI.
     *
     * @param baseURI the base URI, or <code>null</code> to remove current
     *    value
     * @throws IllegalArgumentException if <code>baseURI</code> is not RFC
     *    2396 compliant
     * @see #getBaseURI
     */
    @Override
    public void setBaseURI(String baseURI) {
        this.baseURI = baseURI;
    }

    /**
     * Returns the key selector for finding a key.
     *
     * @return the key selector, or <code>null</code> if not specified
     * @see #setKeySelector(KeySelector)
     */
    @Override
    public KeySelector getKeySelector() {
        return keySelector;
    }

    /**
     * Sets the key selector for finding a key.
     *
     * @param keySelector the key selector, or <code>null</code> to remove the current
     *    setting
     * @see #getKeySelector
     */
    @Override
    public void setKeySelector(KeySelector keySelector) {
        this.keySelector = keySelector;
    }

    /**
     * Returns a <code>URIDereferencer</code> that is used to dereference
     * {@link URIDereferencer}s.
     *
     * @return the <code>URIDereferencer</code>, or <code>null</code> if not
     *    specified
     * @see #setURIDereferencer(URIDereferencer)
     */
    @Override
    public URIDereferencer getURIDereferencer() {
        return uriDereferencer;
    }

    /**
     * Sets a <code>URIDereferencer</code> that is used to dereference
     * {@link URIDereferencer}s. The specified <code>URIDereferencer</code>
     * is used in place of an implementation's default
     * <code>URIDereferencer</code>.
     *
     * @param uriDereferencer the <code>URIDereferencer</code>, or
     *    <code>null</code> to remove any current setting
     * @see #getURIDereferencer
     */
    @Override
    public void setURIDereferencer(URIDereferencer uriDereferencer) {
        this.uriDereferencer = uriDereferencer;
    }

    /**
     * Returns the namespace prefix that the specified namespace URI is
     * associated with. Returns the specified default prefix if the specified
     * namespace URI has not been bound to a prefix. To bind a namespace URI
     * to a prefix, call the {@link #putNamespacePrefix putNamespacePrefix}
     * method.
     *
     * @param namespaceURI a namespace URI
     * @param defaultPrefix the prefix to be returned in the event that the
     *    the specified namespace URI has not been bound to a prefix.
     * @return the prefix that is associated with the specified namespace URI,
     *    or <code>defaultPrefix</code> if the URI is not registered. If
     *    the namespace URI is registered but has no prefix, an empty string
     *    (<code>""</code>) is returned.
     * @throws NullPointerException if <code>namespaceURI</code> is
     *    <code>null</code>
     * @see #putNamespacePrefix(String, String)
     */
    @Override
    public String getNamespacePrefix(String namespaceURI, String defaultPrefix) {
        if ( namespacePrefix == null ) {
            return defaultPrefix;
        }
        Object prefix = namespacePrefix.get(namespaceURI);
        if ( prefix == null ) {
            return defaultPrefix;
        } else if ( prefix.equals("" )) {
            return defaultPrefix;
        } else {
            return prefix.toString();
        }
    }

    /**
     * Maps the specified namespace URI to the specified prefix. If there is
     * already a prefix associated with the specified namespace URI, the old
     * prefix is replaced by the specified prefix.
     *
     * @param namespaceURI a namespace URI
     * @param prefix a namespace prefix (or <code>null</code> to remove any
     *    existing mapping). Specifying the empty string (<code>""</code>)
     *    binds no prefix to the namespace URI.
     * @return the previous prefix associated with the specified namespace
     *    URI, or <code>null</code> if there was none
     * @throws NullPointerException if <code>namespaceURI</code> is
     *    <code>null</code>
     * @see #getNamespacePrefix(String, String)
     */
    @Override
    @SuppressWarnings("unchecked")
    public String putNamespacePrefix(String namespaceURI, String prefix) {
        if ( namespaceURI == null ) {
            return null;
        }
        
        if ( namespacePrefix == null ) {
            namespacePrefix = new HashMap();
        }
        
        //Get the old prefix
        Object oldPrefix = namespacePrefix.get(namespaceURI);
        
        if ( prefix == null && oldPrefix != null ) {
            //Remove the mapping and return the oldprefix
            return namespacePrefix.remove(namespaceURI).toString();
        }
        
        if ( prefix != "" ) 
            namespacePrefix.put(namespaceURI, prefix);
            
        if(oldPrefix != null)
            return oldPrefix.toString();
        return null;
    }

    /**
     * Returns the default namespace prefix. The default namespace prefix
     * is the prefix for all namespace URIs not explicitly set by the
     * {@link #putNamespacePrefix putNamespacePrefix} method.
     *
     * @return the default namespace prefix, or <code>null</code> if none has
     *    been set.
     * @see #setDefaultNamespacePrefix(String)
     */
    @Override
    public String getDefaultNamespacePrefix() {
        return defaultNamespacePrefix;
    }

    /**
     * Sets the default namespace prefix. This sets the namespace prefix for
     * all namespace URIs not explicitly set by the {@link #putNamespacePrefix
     * putNamespacePrefix} method.
     *
     * @param defaultNamespacePrefix the default namespace prefix, or <code>null</code>
     *    to remove the current setting. Specify the empty string
     *    (<code>""</code>) to bind no prefix.
     * @see #getDefaultNamespacePrefix
     */
    @Override
    public void setDefaultNamespacePrefix(String defaultNamespacePrefix) {
        this.defaultNamespacePrefix = defaultNamespacePrefix;
    }

    /**
     * Sets the specified property.
     *
     * @param name the name of the property
     * @param value the value of the property to be set
     * @return the previous value of the specified property, or
     *    <code>null</code> if it did not have a value
     * @throws NullPointerException if <code>name</code> is <code>null</code>
     * @see #getProperty(String)
     */
    @Override
    @SuppressWarnings("unchecked")
    public Object setProperty(String name, Object value) {
        if ( property == null ) {
            property = new HashMap();
        }
        return property.put(name, value);
    }

    /**
     * Returns the value of the specified property.
     *
     * @param name the name of the property
     * @return the current value of the specified property, or
     *    <code>null</code> if it does not have a value
     * @throws NullPointerException if <code>name</code> is <code>null</code>
     * @see #setProperty(String, Object)
     */
    @Override
    public Object getProperty(String name) {
        if ( property != null ) {
            return property.get(name);
        } else {
            return null;
        }
    }

    /**
     * Returns the value to which this context maps the specified key.
     *
     * <p>More formally, if this context contains a mapping from a key
     * <code>k</code> to a value <code>v</code> such that
     * <code>(key==null ? k==null : key.equals(k))</code>, then this method
     * returns <code>v</code>; otherwise it returns <code>null</code>. (There
     * can be at most one such mapping.)
     *
     * <p>This method is useful for retrieving arbitrary information that is
     * specific to the cryptographic operation that this context is used for.
     *
     * @param key the key whose associated value is to be returned
     * @return the value to which this context maps the specified key, or
     *    <code>null</code> if there is no mapping for the key
     * @see #put(Object, Object)
     */
    @Override
    public Object get(Object key) {
        if ( context != null ) {
            return context.get(key);
        } else {
            return null;
        }
    }

    /**
     * Associates the specified value with the specified key in this context.
     * If the context previously contained a mapping for this key, the old
     * value is replaced by the specified value.
     *
     * <p>This method is useful for storing arbitrary information that is
     * specific to the cryptographic operation that this context is used for.
     *
     * @param key key with which the specified value is to be associated with
     * @param value value to be associated with the specified key
     * @return the previous value associated with the key, or <code>null</code>
     *    if there was no mapping for the key
     * @throws IllegalArgumentException if some aspect of this key or value
     *    prevents it from being stored in this context
     * @see #get(Object)
     */
    @Override
    @SuppressWarnings("unchecked")
    public Object put(Object key, Object value) {
        if ( context == null ) {
            context = new HashMap();
        }
        
        return context.put(key, value);
    }
    
}
