/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * TestUtil.java
 *
 * Created on April 7, 2006, 12:45 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.sun.xml.wss.impl.util;

import jakarta.xml.soap.*;
import java.io.*;
import java.util.*;

/**
 *
 * @author ashutosh.shahi@sun.com
 */
public class TestUtil {

    /** Creates a new instance of TestUtil */
    public TestUtil() {
    }
    @SuppressWarnings("unchecked")
    public static void saveMimeHeaders(SOAPMessage msg, String fileName)
    throws IOException {

        FileOutputStream fos = new FileOutputStream(fileName);
        ObjectOutputStream oos = new ObjectOutputStream(fos);

        Hashtable hashTable = new Hashtable();
        MimeHeaders mimeHeaders = msg.getMimeHeaders();
        Iterator iterator = mimeHeaders.getAllHeaders();

        while(iterator.hasNext()) {

            MimeHeader mimeHeader = (MimeHeader) iterator.next();

            hashTable.put(mimeHeader.getName(), mimeHeader.getValue());
        }

        oos.writeObject(hashTable);
        oos.flush();
        oos.close();

        fos.flush();
        fos.close();
    }

    public  static SOAPMessage constructMessage(String mimeHdrsFile, String msgFile)
    throws Exception {
        SOAPMessage message;

        MimeHeaders mimeHeaders = new MimeHeaders();
        FileInputStream fis = new FileInputStream(msgFile);

        ObjectInputStream ois = new ObjectInputStream(
        new FileInputStream(mimeHdrsFile));
        Hashtable hashTable = (Hashtable) ois.readObject();
        ois.close();

        if(hashTable.isEmpty()) {
          //  System.out.println("MimeHeaders Hashtable is empty");
        } else {
            for(int i=0; i < hashTable.size(); i++) {
                Enumeration keys = hashTable.keys();
                Enumeration values = hashTable.elements();
                while (keys.hasMoreElements() && values.hasMoreElements()) {
                    String name = (String) keys.nextElement();
                    String value = (String) values.nextElement();
                    mimeHeaders.addHeader(name, value);
                }
            }
        }

        MessageFactory messageFactory = MessageFactory.newInstance();
        message = messageFactory.createMessage(mimeHeaders, fis);

        message.saveChanges();

        return message;
    }

}
