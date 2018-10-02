/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.opt.api.keyinfo;

import com.sun.xml.ws.security.opt.api.EncryptedKey;
import com.sun.xml.ws.security.opt.crypto.dsig.keyinfo.KeyInfo;
import java.security.Key;

/**
 * 
 * Class to store results from TokenBuilder. Stores the various key information
 * @author K.Venugopal@sun.com
 */
public class BuilderResult {
    private Key dataProtectionKey = null;
    private Key keyProtectionKey = null;
    private KeyInfo keyInfo = null;
    private EncryptedKey encryptedKey = null;
    private String dpKID = "";
    /** Creates a new instance of BuilderResult */
    public BuilderResult() {
    }

    /**
     * 
     * @return the data protection key
     */
    public Key getDataProtectionKey() {
        return dataProtectionKey;
    }

    /**
     * 
     * @param dataProtectionKey set the data protection key
     */
    public void setDataProtectionKey(final Key dataProtectionKey) {
        this.dataProtectionKey = dataProtectionKey;
    }

    /**
     * 
     * @return the key protection key
     */
    public Key getKeyProtectionKey() {
        return keyProtectionKey;
    }

    /**
     * 
     * @param keyProtectionKey store the key protection key
     */
    public void setKeyProtectionKey(final Key keyProtectionKey) {
        this.keyProtectionKey = keyProtectionKey;
    }

    /**
     * 
     * @return the stored keyInfo
     */
    public KeyInfo getKeyInfo() {
        return keyInfo;
    }

    /**
     * 
     * @param keyInfo store the keyInfo from <CODE>TokenBuilder</CODE>
     */
    public void setKeyInfo(final KeyInfo keyInfo) {
        this.keyInfo = keyInfo;
    }

    /**
     * 
     * @return the encryptedKey
     */
    public EncryptedKey getEncryptedKey() {
        return encryptedKey;
    }

    /**
     * 
     * @param encryptedKey store the encryptedKey for Signature or Encryption
     */
    public void setEncryptedKey(final EncryptedKey encryptedKey) {
        this.encryptedKey = encryptedKey;
    }
    
    public void setDPTokenId(final String id){
        this.dpKID = id;
    }
    
    public String getDPTokenId(){
        return dpKID;
    }
}
