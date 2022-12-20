package server

import db.StarUnix
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import models.Nave
import models.mensajes.Request
import models.mensajes.Response

private val log = KotlinLogging.logger {}
private val json = Json

private lateinit var request: Request<Nave>
private lateinit var response: Response<String>

class GestorCliente(private val cliente: Socket, private val su: StarUnix) {

    // Canal de entrada y de salida
    private val receiveRequest = cliente.openReadChannel()
    private val sendResponse = cliente.openWriteChannel(true) // true, para que se envíe el dato al instante

    suspend fun run() = withContext(Dispatchers.IO) {

        // Recibimos el request del cliente
        val sendResponseLaunch = launch {

            val requestJson = receiveRequest.readUTF8Line();

            requestJson?.let {
                request = json.decodeFromString(requestJson)
                log.debug { "Recibido: $request" }

                when (request.type) {
                    Request.Type.ADD -> {
                        val registro = request.content as Nave
                        log.debug { "Registro recibido, agregando" }

                        su.add(registro)
                        response = Response("$registro agregado", Response.Type.OK)
                    }

                    Request.Type.GETLUKE -> {
                        log.debug { "Luke recibido" }

                        val listado = su.getAll()
                        response = Response(listado.toString(), Response.Type.OK)
                    }

                    Request.Type.GETBB8 -> {
                        log.debug { "BB8 recibido" }

                        val informacion = su.getInfoDetallada()
                        response = Response(informacion, Response.Type.OK)
                    }

                    else -> {
                        response = Response("Error", Response.Type.ERROR)
                    }
                }
            }
            // Enviamos la respuesta
            sendResponse.writeStringUtf8(json.encodeToString(response) + "\n")
        }
        // Recogemos la corrutina
        sendResponseLaunch.join()

        // Cerramos la conexion
        log.debug { "Cerrando conexion" }
        withContext(Dispatchers.IO) {
            cliente.close()
        }
    }

}