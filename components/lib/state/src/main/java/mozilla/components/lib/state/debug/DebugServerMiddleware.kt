/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package mozilla.components.lib.state.debug

import android.os.SystemClock
import mozilla.components.lib.state.Action
import mozilla.components.lib.state.Middleware
import mozilla.components.lib.state.MiddlewareStore
import mozilla.components.lib.state.State
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket
import java.util.UUID
import java.util.concurrent.LinkedBlockingQueue

private const val DEFAULT_PORT = 6701

/**
 * TODO
 */
class DebugServerMiddleware<S: State, A: Action> : Middleware<S, A> {
    override fun invoke(
        store: MiddlewareStore<S, A>,
        next: (A) -> Unit,
        action: A
    ) {
        val group = server.sendStartMessage(store.state, action)

        next(action)

        server.sendEndMessage(group, store.state)
    }

    companion object {
        private val server = DebugServer().also {
            it.start()
        }
    }
}

private data class MessageGroup(
    val uuid: UUID,
    val startTime: Long = SystemClock.elapsedRealtimeNanos()
)

private class DebugServer {
    private val clients = mutableMapOf<Socket, Client>()

    fun start() {
        val connectionThread = ConnectionThread(server = this)
        connectionThread.start()
    }

    @Synchronized
    fun onClientConnected(socket: Socket) {
        val writerThread = WriterThread(
            server = this,
            socket = socket
        )

        val client = Client(writerThread)
        clients[socket] = client

        writerThread.start()
    }

    @Synchronized
    fun onClientDisconnect(socket: Socket) {
        val client = clients[socket] ?: return

        try {
            client.writerThread.interrupt()
            socket.close()
        } catch (e: IOException) {
            // TODO
        }
    }

    fun sendStartMessage(state: State, action: Action): MessageGroup {
        val uuid = UUID.randomUUID()

        state.hashCode()

        send("$uuid - START - ${action::class.java.simpleName}")

        return MessageGroup(uuid)
    }

    fun sendEndMessage(group: MessageGroup, state: State) {
        val took = SystemClock.elapsedRealtimeNanos() - group.startTime

        state.hashCode()

        send("${group.uuid} - END [$took ns]")
    }

    @Synchronized
    private fun send(message: String) {
        clients.values.forEach { client ->
            client.writerThread.write(message)
        }
    }
}

private data class Client(
    val writerThread: WriterThread
)

private class ConnectionThread(
    val server: DebugServer
) : Thread() {
    override fun run() {
        val serverSocket = ServerSocket(DEFAULT_PORT)

        while (!isInterrupted) {
            try {
                val socket = serverSocket.accept()
                server.onClientConnected(socket)
            } catch(e: IOException) {
                // TODO
            }
        }
    }
}

private class WriterThread(
    private val server: DebugServer,
    private val socket: Socket
) : Thread() {
    private val queue = LinkedBlockingQueue<String>()

    override fun run() {
        try {
            val outputSteam = socket.getOutputStream().bufferedWriter()
            while (!isInterrupted) {
                val message = queue.take()
                outputSteam.write(message)
                outputSteam.write("\n")
                outputSteam.flush()
            }
        } catch (e: IOException) {
            // TODO
        } finally {
            server.onClientDisconnect(socket)
        }
    }

    fun write(message: String) {
        queue.add(message)
    }
}