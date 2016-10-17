package com.budjb.httprequests.v2.core.entity

import com.budjb.httprequests.v2.core.converter.EntityConverterManager

/**
 * Describes a type of {@link HttpEntity} that supports conversion of its entity body.
 */
interface ConvertingHttpEntity extends HttpEntity {
    /**
     * Provides an {@link EntityConverterManager} to allow the entity to
     * convert its body as necessary.
     *
     * @param converterManager An {@link EntityConverterManager} providing conversion functionality.
     */
    void convert(EntityConverterManager converterManager)
}
