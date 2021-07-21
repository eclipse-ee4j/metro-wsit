/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.api.tx.at;

import com.sun.istack.logging.Logger;
import com.sun.xml.ws.api.FeatureConstructor;

import javax.xml.ws.WebServiceFeature;
import java.util.HashMap;
import java.util.Map;

/**
 * This feature represents the use of WS-AT with a
 * web service.
 * <p>
 * The following describes the affects of this feature with respect
 * to being enabled or disabled:
 * <ul>
 * <li> ENABLED: In this Mode, WS-AT will be enabled.
 * <li> DISABLED: In this Mode, WS-AT will be disabled
 * </ul>
 */
public class TransactionalFeature extends WebServiceFeature {

    private static final Logger LOGGER = Logger.getLogger(TransactionalFeature.class);
    /**
     * Constant value identifying the TransactionalFeature
     */
    public static final String ID = "http://docs.oasis-open.org/ws-tx/";
    private Transactional.TransactionFlowType flowType = Transactional.TransactionFlowType.SUPPORTS;
    private boolean isExplicitMode;
    private Transactional.Version version = Transactional.Version.DEFAULT;
    private Map<String, Transactional.TransactionFlowType> flowTypeMap = new HashMap<String, Transactional.TransactionFlowType>();
    private Map<String, Boolean> enabledMap = new HashMap<String, Boolean>();

    @FeatureConstructor({"enabled", "value", "version"})
    public TransactionalFeature(boolean enabled, Transactional.TransactionFlowType value, Transactional.Version version) {
        LOGGER.entering(enabled, value, version);
        
        this.enabled = enabled;
        this.flowType = value;
        this.version = version;
    }

    /**
     * Create an <code>TransactionalFeature</code>.
     * The instance created will be enabled.
     */
    public TransactionalFeature() {
        LOGGER.entering();
        
        this.enabled = true;
    }

    /**
     * Create an <code>TransactionalFeature</code>
     *
     * @param enabled specifies whether this feature should
     *                be enabled or not.
     */
    public TransactionalFeature(boolean enabled) {
        LOGGER.entering(enabled);
        
        this.enabled = enabled;
    }

    /**
     * Returns the default Transaction flow type for all operations.
     * @return Transactional.TransactionFlowType
     */
    public Transactional.TransactionFlowType getFlowType() {
        return flowType;
    }

    /**
     * Returns the Transaction flow type for a given operation.
     * @return Transactional.TransactionFlowType
     */
    public Transactional.TransactionFlowType getFlowType(String operationName) {
        Transactional.TransactionFlowType type = flowTypeMap.get(operationName);
        if (!isExplicitMode && type == null) {
            type = flowType;
        }
        return type;
    }

    /**
     * Set the default Transaction flow type for all operations.
     * @param flowType
     */
    public void setFlowType(Transactional.TransactionFlowType flowType) {
        this.flowType = flowType;
    }

    /**
     * Set the Transaction flow type for a given wsdl:operation.
     * @param operationName  the local part of wsdl:opration
     * @param flowType Transaction flow type
     */
    public void setFlowType(String operationName, Transactional.TransactionFlowType flowType) {
        flowTypeMap.put(operationName, flowType);
    }

    public String getID() {
        return ID;
    }

    /**
     * Enable/disable this feature at port level
     *
     * @param enabled
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Enable/disable this feature on a given operation
     *
     * @param operationName the local part of operation.
     * @param enabled
     */
    public void setEnabled(String operationName, boolean enabled) {
        enabledMap.put(operationName, enabled);
    }

    /**
     * Returns <code>true</code> if WS-AT is enabled on the given operation.
     *
     * @param operationName  the local part of wsdl:operation
     * @return <code>true</code> if and only if the WS-AT is enabled on the given operation.
     */
    public boolean isEnabled(String operationName) {
        Boolean isEnabled = enabledMap.get(operationName);
        if (isEnabled == null) {
            return isExplicitMode ? false : enabled;
        } else {
            return isEnabled;
        }
    }

    /**
     * Returns the version of WS-AT to be used.
     *
     * @return  Transactional.Version
     */
    public Transactional.Version getVersion() {
        return version;
    }

    /**
     * Set the version of WS-AT to be used.
     *
     * @param version   the version of WS-AT to be used.
     */
    public void setVersion(Transactional.Version version) {
        this.version = version;
    }

    /**
     *  return a map listing the Transactional flow options for operations.
     * @return a mapping listing the Transactional flow options explicit on operations.
     */
    public Map<String, Transactional.TransactionFlowType> getFlowTypeMap() {
        return flowTypeMap;
    }

    /**
     *
     * @return a mapping listing the transactional enabled attributes explicitly set on operations.
     */
    public Map<String, Boolean> getEnabledMap() {
        return enabledMap;
    }

    /**
     * Transactional Feature has two modes, explicit Mode or implicit Mode.
     * In the implicit Mode, the Transactional Feature can be enabled at port level
     *  and be inherited or override at operation level. In the explicit Mode,
     *  transactional flow option can only specified and enabled at operation level.
     *  the default is explicit Mode.
     * @return whether this Transactional Feature is in explicit mode.
     */
    public boolean isExplicitMode() {
        return isExplicitMode;
    }

    /**
     * Change the Transactional Feature mode
     * @param explicitMode whether set to explicit Mode.
     */
    public void setExplicitMode(boolean explicitMode) {
        isExplicitMode = explicitMode;
    }
}
