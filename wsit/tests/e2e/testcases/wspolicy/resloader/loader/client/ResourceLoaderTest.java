/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package wspolicy.resloader.loader.client;

import com.sun.xml.ws.api.ResourceLoader;
import com.sun.xml.ws.api.WSService;
import com.sun.xml.ws.api.WSService.InitParams;
import com.sun.xml.ws.api.server.Container;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import jakarta.xml.ws.Dispatch;
import jakarta.xml.ws.Service;
import junit.framework.TestCase;

public class ResourceLoaderTest extends TestCase {

    public ResourceLoaderTest(String name) {
        super(name);
    }

    public void testResourceLoader() throws MalformedURLException {
        InitParams parameters = new InitParams();
        parameters.setContainer(new MockContainer());
        URL wsdlLocation = new URL("http://localhost:8080/TestServiceServlet/TestServiceService?wsdl");
        QName serviceQName = new QName("http://test.ws.xml.sun.com/", "TestServiceService");
        Service service = WSService.create(wsdlLocation, serviceQName, parameters);
        QName portQName = new QName("http://test.ws.xml.sun.com/", "TestServicePort");
        Dispatch dispatch = service.createDispatch(portQName, Source.class, Service.Mode.PAYLOAD);
    }

    private static class MockContainer extends Container {

        private static final ResourceLoader testLoader = new ResourceLoader() {

            public URL getResource(String resourceName) throws MalformedURLException {
                System.err.println("loading resource \"" + resourceName + "\"");
                return new URL("file:/Users/fr159072/hg/TestServiceEjb/build/jar/META-INF/wsit-com.sun.xml.ws.test.TestService.xml");
            }
        };

        public <T> T getSPI(Class<T> spiType) {
            if (spiType == ResourceLoader.class) {
                return spiType.cast(testLoader);
            }
            return null;
        }
    }
}
