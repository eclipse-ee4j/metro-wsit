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

import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.pipe.NextAction;
import com.sun.xml.ws.api.pipe.Tube;
import com.sun.xml.ws.api.pipe.TubeCloner;
import com.sun.xml.ws.api.pipe.helper.AbstractFilterTubeImpl;

final class CustomTube extends AbstractFilterTubeImpl {
    private static final Logger logger = Logger.getLogger(CustomTube.class.getName());

    static enum Side {
        Client,
        Endpoint
    }

    private final Side side;

    private CustomTube(CustomTube original, TubeCloner cloner) {
        super(original, cloner);

        this.side = original.side;
    }

    @Override
    public CustomTube copy(TubeCloner cloner) {
        return new CustomTube(this, cloner);
    }

    CustomTube(Tube tube, Side side) {
        super(tube);

        this.side = side;
    }

    @Override
    public NextAction processRequest(Packet request) {
        // TODO: place your request processing code here
        logger.info(String.format("Message request intercepted on %s side", side));

        return super.processRequest(request);
    }

    @Override
    public NextAction processResponse(Packet response) {
        // TODO: place your response processing code here
        logger.info(String.format("Message response intercepted on %s side", side));

        return super.processResponse(response);
    }

    @Override
    public NextAction processException(Throwable throwable) {
        // TODO: place your error processing code here
        logger.info(String.format("Message processing exception intercepted on %s side", side));

        return super.processException(throwable);
    }

    @Override
    public void preDestroy() {
        try {
            // TODO: place your resource releasing code here
        } finally {
            super.preDestroy();
        }
    }
}
