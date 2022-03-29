/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.coord.common;

import org.w3c.dom.Element;
import com.sun.xml.ws.util.DOMUtil;
import com.sun.xml.ws.tx.at.WSATConstants;
import com.sun.xml.ws.tx.at.WSATHelper;


public final class WSCUtil {

    private WSCUtil() {}

    public static Element referenceElementTxId(String txId) {
        Element ele = DOMUtil.createDom().createElementNS(WSATConstants.WLA_WSAT_NS_URI,WSATConstants.WSAT_WSAT +":"+WSATConstants.TXID);
        ele.setTextContent(txId);
        return ele;
    }

    public static Element referenceElementBranchQual(String branchQual) {
        Element ele =
                DOMUtil.createDom().createElementNS(
                        WSATConstants.WLA_WSAT_NS_URI,WSATConstants.WSAT_WSAT +":"+WSATConstants.BRANCHQUAL);
        branchQual = branchQual.replaceAll(",", "&#044;");
        ele.setTextContent(branchQual.trim());
        return ele;
    }

    public static Element referenceElementRoutingInfo() {
        String routingInfo = WSATHelper.getInstance().getRoutingAddress();
        Element ele = DOMUtil.createDom().createElementNS(WSATConstants.WLA_WSAT_NS_URI,WSATConstants.WSAT_WSAT +":"+WSATConstants.ROUTING);
        ele.setTextContent(routingInfo);
        return ele;
    }
}
