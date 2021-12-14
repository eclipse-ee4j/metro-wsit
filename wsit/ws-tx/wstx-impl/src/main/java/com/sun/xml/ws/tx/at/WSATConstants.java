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

import javax.xml.namespace.QName;

public interface WSATConstants {

    // general
    String SOAP_ENVELOPE = "http://schemas.xmlsoap.org/soap/envelope";
    String MUST_UNDERSTAND = "mustUnderstand";
    String MESSAGE_ID = "MessageID";
    // addressing
    String WSADDRESSING_NS_URI = "http://schemas.xmlsoap.org/ws/2004/08/addressing";
    String WSA = "wsa";
    String REFERENCE_PARAMETERS = "ReferenceParameters";
    String TO = "To";
    String ADDRESS = "Address";
    String REPLY_TO = "ReplyTo";
    String FROM = "From";
    String FAULT_TO = "FaultTo";
    String ACTION = "Action";
    // coor
    String WSCOOR10_NS_URI = "http://schemas.xmlsoap.org/ws/2004/10/wscoor";
    String CURRENT_WSCOOR = WSCOOR10_NS_URI;
    String WSCOOR = "wscoor";
    String COORDINATION_TYPE = "CoordinationType";
    String REGISTRATION_SERVICE = "RegistrationService";
    String EXPIRES = "Expires";
    String IDENTIFIER = "Identifier";
    String PARTICIPANT_PROTOCOL_SERVICE = "ParticipantProtocolService";
    String PROTOCOL_IDENTIFIER = "ProtocolIdentifier";
    String REGISTER = "Register";
    String REGISTER_RESPONSE = "RegisterResponse";
    String COORDINATION_CONTEXT = "CoordinationContext";
    String COORDINATOR_PROTOCOL_SERVICE = "CoordinatorProtocolService";
    QName WSCOOR_CONTEXT_QNAME = new QName(WSCOOR10_NS_URI, COORDINATION_CONTEXT);
    QName WSCOOR_REGISTER_QNAME = new QName(WSCOOR10_NS_URI, REGISTER);

    String WSCOOR11_NS_URI = "http://docs.oasis-open.org/ws-tx/wscoor/2006/06";
    QName WSCOOR11_CONTEXT_QNAME = new QName(WSCOOR11_NS_URI, COORDINATION_CONTEXT);
    QName WSCOOR11_REGISTER_QNAME = new QName(WSCOOR11_NS_URI, REGISTER);

    // at
    String HTTP_SCHEMAS_XMLSOAP_ORG_WS_2004_10_WSAT = "http://schemas.xmlsoap.org/ws/2004/10/wsat";
    String WSAT10_NS_URI = "http://schemas.xmlsoap.org/ws/2004/10/wsat";
    String WSAT = "wsat";
    String DURABLE_2PC = "Durable2PC";
    String VOLATILE_2PC = "Volatile2PC";
    String PREPARE = "Prepare";
    String COMMIT = "Commit";
    String ROLLBACK = "Rollback";
    String PREPARED = "Prepared";
    String READONLY = "ReadOnly";
    String COMMITTED = "Committed";
    String ABORTED = "Aborted";
    String REPLAY = "Replay";
    String HTTP_SCHEMAS_XMLSOAP_ORG_WS_2004_10_WSAT_DURABLE_2PC = WSAT10_NS_URI + "/" + DURABLE_2PC;
    String HTTP_SCHEMAS_XMLSOAP_ORG_WS_2004_10_WSAT_VOLATILE_2PC = WSAT10_NS_URI + "/" + VOLATILE_2PC;
    String WLA_WSAT_NS_URI = "http://com.sun.xml.ws.tx.at/ws/2008/10/wsat";
    String WSAT_WSAT = "wsat-wsat";
    String WSAT_CONTEXT_ROOT = "__wstx-services";
    String TXID = "txId";
    QName TXID_QNAME = new QName(WLA_WSAT_NS_URI, TXID, WSAT_WSAT);
    String BRANCHQUAL = "branchQual";
    QName BRANCHQUAL_QNAME = new QName(WLA_WSAT_NS_URI, BRANCHQUAL, WSAT_WSAT);
    String ROUTING = "routing";
    QName ROUTING_QNAME = new QName(WLA_WSAT_NS_URI, ROUTING, WSAT_WSAT);
    // outbound endpoints
    String WSAT_COORDINATORPORTTYPEPORT = "/"+ WSAT_CONTEXT_ROOT +"/CoordinatorPortType";
    String WSAT_REGISTRATIONCOORDINATORPORTTYPEPORT = "/"+ WSAT_CONTEXT_ROOT +"/RegistrationPortTypeRPC";
    // inbound endpoints
    String WSAT_REGISTRATIONREQUESTERPORTTYPEPORT = "/"+ WSAT_CONTEXT_ROOT +"/RegistrationRequesterPortType";
    //RegistrationRequesterPortTypeRPC";
    String WSAT_PARTICIPANTPORTTYPEPORT = "/"+ WSAT_CONTEXT_ROOT +"/ParticipantPortType";
    // logger                                                     
    String DEBUG_WSAT = "DebugWSAT";

    String WSAT11_NS_URI = "http://docs.oasis-open.org/ws-tx/wsat/2006/06";
    String WSAT11_DURABLE_2PC = WSAT11_NS_URI + "/" + DURABLE_2PC;
    String WSAT11_VOLATILE_2PC = WSAT11_NS_URI + "/" + VOLATILE_2PC;
    String WSAT11_REGISTRATIONCOORDINATORPORTTYPEPORT = "/"+ WSAT_CONTEXT_ROOT +"/RegistrationPortTypeRPC11";
    String WSAT11_PARTICIPANTPORTTYPEPORT = "/"+ WSAT_CONTEXT_ROOT +"/ParticipantPortType11";
    String WSAT11_COORDINATORPORTTYPEPORT = "/"+ WSAT_CONTEXT_ROOT +"/CoordinatorPortType11";
    // inbound endpoints
    String WSAT11_REGISTRATIONREQUESTERPORTTYPEPORT = "/"+ WSAT_CONTEXT_ROOT +"/RegistrationRequesterPortType11";
    //  TM
    String TXPROP_WSAT_FOREIGN_RECOVERY_CONTEXT = "com.sun.xml.ws.tx.foreignContext";
    // tube request map storage
    String WSAT_TRANSACTION = "wsat.transaction";
    String WSAT_TRANSACTION_XID = "wsat.transaction.xid";
}
