/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.samples.browser

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

open class BrowserActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager?.beginTransaction()?.apply {
                replace(R.id.container, BrowserFragment.create())
                commit()
            }
        }
    }

    override fun onBackPressed() {
        supportFragmentManager.fragments.forEach {
            if (it is BackHandler && it.onBackPressed()) {
                return
            }
        }

        super.onBackPressed()
    }
}
