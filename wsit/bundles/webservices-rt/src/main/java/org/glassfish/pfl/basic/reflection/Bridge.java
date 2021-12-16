/*
 * Copyright (c) 2019, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

//keep in sync with https://raw.githubusercontent.com/eclipse-ee4j/orb-gmbal-pfl/master/pfl-basic/src/main/java11/org/glassfish/pfl/basic/reflection/Bridge.java
//the source bundle of org.glassfish.pfl:pfl-basic does not contain this version foof the class for JDK 11+ (as of 4.1.2)
package org.glassfish.pfl.basic.reflection;

import java.io.OptionalDataException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.Permission;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.Optional;
import java.util.stream.Stream;
import sun.reflect.ReflectionFactory;

/**
 * This class provides the methods for fundamental JVM operations
 * needed in the ORB that are not part of the public Java API.  This includes:
 * <ul>
 * <li>throwException, which can throw undeclared checked exceptions.
 * This is needed to handle throwing arbitrary exceptions across a standardized OMG interface that (incorrectly) does not specify appropriate exceptions.</li>
 * <li>putXXX/getXXX methods that allow unchecked access to fields of objects.
 * This is used for setting uninitialzed non-static final fields (which is
 * impossible with reflection) and for speed.</li>
 * <li>objectFieldOffset to obtain the field offsets for use in the putXXX/getXXX methods</li>
 * <li>newConstructorForSerialization to get the special constructor required for a
 * Serializable class</li>
 * <li>latestUserDefinedLoader to get the latest user defined class loader from
 * the call stack as required by the RMI-IIOP specification (really from the
 * JDK 1.1 days)</li>
 * </ul>
 * The code that calls Bridge.get() must have the following Permissions:
 * <ul>
 * <li>RuntimePermission "reflectionFactoryAccess"</li>
 * <li>BridgePermission "getBridge"</li>
 * <li>ReflectPermission "suppressAccessChecks"</li>
 * </ul>
 * <p>
 * All of these permissions are required to obtain and correctly initialize
 * the instance of Bridge.  No security checks are performed on calls
 * made to Bridge instance methods, so access to the Bridge instance
 * must be protected.
 * <p>
 * This class is a singleton (per ClassLoader of course).  Access to the
 * instance is obtained through the Bridge.get() method.
 */
public final class Bridge extends BridgeBase {
    private static final Permission GET_BRIDGE_PERMISSION = new BridgePermission("getBridge");
    private static Bridge bridge = null;

    private final ReflectionFactory reflectionFactory;



    private Bridge() {
        reflectionFactory = ReflectionFactory.getReflectionFactory();
    }

    /**
     * Fetch the Bridge singleton.  This requires the following
     * permissions:
     * <ul>
     * <li>RuntimePermission "reflectionFactoryAccess"</li>
     * <li>BridgePermission "getBridge"</li>
     * <li>ReflectPermission "suppressAccessChecks"</li>
     * </ul>
     *
     * @return The singleton instance of the Bridge class
     * @throws SecurityException if the caller does not have the
     *                           required permissions and the caller has a non-null security manager.
     */
    public static synchronized Bridge get() {
        SecurityManager sman = System.getSecurityManager();
        if (sman != null) {
            sman.checkPermission(GET_BRIDGE_PERMISSION);
        }

        if (bridge == null) {
            bridge = new Bridge();
        }

        return bridge;
    }


    // A horrible hack, allowing JDK 11 and later to continue to use this class, Properly speaking,
    // code should be rewritten to use the replacement method, but this will work until the JDK
    // actually bans illegal runtime access, rather than just warning about it.
    @SuppressWarnings("deprecation")
    public Class<?> defineClass(String className, byte[] classBytes, ClassLoader classLoader, ProtectionDomain protectionDomain) {
        try {
            return (Class) getDefineClassMethod().invoke(classLoader, className, classBytes, 0, classBytes.length, null);
        } catch (InvocationTargetException | IllegalAccessException exc) {
            throw new Error("Could not access ClassLoader.defineClass()", exc);
        }
    }

    private static Method defineClassMethod;

