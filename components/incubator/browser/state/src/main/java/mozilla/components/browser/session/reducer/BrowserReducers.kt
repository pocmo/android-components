/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package mozilla.components.browser.session.reducer

import mozilla.components.browser.session.action.Action
import mozilla.components.browser.session.action.BrowserAction
import mozilla.components.browser.session.action.SessionAction
import mozilla.components.browser.session.action.SessionListAction
import mozilla.components.browser.session.state.BrowserState
import mozilla.components.browser.session.state.SessionState
import mozilla.components.browser.session.store.ReducerList

object BrowserReducers {
    fun get(): ReducerList<BrowserState> = listOf(
        ::reduce
    )

    private fun reduce(state: BrowserState, action: Action): BrowserState {
        if (action !is BrowserAction) {
            return state
        }

        return when(action) {
            is SessionListAction -> reduceSessionListAction(state, action)
            is SessionAction -> reduceSessionAction(state, action)
        }
    }

    private fun reduceSessionListAction(state: BrowserState, action: SessionListAction): BrowserState {
        return when (action) {
            is SessionListAction.AddSessionAction -> state.copy(sessions = state.sessions + action.session)
            is SessionListAction.SelectSessionAction -> state.copy(selectedSessionId = action.session.id)
            is SessionListAction.RemoveSessionAction -> state.copy(sessions = state.sessions - action.session)
        }
    }

    private fun reduceSessionAction(state: BrowserState, action: SessionAction): BrowserState {
        return when (action) {
            is SessionAction.UpdateUrlAction -> updateSessionInState(state, action.sessionId) {
                it.copy(url = action.url)
            }
            is SessionAction.UpdateProgressAction -> updateSessionInState(state, action.sessionId) {
                it.copy(progress = action.progress)
            }
        }
    }
}

private fun updateSessionInState(
    state: BrowserState,
    sessionId: String,
    update: (SessionState) -> SessionState
): BrowserState {
    return state.copy(sessions = state.sessions.map { current ->
        if (current.id == sessionId) {
            update.invoke(current)
        } else {
            current
        }
    })
}