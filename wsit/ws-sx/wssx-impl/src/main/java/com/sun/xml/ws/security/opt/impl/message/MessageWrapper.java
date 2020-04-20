/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.opt.impl.message;

import com.sun.xml.ws.security.opt.api.SecurityElement;
import com.sun.xml.ws.spi.db.XMLBridge;
import com.sun.istack.NotNull;
import com.sun.xml.bind.api.Bridge;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.api.message.AddressingUtils;
import com.sun.xml.ws.api.message.AttachmentSet;
import com.sun.xml.ws.api.message.HeaderList;
import com.sun.xml.ws.api.message.MessageHeaders;
import java.util.List;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import com.sun.xml.stream.buffer.MutableXMLStreamBuffer;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.addressing.AddressingVersion;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPMessage;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;


/**
 *
 * @author K.Venugopal@sun.com
 */
public class MessageWrapper extends com.sun.xml.ws.api.message.Message{
    private boolean isOneWay = false;
    private SecuredMessage sm;
    private List headers;
    private MessageHeaders hl = new HeaderList();
    private MutableXMLStreamBuffer bufferedMsg = null;
    public MessageWrapper(SecuredMessage sm,boolean oneWay){
        this.sm = sm;
        this.isOneWay = oneWay;
        this.headers = sm.getHeaders();
        for(int i=0;i<headers.size();i++){
            Object obj = headers.get(i);
            if(obj instanceof com.sun.xml.ws.api.message.Header){
                hl.add((com.sun.xml.ws.api.message.Header)obj);
            }else{
                hl.add(new HeaderWrapper((SecurityElement)obj));
            }
        }
    }
    
    public MessageWrapper(MutableXMLStreamBuffer msg,boolean oneWay,MessageHeaders hdrs,SecuredMessage sm){
        this.bufferedMsg = msg;
        this.sm = sm;
        this.hl = hdrs;
        this.isOneWay = oneWay;        
    }
    
    /**
     * Returns true if headers are present in the message.
     *
     * @return
     *      true if headers are present.
     */
    public boolean hasHeaders(){
        // FIXME: RJE -- remove cast when MessageHeaders supports hasHeaders()
        return (((HeaderList)hl).size() > 0);
    }
    
    /**
     * Gets all the headers of this message.
     *
     * <h3>Implementation Note</h3>
     * <p>
     * Message implementation is allowed to defer
     * the construction of {@link MessageHeaders} object. So
     * if you only want to check for the existence of any header
     * element, use {@link #hasHeaders()}.
     *
     * @return
     *      always return the same non-null object.
     */
    public HeaderList getHeaders(){       
        // FIXME: remove cast
        return (HeaderList) hl;
    }
    
    /**
     * Gets the attachments of this message
     * (attachments live outside a message.)
     */
    public AttachmentSet getAttachments() {        
        return sm.getAttachments();
    }
    
    /**
     * Optimization hint for the derived class to check
     * if we may have some attachments.
     */
    protected boolean hasAttachments() {        
        return sm.getAttachments() !=null;
    }
    
    /**
     * Returns true if this message is a request message for a
     * one way operation according to the given WSDL. False otherwise.
     *
     * <p>
     * This method is functionally equivalent as doing
     * {@code getOperation(port).getOperation().isOneWay()}
     * (with proper null check and all.) But this method
     * can sometimes work faster than that (for example,
     * on the client side when used with SEI.)
     *
     * @param port
     *      Messages are always created under the context of
     *      one {@link WSDLPort} and they never go outside that context.
     *      Pass in that "governing" {@link WSDLPort} object here.
     *      We chose to receive this as a parameter instead of
     *      keeping {@link WSDLPort} in a message, just to save the storage.
     *
     *      <p>
     *      The implementation of this method involves caching the return
     *      value, so the behavior is undefined if multiple callers provide
     *      different {@link WSDLPort} objects, which is a bug of the caller.
     */
    public boolean isOneWay(@NotNull WSDLPort port) {
        return isOneWay;
    }
    
    
    
    
    /**
     * Gets the local name of the payload element.
     *
     * @return
     *      null if a Message doesn't have any payload.
     */
    public String getPayloadLocalPart() {
        return sm.getPayloadLocalPart();
    }

