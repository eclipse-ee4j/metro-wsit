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

import com.sun.xml.wss.impl.MessageConstants;
import com.sun.xml.wss.impl.misc.UnsyncByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;

/**
 *
 * @author K.Venugopal@sun.com
 */
public class StAXEXC14nCanonicalizerImpl extends StAXC14nCanonicalizerImpl  {

    private List inclusivePrefixList = null;
    private HashSet visiblyUtilized = new HashSet();

    private UnsyncByteArrayOutputStream tmpBuffer = null;

    NamespaceContextImpl exC14NContext = new NamespaceContextImpl();

    // Since the StreamMessage is leaving out the white spaces around message payload,
    // it must be handled specially to allow message signature verification

    // flag if it is time to write the prolog
    private boolean bodyPrologueTime = false;
    private String bodyPrologue;
    private String bodyEpilogue;

    private boolean forceDefNS  =false;
    /** Creates a new instance of StAXEC14nCanonicalizerImpl */
    public StAXEXC14nCanonicalizerImpl() {
        super();
        tmpBuffer = new UnsyncByteArrayOutputStream();
    }

    public void setBodyPrologueTime(boolean bodyPrologueTime) {
        this.bodyPrologueTime = bodyPrologueTime;
    }

    public void setBodyEpilogue(String bodyEpilogue) {
        this.bodyEpilogue = bodyEpilogue;
    }

    public void setBodyPrologue(String bodyPrologue) {
        this.bodyPrologue = bodyPrologue;
    }

    public boolean isParentToParentAdvice(){
        if(_depth > 2){
            ElementName qname = elementNames[_depth - 2];
            if(qname.getUtf8Data().getLength() == 11 || qname.getUtf8Data().getLength() == 12){
                String str = new String(qname.getUtf8Data().getBytes(), qname.getUtf8Data().getLength() - 6, 6);
                if(str.equals("Advice")){
                    return true;
                }
            }else{
                return false;
            }
        }
        return false;
    }

    @Override
    public void reset(){
        super.reset();
        exC14NContext.reset();
    }

    public void setInclusivePrefixList(List values){
        this.inclusivePrefixList = values;
    }

    public List getInclusivePrefixList(){
        return inclusivePrefixList;
    }

    public void forceDefaultNS(boolean isForce){
        this.forceDefNS = isForce;
    }
    @Override
    public void writeNamespace(String prefix, String namespaceURI) {
        if(prefix == null || prefix.length() == 0){
            String defNS = exC14NContext.getNamespaceURI(prefix);
            if((defNS == null || defNS.length()== 0) && (namespaceURI == null || namespaceURI.length() ==0)){
                if(!forceDefNS)
                    return;
            }
            if(namespaceURI == null){
                namespaceURI = "";
            }
            _defURI = namespaceURI;
            prefix = "";
        }
        exC14NContext.declareNamespace(prefix,namespaceURI);
    }

