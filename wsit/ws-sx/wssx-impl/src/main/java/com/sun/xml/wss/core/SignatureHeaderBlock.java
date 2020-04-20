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
 * $Id: SignatureHeaderBlock.java,v 1.2 2010-10-21 15:37:11 snajper Exp $
 */

package com.sun.xml.wss.core;

import java.security.Key;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.xml.soap.SOAPElement;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.apache.xml.security.utils.ElementProxy;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.signature.SignedInfo;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.signature.ObjectContainer;
import org.apache.xml.security.signature.XMLSignatureException;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.utils.resolver.ResourceResolverSpi;
import com.sun.xml.wss.logging.LogDomainConstants;
import com.sun.xml.wss.impl.MessageConstants;

import com.sun.xml.wss.XWSSecurityException;
import com.sun.xml.wss.impl.misc.SecurityHeaderBlockImpl;

/**
    <element name="Signature" type="ds:SignatureType"/>
    <complexType name="SignatureType">
    <sequence>
        <element ref="ds:SignedInfo"/>
        <element ref="ds:SignatureValue"/>
        <element ref="ds:KeyInfo" minOccurs="0"/>
        <element ref="ds:Object" minOccurs="0" maxOccurs="unbounded"/>
    </sequence>
    <attribute name="Id" type="ID" use="optional"/>
    </complexType>
 */
public class SignatureHeaderBlock  extends SecurityHeaderBlockImpl {

    public static final String SignatureSpecNS = MessageConstants.DSIG_NS;

    public static final String SignatureSpecNSprefix =
        MessageConstants.DSIG_PREFIX;

    public static final String TAG_SIGNATURE = "Signature";

    // delegate ds:Signature member from XML DSIG
    XMLSignature delegateSignature = null;

    boolean dirty = false;

    private static Logger log =
        Logger.getLogger(
            LogDomainConstants.WSS_API_DOMAIN,
            LogDomainConstants.WSS_API_DOMAIN_BUNDLE);

    /**
     *  baseURI URI to be used as context for all relative URIs.
     *  Accepted by all Apache XMLSIG elements
     */
    String baseURI = null;

    /**
     * The Owner Document of this Signature
     */
    private Document document = null;

    /** 
     * parse and create the Signature element
     * @param elem the element representing an XML Signature
     * NOTE : this constructor assumes a fully initialized XML Signature
     * No modifications are allowed on the signature, We can only get existing
     * values. For example appendObject() would throw an Exception. If
     * a KeyInfo was not present in the signature, then calling getKeyInfo()
     * will not append a KeyInfo child to the signature.
     */
    public SignatureHeaderBlock(SOAPElement elem) throws XWSSecurityException {
        super(elem);
        try {
            this.document = elem.getOwnerDocument();
            delegateSignature = new XMLSignature(elem, null);
        } catch (Exception e) {
            // add log here
            log.log(
                Level.SEVERE, 
                "WSS0322.exception.creating.signatureblock",
                e);    
            throw new XWSSecurityException(e);
        }       
    }

    /** 
     * constructor that takes Apache Signature
     * @param signature the XMLSignature from XML DSIG
     * NOTE : No modifications are allowed on the signature, 
     * if a SIGN operation has already been performed on the argument
     * signature. We can only get existing values. 
     * For example appendObject() would throw an Exception. If 
     * a KeyInfo was not present in the signature, then calling getKeyInfo()
     * will not append a KeyInfo child to the signature.
     */
    public SignatureHeaderBlock(XMLSignature signature)
        throws XWSSecurityException {
        this.document = signature.getDocument();
        delegateSignature = signature;
        dirty = true;
        setSOAPElement(getAsSoapElement());
    }

    /**
     * This creates a new <CODE>ds:Signature</CODE> Element and adds an empty
     * <CODE>ds:SignedInfo</CODE>.
     * The <code>ds:SignedInfo</code> is initialized with the specified
     * Signature algorithm and Canonicalizer.ALGO_ID_C14N_EXCL_OMIT_COMMENTS
     * which is RECOMMENDED by the spec. This method's main use is for creating
     * a new signature.
     *
     * @param doc The OwnerDocument of signature 
     * @param signatureMethodURI signature algorithm to use.
     * @throws XWSSecurityException
     */
    public SignatureHeaderBlock(Document doc, String signatureMethodURI) 
        throws XWSSecurityException {
        try {
            this.document = doc;
            delegateSignature = 
                new XMLSignature(
                    doc, null, signatureMethodURI, 
                    Canonicalizer.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);     
            dirty = true;
            setSOAPElement(getAsSoapElement());
        } catch (XMLSecurityException e) {
            log.log(
                Level.SEVERE, 
                "WSS0322.exception.creating.signatureblock",
                e);    
            throw new XWSSecurityException(e);
        }
    } 

