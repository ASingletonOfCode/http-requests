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

import com.budjb.httprequests.converter.EntityConverter
import com.budjb.httprequests.converter.EntityConverterManager
import com.budjb.httprequests.filter.HttpClientFilter
import com.budjb.httprequests.filter.HttpClientFilterManager

/**
 * An interface that describes the common structure and methods of an HTTP client.
 *
 * Various filter classes are supported.
 */
interface HttpClient {
    /**
     * Assigns the {@link EntityConverterManager} object to the client.
     *
     * @param converterManager Converter manager responsible for entity marshaling.
     */
    void setConverterManager(EntityConverterManager converterManager)

    /**
     * Assigns the {@link HttpClientFilterManager} object to the client.
     *
     * @param filterManager Filter manager instance.
     */
    void setFilterManager(HttpClientFilterManager filterManager)

    /**
     * Executes an HTTP request with the given method and closure to configure the request and (optionally)
     * the request entity..
     *
     * @param method HTTP method to use with the HTTP request.
     * @param closure Closure that configures the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    HttpResponse execute(HttpMethod method, @DelegatesTo(HttpRequest) Closure closure) throws IOException

    /**
     * Execute an HTTP request with the given method and request parameters.
     *
     * @param method HTTP method to use with the HTTP request.
     * @param request Request properties to use with the HTTP request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    HttpResponse execute(HttpMethod method, HttpRequest request) throws IOException

    /**
     * Perform an HTTP GET request.
     *
     * @param request Request properties to use with the HTTP request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    HttpResponse get(HttpRequest request) throws IOException

    /**
     * Perform an HTTP GET request.
     *
     * @param requestClosure Closure that configures the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    HttpResponse get(@DelegatesTo(HttpRequest) Closure requestClosure) throws IOException

    /**
     * Perform an HTTP POST request.
     *
     * @param requestClosure Closure that configures the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    HttpResponse post(@DelegatesTo(HttpRequest) Closure requestClosure) throws IOException

    /**
     * Perform an HTTP POST request.
     *
     * @param request Request properties to use with the HTTP request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    HttpResponse post(HttpRequest request) throws IOException

    /**
     * Perform an HTTP PUT request.
     *
     * @param requestClosure Closure that configures the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    HttpResponse put(@DelegatesTo(HttpRequest) Closure requestClosure) throws IOException

    /**
     * Perform an HTTP PUT request.
     *
     * @param request Request properties to use with the HTTP request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    HttpResponse put(HttpRequest request) throws IOException

    /**
     * Perform an HTTP DELETE request.
     *
     * @param requestClosure Closure that configures the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    HttpResponse delete(@DelegatesTo(HttpRequest) Closure requestClosure) throws IOException

    /**
     * Perform an HTTP DELETE request.
     *
     * @param request Request properties to use with the HTTP request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    HttpResponse delete(HttpRequest request) throws IOException

    /**
     * Perform an HTTP OPTIONS request.
     *
     * @param requestClosure Closure that configures the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    HttpResponse options(@DelegatesTo(HttpRequest) Closure requestClosure) throws IOException

    /**
     * Perform an HTTP OPTIONS request.
     *
     * @param request Request properties to use with the HTTP request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    HttpResponse options(HttpRequest request) throws IOException

    /**
     * Perform an HTTP HEAD request.
     *
     * @param requestClosure Closure that configures the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    HttpResponse head(@DelegatesTo(HttpRequest) Closure requestClosure) throws IOException

    /**
     * Perform an HTTP HEAD request.
     *
     * @param request Request properties to use with the HTTP request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    HttpResponse head(HttpRequest request) throws IOException

    /**
     * Perform an HTTP TRACE request.
     *
     * @param requestClosure Closure that configures the request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    HttpResponse trace(@DelegatesTo(HttpRequest) Closure requestClosure) throws IOException

    /**
     * Perform an HTTP TRACE request.
     *
     * @param request Request properties to use with the HTTP request.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    HttpResponse trace(HttpRequest request) throws IOException

    /**
     * Adds a {@link HttpClientFilter} to the HTTP client.
     *
     * @param filter Filter instance to register with the client.
     * @return The object the method was called on.
     */
    HttpClient addFilter(HttpClientFilter filter)

    /**
     * Returns the list of all registered {@link HttpClientFilter} instances.
     *
     * @return The list of registered filter instances.
     */
    List<HttpClientFilter> getFilters()

    /**
     * Unregisters a {@link HttpClientFilter} from the HTTP client.
     *
     * @param filter Filter instance to remove from the client.
     * @return The object the method was called on.
     */
    HttpClient removeFilter(HttpClientFilter filter)

    /**
     * Removes all registered filters.
     *
     * @return The object the method was called on.
     */
    HttpClient clearFilters()

    /**
     * Adds an entity converter to the factory.
     *
     * @param converter Converter to add to the factory.
     */
    void addEntityConverter(EntityConverter converter)

    /**
     * Returns the list of entity converters.
     *
     * @return List of entity converters.
     */
    List<EntityConverter> getEntityConverters()

    /**
     * Remove an entity converter.
     *
     * @param converter Entity converter to remove.
     */
    void removeEntityConverter(EntityConverter converter)

    /**
     * Remove all entity converters.
     */
    void clearEntityConverters()

    /**
     * Creates an {@link HttpRequest} with all of its dependencies included.
     *
     * @return A new {@link HttpRequest}.
     */
    HttpRequest createHttpRequest()

    /**
     * Creates an {@link HttpRequest} and configures it with the given closure.
     *
     * @param closure Closure used to configure the request.
     * @return A new, configured {@link HttpRequest}.
     */
    HttpRequest createHttpRequest(@DelegatesTo(HttpRequest) Closure closure)

    /**
     * Create an {@link HttpEntity} instance for the given object and content type.
     * This method will attempt to convert the object with the converters registered
     * with the factory.
     *
     * @param object
     * @param contentType
     * @return
     */
    HttpEntity createEntity(Object object, ContentType contentType)

    /**
     * Create an {@link HttpEntity} instance for the given input stream and content type.
     *
     * @param inputStream
     * @param contentType
     * @return
     */
    HttpEntity createEntity(InputStream inputStream, ContentType contentType)
}