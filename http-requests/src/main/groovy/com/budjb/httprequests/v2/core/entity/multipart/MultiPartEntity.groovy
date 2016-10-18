/*
 * Copyright 2016 Bud Byrd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
