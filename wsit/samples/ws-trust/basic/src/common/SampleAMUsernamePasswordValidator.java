/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package common;

import com.sun.xml.wss.SubjectAccessor;
import com.sun.xml.wss.impl.callback.PasswordValidationCallback;

import java.util.*;
import javax.security.auth.Subject;

import java.security.Principal;

import javax.security.auth.login.LoginException;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

import com.iplanet.am.util.Debug;
import com.iplanet.am.util.SystemProperties;
import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.sun.identity.authentication.AuthContext;



public class SampleAMUsernamePasswordValidator implements PasswordValidationCallback.PasswordValidator {

    private final static String ORG_NAME = "common.org";

    private static SSOToken selfToken = null;
    private static String orgName = SystemProperties.get(ORG_NAME);

    private static Debug debug = Debug.getInstance("SampleAMUsernamePasswordValidator");

    public boolean validate(PasswordValidationCallback.Request request)
            throws PasswordValidationCallback.PasswordValidationException {
        PasswordValidationCallback.PlainTextPasswordRequest plainTextRequest =
                (PasswordValidationCallback.PlainTextPasswordRequest) request;
        String username = plainTextRequest.getUsername();
        String password = plainTextRequest.getPassword();

        SSOToken token = authenticateUser(username, password);

    debug.message("Authenticated username/pasword SSOToken is "+token);

        updateUserSubject(token);

    debug.message("Leaving  SampleAMUsernamePasswordValidator.validate");

        return true;
    }

    private void updateUserSubject(SSOToken token){
        Subject subj = SubjectAccessor.getRequesterSubject();
        if (subj == null){
            subj = new Subject();
            SubjectAccessor.setRequesterSubject(subj);
        }

        debug.message("Add to subject - SSOToken is "+token);
        Set set = subj.getPublicCredentials();
        set.clear();
        set.add(token);
    }

      private SSOToken authenticateUser(String username, String password) throws PasswordValidationCallback.PasswordValidationException
    {
    debug.message("Entering SampleAMUsernamePasswordValidator.authenticateUser");

        AuthContext ac = null;
        SSOToken token = null;

        debug.message("Username is "+username);
        debug.message("OrgName is "+orgName);

        try {
            debug.message("Trying to make an AuthContext");
            ac = new AuthContext(orgName);
            debug.message("Made an AuthContext");
            ac.login();
            debug.message("Logged in AuthContext");
        } catch (LoginException le) {
            debug.error( "Failed to create AuthContext", le );
            throw new PasswordValidationCallback.PasswordValidationException("Failed to create AuthContext", le);
        }

        try {
            Callback[] callbacks = null;
            // Get the information requested by the plug-ins
            while (ac.hasMoreRequirements()) {
                callbacks = ac.getRequirements();

                if (callbacks != null) {
                    addLoginCallbackMessage(callbacks, orgName, username, password);
                    ac.submitRequirements(callbacks);
                }
            }

            if (ac.getStatus() == AuthContext.Status.SUCCESS) {
                debug.message("Authentication successful");
            } else if (ac.getStatus() == AuthContext.Status.FAILED) {
                debug.message("Authentication failed");
                throw new PasswordValidationCallback.PasswordValidationException("Authentication failed");
            } else {
                debug.message("Unknown authentication status: " + ac.getStatus());
                throw new PasswordValidationCallback.PasswordValidationException("Unknown authentication status: " + ac.getStatus());
            }
        } catch (Exception e) {
            debug.error( "Authentication failed", e );
            throw new PasswordValidationCallback.PasswordValidationException("Authentication failed", e);
        }

        try {
            debug.message("Trying to get SSO token");
            token = ac.getSSOToken();
            debug.message("Got SSO token");
        } catch (Exception e) {
            debug.error( "getSSOToken failed", e );
            throw new PasswordValidationCallback.PasswordValidationException("getSSOToken failed", e);
        }

    debug.message("Leaving  SampleAMUsernamePasswordValidator.authenticateUser");

        return token;
    }

    static void addLoginCallbackMessage(Callback[] callbacks, String orgName, String userName, String password)
    {
        for (int i = 0; i < callbacks.length; i++) {
            if (callbacks[i] instanceof NameCallback) {
                // prompt the user for a username
                debug.message("Name callback");
                NameCallback nc = (NameCallback) callbacks[i];
                nc.setName(userName);
            } else if (callbacks[i] instanceof PasswordCallback) {
                // prompt the user for sensitive information
                debug.message("Password callback");
                PasswordCallback pc = (PasswordCallback) callbacks[i];
                pc.setPassword(password.toCharArray());
            }
        }
    }
}
