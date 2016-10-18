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
