/*
 * Copyright (c) 2010, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * $Id: ReferenceElement.java,v 1.2 2010-10-21 15:37:11 snajper Exp $
 */

package com.sun.xml.wss.core;

import java.util.Iterator;

import jakarta.xml.soap.Name;
import jakarta.xml.soap.SOAPElement;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.UserDataHandler;
import org.w3c.dom.TypeInfo;

import com.sun.xml.wss.XWSSecurityException;
import com.sun.xml.wss.impl.misc.SOAPElementExtension;

import javax.xml.namespace.QName;

/**
 * @author Vishal Mahajan
 */
public abstract class ReferenceElement extends SOAPElementExtension implements SOAPElement {

    protected SOAPElement delegateElement;

    protected static final SOAPFactory soapFactory;

    static {
        SOAPFactory sf = null;
        try {
            sf = SOAPFactory.newInstance();
        } catch (SOAPException e){
        } finally {
            soapFactory = sf;
        }
    }

    /**
     * Default constructor.
     */
    protected ReferenceElement() {}

    protected void setSOAPElement(SOAPElement delegateElement) {
        this.delegateElement = delegateElement;
    }

    public SOAPElement getAsSoapElement() throws XWSSecurityException {
        return delegateElement;
    }

    // Generated delegate methods
    @Override
    public SOAPElement addAttribute(Name arg0, String arg1)
        throws SOAPException {
        return delegateElement.addAttribute(arg0, arg1);
    }

    @Override
    public SOAPElement addChildElement(String arg0) throws SOAPException {
        return delegateElement.addChildElement(arg0);
    }

    @Override
    public SOAPElement addChildElement(String arg0, String arg1)
        throws SOAPException {
        return delegateElement.addChildElement(arg0, arg1);
    }

    @Override
    public SOAPElement addChildElement(String arg0, String arg1, String arg2)
        throws SOAPException {
        return delegateElement.addChildElement(arg0, arg1, arg2);
    }

    @Override
    public SOAPElement addChildElement(Name arg0) throws SOAPException {
        return delegateElement.addChildElement(arg0);
    }

    @Override
    public SOAPElement addChildElement(SOAPElement arg0) throws SOAPException {
        return delegateElement.addChildElement(arg0);
    }

    @Override
    public SOAPElement addNamespaceDeclaration(String arg0, String arg1)
        throws SOAPException {
        return delegateElement.addNamespaceDeclaration(arg0, arg1);
    }

    @Override
    public SOAPElement addTextNode(String arg0) throws SOAPException {
        return delegateElement.addTextNode(arg0);
    }

    @Override
    public Node appendChild(Node arg0) throws DOMException {
        return delegateElement.appendChild(arg0);
    }

    @Override
    public Node cloneNode(boolean arg0) {
        return delegateElement.cloneNode(arg0);
    }

    @Override
    public void detachNode() {
        delegateElement.detachNode();
    }

    public boolean equals(Object obj) {
        return delegateElement.equals(obj);
    }

    @Override
    public Iterator<Name> getAllAttributes() {
        return delegateElement.getAllAttributes();
    }

    @Override
    public String getAttribute(String arg0) {
        return delegateElement.getAttribute(arg0);
    }

    @Override
    public Attr getAttributeNode(String arg0) {
        return delegateElement.getAttributeNode(arg0);
    }

    @Override
    public Attr getAttributeNodeNS(String arg0, String arg1) {
        return delegateElement.getAttributeNodeNS(arg0, arg1);
    }

    @Override
    public String getAttributeNS(String arg0, String arg1) {
        return delegateElement.getAttributeNS(arg0, arg1);
    }

    @Override
    public NamedNodeMap getAttributes() {
        return delegateElement.getAttributes();
    }

    @Override
    public String getAttributeValue(Name arg0) {
        return delegateElement.getAttributeValue(arg0);
    }

    @Override
    public Iterator<jakarta.xml.soap.Node> getChildElements() {
        return delegateElement.getChildElements();
    }

    @Override
    public Iterator<jakarta.xml.soap.Node> getChildElements(Name arg0) {
        return delegateElement.getChildElements(arg0);
    }

