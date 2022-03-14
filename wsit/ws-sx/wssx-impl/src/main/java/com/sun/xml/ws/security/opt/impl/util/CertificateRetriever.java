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

import com.sun.xml.ws.security.impl.policy.PolicyUtil;
import org.apache.xml.security.exceptions.Base64DecodingException;
import com.sun.xml.ws.api.server.WSEndpoint;
import com.sun.xml.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.api.security.trust.client.STSIssuedTokenConfiguration;
import com.sun.xml.ws.policy.AssertionSet;
import com.sun.xml.ws.policy.Policy;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.PolicyException;
import com.sun.xml.ws.policy.PolicyMap;
import com.sun.xml.ws.policy.PolicyMapKey;
import com.sun.xml.wss.AliasSelector;
import com.sun.xml.wss.SecurityEnvironment;
import com.sun.xml.wss.XWSSConstants;
import com.sun.xml.wss.XWSSecurityException;
import com.sun.xml.wss.impl.MessageConstants;
import com.sun.xml.wss.impl.ProcessingContextImpl;
import com.sun.xml.wss.impl.WssSoapFaultException;
import com.sun.xml.wss.impl.callback.KeyStoreCallback;
import com.sun.xml.wss.impl.misc.Base64;
import com.sun.xml.wss.jaxws.impl.TubeConfiguration;
import com.sun.xml.wss.logging.LogDomainConstants;
import com.sun.xml.wss.logging.LogStringsMessages;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.jvnet.staxex.Base64Data;
import org.jvnet.staxex.XMLStreamReaderEx;
import java.security.KeyStore;
import java.security.cert.Certificate;

/**
 *
 * @author suresh
 */
public class CertificateRetriever {

    protected TubeConfiguration pipeConfig = null;
    private static Logger log = Logger.getLogger(LogDomainConstants.WSS_API_DOMAIN,
            LogDomainConstants.WSS_API_DOMAIN_BUNDLE);
    private String location = null;
    private String password = null;
    private String alias = null;
    private Certificate cs = null;
    private FileInputStream fis = null;
    private Policy ep = null;
    private String callbackHandler = null;
    private String aliasSelector = null;

    public CertificateRetriever() {
    }

    public byte[] getBSTFromIdentityExtension(XMLStreamReader reader) throws XMLStreamException {
        boolean isKeyInfo = false;
        boolean isBST = false;
        byte[] bstValue = null;
        while (reader.hasNext()) {
            if (reader.getEventType() == XMLStreamReader.START_ELEMENT) {
                isBST = "BinarySecurityToken".equals(reader.getLocalName()) && MessageConstants.WSSE_NS.equals(reader.getNamespaceURI());
                isKeyInfo = "KeyInfo".equals(reader.getLocalName()) && MessageConstants.DSIG_NS.equals(reader.getNamespaceURI()) ;
                if (isBST || isKeyInfo) {
                    if(isBST){
                         reader.next();
                    }else if(isKeyInfo) {
                       while(reader.hasNext() && !"X509Certificate".equals(reader.getLocalName())){// goes to KeyInfo/X509Data/X509Certificate
                          reader.next();
                       }
                       reader.next();
                    }
                    if (reader.getEventType() == XMLStreamReader.CHARACTERS) {

                        if (reader instanceof XMLStreamReaderEx) {
                            CharSequence data = ((XMLStreamReaderEx) reader).getPCDATA();
                            if (data instanceof Base64Data) {
                                Base64Data binaryData = (Base64Data) data;
                                bstValue = binaryData.getExact();
                                return bstValue;
                            }
                        }
                        try {
                            bstValue = Base64.decode(StreamUtil.getCV(reader));
                        } catch (Base64DecodingException ex) {
                            log.log(Level.WARNING, LogStringsMessages.WSS_0819_ERROR_GETTING_CERTIFICATE_EPRIDENTITY(),ex);
                        //throw new RuntimeException(ex);
                        }
                    } else {
                        log.log(Level.WARNING, LogStringsMessages.WSS_0819_ERROR_GETTING_CERTIFICATE_EPRIDENTITY());
                    //throw new RuntimeException("error reading the xml stream");
                    }
                    return bstValue;
                }
            }
            reader.next();
        }
        return null;
    }

