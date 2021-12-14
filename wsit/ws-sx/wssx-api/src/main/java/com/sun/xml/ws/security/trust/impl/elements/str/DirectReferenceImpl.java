/*
 * Copyright (c) 2013, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * $Id: DirectReferenceImpl.java,v 1.2 2010-10-21 15:36:57 snajper Exp $
 */

package com.sun.xml.ws.security.trust.impl.elements.str;

import com.sun.xml.ws.security.secconv.impl.WSSCVersion10;
import com.sun.xml.ws.security.secconv.impl.wssx.WSSCVersion13;
import com.sun.xml.ws.security.secext10.ReferenceType;
import com.sun.xml.ws.security.trust.elements.str.DirectReference;

import java.net.URI;
import javax.xml.namespace.QName;

/**
 * Reference Interface
 */
public class DirectReferenceImpl extends ReferenceType implements DirectReference {
    private final static QName _WSC_INSTANCE_10_Type_QNAME = new QName(WSSCVersion10.WSSC_10_NS_URI, "Instance");
    private final static QName _WSC_INSTANCE_13_Type_QNAME = new QName(WSSCVersion13.WSSC_13_NS_URI, "Instance");
    private final static String WSC_INSTANCE = "wsc:Instance";
    public DirectReferenceImpl(final String valueType, final String uri){
        setValueType(valueType);
        setURI(uri);
    }
    
    public DirectReferenceImpl(final String valueType, final String uri, final String instance){
        setValueType(valueType);
        setURI(uri);
        if(WSSCVersion10.WSSC_10.getSCTTokenTypeURI().equals(valueType)){
            getOtherAttributes().put(_WSC_INSTANCE_10_Type_QNAME, instance);
        }else if(WSSCVersion13.WSSC_13.getSCTTokenTypeURI().equals(valueType)){
            getOtherAttributes().put(_WSC_INSTANCE_13_Type_QNAME, instance);
        }        
    }
    
    public DirectReferenceImpl(final ReferenceType refType){
        this(refType.getValueType(), refType.getURI());
    }

    @Override
    public URI getURIAttr(){
        return URI.create(super.getURI());
    }

    @Override
    public URI getValueTypeURI(){
        return URI.create(super.getValueType());
    }
    
    @Override
    public String getType(){
        return "Reference";
    }

}
