/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.mc.runtime;

import com.sun.xml.ws.rx.RxRuntimeException;
import com.sun.xml.ws.rx.util.JaxbContextRepository;
import org.glassfish.jaxb.runtime.api.JAXBRIContext;
import com.sun.xml.ws.api.addressing.AddressingVersion;
import com.sun.xml.ws.rx.mc.api.McProtocolVersion;
import jakarta.xml.bind.Unmarshaller;

/**
 *
 */
public enum McRuntimeVersion {

    WSMC200702(
        McProtocolVersion.WSMC200702,

        com.sun.xml.ws.rx.mc.protocol.wsmc200702.MakeConnectionElement.class,
        com.sun.xml.ws.rx.mc.protocol.wsmc200702.MessagePendingElement.class,
        com.sun.xml.ws.rx.mc.protocol.wsmc200702.UnsupportedSelectionType.class);

    public static McRuntimeVersion forProtocolVersion(McProtocolVersion protocolVersion) {
        for (McRuntimeVersion version : values()) {
            if (version.protocolVersion == protocolVersion) {
                return version;
            }
        }

        assert false : "Unsupported WS-MakeConnection protocol version definition detected"; // we should never get here

        return null;
    }

    /**
     * General constants
     */
    public final McProtocolVersion protocolVersion;
    /**
     * Private fields
     */
    private final JaxbContextRepository jaxbContextRepository;

    McRuntimeVersion(McProtocolVersion protocolVersion, Class<?>... protocolClasses) {
        this.protocolVersion = protocolVersion;
        this.jaxbContextRepository = new JaxbContextRepository(protocolClasses);
    }

    /**
     * TODO javadoc
     *
     */
    public String getClientId(String eprAddress) {
        final String mcAnnonymousAddressPrefix = protocolVersion.protocolNamespaceUri + "/anonymous?id=";
        if (eprAddress.startsWith(mcAnnonymousAddressPrefix)) {
            return eprAddress.substring(mcAnnonymousAddressPrefix.length());
        }
        return null;
    }

    /**
     * TODO javadoc
     *
     */
    public String getAnonymousAddress(String uuid) {
        return protocolVersion.protocolNamespaceUri + "/anonymous?id=" + uuid;
    }

    /**
     * Creates JAXB {@link Unmarshaller} that is able to unmarshall protocol elements for given WS-MC version.
     * <p>
     * As JAXB unmarshallers are not thread-safe, this method should be used to create a new {@link Unmarshaller}
     * instance whenever there is a chance that the same instance might be invoked concurrently from multiple
     * threads. On th other hand, it is prudent to cache or pool {@link Unmarshaller} instances if possible as
     * constructing a new {@link Unmarshaller} instance is rather expensive.
     * <p>
     * For additional information see this <a href="https://jaxb.dev.java.net/guide/Performance_and_thread_safety.html">blog entry</a>.
     *
     * @return created JAXB unmarshaller
     *
     * @exception RxRuntimeException in case the creation of unmarshaller failed
     */
    public Unmarshaller getUnmarshaller(AddressingVersion av) throws RxRuntimeException {
        return jaxbContextRepository.getUnmarshaller(av);
    }

    /**
     * Returns JAXB context that is intitialized based on a given addressing version.
     *
     * @param av addressing version used to initialize JAXB context
     *
     * @return JAXB context that is intitialized based on a given addressing version.
     */
    public JAXBRIContext getJaxbContext(AddressingVersion av) {
        return jaxbContextRepository.getJaxbContext(av);
    }
}
