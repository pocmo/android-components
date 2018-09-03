/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package mozilla.components.browser.session.action

import mozilla.components.browser.session.state.SessionState
import mozilla.components.browser.session.state.BrowserState

/**
 * [Action] implementation related to [BrowserState].
 */
sealed class BrowserAction: Action

/**
 * [BrowserAction] implementations related to updating the list of [SessionState] inside [BrowserState].
 */
sealed class SessionListAction: BrowserAction() {
    data class AddSessionAction(val session: SessionState) : SessionListAction()

    data class SelectSessionAction(val session: SessionState) : SessionListAction()

    data class RemoveSessionAction(val session: SessionState) : SessionListAction()
}

/**
 * [BrowserAction] implementations related to updating the a single [SessionState] inside [BrowserState].
 */
sealed class SessionAction: BrowserAction() {
    data class UpdateUrlAction(val sessionId: String, val url: String) : SessionAction()

    data class UpdateProgressAction(val sessionId: String, val progress: Int): SessionAction()
}
