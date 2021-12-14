/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.rm.runtime.sequence.persistent;

import com.sun.istack.logging.Logger;
import com.sun.xml.ws.rx.rm.localization.LocalizationMessages;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 *
 */
public class DefaultDataSourceProvider implements DataSourceProvider {
    /**
     * JNDI name of the JDBC pool to be used for persisting RM data
     */
    private static final String RM_JDBC_POOL_NAME = "jdbc/ReliableMessagingPool";
    /**
     * Logger instance
     */
    private static final Logger LOGGER = Logger.getLogger(DefaultDataSourceProvider.class);

    private static synchronized DataSource getDataSource(String jndiName) throws PersistenceException {
        try {
            javax.naming.InitialContext ic = new javax.naming.InitialContext();
            Object __ds = ic.lookup(jndiName);
            DataSource ds;
            if (__ds instanceof DataSource) {
                ds = (DataSource) __ds;
            } else {
                throw new PersistenceException(LocalizationMessages.WSRM_1154_UNEXPECTED_CLASS_OF_JNDI_BOUND_OBJECT(
                        __ds.getClass().getName(),
                        jndiName,
                        DataSource.class.getName()));
            }

            return ds;
        } catch (NamingException ex) {
            throw LOGGER.logSevereException(new PersistenceException(LocalizationMessages.WSRM_1155_RM_JDBC_CONNECTION_POOL_NOT_FOUND(), ex));
        }
    }
    //
    private final DataSource ds;

    public DefaultDataSourceProvider() {
        this.ds = getDataSource(RM_JDBC_POOL_NAME);
    }

    @Override
    public DataSource getDataSource() {
        return ds;
    }

}
