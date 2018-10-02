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
 * SignedInfo.java
 *
 * Created on January 24, 2006, 3:12 PM
 */

package com.sun.xml.ws.security.opt.crypto.dsig;

import java.io.InputStream;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.jvnet.staxex.XMLStreamReaderEx;

/**
 *
 * @author Abhijit Das
 */
@XmlRootElement(name="SignedInfo",namespace = "http://www.w3.org/2000/09/xmldsig#")
public class SignedInfo extends com.sun.xml.security.core.dsig.SignedInfoType implements javax.xml.crypto.dsig.SignedInfo {
    @XmlTransient private XMLStreamReaderEx _streamSI = null;
    @XmlTransient private byte [] canonInfo = null;
    /** Creates a new instance of SignedInfo */
    public SignedInfo() {
    }
    
    public List getReferences() {
        return reference;
    }
    
    public InputStream getCanonicalizedData() {
        return null;
    }
    
    public boolean isFeatureSupported(String string) {
        return false;
    }
    
    public SignatureMethod getSignatureMethod() {
        return signatureMethod;
    }
    
    public CanonicalizationMethod getCanonicalizationMethod() {
        return canonicalizationMethod;
    }
    
    public void setReference(List<Reference> reference) {
        this.reference = reference;
    }
    
    public byte [] getCanonicalizedSI(){
        //System.out.println("CanonSI is "+ new String(canonInfo));
        return canonInfo;
    }
    
    public void setCanonicalizedSI(byte [] info){
        this.canonInfo = info;
    }
    
    public XMLStreamReaderEx getSignedInfo() {
        return _streamSI;
    }
    
    public void setSignedInfo(XMLStreamReaderEx _streamSI) {
        this._streamSI = _streamSI;
    }
}
