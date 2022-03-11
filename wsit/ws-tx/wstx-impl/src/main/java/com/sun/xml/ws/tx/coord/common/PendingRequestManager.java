/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.coord.common;

import com.sun.xml.ws.tx.coord.common.types.BaseRegisterResponseType;
import com.sun.istack.logging.Logger;
import com.sun.xml.ws.tx.at.localization.LocalizationMessages;

import jakarta.xml.ws.WebServiceException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class PendingRequestManager {
    private static final Logger LOGGER = Logger.getLogger(PendingRequestManager.class);
    static ConcurrentHashMap<String, ResponseBox> pendingRequests = new ConcurrentHashMap<>();

    public static ResponseBox reqisterRequest(String msgId) {
        ResponseBox box = new ResponseBox();
        pendingRequests.put(msgId, box);
        return box;
    }

    public static void removeRequest(String msgId) {
        pendingRequests.remove(msgId);
    }


    public static void registryReponse(String msgId, BaseRegisterResponseType repsonse) {
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.fine(LocalizationMessages.WSAT_4620_GET_RESPONSE_REQUEST("\t" + msgId));
        }
        ResponseBox box = pendingRequests.remove(msgId);
        if (box != null) {
            box.put(repsonse);
        } else if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.fine(LocalizationMessages.WSAT_4621_IGNORE_RESPONSE("\t" + msgId));
        }
    }

    static public class ResponseBox {
        private boolean isSet = false;
        private BaseRegisterResponseType type;

        ResponseBox() {

        }

        public synchronized void put(BaseRegisterResponseType type) {
            this.type = type;
            isSet = true;
            this.notify();
        }

        public synchronized BaseRegisterResponseType getResponse(long timeout) {
            /* A thread can also wake up without being notified, interrupted, or
            * timing out, a so-called <i>spurious wakeup</i>.  While this will rarely
            * occur in practice, applications must guard against it by testing for
            * the condition that should have caused the thread to be awakened, and
            * continuing to wait if the condition is not satisfied.  In other words,
            * waits should always occur in loops, like this one:
            */

            long start = System.currentTimeMillis();
            while (!isSet) {
                try {
                    wait(timeout);
                    long end = System.currentTimeMillis();
                    timeout = timeout - (end -start);
                    if(timeout<=0)
                       break;
                    else start = end;
                } catch (InterruptedException e) {
                    throw new WebServiceException(e);
                }
            }

            return type;
        }
    }

}
