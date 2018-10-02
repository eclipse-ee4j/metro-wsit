/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
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

import static com.sun.xml.ws.xmlfilter.ProcessingStateChange.*;

/**
 *
 * @author Marek Potociar (marek.potociar at sun.com)
 */
public class PrivateElementFilteringStateMachine implements FilteringStateMachine {
    private static final Logger LOGGER = Logger.getLogger(PrivateElementFilteringStateMachine.class);
    
    private int depth; // indicates the depth in which we are currently nested in the element that should be filtered out
    private boolean filteringOn; // indicates that currently processed elements will be filtered out.
    
    private final QName[] filteredElements;
    
    /** Creates a new instance of PrivateElementFilteringStateMachine */
    public PrivateElementFilteringStateMachine(final QName... filteredElements) {
        if (filteredElements == null) {
            this.filteredElements = new QName[]{};
        } else {
            this.filteredElements = new QName[filteredElements.length];
            System.arraycopy(filteredElements, 0, this.filteredElements, 0, filteredElements.length);
        }
    }
    
    public ProcessingStateChange getStateChange(final Invocation invocation, final XMLStreamWriter writer) {
        LOGGER.entering(invocation);
        ProcessingStateChange resultingState = NO_CHANGE;
        try {
            switch (invocation.getMethodType()) {
                case WRITE_START_ELEMENT:
                    if (filteringOn) {
                        depth++;
                    } else {
                        filteringOn = startFiltering(invocation, writer);
                        if (filteringOn) {
                            resultingState = START_FILTERING;
                        }
                    }
                    break;
                case WRITE_END_ELEMENT:
                    if (filteringOn) {
                        if (depth == 0) {
                            filteringOn = false;
                            resultingState = STOP_FILTERING;
//                            return invocation.executeBatch(mirrorWriter);
                        } else {
                            depth--;
                        }
                    }
                    break;
                case CLOSE:
                    if (filteringOn) {
                        filteringOn = false;
                        resultingState = STOP_FILTERING;
                    }
                default:
                    break;
            }
            
            return resultingState;
            
        } finally {
            LOGGER.exiting(resultingState);
        }
    }
    
    private boolean startFiltering(final Invocation invocation, final XMLStreamWriter writer) {
        final QName elementName = XmlFilteringUtils.getElementNameToWrite(invocation, XmlFilteringUtils.getDefaultNamespaceURI(writer));
        
        for (QName filteredElement : filteredElements) {
            if (filteredElement.equals(elementName)) {
                return true;
            }
        }
        
        return false;
    }
}
