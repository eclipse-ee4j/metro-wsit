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
 * $Id: Target.java,v 1.2 2010-10-21 15:37:34 snajper Exp $
 */

package com.sun.xml.wss.impl.policy.mls;

import javax.xml.namespace.QName;
import com.sun.xml.wss.impl.MessageConstants;

/*
 * Target specifies the qname or xpath or id to identify the
 * &lt;target&gt; element to apply the Sign/Encrypt Security Policy on.
 */
public class Target {

    protected static final String TARGET_VALUE_SOAP_BODY="SOAP-BODY";

    /**
     * type-identifier  for qname Target Type
     */
    public static final String TARGET_TYPE_VALUE_QNAME  = "qname";
    /**
     * type-identifier for xpath Target Type
     */
    public static final String TARGET_TYPE_VALUE_XPATH  = "xpath";
    /**
     * type-identifier for uri Target Type
     */
    public static final String TARGET_TYPE_VALUE_URI  = "uri";
    /**
     * All Message Headers targeted at ultimate receiver role should be
     * integrity protected.
     */
    public static final String ALL_MESSAGE_HEADERS ="ALL_MESSAGE_HEADERS";
    public static final String BODY = "{" + MessageConstants.SOAP_1_1_NS + "}Body";
    public static final String BODY1_2 = "{"+MessageConstants.SOAP_1_2_NS+"}Body";
    public static final QName BODY_QNAME = new QName(MessageConstants.SOAP_1_1_NS ,"Body");
    public static final QName SIGNATURE_CONFIRMATION = new QName(MessageConstants.WSSE11_NS,MessageConstants.SIGNATURE_CONFIRMATION_LNAME);

    private String type = TARGET_TYPE_VALUE_QNAME;

    private String value = BODY;
    private boolean contentOnly = true;
    private boolean enforce = true;
    private String  xpathExpr = null;
    private QName qname = null;
    private boolean attachment = false;
    boolean bsp = false;
    boolean headersOnly = false;
    private String xpathVersion;
    private QName policyQName = null;

    /**
     * Default constructor
     * When used, it creates a default Target of type <code>qname</code> and a value of
     * {http://schemas.xmlsoap.org/soap/envelope/}Body
     */
    public Target() {
    }

    /**
     * Constructor
     * @param type the type of the Target (should be one of TARGET_TYPE_VALUE_QNAME, TARGET_TYPE_VALUE_XPATH, TARGET_TYPE_VALUE_URI)
     * @param value the value of the Target
     */
    public Target(String type, String value) {
        this.type = type;
        this.value = value;
        if (TARGET_TYPE_VALUE_QNAME.equals(type) && TARGET_VALUE_SOAP_BODY.equals(value)) {
            this.value = Target.BODY;
        }
    }

    /**
     * Constructor
     * @param type the type of the Target (should be one of TARGET_TYPE_VALUE_QNAME, TARGET_TYPE_VALUE_XPATH, TARGET_TYPE_VALUE_URI)
     * @param value the value of the Target
     * @param contentOnly the content-only flag. This flag is used to decide whether the whole Target or only its Markup(content) should
     * be Encrypted.
     */
    public Target(String type, String value, boolean contentOnly) {
        this.type = type;
        this.value = value;
        this.contentOnly = contentOnly;
        if (TARGET_TYPE_VALUE_QNAME.equals(type) && TARGET_VALUE_SOAP_BODY.equals(value)) {
            this.value = Target.BODY;
        }
    }

    /**
     * Constructor
     * @param type the type of the Target (should be one of TARGET_TYPE_VALUE_QNAME, TARGET_TYPE_VALUE_XPATH, TARGET_TYPE_VALUE_URI)
     * @param value the value of the Target
     * @param contentOnly the content-only flag. This flag is used to decide whether the whole Target or only its Markup(content) should
     * be Encrypted.
     * @param enforce when set to false, will cause the enclosing policy (SignaturePolicy/EncryptionPolicy) to consider the presence of
     * this Target reference as optional, while verifying the Policy on the Receiver side.
     *
     */
    public Target(String type, String value, boolean contentOnly, boolean enforce) {
        this.type = type;
        this.value = value;
        this.contentOnly = contentOnly;
        this.enforce = enforce;
        if (TARGET_TYPE_VALUE_QNAME.equals(type) && TARGET_VALUE_SOAP_BODY.equals(value)) {
            this.value = Target.BODY;
        }
    }

