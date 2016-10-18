package com.budjb.httprequests.v2.core.entity.multipart

import com.budjb.httprequests.v2.core.converter.EntityConverterManager
import com.budjb.httprequests.v2.core.entity.AbstractHttpEntity
import com.budjb.httprequests.v2.core.entity.ContentType
import com.budjb.httprequests.v2.core.entity.ConvertingHttpEntity

abstract class MultiPartEntity extends AbstractHttpEntity implements ConvertingHttpEntity {
    /**
     * Multipart boundary.
     */
    String boundary

    /**
     * List of entities contained in this multi-part.
     */
    List<MultiPart> parts = []

    /**
     * Constructor.
     */
    MultiPartEntity() {
        boundary = "----------------------${UUID.randomUUID().toString()}"
        setInputStream(new MultiPartEntityInputStream(this))
    }

    /**
     * Add an entity to the multi-part.
     *
     * @param entity
     */
    void addPart(MultiPart part) {
        this.inputStream
        parts.add(part)
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void setContentType(ContentType contentType) {
        contentType.setParameter('boundary', boundary)
    }

    /**
     * Converts each part of the multi part entity if necessary.
     *
     * @param converterManager
     */
    @Override
    void convert(EntityConverterManager converterManager) {
        parts.each {
            if (it instanceof ConvertingHttpEntity) {
                it.convert(converterManager)
            }
        }
    }
}
