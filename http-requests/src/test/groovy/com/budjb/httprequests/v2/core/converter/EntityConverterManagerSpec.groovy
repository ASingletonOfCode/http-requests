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
package com.budjb.httprequests.v2.core.converter

import com.budjb.httprequests.v2.MockHttpClient
import com.budjb.httprequests.v2.core.HttpClient
import com.budjb.httprequests.v2.core.converter.bundled.JsonEntityWriter
import com.budjb.httprequests.v2.core.converter.bundled.StringEntityReader
import com.budjb.httprequests.v2.core.converter.bundled.StringEntityWriter
import com.budjb.httprequests.v2.core.entity.ContentType
import com.budjb.httprequests.v2.core.entity.HttpEntity
import com.budjb.httprequests.v2.core.entity.InputStreamHttpEntity
import com.budjb.httprequests.v2.core.exception.UnsupportedConversionException
import com.budjb.httprequests.v2.util.StreamUtils
import groovy.transform.InheritConstructors
import spock.lang.Ignore
import spock.lang.Specification

class EntityConverterManagerSpec extends Specification {
    def 'When a converter is removed from an HttpClient, it is still present in the factory'() {
        setup:
        EntityConverter converter = new StringEntityReader()

        EntityConverterManager converterManager = new EntityConverterManager()
        converterManager.add(converter)
        converterManager.add(new StringEntityWriter())

        HttpClient httpClient = new MockHttpClient()
        httpClient.converterManager = new EntityConverterManager(converterManager)

        when:
        httpClient.removeEntityConverter(converter)

        then:
        !httpClient.getEntityConverters().contains(converter)
        converterManager.getAll().contains(converter)
    }

    def 'When no reader is available to perform conversion, an UnsupportedConversionException is thrown'() {
        setup:
        EntityConverterManager converterManager = new EntityConverterManager()

        when:
        converterManager.read(String, new InputStreamHttpEntity(new ByteArrayInputStream([1, 2, 3] as byte[])))

        then:
        thrown UnsupportedConversionException
    }

    @Ignore
    def 'When no writer is available to perform conversion, an UnsupportedConversionException is thrown'() {
        setup:
        EntityConverterManager converterManager = new EntityConverterManager()

        when:
        converterManager.write('Hello!')

        then:
        thrown UnsupportedConversionException
    }

    def 'When no user-defined Content-Type is specified, the converter manager applies one to the entity'() {
        setup:
        EntityConverterManager converterManager = new EntityConverterManager()
        converterManager.add(new JsonEntityWriter())

        when:
        HttpEntity entity = converterManager.write([foo: 'bar'])

        then:
        entity.contentType == ContentType.APPLICATION_JSON
    }

    def 'When a user-defined Content-Type is specified, the converter manager does not apply one to the entity'() {
        setup:
        EntityConverterManager converterManager = new EntityConverterManager()
        converterManager.add(new JsonEntityWriter())

        when:
        HttpEntity entity = converterManager.write([foo: 'bar'], ContentType.TEXT_PLAIN)

        then:
        entity.contentType == ContentType.TEXT_PLAIN
    }

    def 'When entity converters are cleared from the manager, no converters are registered'() {
        setup:
        EntityConverterManager manager = new EntityConverterManager()
        manager.add(new StringEntityWriter())

        expect:
        manager.getEntityWriters().size() > 0

        when:
        manager.clear()

        then:
        manager.getEntityWriters().size() == 0
    }

    def 'When a writer does not return a converted entity or throws an exception, the next writer is attempted'() {
        setup:
        boolean nullWriter = false
        boolean failWriter = false
        boolean goodWriter = false

        EntityConverterManager manager = new EntityConverterManager()
        manager.add(new NullStringEntityWriter({ nullWriter = true }))
        manager.add(new FailingStringEntityWriter({ failWriter = true }))
        manager.add(new TrackingStringEntityWriter({ goodWriter = true }))

        when:
        HttpEntity entity = manager.write('hello')

        then:
        nullWriter
        failWriter
        goodWriter

        StreamUtils.readString(entity.getInputStream(), 'utf-8') == 'hello'
    }

