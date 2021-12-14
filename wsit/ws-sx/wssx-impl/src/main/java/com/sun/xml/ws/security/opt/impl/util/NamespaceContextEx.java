/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.opt.impl.util;



import java.util.ArrayList;

import java.util.Iterator;

import  com.sun.xml.wss.impl.MessageConstants;

/**

 *

 * @author K.Venugopal@sun.com

 */

public class NamespaceContextEx implements org.jvnet.staxex.NamespaceContextEx {

    private boolean addedWSSNS = false;
    private boolean samlNS = false;
    private boolean dsNS = false;
    private boolean encNS = false;
    private boolean scNS = false;
    private boolean exc14NS = false;
    private boolean addedWSS11NS = false;
    private ArrayList<org.jvnet.staxex.NamespaceContextEx.Binding> list = new ArrayList<>();
    private boolean addedXSDNS = false;
    /** Creates a new instance of NamespaceContextEx */

    public NamespaceContextEx() {
        this.add("S",MessageConstants.SOAP_1_1_NS );
        addDefaultNSDecl();
    }
    
    
    public NamespaceContextEx(boolean soap12Version) {
        if(soap12Version){
            this.add("S",MessageConstants.SOAP_1_2_NS );//SOAP 12
        }else{
            this.add("S",MessageConstants.SOAP_1_1_NS );
        }    
        addDefaultNSDecl();
    }
    
    private void addDefaultNSDecl(){
        
    }
    
    public void addWSSNS(){
        if(!addedWSSNS){
            this.add(MessageConstants.WSSE_PREFIX, MessageConstants.WSSE_NS);
            this.add(MessageConstants.WSU_PREFIX, MessageConstants.WSU_NS);
            addedWSSNS = true;
        }
    }
    
    public void addWSS11NS(){
         if(!addedWSS11NS){
            this.add(MessageConstants.WSSE11_PREFIX, MessageConstants.WSSE11_NS);            
            addedWSS11NS = true;
        }
    }
    
    public void addXSDNS(){
        if(!addedXSDNS){
            this.add("xs", MessageConstants.XSD_NS);
            addedXSDNS = true;
        }
    }
    
    public void addSignatureNS(){
        addWSSNS();
        if(!dsNS){
            this.add(MessageConstants.DSIG_PREFIX, MessageConstants.DSIG_NS);
            dsNS = true;
        }
    }
    
    public void addEncryptionNS(){
        addWSSNS();
        if(!encNS){
            this.add(MessageConstants.XENC_PREFIX, MessageConstants.XENC_NS);
            encNS = true;
        }
    }
    
    public void addSAMLNS(){
        if(!samlNS){
            this.add(MessageConstants.SAML_PREFIX, MessageConstants.SAML_v1_0_NS);
            samlNS = true;
        }
    }
    
    public void addSCNS(){
        if(!scNS){
            this.add(MessageConstants.WSSC_PREFIX, MessageConstants.WSSC_NS);
            scNS = true;
        }
    }
    
    public void addExc14NS(){
        if(!exc14NS){
            this.add("exc14n", MessageConstants.TRANSFORM_C14N_EXCL_OMIT_COMMENTS);
            exc14NS = true;
        }
    }
    
    public void add(String prefix,String uri){
        list.add(new BindingImpl(prefix,uri));   
    }
    
    @Override
    public Iterator<org.jvnet.staxex.NamespaceContextEx.Binding> iterator() {
        return list.iterator();
    }
    
    @Override
    public String getNamespaceURI(String prefix) {
        for(org.jvnet.staxex.NamespaceContextEx.Binding binding : list){
            if(prefix.equals(binding.getPrefix())){
                return binding.getNamespaceURI();
            }
        }
        return null;
    }
    
    @Override
    public String getPrefix(String namespaceURI) {
        for(org.jvnet.staxex.NamespaceContextEx.Binding binding : list){
            if(namespaceURI.equals(binding.getNamespaceURI())){
                return binding.getPrefix();
            }
        }
        return null;
    }
    
    @Override
    public Iterator getPrefixes(final String namespaceURI) {
        return new Iterator(){
            
            int index = 0;
            
            @Override
            public boolean hasNext(){
                if( ++index < list.size() && move()){
                    return true;
                }
                return false;
            }
            
            @Override
            public Object next(){
                return list.get(index).getPrefix();
            }
            
            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
            
            private boolean move(){
                boolean found = false;
                do{
                    if(namespaceURI.equals(list.get(index).getNamespaceURI())){
                        found = true;
                        break;
                    }else{
                        index++;
                    }
                }while(index < list.size());
                return found;
            }
        };
    }
    
    
    static class BindingImpl implements org.jvnet.staxex.NamespaceContextEx.Binding{
        private String prefix="";
        private String uri="";
        public BindingImpl(String prefix,String uri){
            this.prefix = prefix;
            this.uri = uri;
        }
        
        @Override
        public String getPrefix() {
            return prefix;
        }
        
        @Override
        public String getNamespaceURI() {
            return uri;
        }
        
    }
    
}
