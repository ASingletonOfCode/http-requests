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

interface EntityConverter {
    /**
     * Determines if the converter supports the given class type.
     *
     * @param type Conversion type.
     * @return Whether the type is supported.
     */
    boolean supports(Class<?> type)

    /**
     * Determines if the converter supports the given Content-Type.
     *
     * If a converter does not define a set of supported types, the default
     * stance should be that the converter supports all content types.
     *
     * @param contentType Content-Type.
     * @return Whether the Content-Type is supported.
     */
    boolean supports(ContentType contentType)
}
