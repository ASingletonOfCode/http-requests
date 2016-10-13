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
package com.budjb.httprequests.filter.bundled

import com.budjb.httprequests.core.HttpContext
import com.budjb.httprequests.filter.HttpClientFilter
import com.budjb.httprequests.filter.HttpClientRequestFilter

/**
 * An {@link HttpClientFilter} that provides Basic Authentication support.
 */
class BasicAuthFilter implements HttpClientRequestFilter {
    /**
     * Name of the header for basic authentication.
     */
    private static final String HEADER = 'Authorization'

    /**
     * Contains the encoded credentials
     */
    private String credentials

    /**
     * Constructor.
     *
     * @param username User name to authentication with.
     * @param password Password to authenticate with.
     */
    BasicAuthFilter(String username, String password) {
        credentials = "Basic " + "$username:$password".getBytes().encodeBase64()
    }

    /**
     * Provides an opportunity to modify the request object before the HTTP request is made.
     *
     * @param context HTTP request context.
     */
    @Override
    void filterHttpRequest(HttpContext context) {
        context.getRequest().setHeader(HEADER, credentials)
    }
}
