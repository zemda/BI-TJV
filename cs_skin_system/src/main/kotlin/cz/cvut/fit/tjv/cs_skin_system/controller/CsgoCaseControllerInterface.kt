package cz.cvut.fit.tjv.cs_skin_system.controller

import cz.cvut.fit.tjv.cs_skin_system.domain.CsgoCase
import cz.cvut.fit.tjv.cs_skin_system.dto.CsgoCaseCreateDTO
import cz.cvut.fit.tjv.cs_skin_system.dto.CsgoCaseDTO
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

interface CsgoCaseControllerInterface {

    @Operation(
        summary = "Fetch a CSGO case by its id",
        responses =
            [
                ApiResponse(
                    responseCode = "200",
                    description = "Successfully return case",
                    content = [Content(schema = Schema(implementation = CsgoCase::class))]
                ),
                ApiResponse(responseCode = "404", description = "Not found"),
                ApiResponse(responseCode = "500", description = "Server error")
            ]
    )
    @GetMapping("/{id}")
    fun getCsgoCaseById(@PathVariable id: Long): ResponseEntity<Any>

    @Operation(
        summary = "Fetch all CSGO cases",
        responses =
            [
                ApiResponse(
                    responseCode = "200",
                    description = "Successfully return all cases",
                    content = [Content(schema = Schema(implementation = CsgoCase::class))]
                ),
                ApiResponse(responseCode = "500", description = "Server error")
            ]
    )
    @GetMapping
    fun getCsgoCases(): ResponseEntity<List<CsgoCaseDTO>>

    @Operation(
        summary = "Create a CSGO case",
        responses =
            [
                ApiResponse(
                    responseCode = "201",
                    description = "CSGO case created successfully",
                    content = [Content(schema = Schema(implementation = CsgoCase::class))]
                ),
                ApiResponse(
                    responseCode = "400",
                    description = "Invalid case details provided"
                ),
                ApiResponse(responseCode = "500", description = "Server error")
            ]
    )
    @PostMapping
    fun createCsgoCase(@RequestBody csgoCase: CsgoCaseCreateDTO): ResponseEntity<Any>

    @Operation(
        summary = "Change price of a CSGO case",
        responses =
            [
                ApiResponse(
                    responseCode = "200",
                    description = "Price updated successfully",
                    content = [Content(schema = Schema(implementation = CsgoCase::class))]
                ),
                ApiResponse(
                    responseCode = "400",
                    description = "Bad request - new price is not valid"
                ),
                ApiResponse(
                    responseCode = "404",
                    description = "Not found - case with the provided ID does not exist"
                ),
                ApiResponse(responseCode = "500", description = "Server error")
            ]
    )
    @PutMapping("/{caseId}/changePrice/{newPrice}")
    fun changePrice(@PathVariable caseId: Long, @PathVariable newPrice: Double): ResponseEntity<Any>

    @Operation(
        summary = "Add skins to a CSGO case",
        responses =
            [
                ApiResponse(
                    responseCode = "200",
                    description = "Skins added successfully",
                    content = [Content(schema = Schema(implementation = CsgoCase::class))]
                ),
                ApiResponse(responseCode = "400", description = "Bad request"),
                ApiResponse(responseCode = "404", description = "Not found"),
                ApiResponse(responseCode = "500", description = "Server error")
            ]
    )
    @PutMapping("/{caseId}/addSkins")
    fun addSkinsToCase(
            @PathVariable caseId: Long,
            @RequestBody skinIds: List<Long>,
            @RequestParam addSkins: Boolean
    ): ResponseEntity<Any>

    @Operation(
        summary = "Delete a CSGO case",
        responses =
            [
                ApiResponse(
                    responseCode = "204",
                    description = "Case deleted successfully"
                ),
                ApiResponse(responseCode = "404", description = "Case not found"),
                ApiResponse(responseCode = "500", description = "Server error")
            ]
    )
    @DeleteMapping("/{id}")
    fun deleteCsgoCase(@PathVariable id: Long): ResponseEntity<Any>
}
