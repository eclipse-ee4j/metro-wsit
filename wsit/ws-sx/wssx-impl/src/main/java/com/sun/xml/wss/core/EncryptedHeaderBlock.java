/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * EncryptedHeaderBlock.java
 *
 * Created on October 13, 2006, 4:48 PM
 *
 */

package com.sun.xml.wss.core;

import com.sun.xml.wss.XWSSecurityException;
import com.sun.xml.wss.impl.MessageConstants;
import com.sun.xml.wss.impl.SecurableSoapMessage;
import com.sun.xml.wss.impl.misc.SOAPElementExtension;
import com.sun.xml.wss.logging.LogDomainConstants;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.xml.soap.Name;
import jakarta.xml.soap.SOAPElement;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.TypeInfo;
import org.w3c.dom.UserDataHandler;

/**
 * Corresponds to Schema definition for EncryptedData. 
 * Schema definition for EncryptedData is as follows:
 * <p>
 * <pre>{@code
 * <xmp>
 * <element name='EncryptedHeader' type='wsse11:EncryptedHeaderType'/>
 * <complexType name='EncryptedHeaderType'>
 *   <element name='EncryptedData'>
 *     <complexContent>
 *         <extension base='xenc:EncryptedType'/>
 *     </complexContent>
 *   </element>
 * </complexType>
 * </xmp>
 * }</pre>
 *
 * @author Mayank Mishra
 */
public class EncryptedHeaderBlock extends SOAPElementExtension implements SOAPElement  {
    
        private static Logger log =
        Logger.getLogger(
            LogDomainConstants.WSS_API_DOMAIN,
            LogDomainConstants.WSS_API_DOMAIN_BUNDLE);
        
        protected SOAPElement delegateElement;
        private static SOAPFactory soapFactory;
        
        private static final Name idAttributeName;
        //private Document ownerDoc;
        
