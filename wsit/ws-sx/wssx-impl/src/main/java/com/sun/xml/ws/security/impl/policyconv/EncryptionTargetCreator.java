/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.impl.policyconv;

import com.sun.xml.ws.security.impl.policy.Constants;
import com.sun.xml.ws.security.policy.AlgorithmSuite;
import com.sun.xml.wss.impl.policy.mls.EncryptionTarget;
import java.util.logging.Level;
import javax.xml.namespace.QName;

/**
 *
 * @author K.Venugopal@sun.com
 */
public class EncryptionTargetCreator {

    public AlgorithmSuite algorithmSuite;
    public boolean enforce = false;
    /** Creates a new instance of EncryptionTargetCreator */
    public EncryptionTargetCreator(AlgorithmSuite algorithmSuite,boolean enforce) {
        this.algorithmSuite = algorithmSuite;
        this.enforce = enforce;
    }

    public EncryptionTarget newQNameEncryptionTarget(QName targetValue){
        EncryptionTarget target = new EncryptionTarget();
        target.setEnforce(enforce);
        target.setDataEncryptionAlgorithm(algorithmSuite.getEncryptionAlgorithm());
        target.setType(EncryptionTarget.TARGET_TYPE_VALUE_QNAME);
        target.setQName(targetValue);
        //target.setValue(EncryptionTarget.BODY);
        target.setValue("{"+targetValue.getNamespaceURI()+"}"+targetValue.getLocalPart());
        target.setContentOnly(false);
        if(Constants.logger.isLoggable(Level.FINE)){
            Constants.logger.log(Level.FINE,"QName Encryption Target with value "+target.getValue()+ " has been added");
        }
        return target;
    }

    public EncryptionTarget newXpathEncryptionTarget(String xpathTarget){
        EncryptionTarget target = new EncryptionTarget();
        target.setType(EncryptionTarget.TARGET_TYPE_VALUE_XPATH);
        target.setValue(xpathTarget);
        target.setEnforce(enforce);
        target.setDataEncryptionAlgorithm(algorithmSuite.getEncryptionAlgorithm());
        target.setContentOnly(false);
        if(Constants.logger.isLoggable(Level.FINE)){
            Constants.logger.log(Level.FINE,"XPath Encryption Target with value "+target.getValue()+ " has been added");
        }
        return target;
    }

    public EncryptionTarget newURIEncryptionTarget(String uri){
        EncryptionTarget target = new EncryptionTarget();
        target.setEnforce(enforce);
        target.setDataEncryptionAlgorithm(algorithmSuite.getEncryptionAlgorithm());
        target.setType(EncryptionTarget.TARGET_TYPE_VALUE_URI);
        target.setValue(uri);
        target.setContentOnly(false);
        if(Constants.logger.isLoggable(Level.FINE)){
            Constants.logger.log(Level.FINE,"URI Encryption Target with value "+target.getValue()+ " has been added");
        }
        return target;
    }

    public void addAttachmentTransform(EncryptionTarget target, String transformURI){
        EncryptionTarget.Transform tr = target.newEncryptionTransform();
        tr.setTransform(transformURI);
        target.addCipherReferenceTransform(tr);
    }
}
