/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.coord.common;

import com.sun.xml.bind.api.JAXBRIContext;
import com.sun.xml.ws.api.message.Header;
import com.sun.xml.ws.api.message.HeaderList;
import com.sun.xml.ws.api.message.MessageHeaders;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.tx.at.WSATConstants;
import com.sun.xml.ws.api.tx.at.Transactional;
import com.sun.xml.ws.tx.coord.common.types.CoordinationContextIF;


public abstract class CoordinationContextBuilder {
    protected String coordinationType;
    protected String identifier;
    protected long expires;
    protected String address;
    protected String txId;
    protected boolean mustUnderstand;
    protected SOAPVersion soapVersion;

    protected Header coordinationHeader;
    Transactional.Version version;


    public static CoordinationContextBuilder newInstance(Transactional.Version version) {
        if(Transactional.Version.WSAT10 == version)
        return new com.sun.xml.ws.tx.coord.v10.CoordinationContextBuilderImpl();
        else if(Transactional.Version.WSAT11 == version || Transactional.Version.WSAT12 == version) {
          return new com.sun.xml.ws.tx.coord.v11.CoordinationContextBuilderImpl();
        }else {
            throw new IllegalArgumentException(version + "is not a supported ws-at version");
        }
    }


    public static CoordinationContextBuilder headers(MessageHeaders h, Transactional.Version version) {
        CoordinationContextBuilder builder = null;
        // FIXME: RJE -- remove cast once MessageHeaders supports asList()
        HeaderList headers = (HeaderList) h;
        for (int i = 0; i < headers.size(); i++) {
            Header header =  headers.get(i);
            if(header.getLocalPart().equals(WSATConstants.COORDINATION_CONTEXT)){
                if(WSATConstants.WSCOOR10_NS_URI.equals(header.getNamespaceURI())){
                    if (version == Transactional.Version.WSAT10 || version == Transactional.Version.DEFAULT) {
                        builder = new com.sun.xml.ws.tx.coord.v10.CoordinationContextBuilderImpl();
                        builder.version = Transactional.Version.WSAT10;
                    }
                }else if(WSATConstants.WSCOOR11_NS_URI.equals(header.getNamespaceURI())){
                    if (version != Transactional.Version.WSAT10) {
                        builder = new com.sun.xml.ws.tx.coord.v11.CoordinationContextBuilderImpl();
                        builder.version = Transactional.Version.WSAT11;
                    }
                }
                if(builder!=null) {
                  headers.understood(i);
                  return builder.header(header);
                }
            }
        }
        return null;
    }

    public Transactional.Version getVersion() {
        return version;
    }

    public CoordinationContextBuilder address(String address) {
        this.address = address;
        return this;
    }

    public CoordinationContextBuilder txId(String txId) {
        this.txId = txId;
        return this;
    }

    public CoordinationContextBuilder identifier(String identifier) {
        this.identifier = identifier;
        return this;
    }

    public CoordinationContextBuilder expires(long expires) {
        this.expires = expires;
        return this;
    }

    public CoordinationContextBuilder mustUnderstand(boolean mustUnderstand) {
        this.mustUnderstand = mustUnderstand;
        return this;
    }

    public CoordinationContextBuilder soapVersion(SOAPVersion soapVersion) {
        this.soapVersion = soapVersion;
        return this;
    }

  public CoordinationContextBuilder coordinationType(String coordinationType) {
        this.coordinationType = coordinationType;
        return this;
    }

    CoordinationContextBuilder header(Header coordinationHeader) {
      this.coordinationHeader = coordinationHeader;
      return this;
    }

    public CoordinationContextIF buildFromHeader(){
        return _fromHeader(coordinationHeader);
    }
    protected abstract CoordinationContextIF _fromHeader(Header header);

    public abstract CoordinationContextIF build();

    public abstract JAXBRIContext getJAXBRIContext();
}