        private boolean bsp=false;
        
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
                    "WSS0654.soap.exception",
                    e.getMessage());
        }

        idAttributeName = temp;
    }

    public EncryptedHeaderBlock(Document doc) throws XWSSecurityException {
                try {
            setSOAPElement(
                (SOAPElement) doc.createElementNS(
                    MessageConstants.WSSE11_NS,
                    MessageConstants.ENCRYPTED_HEADER_QNAME));
            addNamespaceDeclaration(
                MessageConstants.WSSE11_PREFIX,
                MessageConstants.WSSE11_NS);
        } catch (SOAPException e) {
            log.log(Level.SEVERE, "WSS0360.error.creating.ehb", e.getMessage());
            throw new XWSSecurityException(e);
        }
        //ownerDoc = doc;
    }
    
    /** Creates a new instance of EncryptedHeaderBlock */
    public EncryptedHeaderBlock(SOAPElement delegateElement) throws XWSSecurityException {
        setSOAPElement(delegateElement);
                        try {
            setSOAPElement(
                getSoapFactory().createElement(
                    "EncryptedHeader",
                    MessageConstants.WSSE11_PREFIX,
                    MessageConstants.WSSE11_NS));
            addNamespaceDeclaration(
                MessageConstants.WSSE11_PREFIX,
                MessageConstants.WSSE11_NS);
                        
        } catch (SOAPException e) {
            log.log(Level.SEVERE, "WSS0360.error.creating.ehb", e.getMessage());
            throw new XWSSecurityException(e);
        }
        //ownerDoc = delegateElement.getOwnerDocument();
    }
    
    protected void setSOAPElement(SOAPElement delegateElement) {
        this.delegateElement = delegateElement;
    }
    
    public void copyAttributes(final SecurableSoapMessage secureMsg, final SecurityHeader _secHeader) throws XWSSecurityException{
                
        String SOAP_namespace = secureMsg.getEnvelope().getNamespaceURI();
        String SOAP_prefix = secureMsg.getEnvelope().getPrefix();
        String value_mustUnderstand= _secHeader.getAttributeNS(SOAP_namespace, "mustUnderstand");
        String value_S12_role= _secHeader.getAttributeNS(SOAP_namespace, "role");
        String value_S11_actor = _secHeader.getAttributeNS(SOAP_namespace, "actor");
        String value_S12_relay = _secHeader.getAttributeNS(SOAP_namespace, "relay");
        
        if(value_mustUnderstand!=null && !value_mustUnderstand.equals("")){
            this.setAttributeNS(SOAP_namespace, SOAP_prefix+":mustUnderstand", value_mustUnderstand);
        }
        if(value_S12_role!=null && !value_S12_role.equals("")){
            this.setAttributeNS(SOAP_namespace, SOAP_prefix+":role", value_S12_role);
        }
        if(value_S11_actor!=null && !value_S11_actor.equals("")){
            this.setAttributeNS(SOAP_namespace, SOAP_prefix+":actor", value_S11_actor);
        }
        if(value_S12_relay!=null&&!value_S12_relay.equals("")){
            this.setAttributeNS(SOAP_namespace, SOAP_prefix+":relay", value_S12_relay);
        }
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
    
    protected static SOAPFactory getSoapFactory() {
        return soapFactory;
    }
    
    /**
     * Returns null if id attr is not present
     */
    public String getId() {
        String id = getAttribute("Id");
        if (id.equals(""))
            return null;
        return id;
    }

    public void setId(String id) {
        setAttribute("Id", id);
        setIdAttribute("Id", true);
    }
    
        /**
     * Returns null if Type attr is not present
     */
    public String getType() {
        String type = getAttribute("Type");
        if (type.equals(""))
            return null;
        return type;
    }

    public void setType(String type) {
        setAttribute("Type", type);
    }

    /**
     * Returns null if MimeType attr is not present
     */
    public String getMimeType() {
        String mimeType = getAttribute("MimeType");
        if (mimeType.equals(""))
            return null;
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        setAttribute("MimeType", mimeType);
    }
    
//    public String getId() {
//        return delegateElement.getAttributeValue(idAttributeName);
//    }
//
    public SOAPElement getAsSoapElement() {
        return delegateElement;
    }

    @Override
    public SOAPElement addChildElement(Name name) throws SOAPException {
        return delegateElement.addChildElement(name);
    }

    @Override
    public SOAPElement addChildElement(String string) throws SOAPException {
        return delegateElement.addChildElement(string);
    }

    @Override
    public SOAPElement addChildElement(String string, String string0) throws SOAPException {
        return delegateElement.addChildElement(string, string0);
    }

    @Override
    public SOAPElement addChildElement(String string, String string0, String string1) throws SOAPException {
        return delegateElement.addChildElement(string, string0, string1);
    }

    @Override
    public SOAPElement addChildElement(SOAPElement sOAPElement) throws SOAPException {
        return delegateElement.addChildElement(sOAPElement);
    }

    @Override
    public void removeContents() {
        delegateElement.removeContents();
    }

    @Override
    public SOAPElement addTextNode(String string) throws SOAPException {
        return delegateElement.addTextNode(string);
    }

    @Override
    public SOAPElement addAttribute(Name name, String string) throws SOAPException {
        return delegateElement.addAttribute(name, string);
    }

    @Override
    public SOAPElement addNamespaceDeclaration(String string, String string0) throws SOAPException {
        return delegateElement.addNamespaceDeclaration(string, string0);
    }

    @Override
    public String getAttributeValue(Name name) {
        return delegateElement.getAttributeValue(name);
    }

    @Override
    public Iterator getAllAttributes() {
        return delegateElement.getAllAttributes();
    }

    @Override
    public Iterator getAllAttributesAsQNames() {
         return delegateElement.getAllAttributesAsQNames();
    }

    @Override
    public String getNamespaceURI(String string) {
        return delegateElement.getNamespaceURI(string);
    }

    @Override
    public Iterator getNamespacePrefixes() {
        return delegateElement.getNamespacePrefixes();
    }

    @Override
    public Iterator getVisibleNamespacePrefixes() {
        return delegateElement.getVisibleNamespacePrefixes();
    }

    @Override
    public Name getElementName() {
        return delegateElement.getElementName();
    }

    @Override
    public boolean removeAttribute(Name name) {
        return delegateElement.removeAttribute(name);
    }

    @Override
    public boolean removeNamespaceDeclaration(String string) {
        return delegateElement.removeNamespaceDeclaration(string);
    }

    @Override
    public Iterator getChildElements() {
        return delegateElement.getChildElements();
    }

    @Override
    public Iterator getChildElements(Name name) {
        return delegateElement.getChildElements(name);
    }

    @Override
    public void setEncodingStyle(String string) throws SOAPException {
        delegateElement.setEncodingStyle(string);
    }

    @Override
    public String getEncodingStyle() {
        return delegateElement.getEncodingStyle();
    }

    @Override
    public String getValue() {
        return delegateElement.getValue();
    }

    @Override
    public void setValue(String string) {
        delegateElement.setValue(string);
    }

    @Override
    public void setParentElement(SOAPElement sOAPElement) throws SOAPException {
        delegateElement.setParentElement(sOAPElement);
    }

    @Override
    public SOAPElement getParentElement() {
        return delegateElement.getParentElement();
    }

    @Override
    public void detachNode() {
        delegateElement.detachNode();
    }

    @Override
    public void recycleNode() {
        delegateElement.recycleNode();
    }

    @Override
    public String getNodeName() {
        return delegateElement.getNodeName();
    }

    @Override
    public String getNodeValue() throws DOMException {
        return delegateElement.getNodeValue();
    }

    @Override
    public void setNodeValue(String nodeValue) throws DOMException {
        delegateElement.setNodeValue(nodeValue);                
    }

    @Override
    public short getNodeType() {
        return delegateElement.getNodeType();
    }

    @Override
    public Node getParentNode() {
        return delegateElement.getParentNode();
    }

    @Override
    public NodeList getChildNodes() {
        return delegateElement.getChildNodes();
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
    public Node getPreviousSibling() {
        return delegateElement.getPreviousSibling();
    }

    @Override
    public Node getNextSibling() {
        return delegateElement.getNextSibling();
    }

    @Override
    public NamedNodeMap getAttributes() {
        return delegateElement.getAttributes();
    }

    @Override
    public Document getOwnerDocument() {
        return delegateElement.getOwnerDocument();
    }

    @Override
    public Node insertBefore(Node newChild, Node refChild) throws DOMException {
        return delegateElement.insertBefore(newChild, refChild);
    }

    @Override
    public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
        return delegateElement.replaceChild(newChild, oldChild);
    }

    @Override
    public Node removeChild(Node oldChild) throws DOMException {
        return delegateElement.removeChild(oldChild);
    }

    @Override
    public Node appendChild(Node newChild) throws DOMException {
        return delegateElement.appendChild(newChild);
    }

    @Override
    public boolean hasChildNodes() {
        return delegateElement.hasChildNodes();
        
    }

    @Override
    public Node cloneNode(boolean deep) {
        return delegateElement.cloneNode(deep);
    }

    @Override
    public void normalize() {
        delegateElement.normalize();
    }

    @Override
    public boolean isSupported(String feature, String version) {
        return delegateElement.isSupported(feature, version);
    }

    @Override
    public String getNamespaceURI() {
        return delegateElement.getNamespaceURI();
    }

    @Override
    public String getPrefix() {
        return delegateElement.getPrefix();
    }

    @Override
    public void setPrefix(String prefix) throws DOMException {
        delegateElement.setPrefix(prefix);
    }

    @Override
    public String getLocalName() {
        return delegateElement.getLocalName();
    }

    @Override
    public boolean hasAttributes() {
        return delegateElement.hasAttributes();
    }

    @Override
    public String getBaseURI() {
        return delegateElement.getBaseURI();
    }

    @Override
    public short compareDocumentPosition(Node other) throws DOMException {
        return delegateElement.compareDocumentPosition(other);
    }

    @Override
    public String getTextContent() throws DOMException {
        return delegateElement.getTextContent();
    }

    @Override
    public void setTextContent(String textContent) throws DOMException {
        delegateElement.setTextContent(textContent);
    }

    @Override
    public boolean isSameNode(Node other) {
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
        return delegateElement.lookupNamespaceURI(prefix);
    }

    @Override
    public boolean isEqualNode(Node arg) {
        return delegateElement.isEqualNode(arg);
    }

    @Override
    public Object getFeature(String feature, String version) {
        return delegateElement.getFeature(feature, version);
    }

    @Override
    public Object setUserData(String key, Object data, UserDataHandler handler) {
        return delegateElement.setUserData(key, data, handler);
    }

    @Override
    public Object getUserData(String key) {
        return delegateElement.getUserData(key);
    }

    @Override
    public String getTagName() {
        return delegateElement.getTagName();
    }

    @Override
    public String getAttribute(String name) {
        return delegateElement.getAttribute(name);
    }

    @Override
    public void setAttribute(String name, String value) throws DOMException {
        delegateElement.setAttribute(name, value);                
    }

    @Override
    public void removeAttribute(String name) throws DOMException {
        delegateElement.removeAttribute(name);
    }

    @Override
    public Attr getAttributeNode(String name) {
        return delegateElement.getAttributeNode(name);
    }

    @Override
    public Attr setAttributeNode(Attr newAttr) throws DOMException {
        return delegateElement.setAttributeNode(newAttr);
    }

    @Override
    public Attr removeAttributeNode(Attr oldAttr) throws DOMException {
        return delegateElement.removeAttributeNode(oldAttr);
    }

    @Override
    public NodeList getElementsByTagName(String name) {
        return delegateElement.getElementsByTagName(name);
    }

    @Override
    public String getAttributeNS(String namespaceURI, String localName) throws DOMException {
        return delegateElement.getAttributeNS(namespaceURI, localName);
    }

    @Override
    public void setAttributeNS(String namespaceURI, String qualifiedName, String value) throws DOMException {
       delegateElement.setAttributeNS(namespaceURI, qualifiedName, value); 
        
    }

    @Override
    public void removeAttributeNS(String namespaceURI, String localName) throws DOMException {
        delegateElement.removeAttributeNS(namespaceURI, localName);
    }

    @Override
    public Attr getAttributeNodeNS(String namespaceURI, String localName) throws DOMException {
        return delegateElement.getAttributeNodeNS(namespaceURI, localName);
    }

    @Override
    public Attr setAttributeNodeNS(Attr newAttr) throws DOMException {
        return delegateElement.setAttributeNodeNS(newAttr);
    }

    @Override
    public NodeList getElementsByTagNameNS(String namespaceURI, String localName) throws DOMException {
        return delegateElement.getElementsByTagNameNS(namespaceURI, localName);
    }

    @Override
    public boolean hasAttribute(String name) {
        return delegateElement.hasAttribute(name);
    }

    @Override
    public boolean hasAttributeNS(String namespaceURI, String localName) throws DOMException {
        return delegateElement.hasAttributeNS(namespaceURI, localName);
    }

    @Override
    public TypeInfo getSchemaTypeInfo() {
        return delegateElement.getSchemaTypeInfo();
    }

    @Override
    public void setIdAttribute(String name, boolean isId) throws DOMException {
        delegateElement.setIdAttribute(name, isId);
    }

    @Override
    public void setIdAttributeNS(String namespaceURI, String localName, boolean isId) throws DOMException {
        delegateElement.setIdAttributeNS(namespaceURI, localName, isId);
    }

    @Override
    public void setIdAttributeNode(Attr idAttr, boolean isId) throws DOMException {
        delegateElement.setIdAttributeNode(idAttr, isId);
    }
    
    public void isBSP(boolean flag) {
        bsp = flag;
    }

    public boolean isBSP() {
        return bsp;
    }
    
}
