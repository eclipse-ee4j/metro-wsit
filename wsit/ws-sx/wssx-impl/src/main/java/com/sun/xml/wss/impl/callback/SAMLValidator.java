/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.impl.callback;

import java.util.Map;
import javax.security.auth.Subject;
import javax.xml.stream.XMLStreamReader;
import org.w3c.dom.Element;

/**
 *
 * @author kumar.jayanti
 */
public interface SAMLValidator  extends SAMLAssertionValidator {

        /**
         * Note: The runtime already validates the Enveloped Signature for a HOK assertion before
         * calling this validate() method
         * @param assertion The assertion as a DOM Element
         * @param runtimeProps the runtime properties associated with this request
         * @param clientSubject the Subject of the sender which can be updated after validation
         * with principal/credential information
         */
        void validate(Element assertion, Map runtimeProps, Subject clientSubject)
        ;

        /**
         * Note: The runtime already validates the Enveloped Signature for a HOK assertion before
         * calling this validate() method
         * @param assertion The assertion as an XMLStreamReader
         * @param runtimeProps the runtime properties associated with this request
         * @param clientSubject the Subject of the sender which can be updated after validation
         * with principal/credential information
         */
        void validate(XMLStreamReader assertion, Map runtimeProps, Subject clientSubject)
        ;
}
