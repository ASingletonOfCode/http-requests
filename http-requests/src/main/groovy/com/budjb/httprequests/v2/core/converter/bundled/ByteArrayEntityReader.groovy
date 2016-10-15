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

import com.budjb.httprequests.v2.core.converter.AbstractEntityConverter
import com.budjb.httprequests.v2.core.converter.EntityReader
import com.budjb.httprequests.v2.core.entity.ContentType
import com.budjb.httprequests.v2.util.StreamUtils

/**
 * An entity reader that returns the entity as a byte array.
 */
class ByteArrayEntityReader extends AbstractEntityConverter implements EntityReader {
    /**
     * {@inheritDoc}
     */
    @Override
    protected List<Class<?>> getSupportedTypes() {
        return []
    }

    /**
     * Determines if the reader supports converting an entity to the given class type.
     *
     * @param type Type to convert to.
     * @return Whether the type is supported.
     */
    @Override
    boolean supports(Class<?> type) {
        return type.isArray() && byte.isAssignableFrom(type.getComponentType())
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Object read(InputStream entity, ContentType contentType) throws Exception {
        return StreamUtils.readBytes(entity)
    }
}
