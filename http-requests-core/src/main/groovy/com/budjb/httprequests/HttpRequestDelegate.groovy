package com.budjb.httprequests

import com.budjb.httprequests.core.ContentType
import com.budjb.httprequests.core.HttpRequest

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

    void query(String name, String value) {
        httpRequest.addQueryParameter(name, value)
    }

    void query(String name, List<String> values) {
        httpRequest.addQueryParameter(name, values)
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
}
