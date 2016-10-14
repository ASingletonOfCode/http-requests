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
package com.budjb.httprequests.v2.core.converter.bundled

import com.budjb.httprequests.v2.core.converter.EntityReader
import groovy.util.slurpersupport.GPathResult

/**
 * An entity read that parses XML using {@link XmlSlurper}.
 */
class XmlSlurperEntityReader implements EntityReader {
    /**
     * Determines if the reader supports converting an entity to the given class type.
     *
     * @param type Type to convert to.
     * @return Whether the type is supported.
     */
    @Override
    boolean supports(Class<?> type) {
        return GPathResult.isAssignableFrom(type)
    }

    /**
     * Convert the given entity.
     *
     * If an error occurs, null may be returned so that another converter can attempt a conversion.
     *
     * @param entity Entity as an {@link java.io.InputStream}.
     * @param contentType Content-Type of the entity.
     * @param charset Character set of the entity.
     * @return The converted entity.
     * @throws java.lang.Exception when an unexpected error occurs during conversion.
     */
    @Override
    Object read(InputStream entity, String contentType, String charset) throws Exception {
        return new XmlSlurper(false, false).parse(entity)
    }
}
