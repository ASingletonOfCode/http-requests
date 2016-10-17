package com.budjb.httprequests.v2.core

import com.budjb.httprequests.v2.core.entity.ContentType
import com.budjb.httprequests.v2.core.entity.multipart.InputStreamFormPart
import com.budjb.httprequests.v2.core.entity.multipart.MultiPart
import com.budjb.httprequests.v2.core.entity.multipart.MultiPartEntity
import com.budjb.httprequests.v2.core.entity.multipart.MultiPartFormDataEntity
import com.budjb.httprequests.v2.util.StreamUtils
import spock.lang.Ignore
import spock.lang.Specification

class MultiPartSpec extends Specification {
    @Ignore
    def 'does this even work?'() {
        setup:
        MultiPart part1 = new InputStreamFormPart(new ByteArrayInputStream("I am a text file.".getBytes()), ContentType.TEXT_PLAIN)
        MultiPart part2 = new InputStreamFormPart('jsonfile', new ByteArrayInputStream('{"foo":"bar"}'.getBytes()), ContentType.APPLICATION_JSON)

        MultiPartEntity multiPartEntity = new MultiPartFormDataEntity()
        multiPartEntity.boundary = '---boundary'
        multiPartEntity.addPart(part1)
        multiPartEntity.addPart(part2)

        expect:
        StreamUtils.readString(multiPartEntity.getInputStream(), 'UTF-8') == '''---boundary
Content-Disposition: form-data; name="textfile"
Content-Type: text/plain

I am a text file.
---boundary
Content-Disposition: form-data; name="jsonfile"
Content-Type: application/json

{"foo":"bar"}
---boundary--'''
    }
}
