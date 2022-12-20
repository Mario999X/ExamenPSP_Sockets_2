package db

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import models.Nave
import mu.KotlinLogging
import java.util.concurrent.atomic.AtomicInteger

private val log = KotlinLogging.logger {}

class StarUnix {

    private val registros = mutableListOf<Nave>()

    private val misiles: AtomicInteger = AtomicInteger(0)
    private val contadorId: AtomicInteger = AtomicInteger(0)

    // Lock
    private val lock = Mutex()

    suspend fun add(item: Nave) {
        lock.withLock {
            contadorId.incrementAndGet()
            item.id = contadorId.toInt()

            log.debug { "Agregando $item" }
            registros.add(item)

            misiles.addAndGet(item.misilesProtonicos)
            //println("Misiles registrados: $misiles")
        }
    }

    fun getAll(): List<Nave> {
        log.debug { "Obteniendo registro completo" }
        return registros
    }

    fun getInfoDetallada(): String {
        log.debug { "Obteniendo misiles | Recuento de naves" }
        return "Total naves: ${registros.size} |Total misiles: $misiles"
    }
}