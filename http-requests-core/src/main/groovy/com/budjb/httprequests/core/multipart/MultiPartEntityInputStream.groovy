package com.budjb.httprequests.core.multipart

import com.budjb.httprequests.core.HttpEntity
import com.budjb.httprequests.core.multipart.MultiPartEntityStateMachine.Event
import com.budjb.httprequests.core.multipart.MultiPartEntityStateMachine.RepeatEvaluationException
import com.budjb.httprequests.core.multipart.MultiPartEntityStateMachine.State

class MultiPartEntityInputStream extends InputStream {
    MultiPartEntity multiPartEntity

    /**
     * Entity iterator used during the output phase.
     */
    protected Iterator<Part> partsIterator

    /**
     * Current entity being processed.
     */
    protected Part currentPart

    /**
     * Boundary string used to separate entities.
     */
    protected String boundary

    /**
     * A multi-use iterator to output strings.
     */
    protected Iterator<String> stringIterator

    /**
     * Output state machine.
     */
    protected MultiPartEntityStateMachine stateMachine

    /**
     * Constructor.
     *
     * @param boundary
     * @param entities
     */
    MultiPartEntityInputStream() {
        stateMachine = createStateMachine()
    }

    /**
     * Create the output state machine.
     *
     * @return
     */
    protected MultiPartEntityStateMachine createStateMachine() {
        MultiPartEntityStateMachine stateMachine = new MultiPartEntityStateMachine(State.INIT)

        stateMachine.from(State.INIT).on(Event.INIT_COMPLETE).to(State.EVALUATE_LOOP_CONDITION)
        stateMachine.from(State.EVALUATE_LOOP_CONDITION).on(Event.START_ENTITY).to(State.BOUNDARY)
            .on(Event.BOUNDARY_COMPLETE).to(State.HEADERS) // TODO: add this
            .on(Event.HEADERS_COMPLETE).to(State.STREAM) // TODO: add this
            .on(Event.STREAM_COMPLETE).to(State.STREAM_NEWLINE)
            .on(Event.NEWLINE_COMPLETE).to(State.EVALUATE_LOOP_CONDITION)

        stateMachine.from(State.EVALUATE_LOOP_CONDITION).on(Event.CLOSE).to(State.LAST_BOUNDARY)
            .on(Event.BOUNDARY_COMPLETE).to(State.DONE)


        return stateMachine
    }

    /**
     * Reads the next byte from the {@link InputStream}.
     *
     * @return
     * @throws IOException
     */
    @Override
    int read() throws IOException {
        return evaluateState()
    }

    /**
     * Evaluates the current state of the state machine and determines what data
     * the {@link #read} method should return.
     *
     * @return
     */
    int evaluateState() {
        while (true) {
            try {
                switch (stateMachine.getState()) {
                    case State.INIT:
                        return init()

                    case State.EVALUATE_LOOP_CONDITION:
                        return evaluationLoopCondition()

                    case State.BOUNDARY:
                        return readLeadingBoundary()

                    case State.HEADERS:
                        return readHeaders()

                    case State.STREAM:
                        return readStream()

                    case State.STREAM_NEWLINE:
                        return readNewline()

                    case State.LAST_BOUNDARY:
                        return readClosingBoundary()

                    case State.DONE:
                        return -1

                    default:
                        throw new IllegalStateException("no execution condition defined for state ${stateMachine.getState().toString()}")
                }
            }
            catch (RepeatEvaluationException ignored) {
                // Some states throw this exception when it can not return a byte to output
                // but want the state machine to advance to another state.
            }
        }
    }

    int readHeaders() {
        if (stringIterator == null) {
            List<String> headers = []
            headers.add("Content-Disposition: form-data; name=\"${currentPart.name}\"")
            headers.add("Content-Type: ${currentPart.contentType.toString()}")

            stringIterator = "${headers.join('\n')}\n\n".iterator()
        }

        if (!stringIterator.hasNext()) {
            stringIterator = null
            stateMachine.fire(Event.HEADERS_COMPLETE)
            throw new RepeatEvaluationException()
        }

        return (int) (stringIterator.next()).charAt(0)
    }

    int init() {
        if (!multiPartEntity.parts.size()) {
            throw new IllegalArgumentException("a multi-part entity must have at least one part")
        }

        if (!multiPartEntity.boundary.size()) {
            throw new IllegalArgumentException("a multi-part entity has have a boundary")
        }

        this.boundary = multiPartEntity.boundary
        partsIterator = multiPartEntity.parts.iterator()

        stateMachine.fire(Event.INIT_COMPLETE)

        throw new RepeatEvaluationException()
    }

    /**
     * Reads a byte from the entity's {@link InputStream}.
     *
     * @return
     */
    int readStream() {
        int read = currentPart.getInputStream().read()

        if (read == -1) {
            stateMachine.fire(Event.STREAM_COMPLETE)
        }

        return read
    }

    /**
     * Determines if there are additional {@link HttpEntity} objects to process
     * once the previous one has been completed.
     *
     * @return
     */
    int evaluationLoopCondition() {
        if (partsIterator.hasNext()) {
            currentPart = partsIterator.next()
            stateMachine.fire(Event.START_ENTITY)
        }
        else {
            stateMachine.fire(Event.CLOSE)
        }

        throw new RepeatEvaluationException()
    }

    /**
     * Sets up and reads a leading boundary.
     *
     * @return
     */
    int readLeadingBoundary() {
        if (stringIterator == null) {
            stringIterator = "${boundary}\n".iterator()
        }

        if (!stringIterator.hasNext()) {
            stringIterator = null
            stateMachine.fire(Event.BOUNDARY_COMPLETE)
            throw new RepeatEvaluationException()
        }

        return (int) (stringIterator.next()).charAt(0)
    }

    /**
     * Sets up and reads the closing boundary marker.
     *
     * @return
     */
    int readClosingBoundary() {
        if (stringIterator == null) {
            stringIterator = "${boundary}--".iterator()
        }

        if (!stringIterator.hasNext()) {
            stringIterator = null
            stateMachine.fire(Event.BOUNDARY_COMPLETE)
            throw new RepeatEvaluationException()
        }

        return (int) (stringIterator.next()).charAt(0)
    }

    /**
     * Returns a newline character. This is only really used when outputting an
     * existing {@link InputStream}, such as when reading from an entity.
     *
     * @return
     */
    int readNewline() {
        stateMachine.fire(Event.NEWLINE_COMPLETE)
        return (int) "\n".charAt(0)
    }
}
