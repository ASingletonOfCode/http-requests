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

import java.nio.charset.Charset

/**
 * Base entity converter class that handles some of the common support logic.
 */
abstract class AbstractEntityConverter implements EntityConverter {
    /**
     * Returns the class type the converter supports.
     *
     * @return
     */
    protected abstract List<Class<?>> getSupportedTypes()

    /**
     * {@inheritDoc}
     */
    @Override
    boolean supports(Class<?> type) {
        return getSupportedTypes().any { return it.isAssignableFrom(type) }
    }

    /**
     * Returns a list of Content-Types that the converter supports.
     *
     * A converter should override this if supported content types makes sense.
     *
     * @return A list of supported content types.
     */
    protected List<ContentType> getSupportedContentTypes() {
        return []
    }

    /**
     * {@inheritDoc}
     */
    @Override
    boolean supports(ContentType contentType) {
        return getSupportedContentTypes().any { it.getType() == contentType.getType() }
    }

    /**
     * Returns the system default character set.
     *
     * @return
     */
    protected String getSystemCharset() {
        return Charset.defaultCharset().name()
    }
}
