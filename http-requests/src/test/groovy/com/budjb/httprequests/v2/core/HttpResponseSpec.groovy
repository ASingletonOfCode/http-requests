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

import com.budjb.httprequests.v2.MockHttpResponse
import com.budjb.httprequests.v2.core.converter.EntityConverterManager
import com.budjb.httprequests.v2.core.entity.ContentType
import spock.lang.Specification

class HttpResponseSpec extends Specification {
    def 'Verify header parsing and retrieval'() {
        setup:
        def response = new MockHttpResponse(
            new EntityConverterManager(),
            200,
            [
                foo : ['bar', 'baz'],
                hi  : ['there'],
                peek: 'boo'
            ],
            null,
            null
        )

        expect:
        response.getHeaders() == [
            foo : ['bar', 'baz'],
            hi  : ['there'],
            peek: ['boo']
        ]
        response.getFlattenedHeaders() == [
            foo : ['bar', 'baz'],
            hi  : 'there',
            peek: 'boo'
        ]
        response.getHeaders('foo') == ['bar', 'baz']
        response.getHeaders('hi') == ['there']
        response.getHeaders('peek') == ['boo']
        response.getHeader('foo') == 'bar'
        response.getHeader('hi') == 'there'
        response.getHeader('peek') == 'boo'
        response.getHeader('nope') == null
        response.getHeaders('nope') == null
    }

    def 'Ensure the Allow header is parsed properly'() {
        setup:
        HttpResponse response = new MockHttpResponse(
            new EntityConverterManager(),
            200,
            ['Allow': 'GET,POST,PUT'],
            null,
            null
        )

        expect:
        response.getAllow() == [HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT]
    }

    def 'When the response contains no entity, hasEntity() returns false'() {
        setup:
        HttpResponse response = new MockHttpResponse(
            new EntityConverterManager(),
            200,
            [:],
            null,
            null
        )

        expect:
        !response.hasEntity()
    }

    def 'When the response contains an entity, hasEntity() returns true'() {
        setup:
        HttpResponse response = new MockHttpResponse(
            new EntityConverterManager(),
            200,
            [:],
            null,
            new ByteArrayInputStream([1, 2, 3] as byte[])
        )

        expect:
        response.hasEntity()
    }

    def 'When an entity is closed, its entity is closed'() {
        setup:
        ByteArrayInputStream inputStream = Spy(ByteArrayInputStream, constructorArgs: ['hello'.getBytes()])

        HttpResponse response = new MockHttpResponse(
            new EntityConverterManager(),
            200,
            [:],
            ContentType.TEXT_PLAIN,
            inputStream
        )

        when:
        response.close()

        then:
        1 * inputStream.close()
    }

    def 'Setting the entity with a string content type creates the entity correctly'() {
        setup:
        HttpResponse response = new MockHttpResponse(
            new EntityConverterManager(),
            200,
            [:],
            ContentType.TEXT_PLAIN,
            new ByteArrayInputStream('hello'.getBytes())
        )

        when:
        response.setEntity(new ByteArrayInputStream('{"foo":"bar"}'.getBytes()), 'application/json')

        then:
        response.entity.contentType.type == 'application/json'
    }
}
