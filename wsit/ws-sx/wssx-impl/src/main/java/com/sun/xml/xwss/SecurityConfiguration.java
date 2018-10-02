/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.xwss;

import java.io.InputStream;
import javax.security.auth.callback.CallbackHandler;

import com.sun.xml.wss.SecurityEnvironment;
import com.sun.xml.wss.XWSSecurityException;
import com.sun.xml.wss.impl.XWSSecurityRuntimeException;
import com.sun.xml.wss.impl.config.SecurityConfigurationXmlReader;
import com.sun.xml.wss.impl.config.ApplicationSecurityConfiguration;
import com.sun.xml.wss.impl.misc.DefaultSecurityEnvironmentImpl;
import java.io.IOException;
import java.net.URL;

/**
 * Digester for XWS-Security configuration.
 * @since JAXWS 2.0
 */

public class SecurityConfiguration implements XWSSecurityConfiguration {

    //public static final String MESSAGE_SECURITY_CONFIGURATION =
     //   "com.sun.xml.ws.security.configuration";

    private ApplicationSecurityConfiguration configuration = null;
    private CallbackHandler callbackhandler = null;
    private SecurityEnvironment securityEnvironment = null;
    private boolean configEmpty = false;
    
    public SecurityConfiguration(URL configUrl)throws XWSSecurityException {
        
        if (configUrl == null) {
            configEmpty = true;
            return;
        }

       InputStream config = null;
       try {
           config = configUrl.openStream();
           
           if (config == null) {
               configEmpty = true;
               return;
           }
           
           configuration = SecurityConfigurationXmlReader.
                   createApplicationSecurityConfiguration(config);
           callbackhandler = (CallbackHandler)Class.forName(
                   configuration.getSecurityEnvironmentHandler(),true,
                   Thread.currentThread().getContextClassLoader()).newInstance();
           securityEnvironment =
                   new DefaultSecurityEnvironmentImpl(callbackhandler);
           
       } catch (IOException e) {
           throw new XWSSecurityException(e);
       } catch (Exception e) {
           throw new XWSSecurityException(e);
       } finally {
           try {
               if (config != null) {
                   config.close();
               }
           } catch (IOException e) {
               //do nothing
           }
       }
    } 
    
    /**
     * 
     * @param config XWSS Security Configuration.
     * @throws com.sun.xml.wss.XWSSecurityException is XWS-Security configuration file is not wellformed.
     */
    public SecurityConfiguration(InputStream config) 
        throws XWSSecurityException {
          
        if (config == null) {
            configEmpty = true;
            return;
        }

        try {
            configuration = SecurityConfigurationXmlReader.
                createApplicationSecurityConfiguration(config);
            callbackhandler = (CallbackHandler)Class.forName(
                configuration.getSecurityEnvironmentHandler(),true, 
                Thread.currentThread().getContextClassLoader()).newInstance();
            securityEnvironment =  
                new DefaultSecurityEnvironmentImpl(callbackhandler);
        } catch (Exception e) {
            throw new XWSSecurityException(e);
        }
    }

    /**
     * 
     * @return digested form XWS-Security configuration.
     */
    public ApplicationSecurityConfiguration getSecurityConfiguration() {
        return configuration;
    }

    /**
     * 
     * @return  instance of SecurityEnvironment configured in the XWS-Security Configuration 
     * file.
     */
    public SecurityEnvironment getSecurityEnvironment() {
         return securityEnvironment;        
    }

    /**
     * 
     * @return 
     */
    public boolean isEmpty() {
        return configEmpty;
    }
}
