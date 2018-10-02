/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * InclusiveNamespaces.java
 *
 * Created on August 29, 2006, 6:09 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.sun.xml.security.core.dsig;

import javax.xml.bind.annotation.XmlAttribute;

/**
 *
 * @author K.Venugopal@sun.com
 */
//@XmlRootElement(name="ReferenceType")
//@XmlAccessorType(XmlAccessType.FIELD)
public class InclusiveNamespacesType {
    
    @XmlAttribute(name = "PrefixList", required = true)
    protected String prefixList;
    
    /** Creates a new instance of InclusiveNamespaces */
    public InclusiveNamespacesType() {
    }
    
    public String getPrefixList(){
        return this.prefixList;
    }
    
    public void addToPrefixList(String prefix){
        if(prefixList == null){
            prefixList = prefix;
        } else{
            prefixList = prefixList.concat(" ").concat(prefix);
        }
    }
    
}
