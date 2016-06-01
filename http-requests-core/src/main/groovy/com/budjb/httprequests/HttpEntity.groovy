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

/**
 * An object that represents a distinct request entity contained in an {@link InputStream},
 * with the accompanying <i>Content-Type</i>.
 */
class HttpEntity implements Closeable {
    /**
     * InputStream containing the request entity.
     */
    InputStream inputStream

    /**
     * Content-Type of the entity.
     */
    ContentType contentType

    /**
     * Entity buffer.
     */
    private byte[] buffer

    /**
     * Constructor.
     *
     * @param inputStream An {@link InputStream} containing the entity.
     */
    HttpEntity(InputStream inputStream) {
        this(inputStream, ContentType.DEFAULT_CONTENT_TYPE)
    }

    /**
     * Constructor.
     *
     * @param inputStream An {@link InputStream} containing the entity.
     * @param contentType Content-Type of the entity.
     */
    HttpEntity(InputStream inputStream, ContentType contentType) {
        if (inputStream == null) {
            throw new IllegalArgumentException("input stream must not be null")
        }

        this.inputStream = new EntityInputStream(inputStream)

        if (!contentType) {
            contentType = ContentType.DEFAULT_CONTENT_TYPE
        }
        this.contentType = contentType

    }

    /**
     * Returns whether the entity is buffered.
     *
     * @return Whether the entity is buffered.
     */
    boolean isBuffered() {
        return buffer != null
    }

    /**
     * Buffers the {@link InputStream} into a {@link ByteArrayInputStream} so that it can be replayed.
     */
    void buffer() {
        if (!isBuffered()) {
            buffer = StreamUtils.readBytes(inputStream)
            inputStream.close()
            inputStream = null
        }
    }

    /**
     * Returns the {@link InputStream} containing the entity.
     *
     * @return The {@link InputStream} containing the entity.
     */
    InputStream getInputStream() {
        if (buffer) {
            return new ByteArrayInputStream(buffer)
        }
        else {
            return inputStream
        }
    }

    /**
     * Closes this stream and releases any system resources associated
     * with it. If the stream is already closed then invoking this
     * method has no effect.
     *
     * <p> As noted in {@link AutoCloseable#close()}, cases where the
     * close may fail require careful attention. It is strongly advised
     * to relinquish the underlying resources and to internally
     * <em>mark</em> the {@code Closeable} as closed, prior to throwing
     * the {@code IOException}.
     *
     * @throws IOException if an I/O error occurs
     */
    @Override
    void close() throws IOException {
        if (inputStream) {
            inputStream.close()
        }
    }
}
