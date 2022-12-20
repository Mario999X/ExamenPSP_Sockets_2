package client

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import models.Nave
import models.mensajes.Request
import models.mensajes.Response
import mu.KotlinLogging

private val log = KotlinLogging.logger { }
private val json = Json

private lateinit var request: Request<Nave>

fun main() = runBlocking {

    // Indicamos el Dispatcher para el cliente
    val selectorManager = SelectorManager(Dispatchers.IO)

    // Preparamos el bucle para el cliente | La conexion sera tipo HTTP
    var salidaApp = false

    while (!salidaApp) {
        val sendRequestLaunch = launch {
            // Menu Cliente
            println(
                """
            1. Piloto
            2. BB8
            3. Luke Skywalker
            4. Salir
        """.trimIndent()
            )

            // Leemos la opcion y preparamos el request
            val opcion = readln().toIntOrNull()

            when (opcion) {
                1 -> {
                    log.debug { "Conectado como piloto" }
                    val nave = CreadorNavesRandom.init()
                    log.debug { "\tNave preparada: $nave" }

                    request = Request(nave, Request.Type.ADD)
                    log.debug { "\t--$request enviada, esperando respuesta..." }
                }

                2 -> {
                    log.debug { "Conectado como BB8" }
                    request = Request(null, Request.Type.GETBB8)
                    log.debug { "\t--$request enviada, esperando respuesta..." }
                }

                3 -> {
                    log.debug { "Conectado como Luke Skywalker" }
                    request = Request(null, Request.Type.GETLUKE)
                    log.debug { "\t--$request enviada, esperando respuesta..." }
                }

                4 -> {
                    log.debug { "Saliendo del programa" }
                    salidaApp = true
                }

                null -> {
                    log.debug { "OPCION DESCONOCIDA..." }
                }
            }

            // CONEXION CON EL SERVIDOR / VUELTA AL WHEN SI FUE NULL | SALIDA DEL PROGRAMA SIN ENTRAR AL SERVER
            if (opcion == null || opcion <= 0 || opcion >= 4) {
                println("---")
            } else {

                // Conectamos con el servidor
                val socket = aSocket(selectorManager).tcp().connect("localhost", 6969)
                log.debug { "Conectado a ${socket.remoteAddress}" }

                // Preparamos los canales de entrada-salida
                val receiveResponse = socket.openReadChannel()
                val sendRequest = socket.openWriteChannel(true)

                // Enviamos la request
                sendRequest.writeStringUtf8(json.encodeToString(request) + "\n")

                // Esperamos el response
                val responseJson = receiveResponse.readUTF8Line()
                val response = json.decodeFromString<Response<String>>(responseJson!!)

                log.debug { "\tRespuesta del servidor: ${response.content}" }

                // Cerramos la conexion
                log.debug { "Desconectando..." }
                withContext(Dispatchers.IO) {
                    socket.close()
                }
            }
        }
        // Recogemos la corrutina
        sendRequestLaunch.join()
    }

}