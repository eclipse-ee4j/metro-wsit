/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.internal;

import javax.transaction.xa.Xid;

/**
 *
 * @author paulparkinson
 */
public class XidStub implements Xid {
        boolean m_equalsValue;

        public XidStub(boolean equalsValue) {
            m_equalsValue = equalsValue;
        }

        public boolean equals(Object obj) {
            return m_equalsValue;
        }

        @Override
        public int getFormatId() {
            return 0;
        }

        @Override
        public byte[] getGlobalTransactionId() {
            return new byte[0];
        }

        @Override
        public byte[] getBranchQualifier() {
            return new byte[0];
        }

        public byte[] getTruncatedBranchQualifier(String resName) {
            return new byte[0];
        }
}
