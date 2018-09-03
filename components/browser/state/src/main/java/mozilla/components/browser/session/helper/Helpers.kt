/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package mozilla.components.browser.session.helper

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mozilla.components.browser.session.state.BrowserState
import mozilla.components.browser.session.store.Observer

/**
 *
 *
 * @param onMainThread
 * @param map
 * @param then
 */
fun <R> onlyIfChanged(
    onMainThread: Boolean = false,
    map: (BrowserState) -> R?,
    then: (BrowserState, R) -> Unit
): Observer {
    var lastValue: R? = null

    return fun (value: BrowserState) {
        val mapped = map(value)

        if (mapped !== null && mapped !== lastValue) {
            if (onMainThread) {
                GlobalScope.launch(Dispatchers.Main) {
                    then(value, mapped)
                }
            } else {
                then(value, mapped)
            }
            lastValue = mapped
        }
    }
}