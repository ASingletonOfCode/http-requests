package com.budjb.httprequests.v2.core.entity.multipart

import com.budjb.httprequests.v2.core.entity.AbstractHttpEntity

class MultiPartEntity extends AbstractHttpEntity {
    /**
     * Multipart boundary.
     */
    String boundary

    /**
     * List of entities contained in this multi-part.
     */
    List<Part> parts = []

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
