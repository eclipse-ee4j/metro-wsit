/*
 * Copyright (c) 2018, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package mtom;

import com.sun.xml.ws.transport.http.HttpAdapter;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.xml.ws.soap.MTOM;

/**
 *
 * @author vivekp
 */
@WebService()
@MTOM
public class MtomService {
    static{
        HttpAdapter.dump = true;
    }
    /**
     * Web service operation
     */
    @WebMethod
    public byte[] echoBinary(@WebParam(name = "data") byte[] data) {
        return data;
    }

}
