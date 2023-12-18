package cz.cvut.fit.tjv.cs_skin_system.dto

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class CsgoCaseDTO(
        val id: Long,
        @field:NotBlank val name: String,
        @field:NotNull @field:DecimalMin("0.0") val price: Double,
)
