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
package com.budjb.httprequests.v2.core.entity

import com.budjb.httprequests.v2.util.StateMachine

/**
 * Represents a Content-Type and its parameters.
 */
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
     * Convenience Content-Type for application/json.
     */
    final static ContentType APPLICATION_JSON = new ContentType('application/json')

    /**
     * Convenience Content-Type for application/octet-stream.
     */
    final static ContentType APPLICATION_OCTET_STREAM = new ContentType('application/octet-stream')

    /**
     * Convenience Content-Type for application/xml.
     */
    final static ContentType APPLICATION_XML = new ContentType('application/xml')

    /**
     * Convenience Content-Type for application/x-www-form-urlencoded.
     */
    final static ContentType APPLICATION_X_WWW_FORM_URLENCODED = new ContentType('application/x-www-form-urlencoded')

    /**
     * Convenience Content-Type for text/plain.
     */
    final static ContentType TEXT_PLAIN = new ContentType('text/plain')

    /**
     * Convenience Content-Type for text/xml.
     */
    final static ContentType TEXT_XML = new ContentType('text/xml')

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
                            fireEvent(contentType, stateMachine, ParserEvent.TYPE_SEPARATOR)
                        }
                        else {
                            throw new IllegalArgumentException("expected token character but received '${character}'")
                        }
                    }
                    else {
                        primaryType.append(character)
                    }
                    break

                case ParserState.SUB_TYPE:
                    if (!isTokenChar(character)) {
                        if (character == ';') {
                            fireEvent(contentType, stateMachine, ParserEvent.PARAMETER_SEPARATOR)
                        }
                        else {
                            throw new IllegalArgumentException("expected token character but received '${character}'")
                        }
                    }
                    else {
                        subType.append(character)
                    }
                    break

                case ParserState.KEY_LEADING_WHITE_SPACE:
                    if (character != ' ') {
                        fireEvent(contentType, stateMachine, ParserEvent.TOKEN)

                        if (isTokenChar(character)) {
                            key.append(character)
                        }
                        else {
                            throw new IllegalArgumentException("expected token character or space but received '${character}'")
                        }
                    }
                    break

                case ParserState.KEY:
                    if (!isTokenChar(character)) {
                        if (character == '=') {
                            fireEvent(contentType, stateMachine, ParserEvent.EQUALS)
                        }
                        else if (character == ' ') {
                            fireEvent(contentType, stateMachine, ParserEvent.WHITE_SPACE)
                        }
                        else {
                            throw new IllegalArgumentException("expected token character but received '${character}'")
                        }
                    }
                    else {
                        key.append(character)
                    }
                    break

                case ParserState.KEY_ENDING_WHITE_SPACE:
                    if (character != ' ') {
                        if (character == '=') {
                            fireEvent(contentType, stateMachine, ParserEvent.EQUALS)
                        }
                        else {
                            throw new IllegalArgumentException("expected space or = character but received '${character}'")
                        }
                    }
                    break

                case ParserState.VALUE_LEADING_WHITE_SPACE:
                    if (character != ' ') {
                        if (character == '"') {
                            fireEvent(contentType, stateMachine, ParserEvent.QUOTE)
                        }
                        else if (isTokenChar(character)) {
                            fireEvent(contentType, stateMachine, ParserEvent.TOKEN)
                            value.append(character)
                        }
                        else {
                            throw new IllegalArgumentException("expected space, \", or token character but received '${character}'")
                        }
                    }
                    break

                case ParserState.VALUE:
                    if (!isTokenChar(character)) {
                        if (character == ' ' || character == ';') {
                            if (character == ' ') {
                                fireEvent(contentType, stateMachine, ParserEvent.WHITE_SPACE)
                            }
                            else {
                                fireEvent(contentType, stateMachine, ParserEvent.PARAMETER_SEPARATOR)
                            }

                            parameters.put(key.toString(), value.toString())

                            key = new StringBuilder()
                            value = new StringBuilder()
                        }
                        else if (isSpecialChar(character)) {
                            throw new IllegalArgumentException("expected token character but received special character '${character}'")
                        }
                        else {
                            throw new IllegalArgumentException("expected token character but received '${character}'")
                        }
                    }
                    else {
                        value.append(character)
                    }
                    break

                case ParserState.VALUE_ENDING_WHITE_SPACE:
                    if (character != ' ') {
                        if (character == ';') {
                            fireEvent(contentType, stateMachine, ParserEvent.PARAMETER_SEPARATOR)
                        }
                        else {
                            throw new IllegalArgumentException("expected ; character but received '${character}'")
                        }
                    }
                    break

                case ParserState.VALUE_QUOTED:
                    if (character == '"') {
                        fireEvent(contentType, stateMachine, ParserEvent.QUOTE)

                        parameters.put(key.toString(), value.toString())

                        key = new StringBuilder()
                        value = new StringBuilder()
                    }
                    else if (character == '\\') {
                        fireEvent(contentType, stateMachine, ParserEvent.ESCAPE)
                    }
                    else if (!isTokenChar(character) && !isSpecialChar(character)) {
                        throw new IllegalArgumentException("expected token or special character but received '${character}'")
                    }
                    else {
                        value.append(character)
                    }
                    break

                case ParserState.VALUE_QUOTED_ESCAPE:
                    if (character != '"') {
                        throw new IllegalArgumentException("expected \" character but received '${character}'")
                    }
                    value.append(character)
                    fireEvent(contentType, stateMachine, ParserEvent.TOKEN)
                    break
            }
        }

        // The value may be incomplete if the end of the string is reached.
        if (stateMachine.getCurrentState() == ParserState.VALUE) {
            parameters.put(key.toString(), value.toString())
        }

        // Fire the end string event so that exceptions can occur if necessary.
        fireEvent(contentType, stateMachine, ParserEvent.END_STRING)

        // Finally set the type.
        this.type = "${primaryType.toString()}/${subType.toString()}"
    }

    /**
     * Fires an event with the given state machine. If the transition fails, a meaningful exception is returned.
     *
     * @param contentType
     * @param stateMachine
     * @param event
     * @throws IllegalArgumentException
     */
    protected void fireEvent(String contentType, StateMachine<ParserState, ParserEvent> stateMachine, ParserEvent event) throws IllegalArgumentException {
        try {
            stateMachine.fire(event)
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("content type '${contentType}' is malformed", e)
        }
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
