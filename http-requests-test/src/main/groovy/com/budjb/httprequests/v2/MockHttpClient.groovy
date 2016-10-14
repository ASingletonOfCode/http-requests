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
package com.budjb.httprequests.v2

import com.budjb.httprequests.v2.core.*
import com.budjb.httprequests.v2.core.converter.EntityConverterManager
import com.budjb.httprequests.v2.core.entity.ContentType
import com.budjb.httprequests.v2.core.entity.HttpEntity
import com.budjb.httprequests.v2.core.filter.HttpClientFilterManager

/**
 * An implementation of {@link HttpClient} that does not make an actual HTTP request; rather it allows
 * the contents of the response to be injected into the client and returned as if a request had been made
 * and those properties were returned in the response. This is useful when mocking requests in unit or
 * integration tests.
 */
class MockHttpClient extends AbstractHttpClient {
    /**
     * Headers of the response.
     */
    Map<String, Object> headers = [:]

    /**
     * Content type of the response.
     */
    ContentType contentType

    /**
     * HTTP status code of the response.
     */
    int status

    /**
     * Input stream of the response.
     */
    InputStream responseInputStream

    /**
     * A buffer containing the contents of the request input stream.
     */
    byte[] requestBuffer

    /**
     * HTTP request context.
     */
    HttpContext httpContext

    /**
     * The last {@link HttpRequest} instance used to make a request.
     */
    HttpRequest request

    /**
     * The last {@link HttpResponse} instance returned from a request.
     */
    HttpResponse response

    /**
     * Constructor.
     */
    MockHttpClient() {
        filterManager = new HttpClientFilterManager()
        converterManager = new EntityConverterManager()
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected HttpResponse doExecute(HttpContext context) throws IOException {
        httpContext = context

        HttpRequest request = context.getRequest()
        HttpEntity entity = request.getEntity()

        this.request = request

        if (context.getMethod().supportsRequestEntity && entity) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
            transmit(entity.getInputStream(), filterOutputStream(context, outputStream))
            requestBuffer = outputStream.toByteArray()
        }

        this.response = new MockHttpResponse(converterManager, status, headers, contentType, responseInputStream)

        return this.response
    }

    /**
     * Converts the entity input stream to an output stream.
     *
     * @param inputStream
     * @param outputStream
     */
    private static void transmit(InputStream inputStream, OutputStream outputStream) {
        int read
        byte[] buffer = new byte[8192]

        while ((read = inputStream.read(buffer, 0, 8192)) != -1) {
            outputStream.write(buffer, 0, read)
        }

        inputStream.close()
    }
}
