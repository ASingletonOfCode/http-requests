package com.budjb.httprequests.v2.core

import com.budjb.httprequests.v2.reference.ReferenceHttpClientFactory
import spock.lang.Specification

class HttpClientFactoryHolderSpec extends Specification {
    def 'Retrieving the singleton instance of a factory when none is set results in an IllegalStateException'() {
        when:
        HttpClientFactoryHolder.getHttpClientFactory()

        then:
        thrown IllegalStateException
    }

    def 'When a factory instance is stored in the holder, it is accessible'() {
        setup:
        HttpClientFactory factory = new ReferenceHttpClientFactory()

        when:
        HttpClientFactoryHolder.setHttpClientFactory(factory)

        then:
        HttpClientFactoryHolder.getHttpClientFactory() == factory

        cleanup:
        HttpClientFactoryHolder.clear()
    }
}
