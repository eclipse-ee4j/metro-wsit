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

import com.sun.xml.ws.tx.at.WSATHelper;
import com.sun.xml.ws.tx.at.internal.XidImpl;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import javax.transaction.xa.Xid;

/**
 *
 * @author paulparkinson
 */
class TransactionIdHelperImpl extends TransactionIdHelper {
  private static final int FFID = 0xFF1D;

  // private final DebugLogger debugWSAT = DebugLogger.getDebugLogger("DebugWSAT");

  private Map<String, Xid> tids2xids;
  private Map<Xid, String> xids2tids;

  public TransactionIdHelperImpl() throws NoSuchAlgorithmException {
    tids2xids = new HashMap<String, Xid>();
    xids2tids = new HashMap<Xid, String>();
  }

  public String xid2wsatid(Xid xid) {
      return xidToString(xid, true);
  // return xid.toString();
  }

  //XAResourceHelper.xidToString(Xid xid, true)
  static String xidToString(Xid xid, boolean includeBranchQualifier) {
    if (xid == null) return "";
    StringBuffer sb = new StringBuffer()
      .append(Integer.toHexString(xid.getFormatId()).toUpperCase(Locale.ENGLISH)).append("-")
      .append(byteArrayToString(xid.getGlobalTransactionId()));
    if (includeBranchQualifier) {
      String bqual = byteArrayToString(xid.getBranchQualifier());
      if (!bqual.equals("")) {
        sb.append("-").append(byteArrayToString(xid.getBranchQualifier()));
      }
    }
    return sb.toString();
  }

  private static final char DIGITS[]   = {
    '0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};

  private static String byteArrayToString(byte[] barray) {
    if (barray == null) return "";
    char[] res = new char[barray.length * 2]; // Two chars per byte
    int j = 0;
    for( int i = 0; i < barray.length; i++) {
        res[j++] = DIGITS[(barray[i] & 0xF0) >>> 4];
        res[j++] = DIGITS[barray[i] & 0x0F];
    }
    return new String(res);

  }

  public Xid wsatid2xid(String wsatid) {
    return create(wsatid);
  }


  public static XidImpl create(String xid) {
    StringTokenizer tok = new StringTokenizer(xid, "-");
    if (tok.countTokens() < 2) return null;

    String formatIdString = tok.nextToken();
    String gtridString = tok.nextToken();
    String bqualString = null;
    if (tok.hasMoreElements()) {
      bqualString = tok.nextToken();
    }
    return new XidImpl(Integer.parseInt(formatIdString, 16),
                       stringToByteArray(gtridString),
                       (bqualString != null) ? stringToByteArray(bqualString) : new byte[]{});
  }


  static private byte[] stringToByteArray(String str) {
    if (str == null) return new byte[0];
    byte[] bytes = new byte[str.length()/2];
    for (int i = 0, j = 0; i < str.length(); i++, j++) {
      bytes[j] = (byte) ((Byte.parseByte(str.substring(i,++i), 16) << 4) |
                         Byte.parseByte(str.substring(i,i+1), 16));
    }
    return bytes;
  }

  public synchronized Xid getOrCreateXid(byte[] tid) {
    Xid xid = getXid(tid);
    if (xid != null) return xid;
    byte[] gtrid = WSATHelper.assignUUID().getBytes();
    // xid = XIDFactory.createXID(FFID, gtrid, null);

    xid = new XidImpl(FFID, gtrid, null);
    String stid = new String(tid);
    tids2xids.put(stid, xid);
    xids2tids.put(xid, stid);
  //  if (debugWSAT.isDebugEnabled()) {
  //    debugWSAT.debug("created mapping foreign Transaction Id " + stid + " to local Xid " + xid);
  //  }

    return xid;
  }


  public synchronized byte[] getTid(Xid xid) {
    String stid = xids2tids.get(xid);
    if (stid == null) return null;
    return stid.getBytes();
  }

  public synchronized Xid getXid(byte[] tid) {
    return tids2xids.get(new String(tid));
  }

  public synchronized Xid remove(byte[] tid) {
    if (getXid(tid) == null)
      return null;
    Xid xid = tids2xids.remove(tid);
    xids2tids.remove(xid);
    return xid;
  }

  public synchronized byte[] remove(Xid xid) {
    if (getTid(xid) == null)
      return null;
    String stid = xids2tids.remove(xid);
    tids2xids.remove(stid);
    return stid.getBytes();
  }

  public String toString() {
    return tids2xids.toString();
  }

}
