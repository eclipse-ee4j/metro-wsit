/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.at.v10.types;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Vote.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="Vote">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="VoteCommit"/>
 *     &lt;enumeration value="VoteRollback"/>
 *     &lt;enumeration value="VoteReadOnly"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "Vote")
@XmlEnum
public enum Vote {

    @XmlEnumValue("VoteCommit")
    VOTE_COMMIT("VoteCommit"),
    @XmlEnumValue("VoteRollback")
    VOTE_ROLLBACK("VoteRollback"),
    @XmlEnumValue("VoteReadOnly")
    VOTE_READ_ONLY("VoteReadOnly");
    private final String value;

    Vote(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static Vote fromValue(String v) {
        for (Vote c: Vote.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
