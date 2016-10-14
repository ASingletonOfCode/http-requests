package com.budjb.httprequests.v2.core.entity

import com.budjb.httprequests.v2.core.ContentType

/**
 * An {@link HttpEntity} containing an {@link InputStream}.
 */
class InputStreamHttpEntity extends AbstractHttpEntity {
    /**
     * Buffered input stream source.
     */
    private inputStreamBuffer

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
        setContentType((ContentType)contentType)
    }

    /**
     * Builds an {@link HttpEntity} with the given {@link InputStream} and Content-Type.
     *
     * @param inputStream
     * @param contentType
     */
    InputStreamHttpEntity(InputStream inputStream, String contentType) {
        setInputStream(inputStream)
        setContentType((String)contentType)
    }
}
