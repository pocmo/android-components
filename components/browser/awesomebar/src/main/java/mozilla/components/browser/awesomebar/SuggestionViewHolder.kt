/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package mozilla.components.browser.awesomebar

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import mozilla.components.concept.awesomebar.AwesomeBar

internal sealed class SuggestionViewHolder(
    itemView: View
): RecyclerView.ViewHolder(itemView) {
    /**
     *
     */
    abstract fun bind(suggestion: AwesomeBar.Suggestion)

    /**
     *
     */
    internal class DefaultSuggestionViewHolder(
        private val awesomeBar: BrowserAwesomeBar,
        itemView: View
    ): SuggestionViewHolder(itemView) {
        private val titleView = itemView.findViewById<TextView>(R.id.mozac_browser_awesomebar_title)
        private val descriptionView = itemView.findViewById<TextView>(R.id.mozac_browser_awesomebar_description)

        override fun bind(suggestion: AwesomeBar.Suggestion) {
            val title = if (suggestion.title.isNullOrEmpty()) suggestion.description else suggestion.title

            titleView.text = title
            descriptionView.text = suggestion.description

            itemView.setOnClickListener {
                suggestion.onSuggestionClicked?.invoke()
                awesomeBar.listener?.invoke()
            }
        }

        companion object {
            val LAYOUT_ID = R.layout.mozac_browser_awesomebar_item_generic
        }
    }

    /**
     *
     */
    internal class ChipsSuggestionViewHolder(
        private val awesomeBar: BrowserAwesomeBar,
        itemView: View
    ): SuggestionViewHolder(itemView) {
        private val titleView = itemView.findViewById<TextView>(R.id.mozac_browser_awesomebar_title)
        private val chipsView = itemView.findViewById<LinearLayout>(R.id.mozac_browser_awesomebar_chips)

        override fun bind(suggestion: AwesomeBar.Suggestion) {
            val title = if (suggestion.title.isNullOrEmpty()) suggestion.description else suggestion.title

            titleView.text = title

            chipsView.removeAllViews()

            val inflater = LayoutInflater.from(itemView.context)

            suggestion
                .chips
                .forEach { chip ->
                    val view = inflater.inflate(
                        R.layout.mozac_browser_awesomebar_chip,
                        itemView as ViewGroup,
                        false
                    ) as TextView

                    view.text = chip.title
                    view.setOnClickListener {
                        suggestion.onChipClicked?.invoke(chip)
                        awesomeBar.listener?.invoke()
                    }

                    chipsView.addView(view)
                }
        }

        companion object {
            val LAYOUT_ID = R.layout.mozac_browser_awesomebar_item_chips
        }
    }
}
