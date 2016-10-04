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
package com.budjb.httprequests

class MultiPart extends HttpEntity {
    class MultiPartInputStream extends InputStream {
        /**
         * Reads the next byte of data from the input stream. The value byte is
         * returned as an <code>int</code> in the range <code>0</code> to
         * <code>255</code>. If no byte is available because the end of the stream
         * has been reached, the value <code>-1</code> is returned. This method
         * blocks until input data is available, the end of the stream is detected,
         * or an exception is thrown.
         *
         * <p> A subclass must provide an implementation of this method.
         *
         * @return the next byte of data, or <code>-1</code> if the end of the
         *             stream is reached.
         * @exception IOException  if an I/O error occurs.
         */
        @Override
        int read() throws IOException {
            if (!boundaryStream) {
                boundaryStream = new ByteArrayInputStream(("\n" + boundary).getBytes())
                boundaryStream.mark(1024)
            }

            while (currentEntity < entities.size()) {
                int ch = boundaryStream.read()
                if (ch == -1) {
                    if (!entityNewline) {
                        entityNewline = true
                        return 10 // "\n"
                    }
                    ch = entities.get(currentEntity).getInputStream().read()
                }
                if (ch == -1) {
                    entityNewline = false
                    boundaryStream.reset()
                    currentEntity++
                }
                else {
                    return ch
                }
            }

            if (finalBoundaryCount >= 2) {
                return -1
            }

            int ch = boundaryStream.read()
            if (ch == -1) {
                finalBoundaryCount++
                return 45 // "-"
            }
            else {
                return ch
            }
        }

        /**
         * Disable mark support.
         *
         * @return <code>false</code>, always.
         */
        @Override
        boolean markSupported() {
            return false
        }
    }

    /**
     * Boundary of the entity.
     */
    String boundary = "------${System.currentTimeMillis()}"

    /**
     * Boundary input stream.
     */
    ByteArrayInputStream boundaryStream

    /**
     * Entities that will be output.
     */
    List<HttpEntity> entities = []

    /**
     * Current entity index.
     */
    int currentEntity = 0

    /**
     * Whether the newline following a boundary has been read.
     */
    boolean entityNewline = false

    /**
     * Whether the final boundary has been printed.
     */
    int finalBoundaryCount = 0

    protected MultiPartInputStream multiPartInputStream

    /**
     * Constructor.
     */
    MultiPart() {
        super(ContentType.MULTIPART_MIXED)

        multiPartInputStream = new MultiPartInputStream()
        setInputStream(multiPartInputStream)
    }

    /**
     * Constructor.
     *
     * @param entities
     */
    MultiPart(List<HttpEntity> entities) {
        this()

        entities.each {
            add(it)
        }
    }

    /**
     * Constructor.
     *
     * @param entities
     */
    MultiPart(HttpEntity... entities) {
        this()

        entities.each {
            add(it)
        }
    }

    /**
     * Add a multipart entry.
     *
     * @param inputStream Input stream containing the multipart entry.
     * @param contentType Content-Type of the multipart entry.
     * @return The object this method was called on.
     */
    MultiPart add(HttpEntity entity) {
        entities.add(entity)
        return this
    }

    /**
     * Sets the content type of the entity.
     *
     * @param contentType
     */
    @Override
    void setContentType(ContentType contentType) {
        if (contentType.type != 'multipart') {
            throw new IllegalArgumentException('Content-Type of a multi-part entity must be of the primary type "multipart"')
        }
        contentType.setParameter('boundary', boundary)

        super.setContentType(contentType)
    }

    /**
     * Returns the content type of the request.
     *
     * @return
     */
    @Override
    ContentType getContentType() {
        ContentType type = super.getContentType()
        type.setParameter('boundary', boundary)
        return type
    }
}
