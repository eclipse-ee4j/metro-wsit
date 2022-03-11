/*
 * Copyright (c) 2010, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * BinarySecurityToken.java
 *
 * Created on August 2, 2006, 10:37 AM
 */

package com.sun.xml.ws.security.opt.api.keyinfo;

/**
 * Represents binary-formatted security tokens
 * @author K.Venugopal@sun.com
 */
public interface BinarySecurityToken extends Token{
    /*
     * The ValueType attribute is used to indicate the "value space" of the encoded binary
     * data (e.g. an X.509 certificate). The ValueType attribute allows a URI that defines the
     * value type and space of the encoded binary data.
     */
    String getValueType();
    /*
     * BinarySecurityToken/@EncodingType
     * The EncodingType attribute is used to indicate, using a URI, the encoding format of the
     * binary data (e.g., base64 encoded). A new attribute is introduced, as there are issues
     * with the current schema validation tools that make derivations of mixed simple and
     * complex types difficult within XML Schema. The EncodingType attribute is interpreted
     * to indicate the encoding format of the element.
     */
    String getEncodingType();

    /*
     * returns contents of the BinarySecurityToken
     */
    byte[] getTokenValue();

}
