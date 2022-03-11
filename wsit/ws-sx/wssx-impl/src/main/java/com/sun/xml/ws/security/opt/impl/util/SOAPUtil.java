/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.opt.impl.util;

import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.security.secconv.WSSecureConversationRuntimeException;
import com.sun.xml.wss.impl.WssSoapFaultException;
import javax.xml.namespace.QName;
import com.sun.xml.ws.security.opt.api.keyinfo.BinarySecurityToken;
import com.sun.xml.wss.XWSSecurityException;
import com.sun.xml.wss.impl.MessageConstants;
import com.sun.xml.wss.impl.XWSSecurityRuntimeException;
import java.security.cert.X509Certificate;
import java.security.cert.CertificateFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import jakarta.xml.soap.Detail;
import jakarta.xml.soap.DetailEntry;
import jakarta.xml.soap.SOAPConstants;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPFactory;
import jakarta.xml.soap.SOAPFault;
import jakarta.xml.ws.soap.SOAPFaultException;

/**
 *
 * @author ashutosh.shahi@sun.com
 */
public class SOAPUtil {

    private static boolean enableFaultDetail = false;
    private static final String WSS_DEBUG_PROPERTY = "com.sun.xml.wss.debug";
    private static final String ENABLE_FAULT_DETAIL = "FaultDetail";
    protected static final LocalStringManagerImpl localStrings =
     new LocalStringManagerImpl(SOAPUtil.class);
    private static final String localizedGenericError;

    static {
        String debugFlag = System.getProperty(WSS_DEBUG_PROPERTY);
        if (debugFlag != null && debugFlag.contains(ENABLE_FAULT_DETAIL)) {
            enableFaultDetail = true;
        }
        localizedGenericError = localStrings.getLocalString("generic.validation.error",
                    "Invalid Security Header");
    }

    public static SOAPFaultException getSOAPFaultException(QName faultCode, WSSecureConversationRuntimeException wsre, SOAPFactory soapFactory, SOAPVersion sOAPVersion) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * @return the enableFaultDetail
     */
    public static boolean isEnableFaultDetail() {
        return enableFaultDetail;
    }

    /**
     * @return the localizedGenericError
     */
    public static String getLocalizedGenericError() {
        return localizedGenericError;
    }

    /** Creates a new instance of SOAPUtil */
    public SOAPUtil() {

    }

    public static String getIdFromFragmentRef(String ref) {
        char start = ref.charAt(0);
        if (start == '#') {
            return ref.substring(1);
        }
        return ref;
    }

    public static X509Certificate getCertificateFromToken(BinarySecurityToken bst) throws
            XWSSecurityException {
            byte[] data = bst.getTokenValue();
        try {
            CertificateFactory certFact = CertificateFactory.getInstance("X.509");
            return (X509Certificate) certFact.generateCertificate(new ByteArrayInputStream(data));

        } catch (Exception e) {
            throw new XWSSecurityException(
                    "Unable to create X509Certificate from data");
        }
     }

    /*
      Create and initialize a WssSoapFaultException.
     
    public static WssSoapFaultException newSOAPFaultException(
            String faultstring,
            Throwable th) {
        WssSoapFaultException sfe =
                new WssSoapFaultException(null, faultstring, null, null);
        sfe.initCause(th);
        return sfe;
    }*/

    /**
     * Create and initialize a WssSoapFaultException.
     */
    public static WssSoapFaultException newSOAPFaultException(
            QName faultCode,
            String faultstring,
            Throwable th) {

        if (!isEnableFaultDetail()) {
           return new WssSoapFaultException(MessageConstants.WSSE_INVALID_SECURITY, getLocalizedGenericError(), null, null);
        }
        WssSoapFaultException sfe =
                new WssSoapFaultException(faultCode, faultstring, null, null);
        sfe.initCause(th);
        return sfe;
    }
    
     public static WssSoapFaultException newSOAPFaultException(
            QName faultCode,
            String faultstring,
            Throwable th, boolean faultDetail) {

        if (!faultDetail) {
           return new WssSoapFaultException(MessageConstants.WSSE_INVALID_SECURITY, getLocalizedGenericError(), null, null);
        }
        WssSoapFaultException sfe =
                new WssSoapFaultException(faultCode, faultstring, null, null);
        sfe.initCause(th);
        return sfe;
    }

