/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package common;

import javax.security.auth.Subject;
import com.sun.xml.ws.api.security.trust.*;
import java.security.Principal;
import java.util.*;
import javax.xml.namespace.*;

/**
 *  <wst:Claims Dialect=?http://schemas.xmlsoap.org/ws/2005/05/identity?
 *       xmlns:wst="http://docs.oasis-open.org/ws-sx/ws-trust/200512"
 *       xmlns:ic="http://schemas.xmlsoap.org/ws/2005/05/identity">
 *      <ic:ClaimType Uri=?http://schemas.xmlsoap.org/ws/2005/05/identity/claims/locality?/>
 *      <ic:ClaimType Uri=?http://schemas.xmlsoap.org/ws/2005/05/identity/claims/role?/>
 *  </wst:Claims>
 * @author jdg
 */

public class MySTSAttributeProvider implements STSAttributeProvider {
    
    public Map<QName, List<String>> getClaimedAttributes(Subject subject, String appliesTo, String tokenType, Claims claims)
    {
        String name = null; 
        
        Set<Principal> principals = subject.getPrincipals();
        if (principals != null){
            final Iterator iterator = principals.iterator();
            while (iterator.hasNext()){
                String cnName = principals.iterator().next().getName();
                int pos = cnName.indexOf("=");
                name = cnName.substring(pos+1);
                break;
            }       
        }
        
        Map<QName, List<String>> attrs = new HashMap<QName, List<String>>();

        QName nameIdQName = new QName("http://sun.com",STSAttributeProvider.NAME_IDENTIFIER);
        List<String> nameIdAttrs = new ArrayList<String>();
        nameIdAttrs.add(getUserPseduoName(name));
        attrs.put(nameIdQName,nameIdAttrs);

        if (claims != null){
            MyClaims myClaims = new MyClaims(claims);
            List<String> claimTypes = myClaims.getClaimsTypes();
            for (String claimType : claimTypes){
                if (MyClaims.ROLE.equals(claimType)){
                    QName testQName = new QName("http://sun.com","Role");
                    List<String> testAttrs = new ArrayList<String>();
                    testAttrs.add(getUserRole(name));
                    attrs.put(testQName,testAttrs);
                } else if (MyClaims.LOCALITY.equals(claimType)){
                    QName testQName = new QName("http://sun.com","Locality");
                    List<String> testAttrs = new ArrayList<String>();
                    testAttrs.add(getUserLocality(name));
                    attrs.put(testQName,testAttrs);
                }
            }
        }
        return attrs;
    }  
    
    private String getUserPseduoName(String userName){
        
        if ("alice".equals(userName)){
            return "123";
        }
        
        if ("bob".equals(userName)){
            return "231";
        }
        
        return "456";
    }
    
    private String getUserRole(String userName){
        if ("alice".equals(userName)){
            return "staff ";
        }
        
        if ("bob".equals(userName)){
            return "manager";
        }
        
        return "staff";
    }

    private String getUserLocality(String userName){
        if ("alice".equals(userName)){
            return "Santa Clara, CA";
        }

        if ("bob".equals(userName)){
            return "New York, NY";
        }

        return "Santa Clara, CA";
    }
}
