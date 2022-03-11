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

/**
 * A local string manager.
 * This interface describes the access to i18n messages for classes that need
 * them.
 */

public interface LocalStringManager {

    /**
     * Get a localized string.
     * Strings are stored in a single property file per package named
     * LocalStrings[_locale].properties. Starting from the class of the
     * caller, we walk up the class hierarchy until we find a package
     * resource bundle that provides a value for the requested key.
     *
     * <p>This simplifies access to resources, at the cost of checking for
     * the resource bundle of several classes upon each call. However, due
     * to the caching performed by <code>ResourceBundle</code> this seems
     * reasonable.
     *
     * <p>Due to that, sub-classes <strong>must</strong> make sure they don't
     * have conflicting resource naming.
     * @param callerClass The object making the call, to allow per-package
     * resource bundles
     * @param key The name of the resource to fetch
     * @param defaultValue The default return value if not found
     * @return The localized value for the resource
     */
    String getLocalString(
            Class callerClass,
            String key,
            String defaultValue
    );

    /**
     * Get a local string for the caller and format the arguments accordingly.
     * @param callerClass The caller (to walk through its class hierarchy)
     * @param key The key to the local format string
     * @param defaultFormat The default format if not found in the resources
     * @param arguments The set of arguments to provide to the formatter
     * @return A formatted localized string
     */
    String getLocalString(
            Class callerClass,
            String key,
            String defaultFormat,
            Object[] arguments
    );

    String getLocalString(String key, String defaultValue);
}