    protected static SOAPFault getSOAPFault(WssSoapFaultException sfe, SOAPFactory soapFactory, SOAPVersion version) {

        SOAPFault fault;
        String reasonText = sfe.getFaultString();
        if (reasonText == null) {
            reasonText = (sfe.getMessage() != null) ? sfe.getMessage() : "";
        }
        try {
            if (version == SOAPVersion.SOAP_12) {
                fault = soapFactory.createFault(reasonText, SOAPConstants.SOAP_SENDER_FAULT);
                fault.appendFaultSubcode(sfe.getFaultCode());
            } else {
                fault = soapFactory.createFault(reasonText, sfe.getFaultCode());
            }
        } catch (Exception e) {
            throw new XWSSecurityRuntimeException(e);
        }
        return fault;
    }

    protected static SOAPFault getSOAPFault(QName faultCode, String faultString, SOAPFactory soapFactory, SOAPVersion version) {

           SOAPFault fault;
        try {
            if (version == SOAPVersion.SOAP_12) {
                fault = soapFactory.createFault(faultString, SOAPConstants.SOAP_SENDER_FAULT);
                fault.appendFaultSubcode(faultCode);
            } else {
                fault = soapFactory.createFault(faultString, faultCode);
            }
        } catch (Exception e) {
            throw new XWSSecurityRuntimeException(e);
        }
        return fault;
    }

    public static SOAPFaultException getSOAPFaultException(WssSoapFaultException ex, SOAPFactory factory, SOAPVersion version) {
        SOAPFault fault = getSOAPFault(ex, factory, version);
        if (!isEnableFaultDetail()) {
           return createSOAPFault(fault,ex);
        }
        Throwable cause = ex.getCause();
        setFaultDetail(fault, cause);
        return createSOAPFault(fault,ex);

    }

     public static SOAPFaultException getSOAPFaultException(QName faultCode, Exception ex, SOAPFactory factory, SOAPVersion version) {
        String msg = getExceptionMessage(ex);
        SOAPFault fault = getSOAPFault(faultCode, msg, factory, version);
        if (!isEnableFaultDetail()) {
            return createSOAPFault(fault,ex);
        }
        setFaultDetail(fault, ex);
        return createSOAPFault(fault,ex);
    }

    public static SOAPFaultException getSOAPFaultException(Exception ex, SOAPFactory factory, SOAPVersion version) {
        String msg = getExceptionMessage(ex);
       
        SOAPFault fault = getSOAPFault(MessageConstants.WSSE_INVALID_SECURITY, msg, factory, version);
        if (!isEnableFaultDetail()) {
            return createSOAPFault(fault,ex);
        }
        setFaultDetail(fault, ex);
        return createSOAPFault(fault,ex);

    }

    private static SOAPFaultException createSOAPFault(SOAPFault fault, Throwable cause) {
        SOAPFaultException sfe = new SOAPFaultException(fault);
        if (isEnableFaultDetail()) {
            sfe.initCause(cause);
        } else {
            sfe.initCause(new Exception());
        }
        return sfe;
    }

    
    private static void setFaultDetail(SOAPFault fault, Throwable cause) {
        try {
            //Add Detail element to the fault.
            Detail detail = fault.addDetail();
            QName name = new QName("https://xwss.dev.java.net", "FaultDetail", "xwssfault");
            DetailEntry entry = detail.addDetailEntry(name);
            String exception = "Cause Not Set";
            if (cause != null) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                PrintWriter s = new PrintWriter(bos);
                cause.printStackTrace(s);
                s.flush();
                exception = bos.toString();
            }
            entry.addTextNode(exception);
        } catch (SOAPException ex) {
        //ignore for now
        }
    }

    private static String getExceptionMessage(Throwable ex) {
        if (!isEnableFaultDetail()) {
            return getLocalizedGenericError();
        }
        String msg = ex.getMessage();
        if (msg == null) {
            msg = ex.getClass().getName();
        }
        return msg;
    }
}

