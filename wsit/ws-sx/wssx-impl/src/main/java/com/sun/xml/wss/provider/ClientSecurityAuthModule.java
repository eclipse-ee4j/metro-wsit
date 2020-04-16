/*
 * Copyright (c) 2010, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * $Id: ClientSecurityAuthModule.java,v 1.2 2010-10-21 15:37:47 snajper Exp $
 */

package com.sun.xml.wss.provider;

import java.util.Map;
import java.util.Set;
import java.util.Iterator;

import jakarta.xml.soap.SOAPMessage;
import jakarta.xml.soap.SOAPException;
import javax.security.auth.Subject;
import javax.security.auth.Destroyable;
import javax.security.auth.DestroyFailedException;
import javax.security.auth.callback.CallbackHandler;

import com.sun.xml.wss.impl.MessageConstants;

import com.sun.xml.wss.impl.SecurityAnnotator;
import com.sun.xml.wss.impl.SecurityRecipient;
import com.sun.xml.wss.XWSSecurityException;
import com.sun.xml.wss.impl.ProcessingContextImpl;

import com.sun.xml.wss.impl.config.DeclarativeSecurityConfiguration;

import javax.security.auth.message.AuthException;
import javax.security.auth.message.AuthStatus;
import javax.security.auth.message.MessageInfo;
import javax.security.auth.message.MessagePolicy;
import javax.security.auth.message.module.ClientAuthModule;


public class ClientSecurityAuthModule extends WssProviderAuthModule
                                      implements ClientAuthModule {

       public ClientSecurityAuthModule() {
       }

       @Override
       public void initialize (MessagePolicy requestPolicy,
                               MessagePolicy responsePolicy,
                               CallbackHandler handler,
                               Map options) {
            super.initialize(requestPolicy, responsePolicy, handler, options, true); 
       }

       @SuppressWarnings("unchecked")
       @Override
       public AuthStatus secureRequest (MessageInfo param, Subject subject)
                   throws AuthException {
             try {

                 ProcessingContextImpl context = new ProcessingContextImpl();


                 _sEnvironment.setSubject(subject, context.getExtraneousProperties());
                 Map sharedState = param.getMap();
                 if (sharedState != null) {
                     sharedState.put(SELF_SUBJECT, subject);
                 }

                 com.sun.xml.wss.impl.policy.mls.MessagePolicy senderConfg =
                  ((DeclarativeSecurityConfiguration)_policy).senderSettings();

                 //SOAPMessage msg = ((SOAPAuthParam)param).getRequest();
                 SOAPMessage msg = AuthParamHelper.getRequest(param);
                 context.setSecurityPolicy(senderConfg);
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
                 return AuthStatus.SEND_SUCCESS;

             } catch (XWSSecurityException xwsse) {
                 //TODO: log here
                 xwsse.printStackTrace();
                 throw new AuthException(xwsse.getMessage());
             }
       }

       @Override
       public AuthStatus validateResponse (MessageInfo param,
                                     Subject subject,
                                     Subject serviceSubject)
                   throws AuthException {
             try {
   
                 ProcessingContextImpl context = new ProcessingContextImpl();

                 // are the below two lines required ?.
                 Map sharedState = param.getMap();
                 if (sharedState != null) {
                     Subject selfSubject = (Subject)sharedState.get(SELF_SUBJECT);
                     _sEnvironment.setSubject(selfSubject, context.getExtraneousProperties());
                 }
 
                 _sEnvironment.setRequesterSubject(subject, context.getExtraneousProperties());

                 com.sun.xml.wss.impl.policy.mls.MessagePolicy receiverConfg =
                 ((DeclarativeSecurityConfiguration)_policy).receiverSettings();
                 
                 context.setSecurityPolicy(receiverConfg);
                 //context.setSOAPMessage(((SOAPAuthParam)param).getResponse());
                 context.setSOAPMessage(AuthParamHelper.getResponse(param));
                 context.setSecurityEnvironment(_sEnvironment);

                 SecurityRecipient.validateMessage(context);

                 context.getSecurableSoapMessage().deleteSecurityHeader();
                 return AuthStatus.SUCCESS;
             } catch (XWSSecurityException xwsse) {
                xwsse.printStackTrace();
                throw new AuthException(xwsse.getMessage());
             } 
       }

       @Override
       public void cleanSubject (MessageInfo msg, Subject subject)
                   throws AuthException {
             if (subject == null) {
                // log
                throw new AuthException("Error disposing Subject: null value for Subject");
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
}