    /**
     * return the Apache XML Signature corresponding to this Block
     * @return the XMLSignature
     */
    public XMLSignature getSignature() {
        return delegateSignature;
    }


    /**
     * Digests all References in the SignedInfo, calculates the signature 
     * value and sets it in the SignatureValue Element.
     *
     * @param signingKey the {@link java.security.PrivateKey} or 
     *     {@link javax.crypto.SecretKey} that is used to sign.
     * @throws XWSSecurityException
     */

     public void sign(Key signingKey) throws XWSSecurityException {
         try {
             delegateSignature.sign(signingKey);         
             dirty = true;
         }catch (XMLSignatureException e) {
             log.log(Level.SEVERE, "WSS0323.exception.while.signing", e);
             throw new XWSSecurityException(e);
         }
     }

    /**
     * Returns the completely parsed <code>SignedInfo</code> object.
     *
     * @return the SignedInfo as a SOAPElement
     */
    public SOAPElement getSignedInfo() throws XWSSecurityException {
        return convertToSoapElement(delegateSignature.getSignedInfo());
    }

    public SignedInfo getDSSignedInfo() {
        return delegateSignature.getSignedInfo();
    }

    /**
     * Returns the KeyInfo child.
     *
     * @return the KeyInfo object
     */
    public SOAPElement getKeyInfo() throws XWSSecurityException {
        return convertToSoapElement(delegateSignature.getKeyInfo());
    }

    /**
     * Returns the KeyInfo as a HeaderBlock.
     *
     * @return the KeyInfoHeaderBlock object
     */
    public KeyInfoHeaderBlock  getKeyInfoHeaderBlock()
        throws XWSSecurityException {
        return new KeyInfoHeaderBlock(delegateSignature.getKeyInfo());
    }

    /**
     * Method getSignatureValue
     * @throws XWSSecurityException
     */
    public byte[] getSignatureValue() throws XWSSecurityException {
        try {
            return delegateSignature.getSignatureValue();
        } catch (XMLSignatureException e) {
            log.log(
                Level.SEVERE, 
                "WSS0324.exception.in.getting.signaturevalue", 
                e);
            throw new XWSSecurityException(e);
        }
    }

    /**
     * Adds a Reference with just the URI and the transforms. This uses the
     * SHA1 algorithm as a default digest algorithm.
     *
     * @param referenceURI URI according to the XML Signature specification.
     * @param transforms List of transformations to be applied.
     * @throws XWSSecurityException
     */
    public void addSignedInfoReference(
        String referenceURI, Transforms transforms)
           throws XWSSecurityException {
        try {
            delegateSignature.addDocument(referenceURI, transforms);
            dirty = true;
        } catch (XMLSecurityException e) {
            log.log(
                Level.SEVERE, 
                "WSS0325.exception.adding.reference.to.signedinfo", 
                e);
            throw new XWSSecurityException(e);
        }
    }

    /**
     * Adds a Reference with URI, transforms and Digest algorithm URI
     * 
     * @param referenceURI URI according to the XML Signature specification.
     * @param trans List of transformations to be applied.
     * @param digestURI URI of the digest algorithm to be used.
     * @throws XWSSecurityException
     */
    public void addSignedInfoReference(
        String referenceURI, Transforms trans, String digestURI)
        throws XWSSecurityException {
        try {
            delegateSignature.addDocument(referenceURI, trans, digestURI);
            dirty = true;
        } catch (XMLSecurityException e) {
            log.log(
                Level.SEVERE, 
                "WSS0325.exception.adding.reference.to.signedinfo", 
                e);
            throw new XWSSecurityException(e);
        }
    }
          


    /**
     * Add a Reference with full parameters to this Signature
     *
     * @param referenceURI URI of the resource to be signed.Can be null in which     
     * case the dereferencing is application specific. Can be "" in which it's
     * the parent node (or parent document?). There can only be one "" in each
     * signature.
     * @param trans Optional list of transformations to be done before digesting
     * @param digestURI Mandatory URI of the digesting algorithm to use.
     * @param referenceId Optional id attribute for this Reference
     * @param referenceType Optional mimetype for the URI
     * @throws XWSSecurityException 
     */
    public void addSignedInfoReference(
       String referenceURI, Transforms trans, String digestURI, 
       String referenceId, String referenceType)
       throws XWSSecurityException {
        try {
            delegateSignature.addDocument(referenceURI, trans, digestURI,
                referenceId, referenceType);
            dirty = true;
        } catch (XMLSecurityException e) {
            log.log(
                Level.SEVERE, 
                "WSS0325.exception.adding.reference.to.signedinfo", 
                e);
            throw new XWSSecurityException(e);
        }
   }

