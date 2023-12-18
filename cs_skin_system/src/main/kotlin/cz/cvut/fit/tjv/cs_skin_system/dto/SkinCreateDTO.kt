package cz.cvut.fit.tjv.cs_skin_system.dto

import cz.cvut.fit.tjv.cs_skin_system.domain.Weapon
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class SkinCreateDTO(
        @field:NotBlank val name: String,
        @field:NotBlank val rarity: String,
        @field:NotNull @field:DecimalMin("0.0") val price: Double,
        @field:NotNull @field:Min(0) var paintSeed: Int,
        @field:NotNull @field:DecimalMin("0.0") var float: Double,
        val weapon: Weapon?,
        val dropsFrom: Set<Long>?
)
