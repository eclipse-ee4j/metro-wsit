/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.at.common.endpoint;

import junit.framework.TestCase;
import com.sun.xml.ws.tx.at.runtime.TransactionServices;
import com.sun.xml.ws.tx.at.WSATConstants;
import com.sun.xml.ws.tx.at.common.CoordinatorIF;
import com.sun.xml.ws.tx.at.common.WSATVersion;
import com.sun.xml.ws.tx.at.common.WSATVersionStub;

import jakarta.xml.ws.WebServiceContext;

/**
 *
 * @author paulparkinson
 */
public class ParticipantTest extends TestCase {

   public void testRollback() {
      WebServiceContext context = null;
      WSATVersion version = new WSATVersionStub();
      EmulatedCoordinator testCoordinator = EmulatedCoordinator.createDefault();
       EmulatedTransactionServices testTransactionServices = new EmulatedTransactionServices();
      byte[] testTid = new byte[]{'a'};
      Participant participant =
              new TestParticipant(context, version, testCoordinator, testTransactionServices, testTid);
      assertEquals("abortedOperationCount before rollback call", 0, testCoordinator.abortedOperationCount);
      participant.rollback(null);
      assertEquals("abortedOperationCount after rollback call", 1, testCoordinator.abortedOperationCount);
   }

   public void testPreparedVote() {
      EmulatedCoordinator testCoordinator = EmulatedCoordinator.createDefault();
      EmulatedTransactionServices testTransactionServices = new EmulatedTransactionServices();
      Participant participant = createTestParticipant(testCoordinator, testTransactionServices);
      assertEquals("preparedOperationCount before prepare call", 0, testCoordinator.preparedOperationCount);
      participant.prepare(null);
      assertEquals("preparedOperationCount after prepare call", 1, testCoordinator.preparedOperationCount);
   }

   public void testReadOnlyVote() {
      EmulatedCoordinator testCoordinator = EmulatedCoordinator.createDefault();
      EmulatedTransactionServices testTransactionServices = new EmulatedTransactionServices();
      testTransactionServices.setPrepareVoteReturn(WSATConstants.READONLY);
      Participant participant = createTestParticipant(testCoordinator, testTransactionServices);
      assertEquals("preparedOperationCount before prepare call", 0, testCoordinator.readOnlyOperationCount);
      participant.prepare(null);
      assertEquals("preparedOperationCount after prepare call", 1, testCoordinator.readOnlyOperationCount);
   }

    private Participant createTestParticipant(EmulatedCoordinator testCoordinator, EmulatedTransactionServices testTransactionServices) {
        WebServiceContext context = null;
        WSATVersion version = new WSATVersionStub();
        byte[] testTid = new byte[]{'a'};
        Participant participant =
                new TestParticipant(context, version, testCoordinator, testTransactionServices, testTid);
        return participant;
    }

    public void testPrepareException() {
      WebServiceContext context = null;
      WSATVersion version = new WSATVersionStub();
      EmulatedCoordinator testCoordinator = EmulatedCoordinator.createDefault();
      EmulatedTransactionServices testTransactionServices = new EmulatedTransactionServices();
      testTransactionServices.m_isPrepareException = true;
      byte[] testTid = new byte[]{'a'};
      Participant participant =
              new TestParticipant(context, version, testCoordinator, testTransactionServices, testTid);
      assertEquals("preparedOperationCount before prepare call", 0, testCoordinator.preparedOperationCount);
      assertEquals("abortedOperationCount before prepare call", 0, testCoordinator.abortedOperationCount);
      participant.prepare(null);
      assertEquals("preparedOperationCount after prepare exception call", 0, testCoordinator.preparedOperationCount);
      assertEquals("abortedOperationCount after prepare exceptioncall", 1, testCoordinator.abortedOperationCount);
   }

   public void testCommit() {
      WebServiceContext context = null;
      WSATVersion version = new WSATVersionStub();
      EmulatedCoordinator testCoordinator = EmulatedCoordinator.createDefault();
      EmulatedTransactionServices testTransactionServices = new EmulatedTransactionServices();
      byte[] testTid = new byte[]{'a'};
      Participant participant =
              new TestParticipant(context, version, testCoordinator, testTransactionServices, testTid);
      assertEquals("committedOperationCount before commit call", 0, testCoordinator.committedOperationCount);
      participant.commit(null);
      assertEquals("committedOperationCount after commit call", 1, testCoordinator.committedOperationCount);
   }

   public void testCommitException() {   //failure
      WebServiceContext context = null;
      WSATVersion version = new WSATVersionStub();
      EmulatedCoordinator testCoordinator = EmulatedCoordinator.createDefault();
      EmulatedTransactionServices testTransactionServices = new EmulatedTransactionServices();
      testTransactionServices.m_isCommitException = true;
      byte[] testTid = new byte[]{'a'};
      Participant participant =
              new TestParticipant(context, version, testCoordinator, testTransactionServices, testTid);
      assertEquals("committedOperationCount before commit call", 0, testCoordinator.committedOperationCount);
      participant.commit(null);
      assertEquals("committedOperationCount after commit exception call", 1, testCoordinator.committedOperationCount); //todo change as we interpret exception beyond nota
   }

    static class TestParticipant extends Participant {
      CoordinatorIF m_coordinatorIF;
      TransactionServices m_transactionServices;
      byte[] m_tid;

       @Override
       boolean isInForeignContextMap() {
           return true;
       }

       public TestParticipant(WebServiceContext context, WSATVersion version,
                             CoordinatorIF coordinatorIF, TransactionServices transactionServices, byte[] tid) {
         super(context, version);
         m_coordinatorIF = coordinatorIF;
         m_transactionServices = transactionServices;
         m_tid = tid;
      }

      @Override
      TransactionServices getTransactionaService() {
         return m_transactionServices;
      }

      @Override
      byte[] getWSATTid() {
         return m_tid;
      }

      @Override
      CoordinatorIF getCoordinatorPortType() {
         return m_coordinatorIF;
      }

       @Override
       CoordinatorIF getCoordinatorPortTypeForReplyTo() {
         return m_coordinatorIF;
       }

   }


}

