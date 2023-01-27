/*
 * Copyright (c) 2021, 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */
@SuppressWarnings({"requires-transitive-automatic"}) //santuario 2.3
module org.glassfish.metro.wsit.impl {

    requires transitive java.security.jgss;
    requires java.sql;
    requires java.rmi;
    requires transitive java.xml.crypto;
    requires transitive jakarta.security.auth.message;
    requires static jakarta.transaction;
    requires static jakarta.resource;
    requires static jakarta.cdi;
    requires jdk.security.auth;
    requires transitive org.apache.santuario.xmlsec;

    requires transitive org.glassfish.metro.wsit.api;
    requires com.sun.xml.ws.fi;
    requires com.sun.xml.fastinfoset;
    requires static com.sun.xml.ws.servlet;

    exports com.sun.xml.security.core.dsig;
    exports com.sun.xml.security.core.xenc;
    exports com.sun.xml.ws.api.security.secconv;
    exports com.sun.xml.ws.api.security.trust;
    exports com.sun.xml.ws.api.security.trust.client;
    exports com.sun.xml.ws.api.security.trust.config;
    exports com.sun.xml.ws.assembler.metro;
    exports com.sun.xml.ws.assembler.metro.dev;
    exports com.sun.xml.ws.assembler.metro.jaxws;
    exports com.sun.xml.ws.policy.impl.bindings;
    exports com.sun.xml.ws.rx;
    exports com.sun.xml.ws.rx.mc.runtime;
    exports com.sun.xml.ws.rx.message;
    exports com.sun.xml.ws.rx.rm.faults;
    exports com.sun.xml.ws.rx.rm.protocol;
    exports com.sun.xml.ws.rx.rm.runtime;
    exports com.sun.xml.ws.rx.rm.runtime.delivery;
    exports com.sun.xml.ws.rx.rm.runtime.sequence;
    exports com.sun.xml.ws.rx.rm.runtime.transaction;
    exports com.sun.xml.ws.rx.testing;
    exports com.sun.xml.ws.rx.util;
    exports com.sun.xml.ws.security;
    exports com.sun.xml.ws.security.addressing.policy;
    exports com.sun.xml.ws.security.impl.kerberos;
    exports com.sun.xml.ws.security.impl.policy;
    exports com.sun.xml.ws.security.impl.policyconv;
    exports com.sun.xml.ws.security.opt.api;
    exports com.sun.xml.ws.security.opt.api.keyinfo;
    exports com.sun.xml.ws.security.opt.api.reference;
    exports com.sun.xml.ws.security.opt.api.tokens;
    exports com.sun.xml.ws.security.opt.crypto;
    exports com.sun.xml.ws.security.opt.crypto.dsig;
    exports com.sun.xml.ws.security.opt.crypto.dsig.internal;
    exports com.sun.xml.ws.security.opt.crypto.dsig.keyinfo;
    exports com.sun.xml.ws.security.opt.impl;
    exports com.sun.xml.ws.security.opt.impl.dsig;
    exports com.sun.xml.ws.security.opt.impl.enc;
    exports com.sun.xml.ws.security.opt.impl.incoming;
    exports com.sun.xml.ws.security.opt.impl.keyinfo;
    exports com.sun.xml.ws.security.opt.impl.message;
    exports com.sun.xml.ws.security.opt.impl.outgoing;
    exports com.sun.xml.ws.security.opt.impl.reference;
    exports com.sun.xml.ws.security.opt.impl.tokens;
    exports com.sun.xml.ws.security.opt.impl.util;
    exports com.sun.xml.ws.security.policy;
    exports com.sun.xml.ws.security.secconv;
    exports com.sun.xml.ws.security.secconv.impl.bindings;
    exports com.sun.xml.ws.security.secconv.impl.wssx.bindings;
    exports com.sun.xml.ws.security.secext10;
    exports com.sun.xml.ws.security.secext11;
    exports com.sun.xml.ws.security.spi;
    exports com.sun.xml.ws.security.trust;
    exports com.sun.xml.ws.security.trust.elements;
    exports com.sun.xml.ws.security.trust.elements.str;
    exports com.sun.xml.ws.security.trust.impl;
    exports com.sun.xml.ws.security.trust.impl.wssx;
    exports com.sun.xml.ws.security.wsu10;
    exports com.sun.xml.ws.tx.at;
    exports com.sun.xml.ws.tx.at.common;
    exports com.sun.xml.ws.tx.at.common.client;
    exports com.sun.xml.ws.tx.at.runtime;
    exports com.sun.xml.ws.tx.at.v10.types;
    exports com.sun.xml.ws.tx.at.v11.types;
    exports com.sun.xml.ws.tx.coord.common;
    exports com.sun.xml.ws.tx.coord.common.client;
    exports com.sun.xml.ws.tx.coord.common.types;
    exports com.sun.xml.wss;
    exports com.sun.xml.wss.core;
    exports com.sun.xml.wss.impl;
    exports com.sun.xml.wss.impl.c14n;
    exports com.sun.xml.wss.impl.callback;
    exports com.sun.xml.wss.impl.configuration;
    exports com.sun.xml.wss.impl.misc;
    exports com.sun.xml.wss.impl.policy;
    exports com.sun.xml.wss.impl.policy.mls;
    exports com.sun.xml.wss.impl.policy.spi;
    exports com.sun.xml.wss.impl.policy.verifier;
    exports com.sun.xml.wss.jaxws.impl;
    exports com.sun.xml.wss.provider.wsit;
    exports com.sun.xml.wss.saml;

