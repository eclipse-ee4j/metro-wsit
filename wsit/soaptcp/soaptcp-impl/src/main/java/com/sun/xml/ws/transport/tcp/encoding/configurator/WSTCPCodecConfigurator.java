/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.transport.tcp.encoding.configurator;

/**
 * SOAP/TCP code configurator
 *
 * @author Alexey Stashok
 */
public enum WSTCPCodecConfigurator {
    INSTANCE;

    private static final int MIN_INDEXED_STRING_SIZE_LIMIT = 0;
    private static final int MAX_INDEXED_STRING_SIZE_LIMIT = 32;
    private static final int INDEXED_STRING_MEMORY_LIMIT = 4 * 1024 * 1024; //4M limit


    private DocumentParserFactory documentParserFactory = new DefaultDocumentParserFactory();
    private DocumentSerializerFactory documentSerializerFactory = new DefaultDocumentSerializerFactory();

    private ParserVocabularyFactory parserVocabularyFactory = new DefaultParserVocabularyFactory();
    private SerializerVocabularyFactory serializerVocabularyFactory = new DefaultSerializerVocabularyFactory();

    private int minAttributeValueSize = MIN_INDEXED_STRING_SIZE_LIMIT;
    private int maxAttributeValueSize = MAX_INDEXED_STRING_SIZE_LIMIT;
    private int minCharacterContentChunkSize = MIN_INDEXED_STRING_SIZE_LIMIT;
    private int maxCharacterContentChunkSize = MAX_INDEXED_STRING_SIZE_LIMIT;
    private int attributeValueMapMemoryLimit = INDEXED_STRING_MEMORY_LIMIT;
    private int characterContentChunkMapMemoryLimit = INDEXED_STRING_MEMORY_LIMIT;

    /**
     * Get the {@link DocumentParserFactory}
     * @return {@link DocumentParserFactory}
     */
    public DocumentParserFactory getDocumentParserFactory() {
        return documentParserFactory;
    }

    /**
     * Set the {@link DocumentParserFactory}
     * @param documentParserFactory {@link DocumentParserFactory}
     */
    public void setDocumentParserFactory(DocumentParserFactory documentParserFactory) {
        this.documentParserFactory = documentParserFactory;
    }

    /**
     * Get the {@link DocumentSerializerFactory}
     * @return {@link DocumentSerializerFactory}
     */
    public DocumentSerializerFactory getDocumentSerializerFactory() {
        return documentSerializerFactory;
    }

    /**
     * Set the {@link DocumentSerializerFactory}
     * @param documentSerializerFactory {@link DocumentSerializerFactory}
     */
    public void setDocumentSerializerFactory(DocumentSerializerFactory documentSerializerFactory) {
        this.documentSerializerFactory = documentSerializerFactory;
    }

    /**
     * Get the {@link ParserVocabularyFactory}
     * @return {@link ParserVocabularyFactory}
     */
    public ParserVocabularyFactory getParserVocabularyFactory() {
        return parserVocabularyFactory;
    }

    /**
     * Set the {@link ParserVocabularyFactory}
     * @param parserVocabularyFactory {@link ParserVocabularyFactory}
     */
    public void setParserVocabularyFactory(ParserVocabularyFactory parserVocabularyFactory) {
        this.parserVocabularyFactory = parserVocabularyFactory;
    }

    /**
     * Get the {@link SerializerVocabularyFactory}
     * @return {@link SerializerVocabularyFactory}
     */
    public SerializerVocabularyFactory getSerializerVocabularyFactory() {
        return serializerVocabularyFactory;
    }

    /**
     * Set the {@link SerializerVocabularyFactory}
     * @param serializerVocabularyFactory {@link SerializerVocabularyFactory}
     */
    public void setSerializerVocabularyFactory(SerializerVocabularyFactory serializerVocabularyFactory) {
        this.serializerVocabularyFactory = serializerVocabularyFactory;
    }


    /**
     * Gets the minimum size of attribute values
     * that will be indexed.
     *
     * @return The minimum attribute values size.
     */
    public int getMinAttributeValueSize() {
        return minAttributeValueSize;
    }

    /**
     * Sets the minimum size of attribute values
     * that will be indexed.
     *
     * @param minAttributeValueSize the minimum attribute values size.
     */
    public void setMinAttributeValueSize(int minAttributeValueSize) {
        this.minAttributeValueSize = minAttributeValueSize;
    }

    /**
     * Gets the maximum size of attribute values
     * that will be indexed.
     *
     * @return The maximum attribute values size.
     */
    public int getMaxAttributeValueSize() {
        return maxAttributeValueSize;
    }

    /**
     * Sets the maximum size of attribute values
     * that will be indexed.
     *
     * @param maxAttributeValueSize the maximum attribute values size.
     */
    public void setMaxAttributeValueSize(int maxAttributeValueSize) {
        this.maxAttributeValueSize = maxAttributeValueSize;
    }

    /**
     * Gets the limit on the memory size of Map of attribute values
     * that will be indexed.
     *
     * @return The attribute value size limit.
     */
    public int getAttributeValueMapMemoryLimit() {
        return attributeValueMapMemoryLimit;
    }

    /**
     * Sets the limit on the memory size of Map of attribute values
     * that will be indexed.
     *
     * @param attributeValueMapMemoryLimit The attribute value size limit. Any value less
     * that a length of size limit will be indexed.
     */
    public void setAttributeValueMapMemoryLimit(int attributeValueMapMemoryLimit) {
        this.attributeValueMapMemoryLimit = attributeValueMapMemoryLimit;
    }

    /**
     * Gets the minimum size of character content chunks
     * that will be indexed.
     *
     * @return The minimum character content chunk size.
     */
    public int getMinCharacterContentChunkSize() {
        return minCharacterContentChunkSize;
    }

    /**
     * Sets the minimum size of character content chunks
     * that will be indexed.
     *
     * @param minCharacterContentChunkSize the minimum character content chunk size.
     */
    public void setMinCharacterContentChunkSize(int minCharacterContentChunkSize) {
        this.minCharacterContentChunkSize = minCharacterContentChunkSize;
    }

    /**
     * Gets the maximum size of character content chunks
     * that will be indexed.
     *
     * @return The maximum character content chunk size.
     */
    public int getMaxCharacterContentChunkSize() {
        return maxCharacterContentChunkSize;
    }

    /**
     * Sets the maximum size of character content chunks
     * that will be indexed.
     *
     * @param maxCharacterContentChunkSize the maximum character content chunk size.
     */
    public void setMaxCharacterContentChunkSize(int maxCharacterContentChunkSize) {
        this.maxCharacterContentChunkSize = maxCharacterContentChunkSize;
    }

    /**
     * Gets the limit on the memory size of Map of attribute values
     * that will be indexed.
     *
     * @return The attribute value size limit.
     */
    public int getCharacterContentChunkMapMemoryLimit() {
        return characterContentChunkMapMemoryLimit;
    }

    /**
     * Sets the limit on the memory size of Map of attribute values
     * that will be indexed.
     *
     * @param characterContentChunkMapMemoryLimit The attribute value size limit. Any value less
     * that a length of size limit will be indexed.
     */
    public void setCharacterContentChunkMapMemoryLimit(int characterContentChunkMapMemoryLimit) {
        this.characterContentChunkMapMemoryLimit = characterContentChunkMapMemoryLimit;
    }
}
