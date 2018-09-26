/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package mozilla.components.service.fretboard.scheduler.workmanager

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import mozilla.components.service.fretboard.Fretboard

/**
 * [Worker] implementation for updating the fretboard configuration in the background.
 *
 * Extend this class and provide a fretboard instance.
 */
abstract class SyncWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {
    override fun doWork(): Result {
        return if (fretboard.updateExperiments()) {
            Result.SUCCESS
        } else {
            Result.RETRY
        }
    }

    /**
     * Used to provide the instance of Fretboard
     * the app is using
     *
     * @return current Fretboard instance
     */
    abstract val fretboard: Fretboard
}
