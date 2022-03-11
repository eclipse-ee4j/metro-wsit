/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.core.reference;

import java.security.cert.X509Certificate;

public abstract class KeyIdentifierSPI {

    public static final String vmVendor = System.getProperty("java.vendor.url");
    public static final String sunVmVendor = "http://java.sun.com/";
    public static final String ibmVmVendor = "http://www.ibm.com/";
    public static final boolean isSunVM = sunVmVendor.equals(vmVendor) ? true: false;
    public static final boolean isIBMVM = ibmVmVendor.equals(vmVendor) ? true : false;

    private static final String sunKeyIdentifierSPIClass = "com.sun.wsit.security.SunKeyIdentifierSPI";
    private static final String ibmKeyIdentifierSPIClass = "com.sun.wsit.security.IBMKeyIdentifierSPI";
    private static final String sunKeyIdentifierImplClass="sun.security.x509.KeyIdentifier";
    private static final String ibmKeyIdentifierImplClass="com.ibm.security.x509.KeyIdentifier";

    protected static final String SUBJECT_KEY_IDENTIFIER_OID = "2.5.29.14";

    private static final KeyIdentifierSPI instance;

    static  {

       if (isSunVM) {
           instance = loadClass(sunKeyIdentifierSPIClass);
       } else if (isIBMVM) {
           instance = loadClass(ibmKeyIdentifierSPIClass);
       } else {
            if (testClassExist(sunKeyIdentifierImplClass)) {
               instance = loadClass(sunKeyIdentifierSPIClass);
           } else if (testClassExist(ibmKeyIdentifierImplClass)) {
               instance = loadClass(ibmKeyIdentifierSPIClass);
           } else {
               throw new UnsupportedOperationException("KeyIdentifierSPI Error : No known implementation for VM: " + vmVendor);
           }
       }
    }


    /** Creates a new instance of KeyIdentifierSPI */
    protected KeyIdentifierSPI() {
    }

    /**
     *Return the JRE vendor specific implementation of this SPI
     */
    public static KeyIdentifierSPI getInstance() {
        return instance;
    }

    private static boolean testClassExist(String className) {
        try {
            Class spiClass=null;
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            if (classLoader != null) {
                spiClass = classLoader.loadClass(className);
            }
            if (spiClass == null) {
                spiClass = Class.forName(className);
            }
            return (spiClass != null) ? true : false;
        } catch (Exception x) {
            return false;
        }
    }

    private static KeyIdentifierSPI loadClass(String className) {
        try {
            Class spiClass=null;
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            if (classLoader != null) {
                spiClass = classLoader.loadClass(className);
            }
            if (spiClass == null) {
                spiClass = Class.forName(className);
            }
            return (KeyIdentifierSPI)spiClass.newInstance();
        } catch (ClassNotFoundException x) {
            throw new RuntimeException(
                    "The KeyIdentifierSPI class: " + className + " specified was not found", x);
        } catch (Exception x) {
            throw new RuntimeException(
                    "The KeyIdentifierSPI class: " + className + " could not be instantiated ", x);
        }
    }

    public abstract byte[] getSubjectKeyIdentifier(X509Certificate cert)
       throws KeyIdentifierSPIException;

    protected static final class KeyIdentifierSPIException extends Exception {

        private static final long serialVersionUID = -9207910312279723431L;

        public KeyIdentifierSPIException(Exception ex) {
            this.initCause(ex);
        }

    }

}
