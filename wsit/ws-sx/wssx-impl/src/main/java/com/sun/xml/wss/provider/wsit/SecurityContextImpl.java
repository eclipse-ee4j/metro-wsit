/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.provider.wsit;

import com.sun.xml.ws.security.spi.SecurityContext;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.security.auth.Subject;

/**
 *Provides a Default Implementation (tailored for GlassFish)
 * of the SecurityContext interface
 */
public class SecurityContextImpl implements SecurityContext {

    private static final String GF_SEC_CONTEXT="com.sun.enterprise.security.SecurityContext";
    private Class c = null;
    private Method getCurrent = null;
    private Method serverGenCred =null;
    private Method getSubject = null;
    private Constructor ctor = null;
    @SuppressWarnings("unchecked")
    public SecurityContextImpl() {
        try {
            Class[] params = new Class[]{};
            c = Class.forName(GF_SEC_CONTEXT, true, Thread.currentThread().getContextClassLoader());
            getCurrent = c.getMethod("getCurrent", params);
            serverGenCred = c.getMethod("didServerGenerateCredentials", params);
            getSubject = c.getMethod("getSubject", params);
            params = new Class[]{Subject.class};
            ctor = c.getConstructor(params);
        } catch (NoSuchMethodException ex) {
            //Logger.getLogger(SecurityContextImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            //Logger.getLogger(SecurityContextImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            //Logger.getLogger(SecurityContextImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    public Subject getSubject() {
        Subject s = null;        
        Object[] args = new Object[]{};
        try {
            
            if(getCurrent == null || serverGenCred == null ||getSubject == null) {
                return null;
            }
            
            Object currentSC = getCurrent.invoke(null, args);
            if (currentSC == null) {
                return null;
            }
            Boolean didServerGenerateCredentials = (Boolean)serverGenCred.invoke(currentSC, args);
            if (!didServerGenerateCredentials.booleanValue()) {
                s = (Subject)getSubject.invoke(currentSC, args);
            }
            return s;
          
        } catch (IllegalAccessException ex) {
            return null;
        } catch (IllegalArgumentException ex) {
            return null;
        } catch (InvocationTargetException ex) {
            return null;
        } catch (SecurityException ex) {
            return null;
        }
    }
    
    public void setSubject(Subject subject) {
        //SecurityContext sC = new SecurityContext(s);
	//SecurityContext.setCurrent(sC);
        Class[] params = null;
        Object[] args = null;
        try {
            args = new Object[] {subject};
            if (ctor == null) {
                //TODO: log warning here
                return;
            }
            Object secContext = ctor.newInstance(args);
            params = new Class[]{secContext.getClass()};
            @SuppressWarnings("unchecked")
            Method setCurrent = c.getMethod("setCurrent", params);
            args = new Object[]{secContext};
            if (setCurrent == null) {
                //TODO: log warning here
                return;
            }
            setCurrent.invoke(null, args);
        } catch (InstantiationException ex) {
            //ignore
        } catch (IllegalAccessException ex) {
            //ignore
        } catch (IllegalArgumentException ex) {
            //ignore
        } catch (InvocationTargetException ex) {
            //ignore
        } catch (NoSuchMethodException ex) {
            //ignore
        } catch (SecurityException ex) {
            //ignore
        }
    }


}
