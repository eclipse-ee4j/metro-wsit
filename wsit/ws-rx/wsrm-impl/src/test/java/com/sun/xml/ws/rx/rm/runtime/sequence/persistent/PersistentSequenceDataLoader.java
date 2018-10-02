/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.rm.runtime.sequence.persistent;

import com.sun.xml.ws.rx.rm.runtime.sequence.Sequence.State;
import com.sun.xml.ws.rx.rm.runtime.sequence.SequenceData;
import com.sun.xml.ws.rx.rm.runtime.sequence.SequenceDataLoader;
import com.sun.xml.ws.rx.util.TimeSynchronizer;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;
import javax.sql.DataSource;

/**
 *
 * @author Marek Potociar <marek.potociar at sun.com>
 */
public class PersistentSequenceDataLoader implements SequenceDataLoader {

    private class UnitTestDerbyDataSourceProvider implements DataSourceProvider {

        private final DataSource ds = new DataSource() {

            public Connection getConnection() throws SQLException {
                return dbInstance.getConnection();
            }

            public Connection getConnection(String username, String password) throws SQLException {
                return dbInstance.getConnection();
            }

            public PrintWriter getLogWriter() throws SQLException {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            public void setLogWriter(PrintWriter out) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            public void setLoginTimeout(int seconds) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            public int getLoginTimeout() throws SQLException {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            public <T> T unwrap(Class<T> iface) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            public boolean isWrapperFor(Class<?> iface) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            public Logger getParentLogger() throws SQLFeatureNotSupportedException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };

        public DataSource getDataSource() throws PersistenceException {
            return ds;
        }
    }
    //
    private static final String TEST_ENDPOINT_UID = "test_endpoint_001";
    private static final PersistentSequenceData.SequenceType TEST_SEQUENCE_TYPE = PersistentSequenceData.SequenceType.Inbound;
    //
    private final ConnectionManager cm = ConnectionManager.getInstance(new UnitTestDerbyDataSourceProvider());
    private final TimeSynchronizer ts = new TimeSynchronizer() {

        public long currentTimeInMillis() {
            return System.currentTimeMillis();
        }
    };
    private EmbeddedDerbyDbInstance dbInstance;

    public void setUp() {
        tearDown();

        dbInstance = EmbeddedDerbyDbInstance.start("PersistentRmJunitTestDb");

        if (dbInstance.tableExists("RM_UNACKED_MESSAGES")) {
            dbInstance.execute("DROP TABLE RM_UNACKED_MESSAGES");
        }
        if (dbInstance.tableExists("RM_SEQUENCES")) {
            dbInstance.execute("DROP TABLE RM_SEQUENCES");
        }
        
        dbInstance.execute(
                "CREATE TABLE RM_SEQUENCES ( " +
                "ENDPOINT_UID VARCHAR(512) NOT NULL, " +
                "ID VARCHAR(256) NOT NULL, " +
                "TYPE CHARACTER NOT NULL, " +
                "EXP_TIME BIGINT NOT NULL, " +
                "BOUND_ID VARCHAR(256), " +
                "STR_ID VARCHAR(256), " +
                "STATUS SMALLINT NOT NULL, " +
                "ACK_REQUESTED_FLAG CHARACTER, " +
                "LAST_MESSAGE_NUMBER BIGINT NOT NULL, " +
                "LAST_ACTIVITY_TIME BIGINT NOT NULL, " +
                "LAST_ACK_REQUEST_TIME BIGINT NOT NULL, " +
                "PRIMARY KEY (ENDPOINT_UID, ID)" +
                ")");
        dbInstance.execute(
                "CREATE INDEX IDX_RM_SEQUENCES_BOUND_ID ON RM_SEQUENCES (BOUND_ID)");

        dbInstance.execute(
                "CREATE TABLE RM_UNACKED_MESSAGES ( " +
                "ENDPOINT_UID VARCHAR(512) NOT NULL, " +
                "SEQ_ID VARCHAR(256) NOT NULL, " +
                "MSG_NUMBER BIGINT NOT NULL, " +
                "IS_RECEIVED CHARACTER NOT NULL, " +
                "CORRELATION_ID VARCHAR(256), " +
                "NEXT_RESEND_COUNT INT, " +
                "WSA_ACTION VARCHAR(256), " +
                "MSG_DATA BLOB, " +
                "PRIMARY KEY (ENDPOINT_UID, SEQ_ID, MSG_NUMBER)" +
                ")");

        dbInstance.execute(
                "ALTER TABLE RM_UNACKED_MESSAGES " +
                "ADD CONSTRAINT FK_SEQUENCE " +
                "FOREIGN KEY (ENDPOINT_UID, SEQ_ID) REFERENCES RM_SEQUENCES(ENDPOINT_UID, ID)");
        dbInstance.execute(
                "CREATE INDEX IDX_RM_UNACKED_MESSAGES_CORRELATION_ID ON RM_UNACKED_MESSAGES (CORRELATION_ID)");
    }

    public void tearDown() {
        if (dbInstance != null) {
            dbInstance.stop();
            dbInstance = null;
        }
    }

    public SequenceData newInstance(boolean isInbound, String sequenceId, String securityContextTokenId, long expirationTime, State state, boolean ackRequestedFlag, long lastMessageId, long lastActivityTime, long lastAcknowledgementRequestTime) {
        return PersistentSequenceData.newInstance(
                ts,
                cm,
                TEST_ENDPOINT_UID,
                sequenceId,
                TEST_SEQUENCE_TYPE,
                securityContextTokenId,
                expirationTime,
                state,
                ackRequestedFlag,
                lastMessageId,
                lastActivityTime,
                lastAcknowledgementRequestTime);
    }
}
