/*
 * Copyright (c) 1997, 2025 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.at.common;

import com.sun.istack.logging.Logger;
import com.sun.xml.ws.tx.at.localization.LocalizationMessages;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import jakarta.resource.spi.XATerminator;
import jakarta.transaction.TransactionManager;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

/**
 *  Access Transaction Inflow Contract from Java Connector 1.5 API.
 *  Assumption is the underlying TransactionManager is implementing this
 *  interface.
 *
 *  Separate this from TransactionManagerImpl since this provides mostly service side assistance.
 *  Assists in supporting application client and standalone client to separate from more commonly
 *  used methods in TransactionManagerImpl.
 */
public class TransactionImportManager implements TransactionImportWrapper {

    private static final class MethodInfo<T> {

        final String methodName;
        final Class<?>[] parameterTypes;
        final Class<?> returnType;
        final Class<T> returnTypeCaster;
        //
        Method method;

        public MethodInfo(String methodName, Class<?>[] parameterTypes, Class<T> returnType) {
            this(methodName, parameterTypes, returnType, returnType);
        }

        public MethodInfo(String methodName, Class<?>[] parameterTypes, Class<?> returnType, Class<T> returnTypeCaster) {
            this.methodName = methodName;
            this.parameterTypes = parameterTypes;
            this.returnType = returnType;
            this.returnTypeCaster = returnTypeCaster;
        }

        public boolean isCompatibleWith(Method m) {
            if (!methodName.equals(m.getName())) {
                return false;
            }

            if (!Modifier.isPublic(m.getModifiers())) {
                return false;
            }

            if (!returnType.isAssignableFrom(m.getReturnType())) {
                return false;
            }

            Class<?>[] otherParamTypes = m.getParameterTypes();
            if (parameterTypes.length != otherParamTypes.length) {
                return false;
            }
            for (int i = 0; i < parameterTypes.length; i++) {
                if (!parameterTypes[i].isAssignableFrom(otherParamTypes[i])) {
                    return false;
                }
            }

            return true;
        }

        public T invoke(TransactionManager tmInstance, Object... args) {
            try {
                Object result = method.invoke(tmInstance, args);
                return returnTypeCaster.cast(result);
            } catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
    private static final Logger LOGGER = Logger.getLogger(TransactionImportManager.class);
    private static TransactionImportManager INSTANCE;

    public static TransactionImportManager getInstance() {
        if(INSTANCE==null) INSTANCE = new TransactionImportManager();
        return INSTANCE;

    }
    //
    static private TransactionManager javaeeTM;
    private final MethodInfo<?> recreate;
    private final MethodInfo<?> release;
    private final MethodInfo<XATerminator> getXATerminator;
    private final MethodInfo<Integer> getTransactionRemainingTimeout;
    private final MethodInfo<String> getTxLogLocation;
    static private MethodInfo<?> registerRecoveryResourceHandler;

    private TransactionImportManager() {
        this(TransactionManagerImpl.getInstance().getTransactionManager());
    }

    private TransactionImportManager(TransactionManager tm) {
        javaeeTM = tm;

        this.recreate = new MethodInfo<>(
                "recreate",
                new Class<?>[]{Xid.class, long.class},
                void.class);
        this.release = new MethodInfo<>(
                "release",
                new Class<?>[]{Xid.class},
                void.class);
        this.getXATerminator = new MethodInfo<>(
                "getXATerminator",
                new Class<?>[]{},
                XATerminator.class);
        this.getTransactionRemainingTimeout = new MethodInfo<>(
                "getTransactionRemainingTimeout",
                new Class<?>[]{},
                int.class,
                Integer.class);
        this.getTxLogLocation = new MethodInfo<>(
                "getTxLogLocation",
                new Class<?>[]{},
                String.class);
        registerRecoveryResourceHandler = new MethodInfo<>(
                "registerRecoveryResourceHandler",
                new Class<?>[]{XAResource.class},
                void.class);
        MethodInfo<?>[] requiredMethods = new MethodInfo<?>[]{
            recreate,
            release,
            getXATerminator,
            getTransactionRemainingTimeout,
            getTxLogLocation,
            registerRecoveryResourceHandler
        };

        int remainingMethodsToFind = requiredMethods.length;

        if (javaeeTM != null) {
            for (Method m : javaeeTM.getClass().getDeclaredMethods()) {
                for (MethodInfo mi : requiredMethods) {
                    if (mi.isCompatibleWith(m)) {
                        mi.method = m;
                        remainingMethodsToFind--;
                    }
                }

                if (remainingMethodsToFind == 0) {
                    break;
                }
            }
        }

        if (remainingMethodsToFind != 0) {
            StringBuilder sb =
                    new StringBuilder("Missing required extension methods detected on '" + TransactionManager.class.getName() + "' implementation '" + javaeeTM.getClass().getName() + "':\n");
            for (MethodInfo mi : requiredMethods) {
                if (mi.method == null) {
                    sb.append(mi.methodName).append("\n");
                }
            }
            LOGGER.info(sb.toString());
        }
    }

    /**
     * ${@inheritDoc }
     */
    @Override
    public void recreate(final Xid xid, final long timeout) {
        recreate.invoke(javaeeTM, xid, timeout);
    }

    /**
     * ${@inheritDoc }
     */
    @Override
    public void release(final Xid xid) {
        release.invoke(javaeeTM, xid);
    }

    /**
     * ${@inheritDoc }
     */
    @Override
    public XATerminator getXATerminator() {
        return getXATerminator.invoke(javaeeTM);
    }

    /**
     * ${@inheritDoc }
     */
    @Override
    public int getTransactionRemainingTimeout() {
        final String METHOD = "getTransactionRemainingTimeout";
        int result = 0;
        try {
            result = getTransactionRemainingTimeout.invoke(javaeeTM);
        } catch (IllegalStateException ise) {
            LOGGER.finest(METHOD + " " + LocalizationMessages.WSAT_4617_TXN_MGR_LOOKUP_TXN_TIMEOUT(), ise);
        }
        return result;
    }

    public String getTxLogLocation() {
        return getTxLogLocation.invoke(javaeeTM);
    }

    public void registerRecoveryResourceHandler(XAResource xaResource) {
        registerRecoveryResourceHandler.invoke(javaeeTM, xaResource);
    }
}
