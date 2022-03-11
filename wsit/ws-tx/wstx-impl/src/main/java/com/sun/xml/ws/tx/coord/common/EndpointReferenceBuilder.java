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
import com.sun.xml.ws.api.tx.at.Transactional;

import jakarta.xml.ws.EndpointReference;
import jakarta.xml.ws.wsaddressing.W3CEndpointReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public abstract class EndpointReferenceBuilder<T extends EndpointReference> {
    protected String address;
    protected List<Element> referenceParameters = new ArrayList<>();

    public static EndpointReferenceBuilder newInstance(Transactional.Version version) {
        if(Transactional.Version.WSAT10 == version||Transactional.Version.DEFAULT == version)
          return MemberSubmission();
        else if(Transactional.Version.WSAT11 == version || Transactional.Version.WSAT12 == version) {
          return W3C();
        }else {
            throw new IllegalArgumentException(version + "is not a supported ws-at version");
        }
    }
    public  static EndpointReferenceBuilder<W3CEndpointReference> W3C() {
      return new  W3CEndpointReferenceBuilder();
    }

    public  static EndpointReferenceBuilder<MemberSubmissionEndpointReference> MemberSubmission() {
      return new  MemberSubmissionEndpointReferenceBuilder();
    }

    public EndpointReferenceBuilder<T> address(String address){
        this.address = address;
        return this;
    }

    public EndpointReferenceBuilder<T> referenceParameter(Element... elements){
        referenceParameters.addAll(Arrays.asList(elements));
        return this;
    }

    public EndpointReferenceBuilder<T> referenceParameter(Node... elements){
        for (Node element : elements) {
            referenceParameters.add((Element) element);
        }
        return this;
    }


    public EndpointReferenceBuilder<T> referenceParameter(List<Element> elements){
        this.referenceParameters.addAll(elements);
        return this;
    }

    public abstract T build();

    static class W3CEndpointReferenceBuilder extends EndpointReferenceBuilder<W3CEndpointReference>{

        @Override
        public W3CEndpointReference build() {
            jakarta.xml.ws.wsaddressing.W3CEndpointReferenceBuilder builder = new jakarta.xml.ws.wsaddressing.W3CEndpointReferenceBuilder();
            for (int i = 0; i < referenceParameters.size(); i++) {
                Element element =  referenceParameters.get(i);
                builder.referenceParameter(element);
            }
            return builder.address(address).build();
        }
    }

    static class MemberSubmissionEndpointReferenceBuilder extends EndpointReferenceBuilder<MemberSubmissionEndpointReference>{

        @Override
        public MemberSubmissionEndpointReference build() {
            MemberSubmissionEndpointReference epr = new MemberSubmissionEndpointReference();
            epr.addr = new MemberSubmissionEndpointReference.Address();
            epr.addr.uri = address;
            epr.referenceParameters = new MemberSubmissionEndpointReference.Elements();
            epr.referenceParameters.elements = new ArrayList<>();
            epr.referenceParameters.elements.addAll(referenceParameters);
            return epr;
        }
    }

}
