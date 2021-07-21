/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.rm.protocol.wsrm200702;

import com.sun.xml.ws.rx.rm.runtime.sequence.Sequence;
import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;

/**
 * <p>Java class for IncompleteSequenceBehaviorType.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>{@code
 * <simpleType name="IncompleteSequenceBehaviorType">
 *   <restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     <enumeration value="DiscardEntireSequence"/>
 *     <enumeration value="DiscardFollowingFirstGap"/>
 *     <enumeration value="NoDiscard"/>
 *   </restriction>
 * </simpleType>
 * }</pre>
 *
 */
@XmlType(name = "IncompleteSequenceBehaviorType")
@XmlEnum
public enum IncompleteSequenceBehaviorType {

    @XmlEnumValue("DiscardEntireSequence")
    DISCARD_ENTIRE_SEQUENCE("DiscardEntireSequence", Sequence.IncompleteSequenceBehavior.DISCARD_ENTIRE_SEQUENCE),

    @XmlEnumValue("DiscardFollowingFirstGap")
    DISCARD_FOLLOWING_FIRST_GAP("DiscardFollowingFirstGap", Sequence.IncompleteSequenceBehavior.DISCARD_FOLLOWING_FIRST_GAP),

    @XmlEnumValue("NoDiscard")
    NO_DISCARD("NoDiscard", Sequence.IncompleteSequenceBehavior.NO_DISCARD);
    //
    private final String value;
    private final Sequence.IncompleteSequenceBehavior translation;

    IncompleteSequenceBehaviorType(String v, Sequence.IncompleteSequenceBehavior translation) {
        this.value = v;
        this.translation = translation;
    }

    public String value() {
        return value;
    }

    public static IncompleteSequenceBehaviorType fromValue(String v) {
        for (IncompleteSequenceBehaviorType c : IncompleteSequenceBehaviorType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

    public static IncompleteSequenceBehaviorType fromISB(Sequence.IncompleteSequenceBehavior v) {
        for (IncompleteSequenceBehaviorType c : IncompleteSequenceBehaviorType.values()) {
            if (c.translation == v) {
                return c;
            }
        }
        throw new IllegalArgumentException(v.toString());
    }

    public Sequence.IncompleteSequenceBehavior translate() {
        return translation;
    }
}
