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

import com.budjb.httprequests.v2.core.converter.EntityConverter
import com.budjb.httprequests.v2.core.converter.EntityConverterManager
import com.budjb.httprequests.v2.core.entity.ConvertingHttpEntity
import com.budjb.httprequests.v2.core.entity.HttpEntity
import com.budjb.httprequests.v2.core.filter.HttpClientFilter
import com.budjb.httprequests.v2.core.filter.HttpClientFilterManager

import javax.net.ssl.*
import java.security.SecureRandom
import java.security.cert.X509Certificate

/**
 * A base class for HTTP clients that implements most of the functionality of the {@link HttpClient} interface.
 *
 * Individual HTTP client library implementations should extend this class.
 */
abstract class AbstractHttpClient implements HttpClient {
    /**
     * Converter manager.
     */
    EntityConverterManager converterManager

    /**
     * Filter manager.
     */
    HttpClientFilterManager filterManager

    /**
     * Implements the logic to make an actual request with an HTTP client library.
     *
     * // TODO: revisit this and remove the entity
     *
     * @param context HTTP request context.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     * @throws IOException
     */
    protected
    abstract HttpResponse doExecute(HttpContext context) throws IOException

    /**
     * {@inheritDoc}
     */
    @Override
    HttpResponse execute(HttpMethod method, HttpRequest request) throws IOException {
        return run(method, request)
    }

    /**
     * {@inheritDoc}
     */
    @Override
    HttpResponse execute(HttpMethod method,
                         @DelegatesTo(HttpRequestDelegate) Closure requestClosure) throws IOException {
        return execute(method, HttpRequest.build(requestClosure))
    }

    /**
     * {@inheritDoc}
     */
    @Override
    HttpResponse get(HttpRequest request) throws IOException {
        return execute(HttpMethod.GET, request)
    }

    /**
     * {@inheritDoc}
     */
    @Override
    HttpResponse get(@DelegatesTo(HttpRequestDelegate) Closure requestClosure) throws IOException {
        return execute(HttpMethod.GET, requestClosure)
    }

    /**
     * {@inheritDoc}
     */
    @Override
    HttpResponse post(HttpRequest request) throws IOException {
        return execute(HttpMethod.POST, request)
    }

    /**
     * {@inheritDoc}
     */
    @Override
    HttpResponse post(@DelegatesTo(HttpRequestDelegate) Closure requestClosure) throws IOException {
        return execute(HttpMethod.POST, requestClosure)
    }

    /**
     * {@inheritDoc}
     */
    @Override
    HttpResponse put(HttpRequest request) throws IOException {
        return execute(HttpMethod.PUT, request)
    }

    /**
     * {@inheritDoc}
     */
    @Override
    HttpResponse put(@DelegatesTo(HttpRequestDelegate) Closure requestClosure) throws IOException {
        return execute(HttpMethod.PUT, requestClosure)
    }

    /**
     * {@inheritDoc}
     */
    @Override
    HttpResponse delete(HttpRequest request) throws IOException {
        return execute(HttpMethod.DELETE, request)
    }

    /**
     * {@inheritDoc}
     */
    @Override
    HttpResponse delete(@DelegatesTo(HttpRequestDelegate) Closure requestClosure) throws IOException {
        return execute(HttpMethod.DELETE, requestClosure)
    }

    /**
     * {@inheritDoc}
     */
    @Override
    HttpResponse options(HttpRequest request) throws IOException {
        return execute(HttpMethod.OPTIONS, request)
    }

    /**
     * {@inheritDoc}
     */
    @Override
    HttpResponse options(@DelegatesTo(HttpRequestDelegate) Closure requestClosure) throws IOException {
        return execute(HttpMethod.OPTIONS, requestClosure)
    }

    /**
     * {@inheritDoc}
     */
    @Override
    HttpResponse head(HttpRequest request) throws IOException {
        return execute(HttpMethod.HEAD, request)
    }

    /**
     * {@inheritDoc}
     */
    @Override
    HttpResponse head(@DelegatesTo(HttpRequestDelegate) Closure requestClosure) throws IOException {
        return execute(HttpMethod.HEAD, requestClosure)
    }

    /**
     * {@inheritDoc}
     */
    @Override
    HttpResponse trace(HttpRequest request) throws IOException {
        return execute(HttpMethod.TRACE, request)
    }

    /**
     * {@inheritDoc}
     */
    @Override
    HttpResponse trace(@DelegatesTo(HttpRequestDelegate) Closure requestClosure) throws IOException {
        return execute(HttpMethod.TRACE, requestClosure)
    }

