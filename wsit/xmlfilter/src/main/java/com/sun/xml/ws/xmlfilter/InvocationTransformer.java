/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sun.xml.ws.xmlfilter;

import java.util.Collection;

/**
 *
 * @author Marek Potociar (marek.potociar at sun.com)
 */
public interface InvocationTransformer {
    
    /**
     * Before this invocation is processed by {@link FilteringStateMachine} instances
     * the {@link InvocationTransformer} gets a chance to transform the {@code invocation}
     * into series of several invocations. Original invocation may be included as well.
     * 
     * @param invocation original invocation to be transformed.
     * @return collection of invocations as a result of the transformation
     */
    public Collection<Invocation> transform(Invocation invocation);
}
