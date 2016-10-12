package com.budjb.httprequests.v2.core.entity

import com.budjb.httprequests.v2.core.ContentType

class GenericHttpEntity extends ConvertingHttpEntity {
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
}
