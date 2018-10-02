/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.at.policy;

import java.lang.reflect.Method;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionManagement;

public enum EjbTransactionType {

    NOT_SUPPORTED, 
    NEVER,
    MANDATORY,
    SUPPORTS,
    REQUIRES_NEW,
    REQUIRED,
    NOT_DEFINED;

    public static EjbTransactionType getDefaultFor(Class<?> seiClass) {
        EjbTransactionType result = EjbTransactionType.NOT_DEFINED;

        TransactionAttribute txnAttr = (TransactionAttribute) seiClass.getAnnotation(TransactionAttribute.class);
        if (txnAttr != null) {
            result = EjbTransactionType.valueOf(EjbTransactionType.class, txnAttr.value().name());
        }

        return result;
    }

    public EjbTransactionType getEffectiveType(Method method) {
        TransactionAttribute txnAttr = method.getAnnotation(TransactionAttribute.class);
        if (txnAttr != null) {
            return EjbTransactionType.valueOf(EjbTransactionType.class, txnAttr.value().name());
        }
        return this;
    }

    public static boolean isContainerManagedEJB(Class c) {
        TransactionManagement tm = (TransactionManagement) c.getAnnotation(TransactionManagement.class);
        if (tm != null) {
            switch (tm.value()) {
                case BEAN:
                    return false;
                case CONTAINER:
                default:
                    return true;
            }
        }

        // No TransactionManagement annotation. Default is CONTAINER for EJB.
        if (c.getAnnotation(Stateful.class) != null || c.getAnnotation(Stateless.class) != null) {
            //TODO: Are there any other EJB annotations?
            return true;
        } else {
            // servlet endpoint
            return false;
        }
    }
}
