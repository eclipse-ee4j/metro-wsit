/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.opt.impl.util;

import org.glassfish.jaxb.runtime.marshaller.NamespacePrefixMapper;
import com.sun.xml.wss.impl.MessageConstants;
/**
 *
 * @author K.Venugopal@sun.com
 */
public class WSSNamespacePrefixMapper extends NamespacePrefixMapper{
    
    private boolean soap12 = false;
    /** Creates a new instance of NamespacePrefixMapper */
    public WSSNamespacePrefixMapper() {
    }
    
    /** Creates a new instance of NamespacePrefixMapper */
    public WSSNamespacePrefixMapper(boolean soap12) {
        this.soap12 = soap12;
    }
    
    public String getPreferredPrefix(String namespaceUri, String suggestion, boolean requirePrefix) {
        if(MessageConstants.WSSE_NS.equals(namespaceUri)){
            return MessageConstants.WSSE_PREFIX;
        }
        
        if(MessageConstants.WSSE11_NS.equals(namespaceUri)){
            return MessageConstants.WSSE11_PREFIX;
        }
        if(MessageConstants.XENC_NS.equals(namespaceUri)){
            return MessageConstants.XENC_PREFIX;
        }
        if(MessageConstants.DSIG_NS.equals(namespaceUri)){
            return MessageConstants.DSIG_PREFIX;
        }
        if(MessageConstants.WSU_NS.equals(namespaceUri)){
            return MessageConstants.WSU_PREFIX;
        }
        if(MessageConstants.WSSC_NS.equals(namespaceUri)){
            return MessageConstants.WSSC_PREFIX;
        }
        if("http://www.w3.org/2001/10/xml-exc-c14n#".equals(namespaceUri)){
            return "exc14n";
        }
        if(MessageConstants.SOAP_1_1_NS.equals(namespaceUri)){
            return "S";
        }
        
        if(MessageConstants.SOAP_1_2_NS.equals(namespaceUri)){
            return "S";
        }
        if("http://www.w3.org/2001/XMLSchema-instance".equals(namespaceUri)){
            return "xsi";
        }
        return null;
    }
    
    public String[] getPreDeclaredNamespaceUris() {
        return new String[] { };
    }
    
    public String[] getContextualNamespaceDecls() {
        if(!soap12){
            return new String[] {MessageConstants.WSSE_PREFIX, MessageConstants.WSSE_NS,MessageConstants.WSSE11_PREFIX,
            MessageConstants.WSSE11_NS,MessageConstants.XENC_PREFIX,MessageConstants.XENC_NS,MessageConstants.DSIG_PREFIX,MessageConstants.DSIG_NS,
            MessageConstants.WSU_PREFIX,MessageConstants.WSU_NS, MessageConstants.WSSC_PREFIX,MessageConstants.WSSC_NS,"exc14n","http://www.w3.org/2001/10/xml-exc-c14n#",
            "S",MessageConstants.SOAP_1_1_NS};
        }else{
            return new String[] {MessageConstants.WSSE_PREFIX, MessageConstants.WSSE_NS,MessageConstants.WSSE11_PREFIX,
            MessageConstants.WSSE11_NS,MessageConstants.XENC_PREFIX,MessageConstants.XENC_NS,MessageConstants.DSIG_PREFIX,MessageConstants.DSIG_NS,
            MessageConstants.WSU_PREFIX,MessageConstants.WSU_NS, MessageConstants.WSSC_PREFIX,MessageConstants.WSSC_NS,"exc14n","http://www.w3.org/2001/10/xml-exc-c14n#",
            "S",MessageConstants.SOAP_1_2_NS};
        }
    }
    
}
