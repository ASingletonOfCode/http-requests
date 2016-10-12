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
package com.budjb.httprequests.v2.core.filter

import com.budjb.httprequests.v2.core.HttpContext
import com.budjb.httprequests.v2.core.HttpRequest

/**
 * An {@link HttpClientFilter} that allows modification of the {@link HttpRequest} instance before
 * the request is transmitted.
 */
interface HttpClientRequestFilter extends HttpClientFilter {
    /**
     * Provides an opportunity to modify the {@link HttpRequest} before it is transmitted.
     *
     * @param context HTTP request context.
     */
    void filterHttpRequest(HttpContext context)
}
