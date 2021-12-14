/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.policy.jcaps;

import com.sun.xml.ws.policy.parser.PolicyResourceLoader;
import com.sun.xml.ws.policy.privateutil.PolicyUtils;
import java.net.URL;
import junit.framework.TestCase;

/**
 *
 * @author Marek Potociar (marek.potociar at sun.com)
 * @author Fabian Ritzmann
 */
public class JCapsPolicyValidatorTest extends TestCase {
    public JCapsPolicyValidatorTest(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() {
    }
    
    @Override
    protected void tearDown() {
    }
    
    public void testAssertionValidation() throws Exception {
        URL cfgFileUrl = PolicyUtils.ConfigFile.loadFromClasspath("policy/jcaps/test.wsdl");
        assertNotNull("Unable to locate test WSDL", cfgFileUrl);        
        
        PolicyResourceLoader.getWsdlModel(cfgFileUrl, false);
        PolicyResourceLoader.getWsdlModel(cfgFileUrl, true);
        
    }

}
