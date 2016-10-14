package com.budjb.httprequests.v2.core.entity

import com.budjb.httprequests.v2.core.ContentType

/**
 * Defines an HTTP entity.
 */
interface HttpEntity {
    /**
     * Returns the Content-Type of the entity.
     *
     * @return
     */
    ContentType getContentType()

    /**
     * Sets the Content-Type of the entity.
     *
     * @param contentType
     */
    void setContentType(ContentType contentType)

    /**
     * Sets the Content-Type of the entity.
     *
     * @param contentType
     */
    void setContentType(String contentType)

    /**
     * Returns whether the entity will be buffered when it is written.
     *
     * @return
     */
    boolean isBuffered()

    /**
     * Buffers the source {@link InputStream} so that it can be read multiple times.
     ]     */
    void buffer()

    /**
     * Returns the {@link InputStream} containing the entity.
     *
     * If the entity is buffered, each call to this method will return a new
     * instance of {@link ByteArrayInputStream}.
     *
     * @return
     */
    InputStream getInputStream()

    /**
     * Closes the entity.
     */
    void close()
}
