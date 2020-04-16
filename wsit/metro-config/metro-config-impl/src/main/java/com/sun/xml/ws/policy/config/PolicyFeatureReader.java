/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.policy.config;

import com.sun.istack.logging.Logger;
import com.sun.xml.ws.config.metro.dev.FeatureReader;
import com.sun.xml.ws.config.metro.util.ParserUtil;
import com.sun.xml.ws.policy.PolicyException;
import com.sun.xml.ws.policy.sourcemodel.PolicyModelUnmarshaller;
import com.sun.xml.ws.policy.sourcemodel.PolicySourceModel;

import javax.xml.stream.XMLEventReader;
import jakarta.xml.ws.WebServiceException;

/**
 *
 * @author Fabian Ritzmann
 */
public class PolicyFeatureReader implements FeatureReader {

    private static final Logger LOGGER = Logger.getLogger(PolicyFeatureReader.class);

    // TODO implement
    public PolicyFeature parse(XMLEventReader reader) throws WebServiceException {
        try{
            final PolicyModelUnmarshaller unmarshaller = PolicyModelUnmarshaller.getXmlUnmarshaller();
            final PolicySourceModel model = unmarshaller.unmarshalModel(reader);
            return new PolicyFeature(null);
        } catch (PolicyException e) {
            // TODO logging message
            throw LOGGER.logSevereException(new WebServiceException("Failed to unmarshal policy expression", e));
        }
    }

}
