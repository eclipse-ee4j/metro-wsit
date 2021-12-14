/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.at;

import junit.framework.TestCase;
import org.w3c.dom.*;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import com.sun.xml.ws.tx.at.internal.XidStub;
import com.sun.xml.ws.developer.MemberSubmissionEndpointReference;
import com.sun.xml.ws.tx.coord.common.EndpointReferenceBuilder;
import com.sun.xml.ws.util.DOMUtil;

/**
 *
 * @author paulparkinson
 */
public class WSATXAResourceTest extends TestCase {

    public void testEquality() {
        Node[] node0 = new Node[]{createElement("testsame")};
        Node[] node1 = new Node[]{createElement("testsame")};
        Node[] node2 = new Node[]{createElement("test2 is different from test3")};
        Node[] node3 = new Node[]{createElement("test3")};
        Xid xid0 = new XidStub(true);
        Xid xid1 = new XidStub(true);
        Xid xid2 = new XidStub(false);
        Xid xid3 = new XidStub(false);
        String address0 = "testaddress";
        String address1 = "testaddress1";
        MemberSubmissionEndpointReference epr0_0 = EndpointReferenceBuilder.MemberSubmission().address(address0).referenceParameter(node0).build();
        MemberSubmissionEndpointReference epr0_1 = EndpointReferenceBuilder.MemberSubmission().address(address0).referenceParameter(node1).build();
        MemberSubmissionEndpointReference epr0_2 = EndpointReferenceBuilder.MemberSubmission().address(address0).referenceParameter(node2).build();
        MemberSubmissionEndpointReference epr0_3= EndpointReferenceBuilder.MemberSubmission().address(address0).referenceParameter(node3).build();
        MemberSubmissionEndpointReference epr1_1 = EndpointReferenceBuilder.MemberSubmission().address(address1).referenceParameter(node1).build();
        //test equal
        WSATXAResource testWSATXAResource = new WSATXAResource(epr0_0, xid0);
        WSATXAResource testWSATXAResource2 = new WSATXAResource(epr0_1, xid1);
        assertTrue("WSATResources", testWSATXAResource.equals(testWSATXAResource2));
        //test address not equal
        testWSATXAResource2 = new WSATXAResource(epr1_1, xid1);
        assertFalse("WSATResources", testWSATXAResource.equals(testWSATXAResource2));
        //test equal again
        testWSATXAResource2 = new WSATXAResource(epr0_1, xid1);
        assertTrue("WSATResources", testWSATXAResource.equals(testWSATXAResource2));
        //test xid not equal
        testWSATXAResource = new WSATXAResource(epr0_0, xid2);
        testWSATXAResource2 = new WSATXAResource(epr0_1, xid3);
        assertFalse("WSATResources", testWSATXAResource.equals(testWSATXAResource2));
        //test equal again
        testWSATXAResource2 = new WSATXAResource(epr0_1, xid1);
        assertTrue("WSATResources", testWSATXAResource.equals(testWSATXAResource2));
        //test node/ref-param not equal
        testWSATXAResource = new WSATXAResource(epr0_2, xid0);
        testWSATXAResource2 = new WSATXAResource(epr0_3, xid1);
        assertFalse("WSATResources", testWSATXAResource.equals(testWSATXAResource2));
    }

    public void testPrepare() throws Exception {
        Node[] node0 = new Node[]{createElement("test")};
        Xid xid0 = new XidStub(true);
        String address0 = "testaddress";
        MemberSubmissionEndpointReference epr0_0 = EndpointReferenceBuilder.MemberSubmission().address(address0).referenceParameter(node0).build();
        WSATXAResource testWSATXAResource = new WSATXAResource(epr0_0, xid0) {
            @Override
            WSATHelper getWSATHelper() {
                return new WSATHelperStub();
            }

            @Override
            int getWaitForReplyTimeout() {
                return 1;
            }
        };
        //first test scenario where reply is received before wait...
        testWSATXAResource.setStatus(WSATConstants.READONLY);
        assertEquals("prepare return", XAResource.XA_RDONLY, testWSATXAResource.prepare(xid0));
        testWSATXAResource.setStatus(WSATConstants.PREPARED);
        assertEquals("prepare return", XAResource.XA_OK, testWSATXAResource.prepare(xid0));
        testWSATXAResource.setStatus(WSATConstants.ABORTED);
        try {
            testWSATXAResource.prepare(xid0);
            fail("should have thrown xaex due to aborted status");
        } catch (XAException xaex) {
            assertEquals("xaerrorcode from aborted vote", XAException.XA_RBROLLBACK, xaex.errorCode);
        }
        testWSATXAResource.setStatus(WSATXAResource.ACTIVE);
        try {
            testWSATXAResource.prepare(xid0);
            fail("should have thrown xaex due to unknown status");
        } catch (XAException xaex) {
            assertEquals("xaerrorcode from unknown response", XAException.XAER_RMFAIL, xaex.errorCode);
        }
    }

