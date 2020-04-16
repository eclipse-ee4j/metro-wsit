/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package wspolicy.dispatch.securefail.server;

import jakarta.jws.WebService;

@WebService(endpointInterface="wspolicy.dispatch.securefail.server.Echo")
public class EchoImpl {
    
    public String echo(String yodel) {
        StringBuffer holladrio = new StringBuffer();
        int l = yodel.length();
        for (int i = 0; i <  l; i++) {
            holladrio.append(yodel.substring(i, l));
        }
        return holladrio.toString();
    }
    
}
