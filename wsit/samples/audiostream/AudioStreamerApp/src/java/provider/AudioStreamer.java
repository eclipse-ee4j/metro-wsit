/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package provider;

import java.io.File;
import jakarta.activation.DataHandler;
import jakarta.jws.WebMethod;
import jakarta.jws.WebService;
import jakarta.xml.bind.annotation.XmlMimeType;
import jakarta.xml.ws.WebServiceException;

/**
 *
 * @author Marek Potociar
 */
@WebService
public class AudioStreamer {

    /**
     * Web service operation
     */
    @WebMethod(operationName = "getWavStream")
    public @XmlMimeType("application/octet-stream") DataHandler getWavStream(String name) {
        try {
            File audioFile = new File(System.getProperty("user.home") + File.separator + "tmp/resources/" + name + ".wav");
            return new DataHandler(audioFile.toURL());
        } catch (Exception ex) {
            throw new WebServiceException(ex);
        }
    }
}
