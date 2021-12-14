/*
 * Copyright (c) 2014, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sun.xml.util;

import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

import org.apache.xml.security.algorithms.JCEMapper;
import org.apache.xml.security.encryption.XMLCipher;

public class XMLCipherAdapter {
  /**
   * Construct an <code>Cipher</code> object. Some JDKs don't support RSA/ECB/OAEPPadding, so add an additional action.
   * @return the Cipher
   */
  public static Cipher constructCipher(String algorithm)
      throws  NoSuchPaddingException, NoSuchAlgorithmException {
    String jceAlgorithm = JCEMapper.translateURItoJCEID(algorithm);

    Cipher cipher;
    try {
      cipher = Cipher.getInstance(jceAlgorithm);
    } catch (NoSuchAlgorithmException nsae) {
      if (XMLCipher.RSA_OAEP.equals(algorithm)) {
          cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
      } else {
        throw nsae;
      }
    }
    return cipher;
  }
}
