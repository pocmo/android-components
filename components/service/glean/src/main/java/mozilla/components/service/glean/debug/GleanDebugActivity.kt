/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package mozilla.components.service.glean.debug

import android.app.Activity
import android.os.Bundle

class GleanDebugActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Enable debug mode.

        val intent = packageManager.getLaunchIntentForPackage(packageName)
        startActivity(intent)

        finish()
    }
}
