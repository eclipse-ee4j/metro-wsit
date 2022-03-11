/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.config.metro;

import com.sun.xml.ws.config.metro.dev.FeatureReader;
import javax.xml.namespace.QName;

/**
 * Allows to specify a FeatureReader for each configuration element.
 *
 * @author Fabian Ritzmann
 */
public interface ElementFeatureMapping {

    QName getElementName();

    FeatureReader getFeatureReader();

}
