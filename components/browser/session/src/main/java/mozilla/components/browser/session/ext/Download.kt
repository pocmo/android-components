/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package mozilla.components.browser.session.ext

import mozilla.components.browser.session.Download
import mozilla.components.browser.state.state.content.DownloadState

internal fun Download.toDownloadState() = DownloadState(
    url,
    fileName,
    contentType,
    contentLength,
    userAgent,
    destinationDirectory,
    referrerUrl,
    id
)
