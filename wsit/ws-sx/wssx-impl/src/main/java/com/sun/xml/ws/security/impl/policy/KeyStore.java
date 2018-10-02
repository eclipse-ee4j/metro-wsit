/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.impl.policy;

import com.sun.xml.ws.policy.AssertionSet;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.sourcemodel.AssertionData;
import java.util.Collection;
import javax.xml.namespace.QName;


/**
 *
 * @author K.Venugopal@sun.com
 */
public class KeyStore extends PolicyAssertion implements com.sun.xml.ws.security.policy.KeyStore{
    
    private static QName loc = new QName("location");
    private static QName type = new QName("type");
    private static QName passwd = new QName("storepass");
    private static QName alias = new QName("alias");
    private static QName keypass = new QName("keypass");
    private static QName aliasSelector = new QName("aliasSelector");
    private static QName callbackHandler = new QName("callbackHandler");
    
    private char [] password = null;
    private static QName keyStoreLoginModuleConfigName = new QName("keystoreloginmoduleconfig");
    /** Creates a new instance of KeyStore */
    public KeyStore() {
    }
    
    public KeyStore(AssertionData name,Collection<PolicyAssertion> nestedAssertions, AssertionSet nestedAlternative) {
        super(name,nestedAssertions,nestedAlternative);
    }
    public String getLocation() {
        return this.getAttributeValue(loc);
    }
    
    public String getType() {
        return this.getAttributeValue(type);
    }
    
    public char[] getPassword() {
        if(password == null){
            String val  = this.getAttributeValue(passwd);
            if(val != null){
                password = val.toCharArray();
            }
        }
        return password;
    }
    
    public String getAlias() {
        return this.getAttributeValue(alias);
    }    
    
    public String getKeyPassword() {
        return this.getAttributeValue(keypass);
    }
    
    public String getAliasSelectorClassName() {
        return this.getAttributeValue(aliasSelector);
    }

    public String getKeyStoreCallbackHandler() {
        return this.getAttributeValue(callbackHandler);
    }

    public String getKeyStoreLoginModuleConfigName() {
        return this.getAttributeValue(keyStoreLoginModuleConfigName);
    }
}
