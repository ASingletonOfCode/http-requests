/*
 * Copyright 2016 Bud Byrd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.budjb.httprequests.v2.core

import com.budjb.httprequests.v2.core.entity.ContentType
import com.budjb.httprequests.v2.core.entity.GenericHttpEntity
import com.budjb.httprequests.v2.core.entity.InputStreamHttpEntity
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

    def 'Creating an entity with a closure creates the entity correctly'() {
        when:
        HttpRequest request = HttpRequest.build {
            uri 'https://localhost:8080'
            entity {
                body 'hello'
                contentType 'text/plain'
            }
        }

        then:
        request.entity instanceof GenericHttpEntity
        GenericHttpEntity entity = (GenericHttpEntity) request.entity
        entity.object == 'hello'
        entity.contentType.type == 'text/plain'
    }

    def 'Creating an entity with a closure and input stream creates the entity correctly'() {
        setup:
        InputStream inputStream = new ByteArrayInputStream('hello'.getBytes())

        when:
        HttpRequest request = HttpRequest.build {
            uri 'https://localhost:8080'
            entity {
                body inputStream
                contentType ContentType.APPLICATION_OCTET_STREAM
            }
        }

        then:
        request.entity instanceof InputStreamHttpEntity
        InputStreamHttpEntity entity = (InputStreamHttpEntity) request.entity
        entity.inputStream == inputStream
        entity.contentType.type == 'application/octet-stream'
    }
}
