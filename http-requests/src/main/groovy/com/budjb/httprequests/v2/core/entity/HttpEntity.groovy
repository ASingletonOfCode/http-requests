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
package com.budjb.httprequests.v2.core.entity

/**
 * Defines an HTTP entity.
 */
interface HttpEntity {
    /**
     * Returns the Content-Type of the entity.
     *
     * @return
     */
    ContentType getContentType()

    /**
     * Sets the Content-Type of the entity.
     *
     * @param contentType
     */
    void setContentType(ContentType contentType)

    /**
     * Sets the Content-Type of the entity.
     *
     * @param contentType
     */
    void setContentType(String contentType)

    /**
     * Returns whether the entity will be buffered when it is written.
     *
     * @return
     */
    boolean isBuffered()

    /**
     * Buffers the source {@link InputStream} so that it can be read multiple times.
     ]     */
    void buffer()

    /**
     * Returns the {@link InputStream} containing the entity.
     *
     * If the entity is buffered, each call to this method will return a new
     * instance of {@link ByteArrayInputStream}.
     *
     * @return
     */
    InputStream getInputStream()

    /**
     * Closes the entity.
     */
    void close()
}
