/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.api.tx.at;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.xml.namespace.QName;
import jakarta.xml.ws.spi.WebServiceFeatureAnnotation;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Documented
@WebServiceFeatureAnnotation(id = TransactionalFeature.ID, bean = TransactionalFeature.class)
public @interface Transactional {

    enum TransactionFlowType {
        MANDATORY, SUPPORTS, NEVER
    }

    enum Version {

        WSAT10("wsat10", WsatNamespace.WSAT200410),
        WSAT11("wsat11", WsatNamespace.WSAT200606),
        WSAT12("wsat12", WsatNamespace.WSAT200606),
        DEFAULT("wsat", WsatNamespace.WSAT200606);

        public final QName qname;
        public final WsatNamespace namespaceVersion;

        Version(String prefix, WsatNamespace namespaceVersion) {
            this.namespaceVersion = namespaceVersion;

            this.qname = new QName((namespaceVersion != null) ? namespaceVersion.namespace : "", "ATAssertion", prefix);
        }

        public QName getQName() {
            return qname;
        }

        public static Version forNamespaceVersion(WsatNamespace nsVersion) {
            for (Version version : Version.values()) {
                if (version == WSAT11 || version == DEFAULT) {
                    continue; // return WSAT12 for this namespace
                }

                if (version.namespaceVersion == nsVersion) {
                    return version;
                }
            }
            return DEFAULT;
        }

        public static Version forNamespaceUri(String ns) {
            for (Version version : Version.values()) {
                if (version == WSAT11 || version == DEFAULT) {
                    continue; // return WSAT12 for this namespace
                }

                if (version.qname.getNamespaceURI().equals(ns)) {
                    return version;
                }
            }
            return DEFAULT;
        }
    }

    /**
     * Specifies if this feature is enabled or disabled.
     */
    boolean enabled() default true;

    /**
     * Specifies the transaction flow type.
     */
    TransactionFlowType value() default TransactionFlowType.SUPPORTS;

    /**
     * Specifies the version of WS-AT being supported, when used together with
     * {@literal @}WebServiceRef, the default value Version.WSAT10. When used together with
     * {@literal @}Webservice and {@literal @}Provider, all versions will be supported, the real version
     * will be determined by the request message.
     */
    Version version() default Version.DEFAULT;
}
