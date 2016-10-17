package com.budjb.httprequests.v2.support

import com.budjb.httprequests.v2.core.filter.bundled.LoggingFilter

class StringWriterLoggingFilter extends LoggingFilter {
    StringWriter stringWriter
    
    StringWriterLoggingFilter(StringWriter stringWriter) {
        this.stringWriter = stringWriter
    }

    @Override
    protected void log(String content) {
        stringWriter.write(content)
    }
}
