/*
 * Copyright (c) 2010, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * $Id: SecurityHeader.java,v 1.2 2010-10-21 15:37:11 snajper Exp $
 */

package com.sun.xml.wss.core;

import com.sun.xml.wss.logging.LogDomainConstants;
import com.sun.xml.wss.XWSSecurityException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.xml.soap.Name;
import jakarta.xml.soap.SOAPElement;
import jakarta.xml.soap.SOAPException;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.UserDataHandler;
import org.w3c.dom.TypeInfo;

import com.sun.xml.wss.impl.misc.SecurityHeaderBlockImpl;
import com.sun.xml.wss.impl.misc.SOAPElementExtension;
import com.sun.xml.wss.impl.MessageConstants;

/**
 * @author XWS-Security Development Team
 */
public class SecurityHeader extends SOAPElementExtension implements SOAPElement {
    
    private final SOAPElement delegateHeader;
    private Document ownerDoc;
    
    private static Logger log =
    Logger.getLogger(
    LogDomainConstants.WSS_API_DOMAIN,
    LogDomainConstants.WSS_API_DOMAIN_BUNDLE);
    
    /**
     * The child element of security header to be processed next.
     */
    private SOAPElement currentSoapElement;
    
    /**
     * The first child element of the security header.
     */
    private SOAPElement topMostSoapElement;
    
    public SecurityHeader(SOAPElement delegateHeader) {
        this.delegateHeader = delegateHeader;
        this.ownerDoc = delegateHeader.getOwnerDocument();
        topMostSoapElement = getFirstChildElement();
        currentSoapElement = null;
    }
     
    /**
     * Inserts the header block at the top of the security header, i.e,
     * the block becomes the first child element of the security header.
     * This method will be used on the sender side.
     */
    public void insertHeaderBlock(SecurityHeaderBlock block)
    throws XWSSecurityException {
        SOAPElement elementToInsert = block.getAsSoapElement();
        try {
            if (elementToInsert.getOwnerDocument() != ownerDoc) {
                elementToInsert =
                (SOAPElement) ownerDoc.importNode(
                elementToInsert, true);
            }
            
            updateTopMostSoapElement();
            
            insertBefore(elementToInsert, topMostSoapElement);

        } catch (DOMException e) {
            log.log(Level.SEVERE, "WSS0376.error.inserting.header", e.getMessage());
            throw new XWSSecurityException(e);
        }
        topMostSoapElement = elementToInsert;
    }
    
    public void insertBefore(SecurityHeaderBlock block,Node elem) throws XWSSecurityException {
        SOAPElement elementToInsert = block.getAsSoapElement();
        try {
            if (elementToInsert.getOwnerDocument() != ownerDoc) {
                elementToInsert =
                (SOAPElement) ownerDoc.importNode(
                elementToInsert, true);
            }
        } catch (DOMException e) {
            log.log(Level.SEVERE, "WSS0376.error.inserting.header", e.getMessage());
            throw new XWSSecurityException(e);
        }
        insertBefore(elementToInsert,elem);
    }
    
    
    public void appendChild(SecurityHeaderBlock block) throws XWSSecurityException {
        SOAPElement elementToInsert = block.getAsSoapElement();
        try {
            if (elementToInsert.getOwnerDocument() != ownerDoc) {
                elementToInsert =
                (SOAPElement) ownerDoc.importNode(
                elementToInsert, true);
            }
            appendChild(elementToInsert);
            
        } catch (DOMException e) {
            log.log(Level.SEVERE, "WSS0376.error.inserting.header", e.getMessage());
            throw new XWSSecurityException(e);
        }
    }
    
    public void insertHeaderBlockElement(SOAPElement blockElement)
    throws XWSSecurityException {
        try {
            if (blockElement.getOwnerDocument() != ownerDoc) {
                blockElement =
                (SOAPElement) ownerDoc.importNode(blockElement, true);
            }
            updateTopMostSoapElement();
            
            insertBefore(blockElement, topMostSoapElement);
            
        } catch (DOMException e) {
            log.log(Level.SEVERE, "WSS0376.error.inserting.header", e.getMessage());
            throw new XWSSecurityException(e);
        }
        topMostSoapElement = blockElement;
    }
    
