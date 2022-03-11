/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * DataWrapper.java
 *
 * Created on May 2, 2005, 9:43 AM
 */

package com.sun.xml.wss.impl.dsig;

import com.sun.xml.wss.impl.MessageConstants;
import com.sun.xml.wss.impl.policy.mls.SignatureTarget;
import javax.xml.crypto.Data;
import javax.xml.crypto.NodeSetData;
import javax.xml.crypto.OctetStreamData;

/**
 * Wrapper class for JSR 105 Data objects.Caches SignatureTarget
 * object and data resolved using this signature target.Reduces
 * the burden of instanceof checks.
 * @author K.Venugopal@sun.com
 */
public class DataWrapper{

    private Data data = null;
    private int type = -1;
    private SignatureTarget signatureTarget = null;

    /**
     *
     */
    DataWrapper(Data data){
        this.data = data;
        if(data instanceof AttachmentData){
            type = MessageConstants.ATTACHMENT_DATA;
        }else if (data instanceof NodeSetData){
            type = MessageConstants.NODE_SET_DATA;
        }else if(data instanceof OctetStreamData){
            type = MessageConstants.OCTECT_STREAM_DATA;
        }

    }

    /**
     *
     * @return Data object.
     */
    public Data getData(){
        return this.data;
    }

    /**
     *
     * @return type of data object wrapped.
     */
    public int getType(){
        return type;
    }

    /**
     *
     * @return if Data is AttachmentData
     */
    public boolean isAttachmentData(){
        if(type ==MessageConstants.ATTACHMENT_DATA ){
            return true;
        }else{
            return false;
        }
    }

    /**
     *
     * @return true if Data is NodeSetData.
     */
    public boolean isNodesetData(){
        if(type == MessageConstants.NODE_SET_DATA ){
            return true;
        }else{
            return false;
        }
    }

    /**
     *
     * @return true if Data is OctetStreamData.
     */
    public boolean isOctectData(){
        if(type == MessageConstants.OCTECT_STREAM_DATA ){
            return true;
        }else{
            return false;
        }
    }

    /**
     * null if no target has been set.
     */
    public SignatureTarget getTarget(){
        return signatureTarget;
    }

    /**
     *
     */
    public void setTarget(SignatureTarget target){
        this.signatureTarget = target;
    }
}
