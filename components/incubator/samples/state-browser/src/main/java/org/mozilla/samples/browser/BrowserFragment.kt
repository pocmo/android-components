/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.samples.browser

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_browser.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import mozilla.components.browser.session.state.State
import mozilla.components.browser.session.state.SessionState
import mozilla.components.browser.session.store.Observer
import org.mozilla.samples.browser.engine.GeckoSessionActionProducer
import org.mozilla.samples.browser.ext.components

class BrowserFragment : Fragment(), BackHandler {
    lateinit var state: SessionState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        state = components.store.getState().browserState.sessions.first()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_browser, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        geckoView.session = GeckoSessionActionProducer.create(context!!, state)
    }

    override fun onStart() {
        super.onStart()

        /*
        components.store.observe(owner = this) { state ->
            // Do something with state..
        }
        */

        /*
        components.store.observe(
            owner = this,
            receiveInitialState = true,
            observer = ifChanged<Nothing, List<SessionState>>({ state -> state.browserState.sessions }) { session ->

            })
        */


        components.store.observe(
            receiveInitialState = true,
            observer = ifChanged<Nothing, SessionState>(
                map = { state -> state.browserState.sessions.firstOrNull() },
                onUIThread = true
            ) { session ->
                toolbarView.text = session.url
                progressView.progress = session.progress
            })
    }

    override fun onStop() {
        super.onStop()

    }

    override fun onBackPressed(): Boolean {
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




fun <S, R> ifChanged(onUIThread: Boolean = false, map: (State<S>) -> R?, then: (R) -> Unit): Observer<S> {
    var lastValue: R? = null

    return fun (value: State<S>) {


        val mapped = map(value)

        Log.w("SKDBG", "Last value: $lastValue and now: $mapped")

        if (mapped !== null && mapped !== lastValue) {
            if (onUIThread) {
                launch(UI) {
                    then(mapped)
                }
            } else {
                then(mapped)
            }
            lastValue = mapped
        }
    }
}

