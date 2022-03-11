/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.impl.c14n;

import java.io.ByteArrayOutputStream;

/**
 *
 * @author K.Venugopal@sun.com
 */
public class AttributeNS implements Cloneable , Comparable {
    private String uri;
    private String prefix;
    private boolean written = false;
    byte [] utf8Data = null;
    int code = 0;
    /** Creates a new instance of AttributeNS */
    public AttributeNS() {
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public boolean isWritten() {
        return written;
    }

    public void setWritten(boolean written) {
        this.written = written;
    }

    @Override
    public Object clone() {
        AttributeNS attrNS = new AttributeNS();
        attrNS.setPrefix(this.prefix);
        attrNS.setUri(this.uri);
        return attrNS;
    }

    public boolean equals(Object obj) {
        if(!(obj instanceof AttributeNS)){
            return false;
        }
        AttributeNS attrNS = (AttributeNS)obj;
        if(this.uri == null || this.prefix == null){
            return false;
        }
        if(this.prefix.equals(attrNS.getPrefix()) && this.uri.equals(attrNS.getUri())){
            return true;
        }
        return false;
    }

    public int hashCode(){
        if(code ==0){
            if(uri!=null){
                code =uri.hashCode();
            }
            if(prefix !=null){
                code =code+prefix.hashCode();
            }
        }
        return code;
    }

    public byte [] getUTF8Data(ByteArrayOutputStream tmpBuffer){
        if(utf8Data == null){
            try{
                BaseCanonicalizer.outputAttrToWriter("xmlns",prefix,uri,tmpBuffer);
                utf8Data = tmpBuffer.toByteArray();
            }catch(Exception ex){
                utf8Data = null;
                //should not occur
                //log
            }
        }
        return utf8Data;
    }

    @Override
    public int compareTo(Object cmp) {
        return sortNamespaces(cmp, this);
    }

    protected int sortNamespaces(Object object, Object object0) {
        AttributeNS attr = (AttributeNS)object;
        AttributeNS attr0 = (AttributeNS)object0;
        //assume namespace processing is on.
        String lN = attr.getPrefix();
        String lN0 = attr0.getPrefix();
        return lN.compareTo(lN0);
    }

    public void reset(){
        utf8Data = null;
        prefix = null;
        written = false;
        uri = null;
    }
}
