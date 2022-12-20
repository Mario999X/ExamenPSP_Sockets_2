package client

import models.Nave

object CreadorNavesRandom {

    // El id se le asignara en StarUnix haciendo uso de un atomicInteger
    fun init(): Nave {
        val tipo = getTipo()
        val salto = getSaltoHiper()
        return Nave(0, tipo, salto)
    }

    private fun getTipo(): Nave.TipoNave {
        val aleatorio: Int = (1..2).random()
        val tipoNave = if (aleatorio == 1) {
            Nave.TipoNave.X_WIND
        } else Nave.TipoNave.T_FIGHTER
        return tipoNave
    }

    private fun getSaltoHiper(): Boolean {
        val aleatorio: Int = (1..2).random()
        return aleatorio != 1
    }

}