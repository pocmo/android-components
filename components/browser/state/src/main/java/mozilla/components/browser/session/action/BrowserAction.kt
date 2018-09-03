/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package mozilla.components.browser.session.action

import mozilla.components.browser.session.state.SessionState
import mozilla.components.browser.session.state.BrowserState
import mozilla.components.concept.engine.HitResult

/**
 * [Action] implementation related to [BrowserState].
 */
sealed class BrowserAction: Action

/**
 * [BrowserAction] implementations related to updating the list of [SessionState] inside [BrowserState].
 */
sealed class SessionListAction: BrowserAction() {
    data class AddSessionAction(val session: SessionState, val select: Boolean = false) : SessionListAction()

    data class SelectSessionAction(val sessionId: String) : SessionListAction()

    data class RemoveSessionAction(val sessionId: String) : SessionListAction()
}

/**
 * [BrowserAction] implementations related to updating the a single [SessionState] inside [BrowserState].
 */
sealed class SessionAction: BrowserAction() {
    data class UpdateUrlAction(val sessionId: String, val url: String) : SessionAction()

    data class UpdateProgressAction(val sessionId: String, val progress: Int): SessionAction()

    data class CanGoBackAction(val sessionId: String, val canGoBack: Boolean): SessionAction()

    data class CanGoForward(val sessionId: String, val canGoForward: Boolean): SessionAction()

    data class AddHitResultAction(val sessionId: String, val hitResult: HitResult): SessionAction()

    data class ConsumeHitResultAction(val sessionId: String): SessionAction()
}
