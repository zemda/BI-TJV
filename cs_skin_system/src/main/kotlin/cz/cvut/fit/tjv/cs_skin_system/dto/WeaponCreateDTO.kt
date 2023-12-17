package cz.cvut.fit.tjv.cs_skin_system.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class WeaponCreateDTO(
    @field:NotBlank val name: String,
    @field:NotBlank val type: String,
    val tag: String = "",
    @field:NotNull var skin: Long
)