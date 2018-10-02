/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.at.tube;

import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.tx.at.Transactional;

public class TransactionalAttribute {
  private boolean enabled;
  private boolean required;
  private Transactional.Version version;
  private SOAPVersion soapVersion;


    public TransactionalAttribute(boolean enabled, boolean required, Transactional.Version version) {
        this.enabled = enabled;
        this.required = required;
        this.version = version;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public Transactional.Version getVersion() {
        return version;
    }

    public void setVersion(Transactional.Version version) {
        this.version = version;
    }

    public SOAPVersion getSoapVersion() {
       if(soapVersion == null)
          soapVersion = SOAPVersion.SOAP_11; 
        return soapVersion;
    }

    public void setSoapVersion(SOAPVersion soapVersion) {
        this.soapVersion = soapVersion;
    }
}
