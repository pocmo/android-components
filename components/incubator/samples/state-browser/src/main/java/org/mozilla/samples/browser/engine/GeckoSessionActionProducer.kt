package org.mozilla.samples.browser.engine

import android.content.Context
import mozilla.components.browser.session.action.SessionAction
import mozilla.components.browser.session.state.SessionState
import mozilla.components.browser.session.store.Store
import org.mozilla.geckoview.GeckoResult
import org.mozilla.geckoview.GeckoRuntime
import org.mozilla.geckoview.GeckoSession
import org.mozilla.samples.browser.ext.components

class GeckoSessionActionProducer<S>(
    private val store: Store<S>,
    private val state: SessionState
): GeckoSession.NavigationDelegate, GeckoSession.ProgressDelegate {
    override fun onPageStop(session: GeckoSession?, success: Boolean) {
    }

    override fun onSecurityChange(
        session: GeckoSession?,
        securityInfo: GeckoSession.ProgressDelegate.SecurityInformation?
    ) = Unit

    override fun onPageStart(session: GeckoSession?, url: String?) = Unit

    override fun onProgressChange(session: GeckoSession?, progress: Int) {
        store.dispatch(
            SessionAction.UpdateProgressAction(state.id, progress))
    }

    override fun onCanGoBack(session: GeckoSession?, canGoBack: Boolean) {
        store.dispatch(
            SessionAction.CanGoBackAction(state.id, canGoBack))
    }

    override fun onLocationChange(session: GeckoSession, url: String) {
        store.dispatch(
            SessionAction.UpdateUrlAction(state.id, url))
    }

    override fun onCanGoForward(session: GeckoSession?, canGoForward: Boolean) {
        store.dispatch(
            SessionAction.canGoForward(state.id, canGoForward))
    }

    override fun onNewSession(session: GeckoSession, uri: String): GeckoResult<GeckoSession>? {
        return GeckoResult.fromValue(null)
    }

    override fun onLoadError(session: GeckoSession?, uri: String?, category: Int, error: Int): GeckoResult<String> {
        return GeckoResult.fromValue(null)
    }

    override fun onLoadRequest(session: GeckoSession, uri: String, target: Int, flags: Int): GeckoResult<Boolean>? {
        return GeckoResult.fromValue(false)
    }
}
