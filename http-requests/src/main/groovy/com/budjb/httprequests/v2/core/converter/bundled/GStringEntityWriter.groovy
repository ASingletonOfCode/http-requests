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
import com.budjb.httprequests.v2.core.converter.EntityWriter
import com.budjb.httprequests.v2.core.entity.ContentType

/**
 * An entity writer that will convert a GString.
 */
class GStringEntityWriter extends AbstractEntityConverter implements EntityWriter {
    /**
     * {@inheritDoc}
     */
    @Override
    ContentType getDefaultContentType() {
        return ContentType.TEXT_PLAIN
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<Class<?>> getSupportedTypes() {
        return [GString]
    }

    /**
     * {@inheritDoc}
     */
    @Override
    InputStream write(Object entity, ContentType contentType) throws Exception {
        return new ByteArrayInputStream(entity.toString().getBytes(contentType?.getCharset() ?: getSystemCharset()))
    }
}