    public Certificate getServerKeyStore(WSEndpoint wse) throws IOException {

        QName keyStoreQName = new QName("http://schemas.sun.com/2006/03/wss/server", "KeyStore");
        setLocationPasswordAndAlias(keyStoreQName, wse);

        if (password == null || location == null) {
            if (callbackHandler == null) {
                return null;
            } else {
                cs = getCertificateUsingCallbackHandler(callbackHandler);
                return cs;
            }
        }
        if (alias == null) {
            alias = getAliasUsingAliasSelector();
        }
        KeyStore keyStore = null;
        try {
            keyStore = KeyStore.getInstance("JKS");
            fis = new java.io.FileInputStream(location);
            keyStore.load(fis, password.toCharArray());
            cs = keyStore.getCertificate(alias);
            if (cs == null) {
                log.log(Level.WARNING, LogStringsMessages.WSS_0821_CERTIFICATE_NOT_FOUND_FOR_ALIAS(alias));
            }
        } catch (FileNotFoundException ex) {
            log.log(Level.WARNING, LogStringsMessages.WSS_0818_ERROR_PUTTING_CERTIFICATE_EPRIDENTITY(), ex);
            return null;
        } catch (IOException ex) {
            log.log(Level.WARNING, LogStringsMessages.WSS_0818_ERROR_PUTTING_CERTIFICATE_EPRIDENTITY(), ex);
            return null;
        } catch (NoSuchAlgorithmException ex) {
            log.log(Level.WARNING, LogStringsMessages.WSS_0818_ERROR_PUTTING_CERTIFICATE_EPRIDENTITY(), ex);
            return null;
        } catch (CertificateException ex) {
            log.log(Level.WARNING, LogStringsMessages.WSS_0818_ERROR_PUTTING_CERTIFICATE_EPRIDENTITY(), ex);
            return null;
        } catch (KeyStoreException ex) {
            log.log(Level.WARNING, LogStringsMessages.WSS_0818_ERROR_PUTTING_CERTIFICATE_EPRIDENTITY(), ex);
            return null;
        } finally {
            keyStore = null;
            fis.close();
        }
        return cs;

    }

    public X509Certificate constructCertificate(byte[] bstValue) {
        try {
            X509Certificate cert = null;
            CertificateFactory fact = CertificateFactory.getInstance("X.509");
            cert = (X509Certificate) fact.generateCertificate(new ByteArrayInputStream(bstValue));
            return cert;
        } catch (CertificateException ex) {
            log.log(Level.SEVERE, "error while constructing the certificate from bst value ", ex);
            throw new RuntimeException(ex);
        }
    }

    public boolean checkforEPRIdentity(WSEndpoint wse, QName eprQName) {

        if (wse.getPort() == null) {
            return true;
        }
        getEndpointOROperationalLevelPolicy(wse);
        if (ep == null) {
            return false;
        }
        for (AssertionSet assertionSet : ep) {
            for (PolicyAssertion pa : assertionSet) {
                if (pa.getName().equals(eprQName)) {
                    return true;
                }
            }
        }
        return false;
    }

    private String getAliasUsingAliasSelector() {
        if (aliasSelector == null) {
            return null;
        }
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Class aliasSelectorClass = null;
        if (loader != null) {
            try {
                aliasSelectorClass = loader.loadClass(aliasSelector);
            } catch (ClassNotFoundException e) {
                return null;
            }
        }
        if (aliasSelectorClass == null) {
            // if context classloader didnt work, try this
            loader = this.getClass().getClassLoader();
            try {
                aliasSelectorClass = loader.loadClass(aliasSelector);
            } catch (ClassNotFoundException e) {
                return null;
            }
        }
        if (aliasSelectorClass == null) {
            return null;
        }
        try {
            com.sun.xml.wss.AliasSelector as = (AliasSelector) aliasSelectorClass.getConstructor().newInstance();
            String myAlias = as.select(new java.util.HashMap());//passing empty map as runtime properties is not available here;
            if (myAlias == null) {
                log.log(Level.WARNING, LogStringsMessages.WSS_0823_ALIAS_NOTFOUND_FOR_ALIAS_SELECTOR());
            }
            return myAlias;
        } catch (ReflectiveOperationException ex) {
            log.log(Level.WARNING, LogStringsMessages.WSS_0818_ERROR_PUTTING_CERTIFICATE_EPRIDENTITY(), ex);
            return null;
        }
    }

    private X509Certificate getCertificateUsingCallbackHandler(String callbackHandler) {

        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Class callbackHandlerClass = null;
        if (loader != null) {
            try {
                callbackHandlerClass = loader.loadClass(callbackHandler);
            } catch (ClassNotFoundException e) {
                return null;
            }
        }
        if (callbackHandlerClass == null) {
            // if context classloader didnt work, try this
            loader = this.getClass().getClassLoader();
            try {
                callbackHandlerClass = loader.loadClass(callbackHandler);
            } catch (ClassNotFoundException e) {
                return null;
            }
        }
        if (callbackHandlerClass == null) {
            return null;
        }
        KeyStoreCallback ksc = new KeyStoreCallback();
        Callback[] callbacks = new Callback[1];
        callbacks[0] = ksc;
        try {
            javax.security.auth.callback.CallbackHandler cbh = (CallbackHandler) callbackHandlerClass.getConstructor().newInstance();
            cbh.handle(callbacks);
            X509Certificate cert = null;
            cert = (X509Certificate) ((ksc.getKeystore() != null) ? (ksc.getKeystore().getCertificate(alias)) : null);
            if (cert == null && alias != null) {
                log.log(Level.WARNING, LogStringsMessages.WSS_0821_CERTIFICATE_NOT_FOUND_FOR_ALIAS(alias));
            }
            return cert;
        } catch (IOException | KeyStoreException | ReflectiveOperationException | UnsupportedCallbackException ex) {
            log.log(Level.WARNING, LogStringsMessages.WSS_0818_ERROR_PUTTING_CERTIFICATE_EPRIDENTITY(), ex);
            return null;
        }
    }

