/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package simple.server;

import org.xmlsoap.dab.Department;
import jakarta.xml.ws.Holder;

@jakarta.jws.WebService (endpointInterface="simple.server.IFinancialService")
public class FSImpl implements IFinancialService {

    public String getAccountBalance(Department dept, byte[] data){

        String company = dept.getCompanyName();
        System.out.println("company = " + company);

        String department = dept.getDepartmentName();
        System.out.println("department = " + department);

        String balance = "1,000,000";

        return balance;
    }
}
