/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package mozilla.components.lib.fetch.okhttp

import mozilla.components.concept.fetch.Client
import okhttp3.OkHttpClient
import org.junit.Test

class FetchTestCases : mozilla.components.support.test.fetch.FetchTestCases() {
    override fun createClient(): Client = OkHttpClient(OkHttpClient())

    // Inherits test methods from generic test suite base class

    @Test
    fun `Do something`() {

    }
}
