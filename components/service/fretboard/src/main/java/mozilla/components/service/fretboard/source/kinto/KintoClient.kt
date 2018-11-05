/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package mozilla.components.service.fretboard.source.kinto

import mozilla.components.concept.fetch.Client
import mozilla.components.concept.fetch.Request
import mozilla.components.service.fretboard.ExperimentDownloadException
import java.io.IOException

/**
 * Helper class to make it easier to interact with Kinto
 *
 * @property httpClient http client to use
 * @property baseUrl Kinto server url
 * @property bucketName name of the bucket to fetch
 * @property collectionName name of the collection to fetch
 * @property headers headers to provide along with the request
 */
internal class KintoClient(
    private val httpClient: Client,
    private val baseUrl: String,
    private val bucketName: String,
    private val collectionName: String
) {

    /**
     * Returns all records from the collection
     *
     * @return Kinto response with all records
     */
    @Throws(ExperimentDownloadException::class)
    fun get(): String {
        try {
            return httpClient.fetch(
                Request(recordsUrl())
            ).body.string()
        } catch(e: IOException) {
            throw ExperimentDownloadException(e)
        }
    }

    /**
     * Performs a diff, given the last_modified time
     *
     * @param lastModified last modified time as a UNIX timestamp
     *
     * @return Kinto diff response
     */
    @Throws(ExperimentDownloadException::class)
    fun diff(lastModified: Long): String {
        try {
            return httpClient.fetch(
                Request("${recordsUrl()}?_since=$lastModified")
            ).body.string()
        } catch (e: IOException) {
            throw ExperimentDownloadException(e)
        }
    }

    /**
     * Gets the collection associated metadata
     *
     * @return collection metadata
     */
    fun getMetadata(): String {
        try {
            return httpClient.fetch(
                Request(collectionUrl())
            ).body.string()
        } catch (e: IOException) {
            throw ExperimentDownloadException(e)
        }
    }

    private fun recordsUrl() = "${collectionUrl()}/records"
    private fun collectionUrl() = "$baseUrl/buckets/$bucketName/collections/$collectionName"
}
