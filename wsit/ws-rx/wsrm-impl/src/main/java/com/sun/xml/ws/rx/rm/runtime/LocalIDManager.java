/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.rm.runtime;

import java.util.Iterator;

public interface LocalIDManager {
    void createLocalID(String localID, String sequenceID, long messageNumber);
    void removeLocalIDs(Iterator<String> localIDs);
    BoundMessage getBoundMessage(String localID);
    /**
     * Mark all the localIDs associated the specified sequenceID terminated
     */
    void markSequenceTermination(String sequenceID);
    class BoundMessage {
        final public String sequenceID;
        final public long messageNumber;
        final public long createTime;
        final public long seqTerminateTime;
        public BoundMessage(String sequenceID, long messageNumber, long createTime, long seqTerminateTime) {
            this.sequenceID = sequenceID;
            this.messageNumber = messageNumber;
            this.createTime = createTime;
            this.seqTerminateTime = seqTerminateTime;
        }
        public String toString() {
            return "BoundMessage [sequenceID=" + sequenceID
                    + ", messageNumber=" + messageNumber + ", createTime="
                    + createTime + ", seqTerminateTime=" + seqTerminateTime
                    + "]";
        }
    }
}
