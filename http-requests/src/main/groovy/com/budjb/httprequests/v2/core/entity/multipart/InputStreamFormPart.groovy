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
package com.budjb.httprequests.v2.core.entity.multipart

import com.budjb.httprequests.v2.core.entity.ContentType
import com.budjb.httprequests.v2.core.entity.InputStreamHttpEntity

class InputStreamFormPart extends InputStreamHttpEntity implements FormPart {
    InputStreamFormPart(InputStream inputStream) {
        super(inputStream)
    }

    InputStreamFormPart(InputStream inputStream, String contentType) {
        super(inputStream, contentType)
    }

    InputStreamFormPart(InputStream inputStream, ContentType contentType) {
        super(inputStream, contentType)
    }

    InputStreamFormPart(InputStream inputStream, String contentType, String filename) {
        this(inputStream, contentType)
        this.filename = filename
    }

    InputStreamFormPart(InputStream inputStream, ContentType contentType, String filename) {
        this(inputStream, contentType)
        this.filename = filename
    }
}
