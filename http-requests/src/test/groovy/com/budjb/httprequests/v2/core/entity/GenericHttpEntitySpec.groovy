package com.budjb.httprequests.v2.core.entity

import spock.lang.Specification

class GenericHttpEntitySpec extends Specification {
    def 'Building a generic entity with only an object results in no content type defined'() {
        setup:
        GenericHttpEntity entity = new GenericHttpEntity('Hello!')

        expect:
        entity.contentType == null
    }

    def 'Building a generic entity with an object and a string representation of a content type results in a correctly parsed content type'() {
        setup:
        GenericHttpEntity entity = new GenericHttpEntity('Hello!', 'text/plain')

        expect:
        entity.contentType.type == 'text/plain'
    }

    def 'Building a generic entity with an object and content type sets properties correctly'() {
        setup:
        GenericHttpEntity entity = new GenericHttpEntity('Hello!', ContentType.TEXT_PLAIN)

        expect:
        entity.contentType.type == 'text/plain'
    }
}
