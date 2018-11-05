/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package mozilla.components.concept.fetch

interface Headers : Iterable<Header> {
    /**
     * Returns the number of headers (key / value combinations).
     */
    val size: Int

    operator fun get(index: Int): Header

    operator fun set(index: Int, header: Header)
}

data class Header(
    val name: String,
    val value: String
)

class MutableHeaders : Headers {
    override fun get(index: Int): Header = headers[index]

    override fun set(index: Int, header: Header) {
        headers[index] = header
    }

    override fun iterator(): Iterator<Header> = headers.iterator()

    private val headers: MutableList<Header> = mutableListOf()

    override val size: Int = headers.size

    /**
     * Append a header without removing the headers already present.
     */
    fun append(name: String, value: String): MutableHeaders {
        headers.add(Header(name, value))
        return this
    }

    /**
     * Set the only occurrence of the header; potentially overriding an already existing header.
     */
    fun set(name: String, value: String): MutableHeaders {
        headers.forEachIndexed { index, current ->
            if (current.name == name) {
                headers[index] = Header(name, value)
                return this
            }
        }

        return append(name, value)
    }
}
