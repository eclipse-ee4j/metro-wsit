/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.at;

import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPException;
import javax.xml.namespace.QName;
import javax.xml.ws.soap.SOAPFaultException;
import javax.xml.ws.WebServiceException;
import java.util.Locale;

/**
 * WS-C and WS-AT Fault factory.
 * This class creates and throws WS-AT related faults
 * <p>
 * The properties bind to a SOAP 1.2 fault as follows:
 * {@code
 * <S:Envelope>
 * <S:Header>
 * <wsa:Action>
 * http://schemas.xmlsoap.org/ws/2004/10/wscoor/fault
 * </wsa:Action>
 * <!-- Headers elided for clarity.  -->
 * </S:Header>
 * <S:Body>
 * <S:Fault>
 * <S:Code>
 * <S:Value>[Code]</S:Value>
 * <S:Subcode>
 * <S:Value>[Subcode]</S:Value>
 * </S:Subcode>
 * </S:Code>
 * <S:Reason>
 * <S:Text xml:lang="en">[Reason]</S:Text>
 * </S:Reason>
 * <S:Detail>
 * [Detail]
 * ...
 * </S:Detail>
 * </S:Fault>
 * </S:Body>
 * </S:Envelope>
 * The properties bind to a SOAP 1.1 fault as follows:
 * <S11:Envelope>
 * <S11:Body>
 * <S11:Fault>
 * <faultcode>[Subcode]</faultcode>
 * <faultstring xml:lang="en">[Reason]</faultstring>
 * </S11:Fault>
 * </S11:Body>
 * </S11:Envelope>
 * }
 */
public class WSATFaultFactory {
    static final String INVALID_STATE = "InvalidState";
    static final String INVALID_PROTOCOL = "InvalidProtocol";
    static final String INVALID_PARAMETERS = "InvalidParameters";
    static final String NO_ACTIVITY = "NoActivity";
    static final String CONTEXT_REFUSED = "ContextRefused";
    static final String ALREADY_REGISTERED = "AlreadyRegistered";
    static final String INCONSISTENT_INTERNAL_STATE = "InconsistentInternalState";
    static final String HTTP_SCHEMAS_XMLSOAP_ORG_WS_2004_10_WSAT_FAULT = "http://schemas.xmlsoap.org/ws/2004/10/wsat/fault";
    private static final String CLIENT = "Client";
    static final QName FAULT_CODE_Q_NAME11 = new QName(SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE, CLIENT);
    private static final String SENDER = "Sender";
    static final QName FAULT_CODE_Q_NAME = new QName(SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE, SENDER);
    private static boolean m_isSOAP11 = true;

    /**
     * 4.1. Invalid State
     * This fault is sent by either the coordinator or a participant to indicate that the endpoint
     * that generates the fault has received a message that is not valid for its current state.
     * This is an unrecoverable condition.
     * Properties:
     * [Code] Sender
     * [Subcode] wscoor:InvalidState
     * [Reason] The message was invalid for the current state of the activity.
     * [Detail] unspecified
     */
    public static void throwInvalidStateFault() {
        throwSpecifiedWSATFault("The message was invalid for the current state of the activity.", INVALID_STATE);
    }

    /**
     * 4.2. Invalid Protocol
     * This fault is sent by either the coordinator or a participant to indicate that the endpoint
     * that generates the fault received a message from an invalid protocol.  This is an
     * unrecoverable condition.
     * Properties:
     * [Code] Sender
     * [Subcode] wscoor:InvalidProtocol
     * [Reason] The protocol is invalid or is not supported by the coordinator.
     * [Detail] unspecified
     */
    public static void throwInvalidProtocolFault() {
        throwSpecifiedWSATFault("The protocol is invalid or is not supported by the coordinator.", INVALID_PROTOCOL);
    }

    /**
     * 4.3. Invalid Parameters
     * This fault is sent by either the coordinator or a participant to indicate that the endpoint
     * that generated the fault received invalid parameters on or within a message.  This is an
     * unrecoverable condition.
     * Properties:
     * [Code] Sender
     * [Subcode] wscoor:InvalidParameters
     * [Reason] The message contained invalid parameters and could not be processed.
     * [Detail] unspecified
     */
    public static void throwInvalidParametersFault() {
        throwSpecifiedWSATFault("The message contained invalid parameters and could not be processed.", INVALID_PARAMETERS);
    }


    /**
     * 4.4. No Activity
     * This fault is sent by the coordinator if the participant has been quiet for too long and is
     * presumed to have ended.
     * Properties:
     * [Code] Sender
     * [Subcode] wscoor:NoActivity
     * [Reason] The participant is not responding and is presumed to have ended.
     * [Detail] unspecified
     */
    public static void throwNoActivityFault() {
        throwSpecifiedWSATFault("The participant is not responding and is presumed to have ended.", NO_ACTIVITY);
    }

    /**
     * 4.5. Context Refused
     * This fault is sent to a coordinator to indicate that the endpoint cannot accept a context
     * which it was passed:
     * Properties:
     * [Code] Sender
     * [Subcode] wscoor:ContextRefused
     * [Reason] The coordination context that was provided could not be accepted.
     * [Detail] unspecified
     */
    public static void throwContextRefusedFault() {
        throwSpecifiedWSATFault("The coordination context that was provided could not be accepted.", CONTEXT_REFUSED);
    }

    /**
     * 4.6 Already Registered
     * This fault is sent to a participant if the coordinator detects that the participant attempted
     * to register for the same protocol of the same activity more than once.
     * Properties:
     * [Code] Sender
     * [Subcode] wscoor:AlreadyRegistered
     * [Reason] The participant has already registered for the same protocol.
     * [Detail] unspecified
     */
    public static void throwAlreadyRegisteredFault() {
        throwSpecifiedWSATFault("The participant has already registered for the same protocol.", ALREADY_REGISTERED);
    }

    /**
     * 5.1 InconsistentInternalState
     * This fault is sent by a participant to indicate that it cannot fulfill its obligations.  This
     * indicates a global consistency failure and is an unrecoverable condition.
     * Properties:
     * [Code] Sender
     * [Subcode] wsat:InconsistentInternalState
     * [Reason] A global consistency failure has occurred. This is an unrecoverable condition.
     * [Detail] unspecified
     */
    public static void throwInconsistentInternalStateFault() {
        throwSpecifiedWSATFault("A global consistency failure has occurred. This is an unrecoverable condition.", INCONSISTENT_INTERNAL_STATE);
    }

    static void setSOAPVersion11(boolean isSOAPVersion11) {
        m_isSOAP11 = isSOAPVersion11;
    }

    private static void throwSpecifiedWSATFault(String reasonString, String subCode) {
        try {
            SOAPFault fault;
            if (m_isSOAP11) {
                fault = SOAPFactory.newInstance().createFault(
                        reasonString,
                        new QName(HTTP_SCHEMAS_XMLSOAP_ORG_WS_2004_10_WSAT_FAULT, subCode, "wsat"));
            } else {
                fault = SOAPFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL).createFault();
                fault.setFaultCode(FAULT_CODE_Q_NAME);
                fault.appendFaultSubcode(new QName(HTTP_SCHEMAS_XMLSOAP_ORG_WS_2004_10_WSAT_FAULT, subCode, "wsat"));
                fault.addFaultReasonText(reasonString, Locale.ENGLISH);
            }
            throw new SOAPFaultException(fault);
        } catch (SOAPException e) {
            throw new WebServiceException(e);
        }
    }

}
