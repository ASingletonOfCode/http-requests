package com.budjb.httprequests.v2.core.entity.multipart

import com.budjb.httprequests.v2.core.ContentType
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
