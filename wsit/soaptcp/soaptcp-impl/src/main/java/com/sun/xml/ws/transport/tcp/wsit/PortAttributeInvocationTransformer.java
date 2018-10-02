/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.transport.tcp.wsit;

import com.sun.xml.ws.xmlfilter.Invocation;
import com.sun.xml.ws.xmlfilter.XmlStreamWriterMethodType;
import com.sun.xml.ws.xmlfilter.InvocationTransformer;
import com.sun.xml.ws.xmlfilter.XmlFilteringUtils;
import com.sun.xml.ws.xmlfilter.XmlFilteringUtils.AttributeInfo;

import com.sun.xml.ws.transport.tcp.resources.MessagesMessages;
import com.sun.xml.ws.transport.tcp.server.WSTCPModule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;

import javax.xml.stream.XMLStreamWriter;
import static com.sun.xml.ws.transport.tcp.wsit.TCPConstants.*;

/**
 * SOAP/TCP invocation transformer, which is responsible to insert SOAP/TCP 'port' 
 * attribute in a published WSDL
 * 
 * @author Alexey Stashok
 */
public class PortAttributeInvocationTransformer implements InvocationTransformer {
    private static final String RUNTIME_PORT_CHANGE_VALUE = "SET_BY_RUNTIME";

    private static final Logger logger = Logger.getLogger(
            com.sun.xml.ws.transport.tcp.util.TCPConstants.LoggingDomain + ".server");
    
    private Collection<Invocation> invocationWrapper = new ArrayList<Invocation>(4);
    
    private boolean isProcessingWSTCPAssertion;
    
    private volatile Invocation addPortAttributeInvocation;

    /**
     * Method transforms SOAP/TCP port attribute, otherwise returns the same invocation.
     * WARNING: due to perf. reasons, method reuses the same Collection instance.
     * So call transform next time only if previously returned Collection is not required
     * any more.
     * 
     * @param invocation
     * @return transformed invocations
     */
    public Collection<Invocation> transform(final Invocation invocation) {
        Invocation resultInvocation = invocation;
        switch (invocation.getMethodType()) {
            case WRITE_START_ELEMENT:
                if (!isProcessingWSTCPAssertion) {
                    isProcessingWSTCPAssertion = startBuffering(invocation);
                }
                break;
            case WRITE_END_ELEMENT:
                isProcessingWSTCPAssertion = false;
                break;
            case WRITE_ATTRIBUTE:
                if (isProcessingWSTCPAssertion && isReplacePortAttribute(invocation)) {
                    try {
                        initializeAddPortAttributeIfRequired();
                        if (addPortAttributeInvocation == null && 
                                WSTCPModule.getInstance().getPort() == -1) {
                            if (logger.isLoggable(Level.WARNING)) {
                                logger.log(Level.WARNING,
                                        MessagesMessages.WSTCP_1162_UNSUPPORTED_PORT_ATTRIBUTE());
                            }
                        }
                    } catch(Exception e) {
                        if (logger.isLoggable(Level.WARNING)) {
                            logger.log(Level.WARNING, 
                                    MessagesMessages.WSTCP_1161_ADD_PORT_ATTR_INIT_FAIL(), e);
                        }
                    }
                    
                    resultInvocation = addPortAttributeInvocation;
                }
                break;
            case CLOSE:
                isProcessingWSTCPAssertion = false;
                break;
            default:
                break;
            }

        invocationWrapper.clear();
        if (resultInvocation != null) {
            invocationWrapper.add(resultInvocation);
        }
        
        return invocationWrapper;
    }

    private void initializeAddPortAttributeIfRequired() throws Exception {
        int port;
        if (addPortAttributeInvocation == null && 
                (port = WSTCPModule.getInstance().getPort()) != -1) {
            synchronized(this) {
                if (addPortAttributeInvocation == null) {
                    addPortAttributeInvocation = Invocation.createInvocation(
                            XMLStreamWriter.class.getMethod(
                            XmlStreamWriterMethodType.WRITE_ATTRIBUTE.getMethodName(), 
                            String.class, String.class),
                            new Object[] {
                                TCPTRANSPORT_PORT_ATTRIBUTE.getLocalPart(),
                                Integer.toString(port)
                            });
                }
            }
        }
    }

    private boolean isReplacePortAttribute(Invocation invocation) {
        AttributeInfo attr = XmlFilteringUtils.getAttributeNameToWrite(invocation, "");
        if (TCPTRANSPORT_PORT_ATTRIBUTE.equals(attr.getName())) {
            if (RUNTIME_PORT_CHANGE_VALUE.equals(attr.getValue())) return true;
            
            
            String attrValue = attr.getValue();
            int portNumber = -1;
            if (attrValue != null) {
                try {
                    portNumber = Integer.parseInt(attrValue);
                } catch(NumberFormatException e) {
                }
            }
            
            if (portNumber > 0) return false;
            
            if (logger.isLoggable(Level.WARNING)) {
                logger.log(Level.WARNING, MessagesMessages.WSTCP_1160_PORT_ATTR_INVALID_VALUE(attrValue));
            }
            
            return true;
        }
        
        return false;
    }
    
    private boolean startBuffering(final Invocation invocation) {
        final QName elementName = XmlFilteringUtils.getElementNameToWrite(invocation, "");
        return TCPTRANSPORT_POLICY_ASSERTION.equals(elementName);
    }    
}
