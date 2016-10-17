/*
 * Copyright 2016 Bud Byrd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.budjb.httprequests.v2.core.converter

import com.budjb.httprequests.v2.core.entity.ContentType
import com.budjb.httprequests.v2.core.entity.HttpEntity
import com.budjb.httprequests.v2.core.entity.InputStreamHttpEntity
import com.budjb.httprequests.v2.core.exception.UnsupportedConversionException
import groovy.util.logging.Slf4j

/**
 * A container class for {@link EntityConverter} instances. Handles registration
 * and conversion.
 */
@Slf4j
class EntityConverterManager {
    /**
     * List of registered entity converters.
     */
    private final List<EntityConverter> converters

    /**
     * Base constructor.
     */
    EntityConverterManager() {
        converters = []
    }

    /**
     * Creates a converter manager with the contents of another manager.
     *
     * @param other Other converter manager to make a copy of.
     */
    EntityConverterManager(EntityConverterManager other) {
        converters = []

        other.getAll().each {
            add(it)
        }
    }

    /**
     * Adds an entity converter to the manager.
     *
     * @param converter Converter to add to the manager.
     */
    void add(EntityConverter converter) {
        if (!converters.find { it.getClass() == converter.getClass() }) {
            converters.add(converter)
        }
    }

    /**
     * Returns the list of entity converters.
     *
     * @return List of entity converters.
     */
    List<EntityConverter> getAll() {
        return converters
    }

    /**
     * Remove an entity converter.
     *
     * @param converter Entity converter to remove.
     */
    void remove(EntityConverter converter) {
        converters.remove(converter)
    }

    /**
     * Remove all entity converters.
     */
    void clear() {
        converters.clear()
    }

    /**
     * Returns the list of all registered entity readers.
     *
     * @return
     */
    List<EntityReader> getEntityReaders() {
        return converters.findAll { it instanceof EntityReader } as List<EntityReader>
    }

    /**
     * Returns the list of all registered entity writers.
     *
     * @return
     */
    List<EntityWriter> getEntityWriters() {
        return converters.findAll { it instanceof EntityWriter } as List<EntityWriter>
    }

    /**
     * Writes the given object with the optionally given content type to an {@link HttpEntity}
     * containing the entity as an {@link InputStream}.
     *
     * @param object Object to convert.
     * @return the entity with the body contained in an {@link InputStream}.
     * @throws UnsupportedConversionException
     */
    HttpEntity write(Object object) throws UnsupportedConversionException {
        return write(object, null)
    }

    /**
     * Writes the given object with the optionally given content type to an {@link HttpEntity}
     * containing the entity as an {@link InputStream}.
     *
     * @param object Object to convert.
     * @param contentType Content-Type of the body (optional).
     * @return the entity with the body contained in an {@link InputStream}.
     * @throws UnsupportedConversionException
     */
    HttpEntity write(Object object, ContentType contentType) throws UnsupportedConversionException {
        Class<?> type = object.getClass()

        List<EntityWriter> writerCandidates = []
        if (contentType) {
            writerCandidates.addAll(getEntityWriters().findAll { it.supports(type) && it.supports(contentType) })
        }
        writerCandidates.addAll(getEntityWriters().findAll { it.supports(type) })
        writerCandidates = writerCandidates.unique()

        for (EntityWriter writer : writerCandidates) {
            try {
                InputStream inputStream = writer.write(object, null)

                if (inputStream == null) {
                    continue
                }

                if (contentType != null) {
                    return new InputStreamHttpEntity(inputStream, contentType)
                }
                else if (writer.getDefaultContentType()) {
                    return new InputStreamHttpEntity(inputStream, writer.getDefaultContentType())
                }
                else {
                    return new InputStreamHttpEntity(inputStream)
                }
            }
            catch (Exception e) {
                log.trace("error occurred during conversion with EntityWriter ${writer.getClass()}", e)
            }

        }

        throw new UnsupportedConversionException(type)
    }

    /**
     * Reads an object from the given {@link InputStreamHttpEntity}.
     *
     * Due to this method attempting conversion possibly more than once, the entity will be buffered.
     *
     * @param type Object type to attempt conversion to.
     * @param entity Entity input stream.
     * @return The converted object.
     * @throws UnsupportedConversionException when there are no entity writers that support the object type.
     */
    public <T> T read(Class<?> type, InputStreamHttpEntity entity) throws UnsupportedConversionException, IOException {
        entity.buffer()

        InputStream inputStream = entity.getInputStream()
        ContentType contentType = entity.getContentType()

        List<EntityReader> readerCandidates = []
        if (contentType) {
            readerCandidates.addAll(getEntityReaders().findAll { it.supports(type) && it.supports(contentType) })
        }
        readerCandidates.addAll(getEntityReaders().findAll { it.supports(type) })
        readerCandidates = readerCandidates.unique()

        for (EntityReader reader : readerCandidates) {
            try {
                T object = reader.read(inputStream, contentType) as T

                if (object != null) {
                    return object
                }
            }
            catch (Exception e) {
                log.trace("error occurred during conversion with EntityReader ${reader.getClass()}", e)
            }
        }

        throw new UnsupportedConversionException(type)
    }
}
