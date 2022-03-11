/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.xmlfilter;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamWriter;

import com.sun.istack.logging.Logger;

/**
 *
 * @author Marek Potociar (marek.potociar at sun.com)
 */
public final class MexImportFilteringStateMachine implements FilteringStateMachine {
    private enum StateMachineMode {
        INACTIVE,
        BUFFERING,
        FILTERING
    }
    
    private static final Logger LOGGER = Logger.getLogger(MexImportFilteringStateMachine.class);
    
    private static final String MEX_NAMESPACE = "http://schemas.xmlsoap.org/ws/2004/09/mex";
    private static final String WSDL_NAMESPACE = "http://schemas.xmlsoap.org/wsdl/";
    private static final QName WSDL_IMPORT_ELEMENT = new QName(WSDL_NAMESPACE, "import");
    private static final QName IMPORT_NAMESPACE_ATTIBUTE = new QName(WSDL_NAMESPACE, "namespace");
    
    private int depth; // indicates the depth in which we are currently nested in the element that should be filtered out
    private StateMachineMode currentMode = StateMachineMode.INACTIVE; // indicates that current mode of the filtering state machine
    
    /** Creates a new instance of MexImportFilteringStateMachine */
    public MexImportFilteringStateMachine() {
        // nothing to initialize
    }
    
    @Override
    public ProcessingStateChange getStateChange(final Invocation invocation, final XMLStreamWriter writer) {
        LOGGER.entering(invocation);
        ProcessingStateChange resultingState = ProcessingStateChange.NO_CHANGE;
        try {
            switch (invocation.getMethodType()) {
                case WRITE_START_ELEMENT:
                    if (currentMode == StateMachineMode.INACTIVE) {
                        if (startBuffering(invocation, writer)) {
                            resultingState = ProcessingStateChange.START_BUFFERING;
                            currentMode = StateMachineMode.BUFFERING;
                        }
                    } else {
                        depth++;
                    }
                    break;
                case WRITE_END_ELEMENT:
                    if (currentMode != StateMachineMode.INACTIVE) {
                        if (depth == 0) {
                            resultingState = (currentMode == StateMachineMode.BUFFERING) ? ProcessingStateChange.STOP_BUFFERING : ProcessingStateChange.STOP_FILTERING;
                            currentMode = StateMachineMode.INACTIVE;
                        } else {
                            depth--;
                        }
                    }
                    break;
                case WRITE_ATTRIBUTE:
                    if (currentMode == StateMachineMode.BUFFERING && startFiltering(invocation, writer)) {
                        resultingState = ProcessingStateChange.START_FILTERING;
                        currentMode = StateMachineMode.FILTERING;
                    }
                    break;
                case CLOSE:
                    switch (currentMode) {
                        case BUFFERING:
                            resultingState = ProcessingStateChange.STOP_BUFFERING; break;
                        case FILTERING:
                            resultingState = ProcessingStateChange.STOP_FILTERING; break;
                    }
                    currentMode = StateMachineMode.INACTIVE;
                    break;
                default:
                    break;
            }
            
            return resultingState;
            
        } finally {
            LOGGER.exiting(resultingState);
        }
    }
    
    private boolean startFiltering(final Invocation invocation, final XMLStreamWriter writer) {
        final XmlFilteringUtils.AttributeInfo attributeInfo = XmlFilteringUtils.getAttributeNameToWrite(invocation, XmlFilteringUtils.getDefaultNamespaceURI(writer));
        return IMPORT_NAMESPACE_ATTIBUTE.equals(attributeInfo.getName()) && MEX_NAMESPACE.equals(attributeInfo.getValue());
    }
    
    private boolean startBuffering(final Invocation invocation, final XMLStreamWriter writer) {
        final QName elementName = XmlFilteringUtils.getElementNameToWrite(invocation, XmlFilteringUtils.getDefaultNamespaceURI(writer));
        return WSDL_IMPORT_ELEMENT.equals(elementName);
    }
}