    @Override
    public NodeList getChildNodes() {
        return delegateElement.getChildNodes();
    }

    @Override
    public Name getElementName() {
        return delegateElement.getElementName();
    }

    @Override
    public NodeList getElementsByTagName(String arg0) {
        return delegateElement.getElementsByTagName(arg0);
    }

    @Override
    public NodeList getElementsByTagNameNS(String arg0, String arg1) {
        return delegateElement.getElementsByTagNameNS(arg0, arg1);
    }

    @Override
    public String getEncodingStyle() {
        return delegateElement.getEncodingStyle();
    }

    @Override
    public Node getFirstChild() {
        return delegateElement.getFirstChild();
    }

    @Override
    public Node getLastChild() {
        return delegateElement.getLastChild();
    }

    @Override
    public String getLocalName() {
        return delegateElement.getLocalName();
    }

    @Override
    public Iterator<String> getNamespacePrefixes() {
        return delegateElement.getNamespacePrefixes();
    }

    @Override
    public String getNamespaceURI() {
        return delegateElement.getNamespaceURI();
    }

    @Override
    public String getNamespaceURI(String arg0) {
        return delegateElement.getNamespaceURI(arg0);
    }

    @Override
    public Node getNextSibling() {
        return delegateElement.getNextSibling();
    }

    @Override
    public String getNodeName() {
        return delegateElement.getNodeName();
    }

    @Override
    public short getNodeType() {
        return delegateElement.getNodeType();
    }

    @Override
    public String getNodeValue() throws DOMException {
        return delegateElement.getNodeValue();
    }

    @Override
    public Document getOwnerDocument() {
        return delegateElement.getOwnerDocument();
    }

    @Override
    public SOAPElement getParentElement() {
        return delegateElement.getParentElement();
    }

    @Override
    public Node getParentNode() {
        return delegateElement.getParentNode();
    }

    @Override
    public String getPrefix() {
        return delegateElement.getPrefix();
    }

    @Override
    public Node getPreviousSibling() {
        return delegateElement.getPreviousSibling();
    }

    @Override
    public String getTagName() {
        return delegateElement.getTagName();
    }

    @Override
    public String getValue() {
        return delegateElement.getValue();
    }

    @Override
    public Iterator<String> getVisibleNamespacePrefixes() {
        return delegateElement.getVisibleNamespacePrefixes();
    }

    @Override
    public boolean hasAttribute(String arg0) {
        return delegateElement.hasAttribute(arg0);
    }

    @Override
    public boolean hasAttributeNS(String arg0, String arg1) {
        return delegateElement.hasAttributeNS(arg0, arg1);
    }

    @Override
    public boolean hasAttributes() {
        return delegateElement.hasAttributes();
    }

    @Override
    public boolean hasChildNodes() {
        return delegateElement.hasChildNodes();
    }

    public int hashCode() {
        return delegateElement.hashCode();
    }

    @Override
    public Node insertBefore(Node arg0, Node arg1) throws DOMException {
        return delegateElement.insertBefore(arg0, arg1);
    }

    @Override
    public boolean isSupported(String arg0, String arg1) {
        return delegateElement.isSupported(arg0, arg1);
    }

    @Override
    public void normalize() {
        delegateElement.normalize();
    }

    @Override
    public void recycleNode() {
        delegateElement.recycleNode();
    }

    @Override
    public void removeAttribute(String arg0) throws DOMException {
        delegateElement.removeAttribute(arg0);
    }

    @Override
    public boolean removeAttribute(Name arg0) {
        return delegateElement.removeAttribute(arg0);
    }

    @Override
    public Attr removeAttributeNode(Attr arg0) throws DOMException {
        return delegateElement.removeAttributeNode(arg0);
    }

    @Override
    public void removeAttributeNS(String arg0, String arg1)
        throws DOMException {
        delegateElement.removeAttributeNS(arg0, arg1);
    }

    @Override
    public Node removeChild(Node arg0) throws DOMException {
        return delegateElement.removeChild(arg0);
    }

    @Override
    public void removeContents() {
        delegateElement.removeContents();
    }

