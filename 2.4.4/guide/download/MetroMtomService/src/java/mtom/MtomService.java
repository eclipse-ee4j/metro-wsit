/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * MtomService.java
 *
 * Created on August 15, 2007, 4:02 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package mtom;

import com.sun.xml.ws.transport.http.HttpAdapter;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.ws.soap.MTOM;

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
