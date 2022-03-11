/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.at.internal;

import com.sun.xml.ws.tx.at.WSATXAResourceTest;
import junit.framework.TestCase;

import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

/**
 * User: paulparkinson
 */
public class WSATGatewayRMTest extends TestCase {

    public WSATGatewayRMTest(String name) {
        super(name);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }


    public void testHolder() {
        ;
    }

    // todo test passes and cleans up in the passing case but may not in faling case and so reactive after impl cleanup()
    public void xtestRecoverPendingBranches() throws Exception {
        WSATGatewayRM testWSATGatewayRM = new WSATGatewayRM("unittestserver"){
  /* todo           String getTxLogDir() {
                 return ".";
             }
 */
        };
        Xid[] xids = testWSATGatewayRM.recover(XAResource.TMSTARTRSCAN);
        Xid xid = new XidImpl(1234, new byte[]{'a','b','c'}, new byte[]{'1'}); //todo reuse of xid may not be best/accurate
        BranchRecord branch = new BranchRecord(xid);
        branch.addSubordinate(xid, WSATXAResourceTest.createWSATXAResourceForXid(xid));
        assertEquals("testWSATGatewayRM.pendingXids.size()", 0, WSATGatewayRM.pendingXids.size());
        assertEquals("xids.length", 0, xids.length);
        assertFalse("branch.isLogged()", branch.isLogged());
        testWSATGatewayRM.persistBranchIfNecessary(branch);
        assertTrue("branch.isLogged()", branch.isLogged());
        xids = testWSATGatewayRM.recover(XAResource.TMSTARTRSCAN);
        assertEquals("xids.length", 2, xids.length);  //todo BUG need to get rid of the null entry
        assertEquals("xid", xids[1], xid);
        testWSATGatewayRM.rollback(xid);
    }

    /*
    public void testRegister() throws Exception {
        // need to stub  for registerResourceWithTM in create call here...
        WSATGatewayRM.setTM(new TestTransactionManager());
        WSATGatewayRM wsatGatewayRM = WSATGatewayRM.create("serverName", new TestPersistentStore());
        Xid xid =  new TestXid(true);
        String address = "testaddress";
        Node[] node0 = new Node[]{createElement("test")};
        MemberSubmissionEndpointReference epr0_0 =
                EndpointReferenceBuilder.MemberSubmission().address(address).referenceParameter(node0).build();
        WSATXAResource wsatXAResource = new WSATXAResource(epr0_0, xid);
        TransactionStub transactionStub = new TransactionStub();
        WSATGatewayRM.setTx(transactionStub);
        assertEquals("transactionStub.enlistedNamedResources.size()", transactionStub.enlistedNamedResources.size(), 0);
        assertEquals("transactionStub.enlistedResources.size()", transactionStub.enlistedResources.size(), 0);
        byte[] branchqual = wsatGatewayRM.registerWSATResource(xid, wsatXAResource);
        assertEquals("transactionStub.enlistedNamedResources.size()", transactionStub.enlistedNamedResources.size(), 1);
        assertEquals("transactionStub.enlistedResources.size()", transactionStub.enlistedResources.size(), 1);
    }
      */

}
