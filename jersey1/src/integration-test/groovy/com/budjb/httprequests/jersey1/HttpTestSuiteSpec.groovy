package com.budjb.httprequests.jersey1

import com.budjb.httprequests.HttpClientFactory
import com.budjb.httprequests.HttpIntegrationTestSuiteSpec
import com.budjb.httprequests.connection.HttpHandler
import com.sun.jersey.api.client.Client
import com.sun.jersey.api.client.ClientResponse
import com.sun.jersey.api.client.config.DefaultClientConfig
import com.sun.jersey.api.client.filter.LoggingFilter
import com.sun.jersey.client.urlconnection.HttpURLConnectionFactory
import com.sun.jersey.client.urlconnection.URLConnectionClientHandler

class HttpTestSuiteSpec extends HttpIntegrationTestSuiteSpec {
    /**
     * Create an HTTP client factory to use with tests.
     *
     * @return
     */
    @Override
    HttpClientFactory createHttpClientFactory() {
        return new JerseyHttpClientFactory()
    }

    def 'test some shit'() {
        expect:
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
        PrintStream printStream = new PrintStream(outputStream)

        URI uri = new URI('http://httpbin.org/patch')

        Client client = new Client(new URLConnectionClientHandler(new HttpURLConnectionFactory() {
            @Override
            java.net.HttpURLConnection getHttpURLConnection(URL u) throws IOException {
                return new URL(
                    u.getProtocol(),
                    u.getHost(),
                    u.getPort(),
                    u.getFile(),
                    new HttpHandler()
                ).openConnection() as java.net.HttpURLConnection
            }
        }), new DefaultClientConfig())

        client.addFilter(new LoggingFilter(printStream))
        def response = client.resource(uri).method('PATCH', ClientResponse, "test entity")
        println outputStream.toString()
        response
    }
}
