package com.budjb.httprequests.core

import com.budjb.httprequests.StreamUtils

/**
 * A container for an HTTP entity.
 */
class HttpEntity {
    /**
     * Content-Type of the entity.
     */
    ContentType contentType

    /**
     * An{@link InputStream} containing the entity content.
     *
     * @return
     */
    InputStream inputStream

    /**
     * Constructs an entity with the "application/octet-stream" Content-Type.
     */
    HttpEntity(InputStream inputStream) {
        this(inputStream, ContentType.APPLICATION_OCTET_STREAM)
    }

    /**
     * Constructs the entity with the given Content-Type.
     *
     * @param contentType
     */
    HttpEntity(InputStream inputStream, String contentType) {
        this(inputStream, ContentType.parse(contentType))
    }

    /**
     * Constructs the entity with the given Content-Type.
     *
     * @param contentType
     */
    HttpEntity(InputStream inputStream, ContentType contentType) {
        this.inputStream = inputStream
        this.contentType = contentType
    }

    /**
     * Buffers the {@link InputStream}.
     */
    void buffer() {
        if (!(getInputStream() instanceof ByteArrayInputStream)) {
            setInputStream(new ByteArrayInputStream(StreamUtils.readBytes(inputStream)))
        }
    }

    /**
     * Returns whether the {@link InputStream} is buffered.
     *
     * @return
     */
    boolean isBuffered() {
        return inputStream instanceof ByteArrayInputStream
    }

    /**
     * Resets the buffered {@link InputStream}.
     */
    void reset() {
        if (!isBuffered()) {
            throw new IllegalStateException("can not reset the input stream since it is not buffered")
        }
    }
}
