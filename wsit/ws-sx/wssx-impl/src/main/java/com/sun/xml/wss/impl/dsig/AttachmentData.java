/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * AttachmentData.java
 *
 * Created on April 7, 2005, 11:28 AM
 */

package com.sun.xml.wss.impl.dsig;

import javax.xml.crypto.Data;
import jakarta.xml.soap.AttachmentPart;

/**
 * <B>Wrapper class to be used with XWSS attachment transform 
 * provider implementation.</B>
 * @author K.Venugopal@sun.com
 */
public class AttachmentData implements Data {
    private AttachmentPart attachment = null;
    /** Creates a new instance of AttachmentData */
    public AttachmentData() {
    }
    
    /**
     *
     * @param attachment
     */    
    public void setAttachmentPart(AttachmentPart attachment){
        this.attachment = attachment;
    }
    
    /**
     *
     * @return
     */    
    public AttachmentPart getAttachmentPart(){
        return attachment;
    }
}
