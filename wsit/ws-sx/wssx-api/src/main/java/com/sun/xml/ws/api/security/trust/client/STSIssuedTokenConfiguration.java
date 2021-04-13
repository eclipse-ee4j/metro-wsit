/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.api.security.trust.client;

import com.sun.xml.ws.api.security.trust.Claims;
import com.sun.xml.ws.security.Token;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Jiandong Guo
 */
public abstract class STSIssuedTokenConfiguration implements IssuedTokenConfiguration{
    
    public static final String PROTOCOL_10 = "http://schemas.xmlsoap.org/ws/2005/02/trust";
    public static final String PROTOCOL_13 = "http://docs.oasis-open.org/ws-sx/ws-trust/200512";

    public static final String ISSUED_TOKEN = "IssuedToken";
    public static final String APPLIES_TO = "AppliesTo";
    public static final String ACT_AS ="ActAs";
    public static final String SHARE_TOKEN = "shareToken";
    public static final String RENEW_EXPIRED_TOKEN = "renewExpiredToken";
    public static final String STS_ENDPOINT = "sts-endpoint";
    public static final String STS_MEX_ADDRESS = "sts-mex-address";
    public static final String STS_WSDL_LOCATION ="sts-wsdlLocation";
    public static final String STS_SERVICE_NAME ="sts-service-name";
    public static final String STS_PORT_NAME ="sts-port-name";
    public static final String STS_NAMESPACE ="sts-namespace";
    public static final String STS_SIGNATURE_ALGORITHM ="sts-signature-algorithm";
    public static final String LIFE_TIME = "LifeTime";
    public static final String MAX_CLOCK_SKEW = "MaxClockSkew";
    
    protected String protocol;
    
    protected String stsEndpoint;
    
    protected String stsMEXAddress = null;
    
    protected String stsWSDLLocation = null;;
    
    protected String stsServiceName = null;
    
    protected String stsPortName = null;
    
    protected String stsNamespace = null;

    protected SecondaryIssuedTokenParameters sisPara = null;

    private Map<String, Object> otherOptions = new HashMap<String, Object>();
    
    protected STSIssuedTokenConfiguration(){

    }
    protected STSIssuedTokenConfiguration(String stsEndpoint, String stsMEXAddress){
        this(PROTOCOL_10, stsEndpoint, stsMEXAddress);
    }
    protected STSIssuedTokenConfiguration(String protocol, String stsEndpoint, String stsMEXAddress){
        this.protocol = protocol;
        this.stsEndpoint = stsEndpoint;
        this.stsMEXAddress = stsMEXAddress;
    }
    
    protected STSIssuedTokenConfiguration(String stsEndpoint, 
                          String stsWSDLLocation, String stsServiceName, String stsPortName, String stsNamespace){
        this(PROTOCOL_10, stsEndpoint, stsWSDLLocation, stsServiceName, stsPortName, stsNamespace);
    }
    
    protected STSIssuedTokenConfiguration(String protocol, String stsEndpoint, 
                          String stsWSDLLocation, String stsServiceName, String stsPortName, String stsNamespace){
        this.protocol = protocol;
        this.stsEndpoint = stsEndpoint;
        this.stsWSDLLocation = stsWSDLLocation;
        this.stsServiceName = stsServiceName;
        this.stsPortName = stsPortName;
        this.stsNamespace = stsNamespace;
    }
    
    public String getProtocol(){
        return protocol;
    }
     
    public String getSTSEndpoint(){
        return this.stsEndpoint;
    }
    
    public String getSTSMEXAddress(){
        return this.stsMEXAddress;
    }
    
    public String getSTSWSDLLocation(){
        return this.stsWSDLLocation;
    }
    
    public String getSTSServiceName(){
        return this.stsServiceName;
    }
    
    public String getSTSPortName(){
        return this.stsPortName;
    }
    
    public String getSTSNamespace(){
        return this.stsNamespace;
    }

    public SecondaryIssuedTokenParameters getSecondaryIssuedTokenParameters(){
        return this.sisPara;
    }

    public Map<String, Object> getOtherOptions(){
        return this.otherOptions;
    }
    
    public abstract String getTokenType();
    
    public abstract String getKeyType();
    
    public abstract long getKeySize();
    
    public abstract String getSignatureAlgorithm();
    
    public abstract String getEncryptionAlgorithm();
    
    public abstract String getCanonicalizationAlgorithm();
    
    public abstract String getKeyWrapAlgorithm();
    
    public abstract String getSignWith();
    
    public abstract String getEncryptWith();
    
    public abstract Claims getClaims();
    
    public abstract Token getOBOToken();
}
