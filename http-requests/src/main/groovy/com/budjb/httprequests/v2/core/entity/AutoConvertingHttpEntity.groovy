package com.budjb.httprequests.v2.core.entity

import com.budjb.httprequests.v2.core.converter.EntityConverterManager

/**
 * A base class that handles auto-conversion of its payload.
 */
abstract class AutoConvertingHttpEntity extends AbstractHttpEntity implements ConvertingHttpEntity {
    /**
     * Returns the object that will be converted.
     *
     * @return
     */
    abstract Object getObject()

    /**
     * {@inheritDoc}
     */
    void convert(EntityConverterManager converterManager) {
        setInputStream(converterManager.write(this))
    }
}
