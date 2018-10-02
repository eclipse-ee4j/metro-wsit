/*
 * Copyright (c) 2006, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.kerb;

import org.ietf.jgss.*;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import sun.security.krb5.*;

class AcceptSecContextToken extends InitialToken {

    private KrbApRep apRep = null;

    /**
     * Creates an AcceptSecContextToken for the context acceptor to send to
     * the context initiator.
     */
    public AcceptSecContextToken(Krb5Context context,
				 KrbApReq apReq)
	throws KrbException, IOException {
	
	/*
	 * RFC 1964, section 1.2 states:
	 *  (1) context key: uses Kerberos session key (or subkey, if
	 *  present in authenticator emitted by context initiator) directly
	 *
	 * This does not mention context acceptor. Hence we will not
	 * generate a subkey on the acceptor side. Note: Our initiator will
	 * still allow another acceptor to generate a subkey, even though
	 * our acceptor does not do so.
	 */
	boolean useSubkey = false;

	boolean useSequenceNumber = true;
	
	apRep = new KrbApRep(apReq, useSequenceNumber, useSubkey);
	
	context.resetMySequenceNumber(apRep.getSeqNumber().intValue());

	/*
	 * Note: The acceptor side context key was set when the
	 * InitSecContextToken was received.
	 */
    }

    /**
     * Creates an AcceptSecContextToken at the context initiator's side
     * using the bytes received from  the acceptor.
     */
    public AcceptSecContextToken(Krb5Context context, 
				 Credentials serviceCreds, KrbApReq apReq, 
				 InputStream is)
	throws IOException, GSSException, KrbException  {

	int tokenId = ((is.read()<<8) | is.read());

	if (tokenId != Krb5Token.AP_REP_ID)
	    throw new GSSException(GSSException.DEFECTIVE_TOKEN, -1,
				   "AP_REP token id does not match!");

	byte[] apRepBytes = 
	    new sun.security.util.DerValue(is).toByteArray();
	
	KrbApRep apRep = new KrbApRep(apRepBytes, serviceCreds, apReq);

	/*
	 * Allow the context acceptor to set a subkey if desired, even
	 * though our context acceptor will not do so.
	 */
	EncryptionKey subKey = apRep.getSubKey();
	if (subKey != null) {
	    context.setKey(subKey);
	    /*
	    System.out.println("\n\nSub-Session key from AP-REP is: " +
			       getHexBytes(subKey.getBytes()) + "\n");
	    */
	}

        Integer apRepSeqNumber = apRep.getSeqNumber();  
        int peerSeqNumber = (apRepSeqNumber != null ?   
                             apRepSeqNumber.intValue() :  
                             0); 
        context.resetPeerSequenceNumber(peerSeqNumber); 
    }

    public final byte[] encode() throws IOException {
	byte[] apRepBytes = apRep.getMessage();
	byte[] retVal = new byte[2 + apRepBytes.length];
	writeInt(Krb5Token.AP_REP_ID, retVal, 0);
	System.arraycopy(apRepBytes, 0, retVal, 2, apRepBytes.length);
	return retVal;
    }
}
