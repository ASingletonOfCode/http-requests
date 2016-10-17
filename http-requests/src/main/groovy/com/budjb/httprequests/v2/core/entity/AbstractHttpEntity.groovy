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
