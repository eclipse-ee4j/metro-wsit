/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.coord.v11;

import org.glassfish.jaxb.runtime.api.JAXBRIContext;
import com.sun.xml.ws.tx.coord.common.types.*;
import com.sun.xml.ws.tx.coord.v11.types.*;
import com.sun.xml.ws.tx.coord.v11.types.RegisterResponseType;
import com.sun.xml.ws.tx.at.WSATConstants;

import javax.xml.namespace.QName;
import jakarta.xml.ws.wsaddressing.W3CEndpointReference;
import jakarta.xml.ws.WebServiceException;
import jakarta.xml.bind.JAXBException;
import java.util.List;
import java.util.Map;


public class XmlTypeAdapter {



    public static BaseExpires<Expires> adapt(final Expires delegate) {
        if (delegate == null) return null;
        else return new ExpiresImpl(delegate);
    }

    public static BaseIdentifier<CoordinationContextType.Identifier> adapt(final CoordinationContextType.Identifier delegate) {
        if (delegate == null) return null;
        else return new IdentifierImpl(delegate);
    }

/*
    public CoordinationContextTypeIF<W3CEndpointReference, Expires, CoordinationContextType.Identifier,CoordinationContextType> adapt(final CoordinationContextType delegate) {
        if (delegate == null) return null;
        else return new CoordinationContextTypeImpl(delegate, this);
    }
*/

    public static CoordinationContextIF<W3CEndpointReference, Expires, CoordinationContextType.Identifier,CoordinationContextType> adapt(final CoordinationContext delegate) {
        if (delegate == null) return null;
        else return new CoordinationContextImpl(delegate);
    }


    public static BaseRegisterType<W3CEndpointReference, RegisterType> adapt(final RegisterType delegate) {
        if (delegate == null) return null;
        else return new RegisterTypeImpl(delegate);
    }

    public static BaseRegisterType<W3CEndpointReference, RegisterType> newRegisterType() {
         return new RegisterTypeImpl(new RegisterType());
    }

   public static BaseRegisterResponseType<W3CEndpointReference,RegisterResponseType> adapt(final RegisterResponseType delegate) {
       if (delegate == null) return null;
       else return new RegisterResponseTypeImpl(delegate);
   }

    public static BaseRegisterResponseType newRegisterResponseType() {
        return new RegisterResponseTypeImpl(new RegisterResponseType());
    }

    static class ExpiresImpl extends BaseExpires<Expires> {

        protected ExpiresImpl(Expires delegate) {
            super(delegate);
        }

        public long getValue() {
            return delegate.getValue();
        }

        public void setValue(long value) {
            delegate.setValue(value);
        }

        public Map getOtherAttributes() {
            return delegate.getOtherAttributes();
        }
    }

    static class IdentifierImpl extends BaseIdentifier<CoordinationContextType.Identifier> {

        protected IdentifierImpl(CoordinationContextType.Identifier delegate) {
            super(delegate);
        }

        public String getValue() {
            return delegate.getValue();
        }

        public void setValue(String value) {
            delegate.setValue(value);
        }

        public Map<QName, String> getOtherAttributes() {
            return delegate.getOtherAttributes();
        }

        public QName getQName() {
            return new QName(WSATConstants.WSCOOR11_NS_URI,WSATConstants.IDENTIFIER);
        }

    }


    public static class CoordinationContextTypeImpl implements CoordinationContextTypeIF<W3CEndpointReference,Expires, CoordinationContextType.Identifier,CoordinationContextType> {
        private CoordinationContextType delegate;

        public CoordinationContextTypeImpl(CoordinationContextType delegate) {
            this.delegate = delegate;
        }

        public BaseIdentifier<CoordinationContextType.Identifier> getIdentifier() {
            return XmlTypeAdapter.adapt(delegate.getIdentifier());
        }

        public void setIdentifier(BaseIdentifier<CoordinationContextType.Identifier> value) {
            delegate.setIdentifier(value.getDelegate());
        }

