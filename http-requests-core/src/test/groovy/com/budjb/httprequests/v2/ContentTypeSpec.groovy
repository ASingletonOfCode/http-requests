package com.budjb.httprequests.v2

import com.budjb.httprequests.v2.core.ContentType
import spock.lang.Specification
import spock.lang.Unroll

class ContentTypeSpec extends Specification {
    @Unroll
    def 'Validate that ContentType.toString() produces #result correctly'() {
        setup:
        ContentType contentType = new ContentType(type)
        contentType.charset = charset
        contentType.addParameters(parameters)

        expect:
        contentType.toString() == result

        where:
        type         | charset | parameters                               || result
        'text/plain' | null    | null                                     || 'text/plain'
        'text/plain' | 'UTF-8' | null                                     || 'text/plain; charset=UTF-8'
        'text/plain' | null    | [charset: 'UTF-8', foo: 'bar']           || 'text/plain; charset=UTF-8; foo=bar'
        'text/plain' | 'UTF-8' | [foo: 'bar', ohmy: 'lions/tigers/bears'] || 'text/plain; charset=UTF-8; foo=bar; ohmy="lions/tigers/bears"'
    }

    def 'Validate the MIME and character set constructor builds correctly'() {
        expect:
        new ContentType('text/plain', 'UTF-8').toString() == 'text/plain; charset=UTF-8'
    }

    def 'Validate the MIME, character set, and parameters constructor builds correctly'() {
        expect:
        new ContentType('text/plain', 'UTF-8', [foo: 'bar']).toString() == 'text/plain; foo=bar; charset=UTF-8'
    }

    def 'Validate the MIME and parameters constructor builds correctly'() {
        expect:
        new ContentType('text/plain', [charset: 'UTF-8', foo: 'bar']).toString() == 'text/plain; charset=UTF-8; foo=bar'
    }
}
