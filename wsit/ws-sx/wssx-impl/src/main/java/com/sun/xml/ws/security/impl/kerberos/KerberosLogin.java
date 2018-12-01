/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.impl.kerberos;

import com.sun.xml.wss.XWSSecurityException;
import com.sun.xml.wss.logging.LogDomainConstants;
import java.lang.reflect.Method;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.Key;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.Subject;
import javax.security.auth.login.AccountExpiredException;
import javax.security.auth.login.CredentialExpiredException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSManager;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.Oid;

/**
 *
 * @author ashutosh.shahi@sun.com
 */
public class KerberosLogin {

    private static Logger log = Logger.getLogger(
            LogDomainConstants.WSS_API_DOMAIN,
            LogDomainConstants.WSS_API_DOMAIN_BUNDLE);

    /** Creates a new instance of KerberosLogin */
    public KerberosLogin() {
    }
    @SuppressWarnings("unchecked")
    public KerberosContext login(String loginModule, String servicePrincipal, boolean credDeleg) throws XWSSecurityException{
        KerberosContext krbContext = new KerberosContext();
        LoginContext lc = null;
        try {
            lc = new LoginContext(loginModule, new com.sun.security.auth.callback.TextCallbackHandler());
        } catch (LoginException le) {
            throw new XWSSecurityException("Cannot create LoginContext. ", le);
        } catch (SecurityException se) {
            throw new XWSSecurityException("Cannot create LoginContext. ", se);
        }
        
        try{
            // attempt authentication
            lc.login();
            // if we return with no exception, authentication succeeded
        } catch (AccountExpiredException aee) {
            throw new XWSSecurityException("Your Kerberos account has expired.", aee);
        } catch (CredentialExpiredException cee) {
            throw new XWSSecurityException("Your credentials have expired.", cee);
        }  catch (FailedLoginException fle) {
            throw new XWSSecurityException("Authentication Failed", fle);
        } catch (Exception e) {
            throw new XWSSecurityException("Unexpected Exception in Kerberos login - unable to continue", e);
        }
        
        try{
            Subject loginSubject = lc.getSubject();
            Subject.doAsPrivileged(loginSubject,
                    new  KerberosClientSetupAction(servicePrincipal, credDeleg),
                    null);
            
            Set<Object> setPubCred =  loginSubject.getPublicCredentials();
            Iterator<Object> iter1 = setPubCred.iterator();
            GSSContext gssContext=null;
            while(iter1.hasNext()){
                Object pubObject = iter1.next();
                if(pubObject instanceof byte[]){
                    krbContext.setKerberosToken((byte[])pubObject);
                } else if(pubObject instanceof GSSContext){
                    gssContext = (GSSContext)pubObject;
                    krbContext.setGSSContext(gssContext);
                }
            }
            if (gssContext != null && gssContext.isEstablished()) {
                /**
                 *ExtendedGSSContext ex = (ExtendedGSSContext)x;
                 *Key k = (Key)ex.inquireSecContext(
                 *InquireType.KRB5_GET_SESSION_KEY);
                 **/
                Class inquireType;
                try {
                    inquireType = Class.forName("com.sun.security.jgss.InquireType");
                    Class extendedGSSContext = Class.forName("com.sun.security.jgss.ExtendedGSSContext");
                    Method inquireSecContext = extendedGSSContext.getMethod("inquireSecContext", inquireType);
                    Key key = (Key) inquireSecContext.invoke(gssContext, Enum.valueOf(inquireType, "KRB5_GET_SESSION_KEY"));
                    krbContext.setSecretKey(key.getEncoded());
                } catch (ReflectiveOperationException | IllegalArgumentException | SecurityException ex) {
                    log.log(Level.SEVERE, null, ex);
                    throw new XWSSecurityException(ex);
                }
            } else {
                throw new XWSSecurityException("GSSContext was null in the Login Subject");
            }
        } catch (java.security.PrivilegedActionException pae) {
            throw new XWSSecurityException(pae);
        }
        krbContext.setOnce(true);
        return krbContext;
    }
    @SuppressWarnings("unchecked")
    public KerberosContext login(String loginModule, byte[] token) throws XWSSecurityException{
        KerberosContext krbContext = new KerberosContext();
        LoginContext lc = null;
        try {
            lc = new LoginContext(loginModule, new com.sun.security.auth.callback.TextCallbackHandler());
        } catch (LoginException le) {
            throw new XWSSecurityException("Cannot create LoginContext. ", le);
        } catch (SecurityException se) {
            throw new XWSSecurityException("Cannot create LoginContext. ", se);
        }
        
        try{
            // attempt authentication
            lc.login();
            // if we return with no exception, authentication succeeded
        } catch (AccountExpiredException aee) {
            throw new XWSSecurityException("Your Kerberos account has expired.", aee);
        } catch (CredentialExpiredException cee) {
            throw new XWSSecurityException("Your credentials have expired.", cee);
        }  catch (FailedLoginException fle) {
            throw new XWSSecurityException("Authentication Failed", fle);
        } catch (Exception e) {
            throw new XWSSecurityException("Unexpected Exception in Kerberos login - unable to continue", e);
        }
        
        try{
            Subject loginSubject = lc.getSubject();
            Subject.doAsPrivileged(loginSubject,
                    new  KerberosServerSetupAction(token),
                    null);
            
            Set<Object> setPubCred =  loginSubject.getPublicCredentials();
            Iterator<Object> iter1 = setPubCred.iterator();
            GSSContext gssContext=null;
            while(iter1.hasNext()){
                Object pubObject = iter1.next();
                if(pubObject instanceof byte[]){
                    krbContext.setKerberosToken((byte[])pubObject);
                } else if(pubObject instanceof GSSContext){
                    gssContext = (GSSContext)pubObject;
                    krbContext.setGSSContext(gssContext);
                }
            }
            if (gssContext != null && gssContext.isEstablished()) {
                /**
                 *ExtendedGSSContext ex = (ExtendedGSSContext)x;
                 *Key k = (Key)ex.inquireSecContext(
                 *InquireType.KRB5_GET_SESSION_KEY);
                 **/
                Class inquireType;
                try {
                    inquireType = Class.forName("com.sun.security.jgss.InquireType");
                    Class extendedGSSContext = Class.forName("com.sun.security.jgss.ExtendedGSSContext");
                    Method inquireSecContext = extendedGSSContext.getMethod("inquireSecContext", inquireType);
                    Key key = (Key)inquireSecContext.invoke(gssContext, Enum.valueOf(inquireType, "KRB5_GET_SESSION_KEY"));
                    krbContext.setSecretKey(key.getEncoded());
                } catch (ReflectiveOperationException | IllegalArgumentException | SecurityException ex) {
                    log.log(Level.SEVERE, null, ex);
                    throw new XWSSecurityException(ex);
                }
            } else {
                throw new XWSSecurityException("GSSContext was null in the Login Subject");
            }
        } catch (java.security.PrivilegedActionException pae) {
            throw new XWSSecurityException(pae);
        }
        krbContext.setOnce(false);
        return krbContext;
    }
    
