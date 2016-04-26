package com.budjb.httprequests.connection

import sun.net.www.protocol.https.Handler

/**
 * A custom URL connection handler that creates an HttpURLConnection instance that allows more
 * HTTP methods than the built-in Java HttpURLConnection.
 */
class HttpsHandler extends Handler{
    @Override
    protected URLConnection openConnection(URL u) throws IOException {
        return new HttpRequestsHttpURLConnection(u, this)
    }
}
