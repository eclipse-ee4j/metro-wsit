/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.at.runtime;

import java.security.NoSuchAlgorithmException;
import javax.transaction.xa.Xid;


/**
 * Helper class for converting between  Xids and WS-AT transaction Ids.
 * 
 */
public abstract class TransactionIdHelper {
  private static TransactionIdHelper singleton;

  static {
    try {
      singleton = new TransactionIdHelperImpl();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
  }
  
  /**
   * Returns the TransactionIdHelper instance.
   * 
   * @return the TransactionIdHelper instance.
   */
  public static TransactionIdHelper getInstance() {
    return singleton;
  }

  /**
   * Convert a  Xid to WS-AT Id format.
   * 
   * @param xid
   *          A  Xid.
   * @return The transaction id in WS-AT format
   */
  public abstract String xid2wsatid(Xid xid);

  /**
   * Convert a WS-AT Id that was generated from a  Xid back into a Xid
   * 
   * @param wsatid
   *          A -based WS-AT tid
   * @return A  Xid
   */
  public abstract Xid wsatid2xid(String wsatid);

  /**
   * Returns a foreign Xid that is mapped to the specified WS-AT transaction Id.
   * 
   * @param tid
   *          A foreign WS-AT tid in string representation.
   * @return A foreign Xid that is mapped ot the tid.
   */
  public abstract Xid getOrCreateXid(byte[] tid);

  /**
   * Returns the foreign Xid that is mapped to the specified WS-AT transaction
   * Id.
   * 
   * @param tid
   *          A foreign WS-AT tid.
   * @return The foreign Xid corresponding to the tid.
   */
  public abstract Xid getXid(byte[] tid);

  /**
   * Returns the foreign WS-AT transaction Id that is mapped to the foreign Xid.
   * 
   * @param xid
   *          A foreign Xid that was created from the foreign tid.
   * @return The foreign tid corresponding to the foreign Xid.
   */
  public abstract byte[] getTid(Xid xid);

  /**
   * Removes the foreign WS-AT tid to Xid mapping
   * 
   * @param tid
   *          A foreign WS-AT transaction Id.
   * @return The mapped foreign Xid, or null if no mapping exists
   */
  public abstract Xid remove(byte[] tid);

  /**
   * Removes the foreign WS-AT tid to Xid mapping
   * 
   * @param xid
   *          A foreign Xid that is mapped to a foreign tid.
   * @return The mapped tid, or null if no mapping exists.
   */
  public abstract byte[] remove(Xid xid);

}
