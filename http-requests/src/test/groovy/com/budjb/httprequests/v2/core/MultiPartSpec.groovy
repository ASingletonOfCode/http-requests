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

import com.budjb.httprequests.v2.core.entity.ContentType
import com.budjb.httprequests.v2.core.entity.multipart.InputStreamFormPart
import com.budjb.httprequests.v2.core.entity.multipart.MultiPart
import com.budjb.httprequests.v2.core.entity.multipart.MultiPartEntity
import com.budjb.httprequests.v2.core.entity.multipart.MultiPartFormDataEntity
import com.budjb.httprequests.v2.util.StreamUtils
import spock.lang.Ignore
import spock.lang.Specification

class MultiPartSpec extends Specification {
    @Ignore
    def 'does this even work?'() {
        setup:
        MultiPart part1 = new InputStreamFormPart(new ByteArrayInputStream("I am a text file.".getBytes()), ContentType.TEXT_PLAIN)
        MultiPart part2 = new InputStreamFormPart('jsonfile', new ByteArrayInputStream('{"foo":"bar"}'.getBytes()), ContentType.APPLICATION_JSON)

        MultiPartEntity multiPartEntity = new MultiPartFormDataEntity()
        multiPartEntity.boundary = '---boundary'
        multiPartEntity.addPart(part1)
        multiPartEntity.addPart(part2)

        expect:
        StreamUtils.readString(multiPartEntity.getInputStream(), 'UTF-8') == '''---boundary
Content-Disposition: form-data; name="textfile"
Content-Type: text/plain

I am a text file.
---boundary
Content-Disposition: form-data; name="jsonfile"
Content-Type: application/json

{"foo":"bar"}
---boundary--'''
    }
}
