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

import java.nio.charset.Charset

/**
 * A class that represents a MIME type and allows easy modification and property retrieval.
 */
class ContentType {
    /**
     * JSON.
     */
    static final ContentType APPLICATION_JSON = new ContentType('application/json')

    /**
     * Plain text.
     */
    static final ContentType TEXT_PLAIN = new ContentType('text/plain')

    /**
     * XML.
     */
    static final ContentType TEXT_XML = new ContentType('text/xml')

    /**
     * Binary.
     */
    static final ContentType APPLICATION_OCTET_STREAM = new ContentType('application/octet-stream')

    /**
     * Form URL Encoded.
     */
    static final ContentType APPLICATION_X_WWW_FORM_URLENCODED = new ContentType('application/x-www-form-urlencoded')

    /**
     * Default Content-Type for HTTP requests (application/octet-stream).
     */
    static final ContentType DEFAULT_CONTENT_TYPE = APPLICATION_OCTET_STREAM

    /**
     * System default character set.
     */
    static final String DEFAULT_SYSTEM_CHARACTER_SET = Charset.defaultCharset().toString()

    /**
     * HTTP default character set.
     */
    static final String DEFAULT_HTTP_CHARACTER_SET = 'UTF-8'

    /**
     * Type of the MIME type.
     */
    String type

    /**
     * Subtype of the MIME type.
     */
    String subType

    /**
     * Other parameters of the MIME type.
     */
    Map<String, String> parameters = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER)

    /**
     * Empty constructor.
     */
    ContentType() {
        void
    }

    /**
     * Constructor.
     *
     * @param contentType Content-Type to parse and populate the object with.
     */
    ContentType(String contentType) {
        if (!contentType) {
            type = DEFAULT_CONTENT_TYPE.type
            subType = DEFAULT_CONTENT_TYPE.subType
            return
        }

        String type = contentType
        int paramIndex = contentType.indexOf(';')
        if (paramIndex > 0) {
            type = contentType.substring(0, paramIndex)
        }

        List<String> typeParts = type.tokenize('/')
        if (typeParts.size() != 2) {
            throw new IllegalArgumentException('A Content-Type must follow the format of type/subtype')
        }

        this.type = typeParts[0]
        this.subType = typeParts[1]

        if (paramIndex > 0) {
            contentType.substring(paramIndex).tokenize(';').each {
                List<String> paramParts = it.tokenize('=')
                if (paramParts.size() != 2) {
                    return
                }
                String key = paramParts[0].trim()
                String value = paramParts[1].trim()
                if (key == 'charset') {
                    charset = value
                }
                else {
                    parameters.put(key, value)
                }
            }
        }
    }

    /**
     * Returns the full MIME string.
     *
     * @return Full MIME string.
     */
    @Override
    String toString() {
        StringBuilder stringBuilder = new StringBuilder()

        stringBuilder.append(type).append('/').append(subType)

        if (charset) {
            stringBuilder.append(';charset=').append(charset)
        }

        parameters.each { key, value ->
            stringBuilder.append(';').append(key).append('=').append(value)
        }

        return stringBuilder.toString()
    }

    /**
     * Returns the character set of the MIME type.
     *
     * @return The character set of the MIME type.
     */
    String getCharset() {
        return getParameter('charset') ?: DEFAULT_HTTP_CHARACTER_SET
    }

    /**
     * Sets the character set of the MIME type.
     *
     * @param charset
     */
    void setCharset(String charset) {
        setParameter('charset', charset)
    }

    /**
     * Sets the parameter with the given name with the given value.
     *
     * @param name Name of the parameter.
     * @param value Value of the parameter.
     */
    void setParameter(String name, String value) {
        parameters.put(name, value)
    }

    /**
     * Returns the value of the parameter with the given name.
     *
     * @param name Name of the parameter.
     * @return Value fo the parameter, or <code>null</code>.
     */
    String getParameter(String name) {
        if (parameters.containsKey(name)) {
            return parameters.get(name)
        }
        return null
    }

    /**
     * Returns the portion of the Content-Type without any parameters.
     *
     * @return The portion of the Content-Type without any parameters.
     */
    String getFullType() {
        return "${type}/${subType}"
    }
}
