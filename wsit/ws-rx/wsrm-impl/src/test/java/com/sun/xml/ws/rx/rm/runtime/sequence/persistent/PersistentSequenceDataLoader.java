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
 */
public class PersistentSequenceDataLoader implements SequenceDataLoader {

    private class UnitTestDerbyDataSourceProvider implements DataSourceProvider {

        private final DataSource ds = new DataSource() {

            @Override
            public Connection getConnection() {
                return dbInstance.getConnection();
            }

            @Override
            public Connection getConnection(String username, String password) {
                return dbInstance.getConnection();
            }

            @Override
            public PrintWriter getLogWriter() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void setLogWriter(PrintWriter out) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void setLoginTimeout(int seconds) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public int getLoginTimeout() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public <T> T unwrap(Class<T> iface) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public boolean isWrapperFor(Class<?> iface) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public Logger getParentLogger() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };

        @Override
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

        @Override
        public long currentTimeInMillis() {
            return System.currentTimeMillis();
        }
    };
    private EmbeddedDerbyDbInstance dbInstance;

    @Override
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

    @Override
    public void tearDown() {
        if (dbInstance != null) {
            dbInstance.stop();
            dbInstance = null;
        }
    }

    @Override
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
