package com.budjb.httprequests

import com.budjb.httprequests.core.ContentType
import com.budjb.httprequests.core.multipart.MultiPartEntity
import com.budjb.httprequests.core.multipart.Part
import spock.lang.Specification

class MultiPartSpec extends Specification {
    def 'does this even work?'() {
        setup:
        Part part1 = new Part('textfile', new ByteArrayInputStream("I am a text file.".getBytes()), ContentType.TEXT_PLAIN)
        Part part2 = new Part('jsonfile', new ByteArrayInputStream('{"foo":"bar"}'.getBytes()), ContentType.APPLICATION_JSON)

        MultiPartEntity multiPartEntity = new MultiPartEntity()
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
