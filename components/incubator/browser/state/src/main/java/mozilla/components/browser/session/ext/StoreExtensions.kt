/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package mozilla.components.browser.session.ext

import android.arch.lifecycle.GenericLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.view.View
import mozilla.components.browser.session.store.Observer
import mozilla.components.browser.session.store.Store

/**
 * Registers an [Observer] function that will be invoked whenever the state changes. The [Store.Subscription] will be
 * bound to the passed in [LifecycleOwner]. Once the [Lifecycle] state changes to DESTROYED the [Observer] will be
 * unregistered automatically.
 */
fun <AS> Store<AS>.observe(
    owner: LifecycleOwner,
    receiveInitialState: Boolean = true,
    observer: Observer<AS>
): Store.Subscription<AS> {
    return observe(receiveInitialState, observer).apply {
        binding = LifecycleBoundObserver(owner, this)
    }
}

/**
 * Registers an [Observer] function that will be invoked whenever the state changes. The [Store.Subscription] will be
 * bound to the passed in [View]. Once the [View] gets detached the [Observer] will be unregistered automatically.
 */
fun <AS> Store<AS>.observe(view: View, observer: Observer<AS>, receiveInitialState: Boolean = true): Store.Subscription<AS> {
    return observe(receiveInitialState, observer).apply {
        binding = ViewBoundObserver(view, this)
    }
}

/**
 * GenericLifecycleObserver implementation to bind an observer to a Lifecycle.
 */
private class LifecycleBoundObserver<S>(
    private val owner: LifecycleOwner,
    private val subscription: Store.Subscription<S>
) : GenericLifecycleObserver, Store.Subscription.Binding {
    override fun onStateChanged(source: LifecycleOwner?, event: Lifecycle.Event?) {
        if (owner.lifecycle.currentState == Lifecycle.State.DESTROYED) {
            subscription.unsubscribe()
        }
    }

    override fun unbind() {
        owner.lifecycle.removeObserver(this)
    }
}

/**
 * View.OnAttachStateChangeListener implementation to bind an observer to a View.
 */
private class ViewBoundObserver<S>(
    private val view: View,
    private val subscription: Store.Subscription<S>
) : View.OnAttachStateChangeListener, Store.Subscription.Binding {
    override fun onViewAttachedToWindow(v: View?) = Unit

    override fun onViewDetachedFromWindow(view: View) {
        subscription.unsubscribe()
    }

    override fun unbind() {
        view.removeOnAttachStateChangeListener(this)
    }
}
