package com.budjb.httprequests.v2.core.entity.multipart

import com.budjb.httprequests.v2.core.ContentType

class MultiPartFormDataEntity extends MultiPartEntity {
    MultiPartFormDataEntity() {
        setContentType(ContentType.MULTI_PART_FORM_DATA)
    }
}
