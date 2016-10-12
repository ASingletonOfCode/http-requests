package com.budjb.httprequests.v2.core

import com.budjb.httprequests.v2.core.entity.GenericHttpEntity
import com.budjb.httprequests.v2.core.entity.HttpEntity
import com.budjb.httprequests.v2.core.entity.InputStreamHttpEntity

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

    void uri(String uri) {
        httpRequest.setUri(uri)
    }

    void uri(URI uri) {
        httpRequest.setUri(uri)
    }

    void header(String name, String value) {
        httpRequest.addHeader(name, value)
    }

    void header(String name, List<String> values) {
        httpRequest.addHeader(name, values)
    }

    void headers(Map<String, Object> headers) {
        httpRequest.setHeaders(headers)
    }

    void queryParameter(String name, String value) {
        httpRequest.addQueryParameter(name, value)
    }

    void queryParameter(String name, List<String> values) {
        httpRequest.addQueryParameter(name, values)
    }

    void queryParameters(Map<String, Object> queryParameters) {
        httpRequest.setQueryParameters(queryParameters)
    }

    void accept(String accept) {
        httpRequest.setAccept(accept)
    }

    void accept(ContentType accept) {
        httpRequest.setAccept(accept)
    }

    void readTimeout(int timeout) {
        httpRequest.setReadTimeout(timeout)
    }

    void connectionTimeout(int timeout) {
        httpRequest.setConnectionTimeout(timeout)
    }

    void sslValidated(boolean sslValidated) {
        httpRequest.setSslValidated(sslValidated)
    }

    void followRedirects(boolean followRedirects) {
        httpRequest.setFollowRedirects(followRedirects)
    }

    void bufferResponseEntity(boolean bufferResponseEntity) {
        httpRequest.setBufferResponseEntity(bufferResponseEntity)
    }

    void entity(Object entity) {
        httpRequest.setEntity(entity)
    }

    void entity(InputStream entity) {
        httpRequest.setEntity(entity)
    }

    void entity(HttpEntity entity) {
        httpRequest.setEntity(entity)
    }

    void entity(@DelegatesTo(HttpEntityDelegate) Closure closure) {
        HttpEntityDelegate delegate = new HttpEntityDelegate()

        closure = (Closure) closure.clone()
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.delegate = delegate
        closure.call()

        httpRequest.setEntity(delegate.build())
    }

    void multiPartEntity(Closure closure) {
        // TODO
    }

    class HttpEntityDelegate {
        private Object body
        private ContentType contentType

        void body(Object body) {
            this.body = body
        }

        void contentType(String contentType) {
            this.contentType = ContentType.parse(contentType)
        }

        HttpEntity build() {
            HttpEntity entity

            if (body instanceof InputStream) {
                entity = InputStreamHttpEntity(body)
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
