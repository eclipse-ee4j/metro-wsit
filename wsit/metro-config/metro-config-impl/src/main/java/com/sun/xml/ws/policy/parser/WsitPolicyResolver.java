/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.policy.parser;

import com.sun.istack.logging.Logger;
import com.sun.xml.ws.api.policy.AlternativeSelector;
import com.sun.xml.ws.api.policy.PolicyResolver;
import com.sun.xml.ws.api.policy.PolicyResolverFactory;
import com.sun.xml.ws.api.policy.ValidationProcessor;
import com.sun.xml.ws.policy.AssertionSet;
import com.sun.xml.ws.policy.EffectivePolicyModifier;
import com.sun.xml.ws.policy.Policy;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.PolicyMap;
import com.sun.xml.ws.policy.PolicyException;
import com.sun.xml.ws.policy.PolicyConstants;
import com.sun.xml.ws.policy.PolicyMapExtender;
import com.sun.xml.ws.policy.PolicyMapKey;
import com.sun.xml.ws.policy.PolicyMapMutator;
import com.sun.xml.ws.policy.PolicySubject;
import com.sun.xml.ws.policy.localization.LocalizationMessages;
import com.sun.xml.ws.policy.spi.PolicyAssertionValidator.Fitness;

import java.net.URL;
import java.util.Collection;
import javax.xml.stream.FactoryConfigurationError;
import jakarta.xml.ws.WebServiceException;

/**
 * Load and process the WSIT configuration files. If they are not present, fall
 * back to the JAX-WS default implementation.
 *
 * @author Rama Pulavarthi
 * @author Fabian Ritzmann
 */
public class WsitPolicyResolver implements PolicyResolver {

    private static final Logger LOGGER = Logger.getLogger(WsitPolicyResolver.class);

    public WsitPolicyResolver() {
    }

    @Override
    public PolicyMap resolve(ServerContext context) throws WebServiceException {
        final Class<?> endpointClass = context.getEndpointClass();
        final String configId = endpointClass == null ? null : endpointClass.getName();
        if (!context.hasWsdl()) {
            // Parse WSIT config file.
            PolicyMap map = null;
            try {
                Collection<PolicyMapMutator> mutators = context.getMutators();
                // No need to check if configId != null because it never is if we have WSDL
                map = PolicyConfigParser.parse(configId, context.getContainer(),
                                               mutators.toArray(new PolicyMapMutator[0]));
            } catch (PolicyException e) {
                throw LOGGER.logSevereException(new WebServiceException(
                        LocalizationMessages.WSP_5006_FAILED_TO_READ_WSIT_CONFIG_FOR_ID(configId), e));
            }
            if (map == null)
                LOGGER.config(LocalizationMessages.WSP_5008_CREATE_POLICY_MAP_FOR_CONFIG(configId));
            else {
                // Validate server-side Policies such that there exists a single alternative in each scope.
                validateServerPolicyMap(map);
            }
            return map;
        }
        else {
            try {
                if (configId != null) {
                    // Server-side, there should be only one policy configuration either WSDL or WSIT config.
                    final URL wsitConfigFile = PolicyConfigParser.findConfigFile(configId, context.getContainer());
                    if (wsitConfigFile != null) {
                        LOGGER.warning(LocalizationMessages.WSP_5024_WSIT_CONFIG_AND_WSDL(wsitConfigFile));
                    }
                }
                return PolicyResolverFactory.DEFAULT_POLICY_RESOLVER.resolve(context);
            } catch (PolicyException e) {
                throw LOGGER.logSevereException(new WebServiceException(LocalizationMessages.WSP_5023_FIND_WSIT_CONFIG_FAILED(), e));
            }
        }
    }

    @Override
    public PolicyMap resolve(ClientContext context) {
        PolicyMap effectivePolicyMap;
        try {
            final PolicyMap clientConfigPolicyMap = PolicyConfigParser.parse(
                    PolicyConstants.CLIENT_CONFIGURATION_IDENTIFIER, context.getContainer());
            if (clientConfigPolicyMap == null) {
                LOGGER.config(LocalizationMessages.WSP_5014_CLIENT_CONFIG_PROCESSING_SKIPPED());
                effectivePolicyMap = context.getPolicyMap();
            } else {
                //Merge Policy Configuration from WSDL and configuration file.
                effectivePolicyMap = mergePolicyMap(context.getPolicyMap(), clientConfigPolicyMap);
            }
        } catch (PolicyException e) {
            throw LOGGER.logSevereException(new WebServiceException(
                    LocalizationMessages.WSP_5004_ERROR_WHILE_PROCESSING_CLIENT_CONFIG(), e));
        }
        // Chooses best alternative and sets it as effective Policy in each scope.
        if(effectivePolicyMap != null)
            return doAlternativeSelection(effectivePolicyMap);
        else
            return null;
    }

