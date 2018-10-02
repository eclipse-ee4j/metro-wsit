/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.opt.impl.util;

import java.util.ArrayList;
import java.util.List;
import org.jvnet.staxex.NamespaceContextEx;

/**
 *
 * @author Ashutosh.Shahi@sun.com
 */
public class NamespaceAndPrefixMapper {
    
    public static final String NS_PREFIX_MAPPER = "NS_And_Prefix_Mapper";
    
    NamespaceContextEx ns = null;
    List<String> incList = null;
    
    /** Creates a new instance of NamespaceAndPrefixMapper */
    public NamespaceAndPrefixMapper(NamespaceContextEx ns, boolean disableIncPrefix) {
        this.ns = ns;
        incList = new ArrayList<String>();
        if(!disableIncPrefix){
            incList.add("wsse"); 
            incList.add("S");
        }
    }
    
    public NamespaceAndPrefixMapper(NamespaceContextEx ns, List<String> incList){
        this.ns = ns;
        this.incList = incList;
    }
    
    public void setNamespaceContext(NamespaceContextEx ns){
        this.ns = ns;
    }
    
    public NamespaceContextEx getNamespaceContext(){
        return ns;
    }
    
    public void addToInclusivePrefixList(String s){
        if(incList == null)
            incList = new ArrayList<String>();
        incList.add(s);
    }
    
    public void removeFromInclusivePrefixList(String s){
        if(incList == null)
            return;
        incList.remove(s);
    }
    
    public List<String> getInlusivePrefixList(){
        return incList;
    }
}
