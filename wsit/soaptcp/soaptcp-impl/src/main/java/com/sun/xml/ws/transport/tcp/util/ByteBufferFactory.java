/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.transport.tcp.util;


import java.nio.ByteBuffer;


/**
 * Class was copied from GlassFish Grizzly sources to be available
 * also for client side and don't require GlassFish to be installed
 *
 * Factory class used to create views of a <code>ByteBuffer</code>. 
 * The ByteBuffer can by direct or not.
 *
 * @author Jean-Francois Arcand
 */
public final class ByteBufferFactory{

    
    /**
     * The default capacity of the default view of a <code>ByteBuffer</code>
     */ 
    public static int defaultCapacity = 9000;
    
    
    /**
     * The default capacity of the <code>ByteBuffer</code> from which views
     * will be created.
     */
    public static int capacity = 4000000; 
    
    
    /**
     * The <code>ByteBuffer</code> used to create views.
     */
    private static ByteBuffer byteBuffer;
            
    
    /**
     * Private constructor.
     */
    private ByteBufferFactory(){
    }
    
    
    /**
     * Return a direct <code>ByteBuffer</code> view
     * @param size the Size of the <code>ByteBuffer</code>
     */ 
    public synchronized static ByteBuffer allocateView(final int size, final boolean direct){
        if (byteBuffer == null || 
               (byteBuffer.capacity() - byteBuffer.limit() < size)){
            if ( direct )
                byteBuffer = ByteBuffer.allocateDirect(capacity); 
            else
                byteBuffer = ByteBuffer.allocate(capacity);              
        }

        byteBuffer.limit(byteBuffer.position() + size);
        final ByteBuffer view = byteBuffer.slice();
        byteBuffer.position(byteBuffer.limit());  
        
        return view;
    }

    
    /**
     * Return a direct <code>ByteBuffer</code> view using the default size.
     */ 
    public synchronized static ByteBuffer allocateView(final boolean direct){
        return allocateView(defaultCapacity, direct);
    }
    
}
