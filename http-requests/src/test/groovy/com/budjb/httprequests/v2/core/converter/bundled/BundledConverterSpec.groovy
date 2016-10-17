package com.budjb.httprequests.v2.core.converter.bundled

import com.budjb.httprequests.v2.core.FormData
import com.budjb.httprequests.v2.core.converter.EntityReader
import com.budjb.httprequests.v2.core.converter.EntityWriter
import com.budjb.httprequests.v2.core.entity.ContentType
import com.budjb.httprequests.v2.util.StreamUtils
import groovy.util.slurpersupport.GPathResult
import spock.lang.Specification

class BundledConverterSpec extends Specification {
    final static ContentType APPLICATION_MEH = new ContentType('application/meh')

    def 'Validate JSON entity reader functionality'() {
        setup:
        EntityReader reader = new JsonEntityReader()

        expect:
        reader.supports(Map)
        reader.supports(List)
        reader.supports(ContentType.APPLICATION_JSON)
        !reader.supports(APPLICATION_MEH)
        reader.read(new ByteArrayInputStream('{"foo":"bar"}'.getBytes()), null) == [foo: 'bar']
    }

    def 'Validate byte array reader functionality'() {
        setup:
        EntityReader reader = new ByteArrayEntityReader()

        expect:
        reader.supports(byte[])
        reader.read(new ByteArrayInputStream([1, 2, 3] as byte[]), null) == [1, 2, 3] as byte[]
    }

    def 'Validate string reader functionality'() {
        setup:
        EntityReader reader = new StringEntityReader()

        expect:
        reader.supports(String)
        reader.read(new ByteArrayInputStream('foo'.getBytes()), null) == 'foo'
    }

    def 'Validate XML slurper reader functionality'() {
        setup:
        EntityReader reader = new XmlSlurperEntityReader()

        expect:
        reader.supports(GPathResult)
        reader.supports(ContentType.TEXT_XML)
        reader.supports(ContentType.APPLICATION_XML)
        !reader.supports(APPLICATION_MEH)
        ((GPathResult)reader.read(new ByteArrayInputStream('<foo>bar</foo>'.getBytes()), null)).toString() == 'bar'
    }

    def 'Validate byte array writer functionality'() {
        setup:
        EntityWriter writer = new ByteArrayEntityWriter()

        expect:
        writer.supports(byte[])
        writer.getDefaultContentType() == ContentType.APPLICATION_OCTET_STREAM
        StreamUtils.readBytes(writer.write([1, 2, 3], null)) == [1, 2, 3] as byte[]
    }

    def 'Validate form data writer functionality'() {
        setup:
        EntityWriter writer = new FormDataEntityWriter()
        FormData formData = new FormData()
        formData.addField('foo', 'bar')

        expect:
        writer.supports(FormData)
        writer.supports(ContentType.APPLICATION_X_WWW_FORM_URLENCODED)
        !writer.supports(APPLICATION_MEH)
        writer.getDefaultContentType() == ContentType.APPLICATION_X_WWW_FORM_URLENCODED
        StreamUtils.readString(writer.write(formData, null), 'utf-8') == 'foo=bar'
    }

    def 'Validate GString writer functionality'() {
        setup:
        EntityWriter writer = new GStringEntityWriter()
        String string = 'world'
        GString gstring = "Hello, ${string}!"

        expect:
        writer.supports(GString)
        writer.getDefaultContentType() == ContentType.TEXT_PLAIN
        StreamUtils.readString(writer.write(gstring, null), 'utf-8') == 'Hello, world!'
    }

    def 'Validate JSON writer functionality'() {
        setup:
        EntityWriter writer = new JsonEntityWriter()

        expect:
        writer.supports(Map)
        writer.supports(List)
        !writer.supports(APPLICATION_MEH)
        writer.supports(ContentType.APPLICATION_JSON)
        writer.getDefaultContentType() == ContentType.APPLICATION_JSON
        StreamUtils.readString(writer.write([foo: 'bar'], null), 'utf-8') == '{"foo":"bar"}'
    }

    def 'Validate string writer functionality'() {
        setup:
        EntityWriter writer = new StringEntityWriter()

        expect:
        writer.supports(String)
        writer.getDefaultContentType() == ContentType.TEXT_PLAIN
        StreamUtils.readString(writer.write('Hello, world!', null), 'utf-8') == 'Hello, world!'
    }
}
