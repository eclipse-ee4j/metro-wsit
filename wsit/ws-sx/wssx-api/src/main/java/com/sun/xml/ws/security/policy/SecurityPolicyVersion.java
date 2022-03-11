/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.policy;

/**
 *
 * @author ashutosh.shahi@sun.com
 */
public enum SecurityPolicyVersion {


    SECURITYPOLICY200507("http://schemas.xmlsoap.org/ws/2005/07/securitypolicy",
                         "http://schemas.xmlsoap.org/ws/2005/07/securitypolicy/IncludeToken/Once",
                         "http://schemas.xmlsoap.org/ws/2005/07/securitypolicy/IncludeToken/Never",
                         "http://schemas.xmlsoap.org/ws/2005/07/securitypolicy/IncludeToken/AlwaysToRecipient",
                         "http://schemas.xmlsoap.org/ws/2005/07/securitypolicy/IncludeToken/Always"){

        @Override
        public String getNamespaceURI() {
            return namespaceUri;
        }

        @Override
        public String getIncludeTokenOnce() {
            return includeTokenOnce;
        }

        @Override
        public String getIncludeTokenNever() {
            return includeTokenNever;
        }

        @Override
        public String getIncludeTokenAlwaysToRecipient() {
            return includeTokenAlwaysToRecipient;
        }

        @Override
        public String getIncludeTokenAlways() {
            return includeTokenAlways;
        }

    },
    SECURITYPOLICY12NS("http://docs.oasis-open.org/ws-sx/ws-securitypolicy/200702",
                       "http://docs.oasis-open.org/ws-sx/ws-securitypolicy/200702/IncludeToken/Once",
                       "http://docs.oasis-open.org/ws-sx/ws-securitypolicy/200702/IncludeToken/Never",
                       "http://docs.oasis-open.org/ws-sx/ws-securitypolicy/200702/IncludeToken/AlwaysToRecipient",
                       "http://docs.oasis-open.org/ws-sx/ws-securitypolicy/200702/IncludeToken/Always"){

        @Override
        public String getNamespaceURI() {
            return namespaceUri;
        }

        @Override
        public String getIncludeTokenOnce() {
            return includeTokenOnce;
        }

        @Override
        public String getIncludeTokenNever() {
            return includeTokenNever;
        }

        @Override
        public String getIncludeTokenAlwaysToRecipient() {
            return includeTokenAlwaysToRecipient;
        }

        @Override
        public String getIncludeTokenAlways() {
            return includeTokenAlways;
        }
    },

    SECURITYPOLICY200512("http://docs.oasis-open.org/ws-sx/ws-securitypolicy/200512",
                       "http://docs.oasis-open.org/ws-sx/ws-securitypolicy/200512/IncludeToken/Once",
                       "http://docs.oasis-open.org/ws-sx/ws-securitypolicy/200512/IncludeToken/Never",
                       "http://docs.oasis-open.org/ws-sx/ws-securitypolicy/200512/IncludeToken/AlwaysToRecipient",
                       "http://docs.oasis-open.org/ws-sx/ws-securitypolicy/200512/IncludeToken/Always"){

        @Override
        public String getNamespaceURI() {
            return namespaceUri;
        }

        @Override
        public String getIncludeTokenOnce() {
            return includeTokenOnce;
        }

        @Override
        public String getIncludeTokenNever() {
            return includeTokenNever;
        }

        @Override
        public String getIncludeTokenAlwaysToRecipient() {
            return includeTokenAlwaysToRecipient;
        }

        @Override
        public String getIncludeTokenAlways() {
            return includeTokenAlways;
        }
    },

    MS_SECURITYPOLICY200507("http://schemas.microsoft.com/ws/2005/07/securitypolicy",
                         "http://schemas.microsoft.com/ws/2005/07/securitypolicy/IncludeToken/Once",
                         "http://schemas.microsoft.com/ws/2005/07/securitypolicy/IncludeToken/Never",
                         "http://schemas.microsoft.com/ws/2005/07/securitypolicy/IncludeToken/AlwaysToRecipient",
                         "http://schemas.microsoft.com/ws/2005/07/securitypolicy/IncludeToken/Always"){

        @Override
        public String getNamespaceURI() {
            return namespaceUri;
        }

        @Override
        public String getIncludeTokenOnce() {
            return includeTokenOnce;
        }

        @Override
        public String getIncludeTokenNever() {
            return includeTokenNever;
        }

        @Override
        public String getIncludeTokenAlwaysToRecipient() {
            return includeTokenAlwaysToRecipient;
        }

        @Override
        public String getIncludeTokenAlways() {
            return includeTokenAlways;
        }

    };


    public final String namespaceUri;

    public final String includeTokenOnce;

    public final String includeTokenNever;

    public final String includeTokenAlwaysToRecipient;

    public final String includeTokenAlways;

    public abstract String getNamespaceURI();

    public abstract String getIncludeTokenOnce();

    public abstract String getIncludeTokenNever();

    public abstract String getIncludeTokenAlwaysToRecipient();

    public abstract String getIncludeTokenAlways();

    /** Creates a new instance of SecurityPolicyVersion */
    SecurityPolicyVersion(String nsUri, String includeOnce,
                          String includeNever, String includeAlwaysToRecipient,
                          String includeAlways) {
        this.namespaceUri = nsUri;
        this.includeTokenOnce = includeOnce;
        this.includeTokenNever = includeNever;
        this.includeTokenAlwaysToRecipient = includeAlwaysToRecipient;
        this.includeTokenAlways = includeAlways;
    }

}
