package com.budjb.httprequests.core.entity

import com.budjb.httprequests.core.ContentType

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
     * Sets whether the entity will be buffered when it is written.
     *
     * @param buffered
     */
    void setBuffered(boolean buffered)

    /**
     * Returns the {@link InputStream} containing the entity.
     *
     * @return
     */
    InputStream getInputStream()

    /**
     * Resets the entity if it is buffered.
     */
    void reset()

    /**
     * Closes the entity.
     */
    void close()
}
