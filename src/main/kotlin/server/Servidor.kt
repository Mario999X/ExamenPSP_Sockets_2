package server

import db.StarUnix
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging

private val log = KotlinLogging.logger {}

private const val PUERTO = 6969

fun main() = runBlocking {
    // Monitor
    val su = StarUnix()

    // Dispacher para el servidor IO = Manager
    val selectorManager = SelectorManager(Dispatchers.IO)

    log.debug { "Arrancando servidor..." }

    // Socket TCP
    val serverSocket = aSocket(selectorManager).tcp().bind("localhost", PUERTO)

    while (true) {
        log.debug { "Servidor esperando..." }

        val socket = serverSocket.accept()
        log.debug { "Peticion de cliente -> " + socket.localAddress + " --- " + socket.remoteAddress }

        launch {
            GestorCliente(socket, su).run()
            log.debug { "Cliente desconectado" }
        }
    }
}