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
 * $Id: CertificateValidationCallback.java,v 1.2 2010-10-21 15:37:24 snajper Exp $
 */

package com.sun.xml.wss.impl.callback;

import com.sun.xml.ws.security.opt.impl.util.SOAPUtil;
import com.sun.xml.wss.impl.MessageConstants;
import javax.security.auth.callback.Callback;

import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

/**
 * This Callback is intended for X.509 certificate validation
 * A validator that implements the CertificateValidator interface
 * should be set on the callback by the callback handler.
 *
 * @author XWS-Security Team.
 */
public class CertificateValidationCallback extends XWSSCallback implements Callback {

    private boolean result = false;

    private CertificateValidator validator;

    private X509Certificate certificate;
    
    private boolean revocationEnabled = false;

    public CertificateValidationCallback(X509Certificate certificate) {
        this.certificate = certificate;
    }

    public CertificateValidationCallback(X509Certificate certificate, Map context) {
        this.certificate = certificate;
        this.runtimeProperties = (Map)context;
    }
    
    public boolean getResult() {
        try {
            if (validator != null)  {
                if (validator instanceof ValidatorExtension) {
                    ((ValidatorExtension)validator).setRuntimeProperties(runtimeProperties);
                }
                result = validator.validate(certificate);
            }
        } catch (CertificateValidationCallback.CertificateValidationException ex) {
            throw SOAPUtil.newSOAPFaultException(MessageConstants.WSSE_INVALID_SECURITY_TOKEN,
                        ex.getMessage(), ex, true);
        } catch (Exception e) {
             throw SOAPUtil.newSOAPFaultException(MessageConstants.WSSE_INVALID_SECURITY_TOKEN,
                        e.getMessage(), e);
        }
        return result;
    }

    /**
     * This method must be invoked while handling this CallBack.
     */
    public void setValidator(CertificateValidator validator) {
        this.validator = validator;
        if (this.validator instanceof ValidatorExtension) {
            ((ValidatorExtension)this.validator).setRuntimeProperties(this.getRuntimeProperties());
        }
    }

    public boolean isRevocationEnabled() {
        return revocationEnabled;
    }

    public void setRevocationEnabled(boolean revocationEnabled) {
        this.revocationEnabled = revocationEnabled;
    }


    public static interface CertificateValidator  {

        /** 
         * Certificate validator.
         * @param certificate <code>java.security.cert.X509Certificate</code>
         * @return true if the certificate is valid else false
         */
        public boolean validate(X509Certificate certificate)
                throws CertificateValidationException;
    }


    public static class CertificateValidationException extends Exception {

        public CertificateValidationException(String message) {
            super(message);
        }

        public CertificateValidationException(String message, Throwable cause) {
            super(message, cause);
        }
    
        public CertificateValidationException(Throwable cause) {
            super(cause);
        }
    }
}
