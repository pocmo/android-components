/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package mozilla.components.browser.awesomebar

import android.support.annotation.GuardedBy
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import mozilla.components.concept.awesomebar.AwesomeBar
import java.lang.IllegalArgumentException

internal class SuggestionsAdapter(
    private val awesomeBar: BrowserAwesomeBar
): RecyclerView.Adapter<SuggestionViewHolder>() {
    @GuardedBy("suggestions")
    private val suggestions = mutableListOf<AwesomeBar.Suggestion>()

    fun addSuggestions(suggestions: List<AwesomeBar.Suggestion>) = synchronized(suggestions){
        this.suggestions.addAll(suggestions)
        // TODO: Sort/Rank/Notify appropriately
        notifyDataSetChanged()
    }

    fun clearSuggestions() = synchronized(suggestions) {
        this.suggestions.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SuggestionViewHolder = synchronized(suggestions) {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)

        return when (viewType) {
            SuggestionViewHolder.DefaultSuggestionViewHolder.LAYOUT_ID ->
                SuggestionViewHolder.DefaultSuggestionViewHolder(awesomeBar, view)

            SuggestionViewHolder.ChipsSuggestionViewHolder.LAYOUT_ID ->
                SuggestionViewHolder.ChipsSuggestionViewHolder(awesomeBar, view)

            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun getItemViewType(position: Int): Int = synchronized(suggestions) {
        val suggestion = suggestions.get(position)

        return if (suggestion.chips.isNotEmpty()) {
            SuggestionViewHolder.ChipsSuggestionViewHolder.LAYOUT_ID
        } else {
            SuggestionViewHolder.DefaultSuggestionViewHolder.LAYOUT_ID
        }
    }

    override fun getItemCount(): Int = synchronized(suggestions) {
        return suggestions.size
    }

    override fun onBindViewHolder(
        holder: SuggestionViewHolder,
        position: Int
    ) = synchronized(suggestions) {
        val suggestion = suggestions.get(position)
        holder.bind(suggestion)
    }
}
