/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.impl.policy;

import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.AssertionSet;
import com.sun.xml.ws.policy.sourcemodel.AssertionData;
import java.util.Collection;
import javax.xml.namespace.QName;
/**
 *
 * @author K.Venugopal@sun.com
 */
public class Validator extends PolicyAssertion implements com.sun.xml.ws.security.policy.Validator{
    private static QName name = new QName("name");
    private static QName className = new QName("classname");
    /** Creates a new instance of Validator */
    public Validator() {
    }

    public Validator(AssertionData name,Collection<PolicyAssertion> nestedAssertions, AssertionSet nestedAlternative) {
        super(name,nestedAssertions,nestedAlternative);
    }

    @Override
    public String getValidatorName() {
        return this.getAttributeValue(name);
    }

    @Override
    public String getValidator() {
        return this.getAttributeValue(className);
    }

}
