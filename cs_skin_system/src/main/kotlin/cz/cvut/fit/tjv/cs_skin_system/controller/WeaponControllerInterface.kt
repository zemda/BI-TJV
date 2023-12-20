package cz.cvut.fit.tjv.cs_skin_system.controller

import cz.cvut.fit.tjv.cs_skin_system.domain.Weapon
import cz.cvut.fit.tjv.cs_skin_system.dto.WeaponCreateDTO
import cz.cvut.fit.tjv.cs_skin_system.dto.WeaponDTO
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

interface WeaponControllerInterface {

    @Operation(
        summary = "Fetch a weapon by its id",
        responses =
            [
                ApiResponse(
                    responseCode = "200",
                    description = "Successfully return weapon",
                    content = [Content(schema = Schema(implementation = Weapon::class))]
                ),
                ApiResponse(responseCode = "404", description = "Weapon not found"),
                ApiResponse(responseCode = "500", description = "Server error")
            ]
    )
    @GetMapping("/{id}")
    fun getWeaponById(@PathVariable id: Long): ResponseEntity<Any>

    @Operation(
        summary = "Fetch all weapons",
        responses =
            [
                ApiResponse(
                    responseCode = "200",
                    description = "Successfully return all weapons",
                    content =
                            [Content(schema = Schema(implementation = Weapon::class))]
                ),
                ApiResponse(responseCode = "500", description = "Server error")
            ]
    )
    @GetMapping
    fun getWeapons(): ResponseEntity<List<WeaponDTO>>

    @Operation(
        summary = "Create a weapon",
        responses =
            [
                ApiResponse(
                    responseCode = "201",
                    description = "Weapon created successfully",
                    content = [Content(schema = Schema(implementation = Weapon::class))]
                ),
                ApiResponse(responseCode = "400", description = "Bad request"),
                ApiResponse(responseCode = "500", description = "Server error")
            ]
    )
    @PostMapping
    fun createWeapon(@RequestBody weapon: WeaponCreateDTO): ResponseEntity<Any>

    @Operation(
        summary = "Update tag of a weapon with given id",
        responses =
            [
                ApiResponse(
                    responseCode = "200",
                    description = "Tag updated",
                    content = [Content(schema = Schema(implementation = Weapon::class))]
                ),
                ApiResponse(responseCode = "404", description = "Weapon not found"),
                ApiResponse(responseCode = "400", description = "Bad request"),
                ApiResponse(responseCode = "500", description = "Server error")
            ]
    )
    @PutMapping("/{id}/tag")
    fun updateWeaponTag(@PathVariable id: Long, @RequestParam newTag: String): ResponseEntity<Any>

    @Operation(
        summary = "Delete given weapon",
        responses =
            [
                ApiResponse(
                    responseCode = "204",
                    description = "Weapon deleted successfully"
                ),
                ApiResponse(responseCode = "404", description = "Weapon not found"),
                ApiResponse(responseCode = "500", description = "Server error")
            ]
    )
    @DeleteMapping("/{id}")
    fun deleteWeapon(@PathVariable id: Long): ResponseEntity<Any>
}
