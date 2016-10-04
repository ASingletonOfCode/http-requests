package com.budjb.httprequests

import spock.lang.Specification

class MultiPartSpec extends Specification {
    def 'test some bullshit'() {
        setup:
        HttpEntity entity1 = new HttpEntity(new ByteArrayInputStream("test part 1".getBytes()), ContentType.TEXT_PLAIN)
        HttpEntity entity2 = new HttpEntity(new ByteArrayInputStream("<car><make>toyota</make></car>\n\n".getBytes()), ContentType.TEXT_XML)
        HttpEntity entity3 = new HttpEntity(new ByteArrayInputStream('{"make":"toyota"}\n'.getBytes()), ContentType.TEXT_PLAIN)

        MultiPart entity = new MultiPart(entity1, entity2, entity3)
        entity.boundary = '------12345'

        when:
        String contents = StreamUtils.readString(entity.getInputStream(), 'UTF-8')

        then:
        contents == '''
------12345
test part 1
------12345
<car><make>toyota</make></car>


------12345
{"make":"toyota"}

------12345--'''
    }
}
