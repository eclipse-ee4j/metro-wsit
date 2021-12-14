/*
 * Copyright (c) 2010, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * DoNotCacheCondition.java
 *
 * Created on August 18, 2005, 12:32 PM
 *
 */

package com.sun.xml.wss.saml;

/**
 *This is an implementation of the abstract <code>Condition</code> class, which
 * specifes that the assertion this <code>DoNotCacheCondition</code> is part of,
 * is the new element in SAML 1.1, that allows an assertion party to express that
 * an assertion should not be cached by the relying party for future use. In another
 * word, such an assertion is meant only for "one-time" use by the relying party.
 * 
 * <p>The following schema fragment specifies the expected content contained within 
 * SAML DoNotCacheCondition element.
 * <pre>
 * &lt;complexType name="DoNotCacheConditionType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{urn:oasis:names:tc:SAML:1.0:assertion}ConditionAbstractType"&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 */
public interface OneTimeUse {
    
}
