/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.opt.impl.incoming;

import com.sun.xml.ws.api.message.AttachmentSet;
import com.sun.xml.ws.api.message.MessageHeaders;
import com.sun.xml.ws.security.opt.impl.attachment.AttachmentSetImpl;
import com.sun.xml.wss.ProcessingContext;
import com.sun.xml.wss.impl.policy.MLSPolicy;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author K.Venugopal@sun.com
 */
public class SecurityContext {

    private ArrayList processedSecurityHeaders = new ArrayList(2);
    private ArrayList bufferedSecurityHeaders = null;
    private MessageHeaders nonSecurityHeaders = null;
    private HashMap<String,String> shND = null;
    private HashMap<String,String> envND = null;
    private AttachmentSet attachments = null;
    private AttachmentSet decryptedAttachments = null;

    private MLSPolicy inferredKB = null;

    private ProcessingContext pc = null;

    private boolean isSAMLKB = false;

    /** Creates a new instance of SecurityContext */
    public SecurityContext() {

    }

    public void setAttachmentSet(AttachmentSet attachments){
        this.attachments = attachments;
    }

    public AttachmentSet getAttachmentSet(){
        if(attachments == null){
            attachments = new AttachmentSetImpl();
        }
        return attachments;
    }

    public AttachmentSet getDecryptedAttachmentSet(){
        if(decryptedAttachments == null){
            decryptedAttachments = new AttachmentSetImpl();
        }
        return decryptedAttachments;
    }

    public MLSPolicy getInferredKB(){
        return inferredKB;
    }

    public void setInferredKB(MLSPolicy inferredKB){
        this.inferredKB = inferredKB;
    }

    public void setProcessedSecurityHeaders(ArrayList headers){
        this.processedSecurityHeaders =  headers;
    }

    public ArrayList getProcessedSecurityHeaders(){
        return processedSecurityHeaders;
    }

    public void setBufferedSecurityHeaders(ArrayList headers){
        this.bufferedSecurityHeaders =  headers;
    }

    public ArrayList getBufferedSecurityHeaders(){
        return bufferedSecurityHeaders;
    }

    public MessageHeaders getNonSecurityHeaders(){
        return nonSecurityHeaders;
    }

    public void setNonSecurityHeaders(MessageHeaders list){
        this.nonSecurityHeaders = list;
    }

    public void setSecurityHdrNSDecls(HashMap<String,String> nsDecls){
        this.shND = nsDecls;
    }

    public HashMap<String,String> getSecurityHdrNSDecls(){
        return this.shND;
    }

    public void setSOAPEnvelopeNSDecls(HashMap<String,String> nsDecls){
        this.envND = nsDecls;
    }

    public HashMap<String,String> getSOAPEnvelopeNSDecls(){
        return envND;
    }



    public ProcessingContext getProcessingContext() {
        return pc;
    }

    public void setProcessingContext(ProcessingContext pc){
        this.pc = pc;
    }

    public void setIsSAMLKeyBinding(boolean flag) {
        this.isSAMLKB = flag;
    }

    public boolean getIsSAMLKeyBinding() {
       return this.isSAMLKB;
    }
}
