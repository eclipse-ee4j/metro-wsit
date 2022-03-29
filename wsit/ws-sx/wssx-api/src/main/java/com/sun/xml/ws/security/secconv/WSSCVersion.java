/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.secconv;

import com.sun.xml.ws.security.secconv.impl.WSSCVersion10;
import com.sun.xml.ws.security.secconv.impl.wssx.WSSCVersion13;

/**
 *
 * @author Shyam Rao
 */
public abstract class WSSCVersion {

    public static final WSSCVersion WSSC_10 = new WSSCVersion10();

    public static final WSSCVersion WSSC_13 = new WSSCVersion13();

    public static final String WSSC_10_NS_URI = "http://schemas.xmlsoap.org/ws/2005/02/sc";
    public static final String WSSC_13_NS_URI = "http://docs.oasis-open.org/ws-sx/ws-secureconversation/200512";

    public static WSSCVersion getInstance(String nsURI){
        if (nsURI.equals(WSSC_13.getNamespaceURI())){
            return WSSC_13;
        }
        return WSSC_10;
    }

    protected WSSCVersion() {}

    public abstract String getNamespaceURI();

    public abstract String getSCTTokenTypeURI();

    public abstract String getDKTokenTypeURI();

    public abstract String getSCTRequestAction();

    public abstract String getSCTResponseAction();

    public abstract String getSCTRenewRequestAction();

    public abstract String getSCTRenewResponseAction();

    public abstract String getSCTCancelRequestAction();

    public abstract String getSCTCancelResponseAction();

}
