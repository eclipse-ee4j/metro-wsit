/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.xmlfilter;

import com.sun.istack.logging.Logger;
import com.sun.xml.ws.xmlfilter.localization.LocalizationMessages;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * The class provides an implementation of an {@link InvocationHandler} interface
 * that handles requests of {@link XMLStreamWriter} proxy instances.
 *<p>
 * This {@link InvocationHandler} implementation adds additional feature or enhancement
 * to the underlying {@link XMLStreamWriter} instance. The new enhancement or feature is
 * defined by an {@link InvocationProcessor} implementation.
 * <p>
 * The class also contains a static factory method for creating such 'enhanced'
 * {@link XMLStreamWriter} proxies.
 *
 * @author Marek Potociar (marek.potociar at sun.com)
 */
public final class EnhancedXmlStreamWriterProxy implements InvocationHandler {
    private static final Logger LOGGER = Logger.getLogger(EnhancedXmlStreamWriterProxy.class);

    private static final Class<?>[] PROXIED_INTERFACES = new Class<?>[] {XMLStreamWriter.class};

    // preloaded Method objects for the methods in java.lang.Object
    private static final Method hashCodeMethod;
    private static final Method equalsMethod;
    private static final Method toStringMethod;
    static {
        try {
            hashCodeMethod = Object.class.getMethod("hashCode");
            equalsMethod = Object.class.getMethod("equals", Object.class);
            toStringMethod = Object.class.getMethod("toString");
        } catch (NoSuchMethodException e) {
            throw LOGGER.logSevereException(new NoSuchMethodError(e.getMessage()), e);
        }
    }

    // invocation procesor that processes
    private final InvocationProcessor invocationProcessor;

    /**
     * Creates a wrapper {@link XMLStreamWriter} proxy that adds enhanced feature
     * to the {@code writer} instance.
     *
     * @param writer {@link XMLStreamWriter} instance that should be enhanced with
     *        content filtering feature.
     * @param processorFactory {@link InvocationProcessorFactory} instance that
     *        is used to create {@link InvocationProcessor} which implements new enhancement
     *        or feature.
     *
     * @return new enhanced {XMLStreamWriter} (proxy) instance
     * @throws XMLStreamException in case of any problems with creating the proxy
     */
    public static XMLStreamWriter createProxy(final XMLStreamWriter writer, final InvocationProcessorFactory processorFactory) throws XMLStreamException {
        LOGGER.entering();

        XMLStreamWriter proxy = null;
        try {
            proxy = (XMLStreamWriter) Proxy.newProxyInstance(
                    writer.getClass().getClassLoader(),
                    PROXIED_INTERFACES,
                    new EnhancedXmlStreamWriterProxy(writer, processorFactory));

            return proxy;
        } finally {
            LOGGER.exiting(proxy);
        }
    }

    private EnhancedXmlStreamWriterProxy(final XMLStreamWriter writer, final InvocationProcessorFactory processorFactory) throws XMLStreamException {
        this.invocationProcessor = processorFactory.createInvocationProcessor(writer);
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) {
        if (LOGGER.isMethodCallLoggable()) {
            LOGGER.entering(method, args);
        }

        Object result = null;
        try {
            final Class declaringClass = method.getDeclaringClass();
            if (declaringClass == Object.class) {
                return handleObjectMethodCall(proxy, method, args);
            } else {
                final Invocation invocation = Invocation.createInvocation(method, args);
                result = invocationProcessor.process(invocation);
                return result;
            }
        } finally {
            LOGGER.exiting(result);
        }
    }

    private Object handleObjectMethodCall(final Object proxy, final Method method, final Object[] args) {
        if (method.equals(hashCodeMethod)) {
            return System.identityHashCode(proxy);
        } else if (method.equals(equalsMethod)) {
            return (proxy == args[0] ? Boolean.TRUE : Boolean.FALSE);
        } else if (method.equals(toStringMethod)) {
            return proxy.getClass().getName() + '@' + Integer.toHexString(proxy.hashCode());
        } else {
            throw LOGGER.logSevereException(new InternalError(LocalizationMessages.XMLF_5002_UNEXPECTED_OBJECT_METHOD(method)));
        }
    }
}
