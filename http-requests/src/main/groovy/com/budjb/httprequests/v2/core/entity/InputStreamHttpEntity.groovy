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
package com.budjb.httprequests.v2.core.entity

/**
 * An {@link HttpEntity} containing an {@link InputStream}.
 */
class InputStreamHttpEntity extends AbstractHttpEntity {
    /**
     * Builds an {@link HttpEntity} with the given {@link InputStream}.
     *
     * @param inputStream
     */
    InputStreamHttpEntity(InputStream inputStream) {
        this(inputStream, ContentType.APPLICATION_OCTET_STREAM)
    }

    /**
     * Builds an {@link HttpEntity} with the given {@link InputStream} and Content-Type.
     *
     * @param inputStream
     * @param contentType
     */
    InputStreamHttpEntity(InputStream inputStream, ContentType contentType) {
        setInputStream(inputStream)
        setContentType((ContentType) contentType)
    }

    /**
     * Builds an {@link HttpEntity} with the given {@link InputStream} and Content-Type.
     *
     * @param inputStream
     * @param contentType
     */
    InputStreamHttpEntity(InputStream inputStream, String contentType) {
        setInputStream(inputStream)
        setContentType((String) contentType)
    }
}
