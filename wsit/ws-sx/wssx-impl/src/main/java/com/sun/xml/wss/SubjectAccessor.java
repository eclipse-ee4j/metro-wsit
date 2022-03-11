/*
 * Copyright (c) 2010, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * $Id: SubjectAccessor.java,v 1.2 2010-10-21 15:37:10 snajper Exp $
 */

package com.sun.xml.wss;

import com.sun.xml.wss.impl.MessageConstants;
import com.sun.xml.wss.logging.LogDomainConstants;

import javax.security.auth.Subject;
import java.util.logging.Level;
import java.util.logging.Logger;



/**
 * Class that can be used on the ServerSide by the SEI implementation methods, Callback Handlers
 * and Standalone SAAJ Applications using XWSS.
 */
public class SubjectAccessor {

    private static Logger log =
            Logger.getLogger(
            LogDomainConstants.WSS_API_DOMAIN,
            LogDomainConstants.WSS_API_DOMAIN_BUNDLE);


    private static ThreadLocal<Subject> wssThreadCtx = new ThreadLocal<>();

    /**
     *@return the Requester's Subject if one is available, null otherwise.
     * The subject is populated with credentials from the incoming secure message.
     * Note: the context supplied should either be a ServletEndpointContext or a
     * com.sun.xml.wss.ProcessingContext or jakarta.xml.ws.handler.MessageContext or
     * jakarta.xml.ws.WebServiceContext
     */
    public static  Subject getRequesterSubject(Object context) throws XWSSecurityException {

        if (context instanceof ProcessingContext) {
            return (Subject)((ProcessingContext)context).getExtraneousProperty(MessageConstants.AUTH_SUBJECT);
        }  else if (context instanceof jakarta.xml.ws.handler.MessageContext) {

            jakarta.xml.ws.handler.MessageContext msgContext = (jakarta.xml.ws.handler.MessageContext)context;

            Subject subject =(Subject)msgContext.get(MessageConstants.AUTH_SUBJECT);
            return subject;

        } else if ( context instanceof jakarta.xml.ws.WebServiceContext) {
            try {

                    jakarta.xml.ws.WebServiceContext wsCtx = (jakarta.xml.ws.WebServiceContext) context;
                    jakarta.xml.ws.handler.MessageContext msgContext = wsCtx.getMessageContext();
                    if (msgContext != null) {
                        Subject subject =(Subject)msgContext.get(MessageConstants.AUTH_SUBJECT);
                        return subject;
                    } else {
                        return null;
                    }

            } catch (NoClassDefFoundError | Exception ncde) {
                log.log(Level.SEVERE,
                        "WSS0761.context.not.instanceof.servletendpointcontext", ncde);
                throw new XWSSecurityException(ncde);
            }
        }
        return null;
    }

    /**
     *@return the Requester's Subject if one is available, null otherwise.The subject
     * is populated with credentials from the incoming secure message.
     * This method should be used only with synchronous Request-Response Message
     * Exchange Patterns.
     */
    public static Subject getRequesterSubject(){
        return wssThreadCtx.get();

    }

    /*
     * set the Requester's Subject into the context
     * @param sub the Requesters Subject
     * This method should be used only with synchronous Request-Response Message
     * Exchange Patterns.
     */
    public static void setRequesterSubject(Subject sub){
        wssThreadCtx.set(sub);
    }
}
