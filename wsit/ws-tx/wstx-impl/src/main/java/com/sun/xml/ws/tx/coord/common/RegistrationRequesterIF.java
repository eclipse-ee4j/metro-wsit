/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.coord.common;

import com.sun.xml.ws.tx.coord.common.types.BaseRegisterResponseType;

import jakarta.xml.ws.EndpointReference;


public interface RegistrationRequesterIF<T extends EndpointReference,P> {
   void registerResponse(BaseRegisterResponseType<T, P> parameters);
}