     /**
     * Returns true if this message is a fault.
     *
     * <p>
     * Just a convenience method built on {@link #getPayloadNamespaceURI()}
     * and {@link #getPayloadLocalPart()}.
     */
    public boolean isFault() {
        // TODO: is SOAP version a property of a Message?
        // or is it defined by external factors?
        // how do I compare?
        String localPart = getPayloadLocalPart();
        String action = null;
        if ("EncryptedData".equals(localPart)) {
            if (hl != null) {
                try {
                    action = AddressingUtils.getAction(hl, AddressingVersion.W3C, sm.getSOAPVersion());
                } catch (Exception e) {
                }
            }
            if (action != null && action.endsWith("addressing/fault")) {
                localPart = "Fault";
            }
        }
        if(localPart==null || !localPart.equals("Fault")) {
            return false;
        }

        String nsUri = getPayloadNamespaceURI();
        return nsUri.equals(SOAPVersion.SOAP_11.nsUri) || nsUri.equals(SOAPVersion.SOAP_12.nsUri);
    }
    
    /**
     * Gets the namespace URI of the payload element.
     *
     * @return
     *      null if a Message doesn't have any payload.
     */
    public String getPayloadNamespaceURI(){        
        return sm.getPayloadNamespaceURI();
    }
    // I'm not putting @Nullable on it because doing null check on getPayloadLocalPart() should be suffice
    
    /**
     * Returns true if a Message has a payload.
     *
     * <p>
     * A message without a payload is a SOAP message that looks like:
     * <pre>
     * &lt;xmp&gt;
     * &lt;S:Envelope&gt;
     *   &lt;S:Header&gt;
     *     ...
     *   &lt;/S:Header&gt;
     *   &lt;S:Body /&gt;
     * &lt;/S:Envelope&gt;
     * &lt;/xmp&gt;
     *</pre>
     */
    public boolean hasPayload(){        
        return true;
    }
    
    
    /**
     * Consumes this message including the envelope.
     * returns it as a {@link Source} object.
     */
    public Source readEnvelopeAsSource(){
        throw new UnsupportedOperationException();
    }
    
    
    /**
     * Returns the payload as a {@link Source} object.
     *
     * This consumes the message.
     *
     * @return
     *      if there's no payload, this method returns null.
     */
    public Source readPayloadAsSource(){
        throw new UnsupportedOperationException();
    }
    
