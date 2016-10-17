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
package com.budjb.httprequests.v2.core.filter.bundled

import com.budjb.httprequests.v2.core.HttpContext
import com.budjb.httprequests.v2.core.exception.HttpStatusException
import com.budjb.httprequests.v2.core.filter.HttpClientLifecycleFilter

/**
 * A filter that throws an exception specific to an HTTP status if that status is not in the 200-299 range.
 */
class HttpStatusExceptionFilter implements HttpClientLifecycleFilter {
    @Override
    void onComplete(HttpContext context) {
        if (context.getResponse().getStatus() >= 300) {
            throw HttpStatusException.build(context.getResponse())
        }
    }
}
