package com.budjb.httprequests.v2.core.entity

import com.budjb.httprequests.v2.core.converter.EntityConverterManager

/**
 * A basic, concrete implementation of an {@link HttpEntity} that supports conversion.
 *
 * This class is useful as a general-use entity that will convert some
 * object for use with an HTTP request.
 */
class GenericHttpEntity extends AbstractHttpEntity implements ConvertingHttpEntity {
    /**
     * Object representing the entity.
     */
    Object object

    /**
     * Constructs the entity with the given object and Content-Type of application/octet-stream.
     *
     * @param object
     */
    GenericHttpEntity(Object object) {
        setObject(object)
    }

    /**
     * Constructs the entity with the given object and Content-Type.
     *
     * @param object
     * @param contentType
     */
    GenericHttpEntity(Object object, String contentType) {
        setObject(object)
        setContentType(contentType)
    }

    /**
     * Constructs the entity with the given object and Content-Type.
     *
     * @param object
     * @param contentType
     */
    GenericHttpEntity(Object object, ContentType contentType) {
        setObject(object)
        setContentType(contentType)
    }

    /**
     * {@inheritDoc}
     */
    void convert(EntityConverterManager converterManager) {
        HttpEntity entity = converterManager.write(getObject(), getContentType())

        setInputStream(entity.getInputStream())
        setContentType(entity.getContentType())
    }
}
