package server

import db.StarUnix
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging

/*
-- ENUNCIADO (segun recuerdo) --
R2D2 crea una seccion critica llamada StarUnix, y se pueden enviar tres tipos de mensajes:
    - Por parte de un piloto, envia un registro de una nave
    - Por parte de BB8, busca el numero de misiles totales y el total de naves
    - Por parte de Luke, el quiere el listado de registros/naves al completo.
Nave: Id, tipo de Nave(X-WIND, T-FIGHTER), salto de hiper espacio (boolean), misiles protonicos (entre 10..20), fecha creacion
Tiempo para resolverlo: 2h:30min

* */

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