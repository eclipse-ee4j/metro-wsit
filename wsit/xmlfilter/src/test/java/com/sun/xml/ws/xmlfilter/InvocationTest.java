/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * InvocationTest.java
 * JUnit based test
 *
 * Created on March 2, 2007, 11:44 AM
 */

package com.sun.xml.ws.xmlfilter;

import javax.xml.stream.XMLStreamWriter;
import java.lang.reflect.Method;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Marek Potociar (marek.potociar at sun.com)
 */
public class InvocationTest {

    public InvocationTest() {
    }

    /**
     * Test of createInvocation method, of class com.sun.xml.ws.policy.jaxws.xmlstreamwriter.Invocation.
     */
    @Test
    public void testCreateInvocation() throws Exception {
        Method method = XMLStreamWriter.class.getMethod(XmlStreamWriterMethodType.WRITE_START_ELEMENT.getMethodName(), String.class);
        Object[] args = new Object[] {"test"};
        Invocation result = Invocation.createInvocation(method, args);

        Assert.assertNotNull(result);
    }

    /**
     * Test of getMethodName method, of class com.sun.xml.ws.policy.jaxws.xmlstreamwriter.Invocation.
     */
    @Test
    public void testGetMethodName() throws Exception {
        String methodName;
        Method method;
        Object[] args;
        Invocation result;

        methodName = XmlStreamWriterMethodType.WRITE_START_ELEMENT.getMethodName();
        method = XMLStreamWriter.class.getMethod(methodName, String.class);
        args = new Object[] {"test"};
        result = Invocation.createInvocation(method, args);
        Assert.assertEquals(methodName, result.getMethodName());
        method = XMLStreamWriter.class.getMethod(methodName, String.class, String.class);
        args = new Object[] {"test", "test"};
        result = Invocation.createInvocation(method, args);
        Assert.assertEquals(methodName, result.getMethodName());
        method = XMLStreamWriter.class.getMethod(methodName, String.class, String.class, String.class);
        args = new Object[] {"test", "test", "test"};
        result = Invocation.createInvocation(method, args);
        Assert.assertEquals(methodName, result.getMethodName());

        methodName = XmlStreamWriterMethodType.WRITE_END_ELEMENT.getMethodName();
        method = XMLStreamWriter.class.getMethod(methodName);
        args = null;
        result = Invocation.createInvocation(method, args);
        Assert.assertEquals(methodName, result.getMethodName());

        methodName = XmlStreamWriterMethodType.WRITE_ATTRIBUTE.getMethodName();
        method = XMLStreamWriter.class.getMethod(methodName, String.class, String.class);
        args = new Object[] {"test", "test"};
        result = Invocation.createInvocation(method, args);
        Assert.assertEquals(methodName, result.getMethodName());
        method = XMLStreamWriter.class.getMethod(methodName, String.class, String.class, String.class);
        args = new Object[] {"test", "test", "test"};
        result = Invocation.createInvocation(method, args);
        Assert.assertEquals(methodName, result.getMethodName());
        method = XMLStreamWriter.class.getMethod(methodName, String.class, String.class, String.class, String.class);
        args = new Object[] {"test", "test", "test", "test"};
        result = Invocation.createInvocation(method, args);
        Assert.assertEquals(methodName, result.getMethodName());

        methodName = XmlStreamWriterMethodType.WRITE_CHARACTERS.getMethodName();
        method = XMLStreamWriter.class.getMethod(methodName, String.class);
        args = new Object[] {"test"};
        result = Invocation.createInvocation(method, args);
        Assert.assertEquals(methodName, result.getMethodName());
        method = XMLStreamWriter.class.getMethod(methodName, char[].class, int.class, int.class);
        args = new Object[] {new char[] {'t', 'e', 's', 't'}, 0, 4};
        result = Invocation.createInvocation(method, args);
        Assert.assertEquals(methodName, result.getMethodName());

        methodName = XmlStreamWriterMethodType.FLUSH.getMethodName();
        method = XMLStreamWriter.class.getMethod(methodName);
        args = null;
        result = Invocation.createInvocation(method, args);
        Assert.assertEquals(methodName, result.getMethodName());

    }

