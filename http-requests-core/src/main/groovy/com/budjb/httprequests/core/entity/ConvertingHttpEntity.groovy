package com.budjb.httprequests.core.entity

import com.budjb.httprequests.converter.EntityConverterManager

abstract class ConvertingHttpEntity extends AbstractHttpEntity {
    /**
     * Returns the object that will be converted.
     *
     * @return
     */
    abstract Object getObject()

    /**
     * Converts the entity and stores it in the entity's {@link InputStream}.
     *
     * @param converterManager
     */
    void convert(EntityConverterManager converterManager) {
        inputStream = converterManager.convertHttpEntity(this, getObject())
    }
}