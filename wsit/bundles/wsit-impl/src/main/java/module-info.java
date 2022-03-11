/*
 * Copyright (c) 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

module org.glassfish.metro.wsit.impl {

    requires org.glassfish.metro.wsit.api;
    requires java.security.jgss;
    requires java.transaction.xa;
    requires java.sql;
    requires java.rmi;
    requires transitive java.xml.crypto;
    requires jdk.security.auth;
    requires com.sun.xml.ws.rt;
    requires com.sun.xml.ws.fi;
    requires com.sun.xml.ws.policy;

    requires com.sun.xml.fastinfoset;
    requires static com.sun.xml.ws.servlet;
    requires gmbal;
    requires transitive org.glassfish.ha.api;

    exports com.sun.xml.ws.assembler.metro.jaxws;
    exports com.sun.xml.ws.assembler.metro;
    exports com.sun.xml.ws.rx.mc.runtime;
    exports com.sun.xml.ws.rx.rm.runtime;
    exports com.sun.xml.ws.rx.testing;
    exports com.sun.xml.ws.tx.at.runtime;
    exports com.sun.xml.wss.provider.wsit;

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
