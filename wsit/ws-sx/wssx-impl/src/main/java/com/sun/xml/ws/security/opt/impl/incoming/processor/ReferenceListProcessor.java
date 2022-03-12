/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.opt.impl.incoming.processor;

import com.sun.xml.ws.security.opt.impl.util.StreamUtil;
import com.sun.xml.wss.impl.MessageConstants;
import com.sun.xml.wss.impl.policy.mls.EncryptionPolicy;
import com.sun.xml.wss.impl.policy.mls.EncryptionTarget;
import com.sun.xml.wss.impl.policy.mls.Target;
import java.util.ArrayList;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 *
 * @author K.Venugopal@sun.com
 */
public class ReferenceListProcessor {

    ArrayList<String> refList = null;
    //EncryptionPolicy encPolicy = null;
    EncryptionPolicy.FeatureBinding fb = null;
    /** Creates a new instance of ReferenceListProcessor */
    public ReferenceListProcessor(EncryptionPolicy encPolicy) {
        //this.encPolicy = encPolicy;
        fb = (EncryptionPolicy.FeatureBinding) encPolicy.getFeatureBinding();
    }
    /**
     * processes the ReferenceList and sets the refList member
     * @param reader XMLStreamReader
     */
    public void process(XMLStreamReader reader) throws XMLStreamException{
        refList = new ArrayList<>(2);
        if(StreamUtil.moveToNextStartOREndElement(reader)){
            while(reader.getEventType() != XMLStreamConstants.END_DOCUMENT){
                if(reader.getEventType() == XMLStreamConstants.START_ELEMENT){
                    if(reader.getLocalName() == "DataReference" && reader.getNamespaceURI() == MessageConstants.XENC_NS){
                        String uri = reader.getAttributeValue(null,"URI");
                        if(uri.startsWith("#")){
                            refList.add(uri.substring(1));
                        }else{
                            refList.add(uri);
                        }
                        // for policy creation
                        Target target = new Target(Target.TARGET_TYPE_VALUE_URI, uri);
                        EncryptionTarget encTarget = new EncryptionTarget(target);
                        fb.addTargetBinding(encTarget);
                    }
                }
                if(_exit(reader)){
                    break;
                }
                reader.next();

                if(_exit(reader)){
                    break;
                }
            }
        }
    }

    public ArrayList<String> getReferences(){
        return refList;
    }

    public boolean _exit(XMLStreamReader reader){
        if(reader.getEventType() == XMLStreamConstants.END_ELEMENT){
            return reader.getLocalName() == "ReferenceList" && reader.getNamespaceURI() == MessageConstants.XENC_NS;
        }
        return false;
    }
}
