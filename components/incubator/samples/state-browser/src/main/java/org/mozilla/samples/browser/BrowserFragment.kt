/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.samples.browser

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.InputType
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import kotlinx.android.synthetic.main.fragment_browser.*
import mozilla.components.browser.session.action.EngineAction
import mozilla.components.browser.session.action.SessionListAction
import mozilla.components.browser.session.state.SessionState
import mozilla.components.browser.session.store.Observer
import org.mozilla.samples.browser.ext.components

class BrowserFragment : Fragment(), BackHandler {
    private lateinit var state: SessionState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        state = SessionState("https://www.mozilla.org")
        components.store.dispatch(
            SessionListAction.AddSessionAction(state, select = true))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_browser, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        backView.setOnClickListener {
            components.store.dispatch(
                EngineAction.GoBackEngineAction(state.id))
        }

        forwardiew.setOnClickListener {
            components.store.dispatch(
                    EngineAction.GoForwardAction(state.id))
        }

        toolbarView.apply {
            imeOptions = EditorInfo.IME_ACTION_GO or EditorInfo.IME_FLAG_NO_EXTRACT_UI or EditorInfo.IME_FLAG_NO_FULLSCREEN
            gravity = Gravity.CENTER_VERTICAL
            background = null
            setLines(1)
            inputType = InputType.TYPE_TEXT_VARIATION_URI

            setSelectAllOnFocus(true)

            setOnCommitListener {
                components.store.dispatch(
                        EngineAction.LoadUrlEngineAction(state.id, text.toString())
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()

        components.store.observe(
            receiveInitialState = true,
            observer = ifChanges<Nothing, String>(
                map = { state -> state.browserState.selectedSessionId },
                onUIThread = true
            ) { state,_ ->
                state.browserState.selectedSessionState?.let { session ->
                    components.engineMiddleware.renderState(geckoView, session)
                }
            }
        )

        components.store.observe(
            receiveInitialState = true,
            observer = ifChanges<Nothing, SessionState>(
                map = { state -> state.browserState.sessions.firstOrNull() },
                onUIThread = true
            ) { _, session ->
                toolbarView.setText(session.url)
                progressView.progress = session.progress
                backView.isEnabled = session.canGoBack
                forwardiew.isEnabled = session.canGoForward
            })
    }

    override fun onBackPressed(): Boolean {
        if (state.canGoBack) {
            components.store.dispatch(EngineAction.GoBackEngineAction(state.id))
            return true
        }

        return false
    }

    companion object {
        private const val SESSION_ID = "session_id"

        fun create(sessionId: String? = null): BrowserFragment = BrowserFragment().apply {
            arguments = Bundle().apply {
                putString(SESSION_ID, sessionId)
            }
        }
    }
}

fun <S, R> ifChanges(onUIThread: Boolean = false, map: (State<S>) -> R?, then: (State<S>, R) -> Unit): Observer<S> {
    var lastValue: R? = null

    return fun (value: State<S>) {
        val mapped = map(value)

        if (mapped !== null && mapped !== lastValue) {
            if (onUIThread) {
                launch(UI) {
                    then(value, mapped)
                }
            } else {
                then(value, mapped)
            }
            lastValue = mapped
        }
    }
}

