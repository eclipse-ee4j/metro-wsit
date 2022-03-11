/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.transport.tcp.util;

/**
 * @author Alexey Stashok
 */
public class WSTCPError {
    private final int code;
    private final int subCode;
    private final String description;

    public static WSTCPError createCriticalError(final int subCode, final String description) {
        return new WSTCPError(TCPConstants.CRITICAL_ERROR, subCode, description);
    }

    public static WSTCPError createNonCriticalError(final int subCode, final String description) {
        return new WSTCPError(TCPConstants.NON_CRITICAL_ERROR, subCode, description);
    }

    public static WSTCPError createError(final int code, final int subCode, final String description) {
        return new WSTCPError(code, subCode, description);
    }

    private WSTCPError(final int code, final int subCode, final String description) {
        this.code = code;
        this.subCode = subCode;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public int getSubCode() {
        return subCode;
    }

    public String getDescription() {
        return description;
    }

    public boolean isCritical() {
        return code == TCPConstants.CRITICAL_ERROR;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb = sb.append("Code: ")
                .append(code)
                .append(" SubCode: ")
                .append(subCode)
                .append(" Description: ")
                .append(description);
        return sb.toString();
    }
}
