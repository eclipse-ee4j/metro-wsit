/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.impl.callback;

import org.w3c.dom.Element;
import javax.xml.stream.XMLStreamReader;

/**
 * 
 * @deprecated use the new extended SAMLValidator interface
 * An implementation of  the extended SAMLValidator interface 
 * can throw UnsupportedOperationException for methods defined in this 
 * base interface.
 *  
 */
public interface SAMLAssertionValidator {

        /**
         * SAML validator.
         * @param assertion the assertion to be validated
         * successful validation.
         */
        void validate(Element assertion)
        ;

        /**
         * SAML validator.
         * @param assertion the assertion to be validated
         * successful validation.
         */
        void validate(XMLStreamReader assertion)
        ;

        class SAMLValidationException extends Exception {

            private static final long serialVersionUID = -3676369249933497791L;

            public SAMLValidationException(String message) {
            super(message);
        }
                                                                                                                      
        public SAMLValidationException(String message, Throwable cause) {
            super(message, cause);
        }
                                                                                                                      
        public SAMLValidationException(Throwable cause) {
            super(cause);
        }
    }
}
