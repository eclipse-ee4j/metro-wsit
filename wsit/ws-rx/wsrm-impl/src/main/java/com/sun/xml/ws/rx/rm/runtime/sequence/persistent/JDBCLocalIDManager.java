/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.rm.runtime.sequence.persistent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

import com.sun.istack.logging.Logger;
import com.sun.xml.ws.rx.rm.runtime.LocalIDManager;

/**
DROP TABLE RM_LOCALIDS;

CREATE TABLE RM_LOCALIDS (
LOCAL_ID VARCHAR(512) NOT NULL,
SEQ_ID VARCHAR(256) NOT NULL,
MSG_NUMBER BIGINT NOT NULL,
CREATE_TIME BIGINT,
SEQ_TERMINATE_TIME BIGINT,
PRIMARY KEY (LOCAL_ID)
);
 */
public class JDBCLocalIDManager implements LocalIDManager {
    private final static Logger LOGGER = Logger.getLogger(JDBCLocalIDManager.class);
    private ConnectionManager cm;

    public JDBCLocalIDManager() {
        this(new DefaultDataSourceProvider());
    }

    public JDBCLocalIDManager(DataSourceProvider dataSourceProvider) {
        super();
        this.cm = ConnectionManager.getInstance(dataSourceProvider);
    }

    @Override
    public void createLocalID(String localID, String sequenceID, long messageNumber) {
        Connection con = cm.getConnection();
        PreparedStatement ps = null;
        try {
            ps = cm.prepareStatement(con,
                    "INSERT INTO RM_LOCALIDS (LOCAL_ID, SEQ_ID, MSG_NUMBER, CREATE_TIME) VALUES (?, ?, ?, ?)", true);

            ps.setString(1, localID);
            ps.setString(2, sequenceID);
            ps.setLong(3, messageNumber);
            ps.setLong(4, System.currentTimeMillis());

            int rowCount = ps.executeUpdate();
            if (rowCount != 1) {
                cm.rollback(con);

                throw LOGGER.logSevereException(new PersistenceException(
                        "Inserting LocalID failed."));
            }

            cm.commit(con);
        } catch (final Throwable ex) {
            cm.rollback(con);
            throw LOGGER.logSevereException(new PersistenceException(
                    "Inserting LocalID failed: An unexpected JDBC exception occured", ex));
        } finally {
            cm.recycle(ps);
            cm.recycle(con);
        }
    }

    @Override
    public void removeLocalIDs(Iterator<String> localIDs) {
        if (localIDs != null) {
            if (localIDs.hasNext()) {
                StringBuilder ids = new StringBuilder();
                while (localIDs.hasNext()) {
                    ids.append('\'');
                    ids.append(localIDs.next());
                    ids.append('\'');
                    if (localIDs.hasNext()) {
                        ids.append(',');
                    }
                }
                doRemove(ids.toString());
            }
        }
    }
    private void doRemove(String ids) {
        Connection con = cm.getConnection();
        PreparedStatement ps = null;
        try {
            ps = cm.prepareStatement(con, "DELETE FROM RM_LOCALIDS WHERE LOCAL_ID IN (" + ids + ")", true);

            ps.executeUpdate();

            cm.commit(con);
        } catch (final Throwable ex) {
            cm.rollback(con);
            throw LOGGER.logSevereException(new PersistenceException(
                    "Removing LocalID failed: An unexpected JDBC exception occured", ex));
        } finally {
            cm.recycle(ps);
            cm.recycle(con);
        }
    }

    @Override
    public BoundMessage getBoundMessage(String localID) {
        BoundMessage result = null;
        Connection con = cm.getConnection();
        PreparedStatement ps = null;
        try {
            ps = cm.prepareStatement(con, "SELECT SEQ_ID, MSG_NUMBER, CREATE_TIME, SEQ_TERMINATE_TIME FROM RM_LOCALIDS WHERE LOCAL_ID=?", false);

            ps.setString(1, localID);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                result = new BoundMessage(rs.getString("SEQ_ID"),
                        rs.getLong("MSG_NUMBER"),
                        rs.getLong("CREATE_TIME"),
                        rs.getLong("SEQ_TERMINATE_TIME"));
            }

            cm.commit(con);
        } catch (final SQLException ex) {
            throw LOGGER.logSevereException(new PersistenceException(
                    "Retrieving LocalID failed: An unexpected JDBC exception occured", ex));
        } finally {
            cm.recycle(ps);
            cm.recycle(con);
        }
        return result;
    }

    @Override
    public void markSequenceTermination(String sequenceID) {
        Connection con = cm.getConnection();
        PreparedStatement ps = null;
        try {
            ps = cm.prepareStatement(con,
                    "UPDATE RM_LOCALIDS SET SEQ_TERMINATE_TIME=? WHERE SEQ_ID=?", true);

            ps.setLong(1, System.currentTimeMillis());
            ps.setString(2, sequenceID);

            ps.executeUpdate();

            cm.commit(con);
        } catch (final Throwable ex) {
            cm.rollback(con);
            LOGGER.warning("Failed to mark sequence termination in RM_LOCALIDS table due to error: "
                    + ex.getMessage());
        } finally {
            cm.recycle(ps);
            cm.recycle(con);
        }
    }
}
