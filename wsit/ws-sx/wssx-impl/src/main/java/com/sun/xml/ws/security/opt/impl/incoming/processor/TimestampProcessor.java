/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.opt.impl.incoming.processor;

import com.sun.xml.ws.security.opt.impl.JAXBFilterProcessingContext;
import com.sun.xml.wss.BasicSecurityProfile;
import javax.xml.namespace.QName;
import javax.xml.stream.StreamFilter;
import javax.xml.stream.XMLStreamReader;

/**
 *
 * @author Ashutosh.Shahi@sun.com
 */
public class TimestampProcessor implements StreamFilter{
    
    private String created = null;
    private String expires = null;
    private String currentElement = "";
    private JAXBFilterProcessingContext context = null;
    private static String EXPIRES = "Expires".intern();
    private static String CREATED = "Created".intern();
    /** Creates a new instance of TimestampProcessor */
    public TimestampProcessor(JAXBFilterProcessingContext ctx) {
        this.context = ctx;
    }
    /**
     * parses the  time stamp and sets different members of this class
     * @param reader XMLStreamReader
     * @return boolean
     */
    public boolean accept(XMLStreamReader reader) {
        if(reader.getEventType() == XMLStreamReader.START_ELEMENT){
            if("Created".equals(reader.getLocalName())){
                currentElement = CREATED;
                if(context.isBSP() && created != null){
                    BasicSecurityProfile.log_bsp_3203();
                }
                if(context.isBSP() && hasValueType(reader)){
                    BasicSecurityProfile.log_bsp_3225();
                }
                
            } else if("Expires".equals(reader.getLocalName())){
                if(context.isBSP() && expires != null){
                    BasicSecurityProfile.log_bsp_3224();
                }
                if(context.isBSP() && created == null){
                    BasicSecurityProfile.log_bsp_3221();
                }
                
                if(context.isBSP() && hasValueType(reader)){
                    BasicSecurityProfile.log_bsp_3226();
                }
                currentElement = EXPIRES;
            }else{
                //throw Unsupportedexception                
//                if(context.isBSP() && ! "Timestamp".equals(reader.getLocalName())){
//                    BasicSecurityProfile.log_bsp_3222(reader.getLocalName());
//                }
            }
        }
        
        if(reader.getEventType() == XMLStreamReader.CHARACTERS){
            if(currentElement == CREATED){
                created = reader.getText();
                currentElement = "";
            }else if(currentElement == EXPIRES){
                expires = reader.getText();
                currentElement = "";
            }
        }
        return true;
    }
    
    public String getCreated(){
        return created;
    }
    
    public String getExpires(){
        return expires;
    }
    
    
    private boolean hasValueType(XMLStreamReader reader){
        for(int i=0;i<reader.getAttributeCount();i++){
            QName name = reader.getAttributeName(i);
            if(name != null && "ValueType".equals(name.getLocalPart())){
                return true;
            }
        }
        return false;
    }
}
