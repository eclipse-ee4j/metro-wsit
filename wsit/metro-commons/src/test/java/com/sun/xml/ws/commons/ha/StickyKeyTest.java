/*
 * Copyright (c) 2010, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.commons.ha;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import junit.framework.TestCase;

/**
 *
 * @author Marek Potociar (marek.potociar at sun.com)
 */
public class StickyKeyTest extends TestCase {

    public StickyKeyTest(String testName) {
        super(testName);
    }

    /**
     * Test of serialization method
     */
    public void testSerialization() throws Exception {
        StickyKey original = new StickyKey("abc", "def");

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final ObjectOutputStream oos = new ObjectOutputStream(baos);

        oos.writeObject(original);
        oos.close();

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);

        Object _replica = ois.readObject();
        ois.close();

        assertTrue("Unexpected replica class: " + _replica.getClass(), _replica instanceof StickyKey);
        StickyKey replica = (StickyKey) _replica;

        assertEquals("Original and replica are expected to be equal", original, replica);
        assertEquals(original.key, replica.key);
        assertEquals(original.getHashKey(), replica.getHashKey());
    }


    /**
     * Test of equals method, of class StickyKey.
     */
    public void testEquals() {
        assertTrue(new StickyKey("abc", "def").equals(new StickyKey("abc", "def")));
        assertTrue(new StickyKey("abc", "def").equals(new StickyKey("abc", "cba")));
        assertFalse(new StickyKey("cba", "def").equals(new StickyKey("abc", "def")));
    }

    /**
     * Test of hashCode method, of class StickyKey.
     */
    public void testHashCode() {
        assertEquals(new StickyKey("abc", "def").hashCode(), new StickyKey("abc", "def").hashCode());
        assertEquals(new StickyKey("abc", "def").hashCode(), new StickyKey("abc", "cba").hashCode());
        assertFalse(new StickyKey("cba", "def").hashCode() == new StickyKey("abc", "def").hashCode());
    }
}
