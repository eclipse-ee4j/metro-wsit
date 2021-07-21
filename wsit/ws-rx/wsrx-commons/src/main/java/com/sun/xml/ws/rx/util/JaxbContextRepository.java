/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.util;

import com.sun.xml.ws.rx.RxRuntimeException;
import com.sun.xml.bind.api.JAXBRIContext;
import com.sun.xml.ws.api.addressing.AddressingVersion;
import com.sun.istack.logging.Logger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.ws.EndpointReference;

/**
 * TODO javadoc
 *
 * <b>
 * WARNING: This class is a private utility class used by WS-RX implementation. Any usage outside
 * the intedned scope is strongly discouraged. The API exposed by this class may be changed, replaced
 * or removed without any advance notice.
 * </b>
 *
 */
public final class JaxbContextRepository {

    private static final Logger LOGGER = Logger.getLogger(JaxbContextRepository.class);
    //
    private Map<AddressingVersion, JAXBRIContext> jaxbContexts;
    private ThreadLocal<Map<AddressingVersion, Unmarshaller>> threadLocalUnmarshallers = new ThreadLocal<Map<AddressingVersion, Unmarshaller>>() {

        @Override
        protected Map<AddressingVersion, Unmarshaller> initialValue() {
            Map<AddressingVersion, Unmarshaller> result = new HashMap<AddressingVersion, Unmarshaller>();
            for (Map.Entry<AddressingVersion, JAXBRIContext> entry : jaxbContexts.entrySet()) {
                try {
                    result.put(entry.getKey(), entry.getValue().createUnmarshaller());
                } catch (JAXBException ex) {
                    LOGGER.severe("Unable to create JAXB unmarshaller", ex);
                    throw new RxRuntimeException("Unable to create JAXB unmarshaller", ex);
                }
            }
            return result;
        }
    };

    public JaxbContextRepository(Class<?>... classes) throws RxRuntimeException {
        this.jaxbContexts = new HashMap<AddressingVersion, JAXBRIContext>();
        for (AddressingVersion av : AddressingVersion.values()) {
            this.jaxbContexts.put(av, createContext(av, classes));
        }
    }

    private static final JAXBRIContext createContext(AddressingVersion av, Class<?>... classes) throws RxRuntimeException {
        /**
         * We need to add all supported WS-A EndpointReference implementation classes to the array
         * before we pass the array to the JAXBRIContext factory method.
         */
        LinkedList<Class<?>> jaxbElementClasses = new LinkedList<Class<?>>(Arrays.asList(classes));
        jaxbElementClasses.add(av.eprType.eprClass);

        Map<Class, Class> eprClassReplacementMap = new HashMap<Class, Class>();
        eprClassReplacementMap.put(EndpointReference.class, av.eprType.eprClass);

        try {
            return JAXBRIContext.newInstance(jaxbElementClasses.toArray(classes),
                    null,
                    eprClassReplacementMap,
                    null,
                    false,
                    null);
        } catch (JAXBException ex) {
            throw new RxRuntimeException("Unable to create JAXB RI Context", ex);
        }
    }

    /**
     * Creates JAXB {@link Unmarshaller} that is able to unmarshall elements for specified classes.
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
        return threadLocalUnmarshallers.get().get(av);
    }

    /**
     * Returns JAXB context that is intitialized based on a given addressing version.
     *
     * @param av addressing version used to initialize JAXB context
     *
     * @return JAXB context that is intitialized based on a given addressing version.
     */
    public JAXBRIContext getJaxbContext(AddressingVersion av) {
        return jaxbContexts.get(av);
    }
}
