/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package mozilla.components.concept.awesomebar

import android.graphics.Bitmap
import android.view.View

/**
 * TODO
 */
interface AwesomeBar {

    /**
     * TODO
     */
    fun addProviders(vararg providers: SuggestionProvider)

    /**
     * Fired when the user starts interacting with the awesome bar by entering text in the toolbar.
     */
    fun onInputStarted()

    /**
     * Fired whenever the user changes their input, after they have started interacting with the awesome bar.
     *
     * @param text The current user input in the toolbar.
     */
    fun onInputChanged(text: String)

    /**
     * Fired when the user has cancelled their interaction with the awesome bar.
     */
    fun onInputCancelled()

    /**
     * Casts this awesome bar to an Android View object.
     */
    fun asView(): View = this as View

    /**
     * TODO
     */
    fun setOnStopListener(listener: () -> Unit)

    /**
     * TODO
     */
    data class Suggestion(
        val title: String? = null,
        val description: String? = null,
        val icon: Bitmap? = null,
        val chips: List<Chip> = emptyList(),
        val flags: List<Flag> = emptyList(),
        val onSuggestionClicked: (() -> Unit)? = null,
        val onChipClicked: ((Chip) -> Unit)? = null
    ) {
        /**
         * Chips are compact actions that are shown as part of a suggestion. For example a "search suggestion" may
         * offer multiple search suggestion chips.
         */
        data class Chip(
            val title: String
        )

        enum class Flag {
            BOOKMARK,
            OPEN_TAB
        }
    }

    /**
     * TODO
     */
    interface SuggestionProvider {
        /**
         * Fired when the user starts interacting with the awesome bar by entering text in the toolbar.
         */
        fun onInputStarted() = Unit

        /**
         * Fired whenever the user changes their input, after they have started interacting with the awesome bar.
         *
         * @param text The current user input in the toolbar.
         */
        fun onInputChanged(text: String): List<Suggestion>

        /**
         * Fired when the user has cancelled their interaction with the awesome bar.
         */
        fun onInputCancelled() = Unit
    }
}
