/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.saml.util;

import org.glassfish.jaxb.runtime.marshaller.NamespacePrefixMapper;
import com.sun.xml.wss.impl.MessageConstants;
/**
 *
 * @author K.Venugopal@sun.com
 */
public class WSSNamespacePrefixMapper extends NamespacePrefixMapper{
    private boolean soap12;
    
    /** Creates a new instance of NamespacePrefixMapper */
    public WSSNamespacePrefixMapper() {
    }
    public WSSNamespacePrefixMapper(boolean soap12) {
        this.soap12 = soap12;
    }
    
    @Override
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
        if(MessageConstants.SAML_v1_1_NS.equals(namespaceUri)){
            return MessageConstants.SAML_PREFIX;
        }
        if(MessageConstants.SAML_v2_0_NS.equals(namespaceUri)){
            return MessageConstants.SAML2_PREFIX;
        }
        if("http://www.w3.org/2001/10/xml-exc-c14n#".equals(namespaceUri)){
            return "exc14n";
        }
      
        return null;
    }
    
    @Override
    public String[] getPreDeclaredNamespaceUris() {
        return new String[] { };
    }

    
}
