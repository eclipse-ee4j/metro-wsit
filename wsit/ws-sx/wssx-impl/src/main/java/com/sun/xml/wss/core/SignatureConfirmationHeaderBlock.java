/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.core;

import com.sun.xml.wss.impl.misc.SecurityHeaderBlockImpl;
import com.sun.xml.wss.impl.XMLUtil;
import com.sun.xml.wss.impl.MessageConstants;

import org.w3c.dom.Node;
import org.w3c.dom.Attr;

import jakarta.xml.soap.SOAPElement;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.Name;
import jakarta.xml.soap.SOAPFactory;

import com.sun.xml.wss.XWSSecurityException;

import java.util.Iterator;

/**
 * wsse11:SignatureConfirmation
 *
 * @author ashutosh.shahi@sun.com
 */
public class SignatureConfirmationHeaderBlock extends SecurityHeaderBlockImpl{

    private String signatureValue = null;
    private String wsuId = null;

    /** Creates a new instance of SignatureConfirmationHeaderblock */
    public SignatureConfirmationHeaderBlock(String wsuId, String signatureValue) {
        this.wsuId = wsuId;
        this.signatureValue = signatureValue;
    }

    public SignatureConfirmationHeaderBlock(SOAPElement element) throws XWSSecurityException {

        if(!(MessageConstants.SIGNATURE_CONFIRMATION_LNAME.equals(element.getLocalName()) &&
                XMLUtil.inWsse11NS(element))){
            throw new XWSSecurityException("Invalid SignatureConfirmation Header Block passed");
        }

        setSOAPElement(element);

        String wsuId = getAttributeNS(MessageConstants.WSU_NS, "Id");
        if (!"".equals(wsuId))
            setId(wsuId);

        String signatureValue = getAttribute("Value");
        try{
            if (!"".equals(signatureValue)){
                setSignatureValue(signatureValue);
            }
        } catch(Exception ex){
            throw new XWSSecurityException(ex);
        }

        Iterator children = getChildElements();
        Node object = null;

        while (children.hasNext()) {

            object = (Node)children.next();
            if (object.getNodeType() == Node.ELEMENT_NODE) {
                throw new XWSSecurityException("Child Element Nodes not allowed inside SignatureConfirmation");
            }else if(object.getNodeType() == Node.ATTRIBUTE_NODE){
                Attr attr = (Attr)object;
                if(!(("Id".equals(attr.getLocalName()) && MessageConstants.WSU_NS.equals(attr.getNamespaceURI())) ||
                        "Value".equals(attr.getLocalName()))){
                    throw new XWSSecurityException("The attribute " + attr.getLocalName() + "not allowed in SignatureConfirmation");
                }
            }
        }
    }

    public SignatureConfirmationHeaderBlock(String wsuId){

        this.wsuId = wsuId;
    }

    public static SecurityHeaderBlock fromSoapElement(SOAPElement element) throws XWSSecurityException{
        return SecurityHeaderBlockImpl.fromSoapElement(element,
                SignatureConfirmationHeaderBlock.class);
    }

     @Override
     public SOAPElement getAsSoapElement() throws XWSSecurityException {

        SOAPElement signConfirm;

        try {
            SOAPFactory sFactory = getSoapFactory();
            signConfirm =
                    sFactory.createElement(
                    MessageConstants.SIGNATURE_CONFIRMATION_LNAME,
                    MessageConstants.WSSE11_PREFIX,
                    MessageConstants.WSSE11_NS);

            signConfirm.addNamespaceDeclaration(
                    MessageConstants.WSSE11_PREFIX,
                    MessageConstants.WSSE11_NS);

            try{
            if(signatureValue != null){
                Name name = sFactory.createName("Value");
                signConfirm.addAttribute(name, signatureValue);
            }
            } catch(Exception ex){
                throw new XWSSecurityException(ex);
            }
            if(wsuId != null){
                Name name = sFactory.createName("Id", MessageConstants.WSU_PREFIX, MessageConstants.WSU_NS);
                signConfirm.addAttribute(name, wsuId);
            }
        } catch (SOAPException se) {
            throw new XWSSecurityException(
                    "There was an error creating Signature Confirmation " +
                    se.getMessage());
        }

        setSOAPElement(signConfirm);

        return signConfirm;
     }

    @Override
    public String getId() {
        return this.wsuId;
    }

    public void setId(String wsuId) {
        this.wsuId = wsuId;
    }

    public String getSignatureValue(){
        return this.signatureValue;
    }

    public void setSignatureValue(String signatureValue){
        this.signatureValue = signatureValue;
    }

}
