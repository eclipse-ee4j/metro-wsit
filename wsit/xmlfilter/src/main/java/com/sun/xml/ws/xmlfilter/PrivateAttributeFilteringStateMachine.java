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

import javax.xml.stream.XMLStreamWriter;

import com.sun.istack.logging.Logger;
import com.sun.xml.ws.policy.PolicyConstants;

/**
 *
 * @author Marek Potociar (marek.potociar at sun.com)
 */
public class PrivateAttributeFilteringStateMachine implements FilteringStateMachine {
    private static final Logger LOGGER = Logger.getLogger(PrivateAttributeFilteringStateMachine.class);
    
    private int depth; // indicates the depth in which we are currently nested in the element that should be filtered out
    private boolean filteringOn; // indicates that currently processed elements will be filtered out.
    private boolean cmdBufferingOn; // indicates whether the commands should be buffered or whether they can be directly executed on the underlying XML output stream
    
    
    /** Creates a new instance of PrivateAttributeFilteringStateMachine */
    public PrivateAttributeFilteringStateMachine() {
        // nothing to initialize
    }
    
    @Override
    public ProcessingStateChange getStateChange(final Invocation invocation, final XMLStreamWriter writer) {
        LOGGER.entering(invocation);
        ProcessingStateChange resultingState = ProcessingStateChange.NO_CHANGE;
        try {
            switch (invocation.getMethodType()) {
                case WRITE_START_ELEMENT:
                    if (filteringOn) {
                        depth++;
                    } else if (cmdBufferingOn) {
                        resultingState = ProcessingStateChange.RESTART_BUFFERING;
                    } else {
                        cmdBufferingOn = true;
                        resultingState = ProcessingStateChange.START_BUFFERING;
                    }
                    break;
                case WRITE_END_ELEMENT:
                    if (filteringOn) {
                        if (depth == 0) {
                            filteringOn = false;
                            resultingState = ProcessingStateChange.STOP_FILTERING;
                        } else {
                            depth--;
                        }
                    } else if (cmdBufferingOn) {
                        cmdBufferingOn = false;
                        resultingState = ProcessingStateChange.STOP_BUFFERING;
                    }
                    break;
                case WRITE_ATTRIBUTE:
                    if (!filteringOn && cmdBufferingOn && startFiltering(invocation, writer)) {
                        filteringOn = true;
                        cmdBufferingOn = false;
                        resultingState = ProcessingStateChange.START_FILTERING;
                    }
                    break;
                case CLOSE:
                    if (filteringOn) {
                        filteringOn = false;
                        resultingState = ProcessingStateChange.STOP_FILTERING;
                    } else if (cmdBufferingOn) {
                        cmdBufferingOn = false;
                        resultingState = ProcessingStateChange.STOP_BUFFERING;
                    }
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
        return PolicyConstants.VISIBILITY_ATTRIBUTE.equals(attributeInfo.getName()) && PolicyConstants.VISIBILITY_VALUE_PRIVATE.equals(attributeInfo.getValue());
    }
}