    private synchronized static Method getDefineClassMethod() {
        if (defineClassMethod != null) return defineClassMethod;

        defineClassMethod = AccessController.doPrivileged(
                (PrivilegedAction<Method>) () -> {
                    try {
                        Class<?> cl = Class.forName("java.lang.ClassLoader");
                        Method defineClass = cl.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class, ProtectionDomain.class);
                        defineClass.setAccessible(true);
                        return defineClass;
                    } catch (NoSuchMethodException | ClassNotFoundException exc) {
                        throw new Error("Could not access ClassLoader.defineClass()", exc);
                    }
                }
        );
        return defineClassMethod;
    }

    @Override
    public Class<?> defineClass(Class<?> anchorClass, String className, byte[] classBytes) {
        try {
            MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(anchorClass, MethodHandles.lookup())
                    .dropLookupMode(MethodHandles.Lookup.PRIVATE);
            return lookup.defineClass(classBytes);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to define class ", e);
        }
    }

    private final StackWalker stackWalker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

    // New implementation for Java 9, supplied by Alan Bateman
    @Override
    public final ClassLoader getLatestUserDefinedLoader() {
        // requires getClassLoader permission => needs doPrivileged.
        PrivilegedAction<ClassLoader> pa = () ->
                stackWalker.walk(this::getLatestUserDefinedLoaderFrame)
                        .map(sf -> sf.getDeclaringClass().getClassLoader())
                        .orElseGet(ClassLoader::getPlatformClassLoader);
        return AccessController.doPrivileged(pa);
    }

    private Optional<StackWalker.StackFrame> getLatestUserDefinedLoaderFrame(Stream<StackWalker.StackFrame> stream) {
        return stream.filter(this::isUserLoader).findFirst();
    }

    private boolean isUserLoader(StackWalker.StackFrame sf) {
        ClassLoader cl = sf.getDeclaringClass().getClassLoader();
        if (cl == null) return false;

        ClassLoader platformClassLoader = ClassLoader.getPlatformClassLoader();
        while (platformClassLoader != null && cl != platformClassLoader) platformClassLoader = platformClassLoader.getParent();
        return cl != platformClassLoader;
    }

    @Override
    @SuppressWarnings("unchecked")
    public final <T> Constructor<T> newConstructorForExternalization(Class<T> cl) {
        return (Constructor<T>) reflectionFactory.newConstructorForExternalization( cl );
    }

    @Override
    @SuppressWarnings("unchecked")
    public final <T> Constructor<T> newConstructorForSerialization(Class<T> aClass, Constructor<?> cons) {
        return (Constructor<T>) reflectionFactory.newConstructorForSerialization(aClass, cons);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Constructor<T> newConstructorForSerialization(Class<T> aClass) {
        return (Constructor<T>) reflectionFactory.newConstructorForSerialization( aClass );
    }

    /**
     * Returns true if the given class defines a static initializer method,
     * false otherwise.
     */
    @Override
    public final boolean hasStaticInitializerForSerialization(Class<?> cl) {
        return reflectionFactory.hasStaticInitializerForSerialization(cl);
    }

    @Override
    public final MethodHandle writeObjectForSerialization(Class<?> cl) {
        return reflectionFactory.writeObjectForSerialization(cl);
    }

    @Override
    public final MethodHandle readObjectForSerialization(Class<?> cl) {
        return reflectionFactory.readObjectForSerialization(cl);
    }

    public final MethodHandle readObjectNoDataForSerialization(Class<?> cl) {
        return reflectionFactory.readObjectNoDataForSerialization(cl);
    }

    @Override
    public final MethodHandle readResolveForSerialization(Class<?> cl) {
        return reflectionFactory.readResolveForSerialization(cl);
    }

    @Override
    public final MethodHandle writeReplaceForSerialization(Class<?> cl) {
        return reflectionFactory.writeReplaceForSerialization(cl);
    }

    @Override
    public final OptionalDataException newOptionalDataExceptionForSerialization(boolean bool) {
        return reflectionFactory.newOptionalDataExceptionForSerialization(bool);
    }

    @Override
    public Field toAccessibleField(Field field, Class callingClass) {
        return isClassOpenToModule(field.getDeclaringClass(), callingClass.getModule())
                ? super.toAccessibleField(field, callingClass)
                : null;
    }

    private boolean isClassOpenToModule(Class<?> candidateClass, Module callingModule) {
        return callingModule.isNamed()
                ? candidateClass.getModule().isOpen(candidateClass.getPackageName(), callingModule)
                : candidateClass.getModule().isOpen(candidateClass.getPackageName());
    }

    @Override
    public Method toAccessibleMethod(Method method, Class callingClass) {
        return isClassOpenToModule(method.getDeclaringClass(), callingClass.getModule())
                ? super.toAccessibleMethod(method, callingClass)
                : null;
    }
}