    /**
     * Get the header block to be processed next.
     * This method will be used on the receiver side.
     */
    public SecurityHeaderBlock getCurrentHeaderBlock(Class implType)
    throws XWSSecurityException {
        if (null == currentSoapElement)
            currentSoapElement = getFirstChildElement();
        else {
            Node nextChild = currentSoapElement.getNextSibling();
            while ((null != nextChild) && (nextChild.getNodeType() != Node.ELEMENT_NODE))
                nextChild = nextChild.getNextSibling();
            currentSoapElement = (SOAPElement) nextChild;
        }
        return SecurityHeaderBlockImpl.fromSoapElement(
        currentSoapElement, implType);
    }
    
    public SOAPElement getCurrentHeaderBlockElement() {
        if (null == currentSoapElement)
            currentSoapElement = getFirstChildElement();
        else {
            Node nextChild = currentSoapElement.getNextSibling();
            while ((null != nextChild) && (nextChild.getNodeType() != Node.ELEMENT_NODE))
                nextChild = nextChild.getNextSibling();
            currentSoapElement = (SOAPElement) nextChild;
        }
        return currentSoapElement;
    }
    
    public void setCurrentHeaderElement(SOAPElement currentElement)
    throws XWSSecurityException {
        if (currentElement != null &&
        currentElement.getParentNode() != delegateHeader) {
            log.log(Level.SEVERE, "WSS0396.notchild.securityHeader", 
                    new Object[] {currentElement.toString()} );
            throw new XWSSecurityException(
            "Element set is not a child of SecurityHeader");
        }
        currentSoapElement = currentElement;
    }
    
    public SOAPElement getCurrentHeaderElement() {
        return currentSoapElement;
    }
    
    // TODO : Obsolete method -
    // To be removed once we get rid of the OldEncryptFilter.
    public void updateTopMostSoapElement() {
        topMostSoapElement = getNextSiblingOfTimestamp();

    }
    
    public SOAPElement getFirstChildElement() {
        Iterator eachChild = getChildElements();
        jakarta.xml.soap.Node node;

        if (eachChild.hasNext()) {
            node = (jakarta.xml.soap.Node) eachChild.next();
        } else {
            return null;
        }

        while ((node.getNodeType() != Node.ELEMENT_NODE) && eachChild.hasNext()) {
            node = (jakarta.xml.soap.Node) eachChild.next();
        }
        return (SOAPElement) node;
    }
    
    public SOAPElement getNextSiblingOfTimestamp(){
        SOAPElement firstElement = getFirstChildElement();
        Node temp;
        if(firstElement != null && MessageConstants.TIMESTAMP_LNAME.equals(firstElement.getLocalName())){
            temp = firstElement.getNextSibling();
            if (temp == null) {
                return null;
            }
            
            while (temp.getNodeType() != Node.ELEMENT_NODE && temp.getNextSibling() != null) {
                temp = temp.getNextSibling();
            }
            
            while((temp != null) && (MessageConstants.SIGNATURE_CONFIRMATION_LNAME.equals(temp.getLocalName()))){
                temp = temp.getNextSibling();
                if (temp == null) {
                    return null;
                }
                while(temp.getNodeType() != Node.ELEMENT_NODE && temp.getNextSibling() != null) {
                    temp = temp.getNextSibling();
                }
            }
            return (SOAPElement)temp;
        } else{
            return firstElement;
        }
    }
    
    // This method was introduced to use a work-around for the
    // selectSingleNode() problem.
    public SOAPElement getAsSoapElement() {
        return delegateHeader;
    }
    
    // Mimic SOAPHeaderElement (almost)
    public void setRole(String roleURI) {
        throw new UnsupportedOperationException();
    }
    public String getRole() {
        throw new UnsupportedOperationException();
    }
    public void setMustUnderstand(boolean mustUnderstand) {
        throw new UnsupportedOperationException();
    }
    public boolean isMustUnderstand() {
        throw new UnsupportedOperationException();
    }
    
