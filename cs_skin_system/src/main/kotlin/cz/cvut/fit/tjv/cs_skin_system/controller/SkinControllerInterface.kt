package cz.cvut.fit.tjv.cs_skin_system.controller

import cz.cvut.fit.tjv.cs_skin_system.domain.Skin
import cz.cvut.fit.tjv.cs_skin_system.dto.SkinCreateDTO
import cz.cvut.fit.tjv.cs_skin_system.dto.SkinDTO
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

interface SkinControllerInterface {

    @Operation(
        summary = "Fetch a skin by its id",
        responses =
            [
                ApiResponse(
                    responseCode = "200",
                    description = "Successfully return skin",
                    content = [Content(schema = Schema(implementation = Skin::class))]
                ),
                ApiResponse(responseCode = "404", description = "Skin not found"),
                ApiResponse(responseCode = "500", description = "Server error")
            ]
    )
    @GetMapping("/{id}")
    fun getSkinById(@PathVariable id: Long): ResponseEntity<Any>

    @Operation(
        summary = "Fetch all skins",
        responses =
            [
                ApiResponse(
                    responseCode = "200",
                    description = "Successfully return all skins",
                    content = [Content(schema = Schema(implementation = Skin::class))]
                ),
                ApiResponse(responseCode = "500", description = "Server error")
            ]
    )
    @GetMapping
    fun getSkins(): ResponseEntity<List<SkinDTO>>

    @Operation(
        summary = "Create and optionally assign it a case",
        responses =
            [
                ApiResponse(
                    responseCode = "201",
                    description = "Skin created successfully",
                    content = [Content(schema = Schema(implementation = Skin::class))]
                ),
                ApiResponse(responseCode = "400", description = "Bad request"),
                ApiResponse(responseCode = "500", description = "Server error")
            ]
    )
    @PostMapping
    fun createSkin(@RequestBody skin: SkinCreateDTO): ResponseEntity<Any>

    @Operation(
        summary = "Update price to a skin with given id",
        responses =
            [
                ApiResponse(
                    responseCode = "200",
                    description = "Price updated",
                    content = [Content(schema = Schema(implementation = Skin::class))]
                ),
                ApiResponse(responseCode = "400", description = "Bad request"),
                ApiResponse(responseCode = "404", description = "Skin not found"),
                ApiResponse(responseCode = "500", description = "Server error")
            ]
    )
    @PutMapping("/{id}/price")
    fun updateSkinPrice(@PathVariable id: Long, @RequestParam newPrice: Double): ResponseEntity<Any>

    @Operation(
        summary = "Update from which cases can skin drop",
        responses =
            [
                ApiResponse(
                    responseCode = "200",
                    description = "Skin updated successfully",
                    content = [Content(schema = Schema(implementation = Skin::class))]
                ),
                ApiResponse(responseCode = "404", description = "Skin or case not found"),
                ApiResponse(responseCode = "400", description = "Bad request"),
                ApiResponse(responseCode = "500", description = "Server error")
            ]
    )
    @PutMapping("/{id}/cases")
    fun updateSkinDropsFrom(
            @PathVariable id: Long,
            @RequestBody caseIds: List<Long>
    ): ResponseEntity<Any>

    @Operation(
        summary = "Delete given skin",
        responses =
            [
                ApiResponse(
                    responseCode = "204",
                    description = "Skin deleted successfully"
                ),
                ApiResponse(responseCode = "404", description = "Skin not found"),
                ApiResponse(responseCode = "500", description = "Server error")
            ]
    )
    @DeleteMapping("/{id}")
    fun deleteSkin(@PathVariable id: Long): ResponseEntity<Any>

    @Operation(
        summary = "Fetch skins that we can apply to a weapon",
        responses =
            [
                ApiResponse(
                    responseCode = "200",
                    description = "Success",
                    content = [Content(schema = Schema(implementation = Skin::class))]
                ),
                ApiResponse(responseCode = "500", description = "Server error")
            ]
    )
    @GetMapping("/noWeapon")
    fun getSkinsWithNoWeapon(): ResponseEntity<List<SkinDTO>>

    @Operation(
        summary = "Fetch skins that meet the parameters",
        responses =
            [
                ApiResponse(
                    responseCode = "200",
                    description = "Successfully return skins",
                    content = [Content(schema = Schema(implementation = Skin::class))]
                ),
                ApiResponse(responseCode = "500", description = "Server error")
            ]
    )
    @GetMapping("/filter")
    fun filterSkins(
            @RequestParam(required = false) skinId: Long?,
            @RequestParam(required = false) name: String?,
            @RequestParam(required = false) rarity: String?,
            @RequestParam(required = false) exterior: String?,
            @RequestParam(required = false) price: Double?,
            @RequestParam(required = false) paintSeed: Int?,
            @RequestParam(required = false) float: Double?,
            @RequestParam(required = false) weaponId: Long?,
            @RequestParam(required = false) weaponName: String?,
            @RequestParam(required = false) csgoCaseId: Long?,
            @RequestParam(required = false) csgoCaseName: String?
    ): ResponseEntity<List<SkinDTO>>
}