    private void getEndpointOROperationalLevelPolicy(WSEndpoint wse) {
        PolicyMap pm = wse.getPolicyMap();
        WSDLPort port = wse.getPort();
        QName serviceName = port.getOwner().getName();
        QName portName = port.getName();

        PolicyMapKey endpointKey = PolicyMap.createWsdlEndpointScopeKey(serviceName, portName);

        try {
            ep = pm.getEndpointEffectivePolicy(endpointKey);
            if (ep == null) {
                for (WSDLBoundOperation operation : port.getBinding().getBindingOperations()) {
                    QName operationName = new QName(operation.getBoundPortType().getName().getNamespaceURI(),
                            operation.getName().getLocalPart());
                    PolicyMapKey operationKey = PolicyMap.createWsdlOperationScopeKey(serviceName, portName, operationName);
                    ep = pm.getOperationEffectivePolicy(operationKey);
                    if (ep != null) {
                        break;
                    }
                }
            }
        } catch (PolicyException | IllegalArgumentException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void setLocationPasswordAndAlias(QName qName, WSEndpoint wse) {
        if (wse.getPort() == null) {
            return;
        }
        if (ep == null) {
            return;
        }
        for (AssertionSet assertionSet : ep) {
            for (PolicyAssertion pa : assertionSet) {
                if (PolicyUtil.isConfigPolicyAssertion(pa)) {
                    if (!pa.getName().equals(qName)) {
                        continue;
                    }

                    password = pa.getAttributeValue(new QName("storepass"));
                    location = pa.getAttributeValue(new QName("location"));
                    alias = pa.getAttributeValue(new QName("alias"));
                    callbackHandler = pa.getAttributeValue(new QName("callbackHandler"));
                    aliasSelector = pa.getAttributeValue(new QName("aliasSelector"));

                    StringBuilder sb = null;
                    if (location != null) {
                        sb = new StringBuilder(location);
                        if (location.startsWith("$WSIT")) {
                            String path = System.getProperty("WSIT_HOME");
                            sb.replace(0, 10, path);
                            location = sb.toString();
                        }
                    }
                }
            }
        }
    }
    @SuppressWarnings("unchecked")
    public boolean setServerCertInTheContext(ProcessingContextImpl ctx, SecurityEnvironment secEnv, X509Certificate serverCert) {
        boolean valid = false;
        try {
            valid = secEnv.validateCertificate(serverCert, ctx.getExtraneousProperties());
        } catch (WssSoapFaultException ex) {
            //log.log(Level.WARNING, "exception during validating the server certificate");
        } catch (XWSSecurityException ex) {
            log.log(Level.SEVERE,LogStringsMessages.WSS_0820_ERROR_VALIDATE_CERTIFICATE_EPRIDENTITY(), ex);
        }
        if (valid) {
            log.log(Level.INFO, LogStringsMessages.WSS_0824_USING_SERVER_CERTIFICATE_FROM_EPR_IDENTITY());
            ctx.getExtraneousProperties().put(XWSSConstants.SERVER_CERTIFICATE_PROPERTY, serverCert);
        } else {
            log.log(Level.WARNING, LogStringsMessages.WSS_0822_ERROR_VALIDATING_SERVER_CERTIFICATE());
        }
        return valid;
    }

    public boolean setServerCertInTheSTSConfig(STSIssuedTokenConfiguration config, SecurityEnvironment secEnv, X509Certificate serverCert) {
        boolean valid = false;
        try {
            valid = secEnv.validateCertificate(serverCert, config.getOtherOptions());
        } catch (WssSoapFaultException ex) {
            //log.log(Level.WARNING, "exception during validating the server certificate");
        } catch (XWSSecurityException ex) {
            log.log(Level.SEVERE, LogStringsMessages.WSS_0820_ERROR_VALIDATE_CERTIFICATE_EPRIDENTITY(), ex);
        }
        if (valid) {
            log.log(Level.INFO,LogStringsMessages.WSS_0824_USING_SERVER_CERTIFICATE_FROM_EPR_IDENTITY());
            config.getOtherOptions().put("Identity", serverCert);
        } else {
            log.log(Level.WARNING, LogStringsMessages.WSS_0822_ERROR_VALIDATING_SERVER_CERTIFICATE());
        }
        return valid;
    }
}
