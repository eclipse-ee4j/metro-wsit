/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.transport.tcp.servicechannel;

import com.sun.istack.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.ws.WebFault;

/**
 * @author Alexey Stashok
 */
@WebFault(name = "ServiceChannelException", targetNamespace = "http://servicechannel.tcp.transport.ws.xml.sun.com/", faultBean = "com.sun.xml.ws.transport.tcp.servicechannel.ServiceChannelException$ServiceChannelExceptionBean")
public class ServiceChannelException extends Exception {
    private ServiceChannelExceptionBean faultInfo;
    
    public ServiceChannelException() {
        faultInfo = new ServiceChannelExceptionBean();
    }
    
    public ServiceChannelException(ServiceChannelErrorCode errorCode, @Nullable final String message) {
        super(message);
        faultInfo = new ServiceChannelExceptionBean(errorCode , message);
    }
    
    public ServiceChannelException(final String message, final ServiceChannelExceptionBean faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }
    
    public ServiceChannelException(final String message, final ServiceChannelExceptionBean faultInfo, final Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }
    
    public ServiceChannelExceptionBean getFaultInfo() {
        return faultInfo;
    }
    
    public void setFaultInfo(final ServiceChannelExceptionBean faultInfo) {
        this.faultInfo = faultInfo;
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "serviceChannelExceptionBean", propOrder = {
        "errorCode",
        "message"
    })
    public static class ServiceChannelExceptionBean {
        @XmlElement(required = true)
        private ServiceChannelErrorCode errorCode;
        
        private String message;
        
        public ServiceChannelExceptionBean() {
        }
        
        public ServiceChannelExceptionBean(final ServiceChannelErrorCode errorCode, final String message) {
            this.errorCode = errorCode;
            this.message = message;
        }
        
        public ServiceChannelErrorCode getErrorCode() {
            return errorCode;
        }
        
        public void setErrorCode(ServiceChannelErrorCode errorCode)  {
            this.errorCode = errorCode;
        }
        
        public String getMessage() {
            return this.message;
        }
        
        public void setMessage(final String message) {
            this.message = message;
        }
    }
}
