/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.transport.tcp.util;

import java.io.IOException;
import java.nio.channels.Selector;
import java.util.EmptyStackException;
import java.util.Stack;

/**
 * Class was copied from GlassFish Grizzly sources to be available
 * also for client side and don't require GlassFish to be installed

 * Factory used to dispatch/share <code>Selector</code>.
 *
 * @author Scott Oaks
 * @author Jean-Francois Arcand
 */
public final class SelectorFactory{

    /**
     * The timeout before we exit.
     */
    static long timeout = 5000;


    /**
     * The number of <code>Selector</code> to create.
     */
    static int maxSelectors = 20;


    /**
     * Cache of <code>Selector</code>
     */
    private final static Stack<Selector> selectors = new Stack<>();


    static {
        try{
            for (int i = 0; i < maxSelectors; i++)
                selectors.add(Selector.open());
        } catch (IOException ex){
        }
    }


    /**
     * Get a exclusive <code>Selector</code>
     */
    public static Selector getSelector() {
        synchronized(selectors) {
            Selector s = null;
            try {
                if ( selectors.size() != 0 )
                    s = selectors.pop();
            } catch (EmptyStackException ex){}

            int attempts = 0;
            try{
                while (s == null && attempts < 2) {
                    selectors.wait(timeout);
                    try {
                        if ( selectors.size() != 0 )
                            s = selectors.pop();
                    } catch (EmptyStackException ex){
                        break;
                    }
                    attempts++;
                }
            } catch (InterruptedException ex){}
            return s;
        }
    }


    /**
     * Return the <code>Selector</code> to the cache
     */
    public static void returnSelector(final Selector s) {
        synchronized(selectors) {
            selectors.push(s);
            if (selectors.size() == 1)
                selectors.notify();
        }
    }

}