        public BaseExpires<Expires> getExpires() {
            return XmlTypeAdapter.adapt(delegate.getExpires());
        }

        public void setExpires(BaseExpires<Expires> value) {
            delegate.setExpires(value.getDelegate());
        }


        public String getCoordinationType() {
            return delegate.getCoordinationType();
        }

        public void setCoordinationType(String value) {
            delegate.setCoordinationType(value);
        }

        public W3CEndpointReference getRegistrationService() {
            return delegate.getRegistrationService();
        }

        public void setRegistrationService(W3CEndpointReference value) {
            delegate.setRegistrationService(value);
        }

        public Map<QName, String> getOtherAttributes() {
            return delegate.getOtherAttributes();
        }

        public CoordinationContextType getDelegate() {
            return delegate;
        }
    }

    public static class CoordinationContextImpl extends CoordinationContextTypeImpl implements CoordinationContextIF<W3CEndpointReference, Expires, CoordinationContextType.Identifier, CoordinationContextType> {
        final static JAXBRIContext jaxbContext  = getCoordinationContextJaxbContext();
        private static JAXBRIContext getCoordinationContextJaxbContext() {
            try {
                return (JAXBRIContext)JAXBRIContext.newInstance(CoordinationContext.class);
            } catch (JAXBException e) {
                throw new WebServiceException("Error creating JAXBContext for CoordinationContext. ", e);
            }
        }
        public CoordinationContextImpl(CoordinationContext delegate) {
            super(delegate);
        }

        public List<Object> getAny() {
            return getDelegate().getAny();  //To change body of implemented methods use File | Settings | File Templates.
        }

        public JAXBRIContext getJAXBRIContext() {
            return jaxbContext;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public CoordinationContext getDelegate() {
            return (CoordinationContext) super.getDelegate();    //To change body of overridden methods use File | Settings | File Templates.
        }
    }


    public static class RegisterTypeImpl extends BaseRegisterType<W3CEndpointReference, RegisterType> {

        RegisterTypeImpl(RegisterType delegate) {
            super(delegate);
        }

        @Override
        public String getProtocolIdentifier() {
            return delegate.getProtocolIdentifier();
        }

        @Override
        public void setProtocolIdentifier(String value) {
            delegate.setProtocolIdentifier(value);
        }

        @Override
        public W3CEndpointReference getParticipantProtocolService() {
            return delegate.getParticipantProtocolService();
        }

        @Override
        public void setParticipantProtocolService(W3CEndpointReference value) {
            delegate.setParticipantProtocolService(value);
        }

        @Override
        public List<Object> getAny() {
            return delegate.getAny();
        }

        @Override
        public Map<QName, String> getOtherAttributes() {
            return delegate.getOtherAttributes();
        }

        @Override
        public boolean isDurable() {
            return WSATConstants.WSAT11_DURABLE_2PC.equals(delegate.getProtocolIdentifier());
        }

        @Override
        public boolean isVolatile() {
            return WSATConstants.WSAT11_DURABLE_2PC.equals(delegate.getProtocolIdentifier());
        }
    }

    static class RegisterResponseTypeImpl extends BaseRegisterResponseType<W3CEndpointReference, RegisterResponseType> {

        RegisterResponseTypeImpl(RegisterResponseType delegate) {
            super(delegate);
        }


        @Override
        public W3CEndpointReference getCoordinatorProtocolService() {
            return delegate.getCoordinatorProtocolService();
        }

        @Override
        public void setCoordinatorProtocolService(W3CEndpointReference value) {
            delegate.setCoordinatorProtocolService(value);
        }

        @Override
        public List<Object> getAny() {
            return delegate.getAny();
        }

        @Override
        public Map<QName, String> getOtherAttributes() {
            return delegate.getOtherAttributes();
        }

        @Override
        public RegisterResponseType getDelegate() {
            return delegate;
        }
    }
}