    class KerberosClientSetupAction implements java.security.PrivilegedExceptionAction {
        String server;
        boolean credentialDelegation = false;
        
        public KerberosClientSetupAction(String server, boolean credDeleg){
            this.server = server;
            credentialDelegation = credDeleg;
        }
        
        public Object run() throws Exception {
            
            try {
                Oid krb5Oid = new Oid("1.2.840.113554.1.2.2");
                GSSManager manager = GSSManager.getInstance();
                GSSName serverName = manager.createName(server, null);
                
                GSSContext context = manager.createContext(serverName,
                        krb5Oid,
                        null,
                        GSSContext.DEFAULT_LIFETIME);
                context.requestMutualAuth(false);  // Mutual authentication
                context.requestConf(false);  // Will use confidentiality later
                context.requestInteg(true); // Will use integrity later
                
                context.requestCredDeleg(credentialDelegation);
                
                byte[] token = new byte[0];
                token = context.initSecContext(token, 0, token.length);
                
                AccessControlContext acc = AccessController.getContext();
                Subject loginSubject = Subject.getSubject(acc);
                loginSubject.getPublicCredentials().add(context);
                loginSubject.getPublicCredentials().add(token);
                
            } catch (Exception e) {
                throw new java.security.PrivilegedActionException(e);
            }
            return null;
        }
        
    }
    
    class KerberosServerSetupAction implements java.security.PrivilegedExceptionAction {
        
        byte[] token;
        
        public KerberosServerSetupAction(byte[] token){
            this.token = token;
        }
        @SuppressWarnings("unchecked")
        public Object run() throws Exception {
            
            try {
                
                final GSSManager manager = GSSManager.getInstance();
                GSSContext context = manager.createContext((GSSCredential)null);
                byte[] outToken = context.acceptSecContext(token, 0, token.length);
                if(outToken != null && outToken.length != 0){
                    // error condition - should be zero for kerberos w/o mutual authentication
                }
                AccessControlContext acc = AccessController.getContext();
                Subject loginSubject = Subject.getSubject(acc);
                loginSubject.getPublicCredentials().add(context);
                loginSubject.getPublicCredentials().add(token);
            } catch (Exception e) {
                throw new java.security.PrivilegedActionException(e);
            }
            return null;
        }
        
    }
    
}
