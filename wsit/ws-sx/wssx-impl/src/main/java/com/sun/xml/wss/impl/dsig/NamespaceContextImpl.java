/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.impl.dsig;

import com.sun.xml.wss.impl.MessageConstants;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.xml.namespace.NamespaceContext;

/**
 * Implements NamespaceContext .
 *
 * TODO : Performance Improvements.
 */
public class NamespaceContextImpl implements NamespaceContext{

    HashMap namespaceMap = null;

    public NamespaceContextImpl(){
        namespaceMap = new HashMap(10);
        this.add("SOAP-ENV","http://schemas.xmlsoap.org/soap/envelope/" );
        this.add("env","http://schemas.xmlsoap.org/soap/envelope/" );
        this.add("ds", MessageConstants.DSIG_NS);
        this.add("xenc", MessageConstants.XENC_NS);
        this.add("wsse", MessageConstants.WSSE_NS);
        this.add("wsu", MessageConstants.WSU_NS);
        this.add("saml", MessageConstants.SAML_v1_0_NS);
        this.add("S11","http://schemas.xmlsoap.org/soap/envelope/" );
        this.add("S12","http://www.w3.org/2003/05/soap-envelope" );
    }

    /**
     * Add prefix and namespaceuri to be associated with the prefix.
     * @param prefix Namespace Prefix
     * @param uri NamespaceURI
     */
    @SuppressWarnings("unchecked")
    public void add(String prefix,String uri){
        namespaceMap.put(prefix,uri);

    }
    /**
     *
     */
    @Override
    public String getNamespaceURI(String prefix) {
        return    (String)namespaceMap.get(prefix);
    }

    /**
     *
     * @return NamespaceURI associated with the prefix
     */
    @Override
    public String getPrefix(String namespaceURI) {
        Iterator iterator = namespaceMap.keySet().iterator();
        while(iterator.hasNext()){
            String prefix = (String)iterator.next();
            String uri = (String)namespaceMap.get(prefix);
            if(namespaceURI.equals(uri))
                return prefix;

        }
        return null;
    }

    /**
     *
     * @return Iterator to list of prefixes associated with the namespaceURI
     */
    @Override
    @SuppressWarnings("unchecked")
    public Iterator getPrefixes(String namespaceURI) {

        ArrayList prefixList = new ArrayList();
        Iterator iterator = namespaceMap.keySet().iterator();
        while(iterator.hasNext()){
            String prefix = (String)iterator.next();

            String uri = (String)namespaceMap.get(prefix);

            if(namespaceURI.equals(uri)){
                prefixList.add(prefix);
            }
        }
        return  prefixList.iterator();
    }

    public Map getMap(){
        return namespaceMap;
    }
}
