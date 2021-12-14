/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.rm.runtime;

import com.oracle.webservices.oracle_internal_api.rm.RMRetryException;
import com.sun.xml.ws.api.server.WSEndpoint;
import com.sun.istack.logging.Logger;
import com.sun.xml.ws.client.ClientTransportException;
import com.sun.xml.ws.rx.RxException;
import com.sun.xml.ws.rx.rm.localization.LocalizationMessages;
//
import com.sun.xml.ws.runtime.dev.Session;
import com.sun.xml.ws.runtime.dev.SessionManager;
import java.io.IOException;
import jakarta.xml.ws.WebServiceException;

/**
 * The non-instantiable utility class containing various common static utility methods 
 * used for runtime processing.
 * 
 * @author Marek Potociar (marek.potociar at sun.com)
 */
final class Utilities {

    private static final Logger LOGGER = Logger.getLogger(Utilities.class);

    /**
     * Non-instantiable constructor
     */
    private Utilities() {
        // nothing to do
    }

    /**
     * Checks whether the actual sequence identifier value equals to the expected value
     * and throws a logged exception if th check fails.
     * 
     * @param expected expected sequence identifier value
     * @param actual actual sequence identifier value
     * @throws java.lang.IllegalStateException if actual value does not equal to the expected value
     */
    static void assertSequenceId(String expected, String actual) throws IllegalStateException {
        if (expected != null && !expected.equals(actual)) {
            throw LOGGER.logSevereException(new IllegalStateException(LocalizationMessages.WSRM_1105_SEQUENCE_ID_NOT_RECOGNIZED(actual, expected)));
        }
    }

    static String extractSecurityContextTokenId(com.sun.xml.ws.security.secext10.SecurityTokenReferenceType strType) throws RxException {
        com.sun.xml.ws.security.trust.elements.str.Reference strReference = com.sun.xml.ws.security.trust.WSTrustElementFactory.newInstance().createSecurityTokenReference(
                new com.sun.xml.ws.security.secext10.ObjectFactory().createSecurityTokenReference(strType)).getReference();
        if (!(strReference instanceof com.sun.xml.ws.security.trust.elements.str.DirectReference)) {
            throw LOGGER.logSevereException(
                    new RxException(LocalizationMessages.WSRM_1132_SECURITY_REFERENCE_ERROR(strReference.getClass().getName())));
        }
        return ((com.sun.xml.ws.security.trust.elements.str.DirectReference) strReference).getURIAttr().toString();
    }

    /**
     * Either creates a new <code>Session</code> for the
     * <code>InboundSequence</code> or returns one that has
     * already been created by the SC Pipe.
     *
     * @param endpoint endpoint instance
     * @param sessionId session identifier
     * @return The Session
     */
    static Session startSession(WSEndpoint endpoint, String sessionId) {
        SessionManager manager = SessionManager.getSessionManager(endpoint,null);
        Session session = manager.getSession(sessionId);
        if (session == null) {
            session = manager.createSession(sessionId);
        }

        return session;
    }

    /**
     * Terminates the session associated with the sequence if
     * RM owns the lifetime of the session, i.e. if SC is not present.
     *
     * @param endpoint endpoint instance
     * @param sessionId session identifier
     */
    static void endSessionIfExists(WSEndpoint endpoint, String sessionId) {
        SessionManager manager = SessionManager.getSessionManager(endpoint,null);
        if (manager.getSession(sessionId) != null) {
            manager.terminateSession(sessionId);
        }
    }

    /**
     * Based on the parameter, this utility method determines whether or not
     * it makes sense to try resending the message.
     *
     * @return {@code true} if this exception seems to be related to a connection
     *         problem.
     */
    static boolean isResendPossible(Throwable throwable) {
        if (throwable instanceof RMRetryException || throwable instanceof IOException) {
            return true;
        } else if (throwable instanceof WebServiceException) {
            if (throwable instanceof ClientTransportException) {
                return true; // if endpoint went down, let's try to resend, as it may come up again
            }
            // Unwrap exception and see if it makes sense to retry this request
            // (no need to check for null - handled by instanceof)
            if (throwable.getCause() instanceof RMRetryException ||
                    throwable.getCause() instanceof IOException) {
                return true;
            }
        }
        return false;
    }
}