    opens com.sun.xml.ws.assembler.metro.jaxws to com.sun.xml.ws.rt, com.sun.xml.ws;

    opens com.sun.xml.security.core.ai to jakarta.xml.bind;
    opens com.sun.xml.security.core.dsig to jakarta.xml.bind;
    opens com.sun.xml.security.core.xenc to jakarta.xml.bind;
    opens com.sun.xml.ws.config.metro.parser.jsr109 to jakarta.xml.bind;
    opens com.sun.xml.ws.mex.client.schema to jakarta.xml.bind;
    opens com.sun.xml.ws.policy.impl.bindings to jakarta.xml.bind;
    opens com.sun.xml.ws.rx.mc.protocol.wsmc200702 to jakarta.xml.bind;
    opens com.sun.xml.ws.security.opt.crypto.dsig to jakarta.xml.bind;
    opens com.sun.xml.ws.security.opt.crypto.dsig.keyinfo to jakarta.xml.bind;
    opens com.sun.xml.ws.security.opt.impl.keyinfo to jakarta.xml.bind;
    opens com.sun.xml.ws.security.opt.impl.reference to jakarta.xml.bind;
    opens com.sun.xml.ws.security.secconv.impl.bindings to jakarta.xml.bind;
    opens com.sun.xml.ws.security.secconv.impl.wssx.bindings to jakarta.xml.bind;
    opens com.sun.xml.ws.security.secext10 to jakarta.xml.bind;
    opens com.sun.xml.ws.security.secext11 to jakarta.xml.bind;
    opens com.sun.xml.ws.security.soap11 to jakarta.xml.bind;
    opens com.sun.xml.ws.security.soap12 to jakarta.xml.bind;
    opens com.sun.xml.ws.security.trust.impl.bindings to jakarta.xml.bind;
    opens com.sun.xml.ws.security.trust.impl.wssx.bindings to jakarta.xml.bind;
    opens com.sun.xml.ws.security.wsu10 to jakarta.xml.bind;
    opens com.sun.xml.ws.transport.tcp.servicechannel.stubs to jakarta.xml.bind;
    opens com.sun.xml.ws.tx.at.v10.types to jakarta.xml.bind;
    opens com.sun.xml.ws.tx.at.v11.types to jakarta.xml.bind;
    opens com.sun.xml.ws.tx.coord.v10.types to jakarta.xml.bind;
    opens com.sun.xml.ws.tx.coord.v11.types to jakarta.xml.bind;
    opens com.sun.xml.wss.saml.internal.saml11.jaxb20 to jakarta.xml.bind;
    opens com.sun.xml.wss.saml.internal.saml20.jaxb20 to jakarta.xml.bind;

    uses com.sun.xml.ws.assembler.metro.ServerPipelineHook;
    uses com.sun.xml.ws.assembler.metro.dev.ClientPipelineHook;

    provides com.sun.xml.ws.api.config.management.ManagedEndpointFactory with
            com.sun.xml.ws.config.management.server.EndpointFactoryImpl;
    provides com.sun.xml.ws.api.pipe.TubelineAssemblerFactory with
            com.sun.xml.ws.assembler.metro.impl.MetroTubelineAssemblerFactoryImpl;
    provides com.sun.xml.ws.api.policy.PolicyResolverFactory with
            com.sun.xml.ws.policy.parser.WsitPolicyResolverFactory;
    provides com.sun.xml.ws.api.server.EndpointReferenceExtensionContributor with
            com.sun.xml.wss.provider.wsit.IdentityEPRExtnContributor;
    provides com.sun.xml.ws.api.wsdl.parser.MetadataResolverFactory with
            com.sun.xml.ws.mex.client.MetadataResolverFactoryImpl;
    provides com.sun.xml.ws.policy.jaxws.spi.PolicyFeatureConfigurator with
            com.sun.xml.ws.transport.tcp.policy.TCPTransportFeatureConfigurator,
            com.sun.xml.ws.transport.tcp.policy.OptimalTransportFeatureConfigurator,
            com.sun.xml.ws.rx.mc.policy.spi_impl.McFeatureConfigurator,
            com.sun.xml.ws.rx.rm.policy.spi_impl.RmFeatureConfigurator,
            com.sun.xml.ws.security.addressing.policy.WsawAddressingFeatureConfigurator,
            com.sun.xml.ws.security.impl.policy.SecurityFeatureConfigurator,
            com.sun.xml.ws.tx.at.policy.spi_impl.AtFeatureConfigurator;
    /*
            #com.sun.xml.ws.addressing.policy.AddressingFeatureConfigurator
#com.sun.xml.ws.encoding.policy.MtomFeatureConfigurator
#com.sun.xml.ws.encoding.policy.FastInfosetFeatureConfigurator
#com.sun.xml.ws.encoding.policy.SelectOptimalEncodingFeatureConfigurator
     */

