/*
 * Copyright (c) 2010, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * $Id: ParticipantsImpl.java,v 1.2 2010-10-21 15:36:54 snajper Exp $
 */

package com.sun.xml.ws.security.trust.impl.elements;

import com.sun.xml.ws.security.trust.elements.Participant;
import com.sun.xml.ws.security.trust.elements.Participants;
import com.sun.xml.ws.security.trust.impl.bindings.ParticipantType;
import com.sun.xml.ws.security.trust.impl.bindings.ParticipantsType;
import java.util.List;

/**
 * Contains information about which parties are authorized in the
 * use of the token.
 *
 * @author Manveen Kaur
 */
public class ParticipantsImpl extends ParticipantsType implements Participants {

    public ParticipantsImpl(ParticipantsType psType){
        //ToDo
    }
    public List<Participant> getParticipants() {
        return null;
    }

    public Participant getPrimaryParticipant() {
        return (Participant)getPrimary();
    }

    public void setPrimaryParticipant(final Participant primary) {
        setPrimary((ParticipantType) primary);
    }
    
}
