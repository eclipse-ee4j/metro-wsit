/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.rm.runtime.sequence.invm;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.sun.xml.ws.rx.rm.runtime.LocalIDManager;

public class InMemoryLocalIDManager implements LocalIDManager {
    private Map<String, BoundMessage> store = new HashMap<String, BoundMessage>();
    private InMemoryLocalIDManager() {
    }
    public void createLocalID(String localID, String sequenceID, long messageNumber) {
        //System.out.println("--- creating LocalID: "+localID);
        store.put(localID, new BoundMessage(sequenceID, messageNumber, System.currentTimeMillis(), 0));
        //System.out.println("------ LocalID Manager content: "+store);
    }
    public void removeLocalIDs(Iterator<String> localIDs) {
        //System.out.println("--- removing LocalID: "+localIDs);
        if (localIDs != null) {
            while (localIDs.hasNext()) {
                store.remove(localIDs.next());
            }
        }
        //System.out.println("------ LocalID Manager content: "+store);
    }
    public BoundMessage getBoundMessage(String localID) {
        return store.get(localID);
    }
    public void markSequenceTermination(String sequenceID) {
        //System.out.println("--- seq termination: "+sequenceID);
        for (String localID : store.keySet()) {
            BoundMessage msg = store.get(localID);
            if (sequenceID.equals(msg.sequenceID)) {
                BoundMessage updatedMsg = new BoundMessage(msg.sequenceID, 
                        msg.messageNumber, 
                        msg.createTime, 
                        System.currentTimeMillis());
                store.put(localID, updatedMsg);
            }
        }
        //System.out.println("------ LocalID Manager content: "+store);
    }
    private static LocalIDManager instance = new InMemoryLocalIDManager();
    public static LocalIDManager getInstance() {
        return instance;
    }
}