    // All of the following methods are generated delegate methods...
    @Override
    public SOAPElement addAttribute(Name arg0, String arg1)
    throws SOAPException {
        return delegateHeader.addAttribute(arg0, arg1);
    }
    
    @Override
    public SOAPElement addChildElement(String arg0) throws SOAPException {
        return delegateHeader.addChildElement(arg0);
    }
    
    @Override
    public SOAPElement addChildElement(String arg0, String arg1)
    throws SOAPException {
        return delegateHeader.addChildElement(arg0, arg1);
    }
    
    @Override
    public SOAPElement addChildElement(String arg0, String arg1, String arg2)
    throws SOAPException {
        return delegateHeader.addChildElement(arg0, arg1, arg2);
    }
    
    @Override
    public SOAPElement addChildElement(Name arg0) throws SOAPException {
        return delegateHeader.addChildElement(arg0);
    }
    
    @Override
    public SOAPElement addChildElement(SOAPElement arg0) throws SOAPException {
        return delegateHeader.addChildElement(arg0);
    }
    
    @Override
    public SOAPElement addNamespaceDeclaration(String arg0, String arg1)
    throws SOAPException {
        return delegateHeader.addNamespaceDeclaration(arg0, arg1);
    }
    
    @Override
    public SOAPElement addTextNode(String arg0) throws SOAPException {
        return delegateHeader.addTextNode(arg0);
    }
    
    @Override
    public Node appendChild(Node arg0) throws DOMException {
        return delegateHeader.appendChild(arg0);
    }
  
    public SOAPElement makeUsable(SOAPElement elem)throws XWSSecurityException {          
        SOAPElement elementToInsert = elem;
        try {
            if (elem.getOwnerDocument() != ownerDoc) {
                elementToInsert =
                (SOAPElement) ownerDoc.importNode(
                elem, true);
            }            
            return elementToInsert;
        } catch (DOMException e) {
            log.log(Level.SEVERE, "WSS0376.error.inserting.header", e.getMessage());
            throw new XWSSecurityException(e);
        }
    }
    @Override
    public Node cloneNode(boolean arg0) {
        return delegateHeader.cloneNode(arg0);
    }
    
    @Override
    public void detachNode() {
        delegateHeader.detachNode();
    }
    
    public boolean equals(Object obj) {
        return delegateHeader.equals(obj);
    }
    
    @Override
    public Iterator getAllAttributes() {
        return delegateHeader.getAllAttributes();
    }
    
    @Override
    public String getAttribute(String arg0) {
        return delegateHeader.getAttribute(arg0);
    }
    
    @Override
    public Attr getAttributeNode(String arg0) {
        return delegateHeader.getAttributeNode(arg0);
    }
    
    @Override
    public Attr getAttributeNodeNS(String arg0, String arg1) {
        return delegateHeader.getAttributeNodeNS(arg0, arg1);
    }
    
    @Override
    public String getAttributeNS(String arg0, String arg1) {
        return delegateHeader.getAttributeNS(arg0, arg1);
    }
    
    @Override
    public NamedNodeMap getAttributes() {
        return delegateHeader.getAttributes();
    }
    
    @Override
    public String getAttributeValue(Name arg0) {
        return delegateHeader.getAttributeValue(arg0);
    }
    
    @Override
    public Iterator getChildElements() {
        return delegateHeader.getChildElements();
    }
    
    @Override
    public Iterator getChildElements(Name arg0) {
        return delegateHeader.getChildElements(arg0);
    }
    
    @Override
    public NodeList getChildNodes() {
        return delegateHeader.getChildNodes();
    }
    
    @Override
    public Name getElementName() {
        return delegateHeader.getElementName();
    }
    
    @Override
    public NodeList getElementsByTagName(String arg0) {
        return delegateHeader.getElementsByTagName(arg0);
    }
    
    @Override
    public NodeList getElementsByTagNameNS(String arg0, String arg1) {
        return delegateHeader.getElementsByTagNameNS(arg0, arg1);
    }
    
