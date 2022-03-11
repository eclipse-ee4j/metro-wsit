/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.addressing.impl.policy;


import com.sun.xml.ws.api.addressing.AddressingVersion;
import com.sun.xml.ws.policy.AssertionSet;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.ws.policy.sourcemodel.AssertionData;
import com.sun.xml.ws.policy.spi.AssertionCreationException;
import com.sun.xml.ws.policy.spi.PolicyAssertionCreator;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashSet;
import java.util.logging.Level;

/**
 *
 * @author K.Venugopal@sun.com
 */
public class AddressingPolicyAssertionCreator implements PolicyAssertionCreator {

    private static HashSet<String> implementedAssertions = new HashSet<>();
    private static final String [] NS_SUPPORTED_LIST = new String[] { AddressingVersion.MEMBER.nsUri,
                                                                      AddressingVersion.W3C.nsUri };

    private static final PolicyLogger LOGGER = PolicyLogger.getLogger(AddressingPolicyAssertionCreator.class);

    static{
        implementedAssertions.add("Address");
        implementedAssertions.add("EndpointReference");
    }

    /** Creates a new instance of AddressingPolicyAssertionCreator */
    public AddressingPolicyAssertionCreator() {
    }


    @Override
    public String[] getSupportedDomainNamespaceURIs() {
        return NS_SUPPORTED_LIST;
    }

    protected Class<?> getClass(final AssertionData assertionData) throws AssertionCreationException {
        LOGGER.entering(assertionData);
        try {
            final String className = assertionData.getName().getLocalPart();
            final Class<?> result = Class.forName("com.sun.xml.ws.security.addressing.impl.policy." + className);
            LOGGER.exiting();
            return result;
        } catch (ClassNotFoundException ex) {
            LOGGER.warning(LocalizationMessages.WSA_0001_UNKNOWN_ASSERTION(assertionData.toString()), ex);
            throw new AssertionCreationException(assertionData,ex);
        }
    }

    @Override
    public PolicyAssertion createAssertion(AssertionData assertionData, Collection<PolicyAssertion> nestedAssertions, AssertionSet nestedAlternative, PolicyAssertionCreator policyAssertionCreator) throws AssertionCreationException {
        String localName = assertionData.getName().getLocalPart();
        if(implementedAssertions.contains(localName)){
            Class<?> cl = this.getClass(assertionData);
            Constructor<?> cons = null;
            try {

                cons = getConstructor(cl);

                //cl.getConstructor(javax.xml.stream.events.StartElement.class);
            } catch (NoSuchMethodException ex) {
                if(LOGGER.isLoggable(Level.SEVERE)){
                    LOGGER.log(Level.SEVERE,LocalizationMessages.WSA_0002_ERROR_OBTAINING_CONSTRUCTOR(assertionData.getName()),ex);
                }
                throw new AssertionCreationException(assertionData,ex);
            }catch (SecurityException ex) {
                if(LOGGER.isLoggable(Level.SEVERE)){
                    LOGGER.log(Level.SEVERE,LocalizationMessages.WSA_0002_ERROR_OBTAINING_CONSTRUCTOR(assertionData.getName()),ex);
                }


                throw new AssertionCreationException(assertionData,ex);
            }
            if(cons != null){
                try {
                    return (PolicyAssertion) cons.newInstance(assertionData, nestedAssertions, nestedAlternative);
                } catch (IllegalArgumentException ex) {
                    if(LOGGER.isLoggable(Level.SEVERE)){
                        LOGGER.log(Level.SEVERE,LocalizationMessages.WSA_0003_ERROR_INSTANTIATING(assertionData.getName()));
                    }

                    throw new AssertionCreationException(assertionData,ex);
                } catch (InvocationTargetException ex) {
                    if(LOGGER.isLoggable(Level.SEVERE)){
                        LOGGER.log(Level.SEVERE,LocalizationMessages.WSA_0003_ERROR_INSTANTIATING(assertionData.getName()));
                    }
                    throw new AssertionCreationException(assertionData,ex);
                } catch (InstantiationException ex) {
                    if(LOGGER.isLoggable(Level.SEVERE)){
                        LOGGER.log(Level.SEVERE,LocalizationMessages.WSA_0003_ERROR_INSTANTIATING(assertionData.getName()));
                    }
                    throw new AssertionCreationException(assertionData,ex);
                } catch (IllegalAccessException ex) {
                    if(LOGGER.isLoggable(Level.SEVERE)){
                        LOGGER.log(Level.SEVERE,LocalizationMessages.WSA_0003_ERROR_INSTANTIATING(assertionData.getName()));
                    }
                    throw new AssertionCreationException(assertionData,ex);
                }
            }else{
                try{
                    return (PolicyAssertion)cl.newInstance();
                } catch (InstantiationException ex) {
                    if(LOGGER.isLoggable(Level.SEVERE)){
                        LOGGER.log(Level.SEVERE,LocalizationMessages.WSA_0003_ERROR_INSTANTIATING(assertionData.getName()));
                    }
                    throw new AssertionCreationException(assertionData,ex);
                } catch (IllegalAccessException ex) {
                    if(LOGGER.isLoggable(Level.SEVERE)){
                        LOGGER.log(Level.SEVERE,LocalizationMessages.WSA_0003_ERROR_INSTANTIATING(assertionData.getName()));
                    }
                    throw new AssertionCreationException(assertionData,ex);
                }
            }


        }
        return policyAssertionCreator.createAssertion(assertionData,nestedAssertions,nestedAlternative,policyAssertionCreator);

    }

    private <T> Constructor<T> getConstructor(Class<T> cl) throws NoSuchMethodException{
        return cl.getConstructor(com.sun.xml.ws.policy.sourcemodel.AssertionData.class,java.util.Collection.class,com.sun.xml.ws.policy.AssertionSet.class);
    }

}