    /**
     * Extracts the public key from the certificate and verifies if the              
     * signature is valid by re-digesting all References, comparing those            
     * against the stored DigestValues and then checking to see if the               
     * Signatures match on the SignedInfo.
     *
     * @param cert Certificate that contains the public key part of the keypair      
     * that was used to sign.
     * @return true if the signature is valid, false otherwise
     * @throws XWSSecurityException
     */
    public boolean checkSignatureValue(X509Certificate cert)
           throws XWSSecurityException {
        try {
            return delegateSignature.checkSignatureValue(cert);         
        } catch (XMLSignatureException e) {
            log.log(Level.SEVERE, "WSS0326.exception.verifying.signature", e);
            throw new XWSSecurityException(e);
        }
    }

    /**
     * Verifies if the signature is valid by redigesting all References,
     * comparing those against the stored DigestValues and then checking to see
     * if the Signatures match on the SignedInfo.
     *
     * @param pk {@link java.security.PublicKey} part of the keypair or              
     * {@link javax.crypto.SecretKey} that was used to sign
     * @return true if the signature is valid, false otherwise
     * @throws XWSSecurityException
     */
    public boolean checkSignatureValue(Key pk) throws XWSSecurityException {
        try {
            return delegateSignature.checkSignatureValue(pk);         
        }catch (XMLSignatureException e) {
            log.log(Level.SEVERE, "WSS0326.exception.verifying.signature", e);
            throw new XWSSecurityException(e);
        }
    }

    /**
     * Method appendObject.
     */
    public void appendObject(SOAPElement object) throws XWSSecurityException {
        try {
            ObjectContainer objc = new ObjectContainer(object, null);
            delegateSignature.appendObject(objc);
        } catch (XMLSecurityException e) {
            log.log(Level.SEVERE, "WSS0382.error.appending.object", e.getMessage());
            throw new XWSSecurityException(e);
        }
    }

    /**
     * Returns the <code>index<code>th <code>ds:Object</code> child of the 
     * signature or null if no such <code>ds:Object</code> element exists.
     *
     * @param index
     * @return the <code>index<code>th <code>ds:Object</code> child of the 
     * signature or null if no such <code>ds:Object</code> element exists.
     * 1 is the lowest index (not 0)
     */
    public SOAPElement getObjectItem(int index) throws XWSSecurityException {
        return convertToSoapElement(delegateSignature.getObjectItem(index));
    }

    /**
     * Returns the number of all <code>ds:Object</code> elements.
     *
     * @return the number of all <code>ds:Object</code> elements.
     */
    public int getObjectCount() {
      return delegateSignature.getObjectLength();
    }

    /**
     * Method setId
     */
    public void setId(String id) {
        delegateSignature.setId(id);
    }

    /**
     * Method getId
     *
     * @return the id
     */
    public String getId() {
       return delegateSignature.getId();
    }

    /**
     * Method setBaseURI :  BaseURI needed by Apache KeyInfo Ctor
     * @param uri URI to be used as context for all relative URIs.
     */
    public void setBaseURI(String uri) {
        baseURI = uri;
    }

    /**
     * Method to return the Signature as a SOAPElement
     *
     * @return SOAPElement
     * @throws XWSSecurityException
     *     If owner soap document is not set.
     * @see #setDocument(Document)
     */
    public SOAPElement getAsSoapElement() throws XWSSecurityException {
        if (document == null) {
            log.log(Level.SEVERE, "WSS0383.document.not.set");
            throw new XWSSecurityException("Document not set");
        }
        if (dirty) {
            setSOAPElement(convertToSoapElement(delegateSignature));
            dirty = false;
        }
        return delegateElement;
    }

    /**
     * setDocument.
     * @param doc The owner Document of this Signature
     */
    public void setDocument(Document doc) {
        this.document = doc;
    }

   /**
    * This method should be called when changes are made inside an object
    * through its reference obtained from any of the get methods of this
    * class. As an example, if getKeyInfo() call is made and then changes are made
    * inside the keyInfo, this method should be called to reflect changes
    * when getAsSoapElement() is called finally.
    */
    public void saveChanges() {
        dirty = true;
    }

    /*
     * Add resolver for this instance of XMLSignature
     */
    public void setApacheResourceResolver(ResourceResolverSpi resolver) {
       this.delegateSignature.addResourceResolver(resolver);
    }

    public static SecurityHeaderBlock fromSoapElement(SOAPElement element) 
        throws XWSSecurityException {
        return SecurityHeaderBlockImpl.fromSoapElement(element, 
            SignatureHeaderBlock.class);
    }

    private SOAPElement convertToSoapElement(ElementProxy proxy)
        throws XWSSecurityException {
        try {
            Element elem = proxy.getElement();
            if (elem instanceof SOAPElement) {
                return (SOAPElement) elem;
            } else {
                return (SOAPElement) document.importNode(elem, true);
            }
        } catch (Exception e) {
            log.log(
                Level.SEVERE, 
                "WSS0327.exception.converting.signature.tosoapelement", 
                e);
            throw new XWSSecurityException(e);
        }
    }
    
}
