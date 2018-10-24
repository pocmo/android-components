package mozilla.components.feature.awesomebar.provider

import android.graphics.Bitmap
import mozilla.components.browser.session.SessionManager
import mozilla.components.concept.awesomebar.AwesomeBar
import mozilla.components.feature.tabs.TabsUseCases

class SessionSuggestionProvider(
    private val sessionManager: SessionManager,
    private val selectTabUseCase: TabsUseCases.SelectTabUseCase
): AwesomeBar.SuggestionProvider {
    override fun onInputStarted() {}

    override fun onInputChanged(text: String): List<AwesomeBar.Suggestion> {
        if (text.isEmpty()) {
            return emptyList()
        }

        val suggestions = mutableListOf<AwesomeBar.Suggestion>()

        sessionManager.sessions.forEach { session ->
            if (session.url.contains(text) || session.title.contains(text)) {
                suggestions.add(
                    AwesomeBar.Suggestion(
                        session.title,
                        session.url,
                        Bitmap.createBitmap(10, 10, Bitmap.Config.RGB_565),
                        onSuggestionClicked = { selectTabUseCase.invoke(session) }
                    )
                )
            }
        }

        return suggestions
    }

    override fun onInputCancelled() {}
}
