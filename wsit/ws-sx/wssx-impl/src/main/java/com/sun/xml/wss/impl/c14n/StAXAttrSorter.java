/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.impl.c14n;

/**
 *
 * @author root
 */
public class StAXAttrSorter extends AttrSorter {

    /** Creates a new instance of StAXAttrSorter */
    public StAXAttrSorter (boolean value) {
        super (value);
    }

   @Override
   protected int sortAttributes (Object object, Object object0) {
        StAXAttr attr = (StAXAttr)object;
        StAXAttr attr0 = (StAXAttr)object0;
        String uri = attr.getUri ();
        String uri0 = attr0.getUri ();
        int result = uri.compareTo (uri0);
        if(result == 0){
            String lN = attr.getLocalName ();
            String lN0 = attr0.getLocalName ();
            result = lN.compareTo (lN0);
        }
        return result;
    }
}
