package cz.cvut.fit.tjv.cs_skin_system.controller

import cz.cvut.fit.tjv.cs_skin_system.application.CsgoCaseService
import cz.cvut.fit.tjv.cs_skin_system.dto.CsgoCaseCreateDTO
import cz.cvut.fit.tjv.cs_skin_system.dto.CsgoCaseDTO
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/csgoCase")
@CrossOrigin(origins = ["http://localhost:5173"])
class CsgoCaseController(private val csgoCaseService: CsgoCaseService) {

    @GetMapping("/{id}")
    @Operation(summary = "Get a CSGO case by its id")
    fun getCsgoCaseById(@PathVariable id: Long): ResponseEntity<Any> {
        val csgoCase = csgoCaseService.getById(id)
        return ResponseEntity(csgoCase, HttpStatus.OK)
    }

    @GetMapping
    fun getCsgoCases(): ResponseEntity<List<CsgoCaseDTO>> {
        return ResponseEntity.ok(csgoCaseService.getAll())
    }

    @PostMapping
    @Operation(summary = "Create a CSGO case")
    fun createCsgoCase(@RequestBody csgoCase: CsgoCaseCreateDTO): ResponseEntity<Any> {
        val createdCsgoCase = csgoCaseService.create(csgoCase)
        return ResponseEntity(createdCsgoCase, HttpStatus.CREATED)
    }

    @PutMapping("/{caseId}/changePrice/{newPrice}")
    @Operation(summary = "Change price of a CSGO case")
    fun changePrice(
            @PathVariable caseId: Long,
            @PathVariable newPrice: Double
    ): ResponseEntity<Any> {
        val updatedCsgoCase = csgoCaseService.updateCsgoCase(caseId, newPrice)
        return ResponseEntity(updatedCsgoCase, HttpStatus.OK)
    }

    @PutMapping("/{caseId}/addSkins")
    @Operation(summary = "Add skins to a CSGO case")
    fun addSkinsToCase(
            @PathVariable caseId: Long,
            @RequestBody skinIds: List<Long>,
            @RequestParam addSkins: Boolean
    ): ResponseEntity<Any> {
        val updatedCsgoCase = csgoCaseService.updateCsgoCase(caseId, skinIds, addSkins)
        return ResponseEntity(updatedCsgoCase, HttpStatus.OK)
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a CSGO case")
    fun deleteCsgoCase(@PathVariable id: Long): ResponseEntity<Any> {
        csgoCaseService.deleteById(id)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }
}
