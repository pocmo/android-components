/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package mozilla.components.lib.fetch.httpurlconnection

import mozilla.components.concept.fetch.Client
import mozilla.components.lib.fetch.httpurlconnection.HttpUrlConnectionClient
import org.junit.Assert.assertTrue
import org.junit.Test

class HttpUrlConnectionFetchTestCases : mozilla.components.tooling.fetch.tests.FetchTestCases() {
    override fun createClient(): Client = HttpUrlConnectionClient()

    // Inherits test methods from generic test suite base class

    @Test
    fun `Client instance`() {
        // We need at least one test case defined here so that this is recognized as test class.
        assertTrue(createClient() is HttpUrlConnectionClient)
    }
}
