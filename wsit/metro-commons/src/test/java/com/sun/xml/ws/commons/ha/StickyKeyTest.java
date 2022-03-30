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
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Marek Potociar (marek.potociar at sun.com)
 */
public class StickyKeyTest {

    public StickyKeyTest() {
    }

    /**
     * Test of serialization method
     */
    @Test
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

        Assert.assertTrue("Unexpected replica class: " + _replica.getClass(), _replica instanceof StickyKey);
        StickyKey replica = (StickyKey) _replica;

        Assert.assertEquals("Original and replica are expected to be equal", original, replica);
        Assert.assertEquals(original.key, replica.key);
        Assert.assertEquals(original.getHashKey(), replica.getHashKey());
    }


    /**
     * Test of equals method, of class StickyKey.
     */
    @Test
    public void testEquals() {
        Assert.assertTrue(new StickyKey("abc", "def").equals(new StickyKey("abc", "def")));
        Assert.assertTrue(new StickyKey("abc", "def").equals(new StickyKey("abc", "cba")));
        Assert.assertFalse(new StickyKey("cba", "def").equals(new StickyKey("abc", "def")));
    }

    /**
     * Test of hashCode method, of class StickyKey.
     */
    @Test
    public void testHashCode() {
        Assert.assertEquals(new StickyKey("abc", "def").hashCode(), new StickyKey("abc", "def").hashCode());
        Assert.assertEquals(new StickyKey("abc", "def").hashCode(), new StickyKey("abc", "cba").hashCode());
        Assert.assertFalse(new StickyKey("cba", "def").hashCode() == new StickyKey("abc", "def").hashCode());
    }
}
