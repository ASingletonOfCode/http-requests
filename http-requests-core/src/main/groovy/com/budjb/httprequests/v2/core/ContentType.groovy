package com.budjb.httprequests.v2.core

import com.budjb.httprequests.v2.util.StateMachine

class ContentType {
    /**
     * Name of the character set parameter.
     */
    final private static String CHARSET_NAME = 'charset'

    /**
     * Default character set when no other is provided.
     */
    static final DEFAULT_CHARSET = 'ISO-8859-1'

    /**
     * Set of special characters that MUST be quoted in parameter values to be used.
     */
    final private static String SPECIAL_CHARACTERS = '()<>@,;:\\"/[]?='
    final private
    static String TOKEN_CHARACTERS = 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!#$%^&*\'+-_`{}|~'

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
     * Convenience Content-Type for application/xml.
     */
    final static ContentType APPLICATION_XML = new ContentType('application/xml')

    /**
     * Convenience Content-Type for text/plain.
     */
    final static ContentType TEXT_PLAIN = new ContentType('text/plain')

    /**
     * Multi-part, form-data.
     */
    final static ContentType MULTI_PART_FORM_DATA = new ContentType('multipart/form-data')

    /**
     * MIME-type of the Content-Type.
     */
    String type

    /**
     * Content-Type parameters.
     */
    Map<String, String> parameters = [:]

    /**
     * Constructor that builds the Content-Type with only a MIME-type.
     *
     * @param type MIME-type of the Content-Type.
     */
    ContentType(String type) {
        parse(type)
    }

    /**
     * Parses a Content-Type from the given string.
     *
     * @param contentType
     */
    protected void parse(String contentType) {
        contentType = contentType.trim()

        StateMachine<ParserState, ParserEvent> stateMachine = createParserStateMachine()

        StringBuilder primaryType = new StringBuilder()
        StringBuilder subType = new StringBuilder()

        StringBuilder key = new StringBuilder()
        StringBuilder value = new StringBuilder()

        Iterator<String> it = contentType.iterator()

        while (it.hasNext()) {
            String character = it.next()

            switch (stateMachine.getCurrentState()) {
                case ParserState.PRIMARY_TYPE:
                    if (!isTokenChar(character)) {
                        if (character == '/') {
                            stateMachine.fire(ParserEvent.TYPE_SEPARATOR)
                            break
                        }
                        else {
                            throw new IllegalStateException("expected token character but received ")
                        }
                    }
                    else {
                        primaryType.append(character)
                    }
                    break

                case ParserState.SUB_TYPE:
                    if (!isTokenChar(character)) {
                        if (character == ';') {
                            stateMachine.fire(ParserEvent.PARAMETER_SEPARATOR)
                            break
                        }
                        else {
                            throw new IllegalStateException("expected token character but received '${character}'")
                        }
                    }
                    else {
                        subType.append(character)
                    }
                    break

                case ParserState.KEY_LEADING_WHITE_SPACE:
                    if (character != ' ') {
                        stateMachine.fire(ParserEvent.TOKEN)

                        if (isTokenChar(character)) {
                            key.append(character)
                        }
                        else {
                            throw new IllegalStateException("expected token character or space but received '${character}'")
                        }
                    }
                    break

                case ParserState.KEY:
                    if (!isTokenChar(character)) {
                        if (character == '=') {
                            stateMachine.fire(ParserEvent.EQUALS)
                            break
                        }
                        else if (character == ' ') {
                            stateMachine.fire(ParserEvent.WHITE_SPACE)
                            break
                        }
                        else {
                            throw new IllegalStateException("expected token character but received '${character}'")
                        }
                    }
                    else {
                        key.append(character)
                    }
                    break

                case ParserState.KEY_ENDING_WHITE_SPACE:
                    if (character != ' ') {
                        if (character == '=') {
                            stateMachine.fire(ParserEvent.EQUALS)
                            break
                        }
                        else {
                            throw new IllegalStateException("expected space or = character but received '${character}'")
                        }
                    }
                    break

                case ParserState.VALUE_LEADING_WHITE_SPACE:
                    if (character != ' ') {
                        if (character == '"') {
                            stateMachine.fire(ParserEvent.QUOTE)
                            break
                        }
                        else if (isTokenChar(character)) {
                            stateMachine.fire(ParserEvent.TOKEN)
                            value.append(character)
                        }
                        else {
                            throw new IllegalStateException("expected space, \", or token character but received '${character}'")
                        }
                    }
                    break

                case ParserState.VALUE:
                    if (!isTokenChar(character)) {
                        if (character == ' ' || character == ';') {
                            if (character == ' ') {
                                stateMachine.fire(ParserEvent.WHITE_SPACE)
                            }
                            else {
                                stateMachine.fire(ParserEvent.PARAMETER_SEPARATOR)
                            }

                            parameters.put(key.toString(), value.toString())

                            key = new StringBuilder()
                            value = new StringBuilder()
                        }
                        else if (isSpecialChar(character)) {
                            throw new IllegalStateException("expected token character but got special character '${character}'")
                        }
                    }
                    else {
                        value.append(character)
                    }
                    break

                case ParserState.VALUE_ENDING_WHITE_SPACE:
                    if (character != ' ') {
                        if (character == ';') {
                            stateMachine.fire(ParserEvent.PARAMETER_SEPARATOR)
                            break
                        }
                        else {
                            throw new IllegalStateException("expected ; character but received '${character}'")
                        }
                    }
                    break

                case ParserState.VALUE_QUOTED:
                    if (character == '"') {
                        stateMachine.fire(ParserEvent.QUOTE)

                        parameters.put(key.toString(), value.toString())

                        key = new StringBuilder()
                        value = new StringBuilder()
                    }
                    else if (character == '\\') {
                        stateMachine.fire(ParserEvent.ESCAPE)
                    }
                    else if (!isTokenChar(character) && !isSpecialChar(character)) {
                        throw new IllegalStateException("expected token or special character but got '${character}'")
                    }
                    else {
                        value.append(character)
                    }
                    break

                case ParserState.VALUE_QUOTED_ESCAPE:
                    if (character != '"') {
                        throw new IllegalStateException("expected \" character but got '${character}'")
                    }
                    value.append(character)
                    stateMachine.fire(ParserEvent.TOKEN)
                    break

                default:
                    throw new IllegalStateException("invalid state ${stateMachine.getCurrentState().toString()}")
            }
        }

        // The value may be incomplete if the end of the string is reached.
        if (stateMachine.getCurrentState() == ParserState.VALUE) {
            parameters.put(key.toString(), value.toString())
        }

        // Fire the end string event so that exceptions can occur if necessary.
        stateMachine.fire(ParserEvent.END_STRING)

        // If the parser state isn't done, the content type is malformed.
        if (stateMachine.getCurrentState() != ParserState.DONE) {
            throw new IllegalStateException("immature")
        }

        // Finally set the type.
        this.type = "${primaryType.toString()}/${subType.toString()}"
    }

