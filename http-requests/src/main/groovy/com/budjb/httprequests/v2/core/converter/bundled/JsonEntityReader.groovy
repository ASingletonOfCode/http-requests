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
import groovy.json.JsonSlurper

/**
 * An entity reader that parses an entity as JSON and returns a <code>List</code> or <code>Map</code>.
 */
class JsonEntityReader extends AbstractEntityConverter implements EntityReader {
    /**
     * {@inheritDoc}
     */
    @Override
    protected List<Class<?>> getSupportedTypes() {
        return [Map, List]
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<ContentType> getSupportedContentTypes() {
        return [ContentType.APPLICATION_JSON]
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Object read(InputStream entity, ContentType contentType) throws Exception {
        return new JsonSlurper().parse(entity, contentType?.getCharset() ?: ContentType.DEFAULT_CHARSET)
    }
}
