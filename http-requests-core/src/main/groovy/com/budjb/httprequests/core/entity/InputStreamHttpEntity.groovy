package com.budjb.httprequests.core.entity

import com.budjb.httprequests.core.ContentType

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
        setContentType(contentType)
    }

    /**
     * Builds an {@link HttpEntity} with the given {@link InputStream} and Content-Type.
     *
     * @param inputStream
     * @param contentType
     */
    InputStreamHttpEntity(InputStream inputStream, String contentType) {
        setInputStream(inputStream)
        setContentType(contentType)
    }
}
