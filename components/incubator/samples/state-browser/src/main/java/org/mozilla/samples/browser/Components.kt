/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.samples.browser

import android.content.Context
import mozilla.components.browser.session.state.State
import mozilla.components.browser.session.state.SessionState
import mozilla.components.browser.session.store.Store

/**
 * Helper class for lazily instantiating components needed by the application.
 */
class Components(private val applicationContext: Context) {
    val store by lazy {
        val baseState = State.createBrowserOnlyState()

        val session = SessionState("about:blank")

        Store(baseState.copy(browserState = baseState.browserState.copy(sessions = listOf(session))))
    }
}
