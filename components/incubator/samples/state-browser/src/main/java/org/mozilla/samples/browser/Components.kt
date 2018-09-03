/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.samples.browser

import android.content.Context
import android.os.SystemClock
import mozilla.components.browser.session.action.Action
import mozilla.components.browser.session.state.State
import mozilla.components.browser.session.store.Store
import mozilla.components.support.base.log.Log
import mozilla.components.support.base.log.logger.Logger
import mozilla.components.support.base.log.sink.AndroidLogSink
import org.mozilla.samples.browser.engine.EngineMiddleware

/**
 * Helper class for lazily instantiating components needed by the application.
 */
class Components(private val applicationContext: Context) {
    init {
        Log.addSink(AndroidLogSink())
    }

    val engineMiddleware by lazy { EngineMiddleware<Nothing>(applicationContext) }

    val store by lazy {
        Store(
            initialState =  State.createBrowserOnlyState(),
            middleware = listOf(LoggerMiddleware(Logger("Store")), engineMiddleware))
    }
}

class LoggerMiddleware(
    private val logger: Logger
): Store.Middleware<Nothing> {
    var start: Long = 0

    override fun beforeDispatch(action: Action): Action? {
        start = SystemClock.elapsedRealtime()

        logger.debug("⇢ Dispatching: $action")
        return action
    }

    override fun afterDispatch(action: Action, state: State<Nothing>) {
        val took = SystemClock.elapsedRealtime() - start

        logger.debug("⇠ [${took}ms] Next state: $state")
    }
}
