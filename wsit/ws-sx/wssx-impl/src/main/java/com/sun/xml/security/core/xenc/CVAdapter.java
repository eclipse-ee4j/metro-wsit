/*
 * Copyright (c) 2010, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * SCVAdapter.java
 *
 * Created on August 9, 2006, 2:47 PM
 */

package com.sun.xml.security.core.xenc;
import com.sun.xml.ws.security.opt.impl.enc.CryptoProcessor;

import jakarta.activation.CommandInfo;
import jakarta.activation.CommandMap;
import jakarta.activation.MailcapCommandMap;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import jakarta.activation.DataHandler;

/**
 *
 * @author K.Venugopal@sun.com
 */
public class CVAdapter extends XmlAdapter<DataHandler,byte[]>{

    public static final String MIME_CIPHERVALUE = "application/ciphervalue";
    public static final String CV_HANDLER_CLASS = "com.sun.xml.ws.security.opt.impl.util.CVDataHandler";

    private CryptoProcessor cp;

    public CVAdapter() {
        ensureHandlerRegistered();
    }
    
    public CVAdapter(CryptoProcessor cp){
        this();
        this.cp = cp;
    }
    
    @Override
    public DataHandler marshal(byte[] value){
        return new DataHandler(cp,"application/ciphervalue");
    }
    
    @Override
    public byte[] unmarshal(DataHandler dh){
        throw new UnsupportedOperationException();
    }

    private void ensureHandlerRegistered() {
        CommandMap map = CommandMap.getDefaultCommandMap();
        CommandInfo[] commands = map.getAllCommands(MIME_CIPHERVALUE);
        if (commands != null && commands.length > 0) {
            for (CommandInfo command : commands) {
                if (CV_HANDLER_CLASS.equals(command.getCommandClass())) {
                    return;
                }
            }
        }
        if (map instanceof MailcapCommandMap) {
            ((MailcapCommandMap)map).addMailcap(MIME_CIPHERVALUE + ";;x-java-content-handler=" + CV_HANDLER_CLASS);
        }
    }

}