     /**
     * Checks if the PolicyMap has only single alternative in the scope.
     *
     * @param policyMap
     *      PolicyMap that needs to be validated.
     */
    private static void validateServerPolicyMap(PolicyMap policyMap) {
        try {
            final ValidationProcessor validationProcessor = ValidationProcessor.getInstance();

            for (Policy policy : policyMap) {

                // TODO:  here is a good place to check if the actual policy has only one alternative...

                for (AssertionSet assertionSet : policy) {
                    for (PolicyAssertion assertion : assertionSet) {
                        Fitness validationResult = validationProcessor.validateServerSide(assertion);
                        if (validationResult != Fitness.SUPPORTED) {
                            throw new PolicyException(LocalizationMessages.WSP_5017_SERVER_SIDE_ASSERTION_VALIDATION_FAILED(
                                    assertion.getName(),
                                    validationResult));
                        }
                    }
                }
            }
        } catch (PolicyException e) {
            throw new WebServiceException(e);
        }
    }

    /**
     * Selects a best alternative if there are multiple policy alternatives.
     *
     */
    private static PolicyMap doAlternativeSelection(PolicyMap policyMap) {
        final EffectivePolicyModifier modifier = EffectivePolicyModifier.createEffectivePolicyModifier();
        modifier.connect(policyMap);
        try {
            AlternativeSelector.doSelection(modifier);
        } catch (PolicyException e) {
            throw new WebServiceException(e);
        } finally {
            modifier.disconnect();
        }
        return policyMap;
    }

    /**
     * Merge Policies policyMap and clientPolicyMap.
     *
     * @param policyMap The first policy map to be merged.
     * @param clientPolicyMap The second policy map to be merged.
     * @return merged PolicyMap
     * @throws PolicyException If merge failed.
     */
    private static PolicyMap mergePolicyMap(PolicyMap policyMap, PolicyMap clientPolicyMap) throws PolicyException {
        final PolicyMapExtender mapExtender = PolicyMapExtender.createPolicyMapExtender();
        final String clientWsitConfigId = PolicyConstants.CLIENT_CONFIGURATION_IDENTIFIER;
        if (policyMap != null) {
            mapExtender.connect(policyMap);
            try {
                for (PolicyMapKey key : clientPolicyMap.getAllServiceScopeKeys()) {
                    final Policy policy = clientPolicyMap.getServiceEffectivePolicy(key);
                    // setting subject to provided URL of client WSIT config
                    mapExtender.putServiceSubject(key, new PolicySubject(clientWsitConfigId, policy));
                }

                for (PolicyMapKey key : clientPolicyMap.getAllEndpointScopeKeys()) {
                    final Policy policy = clientPolicyMap.getEndpointEffectivePolicy(key);
                    // setting subject to provided URL of client WSIT config
                    mapExtender.putEndpointSubject(key, new PolicySubject(clientWsitConfigId, policy));
                }

                for (PolicyMapKey key : clientPolicyMap.getAllOperationScopeKeys()) {
                    final Policy policy = clientPolicyMap.getOperationEffectivePolicy(key);
                    // setting subject to provided URL of client WSIT config
                    mapExtender.putOperationSubject(key, new PolicySubject(clientWsitConfigId, policy));
                }

                for (PolicyMapKey key : clientPolicyMap.getAllInputMessageScopeKeys()) {
                    final Policy policy = clientPolicyMap.getInputMessageEffectivePolicy(key);
                    // setting subject to provided URL of client WSIT config
                    mapExtender.putInputMessageSubject(key, new PolicySubject(clientWsitConfigId, policy));
                }

                for (PolicyMapKey key : clientPolicyMap.getAllOutputMessageScopeKeys()) {
                    final Policy policy = clientPolicyMap.getOutputMessageEffectivePolicy(key);
                    // setting subject to provided URL of client WSIT config
                    mapExtender.putOutputMessageSubject(key, new PolicySubject(clientWsitConfigId, policy));
                }

                for (PolicyMapKey key : clientPolicyMap.getAllFaultMessageScopeKeys()) {
                    final Policy policy = clientPolicyMap.getFaultMessageEffectivePolicy(key);
                    // setting subject to provided URL of client WSIT config
                    mapExtender.putFaultMessageSubject(key, new PolicySubject(clientWsitConfigId, policy));
                }
                LOGGER.fine(LocalizationMessages.WSP_5015_CLIENT_CFG_POLICIES_TRANSFERED_INTO_FINAL_POLICY_MAP(policyMap));
            } catch (FactoryConfigurationError ex) {
                throw LOGGER.logSevereException(new PolicyException(ex));
            }
            return policyMap;

        } else {
            return clientPolicyMap;
        }
    }

}
