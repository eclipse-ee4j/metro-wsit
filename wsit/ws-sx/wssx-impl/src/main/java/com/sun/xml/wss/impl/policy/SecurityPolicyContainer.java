/*
 * Copyright (c) 2010, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * $Id: SecurityPolicyContainer.java,v 1.2 2010-10-21 15:37:33 snajper Exp $
 */

package com.sun.xml.wss.impl.policy;

import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;

import com.sun.xml.wss.impl.PolicyTypeUtil;

/**
 * Represents a container for a static collection of SecurityPolicies.
 * It Associates a StaticPolicyContext with a SecurityPolicy.
 */
public class SecurityPolicyContainer implements SecurityPolicy {

    protected HashMap _ctx2PolicyMap = new HashMap();

    public SecurityPolicyContainer() {}

    /**
     * Associate more than one SecurityPolicy with a StaticPolicyContext
     * @param ctx StaticPolicyContext
     * @param policy SecurityPolicy
     */
    @SuppressWarnings("unchecked")
    public void setSecurityPolicy(StaticPolicyContext ctx, SecurityPolicy policy) {
        ArrayList al = (ArrayList)_ctx2PolicyMap.get(ctx);

        if (al != null)
            al.add(policy);
        else {
            al = new ArrayList();
            al.add(policy);
            _ctx2PolicyMap.put(ctx, al);
        }
    }

    /**
     * Return an immutable collection of SecurityPolicies,
     *  association between policies are free to inference
     *
     * @param ctx StaticPolicyContext
     * @return Iterator of security policies associated with the StaticPolicyContext <code>ctx</code>
     */
    public Iterator getSecurityPolicies(StaticPolicyContext ctx) {
        ArrayList list = (ArrayList)_ctx2PolicyMap.get(ctx);

        if (list != null)
            return list.iterator();
        return null;
    }

    /**
     * Returns all keys (StaticPolicyContext)
     * @return Iterator on Key Set
     */
    public Iterator getAllContexts() {
        return _ctx2PolicyMap.keySet().iterator();
    }

    /*
     * Composite SecurityPolicy instances are evaluated at runtime,
     * Throws PolicyGenerationException if evaluation is unsuccessful
     *
     * @param sCtx StaticPolicyContext
     *        dCtx DynamicPolicyContext
     * @return Iterator of SecurityPolicies
     * @exception PolicyGenerationException
     */
    @SuppressWarnings("unchecked")
    public Iterator getSecurityPolicies(StaticPolicyContext sCtx, DynamicPolicyContext dCtx) {
        ArrayList hs0 = (ArrayList)_ctx2PolicyMap.get(sCtx);

        ArrayList hs1 = new ArrayList();

        Iterator i = hs0.iterator();
        while (i.hasNext()) {
            Object obj = i.next();

            /*if (obj instanceof PolicyComposer) {
                PolicyComposer pc = (PolicyComposer)obj;
                try {
                    SecurityPolicy sp = pc.evaluateSecurityPolicy(dCtx);
                    hs1.add(sp);
                } catch (UnsupportedOperationException uoe) {
                    try {
                        Collection s = pc.evaluate(dCtx);
                        hs1.addAll(s);
                    } catch (UnsupportedOperationException eou) {
                        throw new PolicyGenerationException(eou);
                    }
                }
            } else*/
            hs1.add(obj);
        }

        return hs1.iterator();
    }

    /**
     * @return the type of the policy
     */
    @Override
    public String getType() {
        return PolicyTypeUtil.SEC_POLICY_CONTAINER_TYPE;
    }

}
