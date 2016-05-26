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
package com.budjb.httprequests

import com.budjb.httprequests.converter.EntityConverterManager
import com.budjb.httprequests.exception.UnsupportedConversionException

/**
 * An object that represents the response of an HTTP request.
 */
abstract class HttpResponse implements Closeable {
    /**
     * The HTTP status of the response.
     */
    int status

    /**
     * Headers of the response.
     */
    Map<String, List<String>> headers = new TreeMap<String, List<String>>(String.CASE_INSENSITIVE_ORDER)

    /**
     * A list of allowed HTTP methods. Typically returned from OPTIONS requests.
     */
    List<HttpMethod> allow = []

    /**
     * Response entity.
     */
    HttpEntity entity

    /**
     * Request properties used to configure the request that generated this response.
     */
    HttpRequest request

    /**
     * Converter manager.
     */
    EntityConverterManager converterManager

    /**
     * Constructor.
     *
     * @param request Request properties used to make the request.
     * @param converterManager Converter manager.
     */
    HttpResponse(HttpRequest request, EntityConverterManager converterManager) {
        this.request = request
        this.converterManager = converterManager
    }

    /**
     * Sets the response headers from the given map.
     *
     * @param headers Response headers of the request.
     */
    void setHeaders(Map<String, ?> headers) {
        this.headers.clear()

        headers.each { name, values ->
            if (name) {
                setHeader(name, values)
            }
        }
    }

    /**
     * Sets the response header with the given name, parsing out individual values.
     *
     * @param name Name of the header.
     * @param value Value(s) of the header.
     */
    void setHeader(String name, Object value) {
        if (!headers.containsKey(name)) {
            headers.put(name, [])
        }

        if (value instanceof Collection) {
            value.each {
                headers.get(name).add(it.toString())
            }
        }
        else {
            value.toString().split(/,\s*/).each {
                headers.get(name).add(it.toString())
            }
        }
    }

    /**
     * Returns the first value of the header with the given name, or null if it doesn't exist.
     *
     * @param name Name of the header.
     * @return The first value of the requested header, or null if it doesn't exist.
     */
    String getHeader(String name) {
        if (headers.containsKey(name)) {
            return headers.get(name).first()
        }
        return null
    }

    /**
     * Returns a list of values of the header with the given name, or null if the header doesn't exist.
     *
     * @param name Name of the header.
     * @return A list of values of the requested header, or null if it doesn't exist.
     */
    List<String> getHeaders(String name) {
        if (headers.containsKey(name)) {
            return headers.get(name)
        }
        return null
    }

    /**
     * Return all response headers.
     *
     * This method returns a map where the key is the name of the header, and the value is a list of values
     * for the response header. This is true even when there is only one value for the header.
     *
     * @return A copy of the map containing the response headers.
     */
    Map<String, List<String>> getHeaders() {
        return headers.clone() as Map<String, List<String>>
    }

    /**
     * Return all response headers.
     *
     * This method returns a map where the key is the name of the header, and the value is either the single value
     * for that header or a list of values if multiple were received.
     *
     * @return A copy of the map containing the response headers.
     */
    Map<String, Object> getFlattenedHeaders() {
        return headers.collectEntries { name, values ->
            if (values.size() == 1) {
                return [(name): values[0]]
            }
            else {
                return [(name): values]
            }
        } as Map<String, Object>
    }

    /**
     * Returns the entity, converted to the given class type.
     *
     * @param type Class type to convert the entity to.
     * @return The converted entity.
     * @throws UnsupportedConversionException when no converter is found to convert the entity.
     */
    public <T> T getEntity(Class<T> type) throws UnsupportedConversionException, IOException {
        HttpEntity entity = getEntity()
        T object = converterManager.read(type, entity)
        entity.close()

        return object
    }

    /**
     * Closes the entity and releases any system resources associated
     * with it. If the response is already closed then invoking this
     * method has no effect.
     *
     * @throws IOException if an I/O error occurs
     */
    @Override
    void close() throws IOException {
        if (entity != null) {
            entity.close()
        }
    }

    /**
     * Returns whether the response contains an entity.
     *
     * @return Whether the response contains an entity.
     */
    boolean hasEntity() {
        return entity != null
    }

    /**
     * Returns the allowed HTTP methods returned in the response.
     *
     * @return Allowed HTTP methods returned in the response.
     */
    List<HttpMethod> getAllow() {
        if (!allow && headers.containsKey('Allow')) {
            allow = headers.get('Allow').collect { HttpMethod.valueOf(it) }
        }

        return allow
    }

    /**
     * Add a value to the given header name.
     *
     * @param key Name of the value.
     * @param value Value of the header.
     */
    void addHeader(String key, String value) {
        if (!headers.containsKey(key)) {
            headers.put(key, [])
        }
        headers.get(key).add(value)
    }

    /**
     * Sets the entity in the response.
     *
     * @param entity Entity contained the response.
     */
    void setEntity(HttpEntity entity) {
        if (!entity) {
            return
        }

        if (request.isBufferResponseEntity()) {
            entity.buffer()
        }

        this.entity = entity
    }

    /**
     * Returns an {@link InputStream} iff the given input stream is not <code>null</code>
     * and it has at least one byte to read.
     *
     * @param inputStream Input stream to check.
     * @return The passed-in input stream if there are bytes to read.
     */
    protected InputStream getNonEmptyInputStream(InputStream inputStream) {
        if (!inputStream) {
            return null
        }

        int read = inputStream.read()

        if (read == -1) {
            return null
        }

        inputStream = new PushbackInputStream(inputStream)
        inputStream.unread(read)

        return inputStream
    }
}