    /**
     * Test of getMethodType method, of class com.sun.xml.ws.policy.jaxws.xmlstreamwriter.Invocation.
     */
    @Test
    public void testGetMethodType() throws Exception {
        String methodName;
        XmlStreamWriterMethodType methodType;
        Method method;
        Object[] args;
        Invocation result;

        methodName = XmlStreamWriterMethodType.WRITE_START_ELEMENT.getMethodName();
        methodType = XmlStreamWriterMethodType.WRITE_START_ELEMENT;
        method = XMLStreamWriter.class.getMethod(methodName, String.class);
        args = new Object[] {"test"};
        result = Invocation.createInvocation(method, args);
        Assert.assertEquals(methodType, result.getMethodType());
        method = XMLStreamWriter.class.getMethod(methodName, String.class, String.class);
        args = new Object[] {"test", "test"};
        result = Invocation.createInvocation(method, args);
        Assert.assertEquals(methodType, result.getMethodType());
        method = XMLStreamWriter.class.getMethod(methodName, String.class, String.class, String.class);
        args = new Object[] {"test", "test", "test"};
        result = Invocation.createInvocation(method, args);
        Assert.assertEquals(methodType, result.getMethodType());

        methodName = XmlStreamWriterMethodType.WRITE_END_ELEMENT.getMethodName();
        methodType = XmlStreamWriterMethodType.WRITE_END_ELEMENT;
        method = XMLStreamWriter.class.getMethod(methodName);
        args = null;
        result = Invocation.createInvocation(method, args);
        Assert.assertEquals(methodType, result.getMethodType());

        methodName = XmlStreamWriterMethodType.WRITE_ATTRIBUTE.getMethodName();
        methodType = XmlStreamWriterMethodType.WRITE_ATTRIBUTE;
        method = XMLStreamWriter.class.getMethod(methodName, String.class, String.class);
        args = new Object[] {"test", "test"};
        result = Invocation.createInvocation(method, args);
        Assert.assertEquals(methodType, result.getMethodType());
        method = XMLStreamWriter.class.getMethod(methodName, String.class, String.class, String.class);
        args = new Object[] {"test", "test", "test"};
        result = Invocation.createInvocation(method, args);
        Assert.assertEquals(methodType, result.getMethodType());
        method = XMLStreamWriter.class.getMethod(methodName, String.class, String.class, String.class, String.class);
        args = new Object[] {"test", "test", "test", "test"};
        result = Invocation.createInvocation(method, args);
        Assert.assertEquals(methodType, result.getMethodType());

        methodName = XmlStreamWriterMethodType.WRITE_CHARACTERS.getMethodName();
        methodType = XmlStreamWriterMethodType.WRITE_CHARACTERS;
        method = XMLStreamWriter.class.getMethod(methodName, String.class);
        args = new Object[] {"test"};
        result = Invocation.createInvocation(method, args);
        Assert.assertEquals(methodType, result.getMethodType());
        method = XMLStreamWriter.class.getMethod(methodName, char[].class, int.class, int.class);
        args = new Object[] {new char[] {'t', 'e', 's', 't'}, 0, 4};
        result = Invocation.createInvocation(method, args);
        Assert.assertEquals(methodType, result.getMethodType());

        methodName = "flush";
        methodType = XmlStreamWriterMethodType.FLUSH;
        method = XMLStreamWriter.class.getMethod(methodName);
        args = null;
        result = Invocation.createInvocation(method, args);
        Assert.assertEquals(methodType, result.getMethodType());
    }

    /**
     * Test of getArgument method, of class com.sun.xml.ws.policy.jaxws.xmlstreamwriter.Invocation.
     */
    @Test
    public void testGetArgument() throws Exception {
        String methodName;
        Method method;
        Object[] args;
        Invocation result;

        methodName = XmlStreamWriterMethodType.WRITE_START_ELEMENT.getMethodName();
        method = XMLStreamWriter.class.getMethod(methodName, String.class, String.class, String.class);
        args = new Object[] {"aaa", "bbb", "ccc"};
        result = Invocation.createInvocation(method, args);
        for (int i = 0; i < args.length; i++) {
            Assert.assertEquals(args[i], result.getArgument(i));
        }

        methodName = "flush";
        method = XMLStreamWriter.class.getMethod(methodName);
        args = null;
        result = Invocation.createInvocation(method, args);
        try {
            result.getArgument(1);
            Assert.fail("ArrayIndexOutOfBoundsException expected.");
        } catch (ArrayIndexOutOfBoundsException e) {
            // ok
        }
    }

    /**
     * Test of getArgumentsLength method, of class com.sun.xml.ws.policy.jaxws.xmlstreamwriter.Invocation.
     */
    @Test
    public void testGetArgumentsLength() throws Exception {
        String methodName;
        Method method;
        Object[] args;
        Invocation result;

        methodName = XmlStreamWriterMethodType.WRITE_START_ELEMENT.getMethodName();
        method = XMLStreamWriter.class.getMethod(methodName, String.class, String.class, String.class);
        args = new Object[] {"aaa", "bbb", "ccc"};
        result = Invocation.createInvocation(method, args);
        Assert.assertEquals(args.length, result.getArgumentsCount());

        methodName = "flush";
        method = XMLStreamWriter.class.getMethod(methodName);
        args = null;
        result = Invocation.createInvocation(method, args);
        Assert.assertEquals(0, result.getArgumentsCount());
    }

    /**
     * Tests proper behaviour of defence copying of arguments of writeCharacters() method
     */
    @Test
    public void testDefenceCopyInWriteCharactersMethod() throws Exception {
        String methodName = XmlStreamWriterMethodType.WRITE_CHARACTERS.getMethodName();
        Method method = XMLStreamWriter.class.getMethod(methodName, char[].class, int.class, int.class);
        Object[] args;
        Invocation result;
        char[] originalChars, expectedChars;
        int originalStart, originalLength, expectedStart, expectedLength;

        originalChars = new char[] {'x', 'y', 't', 'e', 's', 't', 'z'};
        originalStart = 2;
        originalLength = 4;

        expectedChars = new char[] {'t', 'e', 's', 't'};
        expectedStart = 0;
        expectedLength = 4;

        args = new Object[] {originalChars, originalStart, originalLength};
        result = Invocation.createInvocation(method, args);
        // modifying the original char array to test defence copy
        originalChars[2] = 'w';

        Assert.assertEquals(expectedStart, result.getArgument(1));
        Assert.assertEquals(expectedLength, result.getArgument(2));
        char[] resultChars = (char[]) result.getArgument(0);
        for (int i = expectedStart; i < expectedLength; i++) {
            Assert.assertEquals(expectedChars[i], resultChars[i]);
        }

    }
}
