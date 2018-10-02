/*
 * Copyright (c) 2010, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.rm.faults;

import com.sun.xml.ws.rx.rm.runtime.RmRuntimeVersion;
import com.sun.xml.ws.rx.rm.runtime.RuntimeContext;
import javax.xml.namespace.QName;
import javax.xml.soap.Detail;

/**
 *
 * @author Marek Potociar (marek.potociar at sun.com)
 */
public class WsrmRequiredException extends AbstractSoapFaultException {

    public WsrmRequiredException() {
        super("The RM Destination requires the use of WSRM.", "The RM Destination requires the use of WSRM.", true);
    }

    @Override
    public Code getCode() {
        return Code.Sender;
    }

    @Override
    public QName getSubcode(RmRuntimeVersion rv) {
        return rv.protocolVersion.wsrmRequiredFaultCode;
    }

    @Override
    public Detail getDetail(RuntimeContext rc) {
        return null;
    }
}
