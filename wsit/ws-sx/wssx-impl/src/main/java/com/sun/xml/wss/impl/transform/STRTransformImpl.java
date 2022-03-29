/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.impl.transform;

import com.sun.xml.wss.WSITXMLFactory;
import com.sun.xml.wss.impl.MessageConstants;
import com.sun.xml.wss.logging.LogDomainConstants;
import com.sun.xml.wss.logging.impl.dsig.LogStringsMessages;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.crypto.Data;
import javax.xml.crypto.NodeSetData;
import javax.xml.crypto.OctetStreamData;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;

/*
 * author K.Venugopal@sun.com
 */

public class STRTransformImpl {
    private static Logger logger = Logger.getLogger(LogDomainConstants.IMPL_SIGNATURE_DOMAIN,
            LogDomainConstants.IMPL_SIGNATURE_DOMAIN_BUNDLE);

    protected static Data transform(Data data,XMLCryptoContext context,java.io.OutputStream outputStream)throws javax.xml.crypto.dsig.TransformException{
        try{
            Set nodeSet = getNodeSet(data);
            if(outputStream == null){
                ByteArrayOutputStream bs =   new ByteArrayOutputStream();
                new Canonicalizer20010315ExclOmitComments().engineCanonicalizeXPathNodeSet(nodeSet, "",bs,context);
                OctetStreamData osd =  new OctetStreamData(new ByteArrayInputStream(bs.toByteArray()));
                bs.close();
                return osd;
            }else{
                new Canonicalizer20010315ExclOmitComments().engineCanonicalizeXPathNodeSet(nodeSet, "",outputStream,context);
            }
            return null;
        }catch(Exception ex){
            logger.log(Level.SEVERE,LogStringsMessages.WSS_1322_STR_TRANSFORM(),ex);
        }
        return null;
    }
    @SuppressWarnings("unchecked")
    private static Set getNodeSet(Data data)throws javax.xml.crypto.dsig.TransformException{
        HashSet nodeSet = new HashSet();
        if(data instanceof NodeSetData){
            Iterator it = ((NodeSetData)data).iterator();
            while(it.hasNext()){
                Object node = it.next();
                if(MessageConstants.debug){
                    logger.log(Level.FINEST,"Data is "+node);
                }
                nodeSet.add(node);
            }
        }else if(data instanceof OctetStreamData ){
            try{
                DocumentBuilderFactory factory = WSITXMLFactory.createDocumentBuilderFactory(WSITXMLFactory.DISABLE_SECURE_PROCESSING);
                //new com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl();
                factory.setNamespaceAware(true);
                Document doc = factory.newDocumentBuilder().parse(((OctetStreamData)data).getOctetStream());
                toNodeSet(doc,nodeSet);
            }catch(Exception ex){
                logger.log(Level.SEVERE,LogStringsMessages.WSS_1322_STR_TRANSFORM(),ex);
                throw new javax.xml.crypto.dsig.TransformException(ex.getMessage());

            }
        }
        return nodeSet;
    }
    @SuppressWarnings({"unchecked", "fallthrough"})
    static final void toNodeSet(final Node rootNode,final Set result){
        //handle EKSHA1 under DKT
        if (rootNode == null) return;
        switch (rootNode.getNodeType()) {
            case Node.ELEMENT_NODE:
                result.add(rootNode);
                Element el=(Element)rootNode;
                if (el.hasAttributes()) {
                    NamedNodeMap nl = rootNode.getAttributes();
                    for (int i=0;i<nl.getLength();i++) {
                        result.add(nl.item(i));
                    }
                }
                //no return keep working
            case Node.DOCUMENT_NODE:
                for (Node r=rootNode.getFirstChild();r!=null;r=r.getNextSibling()){
                    if (r.getNodeType()==Node.TEXT_NODE) {
                        result.add(r);
                        while ((r!=null) && (r.getNodeType()==Node.TEXT_NODE)) {
                            r=r.getNextSibling();
                        }
                        if (r==null)
                            return;
                    }
                    toNodeSet(r,result);
                }
                return;
            case Node.COMMENT_NODE:
                return;
            case Node.DOCUMENT_TYPE_NODE:
                return;
            default:
                result.add(rootNode);
        }
    }
}
