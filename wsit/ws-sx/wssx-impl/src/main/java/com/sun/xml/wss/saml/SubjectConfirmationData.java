/*
 * Copyright (c) 2010, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * SubjectConfirmation.java
 *
 * Created on August 18, 2005, 12:34 PM
 *
 */

package com.sun.xml.wss.saml;

import java.util.Date;

/**
 *
 * @author abhijit.das@Sun.COM
 */
public interface SubjectConfirmationData {

    /**
     * Gets the value of the address property.
     *
     * @return object is {@link java.lang.String }
     *
     */
    String getAddress();

    /**
     * Gets the value of the inResponseTo property.
     *
     * @return object is {@link java.lang.String }
     *
     */
    String getInResponseTo();

    /**
     * Gets the value of the notBefore property.
     *
     * @return object is {@link Date }
     *
     */
    Date getNotBeforeDate();

    /**
     * Gets the value of the notOnOrAfter property.
     *
     * @return object is {@link Date }
     *
     */
    Date getNotOnOrAfterDate();

    /**
     * Gets the value of the recipient property.
     *
     * @return object is {@link java.lang.String }
     *
     */
    String getRecipient();
}
