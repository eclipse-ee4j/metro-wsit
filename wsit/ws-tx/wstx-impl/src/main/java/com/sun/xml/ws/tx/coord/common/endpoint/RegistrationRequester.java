/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.coord.common.endpoint;

import com.sun.xml.ws.tx.at.WSATHelper;
import com.sun.xml.ws.tx.coord.common.PendingRequestManager;
import com.sun.xml.ws.tx.coord.common.RegistrationRequesterIF;
import com.sun.xml.ws.tx.coord.common.types.BaseRegisterResponseType;

import javax.xml.ws.WebServiceContext;


public abstract class RegistrationRequester implements RegistrationRequesterIF
{

    private WebServiceContext m_context;

    public RegistrationRequester(WebServiceContext m_context) {
        this.m_context = m_context;
    }

    /**
     *
     * @param parameters
     */
    public void registerResponse(BaseRegisterResponseType parameters) {
        String referenceID =  getWSATHelper().getWSATTidFromWebServiceContextHeaderList(m_context);
        PendingRequestManager.registryReponse(referenceID,parameters);
    }


    protected  abstract WSATHelper getWSATHelper();
}
