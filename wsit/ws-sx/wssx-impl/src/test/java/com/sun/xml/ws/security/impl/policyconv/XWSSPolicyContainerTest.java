/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * XWSSPolicyContainerTest.java
 * JUnit based test
 *
 * Created on August 24, 2006, 3:54 AM
 */

package com.sun.xml.ws.security.impl.policyconv;

import com.sun.xml.wss.impl.policy.mls.EncryptionTarget;
import com.sun.xml.wss.impl.policy.mls.SignatureTarget;
import com.sun.xml.wss.impl.policy.mls.Target;
import com.sun.xml.wss.impl.policy.mls.TimestampPolicy;
import com.sun.xml.wss.impl.policy.mls.WSSPolicy;
import junit.framework.*;
import com.sun.xml.wss.impl.PolicyTypeUtil;
import com.sun.xml.ws.security.policy.MessageLayout;
import com.sun.xml.wss.impl.policy.mls.AuthenticationTokenPolicy;
import com.sun.xml.wss.impl.policy.mls.EncryptionPolicy;
import com.sun.xml.wss.impl.policy.mls.MessagePolicy;
import com.sun.xml.wss.impl.policy.mls.SignaturePolicy;

import java.util.List;

/**
 *
 * @author Mayank.Mishra@SUN.com
 */
public class XWSSPolicyContainerTest extends TestCase {
    
