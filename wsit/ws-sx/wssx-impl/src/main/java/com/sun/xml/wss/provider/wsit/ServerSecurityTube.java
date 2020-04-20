/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.provider.wsit;

import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.pipe.NextAction;
import com.sun.xml.ws.api.pipe.Tube;
import com.sun.xml.ws.api.pipe.TubeCloner;
import com.sun.xml.ws.api.pipe.helper.AbstractFilterTubeImpl;
import com.sun.xml.ws.api.pipe.helper.AbstractTubeImpl;
import com.sun.xml.ws.api.server.WSEndpoint;
import com.sun.xml.wss.NonceManager;
import com.sun.xml.wss.provider.wsit.logging.LogDomainConstants;

import com.sun.xml.wss.provider.wsit.logging.LogStringsMessages;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.Subject;
import javax.security.auth.message.AuthException;
import javax.security.auth.message.AuthStatus;
import javax.security.auth.message.config.ServerAuthContext;
import jakarta.xml.ws.WebServiceException;

/**
 *
 */
public class ServerSecurityTube extends AbstractFilterTubeImpl {

    protected static final Logger logger =
            Logger.getLogger(
            LogDomainConstants.WSIT_PVD_DOMAIN,
            LogDomainConstants.WSIT_PVD_DOMAIN_BUNDLE);
    private final boolean isHttpBinding;
    private PipeHelper helper;
    private AuthStatus status = AuthStatus.SEND_SUCCESS;
    private ServerAuthContext sAC = null;
    private PacketMessageInfo info = null;
    private WSEndpoint wsEndpoint = null;
    @SuppressWarnings("unchecked")
    public ServerSecurityTube(Map<Object, Object> props, final Tube next, boolean isHttpBinding) {
        super(next);
        props.put(PipeConstants.SECURITY_PIPE, this);
        this.helper = new PipeHelper(PipeConstants.SOAP_LAYER, props, null);
        this.isHttpBinding = isHttpBinding;
        this.wsEndpoint = (WSEndpoint) props.get(PipeConstants.ENDPOINT);

        //Registers IdentityComponent if either cs is not null        
        
    }

    protected ServerSecurityTube(ServerSecurityTube that, TubeCloner cloner) {

        super(that, cloner);
        // we can share the helper for all pipes so that the remove 
        // registration (in server side) can be done properly
        this.helper = that.helper;
        this.isHttpBinding = that.isHttpBinding;
    }

    @Override
    public AbstractTubeImpl copy(TubeCloner cloner) {
        return new ServerSecurityTube(this, cloner);
    }

    private Subject getClientSubject(Packet p) {
        Subject s = null;
        if (p != null) {
            s = (Subject) p.invocationProperties.get(PipeConstants.CLIENT_SUBJECT);
        }
        if (s == null) {
            s = helper.getClientSubject();
            if (p != null) {
                p.invocationProperties.put(PipeConstants.CLIENT_SUBJECT, s);
            }
        }
        return s;
    }

