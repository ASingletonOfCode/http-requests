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
package com.budjb.httprequests.v2.core.entity

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
        type                             | charset | parameters                               || result
        'text/plain'                     | null    | null                                     || 'text/plain'
        'text/plain'                     | 'UTF-8' | null                                     || 'text/plain; charset=UTF-8'
        'text/plain'                     | null    | [charset: 'UTF-8', foo: 'bar']           || 'text/plain; charset=UTF-8; foo=bar'
        'text/plain'                     | 'UTF-8' | [foo: 'bar', ohmy: 'lions/tigers/bears'] || 'text/plain; charset=UTF-8; foo=bar; ohmy="lions/tigers/bears"'
        'text/plain; charset=ISO-8859-1' | 'UTF-8' | null                                     || 'text/plain; charset=UTF-8'
        'text/*; foo="(bar)"'            | null    | null                                      | 'text/*; foo="(bar)"'
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

    @Unroll
    def 'Validate that the MIME-type and parameters #contentType are parsed correctly'() {
        setup:
        ContentType type = new ContentType(contentType)

        expect:
        type.type == mimeType
        type.parameters == parameters

        where:
        contentType                             | mimeType     | parameters
        'text/plain'                            | 'text/plain' | [:]
        'text/plain; charset=UTF-8'             | 'text/plain' | ['charset': 'UTF-8']
        'text/plain; charset=UTF-8; q=2'        | 'text/plain' | ['charset': 'UTF-8', 'q': '2']
        'text/plain; charset="UTF-8"'           | 'text/plain' | ['charset': 'UTF-8']
        'text/plain; charset= UTF-8'            | 'text/plain' | ['charset': 'UTF-8']
        'text/plain; charset =UTF-8'            | 'text/plain' | ['charset': 'UTF-8']
        'text/plain; charset=UTF-8'             | 'text/plain' | ['charset': 'UTF-8']
        'text/plain;charset=UTF-8'              | 'text/plain' | ['charset': 'UTF-8']
        'text/plain; charset = "UTF-8"'         | 'text/plain' | ['charset': 'UTF-8']
        'text/plain; charset="UTF-8" '          | 'text/plain' | ['charset': 'UTF-8']
        'text/plain; charset="UTF-8" ; q = 2  ' | 'text/plain' | ['charset': 'UTF-8', 'q': '2']
        'text/plain; charset=UTF-8 ; q = 2  '   | 'text/plain' | ['charset': 'UTF-8', 'q': '2']
        'text/plain; foo="bar\\"baz"'           | 'text/plain' | ['foo': 'bar"baz']

    }

    @Unroll
    def 'Validate that the Content-Type #contentType fails to parse'() {
        when:
        new ContentType(contentType)

        then:
        thrown(IllegalArgumentException)

        where:
        contentType << [
            'meh',
            'foo"/bar',
            'foo/bar; q !=2',
            'foo/ bar',
            'foo/bar/',
            'foo/"bar"',
            'foo; q=2',
            'foo bar',
            'foo/bar; "charset"="utf-8"',
            'foo/bar; charset=mid"quote',
            'foo/bar; foo=[bar]',
            'foo/bar; foo=',
            'foo/bar; foo; bar',
            'foo/bar; foo=bar; hi',
            'foo/bar; foo=sparkly™',
            'foo/bar; foo=bar baz',
            'foo/bar; foo="sparkly™"',
            'foo/bar; foo="bad\\escape"',
            'foo/bar; foo'
        ]
    }

    def 'When building a content type with a character set and null parameters, the character set is set up correctly'() {
        when:
        ContentType contentType = new ContentType('text/plain', 'utf-8', null)

        then:
        contentType.charset == 'utf-8'
        contentType.parameters == [charset: 'utf-8']
    }

    def 'Assigning an empty parameter removes it from the content type'() {
        setup:
        ContentType contentType = new ContentType('text/plain; charset=utf-8')

        when:
        contentType.setParameter('charset', '')

        then:
        contentType.getCharset() == null
    }
}
