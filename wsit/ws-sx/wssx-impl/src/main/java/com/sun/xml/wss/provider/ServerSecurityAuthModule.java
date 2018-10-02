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
 * $Id: ServerSecurityAuthModule.java,v 1.2 2010-10-21 15:37:47 snajper Exp $
 */

package com.sun.xml.wss.provider;

import java.util.Map;
import java.util.Set;
import java.util.Iterator;

import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPException;
import javax.security.auth.Subject;
import javax.security.auth.Destroyable;
import javax.security.auth.DestroyFailedException;
import javax.security.auth.callback.CallbackHandler;

import com.sun.enterprise.security.jauth.AuthParam;
import com.sun.enterprise.security.jauth.AuthPolicy;
//import com.sun.enterprise.security.jauth.SOAPAuthParam;
import com.sun.enterprise.security.jauth.AuthException;
import com.sun.enterprise.security.jauth.ServerAuthModule;

import com.sun.xml.wss.impl.SecurityAnnotator;
import com.sun.xml.wss.impl.SecurityRecipient;
import com.sun.xml.wss.impl.SecurableSoapMessage;
import com.sun.xml.wss.XWSSecurityException;
import com.sun.xml.wss.impl.ProcessingContextImpl;

import com.sun.xml.wss.impl.policy.mls.MessagePolicy;
import com.sun.xml.wss.impl.config.DeclarativeSecurityConfiguration;

import com.sun.xml.wss.impl.WssProviderSecurityEnvironment;

import com.sun.xml.wss.impl.MessageConstants;

public class ServerSecurityAuthModule extends WssProviderAuthModule 
                                      implements ServerAuthModule {

       public ServerSecurityAuthModule() {
       }

       public void initialize (AuthPolicy requestPolicy,
                               AuthPolicy responsePolicy,
                               CallbackHandler handler,
                               Map options) {
            super.initialize(requestPolicy, responsePolicy, handler, options, false);  
       }

       public void validateRequest (AuthParam param,
                                    Subject subject,
                                    Map sharedState)
                   throws AuthException {
             try {

                 ProcessingContextImpl context = new ProcessingContextImpl();

                 _sEnvironment.setRequesterSubject(subject, context.getExtraneousProperties());

                 MessagePolicy receiverCnfg = 
                 ((DeclarativeSecurityConfiguration)_policy).receiverSettings();
 
                 context.setSecurityPolicy(receiverCnfg);
                 context.setSOAPMessage(AuthParamHelper.getRequest(param));
                 context.setSecurityEnvironment(_sEnvironment);

                 SecurityRecipient.validateMessage(context); 

                 populateSharedStateFromContext(sharedState, context);
 
                 context.getSecurableSoapMessage().deleteSecurityHeader();

             } catch (XWSSecurityException xwsse) {
                xwsse.printStackTrace();
                throw new AuthException(xwsse.getMessage());
             }
       }

       public void secureResponse (AuthParam param,
                                   Subject subject,
                                   Map sharedState)
                   throws AuthException {
             try {

               ProcessingContextImpl context = new ProcessingContextImpl();
               _sEnvironment.setSubject(subject, context.getExtraneousProperties());

               populateContextFromSharedState(context, sharedState);

 
               MessagePolicy senderCnfg = 
                 ((DeclarativeSecurityConfiguration)_policy).senderSettings();
              
               SOAPMessage msg = AuthParamHelper.getResponse(param);
               context.setSecurityPolicy(senderCnfg);
               context.setSOAPMessage(msg);
               context.setSecurityEnvironment(_sEnvironment);

                if (optimize  != MessageConstants.NOT_OPTIMIZED  && isOptimized(msg)) {
                      context.setConfigType(optimize);
                } else {
		    try{
                      msg.getSOAPBody();
                      msg.getSOAPHeader();
                      context.setConfigType(MessageConstants.NOT_OPTIMIZED);
		    }catch(SOAPException ex){
			throw new AuthException(ex.getMessage());
		    }
                }

               SecurityAnnotator.secureMessage(context);
               

             } catch (XWSSecurityException xwsse) {
               xwsse.printStackTrace();
               throw new AuthException(xwsse.getMessage());
             } 
       }

       public void disposeSubject (Subject subject,
                                   Map sharedState)
                   throws AuthException {
             if (subject == null) {
                // log
                throw new AuthException("Subject is null in disposeSubject");
             }

             if (!subject.isReadOnly()) {
                 // log
                 //subject = new Subject();
                 return;
             }

             Set principals = subject.getPrincipals();
             Set privateCredentials = subject.getPrivateCredentials();
             Set publicCredentials = subject.getPublicCredentials();

             try {
                principals.clear();
             } catch (UnsupportedOperationException uoe) {
                // log
             }

             Iterator pi = privateCredentials.iterator();
             while (pi.hasNext()) {
                try {
                    Destroyable dstroyable = 
                                   (Destroyable)pi.next();
                    dstroyable.destroy(); 
                } catch (DestroyFailedException dfe) {
                   // log
                } catch (ClassCastException cce) {
                   // log
                }  
             }

             Iterator qi = publicCredentials.iterator();
             while (qi.hasNext()) {
              try {
                    Destroyable dstroyable = 
                                   (Destroyable)qi.next();
                    dstroyable.destroy(); 
                } catch (DestroyFailedException dfe) {
                   // log
                } catch (ClassCastException cce) {
                   // log
                }   
             }       
       }        

       private void populateContextFromSharedState(ProcessingContextImpl context, Map sharedState) {
           context.setExtraneousProperty(
               MessageConstants.AUTH_SUBJECT, sharedState.get(REQUESTER_SUBJECT));
           context.setExtraneousProperty(
               MessageConstants.REQUESTER_KEYID, sharedState.get(REQUESTER_KEYID));
           context.setExtraneousProperty(
               MessageConstants.REQUESTER_ISSUERNAME, sharedState.get(REQUESTER_ISSUERNAME));
           context.setExtraneousProperty(
               MessageConstants.REQUESTER_SERIAL, sharedState.get(REQUESTER_SERIAL));
       }

       @SuppressWarnings("unchecked")
       private void populateSharedStateFromContext(Map sharedState, ProcessingContextImpl context) {
           sharedState.put(
               REQUESTER_SUBJECT, context.getExtraneousProperty(MessageConstants.AUTH_SUBJECT));
           sharedState.put(
               REQUESTER_KEYID, context.getExtraneousProperty(MessageConstants.REQUESTER_KEYID));
           sharedState.put(
               REQUESTER_ISSUERNAME, 
                   context.getExtraneousProperty(MessageConstants.REQUESTER_ISSUERNAME));
           sharedState.put(
               REQUESTER_SERIAL, context.getExtraneousProperty(MessageConstants.REQUESTER_SERIAL));

       }
}
