/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.trust.impl.ic;

import com.sun.xml.ws.api.security.trust.STSAttributeProvider;
import com.sun.xml.ws.api.security.trust.WSTrustException;
import com.sun.xml.ws.security.IssuedTokenContext;
import com.sun.xml.ws.security.trust.elements.BaseSTSRequest;
import com.sun.xml.ws.security.trust.elements.BaseSTSResponse;
import com.sun.xml.ws.security.trust.elements.RequestSecurityToken;
import com.sun.xml.ws.security.trust.elements.RequestSecurityTokenResponse;
import com.sun.xml.ws.security.trust.impl.WSTrustContractImpl;
import com.sun.xml.wss.WSITXMLFactory;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Jiandong Guo
 */
public class ICContractImpl extends WSTrustContractImpl{
    @Override
    protected void handleExtension(BaseSTSRequest request, BaseSTSResponse response, IssuedTokenContext context) throws WSTrustException{
        @SuppressWarnings("unchecked") final Map<QName, List<String>> claimedAttributes = (Map<QName, List<String>>) context.getOtherProperties().get(IssuedTokenContext.CLAIMED_ATTRUBUTES);
        handleDisplayToken((RequestSecurityToken)request, (RequestSecurityTokenResponse)response, claimedAttributes);
    }
    
    private void handleDisplayToken(RequestSecurityToken rst, RequestSecurityTokenResponse rstr, Map<QName, List<String>> claimedAttrs)throws WSTrustException{
        List<Object> list = rst.getExtensionElements();
        boolean displayToken = false;
        for (int i =0; i < list.size(); i++){
            Object ele = list.get(i);
            if ((ele instanceof Element)){
                String localName = ((Element)ele).getLocalName();
                if ("RequestDisplayToken".equals(localName)){
                    displayToken = true;
                    break;
                }
            }
        }
        if (displayToken){
            // Create RequestedDisplayToken
            try {
                final DocumentBuilderFactory dbf = WSITXMLFactory.createDocumentBuilderFactory(WSITXMLFactory.DISABLE_SECURE_PROCESSING);
                dbf.setNamespaceAware(true);
                final DocumentBuilder builder = dbf.newDocumentBuilder();
                Document doc = builder.newDocument();
                Element rdt = doc.createElementNS("http://schemas.xmlsoap.org/ws/2005/05/identity", "RequestedDisplayToken");
                rdt.setAttribute("xmlns", "http://schemas.xmlsoap.org/ws/2005/05/identity");
                Element dt = doc.createElementNS("http://schemas.xmlsoap.org/ws/2005/05/identity", "DisplayToken");
                dt.setAttribute("xml:lang", "en-us");
                rdt.appendChild(dt);
                final Set<Map.Entry<QName, List<String>>> entries = claimedAttrs.entrySet();
                for(Map.Entry<QName, List<String>> entry : entries){
                    final QName attrKey = entry.getKey();
                    final List<String> values = entry.getValue();
                    if (values != null && values.size() > 0){
                        if (!STSAttributeProvider.NAME_IDENTIFIER.equals(attrKey.getLocalPart())){
                            Element dc = doc.createElementNS("http://schemas.xmlsoap.org/ws/2005/05/identity", "DisplayClaim");
                            dc.setAttribute("xmlns", "http://schemas.xmlsoap.org/ws/2005/05/identity");
                            String uri = attrKey.getNamespaceURI()+"/" + attrKey.getLocalPart();
                            dc.setAttribute("Uri", uri);
                            dt.appendChild(dc);
                            Element dtg = doc.createElementNS("http://schemas.xmlsoap.org/ws/2005/05/identity", "DisplayTag");
                            dtg.appendChild(doc.createTextNode(attrKey.getLocalPart()));
                            dc.appendChild(dtg);
                            
                            String displayValue = values.get(0);
                            Element dtv = doc.createElementNS("http://schemas.xmlsoap.org/ws/2005/05/identity", "DisplayValue");
                           // if ("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/privatepersonalidentifier".equals(uri)){
                              //  displayValue = WSTrustUtils.createFriendlyPPID(displayValue);
                           // }
                            dtv.appendChild(doc.createTextNode(displayValue));
                            dc.appendChild(dtv);
                        }
                    }
                }
                rstr.getAny().add(rdt);
            }catch (Exception ex){
                throw new WSTrustException(ex.getMessage(), ex);
            }   
        }
    }
}
