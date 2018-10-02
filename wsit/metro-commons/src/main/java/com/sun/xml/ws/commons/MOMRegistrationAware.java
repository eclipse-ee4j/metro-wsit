/*
 * Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.commons;

/**
 * Provides information whether or not is an object registered at {@link org.glassfish.gmbal.ManagedObjectManager} or not.
 * All classes that use explicit registration at {@link org.glassfish.gmbal.ManagedObjectManager} should implement this
 * interface as it is used when deferring Gmbal API calls in {@link WSEndpointCollectionBasedMOMListener}
 */
public interface MOMRegistrationAware {

    boolean isRegisteredAtMOM();

    void setRegisteredAtMOM(boolean isRegisteredAtMOM);

}