    @Override
    public boolean removeNamespaceDeclaration(String arg0) {
        return delegateElement.removeNamespaceDeclaration(arg0);
    }

    @Override
    public Node replaceChild(Node arg0, Node arg1) throws DOMException {
        return delegateElement.replaceChild(arg0, arg1);
    }

    @Override
    public void setAttribute(String arg0, String arg1) throws DOMException {
        delegateElement.setAttribute(arg0, arg1);
    }

    @Override
    public Attr setAttributeNode(Attr arg0) throws DOMException {
        return delegateElement.setAttributeNode(arg0);
    }

    @Override
    public Attr setAttributeNodeNS(Attr arg0) throws DOMException {
        return delegateElement.setAttributeNodeNS(arg0);
    }

    @Override
    public void setAttributeNS(String arg0, String arg1, String arg2)
        throws DOMException {
        delegateElement.setAttributeNS(arg0, arg1, arg2);
    }

    @Override
    public void setEncodingStyle(String arg0) throws SOAPException {
        delegateElement.setEncodingStyle(arg0);
    }

    @Override
    public void setNodeValue(String arg0) throws DOMException {
        delegateElement.setNodeValue(arg0);
    }

    @Override
    public void setParentElement(SOAPElement arg0) throws SOAPException {
        delegateElement.setParentElement(arg0);
    }

    @Override
    public void setPrefix(String arg0) throws DOMException {
        delegateElement.setPrefix(arg0);
    }

    @Override
    public void setValue(String arg0) {
        delegateElement.setValue(arg0);
    }

    public String toString() {
        return delegateElement.toString();
    }


    // DOM L3 methods from org.w3c.dom.Node
    @Override
    public String getBaseURI() {
        return delegateElement.getBaseURI();
    }

    @Override
    public short compareDocumentPosition(org.w3c.dom.Node other)
                              throws DOMException {
        return delegateElement.compareDocumentPosition(other);
    }

    @Override
    public String getTextContent()
                      throws DOMException {
        return delegateElement.getTextContent();
    }

    @Override
    public void setTextContent(String textContent) throws DOMException {
        delegateElement.setTextContent(textContent);
    }

    @Override
    public boolean isSameNode(org.w3c.dom.Node other) {
        return delegateElement.isSameNode(other);
    }

    @Override
    public String lookupPrefix(String namespaceURI) {
        return delegateElement.lookupPrefix(namespaceURI);
    }

    @Override
    public boolean isDefaultNamespace(String namespaceURI) {
        return delegateElement.isDefaultNamespace(namespaceURI);
    }

    @Override
    public String lookupNamespaceURI(String prefix) {
        return  delegateElement.lookupNamespaceURI(prefix);
    }

    @Override
    public boolean isEqualNode(org.w3c.dom.Node arg) {
         return  delegateElement.isEqualNode(arg);
    }

    @Override
    public Object getFeature(String feature,
                             String version) {
         return  delegateElement.getFeature(feature,version);
    }

    @Override
    public Object setUserData(String key,
                              Object data,
                              UserDataHandler handler) {
        return  delegateElement.setUserData(key,data,handler);
    }

    @Override
    public Object getUserData(String key) {
        return  delegateElement.getUserData(key);
    }

    // DOM L3 methods from org.w3c.dom.Element

    @Override
    public void setIdAttribute(String name, boolean isId) throws DOMException {
        delegateElement.setIdAttribute(name, isId);
    }

    @Override
    public void setIdAttributeNode(Attr idAttr, boolean isId) throws DOMException {
        delegateElement.setIdAttributeNode(idAttr, isId);
    }

    @Override
    public void setIdAttributeNS(String namespaceURI, String localName, boolean isId) throws DOMException {
        delegateElement.setIdAttributeNS(namespaceURI, localName, isId);
    }

    @Override
    public TypeInfo getSchemaTypeInfo() {
        return  delegateElement.getSchemaTypeInfo();
    }

    @Override
    public Iterator<QName> getAllAttributesAsQNames() {
           return delegateElement.getAllAttributesAsQNames();
    }

}