    @Override
    public String getEncodingStyle() {
        return delegateHeader.getEncodingStyle();
    }
    
    @Override
    public Node getFirstChild() {
        return delegateHeader.getFirstChild();
    }
    
    @Override
    public Node getLastChild() {
        return delegateHeader.getLastChild();
    }
    
    @Override
    public String getLocalName() {
        return delegateHeader.getLocalName();
    }
    
    @Override
    public Iterator getNamespacePrefixes() {
        return delegateHeader.getNamespacePrefixes();
    }
    
    @Override
    public String getNamespaceURI() {
        return delegateHeader.getNamespaceURI();
    }
    
    @Override
    public String getNamespaceURI(String arg0) {
        return delegateHeader.getNamespaceURI(arg0);
    }
    
    @Override
    public Node getNextSibling() {
        return delegateHeader.getNextSibling();
    }
    
    @Override
    public String getNodeName() {
        return delegateHeader.getNodeName();
    }
    
    @Override
    public short getNodeType() {
        return delegateHeader.getNodeType();
    }
    
    @Override
    public String getNodeValue() throws DOMException {
        return delegateHeader.getNodeValue();
    }
    
    @Override
    public Document getOwnerDocument() {
        return delegateHeader.getOwnerDocument();
    }
    
    @Override
    public SOAPElement getParentElement() {
        return delegateHeader.getParentElement();
    }
    
    @Override
    public Node getParentNode() {
        return delegateHeader.getParentNode();
    }
    
    @Override
    public String getPrefix() {
        return delegateHeader.getPrefix();
    }
    
    @Override
    public Node getPreviousSibling() {
        return delegateHeader.getPreviousSibling();
    }
    
    @Override
    public String getTagName() {
        return delegateHeader.getTagName();
    }
    
    @Override
    public String getValue() {
        return delegateHeader.getValue();
    }
    
    @Override
    public Iterator getVisibleNamespacePrefixes() {
        return delegateHeader.getVisibleNamespacePrefixes();
    }
    
    @Override
    public boolean hasAttribute(String arg0) {
        return delegateHeader.hasAttribute(arg0);
    }
    
    @Override
    public boolean hasAttributeNS(String arg0, String arg1) {
        return delegateHeader.hasAttributeNS(arg0, arg1);
    }
    
    @Override
    public boolean hasAttributes() {
        return delegateHeader.hasAttributes();
    }
    
    @Override
    public boolean hasChildNodes() {
        return delegateHeader.hasChildNodes();
    }
    
    public int hashCode() {
        return delegateHeader.hashCode();
    }
    
    @Override
    public Node insertBefore(Node arg0, Node arg1) throws DOMException {
        
        return delegateHeader.insertBefore(arg0, arg1);
    }
    
    @Override
    public boolean isSupported(String arg0, String arg1) {
        return delegateHeader.isSupported(arg0, arg1);
    }
    
    @Override
    public void normalize() {
        delegateHeader.normalize();
    }
    
    @Override
    public void recycleNode() {
        delegateHeader.recycleNode();
    }
    
    @Override
    public void removeAttribute(String arg0) throws DOMException {
        delegateHeader.removeAttribute(arg0);
    }
    
    @Override
    public boolean removeAttribute(Name arg0) {
        return delegateHeader.removeAttribute(arg0);
    }
    
    @Override
    public Attr removeAttributeNode(Attr arg0) throws DOMException {
        return delegateHeader.removeAttributeNode(arg0);
    }
    
    @Override
    public void removeAttributeNS(String arg0, String arg1)
    throws DOMException {
        delegateHeader.removeAttributeNS(arg0, arg1);
    }
    
    @Override
    public Node removeChild(Node arg0) throws DOMException {
        return delegateHeader.removeChild(arg0);
    }
    
    @Override
    public void removeContents() {
        delegateHeader.removeContents();
    }
    
    @Override
    public boolean removeNamespaceDeclaration(String arg0) {
        return delegateHeader.removeNamespaceDeclaration(arg0);
    }
    
