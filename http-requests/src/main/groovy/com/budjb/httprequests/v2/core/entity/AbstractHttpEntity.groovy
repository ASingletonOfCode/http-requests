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

import com.budjb.httprequests.v2.util.StreamUtils

/**
 * Base implementation of an {@link HttpEntity}.
 */
abstract class AbstractHttpEntity implements HttpEntity {
    /**
     * Content-Type of the entity.
     */
    ContentType contentType

    /**
     * Input stream containing the entity.
     */
    InputStream inputStream

    /**
     * Entity buffer.
     */
    private byte[] entityBuffer

    /**
     * {@inheritDoc}
     */
    @Override
    void setContentType(ContentType contentType) {
        this.contentType = contentType
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void setContentType(String contentType) {
        setContentType(new ContentType(contentType))
    }

    /**
     * Sets the input stream.
     *
     * @param inputStream
     */
    void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream

        if (isBuffered()) {
            entityBuffer = null
            buffer()
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    InputStream getInputStream() {
        if (!inputStream) {
            throw new IllegalStateException("HTTP entity has no input stream")
        }

        if (isBuffered()) {
            return new ByteArrayInputStream(entityBuffer)
        }

        return inputStream
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void close() {
        this.inputStream?.close()
    }

    /**
     * {@inheritDoc}
     */
    @Override
    boolean isBuffered() {
        return entityBuffer != null
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void buffer() {
        if (entityBuffer == null) {
            entityBuffer = StreamUtils.readBytes(inputStream)
            inputStream.close()
        }
    }
}
