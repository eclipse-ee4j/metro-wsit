/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package simple.server;

import com.sun.xml.ws.api.security.trust.client.STSIssuedTokenConfiguration;
import com.sun.xml.ws.security.Token;
import com.sun.xml.ws.security.trust.GenericToken;
import org.xmlsoap.dab.Department;

import jakarta.xml.ws.Holder;
import simple.schema.client.Ping;
import simple.schema.client.PingResponseBody;

import simple.client.PingService;
import simple.client.IPingService;

import com.sun.xml.ws.security.trust.STSIssuedTokenFeature;
import com.sun.xml.ws.security.trust.impl.client.DefaultSTSIssuedTokenConfiguration;

import com.sun.xml.wss.SubjectAccessor;
import com.sun.xml.wss.XWSSecurityException;
import com.sun.xml.wss.impl.XWSSecurityRuntimeException;
import com.sun.xml.wss.saml.util.SAMLUtil;
import java.util.Set;
import jakarta.annotation.Resource;
import javax.security.auth.Subject;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import jakarta.xml.ws.WebServiceContext;
import org.w3c.dom.Element;

import jakarta.xml.ws.WebServiceFeature;

@jakarta.jws.WebService (endpointInterface="simple.server.IFinancialService")
public class FSImpl implements IFinancialService {
    @Resource
    private WebServiceContext context;

    public String getAccountBalance(Department dept){

        String company = dept.getCompanyName();
        System.out.println("company = " + company);

        String department = dept.getDepartmentName();
        System.out.println("department = " + department);

        // Call the PingService
        ping();

        String balance = "1,000,000";

        return balance;
    }

    private void ping(){
        PingService service = new PingService();

        STSIssuedTokenConfiguration config = new DefaultSTSIssuedTokenConfiguration();
        Token actAsToken = getActAsToken();
        config.getOtherOptions().put(STSIssuedTokenConfiguration.ACT_AS, actAsToken);
        STSIssuedTokenFeature feature = new STSIssuedTokenFeature(config);

        IPingService stub = service.getCustomBindingIPingService(new WebServiceFeature[]{feature});
        stub.ping(new Holder("1"), new Holder("sun"), new Holder("Passed!"));
    }

    private Token getActAsToken(){
        return new GenericToken(getSAMLAssertion());
    }

    private Element getSAMLAssertion() {
        Element samlAssertion = null;
        try {
            Subject subj = SubjectAccessor.getRequesterSubject(context);
            Set<Object> set = subj.getPublicCredentials();
            for (Object obj : set) {
                if (obj instanceof XMLStreamReader) {
                    XMLStreamReader reader = (XMLStreamReader) obj;
                    //To create a DOM Element representing the Assertion :
                    samlAssertion = SAMLUtil.createSAMLAssertion(reader);
                    break;
                } else if (obj instanceof Element) {
                    samlAssertion = (Element) obj;
                    break;
                }
            }
        } catch (XMLStreamException ex) {
            throw new XWSSecurityRuntimeException(ex);
        } catch (XWSSecurityException ex) {
            throw new XWSSecurityRuntimeException(ex);
        }
        return samlAssertion;
    }
}
