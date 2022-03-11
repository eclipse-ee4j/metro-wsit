/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.opt.impl.crypto;

import com.sun.xml.ws.api.message.Attachment;
import java.io.IOException;
import java.io.OutputStream;
import javax.xml.crypto.Data;

/**
 *  An implementation of Data type containing a JAX-WS attachment
 *
 * @author ashutosh.shahi@sun.com
 */
public class AttachmentData implements Data{

    private Attachment attachment = null;

    public AttachmentData(Attachment attachment){
        this.attachment = attachment;
    }

    public void write(OutputStream os) throws IOException{
        attachment.writeTo(os);
    }

    public Attachment getAttachment(){
        return attachment;
    }
}