    provides com.sun.xml.ws.policy.jaxws.spi.PolicyMapConfigurator with
            com.sun.xml.ws.transport.tcp.policy.TCPTransportPolicyMapConfigurator,
            com.sun.xml.ws.transport.tcp.policy.OptimalTransportPolicyMapConfigurator,
            com.sun.xml.ws.rx.mc.policy.spi_impl.McPolicyMapConfigurator,
            com.sun.xml.ws.rx.rm.policy.spi_impl.RmPolicyMapConfigurator,
            com.sun.xml.ws.security.addressing.policy.WsawAddressingPolicyMapConfigurator,
            com.sun.xml.ws.tx.at.policy.spi_impl.AtPolicyMapConfigurator;
    /*
#com.sun.xml.ws.addressing.policy.AddressingPolicyMapConfigurator,
#com.sun.xml.ws.encoding.policy.MtomPolicyMapConfigurator,
     */
    provides com.sun.xml.ws.policy.spi.PolicyAssertionCreator with
            com.sun.xml.ws.rx.mc.policy.spi_impl.McAssertionCreator,
            com.sun.xml.ws.rx.rm.policy.spi_impl.RmAssertionCreator,
            com.sun.xml.ws.security.addressing.impl.policy.AddressingPolicyAssertionCreator,
            com.sun.xml.ws.security.impl.policy.SecurityPolicyAssertionCreator,
            com.sun.xml.ws.security.impl.policy.TrustPolicyAssertionCreator,
            com.sun.xml.ws.security.impl.policy.WSSClientConfigAssertionCreator,
            com.sun.xml.ws.security.impl.policy.WSSServerConfigAssertionCreator,
            com.sun.xml.ws.security.impl.policy.TrustClientConfigAssertionCreator,
            com.sun.xml.ws.security.impl.policy.TrustServerConfigAssertionCreator,
            com.sun.xml.ws.security.impl.policy.SCClientConfigAssertionCreator,
            com.sun.xml.ws.security.impl.policy.SCServerConfigAssertionCreator,
            com.sun.xml.ws.tx.at.policy.spi_impl.AtAssertionCreator;
    provides com.sun.xml.ws.policy.spi.PolicyAssertionValidator with
            com.sun.xml.ws.policy.jcaps.JCapsPolicyValidator,
            com.sun.xml.ws.transport.tcp.wsit.TCPTransportPolicyValidator,
            com.sun.xml.ws.rx.mc.policy.spi_impl.McAssertionValidator,
            com.sun.xml.ws.rx.rm.policy.spi_impl.RmAssertionValidator,
            com.sun.xml.ws.security.addressing.policy.WsawAddressingPolicyValidator,
            com.sun.xml.ws.security.impl.policy.SecurityPolicyValidator,
            com.sun.xml.ws.tx.at.policy.spi_impl.AtAssertionValidator;

    provides com.sun.xml.ws.policy.spi.PrefixMapper with
            com.sun.xml.ws.transport.tcp.wsit.TCPTransportPrefixMapper,
            com.sun.xml.ws.rx.mc.policy.spi_impl.McPrefixMapper,
            com.sun.xml.ws.rx.rm.policy.spi_impl.RmPrefixMapper,
            com.sun.xml.ws.security.addressing.policy.WsawAddressingPrefixMapper,
            com.sun.xml.ws.security.impl.policy.SecurityPrefixMapper,
            com.sun.xml.ws.tx.at.policy.spi_impl.AtPrefixMapper;
    provides com.sun.xml.ws.security.spi.AlternativeSelector with
            com.sun.xml.wss.impl.policy.verifier.UsernameOrSAMLAlternativeSelector,
            com.sun.xml.wss.impl.policy.verifier.UsernameOrX509AlternativeSelector;

}
