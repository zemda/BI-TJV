package cz.cvut.fit.tjv.cs_skin_system.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class WeaponDTO(
    val id: Long,
    @field:NotBlank val name: String,
    @field:NotBlank val type: String,
    var tag: String?,
    @field:NotNull var skin: SkinDTO
)