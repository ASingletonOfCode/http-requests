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
