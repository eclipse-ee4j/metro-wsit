/*
 * Copyright (c) 2010, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * $Id: SecurityHeaderBlockImpl.java,v 1.2 2010-10-21 15:37:30 snajper Exp $
 */

package com.sun.xml.wss.impl.misc;

import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.xml.soap.Name;
import jakarta.xml.soap.SOAPFactory;
import jakarta.xml.soap.SOAPElement;
import jakarta.xml.soap.SOAPException;

import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.TypeInfo;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.DOMException;
import org.w3c.dom.UserDataHandler;

import com.sun.xml.wss.impl.MessageConstants;
import com.sun.xml.wss.logging.LogDomainConstants;
import com.sun.xml.wss.XWSSecurityException;

import com.sun.xml.wss.core.SecurityHeaderBlock;
import com.sun.xml.wss.logging.LogStringsMessages;

/**
 * @author XWS-Security Development Team
 */
public abstract class SecurityHeaderBlockImpl extends SOAPElementExtension implements SecurityHeaderBlock {

    private static Logger log =
        Logger.getLogger(
            LogDomainConstants.WSS_API_DOMAIN,
            LogDomainConstants.WSS_API_DOMAIN_BUNDLE);

    private static final Name idAttributeName;
    private static SOAPFactory soapFactory;
    
    protected SOAPElement delegateElement;

    private boolean bsp = false;

    static
    {
        Name temp = null;
        try {
            soapFactory = SOAPFactory.newInstance();
            temp =
                getSoapFactory().createName(
                    "Id",
                    "wsu",
                    "http://schemas.xmlsoap.org/ws/2003/06/utility");
        } catch (SOAPException e) {
            log.log(Level.SEVERE,
                    LogStringsMessages.WSS_0654_SOAP_EXCEPTION(e.getMessage()),
                    e.getMessage());
        }

        idAttributeName = temp;
    }

    protected SecurityHeaderBlockImpl() {
        // we expect that you will call setSOAPElement later
    }

    protected SecurityHeaderBlockImpl(SOAPElement delegateElement) {
        setSOAPElement(delegateElement);
    }

    protected void setSOAPElement(SOAPElement delegateElement) {
        this.delegateElement = delegateElement;
    }

    public String getId() {
       return delegateElement.getAttributeValue(idAttributeName);
    }

    protected void setWsuIdAttr(Element element, String wsuId) {
        element.setAttributeNS(
            MessageConstants.NAMESPACES_NS,
            "xmlns:" + MessageConstants.WSU_PREFIX,
            MessageConstants.WSU_NS);
        element.setAttributeNS(
            MessageConstants.WSU_NS,
            MessageConstants.WSU_ID_QNAME,
            wsuId);
    }

    @SuppressWarnings("unchecked")
    public static SecurityHeaderBlock fromSoapElement(
        SOAPElement element,
        Class implClass)
        throws XWSSecurityException {

        SecurityHeaderBlock block = null;

        try {
                Constructor implConstructor =
                    implClass.getConstructor(new Class[] {SOAPElement.class});
                block =
                    (SecurityHeaderBlock) implConstructor.newInstance(
                        new Object[] {element});
        } catch (Exception e) {
                log.log(Level.SEVERE,
                        LogStringsMessages.WSS_0655_ERROR_CREATING_HEADERBLOCK(e.getMessage()));
                throw new XWSSecurityException(e);
        }

        return block;
    }

    public SOAPElement getAsSoapElement() throws XWSSecurityException {
        return delegateElement;
    }

    // Generated delegate methods
    public SOAPElement addAttribute(Name arg0, String arg1)
        throws SOAPException {
        return delegateElement.addAttribute(arg0, arg1);
    }

    public SOAPElement addChildElement(String arg0) throws SOAPException {
        return delegateElement.addChildElement(arg0);
    }

    public SOAPElement addChildElement(String arg0, String arg1)
        throws SOAPException {
        return delegateElement.addChildElement(arg0, arg1);
    }

    public SOAPElement addChildElement(String arg0, String arg1, String arg2)
        throws SOAPException {
        return delegateElement.addChildElement(arg0, arg1, arg2);
    }

    public SOAPElement addChildElement(Name arg0) throws SOAPException {
        return delegateElement.addChildElement(arg0);
    }

    public SOAPElement addChildElement(SOAPElement arg0) throws SOAPException {
        return delegateElement.addChildElement(arg0);
    }

    public SOAPElement addNamespaceDeclaration(String arg0, String arg1)
        throws SOAPException {
        return delegateElement.addNamespaceDeclaration(arg0, arg1);
    }

