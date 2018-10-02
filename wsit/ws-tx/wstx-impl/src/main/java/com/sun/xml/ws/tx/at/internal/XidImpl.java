/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.at.internal;

import java.io.Serializable;
import java.util.Arrays;
import javax.transaction.xa.Xid;

/**
 *
 * @author paulparkinson
 */
public class XidImpl implements Xid, Serializable {

    int formatId;
    byte[] globalTransactionId;
    byte[] branchQual;

    public XidImpl(Xid xid) {
        this.formatId= xid.getFormatId();
        this.globalTransactionId = xid.getGlobalTransactionId();
        this.branchQual = xid.getBranchQualifier();
    }

    public XidImpl(int formatId, byte[] globalTransactionId, byte[] branchQual) {
        this.formatId= formatId;
        this.globalTransactionId = globalTransactionId;
        this.branchQual = branchQual;
    }

    public XidImpl(byte[] globalTransactionId) {
        this(1234, globalTransactionId, new byte[]{});
    }

    public XidImpl(byte[] globalTransactionId, int formatId) {
        this(formatId, globalTransactionId, new byte[]{});
    }

    public int getFormatId() {
        return formatId;
    }

    public byte[] getGlobalTransactionId() {
        return globalTransactionId;
    }

    public byte[] getBranchQualifier() {
        return branchQual;
    }

    @Override
    public int hashCode() {
        int pos = 0;
        byte[] array = globalTransactionId;
        return (short) (((array[pos++] &0xFF) << 8) | (array[pos] &0xFF));
    }

    @Override
    public boolean equals(Object obj) {
    if (obj == this) return true;
    if (obj == null || !(obj instanceof Xid)) return false;
    Xid thatXid = (Xid) obj;
        final boolean formatId = getFormatId() == thatXid.getFormatId();
        final boolean gtrid = Arrays.equals(getGlobalTransactionId(), thatXid.getGlobalTransactionId());
        final boolean branchqual = Arrays.equals(getBranchQualifier(), thatXid.getBranchQualifier());
    return
        formatId &&
        gtrid &&
        branchqual;
  }






}
