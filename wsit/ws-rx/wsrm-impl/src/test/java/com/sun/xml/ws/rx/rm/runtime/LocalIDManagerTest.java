/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.rm.runtime;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.sql.DataSource;

import junit.framework.TestCase;

import com.sun.xml.ws.rx.rm.runtime.LocalIDManager;
import com.sun.xml.ws.rx.rm.runtime.LocalIDManager.BoundMessage;
import com.sun.xml.ws.rx.rm.runtime.sequence.invm.InMemoryLocalIDManager;
import com.sun.xml.ws.rx.rm.runtime.sequence.persistent.DataSourceProvider;
import com.sun.xml.ws.rx.rm.runtime.sequence.persistent.EmbeddedDerbyDbInstance;
import com.sun.xml.ws.rx.rm.runtime.sequence.persistent.JDBCLocalIDManager;
import com.sun.xml.ws.rx.rm.runtime.sequence.persistent.PersistenceException;

public class LocalIDManagerTest extends TestCase {

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

    private EmbeddedDerbyDbInstance dbInstance;

    @Override
    public void setUp() {
        tearDown();

        dbInstance = EmbeddedDerbyDbInstance.start("PersistentRmJunitTestDb");

        if (dbInstance.tableExists("RM_LOCALIDS")) {
            dbInstance.execute("DROP TABLE RM_LOCALIDS");
        }
        
        dbInstance.execute(
                "CREATE TABLE RM_LOCALIDS (LOCAL_ID VARCHAR(512) NOT NULL,"+
                "SEQ_ID VARCHAR(256) NOT NULL, MSG_NUMBER BIGINT NOT NULL,"+
                "CREATE_TIME BIGINT, SEQ_TERMINATE_TIME BIGINT, PRIMARY KEY (LOCAL_ID))");
    }

    @Override
    public void tearDown() {
        if (dbInstance != null) {
            dbInstance.stop();
            dbInstance = null;
        }
    }
    
    public void testJDBCLocalIDManager() throws Exception {
        runTest(new JDBCLocalIDManager(new UnitTestDerbyDataSourceProvider()));
    }
    
    public void testInMemoryLocalIDManager() throws Exception {
        runTest(InMemoryLocalIDManager.getInstance());
    }
    
    private void runTest(LocalIDManager mgr) {
        // test createLocalID
        mgr.createLocalID("localid1", "seq1", 1);
        validateLocalID(mgr.getBoundMessage("localid1"), "seq1", 1);

        mgr.createLocalID("localid2", "seq2", 2);
        mgr.createLocalID("localid3", "seq3", 3);
        validateLocalID(mgr.getBoundMessage("localid1"), "seq1", 1);
        validateLocalID(mgr.getBoundMessage("localid2"), "seq2", 2);
        validateLocalID(mgr.getBoundMessage("localid3"), "seq3", 3);
        
        // test removeLocalIDs
        List<String> toRemove = new ArrayList<>();
        mgr.removeLocalIDs(toRemove.iterator());
        validateLocalID(mgr.getBoundMessage("localid1"), "seq1", 1);
        validateLocalID(mgr.getBoundMessage("localid2"), "seq2", 2);
        validateLocalID(mgr.getBoundMessage("localid3"), "seq3", 3);

        toRemove.add("localid1");
        toRemove.add("localid2");
        mgr.removeLocalIDs(toRemove.iterator());
        assertNull(mgr.getBoundMessage("localid1"));
        assertNull(mgr.getBoundMessage("localid2"));
        validateLocalID(mgr.getBoundMessage("localid3"), "seq3", 3);
        
        toRemove = new ArrayList<>();
        toRemove.add("localid3");
        mgr.removeLocalIDs(toRemove.iterator());
        assertNull(mgr.getBoundMessage("localid1"));
        assertNull(mgr.getBoundMessage("localid2"));
        assertNull(mgr.getBoundMessage("localid3"));
        
        toRemove = new ArrayList<>();
        toRemove.add("localid4");
        mgr.removeLocalIDs(toRemove.iterator());
        assertNull(mgr.getBoundMessage("localid1"));
        assertNull(mgr.getBoundMessage("localid2"));
        assertNull(mgr.getBoundMessage("localid3"));
        
        // test markSequenceRemoval
        mgr.createLocalID("localida", "testSequence", 1);
        mgr.createLocalID("localidb", "testSequence", 2);
        validateLocalID(mgr.getBoundMessage("localida"), "testSequence", 1);
        validateLocalID(mgr.getBoundMessage("localidb"), "testSequence", 2);
        mgr.markSequenceTermination("testSequence");
        validateLocalID(mgr.getBoundMessage("localida"), "testSequence", 1, true);
        validateLocalID(mgr.getBoundMessage("localidb"), "testSequence", 2, true);
    }
    
    private void validateLocalID(BoundMessage boundMessage, 
            String expectedSequenceID, 
            long expectedMessageNumber) {
        validateLocalID(boundMessage, expectedSequenceID, expectedMessageNumber, false);
    }
    
    private void validateLocalID(BoundMessage boundMessage, 
            String expectedSequenceID, 
            long expectedMessageNumber,
            boolean terminated) {
        System.out.println(boundMessage);
        assertEquals(expectedSequenceID, boundMessage.sequenceID);
        assertEquals(expectedMessageNumber, boundMessage.messageNumber);
        assertTrue(boundMessage.createTime > 0);
        assertTrue(boundMessage.createTime <= System.currentTimeMillis());
        if (terminated) {
            assertTrue(boundMessage.seqTerminateTime > 0);
            assertTrue(boundMessage.seqTerminateTime <= System.currentTimeMillis());
        } else {
            assertEquals(0, boundMessage.seqTerminateTime);
        }
    }

}
