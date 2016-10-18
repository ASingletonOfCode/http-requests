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

import com.budjb.httprequests.v2.MockHttpResponse
import com.budjb.httprequests.v2.core.converter.EntityConverterManager
import com.budjb.httprequests.v2.core.exception.*
import spock.lang.Specification
import spock.lang.Unroll

class HttpResponseExceptionSpec extends Specification {
    @Unroll
    def 'When an HttpResponseException is build with status #status, exception type #type is returned'() {
        setup:
        HttpResponse response = new MockHttpResponse(
            new EntityConverterManager(),
            status,
            [:],
            null,
            null
        )

        expect:
        HttpStatusException.build(response).getClass() == type

        where:
        type                                      | status
        HttpMultipleChoicesException              | 300
        HttpMovedPermanentlyException             | 301
        HttpFoundException                        | 302
        HttpSeeOtherException                     | 303
        HttpNotModifiedException                  | 304
        HttpUseProxyException                     | 305
        HttpTemporaryRedirectException            | 307
        HttpBadRequestException                   | 400
        HttpUnauthorizedException                 | 401
        HttpPaymentRequiredException              | 402
        HttpForbiddenException                    | 403
        HttpNotFoundException                     | 404
        HttpMethodNotAllowedException             | 405
        HttpNotAcceptableException                | 406
        HttpProxyAuthenticationRequiredException  | 407
        HttpRequestTimeoutException               | 408
        HttpConflictException                     | 409
        HttpGoneException                         | 410
        HttpLengthRequiredException               | 411
        HttpPreconditionFailedException           | 412
        HttpRequestEntityTooLargeException        | 413
        HttpRequestUriTooLongException            | 414
        HttpUnsupportedMediaTypeException         | 415
        HttpRequestedRangeNotSatisfiableException | 416
        HttpExpectationFailedException            | 417
        HttpUnprocessableEntityException          | 422
        HttpInternalServerErrorException          | 500
        HttpNotImplementedException               | 501
        HttpBadGatewayException                   | 502
        HttpServiceUnavailableException           | 503
        HttpGatewayTimeoutException               | 504
        HttpHttpVersionNotSupportedException      | 505
        HttpStatusException                       | 700
    }
}
