package mozilla.components.feature.awesomebar

import android.view.View
import mozilla.components.browser.search.SearchEngine
import mozilla.components.browser.session.SessionManager
import mozilla.components.concept.awesomebar.AwesomeBar
import mozilla.components.concept.engine.EngineView
import mozilla.components.concept.toolbar.Toolbar
import mozilla.components.feature.awesomebar.provider.SearchSuggestionProvider
import mozilla.components.feature.awesomebar.provider.SessionSuggestionProvider
import mozilla.components.feature.search.SearchUseCases
import mozilla.components.feature.tabs.TabsUseCases

class AwesomeBarFeature(
    private val awesomeBar: AwesomeBar,
    private val toolbar: Toolbar,
    private val engineView: EngineView? = null
) {
    init {
        toolbar.setOnEditListener(object : mozilla.components.concept.toolbar.Toolbar.OnEditListener {
            override fun onTextChanged(text: String) = awesomeBar.onInputChanged(text)

            override fun onStartEditing() = showAwesomeBar()

            override fun onStopEditing() = hideAwesomeBar()
        })

        awesomeBar.setOnStopListener { toolbar.displayMode() }
    }

    fun addSessionProvider(
        sessionManager: SessionManager,
        selectTabUseCase: TabsUseCases.SelectTabUseCase
    ): AwesomeBarFeature {
        val provider = SessionSuggestionProvider(sessionManager, selectTabUseCase)
        awesomeBar.addProviders(provider)
        return this
    }

    fun addSearchProvider(
        searchEngine: SearchEngine,
        searchUseCase: SearchUseCases.DefaultSearchUrlUseCase
    ): AwesomeBarFeature {
        awesomeBar.addProviders(SearchSuggestionProvider(searchEngine, searchUseCase))
        return this
    }

    private fun showAwesomeBar() {
        awesomeBar.asView().visibility = View.VISIBLE
        engineView?.asView()?.visibility = View.GONE
        awesomeBar.onInputStarted()
    }

    private fun hideAwesomeBar() {
        awesomeBar.asView().visibility = View.GONE
        engineView?.asView()?.visibility = View.VISIBLE
        awesomeBar.onInputCancelled()
    }
}
