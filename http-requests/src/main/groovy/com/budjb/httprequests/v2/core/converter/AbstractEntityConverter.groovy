package com.budjb.httprequests.v2.core.converter

import com.budjb.httprequests.v2.core.entity.ContentType

import java.nio.charset.Charset

/**
 * Base entity converter class that handles some of the common support logic.
 */
abstract class AbstractEntityConverter implements EntityConverter {
    /**
     * Returns the class type the converter supports.
     *
     * @return
     */
    protected abstract List<Class<?>> getSupportedTypes()

    /**
     * {@inheritDoc}
     */
    @Override
    boolean supports(Class<?> type) {
        return getSupportedTypes().any { return it.isAssignableFrom(type) }
    }

    /**
     * Returns a list of Content-Types that the converter supports.
     *
     * A converter should override this if supported content types makes sense.
     *
     * @return A list of supported content types.
     */
    protected List<ContentType> getSupportedContentTypes() {
        return []
    }

    /**
     * {@inheritDoc}
     */
    @Override
    boolean supports(ContentType contentType) {
        List<ContentType> supported = getSupportedContentTypes()

        if (!supported.size()) {
            return true
        }

        return supported.any { it.getType() == contentType.getType() }
    }

    /**
     * Returns the system default character set.
     *
     * @return
     */
    protected String getSystemCharset() {
        return Charset.defaultCharset().name()
    }
}
