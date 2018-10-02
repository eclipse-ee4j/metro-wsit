/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package common;

/*
 * SampleUsernamePasswordCallbackHandler.java
 *
 * Created on June 17, 2006, 11:50 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

import java.io.*;

/**
 *
 * @author Jiandong Guo
 */
public class SampleUsernamePasswordCallbackHandler implements CallbackHandler {
    
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        
        for (int i=0; i < callbacks.length; i++) {
            Callback callback = callbacks[i];
            if (callback instanceof NameCallback) {
                handleUsernameCallback((NameCallback)callback);
            } else if (callback instanceof PasswordCallback) {
                handlePasswordCallback((PasswordCallback)callback);
            }else{
                throw new UnsupportedCallbackException(callback, "Unknow callback for username or password");
            }
        }
    }
    
    private void handleUsernameCallback(NameCallback cb)throws IOException{
        //BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.err.println("***Please Enter Your User Name: ");
        System.err.flush();
        cb.setName((new BufferedReader(new InputStreamReader(System.in))).readLine());
    }
    
    private void handlePasswordCallback(PasswordCallback cb)throws IOException{
        //BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.err.println("***Please Enter Your Password: ");
        System.err.flush();
        cb.setPassword((new BufferedReader(new InputStreamReader(System.in))).readLine().toCharArray());
    }
}
