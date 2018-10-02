/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.impl.policyconv;

import com.sun.xml.ws.security.policy.RequiredElements;
import com.sun.xml.wss.impl.policy.PolicyGenerationException;
import com.sun.xml.wss.impl.policy.mls.MandatoryTargetPolicy;
import com.sun.xml.wss.impl.policy.mls.MessagePolicy;
import com.sun.xml.wss.impl.policy.mls.Target;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 *
 * @author K.Venugopal@sun.com
 */
public class RequiredElementsProcessor {
    
    private List<RequiredElements> assertionList;
    private MessagePolicy mp;
    /** Creates a new instance of RequiredElementProcessor */
    public RequiredElementsProcessor(List<RequiredElements> al,MessagePolicy mp) {
        this.assertionList = al;
        this.mp = mp;
    }    
    
    public void process() throws PolicyGenerationException{
        Vector<String> targetValues = new Vector<String>();
        MandatoryTargetPolicy mt = new MandatoryTargetPolicy();
        MandatoryTargetPolicy.FeatureBinding mfb = new MandatoryTargetPolicy.FeatureBinding();
        mt.setFeatureBinding(mfb);
        List<Target> targets = mfb.getTargetBindings();
        for(RequiredElements re : assertionList){
            Iterator itr = re.getTargets();
            while(itr.hasNext()){
                String xpathExpr = (String)itr.next();
                if(!targetValues.contains(xpathExpr)){
                    targetValues.add(xpathExpr);
                    Target tr = new Target();
                    tr.setType(Target.TARGET_TYPE_VALUE_XPATH);
                    tr.setValue(xpathExpr);
                    tr.setContentOnly(false);
                    tr.setEnforce(true);
                    targets.add(tr);
                }
            }
        } 
       mp.append(mt);  
    }
}
