package com.budjb.httprequests.v2.core.entity.multipart

class MultiPartEntityStateMachine {
    State state

    Set<Transition> transitions = []

    MultiPartEntityStateMachine(State initialState) {
        state = initialState
    }

    void fire(Event event) {
        Transition transition = transitions.find { it.from == state && it.on == event }

        if (!transition) {
            throw new IllegalArgumentException("no transition defined for state ${state.toString()} and event ${event.toString()}")
        }

        state = transition.to
    }

    TransitionBuilder from(State state) {
        return new TransitionBuilder(state)
    }

    class Transition {
        final State from
        final Event on
        final State to

        Transition(State from, Event on, State to) {
            this.from = from
            this.on = on
            this.to = to
        }
    }

    class TransitionBuilder {
        State initialState
        Event currentEvent

        TransitionBuilder(State initialState) {
            this.initialState = initialState
        }

        TransitionBuilder on(Event event) {
            currentEvent = event
            return this
        }

        TransitionBuilder to(State state) {
            transitions.add(new Transition(initialState, currentEvent, state))
            initialState = state
            currentEvent = null
            return this
        }
    }

    static enum State {
        INIT,
        EVALUATE_LOOP_CONDITION,
        BOUNDARY,
        HEADERS,
        STREAM,
        STREAM_NEWLINE,
        LAST_BOUNDARY,
        DONE
    }

    static enum Event {
        INIT_COMPLETE,
        START_ENTITY,
        BOUNDARY_COMPLETE,
        HEADERS_COMPLETE,
        STREAM_COMPLETE,
        NEWLINE_COMPLETE,
        CLOSE
    }

    static class RepeatEvaluationException extends Exception {
        RepeatEvaluationException() {
            super()
        }
    }
}
