/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package mozilla.components.browser.session.store

import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.newSingleThreadContext
import mozilla.components.browser.session.action.Action
import mozilla.components.browser.session.reducer.BrowserReducers
import mozilla.components.browser.session.state.BrowserState
import mozilla.components.browser.session.state.State

typealias Reducer<S> = (S, Action) -> S
typealias ReducerList<S> = List<Reducer<S>>
typealias Observer<AS> = (State<AS>) -> Unit
typealias Middleware = (Action) -> Action

/**
 * The [Store] holds the [BrowserState] and (optional) application state.
 */
class Store<AS>(
    initialState: State<AS>,
    private val browserReducers: ReducerList<BrowserState> = BrowserReducers.get(),
    private val appReducers: ReducerList<AS> = listOf(),
    private val middleware: List<Middleware> = listOf()
) {
    private val storeContext = newSingleThreadContext("StoreContext")
    private val subscriptions: MutableList<Subscription<AS>> = mutableListOf()
    private var state = initialState

    /**
     * TODO
     */
    fun dispatch(action: Action) = launch(storeContext) {


        val newState = reduce(state, action)

        if (newState == state) {
            // Nothing has changed.
            return@launch
        }

        state = newState

        synchronized(subscriptions) {
            subscriptions.forEach { it.observer.invoke(state) }
        }
    }

    /**
     * Returns the current state.
     */
    fun getState(): State<AS> = state

    /**
     * Registers an observer function that will be invoked whenever the state changes.
     *
     * @param receiveInitialState If true the observer function will be invoked immediately with the current state.
     * @return A subscription object that can be used to unsubscribe from further state changes.
     */
    fun observe(receiveInitialState: Boolean = true, observer: Observer<AS>): Subscription<AS> {
        val subscription = Subscription(observer, store = this)

        synchronized(subscriptions) {
            subscriptions.add(subscription)
        }

        if (receiveInitialState) {
            observer.invoke(state)
        }

        return subscription
    }

    private fun removeSubscription(subscription: Subscription<AS>) {
        synchronized(subscriptions) {
            subscriptions.remove(subscription)
        }
    }

    private fun reduce(state: State<AS>, action: Action): State<AS> {
        return State(
            browserState = reduceState(state.browserState, action, browserReducers),
            appState = if (state.appState == null) {
                null
            } else {
                reduceState(state.appState, action, appReducers)
            }
        )
    }

    /**
     * TODO
     */
    class Subscription<S> internal constructor(
        internal val observer: Observer<S>,
        private val store: Store<S>
    ) {
        var binding: Binding? = null

        fun unsubscribe() {
            store.removeSubscription(this)

            binding?.unbind()
        }

        interface Binding {
            fun unbind()
        }
    }
}

private fun <S> reduceState(state: S, action: Action, reducers: ReducerList<S>): S {
    var current = state

    reducers.forEach { reducer ->
        current = reducer.invoke(current, action)
    }

    return current
}

