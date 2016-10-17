package com.budjb.httprequests.v2.core.entity

import com.budjb.httprequests.v2.util.StreamUtils
import spock.lang.Specification

class AbstractHttpEntitySpec extends Specification{
    def 'When an entity is buffered, its input stream is closed'() {
        setup:
        InputStream inputStream = Spy(ByteArrayInputStream, constructorArgs: ['Hello!'.getBytes()])
        InputStreamHttpEntity entity =  new InputStreamHttpEntity(inputStream)

        expect:
        !entity.isBuffered()

        when:
        entity.buffer()

        then:
        entity.isBuffered()
        1 * inputStream.close()
    }

    def 'When an entity is buffered and its input stream is replaced, the entity is re-buffered'() {
        setup:
        InputStream originalStream = Spy(ByteArrayInputStream, constructorArgs: ['One'.getBytes()])
        InputStream newStream = Spy(ByteArrayInputStream, constructorArgs: ['Two'.getBytes()])
        InputStreamHttpEntity entity = new InputStreamHttpEntity(originalStream)

        when:
        entity.buffer()

        then:
        entity.isBuffered()
        1 * originalStream.close()

        when:
        entity.setInputStream(newStream)

        then:
        entity.isBuffered()
        1 * newStream.close()
        StreamUtils.readString(entity.getInputStream(), 'utf-8') == 'Two'
    }

    def 'Retrieving the input stream from an entity where none is present results in an IllegalStateException'() {
        setup:
        InputStreamHttpEntity entity = new InputStreamHttpEntity(null)

        when:
        entity.getInputStream()

        then:
        thrown IllegalStateException
    }
}
