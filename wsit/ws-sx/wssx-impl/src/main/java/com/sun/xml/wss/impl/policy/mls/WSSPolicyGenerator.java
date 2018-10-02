/*
 * Copyright (c) 2010, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * $Id: WSSPolicyGenerator.java,v 1.2 2010-10-21 15:37:34 snajper Exp $
 */

package com.sun.xml.wss.impl.policy.mls;

//import com.sun.xml.wss.impl.policy.SSLPolicy;
import com.sun.xml.wss.impl.policy.MLSPolicy;
import com.sun.xml.wss.impl.policy.SecurityPolicy;
import com.sun.xml.wss.impl.policy.SecurityPolicyGenerator;
import com.sun.xml.wss.impl.policy.PolicyGenerationException;


/**
 * This class is a Factory for generating the various Security Policy primitives
 * that are understood and processed by XWS-Security.
 * A <code>DynamicSecurityPolicy</code> can obtain an instance of this class to
 * create instances of SecurityPolicies at runtime.
 */
public class WSSPolicyGenerator implements SecurityPolicyGenerator {

    MessagePolicy configuration = new MessagePolicy ();

    /**
     * Default constructor
     */
    public WSSPolicyGenerator () {}
    
    /**
     * return a new concrete MLSPolicy instance
     * @return MLSPolicy
     * @exception PolicyGenerationException
     */
    public MLSPolicy newMLSPolicy () throws PolicyGenerationException {
        throw new PolicyGenerationException ("Unsupported Operation");
    }      

    /**
     * return a new TimestampPolicy instance
     * @return TimestampPolicy
     * @exception PolicyGenerationException
     */
    public TimestampPolicy newTimestampPolicy () throws PolicyGenerationException {
        TimestampPolicy policy = new TimestampPolicy ();

        configuration.append (policy);

        return policy; 
    }   

    /**
     * return a new SignaturePolicy instance
     * @return SignaturePolicy
     * @exception PolicyGenerationException
     */
    public SignaturePolicy newSignaturePolicy () throws PolicyGenerationException {
        SignaturePolicy policy = new SignaturePolicy ();

        configuration.append (policy);

        return policy; 
    }   

    /**
     * return a new EncryptionPolicy instance
     * @return EncryptionPolicy
     * @exception PolicyGenerationException
     */
    public EncryptionPolicy newEncryptionPolicy () throws PolicyGenerationException {
        EncryptionPolicy policy = new EncryptionPolicy ();

        configuration.append (policy);

        return policy; 
    }   

    /**
     * return a new AuthenticationTokenPolicy instance
     * @return AuthenticationTokenPolicy
     * @exception PolicyGenerationException
     */
    public AuthenticationTokenPolicy newAuthenticationTokenPolicy () throws PolicyGenerationException {
        AuthenticationTokenPolicy policy = new AuthenticationTokenPolicy ();

        configuration.append (policy);

        return policy; 
    }  

    /**
     * return a SecurityPolicy that represents a configuration
     * @return SecurityPolicy
     * @exception PolicyGenerationException
     */
    public SecurityPolicy configuration () throws PolicyGenerationException {
        return configuration; 
    }
}
