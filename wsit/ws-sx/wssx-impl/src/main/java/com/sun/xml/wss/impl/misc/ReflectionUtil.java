/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * ReflectionUtil.java
 *
 * Created on August 13, 2007, 2:33 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.sun.xml.wss.impl.misc;

import com.sun.xml.wss.impl.XWSSecurityRuntimeException;
import com.sun.xml.wss.logging.LogDomainConstants;
import com.sun.xml.wss.logging.LogStringsMessages;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

 /**
  * Reflection utilities wrapper
  */
public  class ReflectionUtil {
    private static final Logger log =
            Logger.getLogger(
            LogDomainConstants.WSS_API_DOMAIN,
            LogDomainConstants.WSS_API_DOMAIN_BUNDLE);
    
    /**
     * Reflectively invokes specified method on the specified target
     */
    public static <T> T invoke(final Object target, final String methodName,
            final Class<T> resultClass, final Object... parameters) throws XWSSecurityRuntimeException {
        Class[] parameterTypes;
        if (parameters != null && parameters.length > 0) {
            parameterTypes = new Class[parameters.length];
            int i = 0;
            for (Object parameter : parameters) {
                parameterTypes[i++] = parameter.getClass();
            }
        } else {
            parameterTypes = null;
        }
        
        return invoke(target, methodName, resultClass, parameters, parameterTypes);
    }
    
    /**
     * Reflectively invokes specified method on the specified target
     */
    public static <T> T invoke(final Object target, final String methodName, final Class<T> resultClass,
            final Object[] parameters, final Class[] parameterTypes) throws XWSSecurityRuntimeException {
        try {
            final Method method = target.getClass().getMethod(methodName, parameterTypes);
            final Object result = method.invoke(target, parameters);
            
            return resultClass.cast(result);
        } catch (IllegalArgumentException e) {
            log.log(Level.SEVERE, LogStringsMessages.WSS_0810_METHOD_INVOCATION_FAILED() , e);
            throw e;
        } catch (InvocationTargetException e) {
            log.log(Level.SEVERE, LogStringsMessages.WSS_0810_METHOD_INVOCATION_FAILED() , e);
            throw new XWSSecurityRuntimeException(e);
        } catch (IllegalAccessException e) {
            log.log(Level.SEVERE, LogStringsMessages.WSS_0810_METHOD_INVOCATION_FAILED() , e);
            throw new XWSSecurityRuntimeException(e);
        } catch (SecurityException e) {
            log.log(Level.SEVERE, LogStringsMessages.WSS_0810_METHOD_INVOCATION_FAILED() , e);
            throw e;
        } catch (NoSuchMethodException e) {
            log.log(Level.SEVERE, LogStringsMessages.WSS_0810_METHOD_INVOCATION_FAILED() , e);
            throw new XWSSecurityRuntimeException(e);
        }
    }
    
    
}
