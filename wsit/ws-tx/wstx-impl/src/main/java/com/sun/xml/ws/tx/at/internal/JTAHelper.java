/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.at.internal;

import java.util.Locale;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;


/**
 * JTA-specific helper methods.
 */
class JTAHelper {

  static void throwXAException(int errCode, String errMsg) throws XAException {
    XAException ex = new XAException(xaErrorCodeToString(errCode) + ".  " + errMsg);
    ex.errorCode =  errCode;
    throw ex;
  }

  static void throwXAException(int errCode, String errMsg, Throwable t) 
    throws XAException 
  {
    XAException ex = new XAException(xaErrorCodeToString(errCode) + ".  " + errMsg);
    ex.errorCode =  errCode;
    ex.initCause(t);
    throw ex;
  }
  
  static String xaErrorCodeToString(int err) {
    return xaErrorCodeToString(err, true);
  }

  static String xaErrorCodeToString(int err, boolean detail) {
    StringBuilder msg = new StringBuilder(10);
    switch (err) {
    case XAResource.XA_OK:
      return "XA_OK";
    case XAException.XA_RDONLY:
      return "XA_RDONLY";
    case XAException.XA_HEURCOM:
      msg.append("XA_HEURCOM");
      if (detail) msg.append(" : The transaction branch has been heuristically committed");
      return msg.toString();
    case XAException.XA_HEURHAZ:
      msg.append("XA_HEURHAZ");
      if (detail) msg.append(" : The transaction branch may have been heuristically completed");
      return msg.toString();
    case XAException.XA_HEURMIX:
      msg.append("XA_HEURMIX");
      if (detail) msg.append(" : The transaction branch has been heuristically committed and rolled back");
      return msg.toString();
    case XAException.XA_HEURRB:
      msg.append("XA_HEURRB");
      if (detail) msg.append(" : The transaction branch has been heuristically rolled back");
      return msg.toString();                             
    case XAException.XA_RBCOMMFAIL:
      msg.append("XA_RBCOMMFAIL");
      if (detail) msg.append(" : Rollback was caused by communication failure");
      return msg.toString();
    case XAException.XA_RBDEADLOCK:
      msg.append("XA_RBDEADLOCK");
      if (detail) msg.append(" : A deadlock was detected");
      return msg.toString();
    case XAException.XA_RBINTEGRITY:
      msg.append("XA_RBINTEGRITY");
      if (detail) msg.append(" : A condition that violates the integrity of the resource was detected");
      return msg.toString();
    case XAException.XA_RBOTHER:
      msg.append("XA_RBOTHER");
      if (detail) msg.append(" : The resource manager rolled back the transaction branch for a reason not on this list");
      return msg.toString();
    case XAException.XA_RBPROTO:
      msg.append("XA_RBPROTO");
      if (detail) msg.append(" : A protocol error occured in the resource manager");
      return msg.toString();
    case XAException.XA_RBROLLBACK:
      msg.append("XA_RBROLLBACK");
      if (detail) msg.append(" : Rollback was caused by unspecified reason");
      return msg.toString();
    case XAException.XA_RBTIMEOUT:
      msg.append("XA_RBTIMEOUT");
      if (detail) msg.append(" : A transaction branch took too long");
      return msg.toString();
    case XAException.XA_RBTRANSIENT:
      msg.append("XA_RBTRANSIENT");
      if (detail) msg.append(" : May retry the transaction branch");
      return msg.toString();
    case XAException.XAER_ASYNC:
      msg.append("XAER_ASYNC");
      if (detail) msg.append(" : Asynchronous operation already outstanding");
      return msg.toString();      
    case XAException.XAER_DUPID:
      msg.append("XAER_DUPID");
      if (detail) msg.append(" : The XID already exists");
      return msg.toString();      
    case XAException.XAER_INVAL:
      msg.append("XAER_INVAL");
      if (detail) msg.append(" : Invalid arguments were given");
      return msg.toString();      
    case XAException.XAER_NOTA:
      msg.append("XAER_NOTA");
      if (detail) msg.append(" : The XID is not valid");
      return msg.toString();      
    case XAException.XAER_OUTSIDE:
      msg.append("XAER_OUTSIDE");
      if (detail) msg.append(" : The resource manager is doing work outside global transaction");
      return msg.toString();      
    case XAException.XAER_PROTO:
      msg.append("XAER_PROTO");
      if (detail) msg.append(" : Routine was invoked in an inproper context");
      return msg.toString();      
    case XAException.XAER_RMERR:
      msg.append("XAER_RMERR");
      if (detail) msg.append(" : A resource manager error has occured in the transaction branch");
      return msg.toString();      
    case XAException.XAER_RMFAIL:
      msg.append("XAER_RMFAIL");
      if (detail) msg.append(" : Resource manager is unavailable");
      return msg.toString();      
    default:
      return Integer.toHexString(err).toUpperCase(Locale.ENGLISH);
    }
  }
}