    /**
     * {@inheritDoc}
     */
    @Override
    HttpClient addFilter(HttpClientFilter filter) {
        filterManager.add(filter)
        return this
    }

    /**
     * {@inheritDoc}
     */
    @Override
    HttpClient removeFilter(HttpClientFilter filter) {
        filterManager.remove(filter)
        return this
    }

    /**
     * {@inheritDoc}
     */
    @Override
    List<HttpClientFilter> getFilters() {
        return filterManager.getAll()
    }

    /**
     * {@inheritDoc}
     */
    @Override
    HttpClient clearFilters() {
        filterManager.clear()
        return this
    }

    /**
     * {@inheritDoc}
     */
    void addEntityConverter(EntityConverter converter) {
        converterManager.add(converter)
    }

    /**
     * {@inheritDoc}
     */
    List<EntityConverter> getEntityConverters() {
        return converterManager.getAll()
    }

    /**
     * {@inheritDoc}
     */
    void removeEntityConverter(EntityConverter converter) {
        converterManager.remove(converter)
    }

    /**
     * {@inheritDoc}
     */
    void clearEntityConverters() {
        converterManager.clear()
    }

    /**
     * Orchestrates making the HTTP request. Fires appropriate filter events and hands off to the implementation
     * to perform the actual HTTP request.
     *
     * @param method HTTP request method.
     * @param request {@link HttpRequest} object to configure the request.
     * @param entity Request entity.
     * @return A {@link HttpResponse} object containing the properties of the server response.
     */
    protected HttpResponse run(HttpMethod method, HttpRequest request) {
        HttpEntity entity = request.getEntity()

        HttpContext context = new HttpContext()
        context.setMethod(method)

        if (entity != null) {
            // It is important that conversion happen now for entities of the ConvertingHttpEntity type.
            // If this does not happen, the entity's InputStream will most likely not be set, which
            // will cause failures downstream.
            if (entity instanceof ConvertingHttpEntity) {
                entity.convert(getConverterManager())
            }

            // Requests whose client contains a retry filter must have their entity buffered.
            // If it is not, the retried request will either throw an error due to the entity
            // input stream being closed, or the entity will not actually transmit. So, requests
            // that could potentially be retried are automatically read into a ByteArrayInputStream
            // so that it can be transmitted more than once.
            if (filterManager.hasRetryFilters()) {
                entity.buffer()
            }
        }

        while (true) {
            HttpRequest newRequest = request.clone() as HttpRequest
            context.setRequest(newRequest)
            context.setResponse(null)

            filterManager.filterHttpRequest(context)

            // Requests that do not include an entity should still have their
            // {@link HttpClientLifecycleFilter#onRequest} method called. If the request does
            // contain an entity, it is the responsibility of the implementation to make a call
            // to {@link #filterOutputStream}.
            if (!entity) {
                filterManager.onRequest(context, null)
            }

            // Note that {@link HttpClientRequestEntityFilter#filterRequestEntity} and
            // {@link HttpClientLifecycleFilter#onRequest} should be initiated from the
            // client implementation, and will occur during the execution started below.
            HttpResponse response = doExecute(context)

            context.setResponse(response)

            filterManager.filterHttpResponse(context)

            filterManager.onResponse(context)

            if (!filterManager.onRetry(context)) {
                filterManager.onComplete(context)
                return response
            }

            context.incrementRetries()
        }
    }

    /**
     * Filter the output stream.
     *
     * @param context HTTP request context.
     * @param outputStream Output stream of the request.
     */
    protected OutputStream filterOutputStream(HttpContext context, OutputStream outputStream) {
        outputStream = filterManager.filterRequestEntity(context, outputStream)

        filterManager.onRequest(context, outputStream)

        return outputStream
    }

    /**
     * Create and return an all-trusting TLS {@link SSLContext}.
     *
     * @return An all-trusting TLS {@link SSLContext}.
     */
    protected SSLContext createTrustingSSLContext() {
        TrustManager[] certs = [new X509TrustManager() {
            X509Certificate[] getAcceptedIssuers() {
                null
            }

            void checkClientTrusted(X509Certificate[] certs, String authType) {}

            void checkServerTrusted(X509Certificate[] certs, String authType) {}
        }]

        SSLContext sslContext = SSLContext.getInstance('TLS')
        sslContext.init(null, certs, new SecureRandom())

        return sslContext
    }

    /**
     * Create and return an all-trusting {@link HostnameVerifier}.
     *
     * @return An all-trusting {@link HostnameVerifier}.
     */
    protected HostnameVerifier createTrustingHostnameVerifier() {
        return new HostnameVerifier() {
            boolean verify(String hostname, SSLSession session) {
                return true
            }
        }
    }
}
