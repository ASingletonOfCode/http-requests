package com.budjb.httprequests.v2.core.entity

import com.budjb.httprequests.v2.StreamUtils
import com.budjb.httprequests.v2.core.ContentType

/**
 * Base implementation of an {@link HttpEntity}.
 */
abstract class AbstractHttpEntity implements HttpEntity {
    /**
     * Content-Type of the entity.
     */
    ContentType contentType

    /**
     * Whether the entity should be buffered. This is useful for
     * retransmission.
     */
    boolean buffered = false

    /**
     * Input stream containing the entity.
     */
    InputStream inputStream

    /**
     * Entity buffer.
     */
    private ByteArrayInputStream buffer

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
        setContentType(ContentType.parse(contentType))
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
            if (buffer == null) {
                buffer = new ByteArrayInputStream(StreamUtils.readBytes(inputStream))
            }
            return buffer
        }
        return inputStream
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void reset() {
        if (isBuffered() && buffer != null) {
            buffer.reset()
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void close() {
        getInputStream().close()
    }
}
