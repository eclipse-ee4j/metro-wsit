/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss;

import com.sun.xml.wss.impl.XWSSecurityRuntimeException;
import com.sun.xml.wss.logging.LogDomainConstants;
import com.sun.xml.wss.logging.LogStringsMessages;
import java.util.logging.Level;
import java.util.logging.Logger;
public final class BasicSecurityProfile {
    
    private boolean timeStampFound = false;
    private static Logger log = Logger.getLogger(LogDomainConstants.WSS_API_DOMAIN,LogDomainConstants.WSS_API_DOMAIN_BUNDLE);
    
    /** Creates a new instance of BasicSecurityProfile */
    public BasicSecurityProfile() {
    }
    
    /**
     * 
     */
    public static void log_bsp_3203(){
        log.log(Level.SEVERE,LogStringsMessages.BSP_3203_ONECREATED_TIMESTAMP());
        throw new XWSSecurityRuntimeException(LogStringsMessages.BSP_3203_ONECREATED_TIMESTAMP());
    }
    
    /**
     * 
     */
    public static void log_bsp_3224(){
        log.log(Level.SEVERE,LogStringsMessages.BSP_3224_ONEEXPIRES_TIMESTAMP());
        throw new XWSSecurityRuntimeException(LogStringsMessages.BSP_3224_ONEEXPIRES_TIMESTAMP());
    }
    
    /**
     *
     */
    public static void log_bsp_3222(String elementName){
        log.log(Level.SEVERE,LogStringsMessages.BSP_3222_ELEMENT_NOT_ALLOWED_UNDER_TIMESTMP(elementName));
        throw new XWSSecurityRuntimeException(LogStringsMessages.BSP_3222_ELEMENT_NOT_ALLOWED_UNDER_TIMESTMP(elementName));
    }
    
    /**
     * 
     */
    public static void log_bsp_3221(){
        log.log(Level.SEVERE,LogStringsMessages.BSP_3221_CREATED_BEFORE_EXPIRES_TIMESTAMP());
        throw new XWSSecurityRuntimeException(LogStringsMessages.BSP_3221_CREATED_BEFORE_EXPIRES_TIMESTAMP());
    }
    
    /**
     *
     */
    public static void log_bsp_3227() throws XWSSecurityException{
        log.log(Level.SEVERE,LogStringsMessages.BSP_3227_SINGLE_TIMESTAMP());
        throw new XWSSecurityException(LogStringsMessages.BSP_3227_SINGLE_TIMESTAMP());
    }
    
    /**
     * 
     */
    public static void log_bsp_3225(){
        log.log(Level.SEVERE,LogStringsMessages.BSP_3225_CREATED_VALUE_TYPE_TIMESTAMP());
        throw new XWSSecurityRuntimeException(LogStringsMessages.BSP_3225_CREATED_VALUE_TYPE_TIMESTAMP());
    }
    
    /**
     * 
     */
    public static void log_bsp_3226(){
        log.log(Level.SEVERE,LogStringsMessages.BSP_3226_EXPIRES_VALUE_TYPE_TIMESTAMP());
        throw new XWSSecurityRuntimeException(LogStringsMessages.BSP_3226_EXPIRES_VALUE_TYPE_TIMESTAMP());
    }
    
    /**
     *
     */
    public static void log_bsp_3104() {
        log.log(Level.WARNING,LogStringsMessages.BSP_3104_ENVELOPED_SIGNATURE_DISCORAGED());
    }
    
    /**
     *
     */
    public void setTimeStampFound(boolean value){
        this.timeStampFound = value;
    }
    /**
     * 
     * @return boolean
     */
    public boolean isTimeStampFound(){
        return timeStampFound;
    }
    
}
