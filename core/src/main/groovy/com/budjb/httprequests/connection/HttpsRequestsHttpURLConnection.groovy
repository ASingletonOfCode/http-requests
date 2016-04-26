package com.budjb.httprequests.connection

import sun.net.www.protocol.http.Handler
import sun.net.www.protocol.http.HttpURLConnection
import sun.net.www.protocol.https.HttpsURLConnectionImpl

/**
 * A custom HttpURLConnection class that allows more HTTP methods than is allowed by
 * the built-in Java HttpURLConnection objects.
 */
class HttpsRequestsHttpURLConnection extends HttpsURLConnectionImpl {
    /**
     * Constructor that builds the connection from a URL.
     *
     * @param url URL of the connection.
     * @param handler Handler of the request.
     */
    HttpsRequestsHttpURLConnection(URL url, Handler handler) {
        super(url, handler)
    }

    /**
     * Sets the HTTP method of the request.
     *
     * @param method
     * @throws ProtocolException
     */
    public synchronized void setRequestMethod(String method) throws ProtocolException {
        this.method = method
    }
}