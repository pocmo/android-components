/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package mozilla.components.browser.session.state

data class State<AS>(
    val browserState: BrowserState,
    val appState: AS? = null
) {
    companion object {
        fun createBrowserOnlyState(
            browserState: BrowserState = BrowserState(listOf(), "")
        ): State<Nothing> = State(
            browserState = browserState,
            appState = null)
    }
}
