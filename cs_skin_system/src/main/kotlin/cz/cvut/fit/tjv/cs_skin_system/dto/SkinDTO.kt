package cz.cvut.fit.tjv.cs_skin_system.dto

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class SkinDTO(
        val id: Long,
        @field:NotBlank val name: String,
        @field:NotBlank val rarity: String,
        @field:NotNull val exterior: String,
        @field:NotNull @field:DecimalMin("0.0") var price: Double,
        @field:NotNull @field:Min(0) val paintSeed: Int,
        @field:NotNull @field:DecimalMin("0.0") val float: Double,
        var weapon: WeaponDTO?
)
