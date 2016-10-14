package com.budjb.httprequests.v2.core

import com.budjb.httprequests.v2.core.entity.GenericHttpEntity
import com.budjb.httprequests.v2.core.entity.HttpEntity
import com.budjb.httprequests.v2.core.entity.InputStreamHttpEntity
import com.budjb.httprequests.v2.core.entity.multipart.MultiPart
import com.budjb.httprequests.v2.core.entity.multipart.MultiPartEntity

class HttpRequestDelegate {
    /**
     * {@link HttpRequest} object to build.
     */
    private HttpRequest httpRequest

    /**
     * Constructor.
     *
     * @param httpRequest
     */
    HttpRequestDelegate(HttpRequest httpRequest) {
        this.httpRequest = httpRequest
    }

    /**
     * Sets the request URI.
     *
     * @param uri
     */
    void uri(String uri) {
        httpRequest.setUri(uri)
    }

    /**
     * Sets the request URI.
     *
     * @param uri
     */
    void uri(URI uri) {
        httpRequest.setUri(uri)
    }

    /**
     * Adds a header to the request.
     *
     * @param name Name of the header.
     * @param value Value of the headers.
     */
    void header(String name, String value) {
        httpRequest.addHeader(name, value)
    }

    /**
     * Adds a header with multiple values to the request.
     *
     * @param name Name of the header.
     * @param values Values of the header.
     */
    void header(String name, List<String> values) {
        httpRequest.addHeader(name, values)
    }

    /**
     * Sets the headers for the request.
     *
     * @param headers Request headers.
     */
    void headers(Map<String, Object> headers) {
        httpRequest.setHeaders(headers)
    }

    /**
     * Adds a query parameter to the request.
     *
     * @param name Name of the query parameter.
     * @param value Value of the query parameter.
     */
    void queryParameter(String name, String value) {
        httpRequest.addQueryParameter(name, value)
    }

    /**
     * Adds a query parameter with multiple values to the request.
     *
     * @param name Name of the query parameter.
     * @param values Values of the query parameter.
     */
    void queryParameter(String name, List<String> values) {
        httpRequest.addQueryParameter(name, values)
    }

    /**
     * Sets the query parameters of the request.
     *
     * @param queryParameters Query parameters.
     */
    void queryParameters(Map<String, Object> queryParameters) {
        httpRequest.setQueryParameters(queryParameters)
    }

    /**
     * Sets the accepted Content-Type.
     *
     * @param accept Accepted Content-Type.
     */
    void accept(String accept) {
        httpRequest.setAccept(accept)
    }

    /**
     * Sets the accepted Content-Type.
     *
     * @param accept Accepted Content-Type.
     */
    void accept(ContentType accept) {
        httpRequest.setAccept(accept)
    }

    /**
     * Sets the read timeout of the request.
     *
     * @param timeout Read timeout in milliseconds.
     */
    void readTimeout(int timeout) {
        httpRequest.setReadTimeout(timeout)
    }

    /**
     * Sets the connection timeout of the request.
     *
     * @param timeout Connection timeout in milliseconds.
     */
    void connectionTimeout(int timeout) {
        httpRequest.setConnectionTimeout(timeout)
    }

    /**
     * Sets whether SSL certificates and chains should be validated.
     *
     * @param sslValidated Whether SSL should be validated.
     */
    void sslValidated(boolean sslValidated) {
        httpRequest.setSslValidated(sslValidated)
    }

    /**
     * Sets whether the client should follow redirects.
     *
     * @param followRedirects
     */
    void followRedirects(boolean followRedirects) {
        httpRequest.setFollowRedirects(followRedirects)
    }

    /**
     * Sets the request entity.
     *
     * @param entity
     */
    void entity(Object entity) {
        httpRequest.setEntity(entity)
    }

    /**
     * Sets the request entity built from the given closure.
     *
     * @param closure
     */
    void entity(@DelegatesTo(HttpEntityDelegate) Closure closure) {
        HttpEntityDelegate delegate = new HttpEntityDelegate()

        closure = (Closure) closure.clone()
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.delegate = delegate
        closure.call()

        httpRequest.setEntity(delegate.build())
    }

    void multiPartEntity(Closure closure) {
        MultiPartHttpEntityDelegate delegate = new MultiPartHttpEntityDelegate()

        closure = (Closure) closure.clone()
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.delegate = delegate
        closure.call()

        httpRequest.setEntity(delegate.build())
    }

    class MultiPartHttpEntityDelegate {
        private List<MultiPart> parts = []

        void part(String name, Closure closure) {

        }

        void part(String name, Object object) {
        }

        void part(String name, InputStream inputStream) {

        }

        MultiPartEntity build() {
            return null
        }
    }

    class HttpEntityDelegate {
        private Object body
        private ContentType contentType

        void body(Object body) {
            this.body = body
        }

        void contentType(String contentType) {
            this.contentType = new ContentType(contentType)
        }

        HttpEntity build() {
            HttpEntity entity

            if (body instanceof InputStream) {
                entity = new InputStreamHttpEntity((InputStream) body)
            }
            else {
                entity = new GenericHttpEntity(body)
            }

            if (contentType) {
                entity.setContentType(contentType)
            }

            return entity
        }
    }
}
