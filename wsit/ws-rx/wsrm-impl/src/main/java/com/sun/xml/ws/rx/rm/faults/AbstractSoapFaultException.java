/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.rm.faults;

import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.addressing.AddressingVersion;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.Messages;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.rx.RxRuntimeException;
import com.sun.xml.ws.rx.rm.runtime.RmRuntimeVersion;
import com.sun.xml.ws.rx.rm.runtime.RuntimeContext;
import com.sun.xml.ws.rx.rm.runtime.sequence.Sequence;
import java.util.List;
import javax.xml.namespace.QName;
import jakarta.xml.soap.Detail;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPFault;

/**
 *
 */
public abstract class AbstractSoapFaultException extends RxRuntimeException {

    public static enum Code {
        Sender {
             QName asQName(SOAPVersion sv) {
                return sv.faultCodeClient;
             }
        },
        Receiver {
             QName asQName(SOAPVersion sv) {
                return sv.faultCodeServer;
             }            
        };

        abstract QName asQName(SOAPVersion sv);
    }

    private final boolean mustTryTodeliver;
    private final String faultReasonText;

    protected AbstractSoapFaultException(String exceptionMessage, String faultReasonText, boolean mustTryToDeliver, Throwable cause) {
        super(exceptionMessage, cause);
        
        this.faultReasonText = faultReasonText;
        this.mustTryTodeliver = mustTryToDeliver;
    }

    protected AbstractSoapFaultException(String exceptionMessage, String faultReasonText, boolean mustTryToDeliver) {
        super(exceptionMessage);

        this.faultReasonText = faultReasonText;
        this.mustTryTodeliver = mustTryToDeliver;
    }

    public abstract Code getCode();

    public abstract QName getSubcode(RmRuntimeVersion rv);

    public final String getReason() {
        return faultReasonText;
    }

    public abstract Detail getDetail(RuntimeContext rc);

    public boolean mustTryToDeliver() {
        return mustTryTodeliver;
    }

    public Packet toRequest(RuntimeContext rc) {
        return rc.communicator.createRequestPacket(
                createSoapFaultMessage(rc, true),
                getProperFaultActionForAddressingVersion(rc.rmVersion, rc.addressingVersion),
                false);
    }

    public Packet toResponse(RuntimeContext rc, Packet request) {
        return rc.communicator.createResponsePacket(
                request,
                createSoapFaultMessage(rc, true),
                getProperFaultActionForAddressingVersion(rc.rmVersion, rc.addressingVersion));
    }

    protected final Message createSoapFaultMessage(RuntimeContext rc, boolean attachSequenceFaultElement) {
        try {
            SOAPFault soapFault = rc.soapVersion.saajSoapFactory.createFault();

            // common SOAP1.1 and SOAP1.2 Fault settings
            if (faultReasonText != null) {
                soapFault.setFaultString(faultReasonText, java.util.Locale.ENGLISH);
            }

            final Detail detail = getDetail(rc);
            // SOAP version-specific SOAP Fault settings
            switch (rc.soapVersion) {
                case SOAP_11:
                    soapFault.setFaultCode(getSubcode(rc.rmVersion));
                    break;
                case SOAP_12:
                    soapFault.setFaultCode(getCode().asQName(rc.soapVersion));
                    soapFault.appendFaultSubcode(getSubcode(rc.rmVersion));
                    if (detail != null) {
                        soapFault.addChildElement(detail);
                    }
                    break;
                default:
                    throw new RxRuntimeException("Unsupported SOAP version: '" + rc.soapVersion.toString() + "'");
            }

            Message soapFaultMessage = Messages.create(soapFault);

            if (attachSequenceFaultElement && rc.soapVersion == SOAPVersion.SOAP_11) {
                soapFaultMessage.getHeaders().add(rc.protocolHandler.createSequenceFaultElementHeader(getSubcode(rc.rmVersion), detail));
            }

            return soapFaultMessage;

        } catch (SOAPException ex) {
            throw new RxRuntimeException("Error creating a SOAP fault", ex);
        }
    }

    /**
     * TODO javadoc
     *
     * @return
     */
    protected static String getProperFaultActionForAddressingVersion(RmRuntimeVersion rmVersion, AddressingVersion addressingVersion) {
        return (addressingVersion == AddressingVersion.MEMBER) ? addressingVersion.getDefaultFaultAction() : rmVersion.protocolVersion.wsrmFaultAction;
    }

    protected static final class DetailBuilder {
        private final RuntimeContext rc;

        private final Detail detail;

        public DetailBuilder(RuntimeContext rc) {
            this.rc = rc;
            
            try {
                this.detail = rc.soapVersion.saajSoapFactory.createDetail();
            } catch (SOAPException ex) {
                throw new RxRuntimeException("Error creating a SOAP fault detail", ex);
            }
        }

        public Detail build() {
            return detail;
        }

        public DetailBuilder addSequenceIdentifier(String sequenceId) {
            try {
                detail.addDetailEntry(new QName(rc.rmVersion.protocolVersion.protocolNamespaceUri, "Identifier")).setValue(sequenceId);
            } catch (SOAPException ex) {
                throw new RxRuntimeException("Error creating a SOAP fault detail", ex);
            }

            return this;
        }

        public DetailBuilder addMaxMessageNumber(long number) {
            try {
                detail.addDetailEntry(new QName(rc.rmVersion.protocolVersion.protocolNamespaceUri, "MaxMessageNumber")).setValue(Long.toString(number));
            } catch (SOAPException ex) {
                throw new RxRuntimeException("Error creating a SOAP fault detail", ex);
            }

            return this;
        }

        public DetailBuilder addSequenceAcknowledgement(List<Sequence.AckRange> ackedRanges) {
            // TODO P3 implement adding SequenceAcknowledgement SOAPFault detail entry

            return this;
        }
    }
}
