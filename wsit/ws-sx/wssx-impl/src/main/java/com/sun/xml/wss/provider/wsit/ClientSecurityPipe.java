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

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.message.config.*;
import javax.security.auth.message.AuthStatus;
import jakarta.xml.ws.WebServiceException;

import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.api.pipe.Pipe;
import com.sun.xml.ws.api.pipe.PipeCloner;
import com.sun.xml.ws.api.pipe.helper.AbstractFilterPipeImpl;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.message.Message;

import com.sun.xml.ws.security.secconv.SecureConversationInitiator;
import com.sun.xml.ws.security.secconv.WSSecureConversationException;
import com.sun.xml.wss.provider.wsit.logging.LogDomainConstants;
import com.sun.xml.wss.provider.wsit.logging.LogStringsMessages;
import jakarta.xml.bind.JAXBElement;

/**
 * This pipe is used to do client side security for app server
 */
public class ClientSecurityPipe extends AbstractFilterPipeImpl
    implements SecureConversationInitiator {

    private static final String WSIT_CLIENT_AUTH_CONTEXT="com.sun.xml.wss.provider.wsit.WSITClientAuthContext";
    protected PipeHelper helper;
   
    protected static final Logger log =
            Logger.getLogger(
            LogDomainConstants.WSIT_PVD_DOMAIN,
            LogDomainConstants.WSIT_PVD_DOMAIN_BUNDLE);

    public ClientSecurityPipe(Map<Object, Object> props, Pipe next) {

        super(next);
	props.put(PipeConstants.SECURITY_PIPE,this);

        WSDLPort wsdlModel = (WSDLPort)props.get(PipeConstants.WSDL_MODEL);
        if (wsdlModel != null) {
            props.put(PipeConstants.WSDL_SERVICE,
                wsdlModel.getOwner().getName());
        }
	this.helper = new PipeHelper(PipeConstants.SOAP_LAYER,props,null);

    }

    protected ClientSecurityPipe(ClientSecurityPipe that, PipeCloner cloner) {
        super(that, cloner);
        this.helper = that.helper;
    }
		       
    @Override
    public void preDestroy() {
        //Give the AuthContext a chance to cleanup 
        //create a dummy request packet
        try {
            Packet request = new Packet();
            PacketMessageInfo info = new PacketMapMessageInfo(request, new Packet());
            Subject subj = getClientSubject(request);
            ClientAuthContext cAC = helper.getClientAuthContext(info, subj);
            if (cAC != null && WSIT_CLIENT_AUTH_CONTEXT.equals(cAC.getClass().getName())) {
                cAC.cleanSubject(info, subj);
            }
        } catch (Exception ex) {
        //ignore exceptions
        }
        helper.disable();
    }    
    
    public final Pipe copy(PipeCloner cloner) {
        return new ClientSecurityPipe(this, cloner);
    }
    
    public PipeHelper getPipeHelper() {
        return helper;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Packet process(Packet request) {

	/*
	 * XXX should there be code like the following?
	 if(isHttpBinding) {
	     return next.process(request);
	 }
        */

	PacketMessageInfo info= new PacketMapMessageInfo(request,new Packet());
        
        info.getMap().put(jakarta.xml.ws.Endpoint.WSDL_SERVICE,
            helper.getProperty(PipeConstants.WSDL_SERVICE));

        AuthStatus status = AuthStatus.SEND_SUCCESS;

	Subject clientSubject = getClientSubject(request);

	ClientAuthContext cAC = null;

	try {

	    cAC = helper.getClientAuthContext(info,clientSubject);

	    if (cAC != null) {

		// proceed to process message sescurity
		status = cAC.secureRequest(info, clientSubject);
	    }

	} catch(Exception e) {

	    log.log(Level.SEVERE,LogStringsMessages.WSITPVD_0058_ERROR_SECURE_REQUEST(), e);
	    
	    throw new WebServiceException(
		  "Cannot secure request",e);
	} 

	Packet response = null;

	if (status == AuthStatus.FAILURE) {
	    if (log.isLoggable(Level.FINE)) {
		log.log(Level.FINE,"ws.status_secure_request", status);
	    }
	    response = info.getResponsePacket();
	} else {
	    response = processSecureRequest(info,cAC,clientSubject);
	}

	// may return a security fault even if the MEP was one-way
        return response;
    }    
	
    private Packet processSecureRequest(PacketMessageInfo info, 
	ClientAuthContext cAC, Subject clientSubject) 
	throws WebServiceException {
        
	// send the request
	Packet response = next.process(info.getRequestPacket());
	
	// check for response
	Message m = response.getMessage();

	if (m != null) {

	    if (cAC != null) {
		
		AuthStatus status = AuthStatus.SUCCESS;

		info.setResponsePacket(response);
		
		try {

		    status = cAC.validateResponse(info,clientSubject,null);

		} catch (Exception e) {

		    throw new WebServiceException
			 ("Cannot validate response for {0}",e);
		}

		if (status == AuthStatus.SEND_CONTINUE) {
		    response = processSecureRequest(info, cAC, clientSubject);
		} else {
		    response = info.getResponsePacket();
		} 
	    }
	}

	return response;
    }

    private Subject getClientSubject(Packet p) {

	Subject s = null;
	if (p != null) {
	    s = (Subject) 
		p.invocationProperties.get(PipeConstants.CLIENT_SUBJECT);
	}
	if (s == null) {
	    s = helper.getClientSubject();
            if (p != null) {
	        p.invocationProperties.put(PipeConstants.CLIENT_SUBJECT,s);
            }
	}
	
	return s;
    }
			
    public JAXBElement startSecureConversation(Packet packet) 
            throws WSSecureConversationException {

	PacketMessageInfo info = new PacketMapMessageInfo(packet,new Packet());
	JAXBElement token = null;

	try {

	    // gets the subject from the packet (puts one there if not found)
	    Subject clientSubject = getClientSubject(packet);

	    // put MessageInfo in properties map, since MessageInfo 
	    // is not passed to getAuthContext, key idicates function
	    HashMap<Object, Object> map = new HashMap<Object, Object>();
	    map.put(PipeConstants.SECURITY_TOKEN,info);

	    helper.getSessionToken(map,info,clientSubject);

	    // helper returns token in map of msgInfo, using same key
	    Object o = info.getMap().get(PipeConstants.SECURITY_TOKEN);

	    if (o != null && o instanceof JAXBElement) {
		token = (JAXBElement) o;
	    }

	} catch(Exception e) {

	    if (e instanceof WSSecureConversationException) {
		throw (WSSecureConversationException) e;
	    } else {
		throw new WSSecureConversationException
		    ("Secure Conversation failure: ", e);
	    }
	} 

	return token;
    }
}












