/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.telemetry.net

import android.support.annotation.VisibleForTesting
import mozilla.components.concept.fetch.Client
import mozilla.components.concept.fetch.MutableHeaders
import mozilla.components.concept.fetch.Request
import mozilla.components.concept.fetch.clientError
import mozilla.components.concept.fetch.success
import mozilla.components.support.base.log.logger.Logger
import org.mozilla.telemetry.config.TelemetryConfiguration
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class TelemetryClient(
    private val client: Client
) {
    private val logger = Logger("telemetry/client")

    fun uploadPing(configuration: TelemetryConfiguration, path: String, serializedPing: String): Boolean {
        val request = Request(
            url = configuration.serverEndpoint + path,
            method = Request.Method.POST,
            headers = MutableHeaders()
                .set("Content-Type", "application/json; charset=utf-8")
                .set("User-Agent", configuration.userAgent)
                .set("Date", createDateHeaderValue()),
            body = Request.Body.fromString(serializedPing)
        )

        try {
            client.fetch(request).use { response ->
                return when {
                    response.success -> {
                        // Known success errors (2xx):
                        // 200 - OK. Request accepted into the pipeline.

                        // We treat all success codes as successful upload even though we only expect 200.
                        true
                    }

                    response.clientError -> {
                        // Known client (4xx) errors:
                        // 404 - not found - POST/PUT to an unknown namespace
                        // 405 - wrong request type (anything other than POST/PUT)
                        // 411 - missing content-length header
                        // 413 - request body too large (Note that if we have badly-behaved clients that
                        //       retry on 4XX, we should send back 202 on body/path too long).
                        // 414 - request path too long (See above)

                        // Something our client did is not correct. It's unlikely that the client is going
                        // to recover from this by re-trying again, so we just log and error and report a
                        // successful upload to the service.
                        logger.error("Server returned client error code: ${response.status}", null)
                        true
                    }
                    else -> {
                        // Known other errors:
                        // 500 - internal error

                        // For all other errors we log a warning an try again at a later time.
                        logger.warn("Server returned response code: ${response.status}", null)
                        false
                    }
                }
            }
        } catch (e: IOException) {
            logger.warn("IOException while uploading ping", e)
            return false
        }
    }

    @VisibleForTesting
    internal fun createDateHeaderValue(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US)
        dateFormat.timeZone = TimeZone.getTimeZone("GMT")
        return dateFormat.format(calendar.time)
    }
}
