package com.budjb.httprequests.v2.core.entity.multipart

import com.budjb.httprequests.v2.core.entity.HttpEntity

trait FormPart implements HttpEntity, MultiPart {
    String name
    String filename

    @Override
    Map<String, List<String>> getHeaders() {
        Map headers = [:]

        if (filename) {
            headers.put('Content-Disposition', ["form-data; name=\"${name}\"; filename=\"${filename}\""])
        }
        else {
            headers.put('Content-Disposition', ["form-data; name=\"${name}\""])
        }

        if (getContentType()) {
            headers.put('Content-Type', [getContentType().toString()])
        }

        return headers
    }
}
