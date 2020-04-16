/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.saml.assertion.saml11.jaxb20;

import com.sun.xml.wss.saml.SAMLException;
import com.sun.xml.wss.logging.LogDomainConstants;
import com.sun.xml.wss.logging.LogStringsMessages;
import com.sun.xml.wss.saml.internal.saml11.jaxb20.ConditionsType;
import com.sun.xml.wss.saml.util.SAMLJAXBUtil;
import com.sun.xml.wss.util.DateUtils;
import java.text.ParseException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.xml.bind.JAXBContext;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

/**
 * The validity of an <code>Assertion</code> MAY be subject to a set of
 * <code>Conditions</code>. Each <code>Condition</code> evaluates to a value that
 * is Valid, Invalid or Indeterminate.
 */
public class Conditions extends ConditionsType
    implements com.sun.xml.wss.saml.Conditions {
    
        private Date notBeforeField = null;
        private Date notOnOrAfterField = null;
        
	protected static final Logger log =
		Logger.getLogger(
			LogDomainConstants.WSS_API_DOMAIN,
			LogDomainConstants.WSS_API_DOMAIN_BUNDLE);

	/**
	Constructor taking in nothing (SAML spec allows it)
	*/
	public Conditions() {
            super();
	}

        @SuppressWarnings("unchecked")
        private void setaudienceRestrictionConditionOrDoNotCacheConditionOrCondition(List condition ) {
            this.audienceRestrictionConditionOrDoNotCacheConditionOrCondition = condition;
        }
        
	/**
	 * Constructs an instance of <code>Conditions</code>.
	 *
	 * @param notBefore specifies the earliest time instant at which the
	 *        assertion is valid.
	 * @param notOnOrAfter specifies the time instant at which the assertion
	 *        has expired.
	 * @param condition
	 * @param arc the <code>AudienceRestrictionCondition</code> to be
	 *        added. Can be null, if no audience restriction.
	 * @param doNotCacheCnd
	 * @exception SAMLException if there is a problem in input data and it
	 *            cannot be processed correctly.
	 */
	public Conditions(
		GregorianCalendar notBefore,
		GregorianCalendar notOnOrAfter,
		List condition,
		List arc,
		List doNotCacheCnd)
		{
            
            DatatypeFactory factory = null;
            try {
                factory = DatatypeFactory.newInstance();
            } catch (DatatypeConfigurationException e) {
                factory = null;
            }
            
            if ( factory != null) {
            setNotBefore(factory.newXMLGregorianCalendar(notBefore));
            setNotOnOrAfter(factory.newXMLGregorianCalendar(notOnOrAfter));
            }
            
            if ( condition != null) {
                setaudienceRestrictionConditionOrDoNotCacheConditionOrCondition(condition);
            } else if ( arc != null) {
                setaudienceRestrictionConditionOrDoNotCacheConditionOrCondition(arc);
            } else if ( doNotCacheCnd != null) {
                setaudienceRestrictionConditionOrDoNotCacheConditionOrCondition(doNotCacheCnd);
            }
	}
         
        public Conditions(ConditionsType cType){                                    
            setNotBefore(cType.getNotBefore());
            setNotOnOrAfter(cType.getNotOnOrAfter());                        
            setaudienceRestrictionConditionOrDoNotCacheConditionOrCondition(
                    cType.getAudienceRestrictionConditionOrDoNotCacheConditionOrCondition());            
	}
        
        public Date getNotBeforeDate(){
        try {
            if(notBeforeField != null){
                return notBeforeField;
            }
            if(super.getNotBefore() != null){
                notBeforeField = DateUtils.stringToDate(super.getNotBefore().toString());
            }
        } catch (ParseException ex) {
           log.log(Level.SEVERE, LogStringsMessages.WSS_0430_SAML_GET_NOT_BEFORE_DATE_OR_GET_NOT_ON_OR_AFTER_DATE_PARSE_FAILED(), ex);
        }
            return notBeforeField;
        }
        
        public Date getNotOnOrAfterDate(){
        try {
            if(notOnOrAfterField != null){
                return notOnOrAfterField;
            }
            if(super.getNotOnOrAfter() != null){
                notOnOrAfterField = DateUtils.stringToDate(super.getNotOnOrAfter().toString());
            }
        } catch (ParseException ex) {
           log.log(Level.SEVERE, LogStringsMessages.WSS_0430_SAML_GET_NOT_BEFORE_DATE_OR_GET_NOT_ON_OR_AFTER_DATE_PARSE_FAILED(), ex);
        }
        return notOnOrAfterField;
    }
       @SuppressWarnings("unchecked")
        public List<Object> getConditions(){
            return (List<Object>)(Object)super.getAudienceRestrictionConditionOrDoNotCacheConditionOrCondition();
        }
	/**
	 * Constructs a <code>Conditions</code> element from an existing XML block.
	 *
	 * @param element A <code>org.w3c.dom.Element</code> representing
	 *        DOM tree for <code>Conditions</code> object
	 * @exception SAMLException if it could not process the Element properly,
	 *            implying that there is an error in the sender or in the
	 *            element definition.
	 */
	public static ConditionsType fromElement(org.w3c.dom.Element element)
		throws SAMLException {
            try {
                JAXBContext jc = SAMLJAXBUtil.getJAXBContext();
                    
                jakarta.xml.bind.Unmarshaller u = jc.createUnmarshaller();
            return (ConditionsType)u.unmarshal(element);
            } catch ( Exception ex) {
                throw new SAMLException(ex.getMessage());
            }
	}        
}