    @Override
    public Node replaceChild(Node arg0, Node arg1) throws DOMException {
        return delegateHeader.replaceChild(arg0, arg1);
    }
    
    @Override
    public void setAttribute(String arg0, String arg1) throws DOMException {
        delegateHeader.setAttribute(arg0, arg1);
    }
    
    @Override
    public Attr setAttributeNode(Attr arg0) throws DOMException {
        return delegateHeader.setAttributeNode(arg0);
    }
    
    @Override
    public Attr setAttributeNodeNS(Attr arg0) throws DOMException {
        return delegateHeader.setAttributeNodeNS(arg0);
    }
    
    @Override
    public void setAttributeNS(String arg0, String arg1, String arg2)
    throws DOMException {
        delegateHeader.setAttributeNS(arg0, arg1, arg2);
    }
    
    @Override
    public void setEncodingStyle(String arg0) throws SOAPException {
        delegateHeader.setEncodingStyle(arg0);
    }
    
    @Override
    public void setNodeValue(String arg0) throws DOMException {
        delegateHeader.setNodeValue(arg0);
    }
    
    @Override
    public void setParentElement(SOAPElement arg0) throws SOAPException {
        delegateHeader.setParentElement(arg0);
    }
    
    @Override
    public void setPrefix(String arg0) throws DOMException {
        delegateHeader.setPrefix(arg0);
    }
    
    @Override
    public void setValue(String arg0) {
        delegateHeader.setValue(arg0);
    }
    
    public String toString() {
        return delegateHeader.toString();
    }
    
    // DOM L3 methods from org.w3c.dom.Node
    @Override
    public String getBaseURI() {
        return delegateHeader.getBaseURI();
    }
    
    @Override
    public short compareDocumentPosition(org.w3c.dom.Node other)
    throws DOMException {
        return delegateHeader.compareDocumentPosition(other);
    }
    
    @Override
    public String getTextContent()
    throws DOMException {
        return delegateHeader.getTextContent();
    }
    
    @Override
    public void setTextContent(String textContent) throws DOMException {
        delegateHeader.setTextContent(textContent);
    }
    
    @Override
    public boolean isSameNode(org.w3c.dom.Node other) {
        return delegateHeader.isSameNode(other);
    }
    
    @Override
    public String lookupPrefix(String namespaceURI) {
        return delegateHeader.lookupPrefix(namespaceURI);
    }
    
    @Override
    public boolean isDefaultNamespace(String namespaceURI) {
        return delegateHeader.isDefaultNamespace(namespaceURI);
    }
    
    @Override
    public String lookupNamespaceURI(String prefix) {
        return  delegateHeader.lookupNamespaceURI(prefix);
    }
    
    @Override
    public boolean isEqualNode(org.w3c.dom.Node arg) {
        return  delegateHeader.isEqualNode(arg);
    }
    
    @Override
    public Object getFeature(String feature,
                             String version) {
        return  delegateHeader.getFeature(feature,version);
    }
    
    @Override
    public Object setUserData(String key,
                              Object data,
                              UserDataHandler handler) {
        return  delegateHeader.setUserData(key,data,handler);
    }
    
    @Override
    public Object getUserData(String key) {
        return  delegateHeader.getUserData(key);
    }
    
    // DOM L3 methods from org.w3c.dom.Element
    
    @Override
    public void setIdAttribute(String name, boolean isId) throws DOMException {
        delegateHeader.setIdAttribute(name, isId);
    }
    
    @Override
    public void setIdAttributeNode(Attr idAttr, boolean isId) throws DOMException {
        delegateHeader.setIdAttributeNode(idAttr, isId);
    }
    
    @Override
    public void setIdAttributeNS(String namespaceURI, String localName, boolean isId) throws DOMException {
        delegateHeader.setIdAttributeNS(namespaceURI, localName, isId);
    }
    
    @Override
    public TypeInfo getSchemaTypeInfo() {
        return  delegateHeader.getSchemaTypeInfo();
    }

   @Override
   public Iterator getAllAttributesAsQNames() {
       return  delegateHeader. getAllAttributesAsQNames();
   }

}
