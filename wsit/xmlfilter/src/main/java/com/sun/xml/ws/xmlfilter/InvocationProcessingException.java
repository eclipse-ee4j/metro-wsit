/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.xmlfilter;


import com.sun.xml.ws.xmlfilter.localization.LocalizationMessages;

/**
 *
 * @author Marek Potociar (marek.potociar at sun.com)
 */
public class InvocationProcessingException extends RuntimeException {
    private static final long serialVersionUID = 7039831176664696357L;

    public InvocationProcessingException(final String message) {
	super(message);
    }

    public InvocationProcessingException(final String message, final Throwable cause) {
        super(message, cause);
    }    

    public InvocationProcessingException(final Throwable cause) {
        super(cause.getMessage(), cause);
    }    

    public InvocationProcessingException(final Invocation invocation) {
	super(assemblyExceptionMessage(invocation));
    }

    public InvocationProcessingException(final Invocation invocation, final Throwable cause) {
        super(assemblyExceptionMessage(invocation), cause);
    }    
    
    private static String assemblyExceptionMessage(final Invocation invocation) {
        return LocalizationMessages.XMLF_5005_INVOCATION_ERROR(invocation.getMethodName(), invocation.argsToString());
    }    
}
