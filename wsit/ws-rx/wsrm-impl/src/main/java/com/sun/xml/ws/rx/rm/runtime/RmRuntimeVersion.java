/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.rm.runtime;

import com.sun.xml.ws.rx.RxRuntimeException;
import com.sun.xml.ws.rx.util.JaxbContextRepository;
import org.glassfish.jaxb.runtime.api.JAXBRIContext;

import com.sun.xml.ws.api.addressing.AddressingVersion;
import com.sun.xml.ws.rx.rm.api.RmProtocolVersion;
import jakarta.xml.bind.Unmarshaller;

/**
 * This enumeration contains all currently supported WS-ReliableMessaging versions.
 * The used reliable messaging version affects the WS-ReliableMessaging Policy assertions displayed
 * int the web service's WSDL, XML namespace of RM protocol element being created
 * as well as RM protocol message processing logic.
 *
 * @author Marek Potociar <marek.potociar at sun.com>
 *
 * @see #WSRM200502
 * @see #WSRM200702
 */
public enum RmRuntimeVersion {

    /**
     * <p>
     * This value represents the outdated and obsolete WS-ReliableMessaging v1.0 protocol.
     * </p>
     * <p>
     * You may want to choose this version for your WS endpoints to ensure maximum
     * backward compatibility with clients running on older systems, such as
     * Metro 1.0 or .NET 3.0
     * </p>
     *
     * @see RmVersion
     */
    WSRM200502(
    RmProtocolVersion.WSRM200502,
    com.sun.xml.ws.rx.rm.protocol.wsrm200502.AcceptType.class,
    com.sun.xml.ws.rx.rm.protocol.wsrm200502.AckRequestedElement.class,
    com.sun.xml.ws.rx.rm.protocol.wsrm200502.CreateSequenceElement.class,
    com.sun.xml.ws.rx.rm.protocol.wsrm200502.CreateSequenceResponseElement.class,
    com.sun.xml.ws.rx.rm.protocol.wsrm200502.Expires.class,
    com.sun.xml.ws.rx.rm.protocol.wsrm200502.Identifier.class,
    com.sun.xml.ws.rx.rm.protocol.wsrm200502.OfferType.class,
    com.sun.xml.ws.rx.rm.protocol.wsrm200502.SequenceAcknowledgementElement.class,
    com.sun.xml.ws.rx.rm.protocol.wsrm200502.SequenceElement.class,
    com.sun.xml.ws.rx.rm.protocol.wsrm200502.SequenceFaultElement.class,
    com.sun.xml.ws.rx.rm.protocol.wsrm200502.TerminateSequenceElement.class),
    /**
     * <p>
     * This value represents the version of WS-ReliableMessaging protocol standardized
     * by OASIS organization. This is currently the most up-to-date version.
     * </p>
     * <p>
     * You should primarily use this version for your WS endpoints. It is compatible with
     * clients running on Metro 1.3 or .NET 3.5 and later.
     * </p>
     *
     * @see RmVersion
     */
    WSRM200702(
    RmProtocolVersion.WSRM200702,
    com.sun.xml.ws.rx.rm.protocol.wsrm200702.AcceptType.class,
    com.sun.xml.ws.rx.rm.protocol.wsrm200702.AckRequestedElement.class,
    com.sun.xml.ws.rx.rm.protocol.wsrm200702.Address.class,
    com.sun.xml.ws.rx.rm.protocol.wsrm200702.CloseSequenceElement.class,
    com.sun.xml.ws.rx.rm.protocol.wsrm200702.CloseSequenceResponseElement.class,
    com.sun.xml.ws.rx.rm.protocol.wsrm200702.CreateSequenceElement.class,
    com.sun.xml.ws.rx.rm.protocol.wsrm200702.CreateSequenceResponseElement.class,
    com.sun.xml.ws.rx.rm.protocol.wsrm200702.DetailType.class,
    com.sun.xml.ws.rx.rm.protocol.wsrm200702.Expires.class,
    com.sun.xml.ws.rx.rm.protocol.wsrm200702.Identifier.class,
    com.sun.xml.ws.rx.rm.protocol.wsrm200702.IncompleteSequenceBehaviorType.class,
    com.sun.xml.ws.rx.rm.protocol.wsrm200702.OfferType.class,
    com.sun.xml.ws.rx.rm.protocol.wsrm200702.SequenceAcknowledgementElement.class,
    com.sun.xml.ws.rx.rm.protocol.wsrm200702.SequenceElement.class,
    com.sun.xml.ws.rx.rm.protocol.wsrm200702.SequenceFaultElement.class,
    com.sun.xml.ws.rx.rm.protocol.wsrm200702.TerminateSequenceElement.class,
    com.sun.xml.ws.rx.rm.protocol.wsrm200702.TerminateSequenceResponseElement.class,
    com.sun.xml.ws.rx.rm.protocol.wsrm200702.UsesSequenceSSL.class,
    com.sun.xml.ws.rx.rm.protocol.wsrm200702.UsesSequenceSTR.class);

    public static RmRuntimeVersion forProtocolVersion(RmProtocolVersion protocolVersion) {
        for (RmRuntimeVersion version : values()) {
            if (version.protocolVersion == protocolVersion) {
                return version;
            }
        }

        assert false : "Unsupported WS-ReliableMessaging protocol version definition detected"; // we should never get here

        return null;
    }

    /**
     * Provides a default reliable messaging version value.
     *
     * @return a default reliable messaging version value. Currently returns {@link #WSRM200702}.
     *
     * @see RmVersion
     */
    public static RmRuntimeVersion getDefault() {
        return forProtocolVersion(RmProtocolVersion.getDefault());
    }


    /**
     * General constants
     */
    public final RmProtocolVersion protocolVersion;

    /**
     * Private fields
     */
    private final JaxbContextRepository jaxbContextRepository;

    private RmRuntimeVersion(RmProtocolVersion protocolVersion, Class<?>... rmProtocolClasses) {
        this.protocolVersion = protocolVersion;
        this.jaxbContextRepository = new JaxbContextRepository(rmProtocolClasses);
    }

    /**
     * Creates JAXB {@link Unmarshaller} that is able to unmarshall protocol elements for given WS-RM version.
     * <p />
     * As JAXB unmarshallers are not thread-safe, this method should be used to create a new {@link Unmarshaller} 
     * instance whenever there is a chance that the same instance might be invoked concurrently from multiple
     * threads. On th other hand, it is prudent to cache or pool {@link Unmarshaller} instances if possible as 
     * constructing a new {@link Unmarshaller} instance is rather expensive.
     * <p />
     * For additional information see this <a href="https://jaxb.dev.java.net/guide/Performance_and_thread_safety.html">blog entry</a>.
     *  
     * @return created JAXB unmarshaller
     * 
     * @exception RxRuntimeException in case the creation of unmarshaller failed
     */
    public Unmarshaller createUnmarshaller(AddressingVersion av) throws RxRuntimeException {
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

    @Override
    public String toString() {
        return "RmRuntimeVersion{" + "protocolVersion=" + protocolVersion + '}';
    }   
}
