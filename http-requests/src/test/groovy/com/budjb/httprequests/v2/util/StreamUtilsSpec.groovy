package com.budjb.httprequests.v2.util

import spock.lang.Specification

class StreamUtilsSpec extends Specification {
    def 'Shoveling data between an input stream and an output stream results in all the data transferred'() {
        setup:
        ByteArrayInputStream inputStream = new ByteArrayInputStream('more and more introductions!'.getBytes())
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream()

        when:
        StreamUtils.shovel(inputStream, outputStream)

        then:
        new String(outputStream.toByteArray()) == 'more and more introductions!'
    }

    def 'Reading a string from an input stream works correctly'() {
        expect:
        StreamUtils.readString(new ByteArrayInputStream('hello'.getBytes()), 'utf-8') == 'hello'
    }

    def 'Reading a byte array from an input stream works correctly'() {
        expect:
        new String(StreamUtils.readString(new ByteArrayInputStream('hello'.getBytes()), 'utf-8')) == 'hello'
    }
}
