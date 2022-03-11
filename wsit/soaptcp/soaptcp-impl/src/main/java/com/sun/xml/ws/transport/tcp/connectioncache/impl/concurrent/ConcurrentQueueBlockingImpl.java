/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.transport.tcp.connectioncache.impl.concurrent;

import com.sun.xml.ws.transport.tcp.connectioncache.spi.concurrent.ConcurrentQueue;

public class ConcurrentQueueBlockingImpl<V> implements ConcurrentQueue<V> {
    // This implementation of ConcurrentQueue uses a single lock, which must be
    // acquired to update the list.  Every operation on this class updates the
    // structure, so read/write locking is probably not useful.
    //
    // Trying to build a lock-free implementation runs into the usual problems:
    // we need to atomically update more than one location at a time in the structure.
    // Short of a transactional memory implementation, we would either need a complicated
    // implementation implementing recursive fixup, or something like the Ladan-Mozes and
    // Shavit algorithm (see "An Optimistic Approach to Lock-Free FIFO Queues"
    // at http://people.csail.mit.edu/edya/publications/publicationsAndPatents.htm)
    // that delays fixing up one direction in a double linked list.  However, that
    // algorithm does not consider general deletion, and I don't know whether that
    // capability can be easily added or not.
    // Any of these approaches are quite complicated, and so we won't go there yet.
    // As always, first make it work, then make it fast(er), but only if necessary.
    //
    // Structure: Head points to a node containing a null value, which is a special marker.
    // head.next is the first element, head.prev is the last.  The queue is empty if
    // head.next == head.prev == head.
    final Entry<V> head = new Entry<>(null) ;
    final Object lock = new Object() ;
    int count = 0 ;

    private final class Entry<V> {
    Entry<V> next = null ;
    Entry<V> prev = null ;
    private HandleImpl<V> handle ;

    Entry( V value ) {
        handle = new HandleImpl<>(this, value) ;
    }

    HandleImpl<V> handle() {
        return handle ;
    }
    }

    private final class HandleImpl<V> implements Handle<V> {
    private Entry<V> entry ;
    private final V value ;
    private boolean valid ;

    HandleImpl( Entry<V> entry, V value ) {
        this.entry = entry ;
        this.value = value ;
        this.valid = true ;
    }

    Entry<V> entry() {
        return entry ;
    }

    @Override
    public V value() {
        return value ;
    }

    /** Delete the element corresponding to this handle
     * from the queue.  Takes constant time.
     */
    @Override
    public boolean remove() {
        synchronized (lock) {
        if (!valid) {
            return false ;
        }

        valid = false ;

        entry.next.prev = entry.prev ;
        entry.prev.next = entry.next ;
        count-- ;
        }

        entry.prev = null ;
        entry.next = null ;
        entry.handle = null ;
        entry = null ;
        valid = false ;
        return true ;
    }
    }

    @Override
    public int size() {
    synchronized (lock) {
        return count ;
    }
    }

    /** Add a new element to the tail of the queue.
     * Returns a handle for the element in the queue.
     */
    @Override
    public Handle<V> offer(V arg ) {
    if (arg == null)
        throw new IllegalArgumentException( "Argument cannot be null" ) ;

    Entry<V> entry = new Entry<>(arg) ;

    synchronized (lock) {
        entry.next = head ;
        entry.prev = head.prev ;
        head.prev.next = entry ;
        head.prev = entry ;
        count++ ;
    }

    return entry.handle() ;
    }

    /** Return an element from the head of the queue.
     * The element is removed from the queue.
     */
    @Override
    public V poll() {
    Entry<V> first = null ;

    synchronized (lock) {
        first = head.next ;
        if (first == head)
        return null ;
        else {
        // assert that the following expression returns true!
        first.handle().remove() ;
        }
    }

    // Once first is removed from the queue, it is invisible to other threads,
    // so we don't need to synchronize here.
    first.next = null ;
    first.prev = null ;
        return first.handle().value();
    }
}

