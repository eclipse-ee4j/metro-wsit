/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.coord.common;

import com.sun.xml.ws.developer.MemberSubmissionEndpointReference;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import jakarta.xml.ws.EndpointReference;
import jakarta.xml.ws.wsaddressing.W3CEndpointReference;
import java.lang.reflect.Field;
import java.util.List;


public abstract class EndpointReferenceHelper {


    public static EndpointReferenceHelper newInstance(EndpointReference epr){
      if(epr == null) throw new IllegalArgumentException("EndpointReference can't be null");
      if(epr instanceof MemberSubmissionEndpointReference)
        return new MemberSubmissionEndpointReferenceHelper((MemberSubmissionEndpointReference) epr);
      else if(epr instanceof W3CEndpointReference)
        return new W3CEndpointReferenceHelper((W3CEndpointReference) epr);
      else throw new IllegalArgumentException(epr.getClass() +"is not a supported EndpointReference");
    }


    public abstract String getAddress();

    public abstract  Node[] getReferenceParameters();

    static class MemberSubmissionEndpointReferenceHelper extends EndpointReferenceHelper {
        MemberSubmissionEndpointReference epr;

        MemberSubmissionEndpointReferenceHelper(MemberSubmissionEndpointReference epr) {
            this.epr = epr;
        }

        @Override
        public String getAddress() {
            return epr.addr.uri;
        }

        @Override
        public Node[] getReferenceParameters() {
            return epr.referenceParameters.elements.toArray(new Element[0]);
        }
    }

    static class W3CEndpointReferenceHelper extends EndpointReferenceHelper {
        private static Field address = null;
        private static Field referenceParameters = null;
        private static Class address_class = null;
        private static Class referenceParameters_class = null;
        private static Field uri = null;
        private static Field elements = null;

        static {
            try {
                address = W3CEndpointReference.class.getDeclaredField("address");
                address.setAccessible(true);
                referenceParameters = W3CEndpointReference.class.getDeclaredField("referenceParameters");
                referenceParameters.setAccessible(true);
                address_class = Class.forName("jakarta.xml.ws.wsaddressing.W3CEndpointReference$Address");
                referenceParameters_class = Class.forName("jakarta.xml.ws.wsaddressing.W3CEndpointReference$Elements");
                uri = address_class.getDeclaredField("uri");
                uri.setAccessible(true);
                elements = referenceParameters_class.getDeclaredField("elements");
                elements.setAccessible(true);
            } catch (Exception e) {
                throw new AssertionError(e);
            }

        }

        W3CEndpointReference epr;

        W3CEndpointReferenceHelper(W3CEndpointReference epr) {
            this.epr = epr;
        }

        @Override
        public String getAddress() {
            try {
                return (String) uri.get(address.get(epr));
            } catch (IllegalAccessException e) {
                throw new AssertionError(e);
            }
        }

        @Override
        @SuppressWarnings({"unchecked"})
        public Node[] getReferenceParameters() {
            try {
                return ((List<Element>) elements.get(referenceParameters.get(epr))).toArray(new Element[0]);
            } catch (IllegalAccessException e) {
                throw new AssertionError(e);
            }
        }
    }

}
