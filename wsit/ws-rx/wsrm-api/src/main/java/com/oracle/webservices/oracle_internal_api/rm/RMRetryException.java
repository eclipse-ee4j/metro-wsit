/*
 * Copyright (c) 2013, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.oracle.webservices.oracle_internal_api.rm;

/**
 * {@code RMRetryException} is given to {@code com.oracle.webservices.api.disi.ClientResponseTransport#fail}
 * to signal that the RMS retry sending the message again.
 *
 * <p>This results in the RMS causing the message to be given to
 * {@code com.oracle.webservices.api.disi.ClientRequestTransport#request}
 * again.
 *
 * <p>Note: a retry will not occur is max retries, timeouts, etc., are exceeded.</p>
 */
public class RMRetryException
    extends Exception
{
}

// End of file.
