package com.budjb.httprequests

import java.nio.charset.Charset

/**
 * An object that represents a distinct request entity contained in an {@link InputStream},
 * with the accompanying <i>Content-Type</i>.
 */
class HttpEntity extends InputStream {
    /**
     * Default Content-Type if none is given.
     */
    static final String DEFAULT_CONTENT_TYPE = 'application/octet-stream'

    /**
     * Default character set if none is given.
     */
    static final String DEFAULT_CHARACTER_SET = Charset.defaultCharset().toString()

    /**
     * InputStream containing the request entity.
     */
    InputStream inputStream

    /**
     * Content-Type of the entity.
     */
    String contentType

    /**
     * Character set of the entity.
     */
    String charset

    /**
     * Constructor.
     *
     * @param inputStream
     * @param contentType
     * @param charset
     */
    HttpEntity(InputStream inputStream, String contentType, String charset) {
        if (inputStream == null) {
            throw new IllegalArgumentException("input stream must not be null")
        }
        this.inputStream = inputStream

        if (!contentType) {
            contentType = DEFAULT_CONTENT_TYPE
        }
        this.contentType = contentType

        if (!charset) {
            charset = DEFAULT_CHARACTER_SET
        }
        this.charset = charset
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
        return inputStream.read()
    }

    /**
     * Returns whether mark is supported.
     *
     * @return Whether mark is supported.
     */
    @Override
    boolean markSupported() {
        return inputStream.markSupported()
    }

    /**
     * Marks the current position in the input stream up to <code>readlimit</code> bytes.
     *
     * @param readlimit Number of bytes before the mark becomes invalid.
     */
    @Override
    synchronized void mark(int readlimit) {
        inputStream.mark(readlimit)
    }

    /**
     * Resets the {@link InputStream} to a previously set mark.
     */
    @Override
    synchronized void reset() {
        inputStream.reset()
    }

    /**
     * Returns whether the entity is buffered.
     *
     * @return Whether the entity is buffered.
     */
    boolean isBuffered() {
        return inputStream instanceof ByteArrayInputStream
    }

    /**
     * Buffers the {@link InputStream} into a {@link ByteArrayInputStream} so that it can be replayed.
     */
    void buffer() {
        if (!isBuffered()) {
            InputStream newInputStream = new ByteArrayInputStream(StreamUtils.readBytes(inputStream))
            inputStream.close()
            inputStream = newInputStream
        }
    }
}
