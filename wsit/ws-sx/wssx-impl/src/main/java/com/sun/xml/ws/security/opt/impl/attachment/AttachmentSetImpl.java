/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.opt.impl.attachment;

import com.sun.xml.ws.api.message.Attachment;
import com.sun.xml.ws.api.message.AttachmentSet;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author Ashutosh.Shahi@sun.com
 */
public class AttachmentSetImpl implements AttachmentSet {

    private final ArrayList<Attachment> attList = new ArrayList<>();

    /**
     * Creates an empty {@link AttachmentSet}.
     */
    public AttachmentSetImpl() {
    }

    /**
     * Creates an {@link AttachmentSet} by copying contents from another.
     */
    public AttachmentSetImpl(Iterable<Attachment> base) {
        for (Attachment a : base)
            add(a);
    }

    @Override
    public Attachment get(String contentId) {
        for( int i=attList.size()-1; i>=0; i-- ) {
            Attachment a = attList.get(i);
            if(a.getContentId().equals(contentId))
                return a;
        }
        return null;
    }

    @Override
    public boolean isEmpty() {
        return attList.isEmpty();
    }

    @Override
    public void add(Attachment att) {
        attList.add(att);
    }

    @Override
    public Iterator<Attachment> iterator() {
        return attList.iterator();
    }

}