    public XWSSPolicyContainerTest(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() {
    }
    
    @Override
    protected void tearDown() {
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(XWSSPolicyContainerTest.class);
        
        return suite;
    }
    
    //for client outgoing
    public void testXWSSPolicyContainer1() throws Exception {
        XWSSPolicyContainer container = new XWSSPolicyContainer(false, false);
        SignaturePolicy sigPolicy = new SignaturePolicy();
        SignatureTarget t = new SignatureTarget();
        t.setType("uri");
        t.setValue("sig1");
        SignaturePolicy.FeatureBinding featureBinding =
                (SignaturePolicy.FeatureBinding)sigPolicy.getFeatureBinding();
        
        featureBinding.addTargetBinding(t);
        ((AuthenticationTokenPolicy.X509CertificateBinding)sigPolicy.newX509CertificateKeyBinding()).setReferenceType("Direct");
        
        EncryptionPolicy encPolicy = new EncryptionPolicy();
        EncryptionTarget t1 = new EncryptionTarget();
        t.setType("uri");
        t.setValue("enc1");
        EncryptionPolicy.FeatureBinding featureBinding1 =
                (EncryptionPolicy.FeatureBinding)encPolicy.getFeatureBinding();
        
        featureBinding1.addTargetBinding(t1);
        ((AuthenticationTokenPolicy.X509CertificateBinding)encPolicy.newX509CertificateKeyBinding()).setReferenceType("Direct");
        
        
        TimestampPolicy tp = new TimestampPolicy();
        AuthenticationTokenPolicy atp = new AuthenticationTokenPolicy();
        AuthenticationTokenPolicy.UsernameTokenBinding ut =
                new AuthenticationTokenPolicy.UsernameTokenBinding();
        atp.setFeatureBinding(ut);
        
        container.setMessageMode(false, false);
        container.setPolicyContainerMode(MessageLayout.Lax);
        container.insert(sigPolicy);
        container.insert(encPolicy);
        container.insert(tp);
        container.insert(atp);
        
        print(container.getMessagePolicy(false));
        
        System.out.println("\n\n\n-------------------------\n");
        
    }
    
    
    //for client incoming
    public void testXWSSPolicyContainer2() throws Exception {
        XWSSPolicyContainer container = new XWSSPolicyContainer(false, true);
        SignaturePolicy sigPolicy = new SignaturePolicy();
        SignatureTarget t = new SignatureTarget();
        t.setType("uri");
        t.setValue("sig1");
        SignaturePolicy.FeatureBinding featureBinding =
                (SignaturePolicy.FeatureBinding)sigPolicy.getFeatureBinding();
        
        featureBinding.addTargetBinding(t);
        ((AuthenticationTokenPolicy.X509CertificateBinding)sigPolicy.newX509CertificateKeyBinding()).setReferenceType("Direct");
        
        EncryptionPolicy encPolicy = new EncryptionPolicy();
        EncryptionTarget t1 = new EncryptionTarget();
        t.setType("uri");
        t.setValue("enc1");
        EncryptionPolicy.FeatureBinding featureBinding1 =
                (EncryptionPolicy.FeatureBinding)encPolicy.getFeatureBinding();
        
        featureBinding1.addTargetBinding(t1);
        ((AuthenticationTokenPolicy.X509CertificateBinding)encPolicy.newX509CertificateKeyBinding()).setReferenceType("Direct");
        
        
        TimestampPolicy tp = new TimestampPolicy();
        AuthenticationTokenPolicy atp = new AuthenticationTokenPolicy();
        AuthenticationTokenPolicy.UsernameTokenBinding ut =
                new AuthenticationTokenPolicy.UsernameTokenBinding();
        atp.setFeatureBinding(ut);
        
        container.setMessageMode(false, true);
        container.setPolicyContainerMode(MessageLayout.Lax);
        container.insert(sigPolicy);
        container.insert(encPolicy);
        container.insert(tp);
        container.insert(atp);
        
        print(container.getMessagePolicy(false));
        
        System.out.println("\n\n\n-------------------------\n");
        
        
    }
    
    //for server outgoing
    public void testXWSSPolicyContainer3() throws Exception {
        XWSSPolicyContainer container = new XWSSPolicyContainer(true, false);
        SignaturePolicy sigPolicy = new SignaturePolicy();
        SignatureTarget t = new SignatureTarget();
        t.setType("uri");
        t.setValue("sig1");
        SignaturePolicy.FeatureBinding featureBinding =
                (SignaturePolicy.FeatureBinding)sigPolicy.getFeatureBinding();
        
        featureBinding.addTargetBinding(t);
        ((AuthenticationTokenPolicy.X509CertificateBinding)sigPolicy.newX509CertificateKeyBinding()).setReferenceType("Direct");
        
        EncryptionPolicy encPolicy = new EncryptionPolicy();
        EncryptionTarget t1 = new EncryptionTarget();
        t.setType("uri");
        t.setValue("enc1");
        EncryptionPolicy.FeatureBinding featureBinding1 =
                (EncryptionPolicy.FeatureBinding)encPolicy.getFeatureBinding();
        
        featureBinding1.addTargetBinding(t1);
        ((AuthenticationTokenPolicy.X509CertificateBinding)encPolicy.newX509CertificateKeyBinding()).setReferenceType("Direct");
        
        
        TimestampPolicy tp = new TimestampPolicy();
        AuthenticationTokenPolicy atp = new AuthenticationTokenPolicy();
        AuthenticationTokenPolicy.UsernameTokenBinding ut =
                new AuthenticationTokenPolicy.UsernameTokenBinding();
        atp.setFeatureBinding(ut);
        
        container.setMessageMode(true, false);
        container.setPolicyContainerMode(MessageLayout.Lax);
        container.insert(sigPolicy);
        container.insert(encPolicy);
        container.insert(tp);
        container.insert(atp);
        
        print(container.getMessagePolicy(false));
        
        System.out.println("\n\n\n-------------------------\n");
        
        
    }
    
    //for server incoming
    public void testXWSSPolicyContainer4() throws Exception {
        XWSSPolicyContainer container = new XWSSPolicyContainer(true, true);
        SignaturePolicy sigPolicy = new SignaturePolicy();
        SignatureTarget t = new SignatureTarget();
        t.setType("uri");
        t.setValue("sig1");
        SignaturePolicy.FeatureBinding featureBinding =
                (SignaturePolicy.FeatureBinding)sigPolicy.getFeatureBinding();
        
        featureBinding.addTargetBinding(t);
        ((AuthenticationTokenPolicy.X509CertificateBinding)sigPolicy.newX509CertificateKeyBinding()).setReferenceType("Direct");
        
        EncryptionPolicy encPolicy = new EncryptionPolicy();
        EncryptionTarget t1 = new EncryptionTarget();
        t.setType("uri");
        t.setValue("enc1");
        EncryptionPolicy.FeatureBinding featureBinding1 =
                (EncryptionPolicy.FeatureBinding)encPolicy.getFeatureBinding();
        
        featureBinding1.addTargetBinding(t1);
        ((AuthenticationTokenPolicy.X509CertificateBinding)encPolicy.newX509CertificateKeyBinding()).setReferenceType("Direct");
        
        
        TimestampPolicy tp = new TimestampPolicy();
        AuthenticationTokenPolicy atp = new AuthenticationTokenPolicy();
        AuthenticationTokenPolicy.UsernameTokenBinding ut =
                new AuthenticationTokenPolicy.UsernameTokenBinding();
        atp.setFeatureBinding(ut);
        
        container.setMessageMode(true, true);
        container.setPolicyContainerMode(MessageLayout.Lax);
        container.insert(sigPolicy);
        container.insert(encPolicy);
        container.insert(tp);
        container.insert(atp);
        
        print(container.getMessagePolicy(false));
        
        System.out.println("\n\n\n-------------------------\n");
        
        
    }
    
    public void print(MessagePolicy generated ) throws Exception {
        for ( int i = 0 ; i<generated.size() ; i++ ) {
            System.out.println("Type : " + generated.get(i).getType());
            
            if ( PolicyTypeUtil.signaturePolicy(generated.get(i))) {
                WSSPolicy p = (WSSPolicy)generated.get(i);
                System.out.println("KeyBinding : " + p.getKeyBinding().getType());
                
                SignaturePolicy.FeatureBinding f1 = (SignaturePolicy.FeatureBinding)p.getFeatureBinding();
              @SuppressWarnings("unchecked")
                List<Target> t1 = f1.getTargetBindings();
                System.out.println("No of Targets : " + t1.size());
                for ( Target t : t1 ) {
                    System.out.println(t.getType() + "  " + t.getValue());
                }
            }
            
            if ( PolicyTypeUtil.encryptionPolicy(generated.get(i))) {
                WSSPolicy p = (WSSPolicy)generated.get(i);
                System.out.println("KeyBinding : " + p.getKeyBinding().getType());
                
                EncryptionPolicy.FeatureBinding f1 = (EncryptionPolicy.FeatureBinding)p.getFeatureBinding();
                @SuppressWarnings("unchecked")
                List<Target> t1 = f1.getTargetBindings();
                System.out.println("No of Targets : " + t1.size());
                for ( Target t : t1 ) {
                    System.out.println(t.getType() + "  " + t.getValue());
                }
            }
        }
    }
    
    
}