    public SOAPElement addTextNode(String arg0) throws SOAPException {
        return delegateElement.addTextNode(arg0);
    }

    public Node appendChild(Node arg0) throws DOMException {
        return delegateElement.appendChild(arg0);
    }

    public Node cloneNode(boolean arg0) {
        return delegateElement.cloneNode(arg0);
    }

    public void detachNode() {
        delegateElement.detachNode();
    }

    public boolean equals(Object obj) {
        return delegateElement.equals(obj);
    }

    public Iterator getAllAttributes() {
        return delegateElement.getAllAttributes();
    }

    public String getAttribute(String arg0) {
        return delegateElement.getAttribute(arg0);
    }

    public Attr getAttributeNode(String arg0) {
        return delegateElement.getAttributeNode(arg0);
    }

    public Attr getAttributeNodeNS(String arg0, String arg1) {
        return delegateElement.getAttributeNodeNS(arg0, arg1);
    }

    public String getAttributeNS(String arg0, String arg1) {
        return delegateElement.getAttributeNS(arg0, arg1);
    }

    public NamedNodeMap getAttributes() {
        return delegateElement.getAttributes();
    }

    public String getAttributeValue(Name arg0) {
        return delegateElement.getAttributeValue(arg0);
    }

    public Iterator getChildElements() {
        return delegateElement.getChildElements();
    }

    public Iterator getChildElements(Name arg0) {
        return delegateElement.getChildElements(arg0);
    }

    public NodeList getChildNodes() {
        return delegateElement.getChildNodes();
    }

    public Name getElementName() {
        return delegateElement.getElementName();
    }

    public NodeList getElementsByTagName(String arg0) {
        return delegateElement.getElementsByTagName(arg0);
    }

    public NodeList getElementsByTagNameNS(String arg0, String arg1) {
        return delegateElement.getElementsByTagNameNS(arg0, arg1);
    }

    public String getEncodingStyle() {
        return delegateElement.getEncodingStyle();
    }

    public Node getFirstChild() {
        return delegateElement.getFirstChild();
    }

    public Node getLastChild() {
        return delegateElement.getLastChild();
    }

    public String getLocalName() {
        return delegateElement.getLocalName();
    }

    public Iterator getNamespacePrefixes() {
        return delegateElement.getNamespacePrefixes();
    }

    public String getNamespaceURI() {
        return delegateElement.getNamespaceURI();
    }

    public String getNamespaceURI(String arg0) {
        return delegateElement.getNamespaceURI(arg0);
    }

    public Node getNextSibling() {
        return delegateElement.getNextSibling();
    }

    public String getNodeName() {
        return delegateElement.getNodeName();
    }

    public short getNodeType() {
        return delegateElement.getNodeType();
    }

    public String getNodeValue() throws DOMException {
        return delegateElement.getNodeValue();
    }

    public Document getOwnerDocument() {
        return delegateElement.getOwnerDocument();
    }

    public SOAPElement getParentElement() {
        return delegateElement.getParentElement();
    }

    public Node getParentNode() {
        return delegateElement.getParentNode();
    }

    public String getPrefix() {
        return delegateElement.getPrefix();
    }

    public Node getPreviousSibling() {
        return delegateElement.getPreviousSibling();
    }

    public String getTagName() {
        return delegateElement.getTagName();
    }

    public String getValue() {
        return delegateElement.getValue();
    }

    public Iterator getVisibleNamespacePrefixes() {
        return delegateElement.getVisibleNamespacePrefixes();
    }

    public boolean hasAttribute(String arg0) {
        return delegateElement.hasAttribute(arg0);
    }

    public boolean hasAttributeNS(String arg0, String arg1) {
        return delegateElement.hasAttributeNS(arg0, arg1);
    }

    public boolean hasAttributes() {
        return delegateElement.hasAttributes();
    }

    public boolean hasChildNodes() {
        return delegateElement.hasChildNodes();
    }

    /*public int hashCode() {
        return delegateElement.hashCode();
    }*/

    public Node insertBefore(Node arg0, Node arg1) throws DOMException {
        return delegateElement.insertBefore(arg0, arg1);
    }

    public boolean isSupported(String arg0, String arg1) {
        return delegateElement.isSupported(arg0, arg1);
    }

    public void normalize() {
        delegateElement.normalize();
    }

    public void recycleNode() {
        delegateElement.recycleNode();
    }

    public void removeAttribute(String arg0) throws DOMException {
        delegateElement.removeAttribute(arg0);
    }

