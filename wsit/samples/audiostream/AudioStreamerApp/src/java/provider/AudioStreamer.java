/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package provider;

import java.io.File;
import javax.activation.DataHandler;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.ws.WebServiceException;

/**
 *
 * @author Marek Potociar <marek.potociar at sun.com>
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
