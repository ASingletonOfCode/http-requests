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
package com.budjb.httprequests

import com.budjb.httprequests.artefact.EntityConverterArtefactHandler
import com.budjb.httprequests.artefact.HttpClientFilterArtefactHandler
import com.budjb.httprequests.converter.EntityConverter
import com.budjb.httprequests.filter.HttpClientFilter
import com.budjb.httprequests.reference.ReferenceHttpClientFactory
import grails.plugins.Plugin
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.NoSuchBeanDefinitionException

@Slf4j
class HttpRequestsGrailsGrailsPlugin extends Plugin {
    /**
     * Grails version the plugin's intended for.
     */
    def grailsVersion = "3.0 > *"

    /**
     * Plugin title.
     */
    def title = "HTTP Requests Plugin"

    /**
     * Author name.
     */
    def author = "Bud Byrd"

    /**
     * Author email address.
     */
    def authorEmail = "bud.byrd@gmail.com"

    /**
     * Plugin description.
     */
    def description = 'The HTTP Requests Plugin provides the http-requests library and artefacts for filters and converters.'

    /**
     * Plugin documentation.
     */
    def documentation = "https://budjb.github.io/http-requests/latest"

    /**
     * Plugin license.
     */
    def license = "APACHE"

    /**
     * Issue tracker.
     */
    def issueManagement = [system: "GITHUB", url: "https://github.com/budjb/http-requests/issues"]

    /**
     * SCM.
     */
    def scm = [url: "https://github.com/budjb/http-requests"]

    /**
     * Create beans for the {@link HttpClientFactory} and any {@link HttpClientFilter} or
     * {@link EntityConverter} artefacts.
     *
     * @return
     */
    Closure doWithSpring() {
        { ->
            if (autoLoadFactory()) {
                httpClientFactory(ReferenceHttpClientFactory, autoLoadConverters()) { bean ->
                    bean.autowire = true
                }
            }
            else {
                log.debug("Not attempting to automatically load the httpClientFactory bean due to this feature being " +
                    "disabled in the application configuration.")
            }

            grailsApplication.getArtefacts(HttpClientFilterArtefactHandler.TYPE).each {
                "${it.getClazz().getName()}"(it.getClazz())
            }

            grailsApplication.getArtefacts(EntityConverterArtefactHandler.TYPE).each {
                "${it.getClazz().getName()}"(it.getClazz())
            }
        }
    }

    /**
     * Registers any filter or converter artefacts to the HTTP client factory instance.
     */
    @Override
    void doWithApplicationContext() {
        if (applicationContext.containsBean('httpClientFactory')) {
            HttpClientFactory httpClientFactory = applicationContext.getBean('httpClientFactory', HttpClientFactory)

            grailsApplication.getArtefacts(HttpClientFilterArtefactHandler.TYPE).each {
                log.debug("Adding HttpClientFilter '${it.getClazz().getSimpleName()}'")
                httpClientFactory.addFilter(applicationContext.getBean(it.getClazz().getName(), HttpClientFilter))
            }

            grailsApplication.getArtefacts(EntityConverterArtefactHandler.TYPE).each {
                log.debug("Adding EntityConverter '${it.getClazz().getSimpleName()}'")
                httpClientFactory.addEntityConverter(applicationContext.getBean(it.getClazz().getName(), EntityConverter))
            }
        }
    }

    /**
     * Returns whether to automatically load the built-in converters.
     *
     * @return Whether to automatically load the built-in converters.
     */
    boolean autoLoadConverters() {
        return config.httprequests.autoLoadConverters != false
    }

    /**
     * Returns whether to automatically load an {@link HttpClientFactory} from the classpath.
     *
     * @return
     */
    boolean autoLoadFactory() {
        return config.httprequests.autoLoadFactory != false
    }
}
