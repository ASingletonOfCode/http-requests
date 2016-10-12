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
package com.budjb.httprequests.v2.converter

import com.budjb.httprequests.v2.core.entity.EntityInputStream
import com.budjb.httprequests.v2.core.entity.HttpEntity
import com.budjb.httprequests.v2.exception.UnsupportedConversionException
import groovy.util.logging.Slf4j

@Slf4j
class EntityConverterManager {
    /**
     * Default character set.
     */
    final static String DEFAULT_CHARSET = 'ISO-8859-1'

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
     * TODO: do I want to keep this this way?
     * @param entity
     * @param object
     * @return
     */
    InputStream convertHttpEntity(HttpEntity entity, Object object) {
        Class<?> type = object.getClass()

        // TODO: where do I want to handle setting defaults?
        // TODO: need to make sure the character set persists through to the
        // TODO: ultimate Content-Type
        String characterSet = entity.getContentType()?.getCharset() ?: 'UTF-8'

        for (EntityWriter writer : getEntityWriters()) {
            if (writer.supports(type)) {
                try {
                    InputStream inputStream = writer.write(object, characterSet)

                    if (inputStream == null) {
                        continue
                    }

                    if (entity.getContentType() == null) {
                        String contentType = writer.getContentType()
                        if (contentType) {
                            log.trace("applying Content-Type '${contentType}' to the request")
                            entity.setContentType(contentType)
                        }
                    }
                    return inputStream
                }
                catch (Exception e) {
                    log.trace("error occurred during conversion with EntityWriter ${writer.getClass()}", e)
                }
            }
        }

        throw new UnsupportedConversionException(type)

    }

    /**
     * Reads an object from the given entity {@link InputStream}.
     *
     * @param type Object type to attempt conversion to.
     * @param entity Entity input stream.
     * @param contentType Content Type of the entity.
     * @param charset Character set of the entity.
     * @return The converted object.
     * @throws UnsupportedConversionException when there are no entity writers that support the object type.
     */
    public <T> T read(Class<?> type, InputStream entity, String contentType, String charset) throws UnsupportedConversionException, IOException {
        if (entity instanceof EntityInputStream && entity.isClosed()) {
            throw new IOException("entity stream is closed")
        }

        for (EntityReader reader : getEntityReaders()) {
            if (reader.supports(type)) {
                try {
                    T object = reader.read(entity, contentType, charset) as T

                    if (object != null) {
                        return object
                    }
                }
                catch (Exception e) {
                    log.trace("error occurred during conversion with EntityReader ${reader.getClass()}", e)
                }
            }
        }

        throw new UnsupportedConversionException(type)
    }
}
