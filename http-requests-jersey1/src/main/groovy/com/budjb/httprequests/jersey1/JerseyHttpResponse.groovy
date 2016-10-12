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
package com.budjb.httprequests.jersey1

import com.budjb.httprequests.core.HttpRequest
import com.budjb.httprequests.core.HttpResponse
import com.budjb.httprequests.converter.EntityConverterManager
import com.sun.jersey.api.client.ClientResponse

/**
 * An {@link HttpResponse} implementation that wraps a {@link ClientResponse}.
 */
class JerseyHttpResponse extends HttpResponse {
    /**
     * Jersey Client response.
     */
    ClientResponse response

    /**
     * Constructor.
     *
     * @param request Request properties used to make the request.
     * @param converterManager Converter manager.
     * @param response Jersey Client response.
     */
    JerseyHttpResponse(HttpRequest request, EntityConverterManager converterManager, ClientResponse response) {
        super(request, converterManager)

        this.response = response

        setStatus(response.getStatus())
        setHeaders(response.getHeaders())

        if (response.getType()) {
            setContentType(response.getType().toString())
        }

        if (response.hasEntity()) {
            setEntity(response.getEntityInputStream())
            if (request.isBufferResponseEntity()) {
                close()
            }
        }
        else {
            close()
        }
    }
}
