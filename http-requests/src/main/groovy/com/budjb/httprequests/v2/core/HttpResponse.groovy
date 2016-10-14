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
package com.budjb.httprequests.v2.core

import com.budjb.httprequests.v2.core.converter.EntityConverterManager
import com.budjb.httprequests.v2.core.entity.ContentType
import com.budjb.httprequests.v2.core.entity.HttpEntity
import com.budjb.httprequests.v2.core.entity.InputStreamHttpEntity
import com.budjb.httprequests.v2.core.exception.UnsupportedConversionException

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
    InputStreamHttpEntity entity

    /**
     * Converter manager.
     */
    protected EntityConverterManager converterManager

    /**
     * Constructor.
     *
     * @param request Request properties used to make the request.
     * @param converterManager Converter manager.
     */
    HttpResponse(EntityConverterManager converterManager) {
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
    protected void setHeader(String name, Object value) {
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
     * Sets the entity.
     *
     * @param inputStream Input stream containing the entity.
     * @param contentType Content-Type of the entity.
     */
    void setEntity(InputStream inputStream, String contentType) {
        setEntity(inputStream, new ContentType(contentType))
    }

    /**
     * Sets the entity.
     *
     * @param inputStream Input stream containing the entity.
     * @param contentType Content-Type of the entity.
     */
    void setEntity(InputStream inputStream, ContentType contentType) {
        entity = null

        if (inputStream == null) {
            return
        }

        PushbackInputStream pushBackInputStream = new PushbackInputStream(inputStream)
        int read = pushBackInputStream.read()
        if (read == -1) {
            pushBackInputStream.close()
            return
        }

        pushBackInputStream.unread(read)

        entity = new InputStreamHttpEntity(pushBackInputStream, (ContentType) contentType)
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
}