    public void testRollback() throws Exception {
        Node[] node0 = new Node[]{createElement("test")};
        Xid xid0 = new XidStub(true);
        String address0 = "testaddress";
        MemberSubmissionEndpointReference epr0_0 = EndpointReferenceBuilder.MemberSubmission().address(address0).referenceParameter(node0).build();
        WSATXAResource testWSATXAResource = new WSATXAResource(epr0_0, xid0) {
            @Override
            WSATHelper getWSATHelper() {
                return new WSATHelperStub();
            }

            @Override
            int getWaitForReplyTimeout() {
                return 1;
            }
        };
        //first test scenario where reply is received before wait...
        testWSATXAResource.setStatus(WSATConstants.ABORTED);
        testWSATXAResource.rollback(xid0);
        testWSATXAResource.setStatus(WSATConstants.PREPARED);
        try {
            testWSATXAResource.rollback(xid0);
            fail("should have thrown xaex due to prepared status");
        } catch (XAException xaex) {
            assertEquals("xaerrorcode from aborted vote", XAException.XAER_RMFAIL, xaex.errorCode);
        }
        testWSATXAResource.setStatus(WSATXAResource.ACTIVE);
        try {
            testWSATXAResource.rollback(xid0);
            fail("should have thrown xaex due to unknown status");
        } catch (XAException xaex) {
            assertEquals("xaerrorcode from unknown response", XAException.XAER_RMFAIL, xaex.errorCode);
        }
        testWSATXAResource.setStatus(WSATXAResource.COMMITTED);
        try {
            testWSATXAResource.rollback(xid0);
            fail("should have thrown xaex due to committed status");
        } catch (XAException xaex) {
            assertEquals("xaerrorcode from unknown response", XAException.XAER_RMFAIL, xaex.errorCode); //todo revisit
        }
    }


    public void testCommit() throws Exception {
        Node[] node0 = new Node[]{createElement("test")};
        Xid xid0 = new XidStub(true);
        String address0 = "testaddress";
        MemberSubmissionEndpointReference epr0_0 = EndpointReferenceBuilder.MemberSubmission().address(address0).referenceParameter(node0).build();
        WSATXAResource wsatXAResourceTest = new WSATXAResource(epr0_0, xid0) {
            @Override
            WSATHelper getWSATHelper() {
                return new WSATHelperStub();
            }

            @Override
            int getWaitForReplyTimeout() {
                return 1;
            }
        };
        //first test scenario where reply is received before wait...
        wsatXAResourceTest.setStatus(WSATConstants.COMMITTED);
        wsatXAResourceTest.commit(xid0, false);
        wsatXAResourceTest.setStatus(WSATConstants.PREPARED);
        try {
            wsatXAResourceTest.commit(xid0, false);
            fail("should have thrown xaex due to prepared status");
        } catch (XAException xaex) {
            assertEquals("xaerrorcode from aborted vote", XAException.XAER_RMFAIL, xaex.errorCode);
        }
        wsatXAResourceTest.setStatus(WSATXAResource.ACTIVE);
        try {
            wsatXAResourceTest.commit(xid0, false);
            fail("should have thrown xaex due to unknown status");
        } catch (XAException xaex) {
            assertEquals("xaerrorcode from unknown response", XAException.XAER_PROTO, xaex.errorCode);
        }
        wsatXAResourceTest.setStatus(WSATXAResource.ABORTED);
        try {
            wsatXAResourceTest.commit(xid0, false);
            fail("should have thrown xaex due to committed status");
        } catch (XAException xaex) {
            assertEquals("xaerrorcode from unknown response", XAException.XAER_PROTO, xaex.errorCode); //todo revisit
        }
    }


    public static WSATXAResource createWSATXAResourceForXid(Xid xid) {
            return createWSATXAResourceForXid(xid, true);
    }

    public static WSATXAResourceStub createStubWSATXAResourceForXid(Xid xid) {
            return (WSATXAResourceStub)createWSATXAResourceForXid(xid, false);
    }

    private static Element createElement(String text) {
        Element element = DOMUtil.createDom().createElement("txID");
        element.setTextContent(text);
        return element;
    }

    /**
     *
     * @param b actual impl/true or stub/false
     */
    public static WSATXAResource createWSATXAResourceForXid(Xid xid, boolean b) {
        String address = "testaddress";
        Node[] node0 = new Node[]{createElement("test")};
        MemberSubmissionEndpointReference epr0_0 =
                EndpointReferenceBuilder.MemberSubmission().address(address).referenceParameter(node0).build();
        WSATXAResource wsatXAResource = b?new WSATXAResource(epr0_0, xid):new WSATXAResourceStub(epr0_0, xid);
        return wsatXAResource;
    }
}

