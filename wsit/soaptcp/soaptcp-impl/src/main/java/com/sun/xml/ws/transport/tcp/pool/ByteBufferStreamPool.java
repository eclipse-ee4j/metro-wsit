/*
 * Copyright (c) 1997, 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.transport.tcp.pool;

import com.sun.xml.ws.util.Pool;

/**
 * @author Alexey Stashok
 */
public final class ByteBufferStreamPool<T extends LifeCycle> {

    private final Pool<T> pool;
    public ByteBufferStreamPool(final Class<T> memberClass) {
        pool = new Pool<T>() {
            protected T create() {
                T member = null;
                try {
                    member = ByteBufferStreamPool.this.create(memberClass);
                } catch (Exception e) {
                }

                return member;
            }
        };
    }

    private T create(final Class<T> memberClass) throws ReflectiveOperationException {
        return memberClass.getConstructor().newInstance();
    }

    public T take() {
        final T member = pool.take();
        member.activate();
        return member;
    }

    public void release(final T member) {
        member.passivate();
        pool.recycle(member);
    }
}
