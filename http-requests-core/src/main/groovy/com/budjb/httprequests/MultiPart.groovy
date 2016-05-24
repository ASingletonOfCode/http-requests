package com.budjb.httprequests

import com.budjb.httprequests.converter.EntityConverterManager

class MultiPart extends InputStream {
    /**
     * Entity converter manager.
     */
    EntityConverterManager converterManager

    /**
     * Entries in the multipart entity.
     */
    List<HttpEntity> entities = []

    /**
     * Boundary of the entity.
     */
    String boundary

    /**
     * Constructor.
     *
     * @param converterManager Converter manager.
     */
    MultiPart(EntityConverterManager converterManager) {
        this.converterManager = converterManager
        this.boundary = createBoundary()
    }

    /**
     * Reads the next byte of data from the input stream. The value byte is
     * returned as an <code>int</code> in the range <code>0</code> to
     * <code>255</code>. If no byte is available because the end of the stream
     * has been reached, the value <code>-1</code> is returned. This method
     * blocks until input data is available, the end of the stream is detected,
     * or an exception is thrown.
     *
     * <p> A subclass must provide an implementation of this method.
     *
     * @return the next byte of data, or <code>-1</code> if the end of the
     *             stream is reached.
     * @exception IOException  if an I/O error occurs.
     */
    @Override
    int read() throws IOException {
        return 0 // TODO: make this happen
    }

    /**
     * Add a multipart entry.
     *
     * @param inputStream Input stream containing the multipart entry.
     * @param contentType Content-Type of the multipart entry.
     * @return The object this method was called on.
     */
    MultiPart add(HttpEntity entity) {
        entities.add(entity)
        return this
    }

    /**
     * Add a multipart entry.
     *
     * @param object Object to add to the multipart entity.
     * @param contentType Content-Type of the multipart entry.
     * @param charset Character set of the entity.
     * @return The object this method was called on.
     */
    MultiPart add(Object object, String contentType = null, String charset = null) {
        return add(converterManager.write(object, contentType, charset))
    }

    /**
     * Return a new boundary based on the current system time.
     *
     * @return
     */
    String createBoundary() {
        return "------${System.currentTimeMillis()}"
    }

    /**
     * Disable mark support.
     *
     * @return <code>false</code>, always.
     */
    @Override
    boolean markSupported() {
        return false
    }
}
