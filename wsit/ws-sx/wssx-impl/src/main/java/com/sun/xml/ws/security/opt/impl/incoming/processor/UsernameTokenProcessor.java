/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.opt.impl.incoming.processor;

import com.sun.xml.wss.impl.MessageConstants;
import javax.xml.stream.StreamFilter;
import javax.xml.stream.XMLStreamReader;

/**
 *
 * @author Ashutosh.Shahi@sun.com
 */

public class UsernameTokenProcessor implements StreamFilter{

    String username = null;
    String password = null;
    String passwordDigest = null;
    String passwordType = null;
    String nonce = null;
    String created = null;
    String   Iteration ;
    String Iterations;
    String Salt;
    String currentElement = "";

    private static String USERNAME = "Username".intern();
    private static String PASSWORD = "Password".intern();
    private static String NONCE = "Nonce".intern();
    private static String CREATED = "Created".intern();
    private static String SALT = "Salt".intern();
    private static String ITERATION = "Iteration".intern();
    private static String ITERATIONS = "Iterations".intern();

    /** Creates a new instance of UsernameTokenProcessor */
    public UsernameTokenProcessor() {
    }
    /**
     * parses the UsernameToken and sets the members of the class
     * @param reader XMLStreamReader
     * @return boolean
     */
    @Override
    public boolean accept(XMLStreamReader reader) {

        if(reader.getEventType() == XMLStreamReader.START_ELEMENT){

            if("Username".equals(reader.getLocalName())){
                currentElement = USERNAME;
            } else if("Password".equals(reader.getLocalName())){
                currentElement = PASSWORD;
                passwordType = reader.getAttributeValue(null, "Type");
            } else if("Nonce".equals(reader.getLocalName())){
                currentElement = NONCE;
            } else if("Created".equals(reader.getLocalName())){
                currentElement = CREATED;
            }else if("Salt".equals(reader.getLocalName())){
                currentElement = SALT;
            }else if("Iteration".equals(reader.getLocalName())){
                currentElement = ITERATION;
            }else if("Iterations".equals(reader.getLocalName())){
                currentElement = ITERATIONS;
            }
        }

        if(reader.getEventType() == XMLStreamReader.CHARACTERS){
            if(currentElement == USERNAME){
                username = reader.getText();
                currentElement = "";
            } else if(currentElement == PASSWORD){
                if(MessageConstants.PASSWORD_DIGEST_NS.equals(passwordType)){
                    passwordDigest = reader.getText();
                } else{
                    password = reader.getText();
                }
                currentElement = "";
            } else if(currentElement == NONCE){
                nonce = reader.getText();
                currentElement = "";
            } else if(currentElement == CREATED){
                created = reader.getText();
                currentElement = "";
            }else if (currentElement == SALT){
                 Salt = reader.getText();
                 currentElement = "";
            }else if (currentElement == ITERATION){
                 Iteration = reader.getText();
                 currentElement = "";
            }else if (currentElement == ITERATIONS){
                 Iterations = reader.getText();
                 currentElement = "";
            }

        }
        return true;
    }

    public String getUsername(){
        return username;
    }

    public String getPassword(){
        return password;
    }

    public String getPasswordDigest(){
        return passwordDigest;
    }

    public String getPasswordType(){
        return passwordType;
    }

    public String getNonce(){
        return nonce;
    }

    public String getCreated(){
        return created;
    }
    /**
     * returns the 16 byte salt for creating password derived keys
     * @return Salt String
     */
    public String getSalt(){
        return Salt;
    }
    /**
     *
     * @return Iterations String
     */
    public String getIterations(){
        if (Iteration != null) {
            return Iteration;
        }
        else
            return Iterations;
    }
}

