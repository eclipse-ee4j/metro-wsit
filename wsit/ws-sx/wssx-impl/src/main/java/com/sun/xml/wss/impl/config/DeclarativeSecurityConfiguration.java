/*
 * Copyright (c) 2010, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * $Id: DeclarativeSecurityConfiguration.java,v 1.2 2010-10-21 15:37:25 snajper Exp $
 */

package com.sun.xml.wss.impl.config;

import com.sun.xml.wss.impl.policy.SecurityPolicy;
import com.sun.xml.wss.impl.PolicyTypeUtil;
import com.sun.xml.wss.impl.policy.mls.MessagePolicy;

/**
 * Represents an XWS-Security configuration object, corresponding to the
 * <code>xwss:SecurityConfiguration</code> element (as defined in XWS-Security,
 * configuration schema, xwssconfig.xsd).
 */

public class DeclarativeSecurityConfiguration implements SecurityPolicy {
    private MessagePolicy senderSettings   = new MessagePolicy();
    private MessagePolicy receiverSettings = new MessagePolicy();

    private boolean retainSecHeader = false;
    private boolean resetMU = false;

    /*
     *@param doDumpMessages set it to true to enable dumping of messages
     */
    public void setDumpMessages(boolean doDumpMessages) {
        senderSettings.dumpMessages(doDumpMessages);
        receiverSettings.dumpMessages(doDumpMessages);
    }

    /*
     *@param flag set it to true to enable DynamicPolicyCallbacks for sender side Policies
     */
    public void enableDynamicPolicy(boolean flag) {
        senderSettings.enableDynamicPolicy(flag);
        receiverSettings.enableDynamicPolicy(flag);
    }

    /**
     *@return the <code>MessagePolicy</code> applicable for outgoing requests.
     */
    public MessagePolicy senderSettings() {
        return senderSettings;
    }

    /**
     *@return the <code>MessagePolicy</code> applicable for incoming requests.
     */
    public MessagePolicy receiverSettings() {
        return receiverSettings;
    }

    /*
     *@param bspFlag set it to true of the BSP conformance flag was specified in the configuration
     */
    public void isBSP(boolean bspFlag) {
        //senderSettings.isBSP(bspFlag);
        //enabling this to allow Backward Compatibility with XWSS11
        //Currently XWSS11 with its old xmlsec cannot handle prefixList in
        // Signature CanonicalizationMethod
        senderSettings.isBSP(bspFlag);
        receiverSettings.isBSP(bspFlag);
    }

    /*
     *@return the Retain Security Header Config Property
     */
    public boolean retainSecurityHeader() {
        return retainSecHeader;
    }

    /*
     *@param arg, set the retainSecurityHeader flag.
     */
    public void retainSecurityHeader(boolean arg) {
        this.retainSecHeader = arg;
    }

    /**
     * @return the type of the policy
     */
    @Override
    public String getType() {
        return PolicyTypeUtil.DECL_SEC_CONFIG_TYPE;
    }

    public void resetMustUnderstand(boolean value) {
        this.resetMU = value;
    }

    public boolean resetMustUnderstand() {
        return this.resetMU;
    }
}
