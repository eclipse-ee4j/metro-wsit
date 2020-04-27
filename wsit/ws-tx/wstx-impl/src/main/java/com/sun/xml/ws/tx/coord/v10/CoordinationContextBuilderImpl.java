/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.coord.v10;

import org.glassfish.jaxb.runtime.api.JAXBRIContext;
import com.sun.xml.ws.api.message.Header;
import com.sun.xml.ws.developer.MemberSubmissionEndpointReference;
import org.w3c.dom.Element;
import com.sun.xml.ws.tx.coord.common.CoordinationContextBuilder;
import com.sun.xml.ws.tx.coord.common.EndpointReferenceBuilder;
import com.sun.xml.ws.tx.coord.common.WSCUtil;
import com.sun.xml.ws.tx.coord.common.types.CoordinationContextIF;
import com.sun.xml.ws.tx.coord.v10.types.CoordinationContext;
import com.sun.xml.ws.tx.coord.v10.types.CoordinationContextType;
import com.sun.xml.ws.tx.coord.v10.types.Expires;

import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import jakarta.xml.ws.WebServiceException;


public class CoordinationContextBuilderImpl extends CoordinationContextBuilder {

    protected CoordinationContextIF _fromHeader(Header header){
        try {
            Unmarshaller unmarshaller = XmlTypeAdapter.CoordinationContextImpl.jaxbContext.createUnmarshaller();
            CoordinationContext cct = header.readAsJAXB(unmarshaller);
            return new XmlTypeAdapter().adapt(cct);
        } catch (JAXBException e) {
            throw new WebServiceException(e);
        }
    }

    @Override
    public CoordinationContextIF build() {

        CoordinationContext cct = buildContext();

        return new XmlTypeAdapter().adapt(cct);
    }

    public JAXBRIContext getJAXBRIContext() {
        return XmlTypeAdapter.CoordinationContextImpl.jaxbContext;
    }

    private CoordinationContext buildContext() {
        CoordinationContext cct = new CoordinationContext();
        if (mustUnderstand) {
          if(soapVersion == null){
            throw new WebServiceException("SOAP version is not specified!");
          }
           cct.getOtherAttributes().put(new QName(soapVersion.nsUri,"mustUnderstand"), "1");
        }
        cct.setCoordinationType(coordinationType);

        CoordinationContextType.Identifier IdentifierObj =  new CoordinationContextType.Identifier();
        IdentifierObj.setValue(identifier);
        cct.setIdentifier(IdentifierObj);

        Expires expiresObj = new Expires();
        expiresObj.setValue(expires);
        cct.setExpires(expiresObj);

        cct.setRegistrationService(getEPR());
        return cct;
    }

    private MemberSubmissionEndpointReference getEPR(){
        Element ele = WSCUtil.referenceElementTxId(txId);
        Element ele2 = WSCUtil.referenceElementRoutingInfo();
        return EndpointReferenceBuilder.MemberSubmission().address(address).
                referenceParameter(ele).referenceParameter(ele2).build();
    }


}
