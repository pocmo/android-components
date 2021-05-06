/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package mozilla.components.feature.tabs

import mozilla.components.browser.state.state.BrowserState
import mozilla.components.browser.state.state.createCustomTab
import mozilla.components.browser.state.state.createTab
import mozilla.components.browser.state.store.BrowserStore
import mozilla.components.support.test.libstate.ext.waitUntilIdle
import mozilla.components.support.test.mock
import org.junit.Assert.assertEquals
import org.junit.Test

class CustomTabsUseCasesTest {
    @Test
    fun `MigrateCustomTabUseCase - turns custom tab into regular tab and selects it`() {
        val store = BrowserStore(
            initialState = BrowserState(
                tabs = listOf(
                    createTab("https://getpocket.com", id = "pocket")
                ),
                customTabs = listOf(
                    createCustomTab("https://www.mozilla.org", id = "mozilla"),
                    createCustomTab("https://www.firefox.com", id = "firefox")
                ),
                selectedTabId = "pocket"
            )
        )

        val useCases = CustomTabsUseCases(store, mock())

        useCases.migrate("mozilla", select = false)
        store.waitUntilIdle()

        assertEquals(1, store.state.customTabs.size)
        assertEquals(2, store.state.tabs.size)
        assertEquals("mozilla", store.state.tabs[1].id)
        assertEquals("pocket", store.state.selectedTabId)

        useCases.migrate("firefox", select = true)
        store.waitUntilIdle()

        assertEquals(0, store.state.customTabs.size)
        assertEquals(3, store.state.tabs.size)
        assertEquals("firefox", store.state.tabs[2].id)
        assertEquals("firefox", store.state.selectedTabId)
    }
}
