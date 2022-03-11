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
 * $Id: Action.java,v 1.2 2010-10-21 15:38:03 snajper Exp $
 */

package com.sun.xml.wss.saml.assertion.saml20.jaxb20;

// makeing the implementation dummy for Appserver Release

import com.sun.xml.wss.logging.LogDomainConstants;
import com.sun.xml.wss.saml.internal.saml20.jaxb20.ActionType;
import org.w3c.dom.Element;
import java.util.logging.Logger;


/**
 *This class is designed for <code>Action</code> element in SAML core assertion.
 *The Action Element specifies an action on specified resource for which
 *permission is sought.
 */
public class Action  extends com.sun.xml.wss.saml.internal.saml20.jaxb20.ActionType implements com.sun.xml.wss.saml.Action {
    protected static final Logger log = Logger.getLogger(
            LogDomainConstants.WSS_API_DOMAIN,
            LogDomainConstants.WSS_API_DOMAIN_BUNDLE);

    /**
     * Constructs an action element from an existing XML block.
     *
     * @param element representing a DOM tree element.
     */
    public Action(Element element) {
        setValue(element.getLocalName());
        setNamespace(element.getNamespaceURI());
    }

    /**
     * Convenience constructor of <code>Action</code>
     * @param namespace The attribute "namespace" of
     *        <code>Action</code> element
     * @param action A String representing an action
     */
    public Action(String namespace, String action) {
        setValue(action);
        setNamespace(namespace);
    }

    public Action(ActionType actionType) {
        setValue(actionType.getValue());
        setNamespace(actionType.getNamespace());
    }

    @Override
    public String getValue(){
        return super.getValue();
    }

    @Override
    public String getNamespace(){
        return super.getNamespace();
    }
}
