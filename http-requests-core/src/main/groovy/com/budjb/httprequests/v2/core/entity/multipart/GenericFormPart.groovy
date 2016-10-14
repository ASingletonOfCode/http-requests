package com.budjb.httprequests.v2.core.entity.multipart

import com.budjb.httprequests.v2.core.ContentType
import com.budjb.httprequests.v2.core.entity.GenericHttpEntity

class GenericFormPart extends GenericHttpEntity implements FormPart {
    GenericFormPart(Object body) {
        super(body)
    }

    GenericFormPart(Object body, ContentType contentType) {
        super(body, contentType)
    }

    GenericFormPart(Object body, String contentType) {
        super(body, contentType)
    }

    GenericFormPart(Object body, ContentType contentType, String filename) {
        this(body, contentType)
        this.filename = filename
    }

    GenericFormPart(Object body, String contentType, String filename) {
        this(body, contentType)
        this.filename = filename
    }
}