    @Override
    public void writeStartElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
        String pf = prefix;
        if(prefix == null){
            pf = "";
        }
        super.writeStartElement(pf,localName,namespaceURI);
        _elementPrefix = pf;
        exC14NContext.push();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void closeStartTag() {
        try{
            if(closeStartTag){
                if(_attrResult.size() >0){
                    collectVisiblePrefixes(_attrResult.iterator());
                }
                if(_elementPrefix != null)
                    visiblyUtilized.add(_elementPrefix);
                AttributeNS nsDecl = null;
                /*
                if(_elementPrefix != null && _elementPrefix.length() >=0){
                    AttributeNS eDecl = exC14NContext.getNamespaceDeclaration(_elementPrefix);

                    if(eDecl !=null && !eDecl.isWritten()){
                        eDecl.setWritten(true);
                        _nsResult.add(eDecl);
                    }

                }*/
                if(visiblyUtilized.size() > 0){
                    Iterator prefixItr = visiblyUtilized.iterator();
                    populateNamespaceDecl(prefixItr);
                }
                if(inclusivePrefixList != null){
                    populateNamespaceDecl(inclusivePrefixList.iterator());
                }

                 if (forceDefNS) {
                     forceDefNS = false;
                     if (exC14NContext.getNamespaceDeclaration("") == null
                             && "".equals(exC14NContext.getNamespaceURI(""))) {
                        AttributeNS ns = new AttributeNS();
                        ns.setPrefix("");
                        ns.setUri("");
                        if (!_nsResult.contains(ns)) {
                            _nsResult.add(ns);
                        }
                     }
                }

                if ( _nsResult.size() > 0) {
                    BaseCanonicalizer.sort(_nsResult);
                    writeAttributesNS(_nsResult);
                }

                if  ( _attrResult.size() > 0 ) {
                    BaseCanonicalizer.sort(_attrResult);
                    writeAttributes(_attrResult);
                }
                visiblyUtilized.clear();
                _nsResult.clear();
                _attrResult.clear();
                _stream .write('>');

                // write the message body prolog leaved out be StreamMessage, if necessary
                if (bodyPrologueTime) {
                    if (bodyPrologue != null) {
                        _stream.write(bodyPrologue.getBytes());
                    }
                    bodyPrologueTime = false;
                }

                closeStartTag = false;
                _elementPrefix = null;
                _defURI = null;
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void writeEmptyElement(String namespaceURI, String localName) {
       /* String prefix = nsContext.getPrefix (namespaceURI);
        writeEmptyElement (prefix,localName,namespaceURI);*/
        //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void writeEmptyElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {

        closeStartTag();
        exC14NContext.push();



        try {
            _stream .write('<');
            elemBuffer.reset();
            if(prefix.length() == 0){
                writeStringToUtf8(localName,elemBuffer);
            }else{
                writeStringToUtf8(prefix,elemBuffer);
                writeStringToUtf8(":",elemBuffer);
                writeStringToUtf8(localName,elemBuffer);

            }
            byte [] endElem = elemBuffer.getBytes();
            int len = elemBuffer.getLength();
            visiblyUtilized.add(prefix);
            AttributeNS nsDecl = null;
            _stream.write(endElem, 0, len);

            if ( visiblyUtilized.size() > 0 ) {
                Iterator prefixItr = visiblyUtilized.iterator();
                populateNamespaceDecl(prefixItr);
            }
            if(inclusivePrefixList != null){
                populateNamespaceDecl(inclusivePrefixList.iterator());
            }

            if ( _nsResult.size() > 0 ) {
                BaseCanonicalizer.sort(_nsResult);
                writeAttributesNS(_nsResult);
            }

            if ( _attrResult.size() > 0 ) {
                BaseCanonicalizer.sort(_attrResult);
                writeAttributes(_attrResult);
            }


            visiblyUtilized.clear();
            _nsResult.clear();
            _attrResult.clear();
            // _stream .write('>');
            closeStartTag = false;
            _elementPrefix = "";
            _defURI = null;

            _stream .write('>');
            _stream .write(_END_TAG);
            //writeStringToUtf8(name,_stream);
            _stream.write(endElem, 0, len);
            _stream .write('>');
            exC14NContext.pop();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void writeEndDocument() throws XMLStreamException {
        while(_depth > 0){
            writeEndElement();
        }
    }


    @Override
    public void writeEndElement() throws XMLStreamException {
        closeStartTag();
        if(_depth ==0 ){
            return;
        }
        ElementName qname =  elementNames[--_depth];

        exC14NContext.pop();

        try {

            // if it is a soap:Body to be closed, write original spaces left out by StreamMessage
            // before closing element, if necessary
            if (_depth == 0) {
                String toBeClosed = new String(qname.getUtf8Data().getBytes());
                // is this condition enough?
                if (toBeClosed.contains(":Body") && bodyEpilogue != null) {
                    _stream.write(bodyEpilogue.getBytes());
                }
            }

            _stream .write(_END_TAG);
            //writeStringToUtf8(qname,_stream);
            _stream.write(qname.getUtf8Data().getBytes(), 0, qname.getUtf8Data().getLength());
            qname.getUtf8Data().reset();
            _stream .write('>');
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @SuppressWarnings("unchecked")
    protected void collectVisiblePrefixes(Iterator itr) {
        while(itr.hasNext()){
            StAXAttr attr = (StAXAttr) itr.next();
            String prefix = attr.getPrefix();
            if(prefix.length() >0){
                visiblyUtilized.add(prefix);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void populateNamespaceDecl(Iterator prefixItr){

        AttributeNS nsDecl = null;
        while(prefixItr.hasNext() ){
            String prefix = (String)prefixItr.next();
            if(prefix.equals(MessageConstants.XML_PREFIX)){
                continue;
            }
            nsDecl = exC14NContext.getNamespaceDeclaration(prefix);

            if(nsDecl !=null && !nsDecl.isWritten()){
                nsDecl.setWritten(true);
                _nsResult.add(nsDecl);
            }
        }
    }

    protected void writeAttributesNS(List itr) throws IOException {
        AttributeNS attr = null;
        int size = itr.size();
        for ( int i=0; i<size; i++) {
            attr = (AttributeNS) itr.get(i);
            tmpBuffer.reset();
            _stream.write(attr.getUTF8Data(tmpBuffer));
        }
    }

    @Override
    public NamespaceContext getNamespaceContext() {
        return exC14NContext;
    }
}
