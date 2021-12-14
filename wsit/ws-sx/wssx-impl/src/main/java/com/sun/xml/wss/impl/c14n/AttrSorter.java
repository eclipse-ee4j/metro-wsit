/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * AttrSorter.java
 *
 * Created on August 21, 2005, 4:38 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.sun.xml.wss.impl.c14n;



/**
 *
 * @author K.Venugopal@sun.com
 */
public class AttrSorter implements java.util.Comparator{
    
    boolean namespaceSort = false;
    
    /** Creates a new instance of AttrSorter */
    public AttrSorter (boolean namespaceSort) {
        this.namespaceSort = namespaceSort;
    }
    
    
    @Override
    public int compare (Object o1, Object o2) {
        if(namespaceSort){
            return sortNamespaces (o1,o2);
        }else{
            return sortAttributes (o1,o2);
        }
    }
    
    //double check;
    protected int sortAttributes (Object object, Object object0) {
        Attribute attr = (Attribute)object;
        Attribute attr0 = (Attribute)object0;
        String uri = attr.getNamespaceURI ();
        String uri0 = attr0.getNamespaceURI ();
        int result = uri.compareTo (uri0);
        if(result == 0){
            String lN = attr.getLocalName ();
            String lN0 = attr0.getLocalName ();
            result = lN.compareTo (lN0);
        }
        return result;
    }
    
    //double check;
    protected int sortNamespaces (Object object, Object object0) {
        AttributeNS attr = (AttributeNS)object;
        AttributeNS attr0 = (AttributeNS)object0;
        //assume namespace processing is on.
        String lN = attr.getPrefix ();
        String lN0 = attr0.getPrefix ();     
        return lN.compareTo (lN0);
    }
    
}