    /**
     * Returns whether the given character is considered a token character.
     *
     * @param character
     * @return
     */
    protected boolean isTokenChar(String character) {
        return TOKEN_CHARACTERS.contains(character)
    }

    /**
     * Returns whether the given character is considered a special character.
     *
     * @param character
     * @return
     */
    protected boolean isSpecialChar(String character) {
        return SPECIAL_CHARACTERS.contains(character)
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

    /**
     * Creates and configures a new state machine to parse a string Content-Type.
     *
     * @return
     */
    protected StateMachine<ParserState, ParserEvent> createParserStateMachine() {
        StateMachine<ParserState, ParserEvent> stateMachine = new StateMachine<>(ParserState.PRIMARY_TYPE)

        // Primary type states
        stateMachine.from(ParserState.PRIMARY_TYPE)
            .on(ParserEvent.TYPE_SEPARATOR).to(ParserState.SUB_TYPE)
            .on(ParserEvent.END_STRING).to(ParserState.DONE)

        // Subtype states
        stateMachine.from(ParserState.SUB_TYPE).on(ParserEvent.PARAMETER_SEPARATOR).to(ParserState.KEY_LEADING_WHITE_SPACE)

        // Key state
        stateMachine.from(ParserState.KEY_LEADING_WHITE_SPACE)
            .on(ParserEvent.TOKEN).to(ParserState.KEY)
            .on(ParserEvent.WHITE_SPACE).to(ParserState.KEY_ENDING_WHITE_SPACE)
            .on(ParserEvent.EQUALS).to(ParserState.VALUE_LEADING_WHITE_SPACE)
        stateMachine.from(ParserState.KEY).on(ParserEvent.EQUALS).to(ParserState.VALUE_LEADING_WHITE_SPACE)

        // Value states
        stateMachine.from(ParserState.VALUE_LEADING_WHITE_SPACE).on(ParserEvent.TOKEN).to(ParserState.VALUE)

        // Non-quoted value ending states
        stateMachine.from(ParserState.VALUE).on(ParserEvent.WHITE_SPACE).to(ParserState.VALUE_ENDING_WHITE_SPACE)
        stateMachine.from(ParserState.VALUE).on(ParserEvent.PARAMETER_SEPARATOR).to(ParserState.KEY_LEADING_WHITE_SPACE)
        stateMachine.from(ParserState.VALUE).on(ParserEvent.END_STRING).to(ParserState.DONE)

        // Quoted value states
        stateMachine.from(ParserState.VALUE_LEADING_WHITE_SPACE)
            .on(ParserEvent.QUOTE).to(ParserState.VALUE_QUOTED)
            .on(ParserEvent.ESCAPE).to(ParserState.VALUE_QUOTED_ESCAPE)
            .on(ParserEvent.TOKEN).to(ParserState.VALUE_QUOTED)
            .on(ParserEvent.QUOTE).to(ParserState.VALUE_ENDING_WHITE_SPACE)

        // Value ending white space states
        stateMachine.from(ParserState.VALUE_ENDING_WHITE_SPACE).on(ParserEvent.END_STRING).to(ParserState.DONE)
        stateMachine.from(ParserState.VALUE_ENDING_WHITE_SPACE).on(ParserEvent.PARAMETER_SEPARATOR).to(ParserState.KEY_LEADING_WHITE_SPACE)

        return stateMachine
    }

    /**
     * Parser state machine states.
     */
    protected static enum ParserState {
        PRIMARY_TYPE,
        SUB_TYPE,
        KEY_LEADING_WHITE_SPACE,
        KEY,
        KEY_ENDING_WHITE_SPACE,
        VALUE_LEADING_WHITE_SPACE,
        VALUE,
        VALUE_QUOTED,
        VALUE_QUOTED_ESCAPE,
        VALUE_ENDING_WHITE_SPACE,
        DONE
    }

    /**
     * Parser state machine events.
     */
    protected static enum ParserEvent {
        TYPE_SEPARATOR,
        WHITE_SPACE,
        TOKEN,
        PARAMETER_SEPARATOR,
        EQUALS,
        QUOTE,
        ESCAPE,
        END_STRING
    }
}
