package com.budjb.httprequests.v2.core.entity.multipart

import com.budjb.httprequests.v2.core.entity.HttpEntity

interface MultiPart extends HttpEntity {
    Map<String, Map<String, String>> getHeaders()
}
