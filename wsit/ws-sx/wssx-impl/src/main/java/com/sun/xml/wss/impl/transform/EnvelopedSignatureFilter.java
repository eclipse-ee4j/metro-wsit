/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.impl.transform;

import javax.xml.stream.StreamFilter;
import javax.xml.stream.XMLStreamReader;


/**
 *
 * @author K.Venugopal@sun.com
 */
public class EnvelopedSignatureFilter implements StreamFilter{
    
    private static final String _SIGNATURE = "Signature";
    private static final String _NAMESPACE_URI ="http://www.w3.org/2000/09/xmldsig#";
    
    private boolean _skipSignatureElement = false;
    private boolean _skipDone= false;
    /** Creates a new instance of EnvelopedSignatureTransformImpl */
    public EnvelopedSignatureFilter(){
        
    }
    
    public boolean accept(XMLStreamReader reader) {
        if(_skipDone){
            return false;
        }else if(!_skipSignatureElement){
            if(reader.getEventType() == XMLStreamReader.START_ELEMENT ){
                if(_SIGNATURE.equals(reader.getLocalName()) && _NAMESPACE_URI.equals(reader.getNamespaceURI()) ){
                    this._skipSignatureElement = true;
                    return true;
                }
            }
        }else{
            if(reader.getEventType() == XMLStreamReader.END_ELEMENT){
                if(_SIGNATURE.equals(reader.getLocalName()) && _NAMESPACE_URI.equals(reader.getNamespaceURI()) ){
                    this._skipSignatureElement = false;
                    this._skipDone = true;
                    return true;
                }
            }
            return true;
        }
        return false;
    }
    
}
