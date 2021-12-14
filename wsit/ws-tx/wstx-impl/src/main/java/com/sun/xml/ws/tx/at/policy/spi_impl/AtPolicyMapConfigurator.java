/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.at.policy.spi_impl;

import com.sun.istack.Nullable;
import com.sun.istack.logging.Logger;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.model.JavaMethod;
import com.sun.xml.ws.api.model.SEIModel;
import com.sun.xml.ws.policy.Policy;
import com.sun.xml.ws.policy.PolicyException;
import com.sun.xml.ws.policy.PolicyMap;
import com.sun.xml.ws.policy.PolicySubject;
import com.sun.xml.ws.policy.jaxws.spi.PolicyMapConfigurator;
import com.sun.xml.ws.policy.subject.WsdlBindingSubject;
import com.sun.xml.ws.api.tx.at.Transactional;
import com.sun.xml.ws.tx.at.localization.LocalizationMessages;
import com.sun.xml.ws.tx.at.policy.AtPolicyCreator;
import com.sun.xml.ws.tx.at.policy.EjbTransactionType;
import java.lang.reflect.Method;

import javax.xml.namespace.QName;
import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Level;

/**
 * From CMT EJB methods generate wsdl:binding/wsdl:operations with semantically equivalent WS-AT Policy Assertion(s).
 * <p>
 * Known limitation: not accounting for EJB deployment descriptor, only working off of TransactionAttribute annotations.
 */
public class AtPolicyMapConfigurator implements PolicyMapConfigurator {

    private static final Logger LOGGER = Logger.getLogger(AtPolicyMapConfigurator.class);

    /**
     * Update policy map with operation scope of correct WS-AT policy assertions.
     * <p>
     * Only looking for this for Java to WSDL at tool time.
     *
     */
    @Override
    public Collection<PolicySubject> update(final PolicyMap policyMap, final SEIModel model, final WSBinding wsBinding) throws PolicyException {
        final Collection<PolicySubject> subjects = new LinkedList<>();

        Class<?> seiClass = getDeclaringClass(model);
        if (seiClass == null) {
            return subjects;
        }

        /*
          For now we are not going to consider EJB TX annotations, because we don't have access
          to the EJB deployment descriptor, which could lead to inconsistent WS-AT configuration
          behavior between EJB + annotations and EJB + DD use cases
         */
        // final EjbTransactionType defaultEjbTxnAttr = EjbTransactionType.getDefaultFor(seiClass);
        final EjbTransactionType defaultEjbTxnAttr = EjbTransactionType.NOT_DEFINED;
        final Transactional defaultFeature = seiClass.getAnnotation(Transactional.class);
        for (JavaMethod method : model.getJavaMethods()) {
            final Transactional effectiveFeature = getEffectiveFeature(method.getSEIMethod(), defaultFeature);
            if (effectiveFeature == null || effectiveFeature.enabled() == false) {
                continue;
            }

            final EjbTransactionType effectiveEjbTxType = defaultEjbTxnAttr.getEffectiveType(method.getSEIMethod());

            final String policyId = model.getBoundPortTypeName().getLocalPart() + "_" + method.getOperationName() + "_WSAT_Policy";
            final Policy policy = AtPolicyCreator.createPolicy(policyId, effectiveFeature.version().namespaceVersion, effectiveFeature.value(), effectiveEjbTxType);
            if (policy != null) {
                // attach ws-at policy assertion to binding/operation
                final WsdlBindingSubject wsdlSubject = WsdlBindingSubject.createBindingOperationSubject(model.getBoundPortTypeName(),
                        new QName(model.getTargetNamespace(), method.getOperationName()));
                final PolicySubject generatedWsatPolicySubject = new PolicySubject(wsdlSubject, policy);
                if (LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.fine(LocalizationMessages.WSAT_1002_ADD_AT_POLICY_ASSERTION(
                            model.getPortName().toString(),
                            method.getOperationName(),
                            seiClass.getName(),
                            method.getSEIMethod().getName(),
                            effectiveFeature.value().toString(),
                            effectiveEjbTxType.toString(),
                            policy.toString()));
                }
                subjects.add(generatedWsatPolicySubject);
            }
        }

        return subjects;
    }

    private Class<?> getDeclaringClass(@Nullable SEIModel model) {
        if (model == null || model.getJavaMethods().isEmpty()) {
            return null;
        }

        return model.getJavaMethods().iterator().next().getSEIMethod().getDeclaringClass();
    }

    private Transactional getEffectiveFeature(Method method, Transactional defaultFeature) {
        Transactional feature = method.getAnnotation(Transactional.class);
        if (feature != null) {
            // TODO check compatibility with (existing) default?

            return feature;
        }

        return defaultFeature;
    }
}
