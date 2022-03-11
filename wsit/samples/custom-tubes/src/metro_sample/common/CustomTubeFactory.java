/*
 * Copyright (c) 2011, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package metro_sample.common;

import java.util.logging.Logger;

import com.sun.xml.ws.api.pipe.Tube;
import com.sun.xml.ws.assembler.dev.ClientTubelineAssemblyContext;
import com.sun.xml.ws.assembler.dev.ServerTubelineAssemblyContext;
import com.sun.xml.ws.assembler.dev.TubeFactory;
import jakarta.xml.ws.WebServiceException;

public final class CustomTubeFactory implements TubeFactory {
    private static final Logger logger = Logger.getLogger(CustomTubeFactory.class.getName());

    public Tube createTube(ClientTubelineAssemblyContext context) throws WebServiceException {
        logger.info("Creating client-side interceptor tube");

        return new CustomTube(context.getTubelineHead(), CustomTube.Side.Client);
    }

    public Tube createTube(ServerTubelineAssemblyContext context) throws WebServiceException {
        logger.info("Creating server-side interceptor tube");

        return new CustomTube(context.getTubelineHead(), CustomTube.Side.Endpoint);
    }
}
