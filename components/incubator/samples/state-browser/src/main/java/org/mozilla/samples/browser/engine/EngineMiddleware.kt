/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.samples.browser.engine

import android.content.Context
import mozilla.components.browser.session.action.Action
import mozilla.components.browser.session.action.EngineAction
import mozilla.components.browser.session.action.SessionListAction
import mozilla.components.browser.session.selector.findSession
import mozilla.components.browser.session.state.BrowserState
import mozilla.components.browser.session.state.SessionState
import mozilla.components.browser.session.store.BrowserStore
import org.mozilla.geckoview.GeckoRuntime
import org.mozilla.geckoview.GeckoSession
import org.mozilla.geckoview.GeckoView
import org.mozilla.samples.browser.ext.components

class EngineMiddleware(
    private val applicationContext: Context
): BrowserStore.Middleware {
    private val mapping: MutableMap<String, GeckoSession> = mutableMapOf()

    override fun beforeDispatch(action: Action): Action? {
        if (action !is EngineAction) {
            return action
        }

        return when (action) {
            is EngineAction.GoBackEngineAction -> {
                mapping[action.sessionId]?.goBack()
                action
            }
            is EngineAction.GoForwardAction -> {
                mapping[action.sessionId]?.goForward()
                action
            }
            is EngineAction.LoadUrlEngineAction -> {
                mapping[action.sessionId]?.loadUri(action.url)
                action
            }
        }
    }

    override fun afterDispatch(action: Action, state: BrowserState) {
        when (action) {
            is SessionListAction.AddSessionAction -> {
                state.findSession(action.session.id)?.let {
                    mapping[it.id] = createGeckoSession(it)
                }
            }
        }
    }

    private fun createGeckoSession(session: SessionState): GeckoSession {
        return GeckoSession().apply {
            open(GeckoRuntime.getDefault(applicationContext))

            val actionProducer = GeckoSessionActionProducer(
                    applicationContext.components.store,
                    session)
            navigationDelegate = actionProducer
            progressDelegate = actionProducer

            loadUri(session.url)
        }
    }

    fun renderState(view: GeckoView, state: SessionState) {
        // TODO: This should only close the session if this is an unknown session.
        view.releaseSession()?.close()

        mapping[state.id]?.let { session ->
            view.setSession(session)
        }
    }

    override val shouldRunOnUiThread: Boolean = true
}