    @Override
    @SuppressWarnings("unchecked")
    public NextAction processRequest(Packet request) {

        info = new PacketMapMessageInfo(request, new Packet());
        // XXX at this time, we expect the server subject to be null
        Subject serverSubject = (Subject) request.invocationProperties.get(PipeConstants.SERVER_SUBJECT);
        //could change the request packet
        Packet validatedRequest = null;
        Subject clientSubject = getClientSubject(request);
        try {
            sAC = helper.getServerAuthContext(info, serverSubject);
            if (sAC != null) {
                // client subject must not be null
                // and when return status is SUCCESS, module
                // must have called handler.handle(CallerPrincipalCallback)
                status = sAC.validateRequest(info, clientSubject, serverSubject);
                validatedRequest = info.getRequestPacket();
            } else {
                //throw new WebServiceException("Internal Error : Null ServerAuthContext");
                //could be MEX case here
	        validatedRequest = info.getRequestPacket();
                //explicitly set status to SUCCESS here so we can send the request 
                //could be a MEX endpoint for which SAC is null!.
                status = AuthStatus.SUCCESS;
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, LogStringsMessages.WSITPVD_0053_ERROR_VALIDATE_REQUEST(), e);
            WebServiceException wse = new WebServiceException("Cannot validate request for", e);
            //set status for audit
            status = AuthStatus.SEND_FAILURE;
            // if unable to determine if two-way will return empty response
            validatedRequest = helper.getFaultResponse(info.getRequestPacket(), info.getResponsePacket(), wse);
            return doReturnWith(validatedRequest);
        }

        if (status == AuthStatus.SUCCESS) {
            // only do doAdPriv if SecurityManager is in effect
//            boolean authorized = true;
//            if (authorized) {
            helper.authorize(validatedRequest);
            if (System.getSecurityManager() == null) {
                return doInvoke(super.next, validatedRequest);
            } else {
                final Tube nextTube = super.next;
                final Packet valRequest = validatedRequest;                
                try {
                    return (NextAction) Subject.doAsPrivileged(clientSubject, new PrivilegedExceptionAction() {

                        public Object run() throws Exception {
                            // proceed to invoke the endpoint
                            return doInvoke(nextTube, valRequest);
                        }
                        }, null);
                } catch (PrivilegedActionException pae) {
                    Throwable cause = pae.getCause();
                    if (cause instanceof AuthException) {
                        logger.log(Level.SEVERE, LogStringsMessages.WSITPVD_0055_WS_ERROR_NEXT_PIPE(), cause);
                    }
                    Packet response = helper.getFaultResponse(validatedRequest, info.getResponsePacket(), cause);
                    return doReturnWith(response);
                }
            }

        } else {
            // validateRequest did not return SUCCESS but it returned SEND_SUCCESS or
            // SEND_FAILURE so we do not invoke the ENDPOINT.
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "ws.status_validate_request", status);
            }
            // even for one-way mep, may return response with non-empty message
            Packet response = info.getResponsePacket();
            return doReturnWith(response);
        }
    }

    @Override
    public NextAction processResponse(Packet response) {
        // XXX at this time, we expect the server subject to be null
        Subject serverSubject = (Subject) response.invocationProperties.get(PipeConstants.SERVER_SUBJECT);
        // secure response, including if it is a fault
        if (sAC != null && response.getMessage() != null) {
            try {
                info.setResponsePacket(response);
                response = processResponse(info, sAC, serverSubject);
            } catch (Exception ex) {
                logger.log(Level.SEVERE, LogStringsMessages.WSITPVD_0057_ERROR_PROCESS_RESPONSE(), ex);
            }
        }

        return doReturnWith(response);
    }

    @Override
    public NextAction processException(Throwable t) {
        if (!(t instanceof WebServiceException)) {
            t = new WebServiceException(t);
        }
        return doThrow(t);
    }

    // called when secureResponse is to be called 
    private Packet processResponse(PacketMessageInfo info,
            ServerAuthContext sAC,
            Subject serverSubject) {
        AuthStatus stat;
        try {
            stat = sAC.secureResponse(info, serverSubject);
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "ws.status_secure_response", stat);
            }
            if (AuthStatus.SEND_FAILURE == stat) {
                return helper.makeFaultResponse(info.getResponsePacket(), new Exception("Error in Securing Response"));
            }
            return info.getResponsePacket();
        } catch (Exception e) {
            if (e instanceof AuthException) {
                if (logger.isLoggable(Level.INFO)) {
                    logger.log(Level.INFO, "ws.error_secure_response", e);
                }
            } else {
                logger.log(Level.SEVERE, LogStringsMessages.WSITPVD_0054_ERROR_SECURE_RESPONSE(), e);
            }
            return helper.makeFaultResponse(info.getResponsePacket(), e);
        }
    }

    /**
     * This method is called once in server side and at most one in client side.
     */
    public void preDestroy() {
        helper.disable();
        /**
        Fix for bug 3932/4052
         */
        next.preDestroy();
        NonceManager.deleteInstance(wsEndpoint);
    }
}
