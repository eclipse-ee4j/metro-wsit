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

import com.sun.xml.ws.api.message.Attachment;
import com.sun.xml.ws.api.message.AttachmentSet;
import com.sun.xml.ws.api.message.Header;
import com.sun.xml.ws.api.message.HeaderList;
import com.sun.xml.ws.security.opt.api.NamespaceContextInfo;
import com.sun.xml.ws.security.opt.api.SecurityHeaderElement;
import com.sun.xml.ws.security.opt.impl.JAXBFilterProcessingContext;
import com.sun.xml.ws.security.opt.impl.crypto.AttachmentData;
import java.util.ArrayList;
import java.util.Iterator;
import javax.xml.crypto.Data;
import javax.xml.crypto.URIDereferencer;
import javax.xml.crypto.URIReference;
import javax.xml.crypto.URIReferenceException;
import javax.xml.crypto.XMLCryptoContext;

/**
 *
 * @author K.Venugopal@sun.com
 */
public class URIResolver implements URIDereferencer{
    private SecurityContext securityContext;
    private JAXBFilterProcessingContext pc = null;
    /** Creates a new instance of resolver */
    public URIResolver(JAXBFilterProcessingContext pc) {
        this.pc = pc;
        this.securityContext = pc.getSecurityContext();
    }

    @Override
    public Data dereference(URIReference uRIReference, XMLCryptoContext xMLCryptoContext) throws URIReferenceException {

        // FIXME: RJE -- remove cast once MessageContext supports asList(), hasHeaders()
        HeaderList headers = (HeaderList) securityContext.getNonSecurityHeaders();
        String tmpId = uRIReference.getURI();

        if(tmpId.startsWith("cid:")){
            return dereferenceAttachments(tmpId.substring(4));
        }

        String id = "";
        int index = tmpId.indexOf("#");
        if( index >=0){
            id = tmpId.substring(index+1);
        }else{
            id = tmpId;
        }
        if(headers != null && headers.size() >0){
            Iterator<Header> listItr = headers.listIterator();
            boolean found = false;
            while(listItr.hasNext()){
                GenericSecuredHeader header = (GenericSecuredHeader)listItr.next();
                if(header.hasID(id) && !header.hasEncData()){
                    return new StreamWriterData(header,((NamespaceContextInfo)header).getInscopeNSContext());
                }
            }
        }

        ArrayList pshList =  securityContext.getProcessedSecurityHeaders();
        for(int j=0; j< pshList.size() ; j++){
            SecurityHeaderElement  header = (SecurityHeaderElement) pshList.get(j);
            if(id.equals(header.getId())){
                if(header instanceof NamespaceContextInfo){
                    return new StreamWriterData(header,((NamespaceContextInfo)header).getInscopeNSContext());
                }else{
                    throw new URIReferenceException("Cannot derefernce this MessagePart and use if for any crypto operation " +
                              "as the message part is not cached");
                }
            }
        }

        // looking into buffered headers for - (Should be used only for getting the key)
        // What will happen when encrypting the content but signing the entire element? Can go wrong
        ArrayList bufList =  securityContext.getBufferedSecurityHeaders();
        for(int j=0; j< bufList.size() ; j++){
            SecurityHeaderElement  header = (SecurityHeaderElement) bufList.get(j);
            if(id.equals(header.getId())){
                if(header instanceof NamespaceContextInfo){
                    return new StreamWriterData(header,((NamespaceContextInfo)header).getInscopeNSContext());
                }else{
                    throw new URIReferenceException("Cannot derefernce this MessagePart and use if for any crypto operation " +
                              "as the message part is not cached");
                }
            }
        }

        Data data = null;
        data = (Data)pc.getSTRTransformCache().get(id);
        if(data != null)
            return data;
        data = (Data)pc.getElementCache().get(id);
        return data;
    }

    private Data dereferenceAttachments(String cidRef)  throws URIReferenceException{
        AttachmentSet attachments = securityContext.getDecryptedAttachmentSet();
        if(attachments == null || attachments.isEmpty()){
            attachments = securityContext.getAttachmentSet();
        }
        if(attachments == null || attachments.isEmpty()){
            throw new URIReferenceException ("Attachment Resource with Identifier  "+cidRef+" was not found");
        }
        Attachment attachment = attachments.get(cidRef);
        if(attachment == null){
            throw new URIReferenceException ("Attachment Resource with Identifier  "+cidRef+" was not found");
        }
        return new AttachmentData(attachment);
    }

}
