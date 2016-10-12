package com.budjb.httprequests.v2.core.entity.multipart

import com.budjb.httprequests.v2.core.ContentType

class Part {
    String name

    String filename

    InputStream inputStream

    ContentType contentType = ContentType.APPLICATION_OCTET_STREAM

    Part(String name, InputStream inputStream) {
        this.name = name
        this.inputStream = inputStream
    }

    Part(String name, InputStream inputStream, ContentType contentType) {
        this(name, inputStream)
        this.contentType = contentType
    }

    Part(String name, InputStream inputStream, String filename) {
        this(name, inputStream)
        this.filename = filename
    }

    Part(String name, InputStream inputStream, String filename, ContentType contentType) {
        this(name, inputStream)
        this.contentType = contentType
        this.filename = filename
    }
}
