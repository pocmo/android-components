/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package mozilla.components.browser.session.state

import mozilla.components.concept.engine.HitResult
import java.util.UUID

data class SessionState(
    val url: String,
    val private: Boolean = false,
    val id: String = UUID.randomUUID().toString(),
    val source: SourceState = SourceState.NONE,
    val title: String = "",
    val progress: Int = 0,
    val loading: Boolean = false,
    val canGoBack: Boolean = false,
    val canGoForward: Boolean = false,
    val searchTerms: String = "",
    val securityInfo: SecurityInfoState = SecurityInfoState(),
    val customTabConfig: CustomTabState? = null,
    val download: DownloadState? = null,
    val trackerBlockingEnabled: Boolean = false,
    val trackersBlocked: List<String> = emptyList(),
    val hitResult: HitResult? = null
)
