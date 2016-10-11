package com.budjb.httprequests.core.multipart

import com.budjb.httprequests.converter.EntityConverterManager
import com.budjb.httprequests.core.HttpEntity

class MultiPartEntity extends HttpEntity {
    /**
     * Multipart boundary.
     */
    String boundary

    /**
     * List of entities contained in this multi-part.
     */
    List<Part> parts = []

    /**
     * TODO: is this necessary? should I just move converstion into the client itself?
     *
     * @param converterManager
     */
    void convertEntities(EntityConverterManager converterManager) {
        parts = parts.collect {
            // TODO: fix needing to pass the request
            converterManager.convertEntity(null, it)
        }
    }

    MultiPartEntity() {
        this("----------------------${UUID.randomUUID().toString()}")
    }

    /**
     * Constructor that takes a boundary.
     *
     * @param boundary
     */
    MultiPartEntity(String boundary) {
        super(new MultiPartEntityInputStream(), "multipart/form-data; boundary=${boundary}")
        ((MultiPartEntityInputStream) getInputStream()).setMultiPartEntity(this)
        this.boundary = boundary
    }

    /**
     * Add an entity to the multi-part.
     *
     * @param entity
     */
    void addPart(Part part) {
        parts.add(part)
    }

}
