package com.budjb.httprequests.v2.core

class ContentType {
    /**
     * Name of the character set parameter.
     */
    final private static String CHARSET_NAME = 'charset'

    /**
     * Set of special characters that MUST be quoted in parameter values to be used.
     */
    final private
    static List<String> SPECIAL_CHARACTERS = ["(", ")", "<", ">", "@", ",", ";", ":", "\\", "\"", "/", "[", "]", "?", ".", "="]

    /**
     * Convenience Content-Type for application/octet-stream.
     */
    final static ContentType APPLICATION_OCTET_STREAM = new ContentType('application/octet-stream')

    /**
     * Convenience Content-Type for application/json.
     */
    final static ContentType APPLICATION_JSON = new ContentType('application/json')

    /**
     * Convenience Content-Type for text/xml.
     */
    final static ContentType TEXT_XML = new ContentType('text/xml')

    /**
     * Convenience Content-Type for text/plain.
     */
    final static ContentType TEXT_PLAIN = new ContentType('text/plain')

    /**
     * MIME-type of the Content-Type.
     */
    String type

    /**
     * Content-Type parameters.
     */
    private Map<String, String> parameters = [:]

    /**
     * Parses a Content-Type from a {@link String} into a {@link ContentType}.
     *
     * @param contentType The Content-Type as a String.
     * @return
     */
    static ContentType parse(String contentType) {
        if (!contentType) {
            throw new IllegalArgumentException('contentType is required');
        }

        List<String> tokens = contentType.tokenize(';')

        String type = tokens.remove(0).trim()

        Map<String, String> parameters = tokens.collectEntries { parameter ->
            List<String> attrParts = parameter.tokenize('=')

            if (attrParts.size() != 2) {
                throw new IllegalArgumentException('parameter must have a value')
            }

            return [(attrParts[0].trim()): attrParts[1].trim()]
        }

        return new ContentType(type, parameters)
    }

    /**
     * Constructor that builds the Content-Type with only a MIME-type.
     *
     * @param type MIME-type of the Content-Type.
     */
    ContentType(String type) {
        this(type, null, [:])
    }

    /**
     * Constructor that builds the Content-Type with a character set.
     *
     * @param type MIME-type of the Content-Type.
     * @param charset Character set of the Content-Type.
     */
    ContentType(String type, String charset) {
        this(type, charset, [:])
    }

    /**
     * Constructor that build the Content-Type with a set of parameters.
     *
     * @param type MIME-type of the Content-Type.
     * @param parameters Parameters of the Content-Type.
     */
    ContentType(String type, Map<String, String> parameters) {
        this(type, null, parameters)
    }

    /**
     * Constructor that builds the Content-Type with a character set and a set of parameters.
     *
     * @param type MIME-type of the Content-Type.
     * @param charset Character set of the Content-Type.
     * @param parameters Parameters of the Content-Type.
     */
    ContentType(String type, String charset, Map<String, String> parameters) {
        this.type = type

        if (parameters == null) {
            parameters = [:]
        }

        if (charset) {
            parameters.put(CHARSET_NAME, charset)
        }

        this.parameters = parameters
    }

    /**
     * Returns the character set of the Content-Type.
     *
     * @return Character set, or null if none is set.
     */
    String getCharset() {
        return getParameter(CHARSET_NAME)
    }

    /**
     * Sets the character set of the Content-Type.
     *
     * @param charset Character set of the Content-Type.
     */
    void setCharset(String charset) {
        setParameter(CHARSET_NAME, charset)
    }

    /**
     * Returns a parameter with the given name.
     *
     * @param name Name of the parameter.
     * @return Value of the parameter, or null if it isn't set.
     */
    String getParameter(String name) {
        if (parameters.containsKey(name)) {
            return parameters.get(name)
        }
        return null
    }

    /**
     * Sets a parameter with the given name and value.
     *
     * @param name Name of the parameter.
     * @param value Value of the parameter.
     */
    void setParameter(String name, String value) {
        if (!value) {
            if (parameters.containsKey(name)) {
                parameters.remove(name)
            }
        }
        else {
            parameters.put(name, value)
        }
    }

    /**
     * Outputs the complete Content-Type.
     *
     * @return
     */
    @Override
    String toString() {
        StringBuilder builder = new StringBuilder()

        builder.append(type)

        if (parameters) {
            parameters.each { key, value ->
                builder.append('; ')
                builder.append(key)
                builder.append('=')
                if (SPECIAL_CHARACTERS.any { value.contains(it) }) {
                    builder.append('"')
                    builder.append(value.replaceAll(/"/, '\\"'))
                    builder.append('"')
                }
                else {
                    builder.append(value)
                }
            }
        }

        return builder.toString()
    }

    /**
     * Add parameters to the Content-Type.
     *
     * @param parameters
     */
    void addParameters(Map<String, String> parameters) {
        if (parameters) {
            this.parameters.putAll(parameters)
        }
    }
}
