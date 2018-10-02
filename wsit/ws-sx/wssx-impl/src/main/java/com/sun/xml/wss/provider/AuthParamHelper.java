/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.provider;

import com.sun.enterprise.security.jauth.AuthParam;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.soap.SOAPMessage;

/**
 *
 * @author Kumar
 */
class AuthParamHelper {

    @SuppressWarnings("unchecked")
    static SOAPMessage getRequest(AuthParam param) {
        try {
            Class clz = param.getClass();
            Method meth = clz.getMethod("getRequest", (Class[]) null);
            if (meth != null) {
                Object obj = meth.invoke(param, (Object[]) null);
                if (obj instanceof SOAPMessage) {
                    return (SOAPMessage)obj;
                }
            }

        } catch (NoSuchMethodException ex) {
        } catch (SecurityException ex) {
        } catch (IllegalAccessException ex) {
        } catch (IllegalArgumentException ex) {
        } catch (InvocationTargetException ex) {
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    static SOAPMessage getResponse(AuthParam param) {
         try {
            Class clz = param.getClass();
            Method meth = clz.getMethod("getResponse", (Class[]) null);
            if (meth != null) {
                Object obj = meth.invoke(param, (Object[]) null);
                if (obj instanceof SOAPMessage) {
                    return (SOAPMessage)obj;
                }
            }

        } catch (NoSuchMethodException ex) {
        } catch (SecurityException ex) {
        } catch (IllegalAccessException ex) {
        } catch (IllegalArgumentException ex) {
        } catch (InvocationTargetException ex) {
        }
        return null;
    }
}
