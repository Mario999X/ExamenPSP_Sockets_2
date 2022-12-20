package models

import kotlinx.serialization.Serializable
import java.time.LocalDate

// Importante el serializable (json) y en las data class de mensajes
@Serializable
data class Nave(
    var id: Int,
    var tipoNave: TipoNave,
    var saltoHiperEspacio: Boolean,
    val misilesProtonicos: Int = (10..20).random(),
    val fechaCreacion: String = LocalDate.now().toString()
) {
    enum class TipoNave {
        X_WIND, T_FIGHTER
    }

    override fun toString(): String {
        return "Nave(id=$id, tipoNave=$tipoNave, misilesProtonicos=$misilesProtonicos, saltoHiperEspacio=$saltoHiperEspacio, fechaCreacion='$fechaCreacion')"
    }

}