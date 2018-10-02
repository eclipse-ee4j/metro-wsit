/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.at.common.endpoint;

import junit.framework.TestCase;
import com.sun.xml.ws.tx.at.internal.XidStub;
import com.sun.xml.ws.tx.at.*;
import com.sun.xml.ws.tx.at.common.WSATVersion;
import com.sun.xml.ws.tx.at.common.WSATVersionStub;

import javax.transaction.xa.Xid;
import javax.xml.ws.WebServiceContext;

/**
 *
 * @author paulparkinson
 */
public class CoordinatorTest extends TestCase {

   public void testAll() throws Exception {
      WebServiceContext context = null;
      final Xid m_xid = new XidStub(true);
      final WSATXAResourceStub wsatXAResourceStub = WSATXAResourceTest.createStubWSATXAResourceForXid(m_xid);
      WSATVersion version = new WSATVersionStub();
      final WSATHelperStub m_wsatHelper = new WSATHelperStub(wsatXAResourceStub);
      Coordinator coordinator =  new EmulatedCoordinator(context, version, m_xid, wsatXAResourceStub, true);
      WSATXAResource xaRes = (WSATXAResource)m_wsatHelper.m_durableParticipantXAResourceMap.get(m_xid);
      assertNull("m_wsatHelper.m_durableParticipantXAResourceMap.get(m_xid)", xaRes);
      m_wsatHelper.m_durableParticipantXAResourceMap.put(m_xid, wsatXAResourceStub);
      xaRes = (WSATXAResource)m_wsatHelper.m_durableParticipantXAResourceMap.get(m_xid);
      assertNotNull("m_wsatHelper.m_durableParticipantXAResourceMap.get(m_xid)", xaRes);
      Object paramaters = null;
      coordinator.abortedOperation(paramaters);
      xaRes = (WSATXAResource)m_wsatHelper.m_durableParticipantXAResourceMap.get(m_xid);
      assertNotNull("m_wsatHelper.m_durableParticipantXAResourceMap.get(m_xid)", xaRes);
      assertEquals("xares returned and xaresstub state", ((WSATXAResourceStub)xaRes).m_status, WSATXAResource.ABORTED);
      coordinator.committedOperation(paramaters);
      assertEquals("xares returned and xaresstub state", ((WSATXAResourceStub)xaRes).m_status, WSATXAResource.COMMITTED);
      coordinator.preparedOperation(paramaters);
      assertEquals("xares returned and xaresstub state", ((WSATXAResourceStub)xaRes).m_status, WSATXAResource.PREPARED);
      coordinator.readOnlyOperation(paramaters);
      assertEquals("xares returned and xaresstubstate", ((WSATXAResourceStub)xaRes).m_status, WSATXAResource.READONLY);
      coordinator.replayOperation(paramaters);
   }
}

