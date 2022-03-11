/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.impl.kerberos;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;

/**
 *
 * @author Ashutosh.Shahi@sun.com
 */
public class KerberosContext {

    private GSSContext context = null;
    private byte[] kerberosToken = null;
    private byte[] secretKey = null;
    private SecretKey sKey = null;
    private boolean setOnce = false;

    /** Creates a new instance of KerberosContext */
    public KerberosContext() {
    }

    public void setOnce(boolean setOnce){
        this.setOnce = setOnce;
    }

    public boolean setOnce(){
        return setOnce;
    }

    public void setGSSContext(GSSContext context){
        this.context = context;
    }

    public GSSContext getGSSContext(){
        return context;
    }

    public void setKerberosToken(byte[] token){
        kerberosToken = token;
    }

    public byte[] getKerberosToken(){
        return kerberosToken;
    }

    public void setSecretKey(byte[] secretKey){
        this.secretKey = secretKey;
    }

    public SecretKey getSecretKey(String algorithm){
        if(sKey == null){
            sKey = new SecretKeySpec(secretKey, algorithm);
        }
        return sKey;
    }

    public GSSCredential getDelegatedCredentials() throws GSSException{
        if(context != null && context.getCredDelegState()){
            return context.getDelegCred();
        }

        return null;
    }
}