    /**
     * set the enforcement flag, used when verifying Security on an inbound message.
     *@param enforce if set to True indicates that this Target is a compulsary target under the Policy in which
     * it appears.
     */
    public void setEnforce(boolean enforce) {
        this.enforce = enforce;
    }

    /**
     *@return true if this Target appearing under a Policy should be enforced, false
     * if it is optional.
     */
    public boolean getEnforce() {
        return enforce;
    }

    /**
     * @param headersOnly is set to true, indicates only headers should be processed by
     * this target
     * To be set by Policy
     */
    public void isSOAPHeadersOnly(boolean headersOnly){
        this.headersOnly = headersOnly;
    }

    /**
     * @return true if only the headers should be processed, false otherwise
     * default is false
     */
    public boolean isSOAPHeadersOnly(){
        return headersOnly;
    }

   /*
    * Checks whether BSP checks are enabled for this target.
    */
    public void isBSP(boolean flag) {
        bsp = flag;
    }

    /*
     */
    public boolean isBSP() {
        return bsp;
    }


    /**
     *@return the type of the Target
     */
    public String getType() {
        return type;
    }

    /**
     * set the type of the Target
     * @param type the type of the Target
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     *@return the value of the Target
     */
    public String getValue() {
        return value;
    }

    /**
     * set the value of the Target
     * @param value the value of the Target
     */
    public void setValue(String value) {
        this.value = value;
        xpathExpr = null;


        if(BODY1_2.equals(value)|| BODY.equals(value)){
            this.value = Target.BODY;
        }

        if (TARGET_TYPE_VALUE_QNAME.equals(type) && TARGET_VALUE_SOAP_BODY.equals(value)) {
            this.value = Target.BODY;
        }

        if(value != null && ( value.startsWith("cid:") || value.startsWith(MessageConstants.ATTACHMENTREF)) ){
            attachment = true;
            if(value.equals("cid:*")){
                this.value = MessageConstants.PROCESS_ALL_ATTACHMENTS;
            }
        }
    }

    /**
     * set the contentOnly flag on the Target
     * @param contentOnly the boolean flag indicating content-only when set to true.
     */
    public void setContentOnly(boolean contentOnly) {
        this.contentOnly = contentOnly;
    }

    /**
     *@return true if the contentOnly flag on the Target was set, false otherwise
     */
    public boolean getContentOnly() {
        return contentOnly;
    }

    /**
     *@return the Target value as a String representing an XPath expression
     */
    public String convertToXPATH(){
        if(xpathExpr == null){
            xpathExpr = convertToXpath(value);
        }
        return xpathExpr;
    }

    private String convertToXpath(String qname) {
        QName name = QName.valueOf(qname);
        if ("".equals(name.getNamespaceURI())) {
            return "//" + name.getLocalPart();
        } else {
            return "//*[local-name()='"
                    + name.getLocalPart()
                    + "' and namespace-uri()='"
                    + name.getNamespaceURI()
                    + "']";
        }
    }

    /**
     * Set the Target QName.
     */
    public void setQName(QName qname ) {
        this.type = TARGET_TYPE_VALUE_QNAME;
        this.value = qname.toString();
        this.qname = qname;
        this.value  = qname.toString();
    }

    /**
     *@return the QName for the Target
     */
    public QName getQName(){
        if(type !=TARGET_TYPE_VALUE_QNAME ){
            return null;
        }
        if(qname == null && value != null){
            qname = QName.valueOf(value);
        }
        return qname;
    }

    /**
     * @return true if this Target represents an Attachment
     */
    public boolean isAttachment(){
        return attachment;
    }

    /**
     * returns xpath version to be used if the Target Type is XPATH.
     */
    public String getXPathVersion(){
        return xpathVersion;
    }


    /**
     * sets  xpath version to be used if the Target Type is XPATH.
     */

    public void setXPathVersion(String version){
        xpathVersion = version;
    }

    public void setPolicyQName(QName policyQName){
        this.policyQName = policyQName;
    }

    public QName getPolicyQName(){
        return policyQName;
    }
}
