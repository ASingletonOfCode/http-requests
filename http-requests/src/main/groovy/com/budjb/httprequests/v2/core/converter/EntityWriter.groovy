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
package com.budjb.httprequests.v2.core.converter

import com.budjb.httprequests.v2.core.entity.ContentType

/**
 * Defines an entity writer.
 */
trait EntityWriter implements EntityConverter {
    /**
     * Returns a Content-Type of the converted object that will be set in the HTTP request.
     *
     * If no Content-Type is known, null is returned.
     *
     * @return Content-Type of the converted object, or null if unknown.
     */
    ContentType getDefaultContentType() {
        return null
    }

    /**
     * Convert the given entity.
     *
     * If an error occurs, null may be returned so that another converter may attempt conversion.
     *
     * @param entity Entity object to convert into a byte array.
     * @param contentType The Content-Type of the entity.
     * @return An {@link InputStream} containing the converted entity.
     * @throws Exception when an unexpected error occurs.
     */
    abstract InputStream write(Object entity, ContentType contentType) throws Exception
}