    public boolean removeAttribute(Name arg0) {
        return delegateElement.removeAttribute(arg0);
    }

    public Attr removeAttributeNode(Attr arg0) throws DOMException {
        return delegateElement.removeAttributeNode(arg0);
    }

    public void removeAttributeNS(String arg0, String arg1)
        throws DOMException {
        delegateElement.removeAttributeNS(arg0, arg1);
    }

    public Node removeChild(Node arg0) throws DOMException {
        return delegateElement.removeChild(arg0);
    }

    public void removeContents() {
        delegateElement.removeContents();
    }

    public boolean removeNamespaceDeclaration(String arg0) {
        return delegateElement.removeNamespaceDeclaration(arg0);
    }

    public Node replaceChild(Node arg0, Node arg1) throws DOMException {
        return delegateElement.replaceChild(arg0, arg1);
    }

    public void setAttribute(String arg0, String arg1) throws DOMException {
        delegateElement.setAttribute(arg0, arg1);
    }

    public Attr setAttributeNode(Attr arg0) throws DOMException {
        return delegateElement.setAttributeNode(arg0);
    }

    public Attr setAttributeNodeNS(Attr arg0) throws DOMException {
        return delegateElement.setAttributeNodeNS(arg0);
    }

    public void setAttributeNS(String arg0, String arg1, String arg2)
        throws DOMException {
        delegateElement.setAttributeNS(arg0, arg1, arg2);
    }

    public void setEncodingStyle(String arg0) throws SOAPException {
        delegateElement.setEncodingStyle(arg0);
    }

    public void setNodeValue(String arg0) throws DOMException {
        delegateElement.setNodeValue(arg0);
    }

    public void setParentElement(SOAPElement arg0) throws SOAPException {
        delegateElement.setParentElement(arg0);
    }

    public void setPrefix(String arg0) throws DOMException {
        delegateElement.setPrefix(arg0);
    }

    public void setValue(String arg0) {
        delegateElement.setValue(arg0);
    }

    /*public String toString() {
        return delegateElement.toString();
    }*/

    protected static SOAPFactory getSoapFactory() {
        return soapFactory;
    }

    
    // DOM L3 methods from org.w3c.dom.Node
    public String getBaseURI() {
        return delegateElement.getBaseURI();
    }

    public short compareDocumentPosition(org.w3c.dom.Node other)
                              throws DOMException {
        return delegateElement.compareDocumentPosition(other);
    }

    public String getTextContent()
                      throws DOMException {
        return delegateElement.getTextContent();
    }

    public void setTextContent(String textContent) throws DOMException {
        delegateElement.setTextContent(textContent);
    }

    public boolean isSameNode(org.w3c.dom.Node other) {
        return delegateElement.isSameNode(other);
    }

    public String lookupPrefix(String namespaceURI) {
        return delegateElement.lookupPrefix(namespaceURI);
    }

    public boolean isDefaultNamespace(String namespaceURI) {
        return delegateElement.isDefaultNamespace(namespaceURI);
    }

    public String lookupNamespaceURI(String prefix) {
        return  delegateElement.lookupNamespaceURI(prefix);
    }

    public boolean isEqualNode(org.w3c.dom.Node arg) {
         return  delegateElement.isEqualNode(arg);
    }

    public Object getFeature(String feature,
                  String version) {
         return  delegateElement.getFeature(feature,version);
    }

    public Object setUserData(String key,
                   Object data,
                  UserDataHandler handler) {
        return  delegateElement.setUserData(key,data,handler);
    }

    public Object getUserData(String key) {
        return  delegateElement.getUserData(key);
    }

    // DOM L3 methods from org.w3c.dom.Element
                                                                                                                                     
    public void setIdAttribute(String name, boolean isId) throws DOMException {
        delegateElement.setIdAttribute(name, isId);
    }
                                                                                                                                     
    public void setIdAttributeNode(Attr idAttr, boolean isId) throws DOMException {
        delegateElement.setIdAttributeNode(idAttr, isId);
    }
                                                                                                                                     
    public void setIdAttributeNS(String namespaceURI, String localName, boolean isId) throws DOMException {
        delegateElement.setIdAttributeNS(namespaceURI, localName, isId);
    }

    public TypeInfo getSchemaTypeInfo() {
        return  delegateElement.getSchemaTypeInfo();
    }

    public void isBSP(boolean flag) {
        bsp = flag;
    }

    public boolean isBSP() {
        return bsp;
    }

    public Iterator getAllAttributesAsQNames() {
        return delegateElement.getAllAttributesAsQNames();
    }

}
