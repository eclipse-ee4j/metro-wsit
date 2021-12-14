/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.impl;

import javax.xml.namespace.QName;
import jakarta.xml.soap.Detail;

/** The <code>WssSoapFaultException</code> exception represents a 
 *  SOAP fault.
 *
 *  <p>The message part in the SOAP fault maps to the contents of
 *  <code>faultdetail</code> element accessible through the 
 *  <code>getDetail</code> method on the <code>WssSoapFaultException</code>.
 *  The method <code>createDetail</code> on the 
 *  <code>jakarta.xml.soap.SOAPFactory</code> creates an instance
 *  of the <code>jakarta.xml.soap.Detail</code>.
 *
 *  <p>The <code>faultstring</code> provides a human-readable 
 *  description of the SOAP fault. The <code>faultcode</code> 
 *  element provides an algorithmic mapping of the SOAP fault.
 * 
 *  <p>Refer to SOAP 1.1 and WSDL 1.1 specifications for more
 *  details of the SOAP faults. 
 *
 *  @see jakarta.xml.soap.Detail
 *  @see jakarta.xml.soap.SOAPFactory#createDetail
**/

public class WssSoapFaultException extends java.lang.RuntimeException  {
  
  private QName faultcode;
  private String faultstring;
  private String faultactor;
  private Detail detail;

  /** Constructor for the SOAPFaultException
   *  @param faultcode   <code>QName</code> for the SOAP faultcode
   *  @param faultstring <code>faultstring</code> element of SOAP fault 
   *  @param faultactor  <code>faultactor</code> element of SOAP fault
   *  @param faultdetail <code>faultdetail</code> element of SOAP fault 
   *
   *  @see jakarta.xml.soap.SOAPFactory#createDetail
   */
  public WssSoapFaultException(QName faultcode,
		   String faultstring,
		   String faultactor,
		   jakarta.xml.soap.Detail faultdetail) {
    super(faultstring);
    this.faultcode = faultcode;
    this.faultstring = faultstring;
    this.faultactor = faultactor;
    this.detail = faultdetail;
  }

  /** Gets the <code>faultcode</code> element. The <code>faultcode</code>
   *  element provides an algorithmic mechanism for identifying the
   *  fault. SOAP defines a small set of SOAP fault codes covering 
   *  basic SOAP faults.
   *
   *  @return QName of the faultcode element
   */
  public QName getFaultCode() {
    return this.faultcode;
  }

  /** Gets the <code>faultstring</code> element. The <code>faultstring</code>
   *  provides a human-readable description of the SOAP fault and 
   *  is not intended for algorithmic processing.
   *
   *  @return faultstring element of the SOAP fault
   */
  public String getFaultString() {
    return this.faultstring;
  }

  /** Gets the <code>faultactor</code> element. The <code>faultactor</code>
   *  element provides information about which SOAP node on the 
   *  SOAP message path caused the fault to happen. It indicates 
   *  the source of the fault.
   * 
   *  @return <code>faultactor</code> element of the SOAP fault 
   */
  public String getFaultActor() {
    return this.faultactor;
  }

  /** Gets the detail element. The detail element is intended for
   *  carrying application specific error information related to
   *  the SOAP Body.
   *
   *  @return <code>detail</code> element of the SOAP fault
   *  @see jakarta.xml.soap.Detail
   */
  public Detail getDetail() {
    return this.detail;
  }
}
