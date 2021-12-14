/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.transport.tcp.servicechannel;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.api.BindingID;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.server.DocumentAddressResolver;
import com.sun.xml.ws.api.server.PortAddressResolver;
import com.sun.xml.ws.api.server.SDDocument;
import com.sun.xml.ws.api.server.SDDocumentSource;
import com.sun.xml.ws.api.server.WSEndpoint;
import com.sun.xml.ws.transport.tcp.util.TCPConstants;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public final class ServiceChannelWSDLGenerator {

    private static final String TCP_ENDPOINT_ADDRESS_STUB = TCPConstants.PROTOCOL_SCHEMA + "://CHANGED_BY_RUNTIME";
    
    public static void main(final String[] args) throws Exception {
        final QName serviceName = WSEndpoint.getDefaultServiceName(ServiceChannelWSImpl.class);
        final QName portName = WSEndpoint.getDefaultPortName(serviceName, ServiceChannelWSImpl.class);
        final BindingID bindingId = BindingID.parse(ServiceChannelWSImpl.class);
        final WSBinding binding = bindingId.createBinding();
        final Collection<SDDocumentSource> docs = new ArrayList<>(0);
        
        final WSEndpoint<?> endpoint = WSEndpoint.create(
                ServiceChannelWSImpl.class, true,
                null,
                serviceName, portName, null, binding,
                null, docs, (URL) null
                );
        
        final DocumentAddressResolver resolver = new DocumentAddressResolver() {
            @Override
            public String getRelativeAddressFor(SDDocument current, SDDocument referenced) {
                if (current.isWSDL() && referenced.isSchema() && referenced.getURL().getProtocol().equals("file")) {
                    return referenced.getURL().getFile().substring(1);
                }
                
                return referenced.getURL().toExternalForm();
            }
        };
        
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        /* seems now transformer doesnt support "pretty-output",
        but may be for future using it will make sense */
        final TransformerFactory tFactory =
                TransformerFactory.newInstance();
        final Transformer transformer = tFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT,"yes");
        
        for(final Iterator<SDDocument> it = endpoint.getServiceDefinition().iterator(); it.hasNext();) {
            final SDDocument document = it.next();
            baos.reset();
            
            document.writeTo(new PortAddressResolver() {
                @Override
                public @Nullable String getAddressFor(QName serviceName, @NotNull String portName) {
                    return TCP_ENDPOINT_ADDRESS_STUB;
                }
            }, resolver, baos);
            final ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            
            final FileOutputStream fos = new FileOutputStream("./etc/" + document.getURL().getFile());
            final Source source = new StreamSource(bais);
            final StreamResult result = new StreamResult(fos);
            transformer.transform(source, result);
            fos.close();
            bais.close();
        }
        
        baos.close();
    }
}
