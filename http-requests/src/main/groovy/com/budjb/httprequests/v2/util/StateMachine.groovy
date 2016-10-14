package com.budjb.httprequests.v2.util
/**
 * A simple, generic state machine.
 *
 * @param < S >    Type of the state object.
 * @param < E >    Type of the event object.
 */
class StateMachine<S, E> {
    /**
     * Current state of the state machine.
     */
    protected S currentState

    /**
     * All registered state -> event -> state transitions.
     */
    protected Set<Transition> transitions = []

    /**
     * Constructor.
     *
     * @param initialState Initial state of the state machine.
     */
    StateMachine(S initialState) {
        currentState = initialState
    }

    /**
     * Returns the current state of the state machine.
     *
     * @return The current state of the state machine.
     */
    S getCurrentState() {
        return currentState
    }

    /**
     * Fires an event. If no valid transition exists for the event, an
     * {@link IllegalArgumentException} is thrown.
     *
     * @param event Event to attempt to transition with in the state machine.
     * @return The new current state.
     * @throws IllegalArgumentException when no transition exists for the given event.
     */
    S fire(E event) throws IllegalArgumentException {
        Transition transition = transitions.find { it.from == currentState && it.on == event }

        if (!transition) {
            throw new IllegalArgumentException("no transition defined for state ${currentState.toString()} and event ${event.toString()}")
        }

        currentState = transition.to

        return currentState
    }

    /**
     * Creates a new {@link TransitionBuilder} starting with the given state.
     *
     * @param state Source state of the new transition.
     * @return A {@link TransitionBuilder}.
     */
    TransitionBuilder from(S state) {
        return new TransitionBuilder(state)
    }

    /**
     * Represents a transition from one state to another linked by an event.
     */
    class Transition {
        /**
         * Source state.
         */
        final S from

        /**
         * Transition event.
         */
        final E on

        /**
         * Destination state.
         */
        final S to

        /**
         * Constructor.
         *
         * @param from Source state.
         * @param on Transition event.
         * @param to Destination state.
         */
        Transition(S from, E on, S to) {
            this.from = from
            this.on = on
            this.to = to
        }
    }

    /**
     * A builder class that provides a fluent API to build state transitions.
     */
    class TransitionBuilder {
        /**
         * Source state.
         */
        private S sourceState

        /**
         * Transition event.
         */
        private E currentEvent

        /**
         * Constructor.
         *
         * @param sourceState Source state.
         */
        TransitionBuilder(S sourceState) {
            this.sourceState = sourceState
        }

        /**
         * Sets the transition event.
         *
         * @param event Transition event.
         * @return This builder.
         */
        TransitionBuilder on(E event) {
            currentEvent = event
            return this
        }

        /**
         * Sets the destination state and stores the transition.
         *
         * Additionally, the destination state is set as the new source state
         * so that transition definitions can be chained together.
         *
         * @param state Destination state.
         * @return This builder.
         */
        TransitionBuilder to(S state) {
            transitions.add(new Transition(sourceState, currentEvent, state))
            sourceState = state
            currentEvent = null
            return this
        }
    }
}