    def 'When a reader does not return a converted entity or throws an exception, the next reader is attempted'() {
        boolean nullReader = false
        boolean failReader = false
        boolean goodReader = false

        EntityConverterManager manager = new EntityConverterManager()
        manager.add(new NullStringEntityReader({ nullReader = true }))
        manager.add(new FailingStringEntityReader({ failReader = true }))
        manager.add(new TrackingStringEntityReader({ goodReader = true }))

        InputStreamHttpEntity entity = new InputStreamHttpEntity(new ByteArrayInputStream('hello'.getBytes('utf-8')), new ContentType('text/plain; charset=utf-8'))

        when:
        String result = manager.read(String, entity)

        then:
        nullReader
        failReader
        goodReader

        result == 'hello'
    }

    def 'When an entity has an explicit content type, a writer explicitly supporting that content type is preferred'() {
        setup:
        boolean explicit = false
        boolean fallback = false

        EntityConverterManager manager = new EntityConverterManager()
        manager.add(new TrackingStringEntityWriter({ fallback = true }))
        manager.add(new TrackingStringEntityWriter({ explicit = true }) {
            @Override
            protected List<ContentType> getSupportedContentTypes() {
                return [ContentType.TEXT_PLAIN]
            }
        })

        when:
        HttpEntity entity = manager.write('hello', ContentType.TEXT_PLAIN)

        then:
        explicit
        !fallback
        StreamUtils.readString(entity.getInputStream(), 'utf-8') == 'hello'
    }

    def 'When a charset is provided, the resulting string is built using it'() {
        setup:
        EntityConverterManager converterManager = new EntityConverterManager()
        converterManager.add(new StringEntityReader())

        InputStreamHttpEntity entity = new InputStreamHttpEntity(new ByteArrayInputStream('åäö'.getBytes('UTF-8')), 'text/plain;charset=euc-jp')

        when:
        String converted = converterManager.read(String, entity)

        then:
        converted == '奪辰旦'
    }

    def 'When no charset is provided, ISO-8859-1 is used'() {
        setup:
        EntityConverterManager converterManager = new EntityConverterManager()
        converterManager.add(new StringEntityReader())

        InputStreamHttpEntity entity = new InputStreamHttpEntity(new ByteArrayInputStream('åäö'.getBytes('UTF-8')), 'text/plain')

        when:
        String converted = converterManager.read(String, entity)

        then:
        converted == 'Ã¥Ã¤Ã¶'
    }

    static class TrackingStringEntityReader extends AbstractEntityConverter implements EntityReader {
        Closure closure

        TrackingStringEntityReader(Closure closure) {
            this.closure = closure
        }

        @Override
        protected List<Class<?>> getSupportedTypes() {
            return [String]
        }

        @Override
        Object read(InputStream entity, ContentType contentType) throws Exception {
            closure.call()
            return process(entity, contentType)
        }

        Object process(InputStream entity, ContentType contentType) {
            return StreamUtils.readString(entity, contentType?.getCharset() ?: ContentType.DEFAULT_CHARSET)
        }
    }

    @InheritConstructors
    static class NullStringEntityReader extends TrackingStringEntityReader {
        @Override
        Object process(InputStream entity, ContentType contentType) {
            return null
        }
    }

    @InheritConstructors
    static class FailingStringEntityReader extends TrackingStringEntityReader {
        @Override
        Object process(InputStream entity, ContentType contentType) {
            throw new RuntimeException('this one failed!')
        }
    }

    static class TrackingStringEntityWriter extends AbstractEntityConverter implements EntityWriter {
        Closure closure

        TrackingStringEntityWriter(Closure closure) {
            this.closure = closure
        }

        @Override
        protected List<Class<?>> getSupportedTypes() {
            return [String]
        }

        @Override
        InputStream write(Object entity, ContentType contentType) throws Exception {
            closure.call()
            return process(entity, contentType)
        }

        InputStream process(Object entity, ContentType contentType) throws Exception {
            new ByteArrayInputStream(((String) entity).getBytes(contentType?.charset ?: ContentType.DEFAULT_CHARSET))
        }
    }

    @InheritConstructors
    static class FailingStringEntityWriter extends TrackingStringEntityWriter {
        @Override
        InputStream process(Object entity, ContentType contentType) throws Exception {
            throw new RuntimeException("this one failed!")
        }
    }

    @InheritConstructors
    static class NullStringEntityWriter extends TrackingStringEntityWriter {
        @Override
        InputStream process(Object entity, ContentType contentType) throws Exception {
            return null
        }
    }
}
