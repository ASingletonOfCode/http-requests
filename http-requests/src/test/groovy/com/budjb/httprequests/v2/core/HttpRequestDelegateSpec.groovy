package com.budjb.httprequests.v2.core

import com.budjb.httprequests.v2.core.HttpRequest
import com.budjb.httprequests.v2.core.entity.ContentType
import spock.lang.Specification

class HttpRequestDelegateSpec extends Specification {
    def 'When a request is built with the closure builder, the properties are set correctly'() {
        when:
        HttpRequest request = HttpRequest.build {
            uri 'https://localhost:8080?going=away'
            accept 'application/json'
            connectionTimeout 10000
            readTimeout 5000
            followRedirects false
            sslValidated false
            headers([foo: 'bar'])
        }

        then:
        request.uri == 'https://localhost:8080'
        request.accept.type == 'application/json'
        request.connectionTimeout == 10000
        request.readTimeout == 5000
        !request.followRedirects
        !request.sslValidated
        request.headers == [foo: ['bar']]
        request.queryParameters == [going: ['away']]
    }

    def 'When a request is build with non-string types, the properties are set correctly'() {
        when:
        HttpRequest request = HttpRequest.build {
            uri new URI('https://localhost:8080?going=away')
            accept ContentType.APPLICATION_JSON
            header 'hi', 'there'
            header 'foo', ['bar', 'baz']
            queryParameter 'Foo', ['Bar', 'Baz']
            queryParameter 'Hi', 'There'
        }

        then:
        request.uri == 'https://localhost:8080'
        request.accept.type == 'application/json'
        request.headers == [hi: ['there'], foo: ['bar', 'baz']]
        request.queryParameters == [going: ['away'], Hi: ['There'], Foo: ['Bar', 'Baz']]
    }

    def 'When headers and query parameters are given multi-valued maps, previous entries are overwritten'() {
        when:
        HttpRequest request = HttpRequest.build {
            uri 'https://localhost:8080?query=param'
            headers([foo: ['bar', 'baz']])
            queryParameters([foo: ['bar', 'baz']])
        }

        then:
        request.headers == [foo: ['bar', 'baz']]
        request.queryParameters == [foo: ['bar', 'baz']]
    }
}