    /**
     * Creates the equivalent {@link SOAPMessage} from this message.
     *
     * This consumes the message.
     *
     * @throws SOAPException
     *      if there's any error while creating a {@link SOAPMessage}.
     */
    public SOAPMessage readAsSOAPMessage() throws SOAPException {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Reads the payload as a JAXB object by using the given unmarshaller.
     *
     * This consumes the message.
     *
     * @throws JAXBException
     *      If JAXB reports an error during the processing.
     */
    public <T> T readPayloadAsJAXB(Unmarshaller unmarshaller) throws JAXBException{
        throw new UnsupportedOperationException();
    }
    
    /**
     * Reads the payload as a JAXB object according to the given {@link Bridge}.
     *
     * This consumes the message.
     *
     * @throws JAXBException
     *      If JAXB reports an error during the processing.
     */
    public <T> T readPayloadAsJAXB(Bridge<T> bridge) throws JAXBException{
        throw new UnsupportedOperationException();
    }
    
    
    
    
    /**
     * Reads the payload as a {@link XMLStreamReader}
     *
     * This consumes the message.
     *
     * @return
     *      If there's no payload, this method returns null.
     *      Otherwise always non-null valid {@link XMLStreamReader} that points to
     *      the payload tag name.
     */
    public XMLStreamReader readPayload() throws XMLStreamException{
        _check();
        return sm.readPayload();
    }
    
    /**
     * Writes the payload to StAX.
     *
     * This method writes just the payload of the message to the writer.
     * This consumes the message.
     * The implementation will not write
     * {@link XMLStreamWriter#writeStartDocument()}
     * nor
     * {@link XMLStreamWriter#writeEndDocument()}
     *
     * <p>
     * If there's no payload, this method is no-op.
     *
     * @throws XMLStreamException
     *      If the {@link XMLStreamWriter} reports an error,
     *      or some other errors happen during the processing.
     */
    public void writePayloadTo(XMLStreamWriter sw) throws XMLStreamException{
        _check();
        sm.writePayloadTo(sw);
    }
    
    /**
     * Writes the whole SOAP message (but not attachments)
     * to the given writer.
     *
     * This consumes the message.
     *
     * @throws XMLStreamException
     *      If the {@link XMLStreamWriter} reports an error,
     *      or some other errors happen during the processing.
     */
    public void writeTo(XMLStreamWriter sw) throws XMLStreamException{
        if(bufferedMsg != null){
            bufferedMsg.writeToXMLStreamWriter(sw);
            return;
        }
        sm.writeTo(sw);
    }
    
    /**
     * Writes the whole SOAP envelope as SAX events.
     *
     * <p>
     * This consumes the message.
     *
     * @param contentHandler
     *      must not be nulll.
     * @param errorHandler
     *      must not be null.
     *      any error encountered during the SAX event production must be
     *      first reported to this error handler. Fatal errors can be then
     *      thrown as SAXParseException. {@link SAXException}s thrown
     *      from {@link ErrorHandler} should propagate directly through this method.
     */
    public void writeTo( ContentHandler contentHandler, ErrorHandler errorHandler ) throws SAXException{
        throw new UnsupportedOperationException();
    }
    
    // TODO: do we need a method that reads payload as a fault?
    // do we want a separte streaming representation of fault?
    // or would SOAPFault in SAAJ do?
    
    
    
    /**
     * Creates a copy of a Message.
     *
     * <p>
     * This method creates a new Message whose header/payload/attachments/properties
     * are identical to this Message. Once created, the created Message
     * and the original Message behaves independently --- adding header/
     * attachment to one Message doesn't affect another Message
     * at all.
     *
     * <p>
     * This method does <b>NOT</b> consume a message.
     *
     * <p>
     * To enable efficient copy operations, there's a few restrictions on
     * how copied message can be used.
     *
     * <ol>
     *  <li>The original and the copy may not be
     *      used concurrently by two threads (this allows two Messages
     *      to share some internal resources, such as JAXB marshallers.)
     *      Note that it's OK for the original and the copy to be processed
     *      by two threads, as long as they are not concurrent.
     *
     *  <li>The copy has the same 'life scope'
     *      as the original (this allows shallower copy, such as
     *      JAXB beans wrapped in JAXBMessage.)
     * </ol>
     *
     * <p>
     * A 'life scope' of a message created during a message processing
     * in a pipeline is until a pipeline processes the next message.
     * A message cannot be kept beyond its life scope.
     *
     * (This experimental design is to allow message objects to be reused
     * --- feedback appreciated.)
     *
     *
     *
     * <h3>Design Rationale</h3>
     * <p>
     * Since a Message body is read-once, sometimes
     * (such as when you do fail-over, or WS-RM) you need to
     * create an idential copy of a Message.
     *
     * <p>
     * The actual copy operation depends on the layout
     * of the data in memory, hence it's best to be done by
     * the Message implementation itself.
     *
     * <p>
     * The restrictions placed on the use of copied Message can be
     * relaxed if necessary, but it will make the copy method more expensive.
     */
    // TODO: update the class javadoc with 'lifescope'
    // and move the discussion about life scope there.
    public MessageWrapper copy(){
        return this;
//        if(bufferedMsg == null){
//            try{
//                bufferedMsg = new com.sun.xml.stream.buffer.MutableXMLStreamBuffer();
//                javax.xml.stream.XMLStreamWriter writer = bufferedMsg.createFromXMLStreamWriter();
//                sm.writeTo(writer);                
//            } catch (XMLStreamException ex) {
//                java.util.logging.Logger.getLogger("global").log(java.util.logging.Level.SEVERE,
//                        ex.getMessage(),
//                        ex);
//            }
//        }
//        return new MessageWrapper(bufferedMsg,this.isOneWay,this.hl,this.sm);
    }
    
    private void _check(){
        if(bufferedMsg !=null){
            throw new UnsupportedOperationException("Message is buffered , only writeTo method is supported");
        }
    }

	public <T> T readPayloadAsJAXB(XMLBridge<T> arg0) throws JAXBException {
        throw new UnsupportedOperationException();
	}
}
