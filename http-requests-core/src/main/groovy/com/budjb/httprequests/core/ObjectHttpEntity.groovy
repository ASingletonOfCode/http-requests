package com.budjb.httprequests.core

class ObjectHttpEntity extends HttpEntity {
    /**
     * Object representing the entity.
     */
    final Object object

    /**
     * Constructs the entity with the given object and Content-Type of application/octet-stream.
     *
     * @param object
     */
    ObjectHttpEntity(Object object) {
        this(object, ContentType.APPLICATION_OCTET_STREAM)
    }

    /**
     * Constructs the entity with the given object and Content-Type.
     *
     * @param object
     * @param contentType
     */
    ObjectHttpEntity(Object object, String contentType) {
        this(object, ContentType.parse(contentType))
    }

    /**
     * Constructs the entity with the given object and Content-Type.
     *
     * @param object
     * @param contentType
     */
    ObjectHttpEntity(Object object, ContentType contentType) {
        super(contentType)
        this.object = object
    }
}
